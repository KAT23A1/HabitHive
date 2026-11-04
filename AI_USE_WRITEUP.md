# AI Use Write-Up

## Overview of AI Tool Usage
Throughout the development of HabitHive (Part 3), artificial intelligence tools were utilized across distinct phases of the software engineering lifecycle to enhance productivity and architecture:
- **Claude (Anthropic):** Utilized for architectural planning, designing the offline-first SQLite synchronization engine, developing the exponential decay forgiving habit score algorithm, and generating Compose UI components.
- **Gemini (Google):** Utilized for environment setup guidance, Gradle configuration troubleshooting (AGP 9 and Credential Manager integration), and Firebase Cloud Messaging setup.
- **Autonomous Coding Agents:** Utilized for batch execution of refactoring tasks, string extraction for localization (English, Afrikaans, isiZulu), and unit test suite expansion.

## Architectural Contributions & Refinements
AI-generated suggestions were rigorously evaluated against project constraints. For example, standard Room database architecture was evaluated and replaced with a lightweight `SQLiteOpenHelper` implementation to guarantee strict AGP 9 compatibility without KSP dependency conflicts. All generated localization strings were verified for contextual accuracy in Afrikaans and isiZulu.

## References
- Anthropic. (2026). *Claude: Advanced AI Assistant for Software Engineering and Architectural Design*. San Francisco, CA: Anthropic.
- Google. (2026). *Gemini: Developer Documentation and Android Studio Integration Guidelines*. Mountain View, CA: Google LLC.
- Open Source contributors. (2025). *Android Jetpack Compose & Material 3 Best Practices*.
