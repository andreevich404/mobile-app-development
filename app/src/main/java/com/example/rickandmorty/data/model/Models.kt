package com.example.rickandmorty.data.model

data class Character(
    val id: Int,
    val name: String,
    val species: String,
    val type: String,
    val status: String,
    val imageUrl: String
)

data class CharactersPage(
    val characters: List<Character>,
    val hasNext: Boolean
)

data class Episode(
    val id: Int,
    val code: String,
    val name: String,
    val airDate: String
)

data class CharacterDetails(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: String,
    val location: String,
    val imageUrl: String,
    val episodes: List<Episode>
)
