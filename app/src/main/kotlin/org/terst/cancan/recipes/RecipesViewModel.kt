package org.terst.cancan.recipes

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.terst.cancan.recipes.data.RecipeItem
import org.terst.cancan.recipes.data.RecipeRepository
import javax.inject.Inject

private val categoryOrder = listOf(
    "Water Bath Canning", "Pressure Canning", "Fermentation", "Dehydrating", "Smoking & Curing"
)

data class RecipesUiState(
    val allItems: List<RecipeItem> = emptyList(),
    val query: String = "",
    val activeCategory: String = "All"
) {
    val categories: List<String> = listOf("All") +
        allItems.map { it.category }.distinct()
            .sortedBy { cat -> categoryOrder.indexOf(cat).takeIf { it >= 0 } ?: Int.MAX_VALUE }

    val filtered: List<RecipeItem> = allItems.filter { item ->
        (activeCategory == "All" || item.category == activeCategory) &&
            (query.isBlank() || item.name.contains(query, ignoreCase = true))
    }
}

@HiltViewModel
class RecipesViewModel @Inject constructor(
    repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipesUiState())
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(allItems = repository.items) }
    }

    fun onSearch(query: String) = _uiState.update { it.copy(query = query) }

    fun onCategorySelected(category: String) = _uiState.update { it.copy(activeCategory = category) }
}
