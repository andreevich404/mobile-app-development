package com.example.mycity.ui

import com.example.mycity.data.Place
import com.example.mycity.data.PlaceCategory

data class MyCityUiState(
    val currentCategory: PlaceCategory,
    val places: List<Place>,
    val selectedPlace: Place
)
