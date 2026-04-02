package com.example.recipe_generator.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.recipe_generator.data.local.dao.FavoriteDao
import com.example.recipe_generator.data.local.dao.RecipeDao
import com.example.recipe_generator.data.local.entity.FavoriteEntity
import com.example.recipe_generator.data.local.entity.IngredientEntity
import com.example.recipe_generator.data.local.entity.RecipeEntity
import com.example.recipe_generator.data.local.entity.StepEntity

/**
 * Base de datos Room de la aplicación — AppDatabase.
 *
 * Versión 2: agrega tablas recipes, ingredients y steps (F2-16/17/18).
 * fallbackToDestructiveMigration() para entorno de desarrollo.
 * Singleton — una sola instancia por proceso.
 *
 * Capa: Data
 */
@Database(
    entities = [
        FavoriteEntity::class,
        RecipeEntity::class,
        IngredientEntity::class,
        StepEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipe_generator_db"
                )
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
            }
        }
    }
}
