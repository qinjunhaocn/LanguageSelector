# Language Selector

A per-app language selector for Android 13+ devices, built with Jetpack Compose and [Miuix UI library](https://github.com/compose-miuix-ui/miuix).

## Features

- Set per-app language for any installed app (Android 13+)
- Search and browse installed apps
- View current language for each app
- Force stop apps to apply language changes immediately
- Pin frequently used apps
- Beautiful MIUI/HyperOS-style UI powered by Miuix

## How It Works

This app uses [Shizuku](https://github.com/RikkaApps/Shizuku) to run a privileged service that can set per-app language for any installed application. It replicates the behavior of the "App Languages" feature introduced in Android 13.

The implementation method is inspired by [VegaBobo/Language-Selector](https://github.com/VegaBobo/Language-Selector), but uses the `cmd locale` shell interface instead of direct Binder calls to the system LocaleManager.

## Requirements

- Android 13+ (API 33+) for per-app language functionality
- [Shizuku](https://github.com/RikkaApps/Shizuku) running and authorized
- Android 8.0+ (API 26+) minimum

## Tech Stack

- **Kotlin** 2.4.10
- **Jetpack Compose** (Compose BOM 2026.04.01)
- **Miuix UI** 0.9.3 (HyperOS-style components)
- **Shizuku API** 13.0.0
- **AGP** 8.7.3
- **Gradle** 8.11.1

## Building

The project includes a GitHub Actions workflow (`.github/workflows/build.yml`) that builds debug and release APKs automatically on push.

To build locally:

```bash
./gradlew assembleDebug
```

## Project Structure

```
LanguageSelector/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── aidl/com/xyz/langselector/service/IUserService.aidl
│       ├── java/com/xyz/langselector/
│       │   ├── App.kt
│       │   ├── MainActivity.kt
│       │   ├── data/
│       │   │   ├── model/
│       │   │   │   ├── AppInfo.kt
│       │   │   │   └── LanguageInfo.kt
│       │   │   └── PrefsManager.kt
│       │   ├── service/
│       │   │   ├── UserService.kt
│       │   │   └── ShizukuManager.kt
│       │   ├── util/
│       │   │   ├── LocaleUtils.kt
│       │   │   └── AppUtils.kt
│       │   ├── ui/
│       │   │   ├── theme/Theme.kt
│       │   │   ├── components/
│       │   │   │   ├── AppListItem.kt
│       │   │   │   └── LanguagePickerSheet.kt
│       │   │   ├── MainScreen.kt
│       │   │   ├── AppsScreen.kt
│       │   │   └── SettingsScreen.kt
│       │   └── viewmodel/MainViewModel.kt
│       └── res/
│           ├── values/ (strings, themes, colors)
│           ├── xml/ (locales_config, backup rules)
│           ├── drawable/ (launcher icon)
│           └── mipmap-anydpi-v26/ (adaptive icon)
├── gradle/
│   └── libs.versions.toml
├── .github/workflows/build.yml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## License

This project is for educational purposes.
