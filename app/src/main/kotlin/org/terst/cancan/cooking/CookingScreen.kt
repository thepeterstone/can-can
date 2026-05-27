package org.terst.cancan.cooking

import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.terst.cancan.recipes.data.RecipeItem

private val TIMER_PRESETS = listOf(5, 10, 15, 20, 30, 45, 60)

@Composable
fun CookingScreen(navController: NavController) {
    val viewModel: CookingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as? Activity

    LaunchedEffect(uiState.isActive) {
        if (uiState.isActive) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
    DisposableEffect(Unit) {
        onDispose { activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Cooking",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        uiState.session?.let { session ->
            item {
                SessionCard(
                    session = session,
                    onPrev = viewModel::prevStep,
                    onNext = viewModel::nextStep,
                    onEnd = viewModel::endSession,
                    onAddTimer = viewModel::addTimer
                )
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Timers",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                FilledTonalButton(onClick = viewModel::showAddTimer) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Timer")
                }
            }
        }

        if (uiState.timers.isEmpty()) {
            item {
                Text(
                    text = "No active timers.\nTap \"Add Timer\" or start a recipe.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(uiState.timers, key = { it.id }) { timer ->
                TimerCard(
                    timer = timer,
                    onToggle = { viewModel.toggleTimer(timer.id) },
                    onReset = { viewModel.resetTimer(timer.id) },
                    onRemove = { viewModel.removeTimer(timer.id) }
                )
            }
        }

        if (uiState.session == null) {
            item {
                OutlinedButton(
                    onClick = viewModel::showRecipePicker,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Follow a Recipe")
                }
            }
        }
    }

    if (uiState.showAddTimer) {
        AddTimerSheet(onAdd = viewModel::addTimer, onDismiss = viewModel::dismissAddTimer)
    }

    if (uiState.showRecipePicker) {
        RecipePickerSheet(
            recipes = uiState.recipes,
            onSelect = viewModel::startSession,
            onDismiss = viewModel::dismissRecipePicker
        )
    }
}

@Composable
private fun SessionCard(
    session: CookingSession,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onEnd: () -> Unit,
    onAddTimer: (String, Long) -> Unit
) {
    val step = session.currentStep ?: return
    val suggestedMs = remember(step.instruction) { parseDurationMs(step.instruction) }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.recipe.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Step ${session.stepIndex + 1} of ${session.stepCount}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = onEnd) { Text("End") }
            }

            LinearProgressIndicator(
                progress = { (session.stepIndex + 1f) / session.stepCount },
                modifier = Modifier.fillMaxWidth()
            )

            Text(text = step.instruction, style = MaterialTheme.typography.bodyLarge)

            step.tips.forEach { tip ->
                Text(
                    text = "Tip: $tip",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (suggestedMs != null) {
                FilledTonalButton(
                    onClick = {
                        onAddTimer(
                            "${session.recipe.name} – step ${session.stepIndex + 1}",
                            suggestedMs
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Start ${formatMs(suggestedMs)} timer")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onPrev,
                    enabled = !session.isFirst,
                    modifier = Modifier.weight(1f)
                ) { Text("← Prev") }
                Button(
                    onClick = onNext,
                    enabled = !session.isLast,
                    modifier = Modifier.weight(1f)
                ) { Text("Next →") }
            }
        }
    }
}

@Composable
private fun TimerCard(
    timer: TimerItem,
    onToggle: () -> Unit,
    onReset: () -> Unit,
    onRemove: () -> Unit
) {
    val timeColor by animateColorAsState(
        targetValue = when {
            timer.isFinished -> MaterialTheme.colorScheme.error
            timer.isRunning && timer.remainingMs < 60_000L -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.onSurface
        },
        label = "timerColor"
    )

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = timer.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove timer",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = formatMs(timer.remainingMs),
                style = MaterialTheme.typography.displayMedium,
                fontFamily = FontFamily.Monospace,
                color = timeColor,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            if (timer.isFinished) {
                Text(
                    text = "Done!",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                LinearProgressIndicator(
                    progress = { timer.progress },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                if (!timer.isFinished) {
                    FilledTonalIconButton(onClick = onToggle) {
                        Icon(
                            imageVector = if (timer.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (timer.isRunning) "Pause" else "Resume"
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }
                FilledTonalIconButton(onClick = onReset) {
                    Icon(Icons.Default.Replay, contentDescription = "Reset timer")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTimerSheet(onAdd: (String, Long) -> Unit, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by rememberSaveable { mutableStateOf("") }
    var selectedPreset by rememberSaveable { mutableStateOf(10) }
    var customStr by rememberSaveable { mutableStateOf("") }
    var useCustom by rememberSaveable { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Add Timer", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name (optional)") },
                placeholder = { Text("e.g. Processing time") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Duration",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(TIMER_PRESETS) { min ->
                    FilterChip(
                        selected = !useCustom && selectedPreset == min,
                        onClick = { selectedPreset = min; useCustom = false },
                        label = { Text("${min}m") }
                    )
                }
                item {
                    FilterChip(
                        selected = useCustom,
                        onClick = { useCustom = true },
                        label = { Text("Custom") }
                    )
                }
            }

            if (useCustom) {
                OutlinedTextField(
                    value = customStr,
                    onValueChange = { customStr = it.filter(Char::isDigit) },
                    label = { Text("Minutes") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            val durationMs = if (useCustom) {
                (customStr.toLongOrNull() ?: 0L) * 60_000L
            } else {
                selectedPreset * 60_000L
            }

            Button(
                onClick = { onAdd(name.trim(), durationMs) },
                enabled = durationMs > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Start Timer")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipePickerSheet(
    recipes: List<RecipeItem>,
    onSelect: (RecipeItem) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Choose a Recipe",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.heightIn(max = 440.dp)
            ) {
                items(recipes) { recipe ->
                    ListItem(
                        headlineContent = { Text(recipe.name) },
                        supportingContent = {
                            val steps = "${recipe.steps.size} steps"
                            val meta = if (recipe.timeEstimate.isNotBlank()) {
                                "${recipe.category} · $steps · ${recipe.timeEstimate}"
                            } else {
                                "${recipe.category} · $steps"
                            }
                            Text(meta)
                        },
                        modifier = Modifier.clickable { onSelect(recipe) }
                    )
                }
            }
        }
    }
}

private fun formatMs(ms: Long): String {
    val total = (ms / 1000).coerceAtLeast(0L)
    val h = total / 3600
    val m = (total % 3600) / 60
    val s = total % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}

private fun parseDurationMs(text: String): Long? {
    val regex = Regex("""(\d+)\s*(?:to \d+ )?minutes?""", RegexOption.IGNORE_CASE)
    val match = regex.find(text) ?: return null
    return match.groupValues[1].toLong() * 60_000L
}
