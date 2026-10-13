# Where Can I Watch It?

**Find out where to watch your next movie or TV show — using subscriptions you already have.**

Where Can I Watch It? is an Android app that helps users discover where movies and TV shows are available to stream. Instead of checking Netflix, Disney+, Prime Video, and other streaming platforms individually, users can search for a title in one place and see which services offer it.

The app's primary goal is simple: **help users find something to watch without paying for another subscription unnecessarily.**

## ✨ Features

### 🔎 Universal Search
- Search for movies and TV shows by title.
- Browse matching results with posters and essential information.
- Distinguish between movies, TV shows, and different releases.
- View detailed information about a selected title.

### 📺 Streaming Availability
- Find streaming services that offer a selected title.
- Identify services where the title is included with a subscription.
- Distinguish subscription streaming from rental and purchase options when availability data supports it.
- Display a clear message when no supported streaming option is found.

### 🎯 Your Subscriptions
- Select the streaming services you currently subscribe to.
- Save subscription preferences locally.
- Prioritise results available through your existing subscriptions.
- Identify which service you can use to watch a particular title.

### 🎬 Movie & TV Show Details
- Display movie and TV show posters.
- Show titles, release dates, genres, and descriptions where available.
- Present streaming availability alongside title information.
- Provide links to relevant streaming services when supported.

### ⚙️ Preferences
- Manage your selected streaming services.
- Save app preferences between sessions.
- Configure supported settings without creating an account, where applicable.

## 🎯 Core Philosophy

Where Can I Watch It? is not intended to be another streaming catalogue or recommendation platform.

It is a practical search tool built around one question:

> **"Is this available on a streaming service I already pay for?"**

The app prioritises useful, accurate availability information over endless browsing, recommendations, and unnecessary features.

## 📱 How It Works

1. **Search** — Enter the name of a movie or TV show.
2. **Select** — Open the correct title from the search results.
3. **Check** — View the streaming services offering that title.
4. **Match** — See which available services match your saved subscriptions.
5. **Watch** — Open the relevant streaming service and start watching.

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Kotlin | Primary programming language |
| Jetpack Compose | Native Android user interface |
| TMDB API | Movie and TV show metadata |
| Retrofit | HTTP networking |
| OkHttp | Network client |
| Moshi | JSON parsing and serialization |
| Room | Local database support |
| DataStore | Persistent user preferences |
| Navigation Compose | Navigation between screens |

Streaming availability should be retrieved from a suitable provider that supports the required countries and availability categories. TMDB can provide watch-provider information for supported titles and regions, subject to its API coverage and terms.

**No AI is used anywhere in this application.** There are no AI models, AI-generated recommendations, LLM integrations, or AI-powered search features.

## 🌍 Regional Availability

Streaming catalogues differ by country. Availability results should be requested for the user's selected region wherever the data provider supports it.

The app should clearly distinguish between:
- Included with a subscription
- Available to rent
- Available to purchase
- Free or ad-supported, when supported by the data source
- No supported availability found

An unavailable result does not necessarily mean a title cannot be watched anywhere. It means no matching option was found in the supported availability data.

## 🔐 Privacy

Where Can I Watch It? is designed to keep the experience simple and minimise unnecessary data collection.

- Subscription selections can be stored locally.
- No streaming-service passwords are required.
- The app does not need access to users' streaming accounts.
- No AI processing or AI-related data collection takes place.

Any additional analytics, diagnostics, or third-party services should be disclosed clearly if introduced.

## 🚧 Project Status

This project is under development. Feature availability depends on the current implementation, API coverage, and regional data support.

Planned improvements may include:
- Faster search and improved result handling.
- More accurate subscription matching.
- Better handling of unavailable titles and API errors.
- Improved loading, empty, and error states.
- A refined native Android experience.

## ⚙️ Setup

### Requirements
- Android Studio
- A compatible Android SDK
- JDK version supported by the project
- A TMDB API key

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/DVDHSN/Where-Can-I-Watch-It-.git
   ```

2. Open the project in Android Studio.

3. Obtain a TMDB API key from [The Movie Database](https://www.themoviedb.org/).

4. Configure the API key using the environment or local configuration mechanism expected by the project. Refer to the example environment file and existing configuration instructions.

5. Sync the Gradle project.

6. Build and run the app on an Android emulator or compatible device.

**Important:** Never commit private API credentials or secrets to version control.

## 📡 Data Sources

- **[TMDB](https://www.themoviedb.org/)** — Movie and television metadata, posters, and supported regional watch-provider information.

Availability coverage depends on the data provider. The app does not guarantee that every title or streaming platform will be represented.

This product uses TMDB data but is not endorsed or certified by TMDB. Refer to TMDB's applicable API terms and attribution requirements.

## 🗺️ Roadmap

- [ ] Reliable movie and TV show search
- [ ] Detailed title pages
- [ ] Regional streaming availability
- [ ] Subscription selection and persistence
- [ ] Subscription-matched availability results
- [ ] Clear rental and purchase distinctions
- [ ] Direct links to supported streaming services
- [ ] Improved error handling and offline behaviour
- [ ] UI polish and accessibility improvements

## 📄 License

Check the repository's `LICENSE` file for the applicable licensing terms. If no license has been provided, the project's reuse and distribution permissions should be established before publishing or redistributing the code.

---

**Where Can I Watch It? — Spend less time searching. Spend more time watching.**
