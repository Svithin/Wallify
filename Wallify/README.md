# Wallify — Wallpaper App (Android, Kotlin + Compose)

A wallpaper app that pulls high-resolution static, GIF, and live/video wallpapers
from **seven** free sources, organized into categories, with one-tap "Apply
Wallpaper" support (including registering a real Android live wallpaper for
video content).

## Sources used
| Source | What it provides | Key needed? |
|---|---|---|
| Unsplash | High-res static photos | Free key |
| Pexels | Static photos + video (used for live/parallax) | Free key |
| Pixabay | Static photos + video, huge permissive-license library | Free key |
| Wallhaven | Anime wallpapers + GIFs (hardcoded SFW-only) | Optional (works without) |
| Waifu.im | Dedicated anime image API (hardcoded SFW-only) | No key needed |
| Giphy | Animated GIFs (hardcoded to "g" rating) | Free key |
| NASA | Real space imagery for the Space category (APOD + Image Library) | Free key, or leave as `DEMO_KEY` |

## Categories included
Nature, Abstract, Anime, Women/Portraits, AI Art, Space, Minimal.
Edit `Category.kt` to add/remove/rename categories or change their search query.

## What actually works out of the box
- Category grid → wallpaper grid (merged results from every relevant source
  above, fetched in parallel) → full preview → apply
- Static images via Unsplash, Pexels, Pixabay
- Video/parallax wallpapers via Pexels + Pixabay video search, applied as a
  real Android live wallpaper (see `VideoLiveWallpaperService`)
- Anime wallpapers via Wallhaven + Waifu.im, GIFs via Wallhaven + Giphy — all
  **hardcoded to SFW-only results** at the API-call level (see the comment
  block at the top of `WallpaperRepository.kt`); this is fixed in code and not
  a toggle anywhere in the app
- Space category pulls real NASA imagery, not just generic "space" stock photos
- Dark mode switch

## Don't want to install Android Studio? Build in the cloud instead (free)

This project includes a GitHub Actions workflow that builds the APK for you on
GitHub's servers — nothing to install on your computer.

1. Create a free GitHub account: https://github.com/join
2. Click **+ → New repository** (top right) → name it `Wallify` → **Create repository**
3. On the new repo's page, click **"uploading an existing file"** (or
   **Add file → Upload files**) → drag in the entire unzipped `Wallify` folder
   → **Commit changes**
   - ⚠️ Before uploading, edit `app/build.gradle.kts` (the copy on your computer)
     to add your API keys, exactly like Step 2 below — GitHub will build
     whatever you upload, keys included
4. Click the **Actions** tab at the top of the repo → you'll see "Build APK"
   running automatically (it takes 3-5 minutes). If it doesn't start on its
   own, click **Build APK** on the left → **Run workflow** → **Run workflow**
5. When it finishes (green checkmark ✅), click into that run → scroll down to
   **Artifacts** → click **Wallify-debug-apk** to download a zip containing
   your `.apk`
6. Send that `.apk` to your phone (email, Google Drive, etc.), open it, allow
   "install from unknown sources" when prompted, and install

If the run fails (red ❌), click into it and open the "Build debug APK" step —
paste me the error and I'll fix it.

## Or build locally with Android Studio

### 1. Get free API keys (5 minutes each)
- **Unsplash**: https://unsplash.com/developers → create an app → copy "Access Key"
- **Pexels**: https://www.pexels.com/api/ → sign up → copy API key
- **Pixabay**: https://pixabay.com/api/docs/ → sign up (free) → copy API key
- **Giphy**: https://developers.giphy.com/ → create an app → copy API key
- **NASA**: https://api.nasa.gov/ → sign up (instant, free) → copy API key
  (or just leave it as `DEMO_KEY`, which works out of the box but is rate-limited
  to ~30 requests/hour instead of 1,000/hour)
- **Wallhaven** (optional, higher rate limits): https://wallhaven.cc/settings/account → API key
- **Waifu.im**: no signup needed at all, works immediately

### 2. Add your keys
Open `app/build.gradle.kts` and replace the placeholders:

```kotlin
buildConfigField("String", "UNSPLASH_ACCESS_KEY", "\"YOUR_UNSPLASH_ACCESS_KEY\"")
buildConfigField("String", "PEXELS_API_KEY", "\"YOUR_PEXELS_API_KEY\"")
buildConfigField("String", "WALLHAVEN_API_KEY", "\"YOUR_WALLHAVEN_API_KEY\"")
buildConfigField("String", "PIXABAY_API_KEY", "\"YOUR_PIXABAY_API_KEY\"")
buildConfigField("String", "NASA_API_KEY", "\"DEMO_KEY\"")
buildConfigField("String", "GIPHY_API_KEY", "\"YOUR_GIPHY_API_KEY\"")
```

(Wallhaven works without a key too — just leave that one blank, `""`.)

### 3. Build the APK
You need **Android Studio** (free): https://developer.android.com/studio

1. Open Android Studio → "Open" → select this `Wallify` folder
2. Let it sync Gradle (first sync downloads dependencies — needs internet)
3. Plug in your phone (USB debugging on) or start an emulator → click ▶ Run
   — OR —
4. To get a standalone `.apk` file: **Build → Build Bundle(s)/APK(s) → Build APK(s)**
   The APK lands in `app/build/outputs/apk/debug/app-debug.apk` — copy that to
   your phone and install it (enable "install from unknown sources").

## Notes / next steps you may want
- **Favorites/downloads persistence**: `Room` dependency is already wired in the
  Gradle file; the ViewModel currently keeps favorites in memory only — add a
  `Dao`/`Entity` if you want them to survive app restarts.
- **Pagination**: `WallpaperRepository.search()` takes a `page` param already;
  wire up infinite scroll in `CategoryScreen` when you're ready.
- **True parallax (motion-sensor) wallpapers**: currently "parallax" wallpapers
  are just tagged as a media type; if you want actual gyroscope-based image
  shifting on static images (rather than video), that's an additional custom
  `WallpaperService` — ask and I can add it.
- **Signed release build**: the debug APK above is fine for sideloading on your
  own phone. If you want to publish to the Play Store, you'll need to generate
  a signing key (Android Studio: Build → Generate Signed Bundle/APK) — happy to
  walk through that when you get there.

## Why no NSFW mode
This project intentionally does not include an NSFW toggle or NSFW content
sourcing. Every source that could return adult content is hardcoded to its
safe-search/SFW option at the API-call level — not exposed as a setting:
- Wallhaven: `purity=100` in `WallhavenApi.kt`
- Pixabay: `safesearch=true` in every call in `WallpaperRepository.kt`
- Waifu.im: `is_nsfw=false` in `WallpaperRepository.kt`
- Giphy: `rating="g"` in `WallpaperRepository.kt`
