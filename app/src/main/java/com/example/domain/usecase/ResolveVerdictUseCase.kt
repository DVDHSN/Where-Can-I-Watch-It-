package com.example.domain.usecase

import com.example.domain.model.Provider
import com.example.domain.model.ProviderOffer
import com.example.domain.model.RegionalAvailability
import com.example.domain.model.Verdict

class ResolveVerdictUseCase {

    fun execute(
        countryCode: String,
        userSubscriptions: Set<Int>,
        availability: RegionalAvailability?
    ): Verdict {
        if (availability == null) {
            return Verdict.Unknown(reason = "Availability data not retrieved")
        }

        // Subscriptions include flatrate and free streams
        val subscriptionProviders = availability.flatrateProviders + availability.freeProviders

        // 1. Check for matching active user subscriptions
        val matchingSubscriptions = subscriptionProviders.filter { provider ->
            userSubscriptions.contains(provider.providerId)
        }

        if (matchingSubscriptions.isNotEmpty()) {
            val primary = matchingSubscriptions.first()
            val others = matchingSubscriptions.drop(1)
            return Verdict.Included(
                primaryProvider = primary,
                otherMatchingProviders = others,
                deepLink = availability.tmdbLink
            )
        }

        // 2. Check for rental or digital purchase options
        if (availability.rentOffers.isNotEmpty() || availability.buyOffers.isNotEmpty()) {
            val primaryOffer = availability.rentOffers.firstOrNull() ?: availability.buyOffers.first()
            return Verdict.RentOrBuy(
                primaryProvider = primaryOffer.provider,
                priceDisplay = primaryOffer.price?.let { p ->
                    val curr = primaryOffer.currency ?: ""
                    "$curr $p".trim()
                },
                allRentals = availability.rentOffers,
                allPurchases = availability.buyOffers,
                deepLink = availability.tmdbLink
            )
        }

        // 3. Confirmed unavailable vs unknown
        if (availability.isConfirmedEmpty) {
            return Verdict.Unavailable(countryCode = countryCode)
        }

        return Verdict.Unknown(reason = "No confirmed provider options found for $countryCode")
    }
}
