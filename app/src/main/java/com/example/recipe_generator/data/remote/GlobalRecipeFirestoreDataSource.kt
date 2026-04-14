package com.example.recipe_generator.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

data class GlobalRecipeFirestoreRecipe(
    val id: String,
    val nombre: String,
    val imagen: String,
    val aliases: List<String>,
    val pais: String,
    val keywords: List<String>,
    val updatedAt: Long = System.currentTimeMillis()
)

class GlobalRecipeFirestoreDataSource(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val COLLECTION = "recetas"
    }

    suspend fun findByAlias(normalizedName: String): GlobalRecipeFirestoreRecipe? {
        val snapshot = firestore.collection(COLLECTION)
            .whereArrayContains("aliases", normalizedName)
            .limit(1)
            .get()
            .await()

        val doc = snapshot.documents.firstOrNull() ?: return null
        return GlobalRecipeFirestoreRecipe(
            id = doc.id,
            nombre = doc.getString("nombre").orEmpty(),
            imagen = doc.getString("imagen").orEmpty(),
            aliases = doc.getStringList("aliases"),
            pais = doc.getString("pais").orEmpty(),
            keywords = doc.getStringList("keywords"),
            updatedAt = doc.getLong("updatedAt") ?: 0L
        )
    }

    suspend fun save(recipe: GlobalRecipeFirestoreRecipe) {
        val payload = mapOf(
            "nombre" to recipe.nombre,
            "imagen" to recipe.imagen,
            "aliases" to recipe.aliases,
            "pais" to recipe.pais,
            "keywords" to recipe.keywords,
            "updatedAt" to recipe.updatedAt
        )
        firestore.collection(COLLECTION)
            .document(recipe.id)
            .set(payload, SetOptions.merge())
            .await()
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.getStringList(field: String): List<String> {
        return (get(field) as? List<*>)
            .orEmpty()
            .filterIsInstance<String>()
            .map(String::trim)
            .filter { it.isNotBlank() }
            .distinct()
    }
}

