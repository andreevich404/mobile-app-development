package com.example.mycity.data

class PlacesRepository(
    private val places: List<Place> = LocalPlacesDataSource.places
) {
    fun getPlaces(category: PlaceCategory): List<Place> =
        places.filter { it.category == category }
}
