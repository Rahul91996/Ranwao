package com.example.data.model

data class User(
    val id: String = "usr_1001",
    val name: String = "Alex Rivera",
    val email: String = "alex.rivera@example.com",
    val isPro: Boolean = false,
    val remainingCredits: Int = 3,
    val avatarUrl: String? = null,
    val isLoggedIn: Boolean = true
)

enum class MediaType {
    PHOTO, VIDEO
}

data class BackgroundPreset(
    val id: String,
    val title: String,
    val category: String,
    val drawableRes: Int? = null,
    val imageUrl: String? = null,
    val prompt: String = "",
    val isPremium: Boolean = false
)
