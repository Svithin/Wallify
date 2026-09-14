package com.wallify.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class GiphyImageVariant(val url: String, val width: String, val height: String)
data class GiphyImages(
    val original: GiphyImageVariant,
    val fixed_width: GiphyImageVariant
)
data class GiphyGif(val id: String, val images: GiphyImages)
data class GiphySearchResponse(val data: List<GiphyGif>)

interface GiphyApi {
    /**
     * rating is hardcoded to "g" (general audiences) at every call site in the
     * repository — not exposed as a user-facing setting.
     */
    @GET("v1/gifs/search")
    suspend fun search(
        @Query("api_key") apiKey: String,
        @Query("q") query: String,
        @Query("rating") rating: String = "g",
        @Query("limit") limit: Int = 25
    ): GiphySearchResponse

    companion object {
        const val BASE_URL = "https://api.giphy.com/"
    }
}
