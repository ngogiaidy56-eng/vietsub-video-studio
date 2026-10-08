package com.vietsub.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.vietsub.app.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Vietsub Video Studio") { App() }
}
