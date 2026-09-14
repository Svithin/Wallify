package com.wallify.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

// ---- Photos ----
data class PixabayHit(
    val id: Long,
    val webformatURL: String,
    val largeImageURL: String,
    val imageWidth: Int,
    val imageHeight: Int,
    val user: String
)
data class PixabayPhotoResponse(val hits: List<PixabayHit>)

// ---- Videos ----
data class PixabayVideoFile(val url: String, val width: Int, val height: Int)
data class PixabayVideoFiles(val large: PixabayVideoFile, val medium: PixabayVideoFile)
data class PixabayVideoHit(val id: Long, val videos: PixabayVideoFiles, val user: String)
data class PixabayVideoResponse(val hits: List<PixabayVideoHit>)

interface PixabayApi {
    /**
     * safesearch is hardcoded to true at every call site in the repository —
     * not exposed as a user setting.
     */
    @GET("api/")
    suspend fun searchPhotos(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("image_type") imageType: String = "photo",
        @Query("orientation") orientation: String = "vertical",
        @Query("safesearch") safeSearch: Boolean = true,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): PixabayPhotoResponse

    @GET("api/videos/")
    suspend fun searchVideos(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("safesearch") safeSearch: Boolean = true,
        @Query("per_page") perPage: Int = 15,
        @Query("page") page: Int = 1
    ): PixabayVideoResponse

    companion object {
        const val BASE_URL = "https://pixabay.com/"
    }
}
