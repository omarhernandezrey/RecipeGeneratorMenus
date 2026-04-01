package com.example.recipe_generator

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

/**
 * Single host activity for the app shell.
 *
 * Phase 0:
 * - Keep Jetpack Compose as the main UI framework
 * - Use Navigation Component with fragment destinations
 * - Render each screen through a Fragment that hosts a ComposeView
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    }
}
