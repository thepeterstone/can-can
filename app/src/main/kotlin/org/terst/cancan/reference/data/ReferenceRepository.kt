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

    private val assetFiles = listOf(
        "reference/canning_guide.json",
        "reference/fermentation_guide.json",
        "reference/foraging_guide.json",
        "reference/preservation_guide.json"
    )

    val items: List<ReferenceItem> by lazy {
        assetFiles.flatMap { path -> loadFile(path) }.also {
            CanCanLogger.log("ReferenceRepository: ${it.size} total reference items loaded")
        }
    }

    private fun loadFile(path: String): List<ReferenceItem> = try {
        val text = context.assets.open(path).bufferedReader().use { it.readText() }
        val data = json.decodeFromString<ReferenceData>(text)
        CanCanLogger.log("ReferenceRepository: $path → ${data.items.size} items (schema v${data.version})")
        data.items
    } catch (e: Exception) {
        CanCanLogger.log("ReferenceRepository: $path failed — ${e.message}")
        emptyList()
    }
}
