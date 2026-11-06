package com.example.artspaceapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

const val ARTWORKS_COUNT = 5

class ArtSpaceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ArtSpaceUiState())
    val uiState: StateFlow<ArtSpaceUiState> = _uiState.asStateFlow()

    fun showPrevious() {
        _uiState.update { state ->
            state.copy(currentStep = if (state.currentStep == 1) ARTWORKS_COUNT else state.currentStep - 1)
        }
    }

    fun showNext() {
        _uiState.update { state ->
            state.copy(currentStep = if (state.currentStep == ARTWORKS_COUNT) 1 else state.currentStep + 1)
        }
    }
}
