package com.example.rickandmorty.fake

import com.example.rickandmorty.data.model.Character
import com.example.rickandmorty.data.model.CharacterDetails
import com.example.rickandmorty.data.model.CharactersPage
import com.example.rickandmorty.data.model.Episode

object FakeData {
    fun character(id: Int) = Character(
        id = id,
        name = "Character $id",
        species = "Human",
        type = "",
        status = "Alive",
        imageUrl = "url.$id"
    )

    val pageOne = CharactersPage(listOf(character(1), character(2)), hasNext = true)
    val pageTwo = CharactersPage(listOf(character(3), character(4)), hasNext = false)

    val episodes = listOf(
        Episode(id = 1, code = "S01E01", name = "Pilot", airDate = "December 2, 2013"),
        Episode(id = 2, code = "S01E02", name = "Lawnmower Dog", airDate = "December 9, 2013")
    )

    val details = CharacterDetails(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        origin = "Earth (C-137)",
        location = "Citadel of Ricks",
        imageUrl = "url.1",
        episodes = episodes
    )
}
