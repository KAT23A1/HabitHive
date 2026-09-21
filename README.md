# HabitHive 🐝

An offline-first–inspired **habit tracker** for Android, built for OPSC6312 (Part 2 – App Prototype Development).

**Author:** Katleho Khutsoane (ST10442069)
**Demo video:** <paste your unlisted YouTube link here>

---

## 📱 About

HabitHive helps users build and maintain daily habits by making logging effortless
and progress visible. Users register an account, create habits, tick them off each day,
build streaks, and track their consistency with statistics — all backed by a live
cloud database.

## ✨ Features

- **Secure sign-in** — register and log in with email and password. Passwords are
  hashed by the authentication service, never stored in plain text.
- **Habit management** — create, edit and delete habits (full CRUD).
- **Daily logging & streaks** — tick a habit done for the day and watch its streak grow;
  a "habit score" rewards consistency.
- **Statistics** — habit score, best streak, and a weekly completion chart.
- **Settings** — dark/light theme toggle (remembered between sessions), account info,
  and logout.
- **Robust UI** — validates input, confirms deletes, and handles no-connection
  gracefully instead of crashing.

## 🛠️ Tech stack

| Layer | Choice |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Architecture | MVVM (ViewModel + Repository) |
| Networking | Retrofit + OkHttp + Gson (external libraries) |
| Backend | Supabase — hosted PostgreSQL, auto-generated REST API, and Auth |
| Async | Kotlin Coroutines |
| Testing | JUnit + GitHub Actions CI |

## 🧭 Design considerations

- **Why Supabase:** it provides a hosted database, an automatic REST API, and an
  authentication service in one place — so the app talks to a real, online REST backend
  without me having to host a separate server.
- **REST integration:** the app uses Retrofit to call Supabase's REST endpoints
  (`/rest/v1/habits`, `/rest/v1/habit_logs`) with standard GET, POST, PATCH and DELETE
  verbs, and Supabase's Auth endpoints for sign-up and login.
- **Security:** Row Level Security is enabled on every table, so each user can only
  read and write their own data. API keys are kept out of the repository via
  `local.properties` and exposed to the app through `BuildConfig`.
- **Separation of concerns:** UI (Compose) → ViewModel (state + logic) → Repository/
  network layer, which keeps screens simple and the logic testable.
- **Testability:** the streak calculation is a pure function, so it is unit-tested
  independently of the Android framework.

## 🔗 How the REST API works

The app authenticates with Supabase Auth, receives a token, and attaches it to every
request. Data operations then hit the REST API — for example, `POST /rest/v1/habits`
creates a habit, and `GET /rest/v1/habit_logs` fetches completions. All requests and
responses are JSON.

## ✅ Testing & GitHub Actions

Unit tests for the streak logic live in `app/src/test/`. A GitHub Actions workflow
(`.github/workflows/android.yml`) runs on every push: it installs JDK 17, runs the
unit tests (`./gradlew testDebugUnitTest`) and builds the app (`./gradlew assembleDebug`).
The passing runs are visible under the repository's **Actions** tab.

## 🚀 Running the project

1. Clone the repo and open it in Android Studio.
2. Create a Supabase project and run the SQL to create the `habits` and `habit_logs`
   tables with Row Level Security.
3. Add your keys to `local.properties`:SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-publishable-key

4. Run the app on a device or emulator (min SDK 26).

## 📸 Screenshots
<img width="720" height="1600" alt="WhatsApp Image 2026-09-21 at 16 46 58 (1)" src="https://github.com/user-attachments/assets/43e0fa58-72c2-4ced-a6dc-b3b518cf058c" />

**Demo video:** ▶ Watch the HabitHive demo:https://youtu.be/GbiZSelt0oE
