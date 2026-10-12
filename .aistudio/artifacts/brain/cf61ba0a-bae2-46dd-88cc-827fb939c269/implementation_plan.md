# Where Can I Watch It? — Streaming Availability Android App

A native Android utility that delivers an instant, subscriptions-first answer to: **"Can I watch this with the subscriptions I already pay for?"** Built with Kotlin, Jetpack Compose (Material 3), Room local caching, and live TMDB search & regional watch provider data.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural and user choices have been confirmed and incorporated into the design:
>
> - **API Key Handling**: Configured to read `TMDB_API_KEY` from the AI Studio Secrets panel via `BuildConfig` (injected through `.env` / `.env.example`), with a manual fallback/override field in the Settings screen so testing or custom key rotation is frictionless.
> - **Default Regional Configuration**: Malaysia (`MY`) pre-selected with popular regional streaming platforms (**Netflix**, **Disney+ Hotstar**, **Prime Video**, and **Apple TV+**), alongside full international support for switching to the US, UK, Australia, Singapore, etc.
> - **Watchlist Availability Tracking**: Periodic background checks using Android `WorkManager` with native Android notifications (`POST_NOTIFICATIONS`) for "Now Included" and "Leaving Soon" status updates.

---

## 1. Overview & Core Concept

### What It Does
*Where Can I Watch It?* is a streamlined streaming verdict engine. Instead of forcing users to browse multiple streaming apps or sift through cluttered catalogs, it allows users to specify their current active streaming subscriptions once. When searching for any movie or TV series, the app immediately evaluates regional availability and returns a definitive color-coded verdict:
- 🟢 **Included**: Available at no additional charge on one or more of the user's active subscriptions (e.g. Netflix, Disney+).
- 🟡 **Rent or Buy**: Not included in active subscriptions, but available for rental or digital purchase with verified store links.
- 🔴 **Unavailable**: Confirmed not streaming or purchasable in the selected country.
- ⚪ **Unknown**: Network failure, missing data, or unverified provider status—never falsifying availability.

### Target Audience & Persona
Film and TV enthusiasts, casual evening viewers, and budget-conscious cord-cutters who subscribe to 2–5 streaming services and want an instant, zero-friction answer to what they can watch tonight without opening 5 different apps.

### Key Value
- **Subscriptions-First**: Personalized verdicts tailored strictly to services the user pays for.
- **Honest & Un-fabricated**: Zero mock data; every title, provider, and verdict is computed against real TMDB Watch Providers API responses and validated regional coverage.
- **Offline & Instant**: Cached recent queries, metadata, and watchlists via Room Database.

---

## 2. User Experience & Visual Design

### Key User Flows

```
[ First Launch ] ──> [ Onboarding Flow ]
                           │
                           ├── Step 1: Select Region (Default: Malaysia)
                           ├── Step 2: Select Subscriptions (Netflix, Disney+, Prime, Apple TV+)
                           └── Step 3: Complete & Save to DataStore / Room
                                     │
                                     ▼
                     [ Main 4-Tab Navigation ]
        ┌───────────────────┬─────────────────┬──────────────────┐
        │                   │                 │                  │
   [ Search ]          [ Watchlist ]     [ Discover ]       [ Settings ]
        │                   │                 │                  │
        ├─ Search Query     ├─ Saved Titles   ├─ Trending Movies ├─ Country / Region
        ├─ Instant Verdict  ├─ Live Verdicts  ├─ Popular TV      ├─ Active Subscriptions
        └─ Detail Sheet     └─ Status Alerts  └─ Direct Verdict  ├─ Notification Settings
                                                                 └─ Cache & API Config
```

### Visual Identity & Theme
- **Theme & Atmosphere**: Premium Cinema Dark Mode (dominant near-black background `#0D0F14`, deep slate container `#161B22`, subtle elevated surfaces `#212836`).
- **Verdict Color Language**:
  - 🟢 **Included**: Emerald Green `#10B981` (Container: `#064E3B`, On-Container: `#A7F3D0`)
  - 🟡 **Rent / Buy**: Amber Gold `#F59E0B` (Container: `#78350F`, On-Container: `#FDE68A`)
  - 🔴 **Unavailable**: Crimson Red `#EF4444` (Container: `#7F1D1D`, On-Container: `#FECACA`)
  - ⚪ **Unknown**: Slate Neutral `#94A3B8` (Container: `#334155`, On-Container: `#E2E8F0`)
- **Typography & Hierarchy**: Clean, high-contrast M3 typography hierarchy with bold headline weights for verdicts and tabular-friendly metadata badges (year, runtime, rating, provider badges).
- **Responsive Layout**: Edge-to-edge support with `WindowInsets.safeDrawing`, compact bottom navigation for mobile, and max-width card constraints (`Modifier.widthIn(max = 640.dp)`) for tablet/large-screen ergonomics.

---

## 3. Key Product Decisions & Trade-Offs

| Decision | Chosen Approach | Rationale | Alternatives Considered |
|---|---|---|---|
| **Data Provider** | The Movie Database (TMDB) API v3 | Official, robust, global coverage of movies, TV shows, and Watch Providers (powered by JustWatch metadata). High rate limits, stable schemas. | Streaming Availability API (RapidAPI free tier is highly restrictive and often throttled). |
| **API Key Storage** | `BuildConfig.TMDB_API_KEY` via Secrets Gradle Plugin + Settings override | Fully compliant with AI Studio Secrets workflow while preserving developer flexibility and testability. | Hardcoded key (security violation), user-only input (poor zero-state UX). |
| **Local Persistence** | Room Database (SQLite via KSP) | Type-safe queries, reactive `Flow` updates, transactional persistence for titles, watchlist, and provider cache. | Shared Preferences / pure DataStore (insufficient for relational queries & caching). |
| **Preferences** | Jetpack DataStore (Preferences) | Non-blocking coroutines-based storage for user subscriptions, selected region, and notification flags. | SharedPreferences (synchronous I/O risk on main thread). |
| **Background Refresh** | Android `WorkManager` with PeriodicWorkRequest | Battery-efficient, OS-scheduled background tasks for checking watchlist status changes and triggering notifications. | Foreground service (unnecessary battery drain and intrusive notification). |

---

## 4. Technical Architecture & Data Strategy

### System Architecture Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                              UI LAYER                                  │
│  SearchScreen      WatchlistScreen     DiscoverScreen   SettingsScreen │
│  DetailsBottomSheet / DetailsScreen    OnboardingDialog / Flow         │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Unidirectional State Flow
┌───────────────────────────────────▼────────────────────────────────────┐
│                           VIEWMODEL LAYER                              │
│       SearchViewModel         WatchlistViewModel       SettingsViewModel│
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ UseCases / Business Logic
┌───────────────────────────────────▼────────────────────────────────────┐
│                            DOMAIN LAYER                                │
│       ResolveVerdictUseCase       GetWatchlistUseCase                  │
│       SearchTitlesUseCase         UpdateSubscriptionsUseCase           │
│  Models: Title, Provider, RegionalAvailability, Verdict, MonetizationType│
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Repository Pattern
┌───────────────────────────────────▼────────────────────────────────────┐
│                             DATA LAYER                                 │
│  ┌─────────────────────────────┐     ┌───────────────────────────────┐ │
│  │   TmdbRepository / Api      │     │  Local Database (Room)        │ │
│  │   - Retrofit + OkHttp       │     │  - TitleEntity & TitleDao     │ │
│  │   - Moshi JSON Converters   │     │  - WatchlistEntity & Dao      │ │
│  │   - Live Watch Providers    │     │  - AvailabilityCacheEntity    │ │
│  └──────────────┬──────────────┘     └───────────────┬───────────────┘ │
│                 │                                    │                 │
│                 └──────────────┬─────────────────────┘                 │
│                                │                                       │
│                 ┌──────────────▼──────────────┐                        │
│                 │ DataStore (User Preferences)│                        │
│                 │ - Country: "MY"             │                        │
│                 │ - Subscriptions: [8, 337...] │                        │
│                 └─────────────────────────────┘                        │
└────────────────────────────────────────────────────────────────────────┘
```

### Core Data Models & Verdict Engine

1. **Monetization Types**:
   - `FLATRATE` (Subscription stream, e.g., Netflix, Disney+, Prime Video)
   - `FREE` / `ADS` (Ad-supported streaming)
   - `RENT` (Digital rental, e.g., Apple TV Store, Google Play Movies)
   - `BUY` (Digital purchase)

2. **Verdict Calculation (`ResolveVerdictUseCase`)**:
   ```kotlin
   fun resolve(
       title: Title,
       countryCode: String,
       userSubscriptions: Set<Int>, // TMDB Provider IDs (e.g. 8 for Netflix, 337 for Disney+)
       availability: RegionalAvailability?
   ): Verdict {
       if (availability == null) return Verdict.Unknown("Data unavailable")
       
       // Step 1: Check user subscriptions (FLATRATE / FREE)
       val matchingSub = availability.flatrateProviders.firstOrNull { 
           userSubscriptions.contains(it.providerId) 
       }
       if (matchingSub != null) {
           return Verdict.Included(provider = matchingSub)
       }
       
       // Step 2: Check rental or digital purchase options
       if (availability.rentProviders.isNotEmpty() || availability.buyProviders.isNotEmpty()) {
           val bestRental = availability.rentProviders.firstOrNull() 
               ?: availability.buyProviders.first()
           return Verdict.RentOrBuy(
               primaryProvider = bestRental,
               allRentals = availability.rentProviders,
               allPurchases = availability.buyProviders
           )
       }
       
       // Step 3: Verified absence vs unknown
       if (availability.isConfirmedEmpty) {
           return Verdict.Unavailable
       }
       return Verdict.Unknown("No verified stream in $countryCode")
   }
   ```

3. **Background Worker (`WatchlistAvailabilityWorker`)**:
   - Checks titles in `WatchlistDao`.
   - Compares cached verdict with current live provider verdict.
   - If a title transitions from `RentOrBuy` / `Unavailable` to `Included`, posts a system notification: *"Great news! [Title] is now streaming on your [Provider] subscription."*

---

## 5. Implementation Stages & Verification Plan

1. **Project Setup & Dependencies**:
   - Enable dependencies in `app/build.gradle.kts`: `coil-compose`, `androidx-navigation-compose`, `androidx-datastore-preferences`, `work-runtime-ktx`, Moshi & Retrofit.
   - Configure `.env.example` with `TMDB_API_KEY`.
   - Update `AndroidManifest.xml` with `INTERNET`, `ACCESS_NETWORK_STATE`, and `POST_NOTIFICATIONS`.

2. **Data & Domain Layer Implementation**:
   - Create TMDB API DTOs, Retrofit Service, and OkHttp client with auth interceptor.
   - Setup Room Database (`WatchlistEntity`, `CachedTitleEntity`, `AppDatabase`).
   - Implement `UserPreferencesRepository` backed by DataStore.
   - Implement `ResolveVerdictUseCase` with strict zero-fabricated-data handling.

3. **Unit & Logic Testing**:
   - Write comprehensive unit tests in `app/src/test` covering:
     - Subscription match -> `Verdict.Included`
     - No subscription, rental available -> `Verdict.RentOrBuy`
     - Empty provider catalog -> `Verdict.Unavailable`
     - Network failure / null data -> `Verdict.Unknown`
     - Country code filtering and provider ID mapping.

4. **UI Layer & Material 3 Screen Design**:
   - **Theme & Components**: Verdict badges, provider icons, movie card, bottom navigation bar.
   - **Onboarding Flow**: Country picker (default Malaysia) + streaming provider selection checklist.
   - **Search Screen**: Reactive search bar, filter chips (All, Movies, TV), active subscriptions banner, search results with instant verdict badges.
   - **Title Details Bottom Sheet / View**: Full poster, backdrop, overview, runtime, genres, personalized verdict banner with deep links to provider web pages, and watchlist toggle.
   - **Watchlist Screen**: Filter by verdict status (All, Included, Rent/Buy), swipe/button to remove, direct verdict display.
   - **Discover Screen**: TMDB trending and popular titles with instant subscription verdicts.
   - **Settings Screen**: Change country, toggle subscribed services, API key configuration, cache reset, attribution to TMDB / JustWatch.

5. **Background Sync & Notifications**:
   - Implement `WatchlistSyncWorker` with `WorkManager`.
   - Setup Android Notification Channel for availability alerts.

6. **End-to-End Verification**:
   - Compile and verify with `compile_applet`.
   - Validate UI layout, edge-to-edge rendering, test tags, and reactive persistence across app restarts.
