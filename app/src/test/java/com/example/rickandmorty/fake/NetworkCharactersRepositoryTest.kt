package com.example.rickandmorty.fake

import com.example.rickandmorty.data.repository.NetworkCharactersRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkCharactersRepositoryTest {

    @Test
    fun getCharacters_mapsDtoAndDetectsNextPage() = runTest {
        val repository = NetworkCharactersRepository(FakeRickAndMortyApi())

        val first = repository.getCharacters(1)
        val last = repository.getCharacters(2)

        assertEquals(listOf(11, 12), first.characters.map { it.id })
        assertTrue(first.hasNext)
        assertFalse(last.hasNext)
    }

    @Test
    fun getCharacter_manyEpisodes_requestsThemInOneCall() = runTest {
        val api = FakeRickAndMortyApi(episodeIds = listOf(1, 2))

        val details = NetworkCharactersRepository(api).getCharacter(5)

        assertEquals(listOf("S01E01", "S01E02"), details.episodes.map { it.code })
        assertEquals(listOf("character:5", "episodes:1,2"), api.calls)
    }

    @Test
    fun getCharacter_singleEpisode_usesSingleEpisodeEndpoint() = runTest {
        val api = FakeRickAndMortyApi(episodeIds = listOf(3))

        val details = NetworkCharactersRepository(api).getCharacter(5)

        assertEquals(1, details.episodes.size)
        assertEquals(listOf("character:5", "episode:3"), api.calls)
    }

    @Test
    fun getCharacter_noEpisodes_doesNotRequestEpisodes() = runTest {
        val api = FakeRickAndMortyApi(episodeIds = emptyList())

        val details = NetworkCharactersRepository(api).getCharacter(5)

        assertTrue(details.episodes.isEmpty())
        assertEquals(listOf("character:5"), api.calls)
    }
}
