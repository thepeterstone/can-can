package org.terst.cancan.cooking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.terst.cancan.recipes.data.RecipeItem
import org.terst.cancan.recipes.data.RecipeRepository
import java.util.UUID
import javax.inject.Inject

data class TimerItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val totalMs: Long,
    val remainingMs: Long,
    val isRunning: Boolean = false
) {
    val isFinished: Boolean get() = remainingMs <= 0
    val progress: Float get() =
        if (totalMs <= 0) 1f else (1f - remainingMs.toFloat() / totalMs).coerceIn(0f, 1f)
}

data class CookingSession(
    val recipe: RecipeItem,
    val stepIndex: Int = 0
) {
    val currentStep get() = recipe.steps.getOrNull(stepIndex)
    val isFirst: Boolean get() = stepIndex == 0
    val isLast: Boolean get() = recipe.steps.isEmpty() || stepIndex >= recipe.steps.lastIndex
    val stepCount: Int get() = recipe.steps.size
}

data class CookingUiState(
    val timers: List<TimerItem> = emptyList(),
    val session: CookingSession? = null,
    val showAddTimer: Boolean = false,
    val showRecipePicker: Boolean = false,
    val recipes: List<RecipeItem> = emptyList()
) {
    val hasRunningTimers: Boolean get() = timers.any { it.isRunning }
    val isActive: Boolean get() = hasRunningTimers || session != null
}

@HiltViewModel
class CookingViewModel @Inject constructor(
    recipeRepository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CookingUiState(recipes = recipeRepository.items))
    val uiState: StateFlow<CookingUiState> = _uiState.asStateFlow()

    private var tickJob: Job? = null
    private var timerCount = 0

    fun addTimer(name: String, durationMs: Long) {
        timerCount++
        val label = name.ifBlank { "Timer $timerCount" }
        val timer = TimerItem(name = label, totalMs = durationMs, remainingMs = durationMs, isRunning = true)
        _uiState.update { it.copy(timers = it.timers + timer, showAddTimer = false) }
        ensureTickRunning()
    }

    fun toggleTimer(id: String) {
        _uiState.update { state ->
            state.copy(timers = state.timers.map { t ->
                if (t.id == id && !t.isFinished) t.copy(isRunning = !t.isRunning) else t
            })
        }
        ensureTickRunning()
    }

    fun resetTimer(id: String) {
        _uiState.update { state ->
            state.copy(timers = state.timers.map { t ->
                if (t.id == id) t.copy(remainingMs = t.totalMs, isRunning = false) else t
            })
        }
    }

    fun removeTimer(id: String) {
        _uiState.update { it.copy(timers = it.timers.filter { t -> t.id != id }) }
    }

    fun showAddTimer() = _uiState.update { it.copy(showAddTimer = true) }
    fun dismissAddTimer() = _uiState.update { it.copy(showAddTimer = false) }
    fun showRecipePicker() = _uiState.update { it.copy(showRecipePicker = true) }
    fun dismissRecipePicker() = _uiState.update { it.copy(showRecipePicker = false) }

    fun startSession(recipe: RecipeItem) =
        _uiState.update { it.copy(session = CookingSession(recipe), showRecipePicker = false) }

    fun endSession() = _uiState.update { it.copy(session = null) }

    fun nextStep() = _uiState.update { state ->
        val s = state.session ?: return@update state
        if (!s.isLast) state.copy(session = s.copy(stepIndex = s.stepIndex + 1)) else state
    }

    fun prevStep() = _uiState.update { state ->
        val s = state.session ?: return@update state
        if (!s.isFirst) state.copy(session = s.copy(stepIndex = s.stepIndex - 1)) else state
    }

    private fun ensureTickRunning() {
        if (tickJob?.isActive == true) return
        tickJob = viewModelScope.launch {
            while (_uiState.value.timers.any { it.isRunning }) {
                delay(1000L)
                _uiState.update { state ->
                    state.copy(timers = state.timers.map { t ->
                        if (t.isRunning && !t.isFinished) {
                            val newRemaining = (t.remainingMs - 1000L).coerceAtLeast(0L)
                            t.copy(remainingMs = newRemaining, isRunning = newRemaining > 0)
                        } else t
                    })
                }
            }
        }
    }
}
