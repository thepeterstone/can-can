package org.terst.cancan.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.terst.cancan.inventory.data.InventoryItemEntity
import org.terst.cancan.inventory.data.InventoryRepository
import javax.inject.Inject

data class InventoryUiState(
    val items: List<InventoryItemEntity> = emptyList(),
    val query: String = "",
    val activeCategory: String = "All",
    val showAddSheet: Boolean = false,
    val editingItem: InventoryItemEntity? = null,
    val showScanner: Boolean = false,
    val pendingBarcode: String = ""
) {
    val categories: List<String> = listOf("All") + items.map { it.category }.distinct().sorted()
    val filtered: List<InventoryItemEntity> = items.filter { item ->
        (activeCategory == "All" || item.category == activeCategory) &&
            (query.isBlank() || item.name.contains(query, ignoreCase = true))
    }
}

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.items.collect { items ->
                _uiState.update { it.copy(items = items) }
            }
        }
    }

    fun onSearch(query: String) = _uiState.update { it.copy(query = query) }

    fun onCategorySelected(category: String) = _uiState.update { it.copy(activeCategory = category) }

    fun showAdd() = _uiState.update { it.copy(showAddSheet = true, editingItem = null) }

    fun showEdit(item: InventoryItemEntity) = _uiState.update { it.copy(showAddSheet = true, editingItem = item) }

    fun dismissSheet() = _uiState.update { it.copy(showAddSheet = false, editingItem = null) }

    fun save(item: InventoryItemEntity) {
        viewModelScope.launch { repository.save(item) }
        dismissSheet()
    }

    fun delete(item: InventoryItemEntity) {
        viewModelScope.launch { repository.delete(item) }
        dismissSheet()
    }

    fun showScanner() = _uiState.update { it.copy(showScanner = true) }

    fun dismissScanner() = _uiState.update { it.copy(showScanner = false) }

    fun onBarcodeScanned(barcode: String) = _uiState.update {
        it.copy(showScanner = false, showAddSheet = true, pendingBarcode = barcode)
    }

    fun clearPendingBarcode() = _uiState.update { it.copy(pendingBarcode = "") }
}
