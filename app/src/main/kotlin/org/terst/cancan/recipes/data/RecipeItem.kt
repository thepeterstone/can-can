package org.terst.cancan.recipes.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipesData(
    val version: Int,
    val items: List<RecipeItem>
)

@Serializable
data class RecipeItem(
    val id: String,
    val name: String,
    val category: String,
    val summary: String = "",
    val difficulty: String = "",
    @SerialName("time_estimate") val timeEstimate: String = "",
    @SerialName("yield") val recipeYield: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<RecipeStep> = emptyList(),
    @SerialName("safety_notes") val safetyNotes: String = "",
    val source: String = ""
)

@Serializable
data class Ingredient(
    val name: String,
    val quantity: String = "",
    val notes: String = ""
)

@Serializable
data class RecipeStep(
    val instruction: String,
    val tips: List<String> = emptyList()
)
