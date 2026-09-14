package com.wallify.app.service

import android.content.Context
import android.net.Uri
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import java.io.File

@UnstableApi
class VideoLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = VideoEngine()

    inner class VideoEngine : Engine() {
        private var player: ExoPlayer? = null

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            val path = getSharedPreferences("wallify_prefs", Context.MODE_PRIVATE)
                .getString("live_wallpaper_path", null) ?: return

            player = ExoPlayer.Builder(this@VideoLiveWallpaperService).build().apply {
                setVideoSurface(holder.surface)
                setMediaItem(MediaItem.fromUri(Uri.fromFile(File(path))))
                repeatMode = ExoPlayer.REPEAT_MODE_ALL
                volume = 0f
                prepare()
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) player?.play() else player?.pause()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            player?.release()
            player = null
        }
    }
}
