# Release Notes - HabitHive v1.0.0 (Final PoE)

Compared to the Part 2 prototype, v1.0.0 introduces robust production-grade capabilities:

- **Google Single Sign-On**: Streamlined sign-in using Android Credential Manager and Supabase ID-token exchange.
- **Offline Mode with Automatic Sync**: Local SQLite persistence with background/automatic connectivity syncing and pending change counters.
- **Real-Time Push Notifications**: Firebase Cloud Messaging integration with custom notification channels.
- **Multi-Language Support**: English, Afrikaans, and isiZulu localization with live in-app language switching.
- **Forgiving Habit Score** (*Innovative Feature*): Advanced 30-day exponential decay scoring model that accommodates occasional missed days.
- **Streak Achievement Badges** (*Innovative Feature*): Visual milestone recognition for 3, 7, and 30-day streaks.
- **Localized Validation & Error Messages**: Fully translated error feedback across authentication and network states.
- **Secure Logout**: Clears session tokens and wipes local database records to protect user privacy on shared devices.
- **Application ID & Signing**: Production package ID updated to `com.katleho.habithive` with release signing configuration.
