package com.brunorodrigues.portfolio.github.data

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int = 0,
    val login: String = "",
    val name: String = "",
    @SerializedName("avatar_url") val avatarUrl: String = ""
)