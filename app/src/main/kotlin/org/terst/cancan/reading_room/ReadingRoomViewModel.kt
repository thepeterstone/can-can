package org.terst.cancan.reading_room

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ReadingRoomUiState(
    val allDocuments: List<ReadingRoomDocument> = emptyList(),
    val activeCategory: String? = null
) {
    val categories: List<String> = allDocuments.map { it.category }.distinct()
    val filtered: List<ReadingRoomDocument> = if (activeCategory == null) {
        allDocuments
    } else {
        allDocuments.filter { it.category == activeCategory }
    }
}

@HiltViewModel
class ReadingRoomViewModel @Inject constructor(
    repository: ReadingRoomRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReadingRoomUiState())
    val uiState: StateFlow<ReadingRoomUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(allDocuments = repository.documents) }
    }

    fun onCategorySelected(category: String?) {
        _uiState.update { it.copy(activeCategory = category) }
    }
}
