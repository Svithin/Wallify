package com.wallify.app.data.repository

import com.wallify.app.BuildConfig
import com.wallify.app.data.model.Category
import com.wallify.app.data.model.MediaType
import com.wallify.app.data.model.Wallpaper
import com.wallify.app.data.remote.RetrofitClient
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Fans a category search out to whichever sources are the best fit for that category.
 * Every source-specific SFW/safe-search flag is hardcoded at the call site below —
 * none of them are exposed as user-facing settings anywhere in this app:
 *   - Wallhaven: purity=100 (see WallhavenApi)
 *   - Pixabay: safesearch=true
 *   - Waifu.im: is_nsfw=false
 *   - Giphy: rating="g"
 */
class WallpaperRepository {

    suspend fun search(category: Category, page: Int = 1): List<Wallpaper> = coroutineScope {
        val tasks = mutableListOf<Deferred<List<Wallpaper>>>()

        // Universal sources: solid for every non-anime category
        tasks += async { safeCall { fetchUnsplash(category, page) } }
        tasks += async { safeCall { fetchPexelsPhotos(category, page) } }
        tasks += async { safeCall { fetchPexelsVideos(category, page) } }
        tasks += async { safeCall { fetchPixabayPhotos(category, page) } }
        tasks += async { safeCall { fetchPixabayVideos(category, page) } }
        tasks += async { safeCall { fetchGiphy(category, page) } }

        // Anime gets two dedicated anime-specific sources on top
        if (category == Category.ANIME) {
            tasks += async { safeCall { fetchWallhaven(category, page) } }
            tasks += async { safeCall { fetchWaifu(category) } }
        }

        // Space gets NASA's actual imagery on top of the generic stock sources
        if (category == Category.SPACE) {
            tasks += async { safeCall { fetchNasaApod() } }
            tasks += async { safeCall { fetchNasaImageLibrary() } }
        }

        tasks.flatMap { it.await() }
    }

    private suspend fun safeCall(block: suspend () -> List<Wallpaper>): List<Wallpaper> =
        runCatching { block() }.getOrDefault(emptyList())

    // ---------- Unsplash ----------
    private suspend fun fetchUnsplash(category: Category, page: Int): List<Wallpaper> {
        val res = RetrofitClient.unsplash.search(
            auth = "Client-ID ${BuildConfig.UNSPLASH_ACCESS_KEY}",
            query = category.query,
            page = page
        )
        return res.results.map {
            Wallpaper(
                id = "unsplash_${it.id}",
                source = "unsplash",
                thumbnailUrl = it.urls.thumb,
                fullResUrl = it.urls.full,
                mediaType = MediaType.STATIC,
                width = it.width,
                height = it.height,
                category = category,
                attribution = "Photo by ${it.user.name} on Unsplash"
            )
        }
    }

    // ---------- Pexels ----------
    private suspend fun fetchPexelsPhotos(category: Category, page: Int): List<Wallpaper> {
        val res = RetrofitClient.pexels.searchPhotos(
            apiKey = BuildConfig.PEXELS_API_KEY,
            query = category.query,
            page = page
        )
        return res.photos.map {
            Wallpaper(
                id = "pexels_photo_${it.id}",
                source = "pexels",
                thumbnailUrl = it.src.medium,
                fullResUrl = it.src.original,
                mediaType = MediaType.STATIC,
                width = it.width,
                height = it.height,
                category = category,
                attribution = "Photo by ${it.photographer} on Pexels"
            )
        }
    }

    private suspend fun fetchPexelsVideos(category: Category, page: Int): List<Wallpaper> {
        val res = RetrofitClient.pexels.searchVideos(
            apiKey = BuildConfig.PEXELS_API_KEY,
            query = category.query,
            page = page
        )
        return res.videos.mapNotNull { video ->
            val bestFile = video.video_files.maxByOrNull { it.width * it.height } ?: return@mapNotNull null
            Wallpaper(
                id = "pexels_video_${video.id}",
                source = "pexels",
                thumbnailUrl = video.image,
                fullResUrl = bestFile.link,
                mediaType = MediaType.VIDEO_LIVE,
                width = bestFile.width,
                height = bestFile.height,
                category = category,
                attribution = "Video via Pexels"
            )
        }
    }

    // ---------- Pixabay ----------
    private suspend fun fetchPixabayPhotos(category: Category, page: Int): List<Wallpaper> {
        val res = RetrofitClient.pixabay.searchPhotos(
            apiKey = BuildConfig.PIXABAY_API_KEY,
            query = category.query,
            safeSearch = true,
            page = page
        )
        return res.hits.map {
            Wallpaper(
                id = "pixabay_photo_${it.id}",
                source = "pixabay",
                thumbnailUrl = it.webformatURL,
                fullResUrl = it.largeImageURL,
                mediaType = MediaType.STATIC,
                width = it.imageWidth,
                height = it.imageHeight,
                category = category,
                attribution = "Image by ${it.user} on Pixabay"
            )
        }
    }

    private suspend fun fetchPixabayVideos(category: Category, page: Int): List<Wallpaper> {
        val res = RetrofitClient.pixabay.searchVideos(
            apiKey = BuildConfig.PIXABAY_API_KEY,
            query = category.query,
            safeSearch = true,
            page = page
        )
        return res.hits.map {
            val file = it.videos.large
            Wallpaper(
                id = "pixabay_video_${it.id}",
                source = "pixabay",
                thumbnailUrl = it.videos.medium.url,
                fullResUrl = file.url,
                mediaType = MediaType.VIDEO_LIVE,
                width = file.width,
                height = file.height,
                category = category,
                attribution = "Video by ${it.user} on Pixabay"
            )
        }
    }

    // ---------- Wallhaven (anime only) ----------
    private suspend fun fetchWallhaven(category: Category, page: Int): List<Wallpaper> {
        val res = RetrofitClient.wallhaven.search(
            query = category.query,
            apiKey = BuildConfig.WALLHAVEN_API_KEY.ifBlank { null },
            page = page
            // purity defaults to "100" (SFW) inside WallhavenApi and is never overridden here
        )
        return res.data.map {
            val isGif = it.file_type.contains("gif")
            Wallpaper(
                id = "wallhaven_${it.id}",
                source = "wallhaven",
                thumbnailUrl = it.thumbs.large,
                fullResUrl = it.path,
                mediaType = if (isGif) MediaType.GIF else MediaType.STATIC,
                width = it.dimension_x,
                height = it.dimension_y,
                category = category,
                attribution = "via Wallhaven"
            )
        }
    }

    // ---------- Waifu.im (anime only, no API key needed) ----------
    private suspend fun fetchWaifu(category: Category): List<Wallpaper> {
        val res = RetrofitClient.waifu.search(isNsfw = false)
        return res.images.map {
            Wallpaper(
                id = "waifu_${it.image_id}",
                source = "waifu.im",
                thumbnailUrl = it.url,
                fullResUrl = it.url,
                mediaType = if (it.extension.contains("gif")) MediaType.GIF else MediaType.STATIC,
                width = it.width,
                height = it.height,
                category = category,
                attribution = "via waifu.im"
            )
        }
    }

    // ---------- Giphy (all categories, general-audience only) ----------
    private suspend fun fetchGiphy(category: Category, page: Int): List<Wallpaper> {
        val res = RetrofitClient.giphy.search(
            apiKey = BuildConfig.GIPHY_API_KEY,
            query = category.query,
            rating = "g"
        )
        return res.data.map {
            Wallpaper(
                id = "giphy_${it.id}",
                source = "giphy",
                thumbnailUrl = it.images.fixed_width.url,
                fullResUrl = it.images.original.url,
                mediaType = MediaType.GIF,
                width = it.images.original.width.toIntOrNull() ?: 0,
                height = it.images.original.height.toIntOrNull() ?: 0,
                category = category,
                attribution = "via GIPHY"
            )
        }
    }

    // ---------- NASA (space only) ----------
    private suspend fun fetchNasaApod(): List<Wallpaper> {
        val items = RetrofitClient.nasaApod.apod(apiKey = BuildConfig.NASA_API_KEY)
        return items.filter { it.media_type == "image" }.map {
            val url = it.hdurl ?: it.url
            Wallpaper(
                id = "nasa_apod_${it.title.hashCode()}",
                source = "nasa",
                thumbnailUrl = it.url,
                fullResUrl = url,
                mediaType = MediaType.STATIC,
                width = 0,
                height = 0,
                category = Category.SPACE,
                attribution = "NASA APOD: ${it.title}"
            )
        }
    }

    private suspend fun fetchNasaImageLibrary(): List<Wallpaper> {
        val res = RetrofitClient.nasaImageLibrary.search(query = "galaxy nebula space")
        return res.collection.items.mapNotNull { item ->
            val imageLink = item.links?.firstOrNull { it.rel == "preview" }?.href ?: return@mapNotNull null
            val title = item.data.firstOrNull()?.title ?: "NASA image"
            Wallpaper(
                id = "nasa_lib_${item.data.firstOrNull()?.nasa_id ?: imageLink.hashCode()}",
                source = "nasa",
                thumbnailUrl = imageLink,
                fullResUrl = imageLink,
                mediaType = MediaType.STATIC,
                width = 0,
                height = 0,
                category = Category.SPACE,
                attribution = "NASA: $title"
            )
        }
    }
}
