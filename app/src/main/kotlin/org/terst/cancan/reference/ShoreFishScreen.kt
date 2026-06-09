package org.terst.cancan.reference

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import org.terst.cancan.reference.data.ShoreFish

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoreFishScreen(navController: NavController) {
    val viewModel: ShoreFishViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedFish by remember { mutableStateOf<ShoreFish?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            items(uiState.categories) { category ->
                FilterChip(
                    selected = uiState.activeCategory == category,
                    onClick = { viewModel.onCategorySelected(category) },
                    label = { Text(category) }
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(uiState.filtered, key = { it.id }) { fish ->
                FishCard(
                    fish = fish,
                    imageUrl = uiState.imageUrls[fish.id],
                    onFetchImage = viewModel::fetchImageFor,
                    onClick = { selectedFish = fish }
                )
            }
        }
    }

    selectedFish?.let { fish ->
        ModalBottomSheet(
            onDismissRequest = { selectedFish = null },
            sheetState = sheetState
        ) {
            FishDetail(
                fish = fish,
                imageUrl = uiState.imageUrls[fish.id],
                onFetchImage = viewModel::fetchImageFor
            )
        }
    }
}

@Composable
private fun FishCard(
    fish: ShoreFish,
    imageUrl: String?,
    onFetchImage: (String, String) -> Unit,
    onClick: () -> Unit
) {
    LaunchedEffect(fish.id) {
        if (fish.wikipediaTitle.isNotBlank()) onFetchImage(fish.id, fish.wikipediaTitle)
    }

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = fish.hawaiianName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = fish.hawaiianName.first().toString(),
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(
                    text = fish.hawaiianName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = fish.commonName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FishDetail(
    fish: ShoreFish,
    imageUrl: String?,
    onFetchImage: (String, String) -> Unit
) {
    LaunchedEffect(fish.id) {
        if (fish.wikipediaTitle.isNotBlank()) onFetchImage(fish.id, fish.wikipediaTitle)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = fish.hawaiianName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(MaterialTheme.shapes.large)
            )
            Spacer(Modifier.height(16.dp))
        }

        Text(
            text = fish.hawaiianName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = fish.commonName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = fish.scientificName,
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        CategoryBadge(fish.fishingCategory)
        Spacer(Modifier.height(12.dp))

        DetailRow("Habitat", fish.habitat)
        DetailRow("Depth", fish.depthRange)
        DetailRow("Best season", fish.bestSeason)
        DetailRow("Typical size", fish.avgSize)
        DetailRow("Edibility", fish.edibilityRating)

        if (fish.identificationFeatures.isNotEmpty()) {
            DetailSection("Identification") {
                fish.identificationFeatures.forEach { BulletItem(it) }
            }
        }

        if (fish.catchMethods.isNotEmpty()) {
            DetailSection("Catch methods") {
                fish.catchMethods.forEach { BulletItem(it) }
            }
        }

        if (fish.bestSpots.isNotEmpty()) {
            DetailSection("Best spots") {
                fish.bestSpots.forEach { BulletItem(it) }
            }
        }

        if (fish.preparation.isNotBlank()) {
            DetailSection("Preparation") {
                Text(fish.preparation, style = MaterialTheme.typography.bodyMedium)
            }
        }

        if (fish.regulations.isNotBlank()) {
            DetailSection("Regulations") {
                Text(fish.regulations, style = MaterialTheme.typography.bodyMedium)
            }
        }

        if (fish.safetyNote.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Safety",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(top = 2.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = fish.safetyNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun CategoryBadge(category: String) {
    val color = when (category) {
        "Shore Casting" -> MaterialTheme.colorScheme.primary
        "Cast Net" -> MaterialTheme.colorScheme.secondary
        "Spearfishing" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    if (value.isBlank()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(96.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DetailSection(heading: String, content: @Composable () -> Unit) {
    Spacer(Modifier.height(12.dp))
    Text(
        text = heading,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(Modifier.height(4.dp))
    content()
}

@Composable
private fun BulletItem(text: String) {
    Row(modifier = Modifier.padding(start = 4.dp, top = 2.dp)) {
        Text(
            text = "·",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 6.dp)
        )
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}
