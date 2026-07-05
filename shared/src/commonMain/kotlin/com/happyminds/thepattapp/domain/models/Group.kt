package com.happyminds.thepattapp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val id: String,
    val name: String,
    val members: List<User> = emptyList(),
    val shareToken: String? = null,
    val isSettled: Boolean = false,
    val isMiscellaneous: Boolean = false
)
