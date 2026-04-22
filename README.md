# Radar

A real-time, location-based social messaging app for Android. Radar lets users discover nearby people, see who is online, and chat with them instantly — all powered by a custom REST API and Firebase Realtime Database.

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Screens](#screens)
- [Data Flow](#data-flow)
- [Build & Run](#build--run)
- [Testing](#testing)
- [Configuration](#configuration)

---

## Features

- **Google Sign-In** — one-tap authentication via the Credential Manager API
- **Location Onboarding** — captures the device's GPS coordinates on first launch to register the user
- **User Discovery** — grid view of nearby users with real-time online/offline status indicators
- **Instant Messaging** — bidirectional chat backed by Firebase Realtime Database with sent/received message bubbles
- **Session Persistence** — login state survives app restarts via DataStore / SharedPreferences
- **Skeleton Loaders & Error States** — polished loading/error/empty UI throughout
- **Mock & Prod Flavors** — swap between a local development server and a production AWS server at build time

---

## Architecture

Radar follows **Clean Architecture** with an **MVI pattern** on the presentation layer.

```
┌─────────────────────────────┐
│       UI / Presentation     │  Jetpack Compose, ViewModels, MVI State/Intent
├─────────────────────────────┤
│         Domain              │  Use Cases, Domain Models, Repository Interfaces
├─────────────────────────────┤
│          Data               │  Retrofit API, Firebase, SharedPreferences, Repositories
└─────────────────────────────┘
```

### Presentation (MVI)

ViewModels expose a single `StateFlow<State>` and accept `Intent` objects via `handleIntent()`.

```
User Action → Intent → ViewModel → Use Case → ApiResult → new State → Composable re-renders
```

`UserListViewModel` is the canonical MVI example:
- **Intent**: `UserListIntent.LoadUsers`, `UserListIntent.RetryLoad`
- **State**: `UserListState(users, isLoading, error)`
- **Side-effects** are absent by design — everything flows through state

Other ViewModels (`ChatViewModel`, `SignUpViewModel`, `LocationViewModel`) follow standard MVVM with `StateFlow`.

### Domain

Pure Kotlin — no Android dependencies. Defines:
- **Use Cases** — one public `operator fun invoke()` or `suspend operator fun invoke()` each
- **Repository interfaces** — abstractions the Data layer implements
- **Domain models** — `User`, `Chat`, `Message`, `LocationData`, `ApiResult<T>`

### Data

- **`DefaultUserRepository`** — wraps `RadarApiService` (Retrofit), checks network availability before calls
- **`ChatRepository`** — wraps Firebase Realtime Database, exposes a `Flow<List<Message>>`
- **`DefaultLocationRepository`** — wraps `FusedLocationProviderClient` via `suspendCancellableCoroutine`
- **`LoginRepository`** — reads/writes auth state to SharedPreferences

---

## Tech Stack

| Category | Library | Version |
|---|---|---|
| UI | Jetpack Compose BOM | 2025.06.01 |
| UI | Material Design 3 | via BOM |
| Navigation | Navigation Compose (typed routes) | 2.8.4 |
| DI | Hilt | 2.57.2 |
| Networking | Retrofit 2 | 2.11.0 |
| Networking | OkHttp Logging Interceptor | 4.12.0 |
| Serialization | Moshi + Kotlin adapter | 1.15.1 |
| Serialization | Kotlinx Serialization JSON | 1.6.3 |
| Image Loading | Coil 3 (Compose + OkHttp backend) | 3.3.0 |
| Async | Kotlin Coroutines + Flow | 1.8.0 |
| State | StateFlow / MutableStateFlow | — |
| Realtime DB | Firebase Realtime Database | BOM 34.7.0 |
| Auth | Google Sign-In / Credential Manager | 21.2.0 / 1.6.0-beta03 |
| Location | Fused Location Provider | 21.3.0 |
| Local Storage | DataStore Preferences | 1.0.0 |
| Permissions | Accompanist Permissions | 0.30.1 |
| Testing | JUnit 4 | 4.13.2 |
| Testing | Mockito Kotlin | 5.4.0 |
| Testing | Turbine (Flow testing) | 1.1.0 |
| Testing | Kotlinx Coroutines Test | 1.8.0 |

---

## Project Structure

```
app/src/main/java/com/dp/radar/
│
├── data/
│   ├── datasources/remote/
│   │   ├── RadarApiService.kt          # Retrofit interface — all API endpoints
│   │   └── dto/
│   │       ├── UserDto.kt              # API response DTO with toDomain() mapper
│   │       └── LatLong.kt              # GPS coordinates {lat, lon}
│   ├── repositories/
│   │   ├── DefaultUserRepository.kt    # Implements UserRepository via Retrofit
│   │   ├── DefaultLocationRepository.kt# Implements LocationRepository via FusedLocation
│   │   ├── ChatRepository.kt           # Firebase Realtime Database adapter
│   │   └── login/LoginRepository.kt    # Auth state (SharedPreferences)
│   ├── di/
│   │   ├── NetworkModule.kt            # Hilt: Retrofit, OkHttp, Moshi, base URL
│   │   └── RadarModule.kt              # Hilt: repositories, dispatcher, prefs
│   └── NetworkMonitor.kt               # Connectivity check before API calls
│
├── domain/
│   ├── model/
│   │   ├── User.kt                     # User, UserRequestDto, Chat, Message, MessageType
│   │   └── LocationData.kt             # {latitude, longitude}
│   ├── repositories/
│   │   ├── UserRepository.kt           # getUsers(), createUser()
│   │   ├── LocationRepository.kt       # getCurrentLocation()
│   │   └── ILoginRepository.kt         # saveEmail/Id, isLoggedIn, clearEmail
│   ├── login/
│   │   ├── GetIsLoggedInUseCase.kt
│   │   ├── GetUserIdUseCase.kt
│   │   ├── SaveUserIdUseCase.kt
│   │   ├── SaveEmailUseCase.kt
│   │   └── ClearEmailUseCase.kt
│   ├── GetUsersUseCase.kt              # Fetch user list → ApiResult<List<User>>
│   ├── CreateUserUseCase.kt            # Register user with location
│   ├── GetCurrentLocationUseCase.kt    # Request GPS fix
│   ├── GetUserChatUseCase.kt           # Fetch chat history
│   └── ApiResult.kt                    # sealed: Success<T>(data) | Error(message)
│
├── ui/
│   ├── MainActivity.kt                 # Root: checks auth, picks LoginFlow vs MainFlow
│   ├── RadarApplication.kt             # @HiltAndroidApp
│   ├── viewmodel/
│   │   ├── RadarViewModel.kt           # Global state: isLoggedIn, bottom/top bar visibility
│   │   ├── UserListViewModel.kt        # MVI — user discovery screen
│   │   ├── ChatViewModel.kt            # Real-time messaging state
│   │   ├── SignUpViewModel.kt          # Onboarding / user creation
│   │   └── LocationViewModel.kt        # GPS fetch
│   ├── composable/
│   │   ├── login/
│   │   │   ├── LoginScreen.kt          # Google Sign-In UI
│   │   │   └── LoginSuccessScreen.kt   # Post-signup welcome
│   │   ├── home/
│   │   │   ├── HomeScreen.kt           # 3-column user grid with online dots
│   │   │   └── ChatDetailScreen.kt     # Message bubbles + Firebase sync
│   │   ├── LocationScreen.kt           # Permission + GPS onboarding
│   │   ├── ChatScreen.kt               # User list for messaging
│   │   └── SettingsScreen.kt           # Placeholder
│   ├── components/
│   │   ├── RadarTopBar.kt
│   │   ├── BottomBar.kt
│   │   ├── SkeletonLoader.kt
│   │   └── BackHandler.kt
│   ├── navigation/
│   │   ├── RadarScreen.kt              # Typed @Serializable route definitions
│   │   ├── LoginFlow.kt                # Auth NavHost
│   │   └── MainFlow.kt                 # App NavHost
│   ├── theme/                          # Material 3 theme, colors, typography
│   ├── UserListMVI.kt                  # UserListIntent + UserListState
│   └── ChatListMVI.kt                  # Chat MVI classes
│
└── utils/
    └── NavType.kt                      # Custom nav argument serializers

app/src/test/java/com/dp/radar/
├── domain/
│   └── GetUsersUseCaseTest.kt
├── ui/viewmodel/
│   └── UserListViewModelTest.kt
└── utils/
    └── MainDispatcherRule.kt
```

---

## Screens

### Login
Google One-Tap sign-in. After authentication the user is routed to the **Location** screen on first launch, or directly to **Home** if already registered.

### Location Onboarding
Requests `ACCESS_FINE_LOCATION` permission, fetches a GPS fix via the Fused Location Provider, and calls `CreateUserUseCase` to register the user's profile on the backend.

### Home
A lazy 3-column grid of all users (excluding the current user). Each card shows an avatar, username, and a green online-indicator dot. Tapping a card opens the chat detail for that user.

### Chat Detail
Real-time messaging screen. Sent messages appear on the right in the app's primary colour; received messages appear on the left. Messages are written to and read from Firebase Realtime Database, so updates are instantaneous across devices.

### Settings
Placeholder — not yet implemented.

---

## Data Flow

### Fetch Users (Home Screen)

```
HomeScreen
  └─ collectAsState(UserListState)
       └─ UserListViewModel.handleIntent(LoadUsers)
            └─ loadUsers() [viewModelScope + IO dispatcher]
                 └─ GetUsersUseCase()
                      └─ UserRepository.getUsers()
                           └─ RadarApiService.getUsers()  [Retrofit → REST API]
                                → ApiResult.Success(users)
                 └─ _state.update { copy(users = filtered, isLoading = false) }
```

### Send / Receive Messages

```
ChatDetailScreen
  └─ collectAsState(messages)
       └─ ChatViewModel observes ChatRepository
            └─ ChatRepository listens on Firebase
                 → Realtime updates pushed to Flow<List<Message>>

User sends message
  └─ ChatViewModel.sendMessage()
       └─ ChatRepository.sendMessage()
            └─ Firebase.getReference(...).setValue(message)
```

### Authentication

```
App launch
  └─ RadarViewModel checks GetIsLoggedInUseCase()
       ├─ true  → MainFlow (Home)
       └─ false → LoginFlow

LoginScreen
  └─ Google Credential Manager → idToken / email
       └─ SaveEmailUseCase() + SaveUserIdUseCase()
            └─ Navigate to LocationScreen (first time) or Home
```

---

## Build & Run

### Prerequisites

- Android Studio Iguana / Jellyfish or later
- JDK 11
- Android SDK platform 35
- A physical device or emulator running API 24+
- `google-services.json` placed in `app/` (obtain from Firebase Console)
- For the **mock** flavor: a local server running on `http://10.0.2.2:8080/api/`

### Build Flavors

| Flavor | Base URL | Use |
|---|---|---|
| `mock` | `http://10.0.2.2:8080/api/` | Local development (Android emulator → host machine) |
| `prod` | `http://13.49.114.84:8080/api/` | Production AWS server |

### Build Commands

```bash
# Assemble APK
./gradlew :app:assembleMockDebug
./gradlew :app:assembleProdDebug
./gradlew :app:assembleProdRelease

# Install directly to a connected device / emulator
./gradlew :app:installMockDebug
./gradlew :app:installProdDebug

# Run all unit tests
./gradlew :app:testMockDebugUnitTest
```

### Required Permissions

Declared in `AndroidManifest.xml`:

- `INTERNET`, `ACCESS_NETWORK_STATE`
- `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`
- `USE_BIOMETRIC`, `USE_FINGERPRINT`

---

## Testing

Unit tests live under `app/src/test/`. The project uses:

| Tool | Purpose |
|---|---|
| JUnit 4 | Test runner and assertions |
| Mockito Kotlin | Mocking dependencies (supports final Kotlin classes via mockito-core 5.x) |
| Turbine | Asserting `StateFlow` emissions in sequence |
| Kotlinx Coroutines Test | `runTest`, `StandardTestDispatcher`, `TestCoroutineScheduler` |

### Key Test Helpers

**`MainDispatcherRule`** — a JUnit `TestWatcher` that replaces `Dispatchers.Main` with an `UnconfinedTestDispatcher` before each test and resets it after. Required for any ViewModel test that uses `viewModelScope`.

### ViewModel Tests

`UserListViewModelTest` verifies the full MVI state sequence for success and failure scenarios using Turbine:

```kotlin
@Test
fun `Successful load should emit Loading then Success state`() = runTest {
    whenever(mockGetUsersUseCase.invoke()).thenReturn(ApiResult.Success(fakeUsers))
    viewModel = UserListViewModel(
        mockGetUsersUseCase, mockGetUserIdUseCase,
        StandardTestDispatcher(testScheduler)   // shares scheduler with runTest
    )

    viewModel.state.test {
        assertEquals(false, awaitItem().isLoading)  // Initial
        assertTrue(awaitItem().isLoading)            // Loading
        val success = awaitItem()                    // Success
        assertEquals(fakeUsers, success.users)
        cancelAndIgnoreRemainingEvents()
    }
}
```

`StandardTestDispatcher(testScheduler)` is passed to the ViewModel so that `runTest`'s scheduler controls when the ViewModel's coroutines execute, giving Turbine a chance to observe each distinct `StateFlow` emission in order.

---

## Configuration

### BuildConfig Fields

| Field | Flavor | Value |
|---|---|---|
| `BASE_URL` | mock | `http://10.0.2.2:8080/api/` |
| `BASE_URL` | prod | `http://13.49.114.84:8080/api/` |

### Google Sign-In

The Web Client ID is embedded in `LoginScreen.kt`. To use a different Firebase project, replace the client ID and update `google-services.json`.

### Firebase

Realtime Database rules and `google-services.json` are managed separately per environment. The database reference path for messages follows the pattern:

```
chats/{senderId}_{receiverId}/messages/{messageId}
```

### SDK Versions

| Setting | Value |
|---|---|
| `compileSdk` | 36 |
| `targetSdk` | 35 |
| `minSdk` | 24 (Android 7.0) |
| `jvmTarget` | 11 |