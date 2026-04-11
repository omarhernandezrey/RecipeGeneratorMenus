package com.example.recipe_generator.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.recipe_generator.data.local.dao.AppNotificationDao
import com.example.recipe_generator.data.local.dao.FavoriteDao
import com.example.recipe_generator.data.local.dao.RecipeDao
import com.example.recipe_generator.data.local.dao.UserProfileDao
import com.example.recipe_generator.data.local.dao.UserRecipeDao
import com.example.recipe_generator.data.local.dao.WeeklyPlanDao
import com.example.recipe_generator.data.local.entity.AppNotificationEntity
import com.example.recipe_generator.data.local.entity.FavoriteEntity
import com.example.recipe_generator.data.local.entity.IngredientEntity
import com.example.recipe_generator.data.local.entity.RecipeEntity
import com.example.recipe_generator.data.local.entity.StepEntity
import com.example.recipe_generator.data.local.entity.UserProfileEntity
import com.example.recipe_generator.data.local.entity.UserRecipeEntity
import com.example.recipe_generator.data.local.entity.WeeklyPlanEntity

/**
 * Base de datos Room de la aplicación — AppDatabase.
 *
 * Historial de versiones:
 *  v1 — FavoriteEntity
 *  v2 — RecipeEntity, IngredientEntity, StepEntity
 *  v3 — ajustes internos (desarrollo)
 *  v4 — UserRecipeEntity (C-01), WeeklyPlanEntity (C-02)
 *  v5 — UserProfileEntity (C-03) — C-07: 3 entities + 3 DAOs completos
 *  v6 — FavoriteEntity: clave compuesta (userId, recipeId) — E-03
 *
 * fallbackToDestructiveMigration(dropAllTables = true) para entorno de desarrollo.
 * Singleton — una sola instancia por proceso.
 *
 * Entidades del usuario: UserRecipeEntity · WeeklyPlanEntity · UserProfileEntity
 * DAOs del usuario    : UserRecipeDao    · WeeklyPlanDao    · UserProfileDao
 *
 * Capa: Data
 */
@Database(
    entities = [
        FavoriteEntity::class,
        RecipeEntity::class,
        IngredientEntity::class,
        StepEntity::class,
        UserRecipeEntity::class,
        WeeklyPlanEntity::class,
        UserProfileEntity::class,
        AppNotificationEntity::class
    ],
    version = 9,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao
    abstract fun recipeDao(): RecipeDao
    abstract fun userRecipeDao(): UserRecipeDao
    abstract fun weeklyPlanDao(): WeeklyPlanDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun appNotificationDao(): AppNotificationDao

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
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build().also { INSTANCE = it }
            }
        }
    }
}
