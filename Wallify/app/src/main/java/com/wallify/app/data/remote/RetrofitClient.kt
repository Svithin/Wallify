package com.wallify.app.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private fun build(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val unsplash: UnsplashApi by lazy { build(UnsplashApi.BASE_URL).create(UnsplashApi::class.java) }
    val pexels: PexelsApi by lazy { build(PexelsApi.BASE_URL).create(PexelsApi::class.java) }
    val wallhaven: WallhavenApi by lazy { build(WallhavenApi.BASE_URL).create(WallhavenApi::class.java) }
    val pixabay: PixabayApi by lazy { build(PixabayApi.BASE_URL).create(PixabayApi::class.java) }
    val nasaApod: NasaApi by lazy { build(NasaApi.BASE_URL).create(NasaApi::class.java) }
    val nasaImageLibrary: NasaImageLibraryApi by lazy { build(NasaImageLibraryApi.BASE_URL).create(NasaImageLibraryApi::class.java) }
    val waifu: WaifuApi by lazy { build(WaifuApi.BASE_URL).create(WaifuApi::class.java) }
    val giphy: GiphyApi by lazy { build(GiphyApi.BASE_URL).create(GiphyApi::class.java) }
}
