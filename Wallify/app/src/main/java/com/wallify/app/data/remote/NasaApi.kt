package com.wallify.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

// ---- APOD (Astronomy Picture of the Day) ----
data class NasaApodItem(
    val title: String,
    val url: String,
    val hdurl: String?,
    val media_type: String // "image" or "video"
)

// ---- NASA Image & Video Library search (much bigger catalog than APOD alone) ----
data class NasaImageSearchResponse(val collection: NasaCollection)
data class NasaCollection(val items: List<NasaCollectionItem>)
data class NasaCollectionItem(val data: List<NasaItemData>, val links: List<NasaLink>?)
data class NasaItemData(val title: String, val nasa_id: String, val media_type: String)
data class NasaLink(val href: String, val rel: String)

interface NasaApi {
    @GET("planetary/apod")
    suspend fun apod(
        @Query("api_key") apiKey: String,
        @Query("count") count: Int = 20
    ): List<NasaApodItem>

    companion object {
        const val BASE_URL = "https://api.nasa.gov/"
    }
}

interface NasaImageLibraryApi {
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("media_type") mediaType: String = "image"
    ): NasaImageSearchResponse

    companion object {
        // No API key needed for this one at all
        const val BASE_URL = "https://images-api.nasa.gov/"
    }
}
