package com.wallify.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wallify.app.data.model.Category
import com.wallify.app.viewmodel.WallpaperViewModel

@Composable
fun HomeScreen(viewModel: WallpaperViewModel, onCategoryClick: (Category) -> Unit) {
    val darkMode = viewModel.darkMode.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wallify") },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 12.dp)) {
                        Text("Dark", modifier = Modifier.padding(end = 4.dp))
                        Switch(checked = darkMode, onCheckedChange = { viewModel.toggleDarkMode() })
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier.padding(padding)
        ) {
            items(Category.entries.toList()) { category ->
                CategoryTile(category = category, onClick = { onCategoryClick(category) })
            }
        }
    }
}

@Composable
private fun CategoryTile(category: Category, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(category.displayName, style = MaterialTheme.typography.titleMedium)
        }
    }
}
