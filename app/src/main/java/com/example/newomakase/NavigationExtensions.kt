package com.example.newomakase

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

fun Fragment.findSafeNavController(currentDestinationId: Int? = null): NavController? {
    val navController = findNavController()
    if (isAdded && !isDetached && view != null && !isRemoving) {
        if (currentDestinationId == null || navController.currentDestination?.id == currentDestinationId) {
            return navController
        }
    }
    return null
}