package com.example.recipe_generator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.recipe_generator.ui.screens.MenuGeneratorScreen
import com.example.recipe_generator.ui.screens.RecipeListScreen
import com.example.recipe_generator.ui.theme.RecipeGeneratorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeGeneratorTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    var currentScreen by remember { mutableStateOf(0) }

    when (currentScreen) {
        0 -> RecipeListScreen(onNavigate = { currentScreen = it })
        2 -> MenuGeneratorScreen(onNavigate = { currentScreen = it })
        else -> RecipeListScreen(onNavigate = { currentScreen = it })
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeListScreenPreview() {
    RecipeGeneratorTheme {
        RecipeListScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun MenuGeneratorScreenPreview() {
    RecipeGeneratorTheme {
        MenuGeneratorScreen()
    }
}
