package com.example.rickandmorty.ui.character

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.rickandmorty.RickAndMortyApplication
import com.example.rickandmorty.data.model.CharacterDetails
import com.example.rickandmorty.data.repository.CharactersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface CharacterUiState {
    data object Loading : CharacterUiState
    data class Success(val character: CharacterDetails) : CharacterUiState
    data object Error : CharacterUiState
}

class CharacterViewModel(
    private val characterId: Int,
    private val repository: CharactersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CharacterUiState>(CharacterUiState.Loading)
    val uiState: StateFlow<CharacterUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = CharacterUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                CharacterUiState.Success(repository.getCharacter(characterId))
            } catch (e: IOException) {
                CharacterUiState.Error
            } catch (e: HttpException) {
                CharacterUiState.Error
            }
        }
    }

    companion object {
        const val CHARACTER_ID_KEY = "characterId"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as RickAndMortyApplication
                val savedStateHandle: SavedStateHandle = createSavedStateHandle()
                val id: Int = checkNotNull(savedStateHandle[CHARACTER_ID_KEY])
                CharacterViewModel(id, application.container.charactersRepository)
            }
        }
    }
}
