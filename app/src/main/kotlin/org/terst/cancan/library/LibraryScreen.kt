package org.terst.cancan.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@Composable
fun LibraryScreen(navController: NavController) {
    val viewModel: LibraryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LibraryContent(
        uiState = uiState,
        onCategorySelected = viewModel::onCategorySelected,
        onDocumentClick = { doc -> navController.navigate("pdf_viewer/${doc.id}") }
    )
}

@Composable
private fun LibraryContent(
    uiState: LibraryUiState,
    onCategorySelected: (String?) -> Unit,
    onDocumentClick: (LibraryDocument) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Library",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            item {
                FilterChip(
                    selected = uiState.activeCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text("All") }
                )
            }
            items(uiState.categories) { category ->
                FilterChip(
                    selected = uiState.activeCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category) }
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(uiState.filtered, key = { it.id }) { doc ->
                DocumentCard(doc = doc, onClick = { onDocumentClick(doc) })
            }
        }
    }
}

@Composable
private fun DocumentCard(doc: LibraryDocument, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doc.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = doc.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                if (doc.description.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = doc.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open document",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
