package com.brunorodrigues.portfolio.github.api

import com.brunorodrigues.portfolio.github.BuildConfig
import com.brunorodrigues.portfolio.github.data.RepositoriesResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Client() {
    private var service: Service

    init {
        val gson: Gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        service = retrofit.create(Service::class.java)
    }

    suspend fun getRepositories(page: Int) = service.getRepositories(page)

    suspend fun getPullRequests(userName: String, repositoryName: String, page: Int) = service.getPullRequests(userName, repositoryName, page)
}