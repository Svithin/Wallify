package com.wallify.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wallify.app.data.model.Category
import com.wallify.app.data.model.Wallpaper
import com.wallify.app.ui.components.WallpaperCard
import com.wallify.app.viewmodel.UiState
import com.wallify.app.viewmodel.WallpaperViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    category: Category,
    viewModel: WallpaperViewModel,
    onBack: () -> Unit,
    onWallpaperClick: (Wallpaper) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(category) {
        viewModel.loadCategory(category)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category.displayName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is UiState.Error -> Text(
                    "Couldn't load wallpapers: ${s.message}",
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
                is UiState.Success -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(s.wallpapers) { wallpaper ->
                        WallpaperCard(wallpaper = wallpaper, onClick = { onWallpaperClick(wallpaper) })
                    }
                }
            }
        }
    }
}
