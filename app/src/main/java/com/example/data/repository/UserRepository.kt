package com.example.data.repository

import com.example.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository {

    private val _currentUser = MutableStateFlow(
        User(
            id = "usr_9981",
            name = "Alex Rivera",
            email = "alex.rivera@example.com",
            isPro = false,
            remainingCredits = 3,
            isLoggedIn = true
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    fun loginWithEmail(email: String, name: String) {
        _currentUser.value = _currentUser.value.copy(
            email = email,
            name = if (name.isNotBlank()) name else email.substringBefore("@").capitalize(),
            isLoggedIn = true
        )
    }

    fun loginWithGoogle() {
        _currentUser.value = _currentUser.value.copy(
            name = "Alex Rivera (Google)",
            email = "alex.rivera@gmail.com",
            isLoggedIn = true
        )
    }

    fun logout() {
        _currentUser.value = _currentUser.value.copy(isLoggedIn = false)
    }

    fun useFreeCredit(): Boolean {
        val user = _currentUser.value
        if (user.isPro) return true
        if (user.remainingCredits > 0) {
            _currentUser.value = user.copy(remainingCredits = user.remainingCredits - 1)
            return true
        }
        return false
    }

    fun upgradeToPro() {
        _currentUser.value = _currentUser.value.copy(isPro = true)
    }

    fun restorePurchases(): Boolean {
        // Simulates restore purchase success
        _currentUser.value = _currentUser.value.copy(isPro = true)
        return true
    }
}
