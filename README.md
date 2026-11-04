# HabitHive
Part 3 – Final PoE (Final Asset Development)
An offline-first habit tracker built for OPSC6312 (Final PoE).
Author: Katleho Khutsoane (ST10442069).

## Part 3 Additions & Features
- **Google SSO**: Secure authentication via Android Credential Manager and Supabase ID-token exchange.
- **Offline-First Mode with Automatic Sync**: Local SQLite database storage (`habithive_local.db`) with automatic bidirectional sync when connectivity returns.
- **FCM Push Notifications**: Real-time push notification support using Firebase Cloud Messaging.
- **Multi-Language**: Full localization support for English (`en`), Afrikaans (`af`), and isiZulu (`zu`) with an in-app locale switcher (`AppCompatDelegate`).
- **Forgiving Habit Score** (Innovative Feature): Exponentially weighted rolling score over a 30-day window (half-life = 5 days) where recent days count most and a single missed day only gently dents the score.
- **Streak Achievement Badges** (Innovative Feature): Milestone badges (Bronze 3-day, Silver 7-day, Gold 30-day streaks) unlocking dynamically based on user streak performance.

## Design Considerations
- **Offline-First with Local SQLite**: Uses Android's built-in `SQLiteOpenHelper` to ensure high responsiveness and zero network dependency, avoiding Room/KSP compatibility risks with AGP 9. Client-generated UUIDs ensure conflict-free offline inserts.
- **Supabase Auth with Google ID-Token Exchange**: Leverages modern Credential Manager for frictionless single sign-on while delegating secure session management to Supabase.
- **FCM**: Enables backend and scheduled push notifications to re-engage users.

## Tech Stack
| Component | Technology |
|---|---|
| UI Toolkit | Jetpack Compose & Material 3 |
| Navigation | Jetpack Navigation Compose |
| Architecture | MVVM + Repository pattern |
| Local Storage | Android SQLite (`SQLiteOpenHelper`) |
| Remote Backend | Supabase REST (PostgREST) & Auth |
| Network | Retrofit 2 + Gson + OkHttp |
| Authentication | Google Credential Manager + Supabase Auth |
| Push Notifications | Firebase Cloud Messaging (FCM) |
| Localization | AppCompat per-app locales (`locales_config.xml`) |

## GitHub and GitHub Actions
Continuous integration via GitHub Actions (`.github/workflows/android.yml`) automatically builds the debug APK, runs unit tests (`StreaksTest`, `HabitScoreTest`), and uploads build artifacts.

## Release Notes
See [RELEASE_NOTES.md](RELEASE_NOTES.md) for version 1.0.0 details.
