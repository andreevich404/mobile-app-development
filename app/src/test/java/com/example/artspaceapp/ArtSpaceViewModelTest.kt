package com.example.artspaceapp

import org.junit.Assert.assertEquals
import org.junit.Test

class ArtSpaceViewModelTest {
    private val viewModel = ArtSpaceViewModel()

    @Test
    fun initialStep_IsFirstArtwork() {
        assertEquals(1, viewModel.uiState.value.currentStep)
    }

    @Test
    fun showNext_FromLastArtwork_WrapsToFirst() {
        repeat(ARTWORKS_COUNT) { viewModel.showNext() }
        assertEquals(1, viewModel.uiState.value.currentStep)
    }

    @Test
    fun showPrevious_FromFirstArtwork_WrapsToLast() {
        viewModel.showPrevious()
        assertEquals(ARTWORKS_COUNT, viewModel.uiState.value.currentStep)
    }
}
