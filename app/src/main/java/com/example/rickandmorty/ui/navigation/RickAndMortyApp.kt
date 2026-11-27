package com.example.rickandmorty.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rickandmorty.R
import com.example.rickandmorty.ui.character.CharacterScreen
import com.example.rickandmorty.ui.character.CharacterViewModel
import com.example.rickandmorty.ui.characters.CharactersScreen
import com.example.rickandmorty.ui.characters.CharactersViewModel

object Routes {
    const val CHARACTERS = "characters"
    const val CHARACTER = "character/{${CharacterViewModel.CHARACTER_ID_KEY}}"
    fun character(id: Int) = "character/$id"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RickAndMortyApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val isCharacterScreen = backStackEntry?.destination?.route == Routes.CHARACTER

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (isCharacterScreen) R.string.character_title else R.string.characters_title
                        )
                    )
                },
                navigationIcon = {
                    if (isCharacterScreen) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = stringResource(R.string.back_button)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.CHARACTERS,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Routes.CHARACTERS) {
                val viewModel: CharactersViewModel = viewModel(factory = CharactersViewModel.Factory)
                val uiState by viewModel.uiState.collectAsState()
                CharactersScreen(
                    uiState = uiState,
                    onCharacterClick = { id -> navController.navigate(Routes.character(id)) },
                    onLoadMore = viewModel::loadNextPage,
                    contentPadding = innerPadding
                )
            }
            composable(
                route = Routes.CHARACTER,
                arguments = listOf(
                    navArgument(CharacterViewModel.CHARACTER_ID_KEY) { type = NavType.IntType }
                )
            ) {
                val viewModel: CharacterViewModel = viewModel(factory = CharacterViewModel.Factory)
                val uiState by viewModel.uiState.collectAsState()
                CharacterScreen(
                    uiState = uiState,
                    onRetry = viewModel::load,
                    contentPadding = innerPadding
                )
            }
        }
    }
}
