package org.terst.cancan.reference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.terst.cancan.reference.data.ReferenceItem
import org.terst.cancan.reference.data.ReferenceRepository
import org.terst.cancan.reference.data.WikipediaImageRepository
import javax.inject.Inject

private val categoryOrder = listOf(
    "Tomatoes", "Vegetables", "Fruits", "Jams", "Pickles", "Meats",
    "Fermentation", "Foraging", "Hawaii Foraging", "Hawaii Fishing",
    "Dehydrating", "Smoking & Curing"
)

data class ReferenceUiState(
    val allItems: List<ReferenceItem> = emptyList(),
    val query: String = "",
    val activeCategory: String = "All",
    val imageUrls: Map<String, String> = emptyMap()
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
    repository: ReferenceRepository,
    private val wikiImages: WikipediaImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReferenceUiState())
    val uiState: StateFlow<ReferenceUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(allItems = repository.items) }
    }

    fun onSearch(query: String) = _uiState.update { it.copy(query = query) }

    fun onCategorySelected(category: String) = _uiState.update { it.copy(activeCategory = category) }

    fun fetchImageFor(itemId: String, wikipediaTitle: String) {
        if (_uiState.value.imageUrls.containsKey(itemId)) return
        viewModelScope.launch {
            val url = wikiImages.getImageUrl(itemId, wikipediaTitle) ?: return@launch
            _uiState.update { it.copy(imageUrls = it.imageUrls + (itemId to url)) }
        }
    }
}
