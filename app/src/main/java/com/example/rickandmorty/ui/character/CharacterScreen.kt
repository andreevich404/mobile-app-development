package com.example.rickandmorty.ui.character

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rickandmorty.R
import com.example.rickandmorty.data.model.CharacterDetails
import com.example.rickandmorty.ui.ErrorContent
import com.example.rickandmorty.ui.LoadingContent

@Composable
fun CharacterScreen(
    uiState: CharacterUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    when (uiState) {
        CharacterUiState.Loading -> LoadingContent(modifier.fillMaxSize())
        CharacterUiState.Error -> ErrorContent(onRetry = onRetry, modifier = modifier.fillMaxSize())
        is CharacterUiState.Success -> CharacterDetailsContent(
            character = uiState.character,
            modifier = modifier,
            contentPadding = contentPadding
        )
    }
}

@Composable
private fun CharacterDetailsContent(
    character: CharacterDetails,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AsyncImage(
                    model = character.imageUrl,
                    contentDescription = stringResource(R.string.character_photo, character.name),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                )
                Text(text = character.name, style = MaterialTheme.typography.headlineMedium)
                InfoRow(stringResource(R.string.label_status), character.status)
                InfoRow(stringResource(R.string.label_species), character.species)
                if (character.type.isNotBlank()) {
                    InfoRow(stringResource(R.string.label_type), character.type)
                }
                InfoRow(stringResource(R.string.label_gender), character.gender)
                InfoRow(stringResource(R.string.label_origin), character.origin)
                InfoRow(stringResource(R.string.label_location), character.location)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = stringResource(R.string.label_episodes, character.episodes.size),
                    style = MaterialTheme.typography.titleLarge
                )
                if (character.episodes.isEmpty()) {
                    Text(stringResource(R.string.no_episodes))
                }
            }
        }
        items(character.episodes, key = { it.id }) { episode ->
            Column {
                Text(
                    text = "${episode.code} · ${episode.name}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = episode.airDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(text = value.ifBlank { "—" }, style = MaterialTheme.typography.bodyLarge)
    }
}
