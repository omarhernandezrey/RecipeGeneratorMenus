package com.example.recipe_generator.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.recipe_generator.data.local.dao.FavoriteDao
import com.example.recipe_generator.data.local.entity.FavoriteEntity

/**
 * Base de datos Room de la aplicación — AppDatabase.
 *
 * Contiene la tabla de favoritos. F3-29: FavoritesRepository con Room.
 * Singleton — una sola instancia por proceso mediante companion object.
 *
 * Capa: Data
 */
@Database(
    entities = [FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipe_generator_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
