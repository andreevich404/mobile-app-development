package com.example.rickandmorty.ui.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.rickandmorty.RickAndMortyApplication
import com.example.rickandmorty.data.model.Character
import com.example.rickandmorty.data.repository.CharactersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class CharactersUiState(
    val characters: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val endReached: Boolean = false
) {
    val isInitialLoading: Boolean get() = isLoading && characters.isEmpty()
    val isLoadingMore: Boolean get() = isLoading && characters.isNotEmpty()
    val isInitialError: Boolean get() = hasError && characters.isEmpty()
}

class CharactersViewModel(
    private val repository: CharactersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharactersUiState())
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    private var nextPage = 1

    init {
        loadNextPage()
    }

    /** Loads the next page of characters; does nothing while a request is running or the list ended. */
    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoading || state.endReached) return

        _uiState.update { it.copy(isLoading = true, hasError = false) }
        viewModelScope.launch {
            try {
                val page = repository.getCharacters(nextPage)
                nextPage++
                _uiState.update {
                    it.copy(
                        characters = it.characters + page.characters,
                        isLoading = false,
                        endReached = !page.hasNext
                    )
                }
            } catch (e: IOException) {
                _uiState.update { it.copy(isLoading = false, hasError = true) }
            } catch (e: HttpException) {
                _uiState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as RickAndMortyApplication
                CharactersViewModel(application.container.charactersRepository)
            }
        }
    }
}
