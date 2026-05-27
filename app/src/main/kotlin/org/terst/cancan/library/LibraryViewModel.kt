package org.terst.cancan.library

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class LibraryUiState(
    val allDocuments: List<LibraryDocument> = emptyList(),
    val activeCategory: String? = null
) {
    val categories: List<String> = allDocuments.map { it.category }.distinct()
    val filtered: List<LibraryDocument> = if (activeCategory == null) {
        allDocuments
    } else {
        allDocuments.filter { it.category == activeCategory }
    }
}

@HiltViewModel
class LibraryViewModel @Inject constructor(
    repository: LibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(allDocuments = repository.documents) }
    }

    fun onCategorySelected(category: String?) {
        _uiState.update { it.copy(activeCategory = category) }
    }
}
