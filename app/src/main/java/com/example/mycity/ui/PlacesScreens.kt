package com.example.mycity.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mycity.R
import com.example.mycity.data.Place

@Composable
fun PlaceCard(
    place: Place,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Image(
            painter = painterResource(place.image),
            contentDescription = stringResource(place.name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(place.name),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = stringResource(place.description),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun PlacesListOnly(
    places: List<Place>,
    onPlaceClick: (Place) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(places, key = { it.id }) { place ->
            PlaceCard(
                place = place,
                selected = false,
                onClick = { onPlaceClick(place) }
            )
        }
    }
}

@Composable
fun PlacesListAndDetail(
    places: List<Place>,
    selectedPlace: Place,
    onPlaceClick: (Place) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(places, key = { it.id }) { place ->
                PlaceCard(
                    place = place,
                    selected = place.id == selectedPlace.id,
                    onClick = { onPlaceClick(place) }
                )
            }
        }
        PlaceDetail(
            place = selectedPlace,
            modifier = Modifier
                .weight(1.4f)
                .padding(top = 16.dp, end = 16.dp, bottom = 16.dp)
        )
    }
}

@Composable
fun PlaceDetail(
    place: Place,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(place.image),
            contentDescription = stringResource(place.name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        )
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(place.name),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = stringResource(place.description),
                style = MaterialTheme.typography.bodyLarge
            )
            InfoRow(
                label = stringResource(R.string.address_label),
                value = stringResource(place.address)
            )
            InfoRow(
                label = stringResource(R.string.hours_label),
                value = stringResource(place.hours)
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
