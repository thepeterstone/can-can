package org.terst.cancan.reference

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.terst.cancan.reference.data.ReferenceItem
import org.terst.cancan.reference.data.ReferenceRepository
import javax.inject.Inject

private val categoryOrder = listOf("Tomatoes", "Vegetables", "Fruits", "Jams", "Pickles", "Meats")

data class ReferenceUiState(
    val allItems: List<ReferenceItem> = emptyList(),
    val query: String = "",
    val activeCategory: String = "All"
) {
    val categories: List<String> = listOf("All") +
        allItems.map { it.category }.distinct()
            .sortedBy { cat -> categoryOrder.indexOf(cat).takeIf { it >= 0 } ?: Int.MAX_VALUE }

    val filtered: List<ReferenceItem> = allItems.filter { item ->
        (activeCategory == "All" || item.category == activeCategory) &&
            (query.isBlank() || item.name.contains(query, ignoreCase = true))
    }
}

@HiltViewModel
class ReferenceViewModel @Inject constructor(
    repository: ReferenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReferenceUiState())
    val uiState: StateFlow<ReferenceUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(allItems = repository.items) }
    }

    fun onSearch(query: String) = _uiState.update { it.copy(query = query) }

    fun onCategorySelected(category: String) = _uiState.update { it.copy(activeCategory = category) }
}
