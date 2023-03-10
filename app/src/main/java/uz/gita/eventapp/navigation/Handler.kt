package uz.gita.eventapp.navigation

import androidx.navigation.NavController
import kotlinx.coroutines.flow.Flow

typealias navController = NavController.() -> Unit

interface Handler {
    val navigationStack: Flow<(NavController) -> Unit>
}