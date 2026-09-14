package com.wallify.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.wallify.app.data.model.MediaType
import com.wallify.app.data.model.Wallpaper

@Composable
fun WallpaperCard(wallpaper: Wallpaper, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(0.65f)
            .clickable(onClick = onClick)
    ) {
        Box {
            AsyncImage(
                model = wallpaper.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            if (wallpaper.mediaType != MediaType.STATIC) {
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = badgeLabel(wallpaper.mediaType),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

private fun badgeLabel(type: MediaType) = when (type) {
    MediaType.GIF -> "GIF"
    MediaType.VIDEO_LIVE -> "LIVE"
    MediaType.PARALLAX -> "PARALLAX"
    MediaType.STATIC -> ""
}
