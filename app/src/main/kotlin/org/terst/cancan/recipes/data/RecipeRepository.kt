package org.terst.cancan.recipes.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import org.terst.cancan.core.CanCanLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }

    val items: List<RecipeItem> by lazy {
        loadFile("recipes/recipes.json").also {
            CanCanLogger.log("RecipeRepository: ${it.size} recipes loaded")
        }
    }

    private fun loadFile(path: String): List<RecipeItem> = try {
        val text = context.assets.open(path).bufferedReader().use { it.readText() }
        val data = json.decodeFromString<RecipesData>(text)
        CanCanLogger.log("RecipeRepository: $path → ${data.items.size} items (schema v${data.version})")
        data.items
    } catch (e: Exception) {
        CanCanLogger.log("RecipeRepository: $path failed — ${e.message}")
        emptyList()
    }
}
