package com.brunorodrigues.portfolio.github.api

import com.brunorodrigues.portfolio.github.data.PullRequest
import com.brunorodrigues.portfolio.github.data.RepositoriesResponse
import com.brunorodrigues.portfolio.github.data.User
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Service {
    @GET("/search/repositories?q=language:Java&sort=stars")
    suspend fun getRepositories(
        @Query("page") page: Int
    ): RepositoriesResponse

    @GET("/repos/{owner}/{repo}/pulls")
    suspend fun getPullRequests(@Path("owner") userName: String,
                                @Path("repo") repositoryName: String,
                                @Query("page") page: Int
    ): ArrayList<PullRequest>

    @GET("/users/{username}")
    suspend fun getUser(
        @Path("username") name: String
    ): User
}