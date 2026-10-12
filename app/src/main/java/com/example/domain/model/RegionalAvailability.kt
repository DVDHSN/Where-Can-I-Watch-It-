package com.example.domain.model

data class RegionalAvailability(
    val countryCode: String,
    val flatrateProviders: List<Provider> = emptyList(),
    val freeProviders: List<Provider> = emptyList(),
    val adsProviders: List<Provider> = emptyList(),
    val rentOffers: List<ProviderOffer> = emptyList(),
    val buyOffers: List<ProviderOffer> = emptyList(),
    val tmdbLink: String? = null,
    val isConfirmedEmpty: Boolean = false
)
