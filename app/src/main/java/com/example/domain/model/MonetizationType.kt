package com.example.domain.model

enum class MonetizationType {
    FLATRATE,
    FREE,
    ADS,
    RENT,
    BUY
}

data class ProviderOffer(
    val provider: Provider,
    val type: MonetizationType,
    val price: Double? = null,
    val currency: String? = null,
    val link: String? = null
)
