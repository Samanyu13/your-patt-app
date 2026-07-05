package com.happyminds.thepattapp.domain.services

import com.happyminds.thepattapp.domain.models.Group

class DeepLinkService {
    private val BASE_URL = "https://thepattapp.com/join"

    fun generateInviteLink(group: Group): String {
        val token = group.shareToken ?: "group_${group.id}"
        return "$BASE_URL?token=$token&name=${group.name}"
    }

    fun parseInviteLink(url: String): Pair<String, String>? {
        if (!url.startsWith(BASE_URL)) return null
        
        val params = url.substringAfter("?").split("&").associate {
            val (key, value) = it.split("=")
            key to value
        }
        
        val token = params["token"] ?: return null
        val name = params["name"] ?: "Shared Group"
        
        return token to name
    }
}
