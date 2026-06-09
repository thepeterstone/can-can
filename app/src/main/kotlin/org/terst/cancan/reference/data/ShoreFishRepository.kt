package org.terst.cancan.reference.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import org.terst.cancan.core.CanCanLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoreFishRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }

    val species: List<ShoreFish> by lazy {
        try {
            val text = context.assets.open("reference/shore_fish_lookup.json")
                .bufferedReader().use { it.readText() }
            val data = json.decodeFromString<ShoreFishData>(text)
            CanCanLogger.log("ShoreFishRepository: ${data.species.size} species loaded (v${data.version})")
            data.species
        } catch (e: Exception) {
            CanCanLogger.log("ShoreFishRepository: load failed — ${e.message}")
            emptyList()
        }
    }
}
