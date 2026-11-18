package com.example.mycity.ui

import androidx.lifecycle.ViewModel
import com.example.mycity.data.Place
import com.example.mycity.data.PlaceCategory
import com.example.mycity.data.PlacesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MyCityViewModel(
    private val repository: PlacesRepository = PlacesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(stateFor(PlaceCategory.entries.first()))
    val uiState: StateFlow<MyCityUiState> = _uiState.asStateFlow()

    fun selectCategory(category: PlaceCategory) {
        _uiState.value = stateFor(category)
    }

    fun selectPlace(place: Place) {
        _uiState.update { it.copy(selectedPlace = place) }
    }

    private fun stateFor(category: PlaceCategory): MyCityUiState {
        val places = repository.getPlaces(category)
        return MyCityUiState(
            currentCategory = category,
            places = places,
            selectedPlace = places.first()
        )
    }
}
