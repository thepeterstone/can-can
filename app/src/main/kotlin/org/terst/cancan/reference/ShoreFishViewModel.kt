package org.terst.cancan.reference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.terst.cancan.reference.data.ShoreFish
import org.terst.cancan.reference.data.ShoreFishRepository
import org.terst.cancan.reference.data.WikipediaImageRepository
import javax.inject.Inject

data class ShoreFishUiState(
    val allSpecies: List<ShoreFish> = emptyList(),
    val activeCategory: String = "All",
    val imageUrls: Map<String, String> = emptyMap()
) {
    val categories: List<String> = listOf("All") + allSpecies.map { it.fishingCategory }.distinct()
    val filtered: List<ShoreFish> =
        if (activeCategory == "All") allSpecies
        else allSpecies.filter { it.fishingCategory == activeCategory }
}

@HiltViewModel
class ShoreFishViewModel @Inject constructor(
    repository: ShoreFishRepository,
    private val wikiImages: WikipediaImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShoreFishUiState())
    val uiState: StateFlow<ShoreFishUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(allSpecies = repository.species) }
    }

    fun onCategorySelected(category: String) = _uiState.update { it.copy(activeCategory = category) }

    fun fetchImageFor(id: String, wikipediaTitle: String) {
        if (_uiState.value.imageUrls.containsKey(id)) return
        viewModelScope.launch {
            val url = wikiImages.getImageUrl(id, wikipediaTitle) ?: return@launch
            _uiState.update { it.copy(imageUrls = it.imageUrls + (id to url)) }
        }
    }
}
