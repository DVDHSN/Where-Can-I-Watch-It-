package com.example.domain.model

data class Country(
    val code: String,
    val name: String,
    val flagEmoji: String,
    val popularProviders: List<ProviderPreset>
)

data class ProviderPreset(
    val id: Int,
    val name: String,
    val logoPath: String,
    val isDefaultSelected: Boolean = false
) {
    val logoUrl: String
        get() = if (logoPath.startsWith("http")) logoPath else "https://image.tmdb.org/t/p/w185$logoPath"
}

object CountryPresets {
    val MALAYSIA = Country(
        code = "MY",
        name = "Malaysia",
        flagEmoji = "🇲🇾",
        popularProviders = listOf(
            ProviderPreset(8, "Netflix", "/pbpMk2JmcoNnQwx5JGpXngfoWtp.jpg", isDefaultSelected = true),
            ProviderPreset(337, "Disney+ Hotstar", "/7rwgEs15tFwyR9NPQ5vpzxTj19Q.jpg", isDefaultSelected = true),
            ProviderPreset(119, "Amazon Prime Video", "/pvsGozFwhAEu5hnFjM1Gg8e1m8q.jpg", isDefaultSelected = true),
            ProviderPreset(350, "Apple TV+", "/2E03GQKVscSmb9iAg4q553f19T0.jpg", isDefaultSelected = true),
            ProviderPreset(158, "Viu", "/2nyb4pUjL6nL4i4rY2qH41kG5kZ.jpg"),
            ProviderPreset(581, "iQIYI", "/bL7k2qG3qU6o3iL9tH4h8M4xG4j.jpg"),
            ProviderPreset(283, "Crunchyroll", "/fz6n877z3bS0z0z6Wz2fK5j0eZq.jpg"),
            ProviderPreset(188, "YouTube Premium", "/oRvKc5HPkL0s7lH3mH4h3gH5mRk.jpg")
        )
    )

    val UNITED_STATES = Country(
        code = "US",
        name = "United States",
        flagEmoji = "🇺🇸",
        popularProviders = listOf(
            ProviderPreset(8, "Netflix", "/pbpMk2JmcoNnQwx5JGpXngfoWtp.jpg", isDefaultSelected = true),
            ProviderPreset(337, "Disney+", "/7rwgEs15tFwyR9NPQ5vpzxTj19Q.jpg", isDefaultSelected = true),
            ProviderPreset(1899, "Max", "/fksCUZ9QDWZMUwL2LgMtL9smByg.jpg", isDefaultSelected = true),
            ProviderPreset(119, "Amazon Prime Video", "/pvsGozFwhAEu5hnFjM1Gg8e1m8q.jpg", isDefaultSelected = true),
            ProviderPreset(15, "Hulu", "/bxBlRPEPpMVDc4jMhSrTf2339DW.jpg"),
            ProviderPreset(350, "Apple TV+", "/2E03GQKVscSmb9iAg4q553f19T0.jpg"),
            ProviderPreset(531, "Paramount+", "/fi83B1oztoS47xxcemFdPMhIzK.jpg"),
            ProviderPreset(386, "Peacock", "/8VCV78Prwd9QzZnEm0ReO6bERDa.jpg")
        )
    )

    val UNITED_KINGDOM = Country(
        code = "GB",
        name = "United Kingdom",
        flagEmoji = "🇬🇧",
        popularProviders = listOf(
            ProviderPreset(8, "Netflix", "/pbpMk2JmcoNnQwx5JGpXngfoWtp.jpg", isDefaultSelected = true),
            ProviderPreset(337, "Disney+", "/7rwgEs15tFwyR9NPQ5vpzxTj19Q.jpg", isDefaultSelected = true),
            ProviderPreset(119, "Amazon Prime Video", "/pvsGozFwhAEu5hnFjM1Gg8e1m8q.jpg", isDefaultSelected = true),
            ProviderPreset(350, "Apple TV+", "/2E03GQKVscSmb9iAg4q553f19T0.jpg"),
            ProviderPreset(38, "BBC iPlayer", "/97yvRBw1Gz75tWzB3T9n0K9r1Vz.jpg"),
            ProviderPreset(531, "Paramount+", "/fi83B1oztoS47xxcemFdPMhIzK.jpg"),
            ProviderPreset(103, "NOW", "/n7K8c8j8c8j8c8j8c8j8c8j8c8j.jpg")
        )
    )

    val AUSTRALIA = Country(
        code = "AU",
        name = "Australia",
        flagEmoji = "🇦🇺",
        popularProviders = listOf(
            ProviderPreset(8, "Netflix", "/pbpMk2JmcoNnQwx5JGpXngfoWtp.jpg", isDefaultSelected = true),
            ProviderPreset(337, "Disney+", "/7rwgEs15tFwyR9NPQ5vpzxTj19Q.jpg", isDefaultSelected = true),
            ProviderPreset(119, "Amazon Prime Video", "/pvsGozFwhAEu5hnFjM1Gg8e1m8q.jpg", isDefaultSelected = true),
            ProviderPreset(350, "Apple TV+", "/2E03GQKVscSmb9iAg4q553f19T0.jpg"),
            ProviderPreset(21, "Stan", "/mXE2F0p64f0p64f0p64f0p64f0p.jpg"),
            ProviderPreset(134, "BINGE", "/bL7k2qG3qU6o3iL9tH4h8M4xG4j.jpg")
        )
    )

    val SINGAPORE = Country(
        code = "SG",
        name = "Singapore",
        flagEmoji = "🇸🇬",
        popularProviders = listOf(
            ProviderPreset(8, "Netflix", "/pbpMk2JmcoNnQwx5JGpXngfoWtp.jpg", isDefaultSelected = true),
            ProviderPreset(337, "Disney+", "/7rwgEs15tFwyR9NPQ5vpzxTj19Q.jpg", isDefaultSelected = true),
            ProviderPreset(119, "Amazon Prime Video", "/pvsGozFwhAEu5hnFjM1Gg8e1m8q.jpg", isDefaultSelected = true),
            ProviderPreset(350, "Apple TV+", "/2E03GQKVscSmb9iAg4q553f19T0.jpg"),
            ProviderPreset(158, "Viu", "/2nyb4pUjL6nL4i4rY2qH41kG5kZ.jpg"),
            ProviderPreset(283, "Crunchyroll", "/fz6n877z3bS0z0z6Wz2fK5j0eZq.jpg")
        )
    )

    val CANADA = Country(
        code = "CA",
        name = "Canada",
        flagEmoji = "🇨🇦",
        popularProviders = listOf(
            ProviderPreset(8, "Netflix", "/pbpMk2JmcoNnQwx5JGpXngfoWtp.jpg", isDefaultSelected = true),
            ProviderPreset(337, "Disney+", "/7rwgEs15tFwyR9NPQ5vpzxTj19Q.jpg", isDefaultSelected = true),
            ProviderPreset(119, "Amazon Prime Video", "/pvsGozFwhAEu5hnFjM1Gg8e1m8q.jpg", isDefaultSelected = true),
            ProviderPreset(350, "Apple TV+", "/2E03GQKVscSmb9iAg4q553f19T0.jpg"),
            ProviderPreset(531, "Paramount+", "/fi83B1oztoS47xxcemFdPMhIzK.jpg"),
            ProviderPreset(230, "Crave", "/bxBlRPEPpMVDc4jMhSrTf2339DW.jpg")
        )
    )

    val ALL_COUNTRIES = listOf(
        MALAYSIA,
        UNITED_STATES,
        UNITED_KINGDOM,
        AUSTRALIA,
        SINGAPORE,
        CANADA
    )

    fun findByCode(code: String): Country {
        return ALL_COUNTRIES.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: MALAYSIA
    }
}
