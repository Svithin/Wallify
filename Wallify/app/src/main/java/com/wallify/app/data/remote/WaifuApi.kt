package com.wallify.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class WaifuImage(
    val image_id: Long,
    val url: String,
    val width: Int,
    val height: Int,
    val extension: String,
    val is_nsfw: Boolean
)
data class WaifuSearchResponse(val images: List<WaifuImage>)

interface WaifuApi {
    /**
     * is_nsfw is hardcoded to false at every call site in the repository — not exposed
     * as a user-facing setting. No API key required for this service.
     */
    @GET("search")
    suspend fun search(
        @Query("included_tags") tags: List<String> = listOf("waifu"),
        @Query("is_nsfw") isNsfw: Boolean = false,
        @Query("limit") limit: Int = 30
    ): WaifuSearchResponse

    companion object {
        const val BASE_URL = "https://api.waifu.im/"
    }
}
