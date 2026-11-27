package com.example.rickandmorty.fake

import com.example.rickandmorty.data.model.CharacterDetails
import com.example.rickandmorty.data.model.CharactersPage
import com.example.rickandmorty.data.repository.CharactersRepository
import java.io.IOException

class FakeCharactersRepository(
    private val pages: List<CharactersPage> = listOf(FakeData.pageOne, FakeData.pageTwo),
    private val details: CharacterDetails = FakeData.details
) : CharactersRepository {
    var failing = false
    val requestedPages = mutableListOf<Int>()

    override suspend fun getCharacters(page: Int): CharactersPage {
        requestedPages += page
        if (failing) throw IOException("no network")
        return pages[page - 1]
    }

    override suspend fun getCharacter(id: Int): CharacterDetails {
        if (failing) throw IOException("no network")
        return details
    }
}
