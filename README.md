# HabitHive 🐝

An offline-first–inspired **habit tracker** for Android, built for OPSC6312 (Part 2 – App Prototype Development).

**Author:** Katleho Khutsoane (ST10442069)
**Demo video:**https://youtube.com/shorts/34wqVSIe5VQ?feature=share

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
<img width="940" height="475" alt="image" src="https://github.com/user-attachments/assets/637f0b5f-3e08-431a-90cc-4c42c3bb011e" />


## 🚀 Running the project

1. Clone the repo and open it in Android Studio.
2. Create a Supabase project and run the SQL to create the `habits` and `habit_logs`
   tables with Row Level Security.
3. Add your keys to `local.properties`:SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-publishable-key

4. Run the app on a device or emulator (min SDK 26).

## 📸 Screenshots
<img width="275" height="618" alt="image" src="https://github.com/user-attachments/assets/f3a824fd-1ff4-4666-8b8d-9ab645b9d011" />
<img width="277" height="627" alt="image" src="https://github.com/user-attachments/assets/7422f34d-ba86-4496-99a4-81c88bfaf98e" />
<img width="280" height="632" alt="image" src="https://github.com/user-attachments/assets/afac94a4-6404-45d5-8ebd-f8c4d8d84e02" />
In Afrikaans screenshot view
<img width="310" height="693" alt="image" src="https://github.com/user-attachments/assets/f28d2d76-ec3b-4f9a-930d-05dd51dc3d15" />
<img width="308" height="690" alt="image" src="https://github.com/user-attachments/assets/475e5212-0db5-44da-956f-4f8b92b345b5" />
<img width="308" height="692" alt="image" src="https://github.com/user-attachments/assets/4d750c0e-6ba4-431f-8984-24b48a4595f5" />








**Demo video:** ▶ Watch the HabitHive demo:https://youtube.com/shorts/34wqVSIe5VQ?feature=share
