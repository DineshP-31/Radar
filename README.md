# Radar

A real-time, location-based social messaging app for Android. Radar lets users discover nearby people, see who is online, and chat with them instantly — all powered by a custom REST API and Firebase Realtime Database.

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Screens](#screens)
- [How to Use](#how-to-use)
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
- **Session Persistence** — login state survives app restarts via Jetpack DataStore Preferences, exposed as a reactive `Flow<Boolean>`
- **Skeleton Loaders & Error States** — polished loading/error/empty UI throughout
- **Build Flavors** — swap between a REST API backend (`api`) and Firebase Realtime Database (`firebase`) at build time with zero code changes

---

## Architecture

Radar follows **Clean Architecture** with an **MVI pattern** on the presentation layer.

```
┌─────────────────────────────┐
│       UI / Presentation     │  Jetpack Compose, ViewModels, MVI State/Intent
├─────────────────────────────┤
│         Domain              │  Use Cases, Domain Models, Repository Interfaces
├─────────────────────────────┤
│          Data               │  Retrofit API, Firebase, DataStore Preferences, Repositories
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
- **Use Cases** — read use cases return `Flow<T>` directly from the repository; write use cases are `suspend operator fun invoke()`
- **Repository interfaces** — abstractions the Data layer implements
- **Domain models** — `User`, `Chat`, `Message`, `LocationData`, `ApiResult<T>`

### Data

- **`DefaultUserRepository`** *(api flavor)* — wraps `RadarApiService` (Retrofit), checks network availability before calls
- **`FirebaseUserRepository`** *(firebase flavor)* — reads/writes users directly to Firebase Realtime Database under `/users/{id}` using `suspendCancellableCoroutine`
- **`ChatRepository`** — wraps Firebase Realtime Database, exposes a `Flow<List<Message>>`
- **`DefaultLocationRepository`** — wraps `FusedLocationProviderClient` via `suspendCancellableCoroutine`
- **`LoginRepository`** — reads/writes auth state to `DataStore<Preferences>`; reads are exposed as `Flow<Boolean>` / `Flow<Long>` so all consumers react automatically to state changes

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
app/src/
│
├── main/java/com/dp/radar/             # Shared across all flavors
│   ├── data/
│   │   ├── datasources/remote/
│   │   │   ├── RadarApiService.kt          # Retrofit interface — all API endpoints
│   │   │   └── dto/
│   │   │       ├── UserDto.kt              # API response DTO with toDomain() mapper
│   │   │       └── LatLong.kt              # GPS coordinates {lat, lon}
│   │   ├── repositories/
│   │   │   ├── DefaultUserRepository.kt    # UserRepository via Retrofit (api flavor)
│   │   │   ├── DefaultLocationRepository.kt# LocationRepository via FusedLocation
│   │   │   ├── ChatRepository.kt           # Firebase Realtime Database adapter
│   │   │   └── login/LoginRepository.kt    # Auth state (DataStore<Preferences>)
│   │   ├── di/
│   │   │   ├── NetworkModule.kt            # Hilt: Retrofit, OkHttp, Moshi, base URL
│   │   │   └── RadarModule.kt              # Hilt: shared bindings — DataStore, dispatcher, location
│   │   └── NetworkMonitor.kt               # Connectivity check before API calls
│
├── api/java/com/dp/radar/              # api flavor only
│   └── data/di/
│       └── UserRepositoryModule.kt         # Hilt: binds DefaultUserRepository
│
└── firebase/java/com/dp/radar/         # firebase flavor only
    └── data/
        ├── repositories/
        │   └── FirebaseUserRepository.kt   # UserRepository via Firebase Realtime Database
        └── di/
            └── UserRepositoryModule.kt     # Hilt: provides FirebaseDatabase + FirebaseUserRepository
│
├── domain/
│   ├── model/
│   │   ├── User.kt                     # User, UserRequestDto, Chat, Message, MessageType
│   │   └── LocationData.kt             # {latitude, longitude}
│   ├── repositories/
│   │   ├── UserRepository.kt           # getUsers(), createUser()
│   │   ├── LocationRepository.kt       # getCurrentLocation()
│   │   └── ILoginRepository.kt         # Flow<Boolean> isLoggedIn, Flow<Long> userId, suspend writes
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
├── data/repositories/login/
│   └── LoginRepositoryTest.kt          # DataStore-backed real instance + Turbine
├── domain/
│   ├── GetUsersUseCaseTest.kt
│   └── login/
│       ├── GetIsLoggedInUseCaseTest.kt
│       ├── SaveEmailUseCaseTest.kt
│       └── ClearEmailUseCaseTest.kt
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

## How to Use

> **Recommended for testing:** use the `firebaseDebug` build variant. It requires no local server — user profiles and messages are stored directly in Firebase Realtime Database. Switch to it in Android Studio via **View → Tool Windows → Build Variants → `firebaseDebug`**, then run `./gradlew :app:installFirebaseDebug`.

### End-to-End: Real-Time Chat Between Two Devices

> Both devices must have an internet connection. Install the **`firebaseDebug`** variant on each.

**Set up Device A (Alice)**

1. Install and open the app.
2. Tap **Sign in with Google** and choose a Gmail account (e.g. `alice@gmail.com`).
3. Grant the location permission when prompted — the app captures GPS coordinates to register your profile.
4. Your profile is created in Firebase under `/users`. You land on the **Home** screen.

**Set up Device B (Bob)**

1. Install and open the app on a second device (or emulator).
2. Tap **Sign in with Google** and choose a **different** Gmail account (e.g. `bob@gmail.com`).
3. Grant the location permission.
4. Bob's profile is created in Firebase. The **Home** screen loads and shows Alice's card.

**Start chatting**

1. On Device B, tap Alice's card — the **Chat Detail** screen opens.
2. Type a message and send it — it appears on the right (sent bubble).
3. On Device A, tap Bob's card — the **Chat Detail** screen opens. Bob's message appears on the left in real time, with no refresh needed.
4. Reply from Device A — the message appears instantly on Device B.

> Messages are stored in Firebase Realtime Database at `chats/{senderId}_{receiverId}/messages/{messageId}` and pushed to both devices via a persistent `ValueEventListener`.

---

## Data Flow

### Fetch Users (Home Screen)

The data source is determined by the build flavor — the `UserRepository` interface is the same in both cases.

**`api` flavor:**
```
HomeScreen
  └─ collectAsState(UserListState)
       └─ UserListViewModel.handleIntent(LoadUsers)
            └─ loadUsers() [viewModelScope + IO dispatcher]
                 └─ GetUsersUseCase()
                      └─ DefaultUserRepository.getUsers()
                           └─ RadarApiService.getUsers()  [Retrofit → REST API]
                                → ApiResult.Success(users)
                 └─ _state.update { copy(users = filtered, isLoading = false) }
```

**`firebase` flavor:**
```
HomeScreen
  └─ collectAsState(UserListState)
       └─ UserListViewModel.handleIntent(LoadUsers)
            └─ loadUsers() [viewModelScope + IO dispatcher]
                 └─ GetUsersUseCase()
                      └─ FirebaseUserRepository.getUsers()
                           └─ FirebaseDatabase.getReference("users")
                                → ValueEventListener.onDataChange → ApiResult.Success(users)
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
  └─ RadarViewModel.isLoggedIn: StateFlow<Boolean>
       └─ GetIsLoggedInUseCase() → ILoginRepository.isLoggedIn: Flow<Boolean>
            └─ DataStore<Preferences>.data.map { prefs[KEY_EMAIL] != null }
                 ├─ emits true  → MainActivity shows MainFlow (Home)
                 └─ emits false → MainActivity shows LoginFlow

Login / Onboarding
  └─ Google Credential Manager → idToken / email
       └─ viewModelScope.launch { SaveEmailUseCase(email) }   // suspend → DataStore.edit
            └─ DataStore emits updated prefs
                 └─ isLoggedIn Flow emits true → UI auto-navigates to MainFlow

Logout
  └─ viewModelScope.launch { ClearEmailUseCase() }            // suspend → DataStore.edit
       └─ DataStore emits updated prefs
            └─ isLoggedIn Flow emits false → UI shows LoginFlow
```

---

## Build & Run

### Prerequisites

- Android Studio Iguana / Jellyfish or later
- JDK 11
- Android SDK platform 35
- A physical device or emulator running API 24+
- `google-services.json` placed in `app/` (obtain from Firebase Console)
- For the **api** flavor: a running backend server (local or AWS)

### Build Flavors

The app has two flavor dimensions: **backend** (data source) × **build type** (debug/release).

| Flavor | Backend | User Data Source |
|---|---|---|
| `api` | REST API via Retrofit | `DefaultUserRepository` → `RadarApiService` |
| `firebase` | Firebase Realtime Database | `FirebaseUserRepository` → `/users/{id}` |

The `BASE_URL` (set per build type) is used by the `api` flavor. The `firebase` flavor ignores it for user operations.

| Build Type | `BASE_URL` |
|---|---|
| `debug` | `http://10.0.2.2:8080/api/` (emulator → localhost) |
| `release` | `http://13.49.114.84:8080/api/` (AWS) |

### Build Commands

```bash
# Assemble APK
./gradlew :app:assembleApiDebug
./gradlew :app:assembleApiRelease
./gradlew :app:assembleFirebaseDebug
./gradlew :app:assembleFirebaseRelease

# Install directly to a connected device / emulator
./gradlew :app:installApiDebug
./gradlew :app:installFirebaseDebug

# Run unit tests for a variant
./gradlew :app:testApiDebugUnitTest
./gradlew :app:testFirebaseDebugUnitTest
```

To switch variants in Android Studio: **View → Tool Windows → Build Variants**, then select `firebaseDebug` or `apiDebug`.

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
| Turbine | Asserting `Flow` / `StateFlow` emissions in sequence |
| Kotlinx Coroutines Test | `runTest`, `StandardTestDispatcher`, `TestCoroutineScheduler` |
| DataStore (real instance) | `LoginRepositoryTest` uses a real `DataStore` backed by a temp file — no mocking needed |

### Key Test Helpers

**`MainDispatcherRule`** — a JUnit `TestWatcher` that replaces `Dispatchers.Main` with an `UnconfinedTestDispatcher` before each test and resets it after. Required for any ViewModel test that uses `viewModelScope`.

### Repository Tests

`LoginRepositoryTest` exercises the DataStore-backed repository with a real `PreferenceDataStoreFactory` instance pointing at a temp file, and asserts `Flow` emissions with Turbine:

```kotlin
@Test
fun `saveEmail causes isLoggedIn to emit true`() = runTest(testDispatcher) {
    repository.saveEmail("test@example.com")
    repository.isLoggedIn.test {
        assertEquals(true, awaitItem())
        cancelAndIgnoreRemainingEvents()
    }
}
```

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

| Field | Build Type | Value |
|---|---|---|
| `BASE_URL` | `debug` | `http://10.0.2.2:8080/api/` |
| `BASE_URL` | `release` | `http://13.49.114.84:8080/api/` |

`BASE_URL` is consumed by `NetworkModule` for Retrofit. The `firebase` flavor still compiles with this field but does not use it for user create/list operations.

### Google Sign-In

The Web Client ID is embedded in `LoginScreen.kt`. To use a different Firebase project, replace the client ID and update `google-services.json`.

### Firebase

`google-services.json` must be placed in `app/`. Realtime Database rules are managed in the Firebase Console.

**Database structure:**

```
/users/{timestampId}
    id          : Long
    username    : String
    email       : String
    avatarUrl   : String
    isOnline    : Boolean
    lat         : Double
    lon         : Double

/chats/{senderId}_{receiverId}/messages/{messageId}
    messageId   : String
    message     : String
    timestamp   : Long
    messageType : String  ("SENT" | "RECEIVED")
    senderId    : Long
    receiverId  : Long
```

**Security rules (development — open access):**

```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

**Security rules (production — authenticated access):**

```json
{
  "rules": {
    "users": {
      ".read": "auth != null",
      ".write": "auth != null"
    },
    "chats": {
      ".read": "auth != null",
      ".write": "auth != null"
    }
  }
}
```

### SDK Versions

| Setting | Value |
|---|---|
| `compileSdk` | 36 |
| `targetSdk` | 35 |
| `minSdk` | 24 (Android 7.0) |
| `jvmTarget` | 11 |