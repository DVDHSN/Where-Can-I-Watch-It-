package com.example.domain.usecase

import com.example.domain.model.MonetizationType
import com.example.domain.model.Provider
import com.example.domain.model.ProviderOffer
import com.example.domain.model.RegionalAvailability
import com.example.domain.model.Verdict
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ResolveVerdictUseCaseTest {

    private lateinit var useCase: ResolveVerdictUseCase

    private val netflix = Provider(providerId = 8, providerName = "Netflix", logoPath = "/netflix.jpg")
    private val disney = Provider(providerId = 337, providerName = "Disney+", logoPath = "/disney.jpg")
    private val appleTv = Provider(providerId = 350, providerName = "Apple TV+", logoPath = "/appletv.jpg")
    private val googlePlay = Provider(providerId = 3, providerName = "Google Play Movies", logoPath = "/google.jpg")

    @Before
    fun setUp() {
        useCase = ResolveVerdictUseCase()
    }

    @Test
    fun `returns Included when title is available on user subscription`() {
        val userSubscriptions = setOf(8) // Subscribed to Netflix
        val availability = RegionalAvailability(
            countryCode = "MY",
            flatrateProviders = listOf(netflix),
            tmdbLink = "https://tmdb.org/link"
        )

        val verdict = useCase.execute("MY", userSubscriptions, availability)

        assertTrue(verdict is Verdict.Included)
        val included = verdict as Verdict.Included
        assertEquals("Netflix", included.primaryProvider.providerName)
        assertEquals(8, included.primaryProvider.providerId)
        assertEquals("https://tmdb.org/link", included.deepLink)
    }

    @Test
    fun `returns Included with multiple matching providers`() {
        val userSubscriptions = setOf(8, 337) // Subscribed to Netflix and Disney+
        val availability = RegionalAvailability(
            countryCode = "MY",
            flatrateProviders = listOf(netflix, disney)
        )

        val verdict = useCase.execute("MY", userSubscriptions, availability)

        assertTrue(verdict is Verdict.Included)
        val included = verdict as Verdict.Included
        assertEquals("Netflix", included.primaryProvider.providerName)
        assertEquals(1, included.otherMatchingProviders.size)
        assertEquals("Disney+", included.otherMatchingProviders[0].providerName)
    }

    @Test
    fun `returns RentOrBuy when not in user subscriptions but rental exists`() {
        val userSubscriptions = setOf(8) // Only Netflix
        val rentOffer = ProviderOffer(provider = appleTv, type = MonetizationType.RENT, price = 3.99, currency = "USD")
        val availability = RegionalAvailability(
            countryCode = "MY",
            flatrateProviders = listOf(disney), // Only Disney+, which user doesn't have
            rentOffers = listOf(rentOffer)
        )

        val verdict = useCase.execute("MY", userSubscriptions, availability)

        assertTrue(verdict is Verdict.RentOrBuy)
        val rent = verdict as Verdict.RentOrBuy
        assertEquals("Apple TV+", rent.primaryProvider.providerName)
        assertEquals("USD 3.99", rent.priceDisplay)
    }

    @Test
    fun `returns Unavailable when confirmed empty for region`() {
        val userSubscriptions = setOf(8, 337)
        val availability = RegionalAvailability(
            countryCode = "MY",
            flatrateProviders = emptyList(),
            rentOffers = emptyList(),
            buyOffers = emptyList(),
            isConfirmedEmpty = true
        )

        val verdict = useCase.execute("MY", userSubscriptions, availability)

        assertTrue(verdict is Verdict.Unavailable)
        val unavail = verdict as Verdict.Unavailable
        assertEquals("MY", unavail.countryCode)
    }

    @Test
    fun `returns Unknown when availability data is null`() {
        val userSubscriptions = setOf(8)

        val verdict = useCase.execute("MY", userSubscriptions, availability = null)

        assertTrue(verdict is Verdict.Unknown)
    }

    @Test
    fun `returns Included when available as free stream in user subscriptions`() {
        val userSubscriptions = setOf(188) // YouTube / Free provider
        val freeProvider = Provider(providerId = 188, providerName = "YouTube", logoPath = "/yt.jpg")
        val availability = RegionalAvailability(
            countryCode = "MY",
            freeProviders = listOf(freeProvider)
        )

        val verdict = useCase.execute("MY", userSubscriptions, availability)

        assertTrue(verdict is Verdict.Included)
        assertEquals("YouTube", (verdict as Verdict.Included).primaryProvider.providerName)
    }
}
