package com.example.rickandmorty.data.repository

import com.example.rickandmorty.data.model.Character
import com.example.rickandmorty.data.model.CharacterDetails
import com.example.rickandmorty.data.model.CharactersPage
import com.example.rickandmorty.data.model.Episode
import com.example.rickandmorty.data.remote.CharacterDto
import com.example.rickandmorty.data.remote.EpisodeDto
import com.example.rickandmorty.data.remote.RickAndMortyApi

interface CharactersRepository {
    suspend fun getCharacters(page: Int): CharactersPage
    suspend fun getCharacter(id: Int): CharacterDetails
}

class NetworkCharactersRepository(
    private val api: RickAndMortyApi
) : CharactersRepository {

    override suspend fun getCharacters(page: Int): CharactersPage {
        val response = api.getCharacters(page)
        return CharactersPage(
            characters = response.results.map { it.toCharacter() },
            hasNext = response.info.next != null
        )
    }

    override suspend fun getCharacter(id: Int): CharacterDetails {
        val dto = api.getCharacter(id)
        val episodeIds = dto.episode.mapNotNull { it.substringAfterLast('/').toIntOrNull() }
        val episodes = when (episodeIds.size) {
            0 -> emptyList()
            1 -> listOf(api.getEpisode(episodeIds.first()))
            else -> api.getEpisodes(episodeIds.joinToString(","))
        }
        return dto.toDetails(episodes.map { it.toEpisode() })
    }
}

internal fun CharacterDto.toCharacter() = Character(
    id = id,
    name = name,
    species = species,
    type = type,
    status = status,
    imageUrl = image
)

internal fun CharacterDto.toDetails(episodes: List<Episode>) = CharacterDetails(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    origin = origin?.name.orEmpty(),
    location = location?.name.orEmpty(),
    imageUrl = image,
    episodes = episodes
)

internal fun EpisodeDto.toEpisode() = Episode(
    id = id,
    code = episode,
    name = name,
    airDate = airDate
)
