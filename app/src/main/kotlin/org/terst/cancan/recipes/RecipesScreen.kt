package org.terst.cancan.recipes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.terst.cancan.recipes.data.Ingredient
import org.terst.cancan.recipes.data.RecipeItem
import org.terst.cancan.recipes.data.RecipeStep
import org.terst.cancan.ui.theme.CanCanTheme

@Composable
fun RecipesScreen(navController: NavController) {
    val viewModel: RecipesViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RecipesContent(
        uiState = uiState,
        onSearch = viewModel::onSearch,
        onCategorySelected = viewModel::onCategorySelected
    )
}

@Composable
private fun RecipesContent(
    uiState: RecipesUiState,
    onSearch: (String) -> Unit,
    onCategorySelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = onSearch,
            placeholder = { Text("Search recipes…") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            items(uiState.categories) { category ->
                FilterChip(
                    selected = uiState.activeCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category) }
                )
            }
        }

        if (uiState.filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No results",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.filtered, key = { it.id }) { item ->
                    RecipeItemCard(item = item)
                }
            }
        }
    }
}

@Composable
private fun RecipeItemCard(item: RecipeItem) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                DifficultyBadge(difficulty = item.difficulty)
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }

            AnimatedVisibility(visible = expanded) {
                RecipeExpandedContent(item = item)
            }
        }
    }
}

@Composable
private fun RecipeExpandedContent(item: RecipeItem) {
    Column(modifier = Modifier.padding(top = 12.dp)) {
        if (item.summary.isNotBlank()) {
            Text(
                text = item.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
        }

        val meta = listOfNotNull(
            item.timeEstimate.takeIf { it.isNotBlank() }?.let { "Time: $it" },
            item.recipeYield.takeIf { it.isNotBlank() }?.let { "Yield: $it" }
        ).joinToString(" · ")
        if (meta.isNotBlank()) {
            Text(
                text = meta,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (item.ingredients.isNotEmpty()) {
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            item.ingredients.forEach { ingredient ->
                IngredientRow(ingredient = ingredient)
            }
        }

        if (item.steps.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Steps",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            item.steps.forEachIndexed { index, step ->
                RecipeStepBlock(stepNumber = index + 1, step = step)
            }
        }

        if (item.safetyNotes.isNotBlank()) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Safety note",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 2.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = item.safetyNotes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Text(
            text = "Source: ${item.source}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun IngredientRow(ingredient: Ingredient) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "·",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 6.dp, top = 1.dp)
        )
        Column {
            val quantityAndName = listOfNotNull(
                ingredient.quantity.takeIf { it.isNotBlank() },
                ingredient.name
            ).joinToString(" ")
            Text(text = quantityAndName, style = MaterialTheme.typography.bodySmall)
            if (ingredient.notes.isNotBlank()) {
                Text(
                    text = ingredient.notes,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RecipeStepBlock(stepNumber: Int, step: RecipeStep) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row {
            Text(
                text = "$stepNumber.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .width(24.dp)
                    .padding(top = 1.dp)
            )
            Text(
                text = step.instruction,
                style = MaterialTheme.typography.bodySmall
            )
        }
        if (step.tips.isNotEmpty()) {
            step.tips.forEach { tip ->
                Row(modifier = Modifier.padding(start = 24.dp, top = 2.dp)) {
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: String) {
    val color = when (difficulty.lowercase()) {
        "beginner" -> MaterialTheme.colorScheme.primary
        "intermediate" -> MaterialTheme.colorScheme.secondary
        "advanced" -> MaterialTheme.colorScheme.tertiary
        else -> return
    }
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = difficulty.replaceFirstChar { it.uppercaseChar() },
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRecipesScreen() {
    CanCanTheme {
        RecipesContent(
            uiState = RecipesUiState(
                allItems = listOf(
                    RecipeItem(
                        id = "wbc-strawberry-jam",
                        name = "Strawberry Jam",
                        category = "Water Bath Canning",
                        summary = "A classic, pectin-set strawberry jam with bright flavor and firm set.",
                        difficulty = "beginner",
                        timeEstimate = "1.5–2 hours",
                        recipeYield = "6–7 half-pints",
                        ingredients = listOf(
                            Ingredient("Fresh strawberries, hulled and crushed", "5 cups"),
                            Ingredient("Granulated sugar", "7 cups")
                        ),
                        steps = listOf(
                            RecipeStep(
                                "Prepare canner and jars.",
                                listOf("Keep jars hot until filling.")
                            ),
                            RecipeStep("Crush strawberries and stir in pectin and lemon juice.")
                        ),
                        safetyNotes = "Do not reduce sugar — it is a preservative.",
                        source = "Ball Complete Book of Home Preserving"
                    ),
                    RecipeItem(
                        id = "ferm-sauerkraut",
                        name = "Classic Sauerkraut",
                        category = "Fermentation",
                        summary = "Traditional lacto-fermented sauerkraut from just cabbage and salt.",
                        difficulty = "beginner",
                        timeEstimate = "1–4 weeks",
                        recipeYield = "1 quart",
                        source = "Traditional / NCHFP"
                    )
                )
            ),
            onSearch = {},
            onCategorySelected = {}
        )
    }
}
