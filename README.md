# ADE App

ADE App is a native Android application for viewing ADE timetables and creating alarms based on class start times.

The app lets a user sign in with an ADE URL, fetch their calendar from the ADE web API, browse timetable resources, view schedules, and manage alarms that can ring before classes.

## Features

- ADE timetable import from a shared ADE URL.
- Day and week timetable views.
- Resource browsing for students, teachers, classrooms, equipment, and lessons.
- Configurable class alarms with labels, repeat count, interval, and default lead time.
- Full-screen alarm activity with snooze support.
- Local settings persistence with Android DataStore.
- English and French string resources.

## Tech Stack

- Kotlin
- Android Gradle Plugin
- Jetpack Compose
- Material 3
- Navigation Compose
- Android DataStore
- kotlinx.serialization
- XML parsing for ADE API responses

## Requirements

- Android Studio with Android SDK 35 installed.
- JDK 17 for the Gradle/Android plugin toolchain.
- A device or emulator running Android 7.0/API 24 or newer.
- Git tags for versioning. The build reads the latest tag with `git describe --tags --abbrev=0`.

Use semantic version tags such as:

```sh
git tag v1.0.0
```

Without at least one tag, Gradle configuration fails because `versionName` and `versionCode` are generated from the latest Git tag.

## Getting Started

Clone the repository and open it in Android Studio, or build it from the command line:

```sh
./gradlew assembleDebug
```

Install the debug APK on a connected device:

```sh
./gradlew installDebug
```

Run unit tests:

```sh
./gradlew test
```

Run Android instrumentation tests:

```sh
./gradlew connectedAndroidTest
```

## ADE Login URL

The login screen expects an ADE URL that already contains the required query parameters:

- `data`
- `projectId`
- `resources`

The URL path must start with one of:

- `/direct/`
- `/jsp/`
- `/ade/`

Only a single resource ID is currently supported. URLs with multiple `resources` values are rejected.

Example shape:

```text
https://ade.example.edu/direct/index.jsp?data=TOKEN&projectId=1&resources=12345
```

The app extracts the base URL, project ID, resource ID, and data token, then uses ADE web API calls under `/jsp/webapi`.

## Project Structure

```text
app/src/main/java/com/chtibizoux/adeapp/
├── alarms/        Alarm scheduling, receivers, service, and full-screen alarm UI
├── data/          Settings models, repository, ADE HTTP access, and XML parsers
├── ui/            Compose UI, navigation, login, settings, home, and timetable screens
└── MainActivity.kt
```

Important files:

- `app/build.gradle.kts`: Android app configuration, dependencies, and tag-based version generation.
- `gradle/libs.versions.toml`: Central dependency and plugin versions.
- `app/src/main/AndroidManifest.xml`: Android permissions, activities, service, and alarm receiver declarations.

## Permissions

The app requests permissions needed for network access, notifications, exact alarms, wake locks, vibration, full-screen alarm display, foreground alarm service, and boot handling.

On recent Android versions, users may need to grant notification and exact alarm permissions for alarms to behave as expected.

## Release Builds

Create a release APK with:

```sh
./gradlew assembleRelease
```

Release minification is currently disabled in `app/build.gradle.kts`.
