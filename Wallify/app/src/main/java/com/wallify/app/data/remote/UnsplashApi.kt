package com.wallify.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class UnsplashPhoto(
    val id: String,
    val width: Int,
    val height: Int,
    val urls: UnsplashUrls,
    val user: UnsplashUser
)

data class UnsplashUrls(val full: String, val regular: String, val thumb: String)
data class UnsplashUser(val name: String)
data class UnsplashSearchResponse(val results: List<UnsplashPhoto>)

interface UnsplashApi {
    @GET("search/photos")
    suspend fun search(
        @Header("Authorization") auth: String,
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1,
        @Query("orientation") orientation: String = "portrait"
    ): UnsplashSearchResponse

    companion object {
        const val BASE_URL = "https://api.unsplash.com/"
    }
}
