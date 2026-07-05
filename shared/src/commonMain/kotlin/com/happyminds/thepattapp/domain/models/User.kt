package com.happyminds.thepattapp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String? = null,
    val isPlaceholder: Boolean = false
)
