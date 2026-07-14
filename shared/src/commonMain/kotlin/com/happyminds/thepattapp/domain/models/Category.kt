package com.happyminds.thepattapp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val name: String,
    val parentCategoryId: String? = null,
    val icon: String? = null
)
