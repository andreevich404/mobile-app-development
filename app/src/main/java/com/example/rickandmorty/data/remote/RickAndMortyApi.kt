package com.example.rickandmorty.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int): CharactersResponseDto

    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): CharacterDto

    @GET("episode/{id}")
    suspend fun getEpisode(@Path("id") id: Int): EpisodeDto

    /** Several ids separated by comma, e.g. "1,2,3". The API returns an array for 2+ ids. */
    @GET("episode/{ids}")
    suspend fun getEpisodes(@Path("ids") ids: String): List<EpisodeDto>
}

@Serializable
data class CharactersResponseDto(
    val info: PageInfoDto,
    val results: List<CharacterDto>
)

@Serializable
data class PageInfoDto(
    val count: Int,
    val pages: Int,
    val next: String? = null,
    val prev: String? = null
)

@Serializable
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String = "",
    val gender: String = "",
    val origin: NamedResourceDto? = null,
    val location: NamedResourceDto? = null,
    val image: String,
    val episode: List<String> = emptyList()
)

@Serializable
data class NamedResourceDto(
    val name: String,
    val url: String = ""
)

@Serializable
data class EpisodeDto(
    val id: Int,
    val name: String,
    @SerialName("air_date") val airDate: String = "",
    val episode: String
)
