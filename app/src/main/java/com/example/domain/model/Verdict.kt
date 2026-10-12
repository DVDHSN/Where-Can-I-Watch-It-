package com.example.domain.model

sealed interface Verdict {
    data class Included(
        val primaryProvider: Provider,
        val otherMatchingProviders: List<Provider> = emptyList(),
        val deepLink: String? = null
    ) : Verdict

    data class RentOrBuy(
        val primaryProvider: Provider,
        val priceDisplay: String? = null,
        val allRentals: List<ProviderOffer> = emptyList(),
        val allPurchases: List<ProviderOffer> = emptyList(),
        val deepLink: String? = null
    ) : Verdict

    data class Unavailable(
        val countryCode: String
    ) : Verdict

    data class Unknown(
        val reason: String
    ) : Verdict
}
