package com.example.rickandmorty.character

import com.example.rickandmorty.fake.FakeCharactersRepository
import com.example.rickandmorty.fake.FakeData
import com.example.rickandmorty.rules.TestDispatcherRule
import com.example.rickandmorty.ui.character.CharacterUiState
import com.example.rickandmorty.ui.character.CharacterViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CharacterViewModelTest {
    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @Test
    fun characterViewModel_init_loadsCharacterWithEpisodes() {
        val viewModel = CharacterViewModel(1, FakeCharactersRepository())

        assertEquals(CharacterUiState.Success(FakeData.details), viewModel.uiState.value)
    }

    @Test
    fun characterViewModel_networkError_setsErrorState() {
        val repository = FakeCharactersRepository().apply { failing = true }
        val viewModel = CharacterViewModel(1, repository)

        assertEquals(CharacterUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun characterViewModel_load_afterError_recoversToSuccess() {
        val repository = FakeCharactersRepository().apply { failing = true }
        val viewModel = CharacterViewModel(1, repository)

        repository.failing = false
        viewModel.load()

        assertEquals(CharacterUiState.Success(FakeData.details), viewModel.uiState.value)
    }
}
