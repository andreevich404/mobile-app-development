package com.example.rickandmorty.di

import com.example.rickandmorty.data.remote.RickAndMortyApi
import com.example.rickandmorty.data.repository.CharactersRepository
import com.example.rickandmorty.data.repository.NetworkCharactersRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

interface AppContainer {
    val charactersRepository: CharactersRepository
}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "https://rickandmortyapi.com/api/"

    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val api: RickAndMortyApi by lazy { retrofit.create(RickAndMortyApi::class.java) }

    override val charactersRepository: CharactersRepository by lazy {
        NetworkCharactersRepository(api)
    }
}
