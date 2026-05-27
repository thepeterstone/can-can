package org.terst.cancan.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.terst.cancan.inventory.data.InventoryItemEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CATEGORIES = listOf(
    "Water Bath Canning",
    "Pressure Canning",
    "Fermentation",
    "Dehydrating",
    "Smoking & Curing",
    "Frozen",
    "Other"
)

private val UNITS = listOf("jars", "half-pints", "pints", "quarts", "lbs", "oz", "pieces", "batches")

private val DATE_FORMAT = SimpleDateFormat("MMM d, yyyy", Locale.US)

@Composable
fun InventoryScreen(navController: NavController) {
    val viewModel: InventoryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.showScanner) {
        BarcodeScannerScreen(
            onBarcodeDetected = viewModel::onBarcodeScanned,
            onDismiss = viewModel::dismissScanner
        )
        return
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::showAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add item")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Text(
                text = "Inventory",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onSearch,
                placeholder = { Text("Search items…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(uiState.categories) { category ->
                    FilterChip(
                        selected = uiState.activeCategory == category,
                        onClick = { viewModel.onCategorySelected(category) },
                        label = { Text(category) }
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.filtered, key = { it.id }) { item ->
                    InventoryItemCard(item = item, onClick = { viewModel.showEdit(item) })
                }
            }
        }

        if (uiState.showAddSheet) {
            AddEditSheet(
                item = uiState.editingItem,
                pendingBarcode = uiState.pendingBarcode,
                onSave = viewModel::save,
                onDelete = viewModel::delete,
                onDismiss = viewModel::dismissSheet,
                onScanBarcode = viewModel::showScanner
            )
        }
    }
}

@Composable
private fun InventoryItemCard(item: InventoryItemEntity, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = MaterialTheme.shapes.extraSmall,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = item.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "${item.quantity} ${item.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ExpiryChip(item.expiryDate)
            }
        }
    }
}

@Composable
private fun ExpiryChip(expiryDate: Long?) {
    if (expiryDate == null) return
    val now = System.currentTimeMillis()
    val thirtyDays = 30L * 24 * 60 * 60 * 1000
    val (label, color) = when {
        expiryDate < now -> "Expired" to MaterialTheme.colorScheme.error
        expiryDate < now + thirtyDays -> "Expires soon" to MaterialTheme.colorScheme.tertiary
        else -> DATE_FORMAT.format(Date(expiryDate)) to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Spacer(Modifier.height(4.dp))
    Text(text = label, style = MaterialTheme.typography.labelSmall, color = color)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditSheet(
    item: InventoryItemEntity?,
    pendingBarcode: String,
    onSave: (InventoryItemEntity) -> Unit,
    onDelete: (InventoryItemEntity) -> Unit,
    onDismiss: () -> Unit,
    onScanBarcode: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isEdit = item != null

    var name by rememberSaveable { mutableStateOf(item?.name ?: "") }
    var category by rememberSaveable { mutableStateOf(item?.category ?: CATEGORIES.first()) }
    var quantityStr by rememberSaveable { mutableStateOf(item?.quantity?.toString() ?: "1") }
    var unit by rememberSaveable { mutableStateOf(item?.unit ?: UNITS.first()) }
    var notes by rememberSaveable { mutableStateOf(item?.notes ?: "") }
    var barcode by rememberSaveable(pendingBarcode) { mutableStateOf(pendingBarcode.ifBlank { item?.barcode ?: "" }) }
    var hasExpiry by rememberSaveable { mutableStateOf(item?.expiryDate != null) }
    var expiryMs by rememberSaveable { mutableStateOf(item?.expiryDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var unitExpanded by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = expiryMs)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    expiryMs = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isEdit) "Edit Item" else "Add Item",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    CATEGORIES.forEach { c ->
                        DropdownMenuItem(text = { Text(c) }, onClick = { category = c; categoryExpanded = false })
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = { quantityStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Qty") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                ExposedDropdownMenuBox(
                    expanded = unitExpanded,
                    onExpandedChange = { unitExpanded = it },
                    modifier = Modifier.weight(2f)
                ) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(unitExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = unitExpanded,
                        onDismissRequest = { unitExpanded = false }
                    ) {
                        UNITS.forEach { u ->
                            DropdownMenuItem(text = { Text(u) }, onClick = { unit = u; unitExpanded = false })
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Expiry date", modifier = Modifier.weight(1f))
                Switch(checked = hasExpiry, onCheckedChange = { hasExpiry = it; if (!it) expiryMs = null })
            }

            if (hasExpiry) {
                val label = expiryMs?.let { DATE_FORMAT.format(Date(it)) } ?: "Pick date"
                TextButton(onClick = { showDatePicker = true }) { Text(label) }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text("Barcode") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onScanBarcode) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan barcode")
                }
            }

            Button(
                onClick = {
                    onSave(
                        InventoryItemEntity(
                            id = item?.id ?: 0,
                            name = name.trim(),
                            category = category,
                            quantity = quantityStr.toIntOrNull() ?: 1,
                            unit = unit,
                            notes = notes.trim(),
                            dateAdded = item?.dateAdded ?: System.currentTimeMillis(),
                            expiryDate = if (hasExpiry) expiryMs else null,
                            barcode = barcode.trim()
                        )
                    )
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }

            if (isEdit) {
                TextButton(
                    onClick = { onDelete(item!!) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
