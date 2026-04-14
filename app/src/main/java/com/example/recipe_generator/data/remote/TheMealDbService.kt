package com.example.recipe_generator.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class TheMealDbSearchResponse(
    @SerializedName("meals")
    val meals: List<TheMealDbMealDto>?
)

data class TheMealDbMealDto(
    @SerializedName("idMeal")
    val idMeal: String,
    @SerializedName("strMeal")
    val strMeal: String,
    @SerializedName("strMealThumb")
    val strMealThumb: String?,
    @SerializedName("strArea")
    val strArea: String?,
    @SerializedName("strCategory")
    val strCategory: String?,
    @SerializedName("strTags")
    val strTags: String?
)

interface TheMealDbService {
    @GET("search.php")
    suspend fun searchMeals(
        @Query("s") query: String
    ): TheMealDbSearchResponse
}

object TheMealDbServiceFactory {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    fun create(): TheMealDbService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TheMealDbService::class.java)
    }
}

