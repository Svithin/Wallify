package com.wallify.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// ---- Photos ----
data class PexelsPhotoSrc(val original: String, val large2x: String, val medium: String)
data class PexelsPhoto(val id: Int, val width: Int, val height: Int, val src: PexelsPhotoSrc, val photographer: String)
data class PexelsPhotoSearchResponse(val photos: List<PexelsPhoto>)

// ---- Videos (used for live / parallax wallpapers) ----
data class PexelsVideoFile(val link: String, val width: Int, val height: Int, val quality: String)
data class PexelsVideoPicture(val picture: String)
data class PexelsVideo(
    val id: Int,
    val width: Int,
    val height: Int,
    val image: String,
    val video_files: List<PexelsVideoFile>
)
data class PexelsVideoSearchResponse(val videos: List<PexelsVideo>)

interface PexelsApi {
    @GET("v1/search")
    suspend fun searchPhotos(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1,
        @Query("orientation") orientation: String = "portrait"
    ): PexelsPhotoSearchResponse

    @GET("videos/search")
    suspend fun searchVideos(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 15,
        @Query("page") page: Int = 1,
        @Query("orientation") orientation: String = "portrait"
    ): PexelsVideoSearchResponse

    companion object {
        const val BASE_URL = "https://api.pexels.com/"
    }
}
