package org.terst.cancan.reference.data

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

@Singleton
class WikipediaImageRepository @Inject constructor() {
    private val json = Json { ignoreUnknownKeys = true }
    private val cache = ConcurrentHashMap<String, String>()

    suspend fun getThumbnailUrl(wikipediaTitle: String): String? {
        if (wikipediaTitle.isBlank()) return null
        cache[wikipediaTitle]?.let { return it }
        return withContext(Dispatchers.IO) {
            try {
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
                    ?.also { url -> cache[wikipediaTitle] = url }
            } catch (e: Exception) {
                CanCanLogger.log("WikiImage: $wikipediaTitle — ${e.message}")
                null
            }
        }
    }

    @Serializable private data class WikiSummary(val thumbnail: WikiThumbnail? = null)
    @Serializable private data class WikiThumbnail(val source: String)
}
