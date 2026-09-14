package com.wallify.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.wallify.app.data.model.Wallpaper
import com.wallify.app.util.WallpaperApplier
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperDetailScreen(wallpaper: Wallpaper, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isApplying by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preview") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = wallpaper.fullResUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.weight(1f).fillMaxWidth()
            )

            wallpaper.attribution?.let {
                Text(it, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(8.dp))
            }

            message?.let {
                Text(it, modifier = Modifier.padding(horizontal = 16.dp))
            }

            Button(
                enabled = !isApplying,
                onClick = {
                    isApplying = true
                    message = null
                    scope.launch {
                        try {
                            WallpaperApplier.apply(context, wallpaper)
                            message = "Wallpaper applied!"
                        } catch (e: Exception) {
                            message = "Failed to apply: ${e.message}"
                        } finally {
                            isApplying = false
                        }
                    }
                },
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                Text(if (isApplying) "Applying..." else "Apply Wallpaper")
            }
        }
    }
}
