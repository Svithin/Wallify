package com.wallify.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class WallhavenWallpaper(
    val id: String,
    val url: String,
    val path: String,          // full resolution image url
    val thumbs: WallhavenThumbs,
    val dimension_x: Int,
    val dimension_y: Int,
    val file_type: String      // "image/png", "image/jpeg", "image/gif" (Wallhaven has an animated category)
)
data class WallhavenThumbs(val large: String)
data class WallhavenSearchResponse(val data: List<WallhavenWallpaper>)

interface WallhavenApi {
    /**
     * purity is intentionally hardcoded to "100" (SFW only) everywhere this is called from
     * the repository layer. This is NOT exposed as a user-facing toggle.
     * Purity string format is sfw/sketchy/nsfw as "1/0/0".
     */
    @GET("v1/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("purity") purity: String = "100",
        @Query("categories") categories: String = "010", // anime category bit
        @Query("apikey") apiKey: String? = null,
        @Query("page") page: Int = 1
    ): WallhavenSearchResponse

    companion object {
        const val BASE_URL = "https://wallhaven.cc/api/"
    }
}
