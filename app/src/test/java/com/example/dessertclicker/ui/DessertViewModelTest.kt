package com.example.dessertclicker.ui

import com.example.dessertclicker.data.Datasource
import org.junit.Assert.assertEquals
import org.junit.Test

class DessertViewModelTest {
    private val viewModel = DessertViewModel()
    private val desserts = Datasource.dessertList

    @Test
    fun initialState_NothingSoldAndFirstDessertShown() {
        val state = viewModel.uiState.value
        assertEquals(0, state.dessertsSold)
        assertEquals(0, state.revenue)
        assertEquals(desserts.first().imageId, state.currentDessertImageId)
    }

    @Test
    fun onDessertClicked_IncreasesSoldAndAddsPriceToRevenue() {
        val price = viewModel.uiState.value.currentDessertPrice

        viewModel.onDessertClicked()

        val state = viewModel.uiState.value
        assertEquals(1, state.dessertsSold)
        assertEquals(price, state.revenue)
    }

    @Test
    fun onDessertClicked_AfterThreshold_ShowsNextDessert() {
        val next = desserts[1]
        repeat(next.startProductionAmount) { viewModel.onDessertClicked() }

        val state = viewModel.uiState.value
        assertEquals(next.imageId, state.currentDessertImageId)
        assertEquals(next.price, state.currentDessertPrice)
    }
}
