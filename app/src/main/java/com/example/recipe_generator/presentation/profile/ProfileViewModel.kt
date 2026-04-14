package com.example.recipe_generator.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe_generator.data.sync.FirestoreSyncService
import com.example.recipe_generator.domain.model.User
import com.example.recipe_generator.domain.model.UserProfile
import com.example.recipe_generator.domain.repository.AuthRepository
import com.example.recipe_generator.domain.repository.FavoritesRepository
import com.example.recipe_generator.domain.repository.UserPrefsRepository
import com.example.recipe_generator.domain.repository.UserProfileRepository
import com.example.recipe_generator.domain.repository.UserRecipeRepository
import com.example.recipe_generator.domain.repository.WeeklyPlanRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

data class ProfileStats(
    val recipesCount: Int = 0,
    val favoritesCount: Int = 0,
    val plannedDaysCount: Int = 0
)

/**
 * D-10: ViewModel de perfil y edición de perfil.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository,
    private val userRecipeRepository: UserRecipeRepository,
    private val favoritesRepository: FavoritesRepository,
    private val weeklyPlanRepository: WeeklyPlanRepository,
    private val userPrefsRepository: UserPrefsRepository,
    private val firestoreSyncService: FirestoreSyncService
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    private val _stats = MutableStateFlow(ProfileStats())
    val stats: StateFlow<ProfileStats> = _stats.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _saveVersion = MutableStateFlow(0)
    val saveVersion: StateFlow<Int> = _saveVersion.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                _currentUser.value = user
            }
        }

        viewModelScope.launch {
            currentUser
                .filterNotNull()
                .flatMapLatest { user -> userProfileRepository.getProfile(user.uid) }
                .collect { loadedProfile ->
                    _profile.value = loadedProfile
                }
        }

        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            currentUser
                .flatMapLatest { user ->
                    if (user == null) {
                        flowOf(ProfileStats())
                    } else {
                        combine(
                            userRecipeRepository.getMyRecipes(user.uid),
                            favoritesRepository.getFavoriteIds(user.uid),
                            weeklyPlanRepository.getWeeklyPlan(user.uid)
                        ) { recipes, favoriteIds, weeklyPlan ->
                            ProfileStats(
                                recipesCount = recipes.size,
                                favoritesCount = favoriteIds.size,
                                plannedDaysCount = weeklyPlan.map { it.dayOfWeek }.distinct().size
                            )
                        }
                    }
                }
                .collect { stats ->
                    _stats.value = stats
                }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun updateName(displayName: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            runCatching {
                authRepository.updateUserProfile(displayName, null).getOrThrow()
                val updatedProfile = buildProfile(
                    user = _currentUser.value,
                    displayName = displayName,
                    photoUrl = _profile.value?.photoUrl ?: _currentUser.value?.photoUrl,
                    preferredDiets = _profile.value?.preferredDiets.orEmpty(),
                    defaultPortions = _profile.value?.defaultPortions ?: 2
                )
                saveProfile(updatedProfile)
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo actualizar el nombre"
            }
            _isSaving.value = false
        }
    }

    fun updatePhoto(photoUrl: String?) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            runCatching {
                authRepository.updateUserProfile(null, photoUrl).getOrThrow()
                val updatedProfile = buildProfile(
                    user = _currentUser.value ?: user,
                    displayName = _profile.value?.displayName ?: user.displayName.orEmpty(),
                    photoUrl = photoUrl,
                    preferredDiets = _profile.value?.preferredDiets.orEmpty(),
                    defaultPortions = _profile.value?.defaultPortions ?: 2
                )
                saveProfile(updatedProfile)
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo actualizar la foto"
            }
            _isSaving.value = false
        }
    }

    fun saveProfileChanges(
        displayName: String,
        photoUrl: String?,
        preferredDiets: List<String>,
        defaultPortions: Int,
        culinaryStudies: String = _profile.value?.culinaryStudies.orEmpty(),
        culinaryExperience: String = _profile.value?.culinaryExperience.orEmpty()
    ) {
        val user = _currentUser.value ?: return

        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            runCatching {
                authRepository.updateUserProfile(displayName, photoUrl).getOrThrow()

                val updatedProfile = buildProfile(
                    user = _currentUser.value ?: user,
                    displayName = displayName,
                    photoUrl = photoUrl,
                    preferredDiets = preferredDiets,
                    defaultPortions = defaultPortions,
                    culinaryStudies = culinaryStudies,
                    culinaryExperience = culinaryExperience
                )
                saveProfile(updatedProfile)
                userPrefsRepository.saveDefaultPortions(defaultPortions)
                userPrefsRepository.saveSelectedDiets(preferredDiets.toSet())
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "No se pudo guardar el perfil"
            }
            _isSaving.value = false
        }
    }

    private suspend fun saveProfile(profile: UserProfile) {
        userProfileRepository.saveProfile(profile)
        firestoreSyncService.uploadProfile(profile)
        _profile.value = profile
        _saveVersion.value += 1
    }

    private fun buildProfile(
        user: User?,
        displayName: String,
        photoUrl: String?,
        preferredDiets: List<String>,
        defaultPortions: Int,
        culinaryStudies: String = _profile.value?.culinaryStudies.orEmpty(),
        culinaryExperience: String = _profile.value?.culinaryExperience.orEmpty()
    ): UserProfile {
        val currentProfile = _profile.value
        return UserProfile(
            uid = user?.uid ?: currentProfile?.uid.orEmpty(),
            displayName = displayName,
            email = currentProfile?.email?.ifBlank { null } ?: user?.email.orEmpty(),
            photoUrl = photoUrl,
            preferredDiets = preferredDiets,
            defaultPortions = defaultPortions,
            culinaryStudies = culinaryStudies,
            culinaryExperience = culinaryExperience,
            createdAt = currentProfile?.createdAt ?: System.currentTimeMillis()
        )
    }
}
