package org.terst.cancan.reference.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import org.terst.cancan.core.CanCanLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReferenceRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }

    val items: List<ReferenceItem> by lazy {
        try {
            val text = context.assets.open("reference/canning_guide.json")
                .bufferedReader().use { it.readText() }
            val data = json.decodeFromString<ReferenceData>(text)
            CanCanLogger.log("ReferenceRepository: loaded ${data.items.size} items (schema v${data.version})")
            data.items
        } catch (e: Exception) {
            CanCanLogger.log("ReferenceRepository: failed to load guide — ${e.message}")
            emptyList()
        }
    }
}
