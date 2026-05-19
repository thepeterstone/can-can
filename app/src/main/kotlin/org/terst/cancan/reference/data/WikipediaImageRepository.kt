package org.terst.cancan.reference.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.terst.cancan.core.CanCanLogger
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

private val IMAGE_EXTENSIONS = listOf("webp", "jpg", "jpeg", "png")

@Singleton
class WikipediaImageRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val cache = ConcurrentHashMap<String, String>()

    suspend fun getImageUrl(itemId: String, wikipediaTitle: String): String? {
        cache[itemId]?.let { return it }
        return withContext(Dispatchers.IO) {
            bundledAssetUrl(itemId)?.also { cache[itemId] = it }
                ?: fetchWikipediaUrl(wikipediaTitle)?.also { cache[itemId] = it }
        }
    }

    private fun bundledAssetUrl(itemId: String): String? {
        for (ext in IMAGE_EXTENSIONS) {
            val path = "reference/images/$itemId.$ext"
            try {
                context.assets.open(path).close()
                return "file:///android_asset/$path"
            } catch (_: Exception) {}
        }
        return null
    }

    private fun fetchWikipediaUrl(wikipediaTitle: String): String? {
        if (wikipediaTitle.isBlank()) return null
        return try {
            val encoded = wikipediaTitle.replace(' ', '_')
            val connection = URL(
                "https://en.wikipedia.org/api/rest_v1/page/summary/$encoded"
            ).openConnection() as HttpURLConnection
            connection.apply {
                setRequestProperty("User-Agent", "can-can/1.0 Android")
                connectTimeout = 6_000
                readTimeout = 10_000
            }
            val text = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()
            json.decodeFromString<WikiSummary>(text).thumbnail?.source
        } catch (e: Exception) {
            CanCanLogger.log("WikiImage: $wikipediaTitle — ${e.message}")
            null
        }
    }

    @Serializable private data class WikiSummary(val thumbnail: WikiThumbnail? = null)
    @Serializable private data class WikiThumbnail(val source: String)
}
