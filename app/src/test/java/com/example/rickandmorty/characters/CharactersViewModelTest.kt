package com.example.rickandmorty.characters

import com.example.rickandmorty.fake.FakeCharactersRepository
import com.example.rickandmorty.fake.FakeData
import com.example.rickandmorty.rules.TestDispatcherRule
import com.example.rickandmorty.ui.characters.CharactersViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CharactersViewModelTest {
    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @Test
    fun charactersViewModel_init_loadsFirstPage() {
        val viewModel = CharactersViewModel(FakeCharactersRepository())

        val state = viewModel.uiState.value
        assertEquals(FakeData.pageOne.characters, state.characters)
        assertFalse(state.isLoading)
        assertFalse(state.endReached)
    }

    @Test
    fun charactersViewModel_loadNextPage_appendsSecondPageAndReachesEnd() {
        val viewModel = CharactersViewModel(FakeCharactersRepository())

        viewModel.loadNextPage()

        val state = viewModel.uiState.value
        assertEquals(FakeData.pageOne.characters + FakeData.pageTwo.characters, state.characters)
        assertTrue(state.endReached)
    }

    @Test
    fun charactersViewModel_loadNextPage_afterEnd_doesNotRequestMore() {
        val repository = FakeCharactersRepository()
        val viewModel = CharactersViewModel(repository)
        viewModel.loadNextPage()

        viewModel.loadNextPage()

        assertEquals(listOf(1, 2), repository.requestedPages)
    }

    @Test
    fun charactersViewModel_networkError_setsErrorAndRetryLoadsSamePage() {
        val repository = FakeCharactersRepository().apply { failing = true }
        val viewModel = CharactersViewModel(repository)
        assertTrue(viewModel.uiState.value.isInitialError)

        repository.failing = false
        viewModel.loadNextPage()

        val state = viewModel.uiState.value
        assertFalse(state.hasError)
        assertEquals(FakeData.pageOne.characters, state.characters)
        assertEquals(listOf(1, 1), repository.requestedPages)
    }

    @Test
    fun charactersViewModel_errorOnNextPage_keepsLoadedCharacters() {
        val repository = FakeCharactersRepository()
        val viewModel = CharactersViewModel(repository)

        repository.failing = true
        viewModel.loadNextPage()

        val state = viewModel.uiState.value
        assertTrue(state.hasError)
        assertFalse(state.isInitialError)
        assertEquals(FakeData.pageOne.characters, state.characters)
    }
}
