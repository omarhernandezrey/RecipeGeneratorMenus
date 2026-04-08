package com.example.recipe_generator.presentation.weeklyplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_generator.data.sync.FirestoreWeeklyPlanSync
import com.example.recipe_generator.domain.model.WeeklyPlan
import com.example.recipe_generator.domain.repository.WeeklyPlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeeklyPlanUiState(
    val plan: List<WeeklyPlan> = emptyList(),
    val isSyncing: Boolean = true,
    val error: String? = null
)

class MyWeeklyPlanViewModel(
    private val userId: String,
    private val weeklyPlanRepository: WeeklyPlanRepository,
    private val firestoreWeeklyPlanSync: FirestoreWeeklyPlanSync
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyPlanUiState())
    val uiState: StateFlow<WeeklyPlanUiState> = _uiState.asStateFlow()

    init {
        observeWeeklyPlan()
        startRemoteSync()
    }

    fun assignRecipe(day: String, mealType: String, recipeId: String) {
        viewModelScope.launch {
            runCatching {
                weeklyPlanRepository.setMeal(userId, day, mealType, recipeId)
                firestoreWeeklyPlanSync.syncDayToRemote(userId, day)
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    error = throwable.message ?: "No se pudo asignar la receta"
                )
            }
        }
    }

    fun removeMeal(day: String, mealType: String) {
        viewModelScope.launch {
            runCatching {
                weeklyPlanRepository.removeMeal(userId, day, mealType)
                firestoreWeeklyPlanSync.syncDayToRemote(userId, day)
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    error = throwable.message ?: "No se pudo eliminar la comida"
                )
            }
        }
    }

    fun getPlanForDay(day: String): List<WeeklyPlan> =
        _uiState.value.plan.filter { it.dayOfWeek == day }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun observeWeeklyPlan() {
        viewModelScope.launch {
            weeklyPlanRepository.getWeeklyPlan(userId).collect { plan ->
                _uiState.value = _uiState.value.copy(
                    plan = plan,
                    isSyncing = false
                )
            }
        }
    }

    private fun startRemoteSync() {
        viewModelScope.launch {
            runCatching {
                firestoreWeeklyPlanSync.syncRemoteToLocal(userId)
                firestoreWeeklyPlanSync.startRealtimeSync(userId)
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    error = throwable.message ?: "No se pudo sincronizar el plan semanal"
                )
            }
        }
    }

    override fun onCleared() {
        firestoreWeeklyPlanSync.stopRealtimeSync()
        super.onCleared()
    }
}
