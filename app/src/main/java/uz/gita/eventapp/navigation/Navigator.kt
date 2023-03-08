package uz.gita.eventapp.navigation

import androidx.navigation.NavDirections

interface Navigator {
    suspend fun navigateTo(direction: NavDirections)
    suspend fun back()
}