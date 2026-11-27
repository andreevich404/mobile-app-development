package com.example.rickandmorty.ui.characters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rickandmorty.R
import com.example.rickandmorty.data.model.Character
import com.example.rickandmorty.ui.ErrorContent
import com.example.rickandmorty.ui.LoadingContent
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

/** How many items before the end of the list the next page starts loading. */
private const val PRELOAD_DISTANCE = 4

@Composable
fun CharactersScreen(
    uiState: CharactersUiState,
    onCharacterClick: (Int) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    when {
        uiState.isInitialLoading -> LoadingContent(modifier.fillMaxSize())
        uiState.isInitialError -> ErrorContent(onRetry = onLoadMore, modifier = modifier.fillMaxSize())
        else -> CharactersList(
            uiState = uiState,
            onCharacterClick = onCharacterClick,
            onLoadMore = onLoadMore,
            modifier = modifier,
            contentPadding = contentPadding
        )
    }
}

@Composable
private fun CharactersList(
    uiState: CharactersUiState,
    onCharacterClick: (Int) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val listState = rememberLazyListState()
    listState.OnReachEnd(itemCount = uiState.characters.size, action = onLoadMore)

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(uiState.characters, key = { it.id }) { character ->
            CharacterCard(character = character, onClick = { onCharacterClick(character.id) })
        }
        if (uiState.isLoadingMore) {
            item(key = "loading") {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
        if (uiState.hasError) {
            item(key = "error") {
                ErrorContent(onRetry = onLoadMore, modifier = Modifier.fillMaxWidth().padding(16.dp))
            }
        }
    }
}

/** Calls [action] when the user scrolls close to the last item of the list. */
@Composable
private fun LazyListState.OnReachEnd(itemCount: Int, action: () -> Unit) {
    val shouldLoadMore by remember(itemCount) {
        derivedStateOf {
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            itemCount > 0 && lastVisible >= itemCount - 1 - PRELOAD_DISTANCE
        }
    }
    LaunchedEffect(this, itemCount) {
        snapshotFlow { shouldLoadMore }
            .distinctUntilChanged()
            .filter { it }
            .collect { action() }
    }
}

@Composable
fun CharacterCard(character: Character, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = stringResource(R.string.character_photo, character.name),
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp))
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = character.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (character.type.isNotBlank()) {
                        "${character.species} (${character.type})"
                    } else {
                        character.species
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = character.status,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
