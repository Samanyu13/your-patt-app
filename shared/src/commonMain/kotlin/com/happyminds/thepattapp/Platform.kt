package com.happyminds.thepattapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform