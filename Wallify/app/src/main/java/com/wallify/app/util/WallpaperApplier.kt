package com.wallify.app.util

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import com.wallify.app.data.model.MediaType
import com.wallify.app.data.model.Wallpaper
import com.wallify.app.service.VideoLiveWallpaperService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

object WallpaperApplier {

    /**
     * Downloads the full-res image and sets it as the device wallpaper.
     * For VIDEO_LIVE wallpapers, this instead saves the video locally and opens
     * the system's "Set live wallpaper" screen pointed at our WallpaperService.
     */
    suspend fun apply(context: Context, wallpaper: Wallpaper, targetBothScreens: Boolean = true) {
        when (wallpaper.mediaType) {
            MediaType.STATIC, MediaType.GIF, MediaType.PARALLAX -> applyStaticImage(context, wallpaper, targetBothScreens)
            MediaType.VIDEO_LIVE -> applyLiveWallpaper(context, wallpaper)
        }
    }

    private suspend fun applyStaticImage(context: Context, wallpaper: Wallpaper, targetBothScreens: Boolean) {
        withContext(Dispatchers.IO) {
            val bytes = URL(wallpaper.fullResUrl).openStream().use { it.readBytes() }
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            val manager = WallpaperManager.getInstance(context)
            val flags = if (targetBothScreens) {
                WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
            } else {
                WallpaperManager.FLAG_SYSTEM
            }
            manager.setBitmap(bitmap, null, true, flags)
        }
    }

    private suspend fun applyLiveWallpaper(context: Context, wallpaper: Wallpaper) {
        withContext(Dispatchers.IO) {
            val bytes = URL(wallpaper.fullResUrl).openStream().use { it.readBytes() }
            val file = File(context.filesDir, "current_live_wallpaper.mp4")
            file.writeBytes(bytes)
        }
        withContext(Dispatchers.Main) {
            // Persist which file the service should play, then hand off to the system picker
            context.getSharedPreferences("wallify_prefs", Context.MODE_PRIVATE)
                .edit()
                .putString("live_wallpaper_path", File(context.filesDir, "current_live_wallpaper.mp4").absolutePath)
                .apply()

            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
            intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                android.content.ComponentName(context, VideoLiveWallpaperService::class.java)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
