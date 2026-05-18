package org.terst.cancan.reference

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.terst.cancan.reference.data.GuideSection
import org.terst.cancan.reference.data.ProcessingEntry
import org.terst.cancan.reference.data.ReferenceItem
import org.terst.cancan.ui.theme.CanCanTheme

@Composable
fun ReferenceScreen(navController: NavController) {
    val viewModel: ReferenceViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ReferenceContent(
        uiState = uiState,
        onSearch = viewModel::onSearch,
        onCategorySelected = viewModel::onCategorySelected
    )
}

@Composable
private fun ReferenceContent(
    uiState: ReferenceUiState,
    onSearch: (String) -> Unit,
    onCategorySelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = onSearch,
            placeholder = { Text("Search foods, guides, plants…") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            items(uiState.categories) { category ->
                FilterChip(
                    selected = uiState.activeCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category) }
                )
            }
        }

        if (uiState.filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No results",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.filtered, key = { it.id }) { item ->
                    ReferenceItemCard(item = item)
                }
            }
        }
    }
}

@Composable
private fun ReferenceItemCard(item: ReferenceItem) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (item.type == "lookup") {
                    MethodBadge(method = item.method)
                    Spacer(Modifier.width(8.dp))
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }

            AnimatedVisibility(visible = expanded) {
                if (item.type == "guide") {
                    GuideExpandedContent(item = item)
                } else {
                    LookupExpandedContent(item = item)
                }
            }
        }
    }
}

@Composable
private fun LookupExpandedContent(item: ReferenceItem) {
    Column(modifier = Modifier.padding(top = 12.dp)) {
        item.entries.forEachIndexed { index, entry ->
            if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
            ProcessingEntryRow(entry = entry)
        }
        if (item.altitudeNote.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Altitude: ${item.altitudeNote}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        SafetyAndSource(item = item)
    }
}

@Composable
private fun GuideExpandedContent(item: ReferenceItem) {
    Column(modifier = Modifier.padding(top = 12.dp)) {
        if (item.summary.isNotBlank()) {
            Text(
                text = item.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
        }

        val meta = listOfNotNull(
            item.difficulty.takeIf { it.isNotBlank() }
                ?.let { "Difficulty: ${it.replaceFirstChar { c -> c.uppercaseChar() }}" },
            item.timeEstimate.takeIf { it.isNotBlank() }?.let { "Time: $it" }
        ).joinToString(" · ")
        if (meta.isNotBlank()) {
            Text(
                text = meta,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item.sections.forEach { section ->
            GuideSectionBlock(section = section)
        }

        SafetyAndSource(item = item)
    }
}

@Composable
private fun GuideSectionBlock(section: GuideSection) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = section.heading,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = section.body,
            style = MaterialTheme.typography.bodySmall
        )
        if (section.tips.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            section.tips.forEach { tip ->
                Row(modifier = Modifier.padding(start = 8.dp, top = 2.dp)) {
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(text = tip, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun SafetyAndSource(item: ReferenceItem) {
    if (item.safetyNotes.isNotBlank()) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = "Safety note",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .size(16.dp)
                    .padding(top = 2.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = item.safetyNotes,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
    Text(
        text = "Source: ${item.source}",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun MethodBadge(method: String) {
    val label = when (method) {
        "water_bath" -> "WB"
        "pressure" -> "PC"
        "either" -> "WB/PC"
        else -> return
    }
    val color = when (method) {
        "water_bath" -> MaterialTheme.colorScheme.primary
        "pressure" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.secondary
    }
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun ProcessingEntryRow(entry: ProcessingEntry) {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "${entry.jarSize.formatJarSize()} · ${entry.pack.replaceFirstChar { it.uppercaseChar() }} pack",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${entry.processingTimeMinutes} min",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
        if (entry.pressurePsiWeighted != null && entry.pressurePsiDial != null) {
            Text(
                text = "Weighted: ${entry.pressurePsiWeighted} lb  ·  Dial: ${entry.pressurePsiDial} lb",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun String.formatJarSize(): String = when (this) {
    "half_pint" -> "½ pint"
    "pint" -> "Pint"
    "quart" -> "Quart"
    else -> replaceFirstChar { it.uppercaseChar() }.replace('_', ' ')
}

@Preview(showBackground = true)
@Composable
private fun LookupCardPreview() {
    CanCanTheme {
        ReferenceContent(
            uiState = ReferenceUiState(
                allItems = listOf(
                    ReferenceItem(
                        id = "green-beans", name = "Green Beans", category = "Vegetables",
                        type = "lookup", method = "pressure",
                        safetyNotes = "Low-acid. Pressure canning required.",
                        source = "USDA 2015",
                        altitudeNote = "Dial: +1 lb per 2,000 ft above 2,000 ft.",
                        entries = listOf(
                            ProcessingEntry("pint", "hot", 20, 10, 11),
                            ProcessingEntry("quart", "hot", 25, 10, 11)
                        )
                    ),
                    ReferenceItem(
                        id = "sauerkraut-guide", name = "Sauerkraut", category = "Fermentation",
                        type = "guide", summary = "Classic fermented cabbage.",
                        difficulty = "beginner", timeEstimate = "1–4 weeks",
                        source = "NCHFP",
                        sections = listOf(
                            GuideSection("How It Works", "Salt draws water from cabbage, creating brine.",
                                tips = listOf("Use 1–3% salt by weight", "Keep cabbage submerged"))
                        )
                    )
                )
            ),
            onSearch = {},
            onCategorySelected = {}
        )
    }
}
