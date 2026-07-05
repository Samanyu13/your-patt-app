package com.happyminds.thepattapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.happyminds.thepattapp.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "ThePattApp",
        ) {
            App()
        }
    }
}