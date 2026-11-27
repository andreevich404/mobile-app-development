package com.example.rickandmorty.fake

import com.example.rickandmorty.data.remote.CharacterDto
import com.example.rickandmorty.data.remote.CharactersResponseDto
import com.example.rickandmorty.data.remote.EpisodeDto
import com.example.rickandmorty.data.remote.NamedResourceDto
import com.example.rickandmorty.data.remote.PageInfoDto
import com.example.rickandmorty.data.remote.RickAndMortyApi

class FakeRickAndMortyApi(private val episodeIds: List<Int> = listOf(1, 2)) : RickAndMortyApi {
    val calls = mutableListOf<String>()

    private fun dto(id: Int) = CharacterDto(
        id = id,
        name = "Character $id",
        status = "Alive",
        species = "Human",
        type = "",
        gender = "Male",
        origin = NamedResourceDto("Earth"),
        location = NamedResourceDto("Citadel"),
        image = "url.$id",
        episode = episodeIds.map { "https://rickandmortyapi.com/api/episode/$it" }
    )

    private fun episodeDto(id: Int) =
        EpisodeDto(id = id, name = "Episode $id", airDate = "Date $id", episode = "S01E0$id")

    override suspend fun getCharacters(page: Int): CharactersResponseDto {
        calls += "characters:$page"
        return CharactersResponseDto(
            info = PageInfoDto(count = 4, pages = 2, next = if (page == 1) "next" else null),
            results = listOf(dto(page * 10 + 1), dto(page * 10 + 2))
        )
    }

    override suspend fun getCharacter(id: Int): CharacterDto {
        calls += "character:$id"
        return dto(id)
    }

    override suspend fun getEpisode(id: Int): EpisodeDto {
        calls += "episode:$id"
        return episodeDto(id)
    }

    override suspend fun getEpisodes(ids: String): List<EpisodeDto> {
        calls += "episodes:$ids"
        return ids.split(",").map { episodeDto(it.toInt()) }
    }
}
