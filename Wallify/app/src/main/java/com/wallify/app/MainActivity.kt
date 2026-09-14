package com.wallify.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wallify.app.data.model.Category
import com.wallify.app.ui.screens.CategoryScreen
import com.wallify.app.ui.screens.HomeScreen
import com.wallify.app.ui.screens.WallpaperDetailScreen
import com.wallify.app.ui.theme.WallifyTheme
import com.wallify.app.viewmodel.WallpaperViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: WallpaperViewModel = viewModel()
            val darkMode by viewModel.darkMode

            WallifyTheme(darkTheme = darkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WallifyNavGraph(viewModel)
                }
            }
        }
    }
}

/**
 * Holds the currently selected wallpaper for the detail screen since passing a full
 * object through nav args is awkward — kept simple for this project.
 */
private var selectedWallpaperHolder: com.wallify.app.data.model.Wallpaper? = null

@androidx.compose.runtime.Composable
fun WallifyNavGraph(viewModel: WallpaperViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(viewModel = viewModel, onCategoryClick = { category ->
                navController.navigate("category/${category.name}")
            })
        }
        composable(
            route = "category/{categoryName}",
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: Category.NATURE.name
            val category = Category.valueOf(categoryName)
            CategoryScreen(
                category = category,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onWallpaperClick = { wallpaper ->
                    selectedWallpaperHolder = wallpaper
                    navController.navigate("detail")
                }
            )
        }
        composable("detail") {
            selectedWallpaperHolder?.let { wallpaper ->
                WallpaperDetailScreen(wallpaper = wallpaper, onBack = { navController.popBackStack() })
            }
        }
    }
}
