package com.wallify.app.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallify.app.data.model.Category
import com.wallify.app.data.model.Wallpaper
import com.wallify.app.data.repository.WallpaperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val wallpapers: List<Wallpaper>) : UiState()
    data class Error(val message: String) : UiState()
}

class WallpaperViewModel(
    private val repository: WallpaperRepository = WallpaperRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val darkMode = mutableStateOf(false)

    private val favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = favoriteIds.asStateFlow()

    fun loadCategory(category: Category) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val results = repository.search(category)
                _uiState.value = UiState.Success(results)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun toggleFavorite(wallpaperId: String) {
        val current = favoriteIds.value
        favoriteIds.value = if (wallpaperId in current) current - wallpaperId else current + wallpaperId
    }

    fun toggleDarkMode() {
        darkMode.value = !darkMode.value
    }
}
