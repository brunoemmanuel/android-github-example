package com.brunorodrigues.portfolio.github.data

import com.google.gson.annotations.SerializedName
import java.util.*

data class PullRequest(
    val id: Int = 0,
    val title: String = "",
    val body: String = "",
    val user: User?,
    @SerializedName("html_url") val htmlUrl: String = "",
    @SerializedName("created_at") val createdAt: Date?
)