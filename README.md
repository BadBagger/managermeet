# ManagerMeet

ManagerMeet is a local-first Android product planning app for Smithware Studios.

Tagline: Turn app ideas into build-ready plans.

## What is included

- Kotlin + Jetpack Compose + Material 3 Android app
- Room database for local project and settings records
- DataStore preferences for dark mode and compact dashboard cards
- Dashboard, Add/Edit, Detail, Settings, and Export screens
- Demo data, empty states, input validation, edit, delete, and archive support
- Codex-ready prompt generation
- Light and dark mode
- No login, no cloud sync, no paid APIs, and no network permission

## Privacy stance

Your app ideas stay on this device. ManagerMeet v1 does not upload ideas, prompts, app plans, or project data.

## Build

On this Windows machine:

```powershell
$env:JAVA_HOME='C:\Users\KyleB\Documents\Codex\2026-07-04\build-a-native-android-app-using\.local-jdk\jdk-17.0.19+10'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
.\gradlew.bat :app:assembleRelease
```

The release APK is generated at:

```text
app\build\outputs\apk\release\app-release.apk
```
