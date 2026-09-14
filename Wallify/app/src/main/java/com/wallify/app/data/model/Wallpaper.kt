package com.wallify.app.data.model

enum class MediaType { STATIC, GIF, VIDEO_LIVE, PARALLAX }

enum class Category(val displayName: String, val query: String) {
    NATURE("Nature", "nature landscape"),
    ABSTRACT("Abstract", "abstract art"),
    ANIME("Anime", "anime"),
    WOMEN_PORTRAIT("Women / Portraits", "woman portrait fashion"),
    AI_ART("AI Art", "ai generated art"),
    SPACE("Space", "space galaxy"),
    MINIMAL("Minimal", "minimal wallpaper")
}

/**
 * Unified model that every remote source (Unsplash, Pexels, Wallhaven) gets mapped into,
 * so the UI never needs to know which API a wallpaper came from.
 */
data class Wallpaper(
    val id: String,
    val source: String,          // "unsplash" | "pexels" | "wallhaven"
    val thumbnailUrl: String,
    val fullResUrl: String,
    val mediaType: MediaType,
    val width: Int,
    val height: Int,
    val category: Category,
    val attribution: String? = null   // required by Unsplash/Pexels ToS
)
