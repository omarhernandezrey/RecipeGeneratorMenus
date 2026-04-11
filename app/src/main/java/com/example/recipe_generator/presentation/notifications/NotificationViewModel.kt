package com.example.recipe_generator.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_generator.domain.model.AppNotification
import com.example.recipe_generator.domain.repository.AppNotificationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class NotificationViewModel(
    private val repository: AppNotificationRepository
) : ViewModel() {

    val notifications: StateFlow<List<AppNotification>> = repository
        .getNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val unreadCount: StateFlow<Int> = repository
        .getUnreadCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    init {
        // Limpiar notificaciones expiradas (> 24 h) al iniciar
        viewModelScope.launch {
            val cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
            repository.deleteExpired(cutoff)
        }
    }

    fun markAllRead() {
        viewModelScope.launch { repository.markAllRead() }
    }

    fun deleteById(id: String) {
        viewModelScope.launch { repository.deleteById(id) }
    }
}
