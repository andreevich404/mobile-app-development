package com.example.mycity

import com.example.mycity.data.LocalPlacesDataSource
import com.example.mycity.data.PlaceCategory
import com.example.mycity.ui.MyCityViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MyCityViewModelTest {
    private val viewModel = MyCityViewModel()

    @Test
    fun initialState_ShowsFirstCategoryWithFirstPlaceSelected() {
        val state = viewModel.uiState.value
        assertEquals(PlaceCategory.entries.first(), state.currentCategory)
        assertEquals(state.places.first(), state.selectedPlace)
    }

    @Test
    fun selectCategory_ShowsOnlyPlacesOfThatCategory() {
        viewModel.selectCategory(PlaceCategory.Parks)

        val state = viewModel.uiState.value
        assertEquals(PlaceCategory.Parks, state.currentCategory)
        assertTrue(state.places.all { it.category == PlaceCategory.Parks })
        assertEquals(state.places.first(), state.selectedPlace)
    }

    @Test
    fun selectPlace_UpdatesSelectedPlace() {
        val second = viewModel.uiState.value.places[1]

        viewModel.selectPlace(second)

        assertEquals(second, viewModel.uiState.value.selectedPlace)
    }

    @Test
    fun dataSource_EachCategoryHasAtLeastThreePlaces() {
        PlaceCategory.entries.forEach { category ->
            assertTrue(LocalPlacesDataSource.places.count { it.category == category } >= 3)
        }
    }
}
