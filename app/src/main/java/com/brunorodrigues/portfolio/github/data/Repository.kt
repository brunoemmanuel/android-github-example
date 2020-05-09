package com.brunorodrigues.portfolio.github.data

import com.google.gson.annotations.SerializedName

data class Repository(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val owner: User?,
    @SerializedName("forks_count") val forksCount: Int = 0,
    @SerializedName("stargazers_count") val stargazersCount: Int = 0
)