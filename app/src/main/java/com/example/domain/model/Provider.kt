package com.example.domain.model

data class Provider(
    val providerId: Int,
    val providerName: String,
    val logoPath: String?,
    val displayPriority: Int = 0
) {
    val logoUrl: String?
        get() = logoPath?.let {
            if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w185$it"
        }
}
