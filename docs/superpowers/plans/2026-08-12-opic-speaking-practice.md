# OPIc 스피킹 연습 앱 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build an Android app that generates OPIc-style speaking practice sets (English-word-order Korean hint → user records their English attempt → compare against an AI-generated AL-level model sentence spoken via TTS), per the approved design at `docs/superpowers/specs/2026-08-12-opic-speaking-practice-design.md`.

**Architecture:** MVVM + Jetpack Compose (Material3) on top of the existing default Android Studio template (package `com.example.myapplication`). A `PracticeRepository` calls the Anthropic Messages API (via OkHttp) to generate each set and a pure-Kotlin parser turns the JSON response into domain models. Favorites persist in Room; the API key persists in `EncryptedSharedPreferences`. Audio uses Android's built-in `TextToSpeech` (model sentence) and `MediaRecorder`/`MediaPlayer` (user recording). No DI framework — a small hand-written `AppContainer` wires real dependencies in `MainActivity`.

**Tech Stack:** Kotlin 2.2.10, AGP 9.3.1, Jetpack Compose (BOM 2026.02.01) + Material3, Room 2.8.4 (KSP), Navigation Compose 2.9.7, Lifecycle ViewModel Compose 2.10.0, kotlinx-coroutines 1.11.0, kotlinx-serialization-json 1.11.0, OkHttp 5.4.0 (+ mockwebserver3 for tests), androidx.security:security-crypto 1.1.0.

## Global Constraints

- minSdk 24, targetSdk 36, compileSdk 36, namespace `com.example.myapplication` — unchanged from the existing template.
- No new DI framework (no Hilt/Koin) — dependencies are wired by hand via `AppContainer`.
- LLM calls go to `https://api.anthropic.com/v1/messages` using model id `claude-sonnet-5`; the user supplies their own API key in Settings, stored only in `EncryptedSharedPreferences`, never bundled or hardcoded.
- Room persists **only** favorited sentences (`FavoriteSentence`). Generated sets are never written to disk; they live only in `PracticeSetViewModel` memory for the current session.
- Every dependency version is pinned exactly as declared in Task 1 — do not introduce other networking/serialization/DI libraries.
- Pure-Kotlin logic (prompt building, JSON parsing, repository orchestration, ViewModels) is unit-tested under `src/test` using JUnit4 + `kotlinx-coroutines-test`. Anything that touches Room, `EncryptedSharedPreferences`, `MediaRecorder`/`TextToSpeech`, or Compose UI is tested under `src/androidTest`.
- Korean hints must preserve literal English word order (not natural Korean phrasing) — this is enforced by the `PromptBuilder` prompt and is the core pedagogical mechanic of the app.

---

### Task 1: Project dependencies, permissions, and app name

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `build.gradle.kts`
- Modify: `app/build.gradle.kts`
- Modify: `app/src/main/AndroidManifest.xml`
- Modify: `app/src/main/res/values/strings.xml`

**Interfaces:**
- Produces: every Gradle version-catalog alias referenced by later tasks (`libs.androidx.room.*`, `libs.androidx.navigation.compose`, `libs.androidx.lifecycle.viewmodel.compose`, `libs.androidx.security.crypto`, `libs.androidx.test.core`, `libs.androidx.test.rules`, `libs.kotlinx.coroutines.android`, `libs.kotlinx.coroutines.test`, `libs.kotlinx.serialization.json`, `libs.okhttp`, `libs.okhttp.mockwebserver3`, `libs.plugins.ksp`, `libs.plugins.kotlin.serialization`), plus `INTERNET`/`RECORD_AUDIO` manifest permissions.

This task has no unit test of its own — its "test" is a successful Gradle build. There is nothing to implement yet that consumes the new libraries, so we verify by compiling.

- [ ] **Step 1: Replace `gradle/libs.versions.toml`**

```toml
[versions]
agp = "9.3.1"
coreKtx = "1.10.1"
junit = "4.13.2"
junitVersion = "1.1.5"
espressoCore = "3.5.1"
lifecycleRuntimeKtx = "2.6.1"
activityCompose = "1.8.0"
kotlin = "2.2.10"
composeBom = "2026.02.01"
ksp = "2.3.6"
room = "2.8.4"
navigationCompose = "2.9.7"
lifecycleViewmodelCompose = "2.10.0"
kotlinxCoroutines = "1.11.0"
kotlinxSerializationJson = "1.11.0"
okhttp = "5.4.0"
securityCrypto = "1.1.0"
testCore = "1.6.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleViewmodelCompose" }
androidx-security-crypto = { group = "androidx.security", name = "security-crypto", version.ref = "securityCrypto" }
androidx-test-core = { group = "androidx.test", name = "core", version.ref = "testCore" }
androidx-test-rules = { group = "androidx.test", name = "rules", version.ref = "testCore" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "kotlinxCoroutines" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "kotlinxCoroutines" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerializationJson" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
okhttp-mockwebserver3 = { group = "com.squareup.okhttp3", name = "mockwebserver3", version.ref = "okhttp" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

- [ ] **Step 2: Replace root `build.gradle.kts`**

```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}
```

- [ ] **Step 3: Replace `app/build.gradle.kts`**

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.myapplication"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.security.crypto)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.okhttp.mockwebserver3)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
```

- [ ] **Step 4: Add permissions to `app/src/main/AndroidManifest.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.MyApplication">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.MyApplication"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

- [ ] **Step 5: Update the app name**

`app/src/main/res/values/strings.xml`:

```xml
<resources>
    <string name="app_name">오픽 스피킹 연습</string>
</resources>
```

- [ ] **Step 6: Verify the project builds with the new dependency graph**

Run: `./gradlew :app:assembleDebug`
Expected: `BUILD SUCCESSFUL` (KSP runs with no Room entities yet, so it's a no-op; all new libraries resolve).

- [ ] **Step 7: Commit**

```bash
git add gradle/libs.versions.toml build.gradle.kts app/build.gradle.kts app/src/main/AndroidManifest.xml app/src/main/res/values/strings.xml
git commit -m "Add dependencies for OPIc speaking practice app"
```

---

### Task 2: Domain models — `PracticeCategory`, `PracticeQuestion`

**Files:**
- Create: `app/src/main/java/com/example/myapplication/data/model/PracticeCategory.kt`
- Create: `app/src/main/java/com/example/myapplication/data/model/PracticeQuestion.kt`
- Test: `app/src/test/java/com/example/myapplication/data/model/PracticeCategoryTest.kt`

**Interfaces:**
- Produces: `enum class PracticeCategory(val koreanLabel: String, val promptTopic: String)` with 8 entries; `data class PracticeQuestion(val koreanHint: String, val englishSentence: String, val category: PracticeCategory)`. Both are consumed by every later task.

- [ ] **Step 1: Write the failing test**

`app/src/test/java/com/example/myapplication/data/model/PracticeCategoryTest.kt`:

```kotlin
package com.example.myapplication.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PracticeCategoryTest {

    @Test
    fun `has eight categories covering the al-level topic list`() {
        assertEquals(8, PracticeCategory.entries.size)
    }

    @Test
    fun `every category has non-blank labels`() {
        PracticeCategory.entries.forEach { category ->
            assertFalse(category.koreanLabel.isBlank())
            assertFalse(category.promptTopic.isBlank())
        }
    }

    @Test
    fun `includes the roleplay and comparison categories required for AL level`() {
        assertTrue(PracticeCategory.entries.contains(PracticeCategory.SURVEY_ROLEPLAY))
        assertTrue(PracticeCategory.entries.contains(PracticeCategory.COMPARISON))
        assertTrue(PracticeCategory.entries.contains(PracticeCategory.PROBLEM_SOLVING_ROLEPLAY))
    }

    @Test
    fun `practice questions with identical fields are equal`() {
        val a = PracticeQuestion("힌트", "sentence", PracticeCategory.HOBBY)
        val b = PracticeQuestion("힌트", "sentence", PracticeCategory.HOBBY)

        assertEquals(a, b)
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "com.example.myapplication.data.model.PracticeCategoryTest"`
Expected: FAIL (compilation error — `PracticeCategory` and `PracticeQuestion` are unresolved references).

- [ ] **Step 3: Implement `PracticeCategory`**

`app/src/main/java/com/example/myapplication/data/model/PracticeCategory.kt`:

```kotlin
package com.example.myapplication.data.model

enum class PracticeCategory(
    val koreanLabel: String,
    val promptTopic: String
) {
    SELF_INTRODUCTION("자기소개", "a self-introduction"),
    HOUSING("거주지/집", "where the speaker lives"),
    WORK_OR_SCHOOL("직장/학교", "the speaker's job or school"),
    HOBBY("취미/여가활동", "a hobby or leisure activity"),
    PAST_EXPERIENCE("과거 경험", "a memorable past experience"),
    SURVEY_ROLEPLAY("서베이/돌발상황 롤플레이", "an unexpected situation role-play"),
    COMPARISON("비교·대비", "comparing something from the past against now"),
    PROBLEM_SOLVING_ROLEPLAY("문제해결 롤플레이", "resolving a problem in a phone-call role-play")
}
```

- [ ] **Step 4: Implement `PracticeQuestion`**

`app/src/main/java/com/example/myapplication/data/model/PracticeQuestion.kt`:

```kotlin
package com.example.myapplication.data.model

data class PracticeQuestion(
    val koreanHint: String,
    val englishSentence: String,
    val category: PracticeCategory
)
```

- [ ] **Step 5: Run the test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.example.myapplication.data.model.PracticeCategoryTest"`
Expected: PASS (4 tests)

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/example/myapplication/data/model app/src/test/java/com/example/myapplication/data/model
git commit -m "Add PracticeCategory and PracticeQuestion domain models"
```

---

### Task 3: `PromptBuilder`

**Files:**
- Create: `app/src/main/java/com/example/myapplication/data/remote/PromptBuilder.kt`
- Test: `app/src/test/java/com/example/myapplication/data/remote/PromptBuilderTest.kt`

**Interfaces:**
- Consumes: `PracticeCategory` (Task 2).
- Produces: `fun buildSetPrompt(category: PracticeCategory, alreadyAskedQuestions: List<String>, questionCount: Int = 5): String`, consumed by `PracticeRepository` (Task 6).

- [ ] **Step 1: Write the failing test**

`app/src/test/java/com/example/myapplication/data/remote/PromptBuilderTest.kt`:

```kotlin
package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptBuilderTest {

    @Test
    fun `includes category topic and requested count`() {
        val prompt = buildSetPrompt(PracticeCategory.HOBBY, emptyList(), questionCount = 5)

        assertTrue(prompt.contains(PracticeCategory.HOBBY.promptTopic))
        assertTrue(prompt.contains("5"))
        assertTrue(prompt.contains("OPIc AL"))
    }

    @Test
    fun `includes each already asked question to avoid repeats`() {
        val asked = listOf("What do you do on weekends?", "Describe your neighborhood.")

        val prompt = buildSetPrompt(PracticeCategory.HOUSING, asked)

        asked.forEach { question -> assertTrue(prompt.contains(question)) }
    }

    @Test
    fun `handles empty history without listing any question`() {
        val prompt = buildSetPrompt(PracticeCategory.SELF_INTRODUCTION, emptyList())

        assertTrue(prompt.contains("None yet."))
    }

    @Test
    fun `requires literal english-ordered korean hint format`() {
        val prompt = buildSetPrompt(PracticeCategory.COMPARISON, emptyList())

        assertTrue(prompt.contains("word order"))
        assertTrue(prompt.contains("korean_hint"))
        assertTrue(prompt.contains("english_sentence"))
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "com.example.myapplication.data.remote.PromptBuilderTest"`
Expected: FAIL (compilation error — `buildSetPrompt` is unresolved).

- [ ] **Step 3: Implement `PromptBuilder`**

`app/src/main/java/com/example/myapplication/data/remote/PromptBuilder.kt`:

```kotlin
package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory

private const val FEW_SHOT_EXAMPLE = """
Example item:
{"korean_hint": "나는 좋아한다 / 산책하는 것을 / 왜냐하면 / 그것이 나를 편안하게 해주기 때문에", "english_sentence": "I like taking walks because it makes me feel relaxed."}
"""

fun buildSetPrompt(
    category: PracticeCategory,
    alreadyAskedQuestions: List<String>,
    questionCount: Int = 5
): String {
    val avoidSection = if (alreadyAskedQuestions.isEmpty()) {
        "None yet."
    } else {
        alreadyAskedQuestions.joinToString(separator = "\n") { "- $it" }
    }

    return """
        You are an OPIc (Oral Proficiency Interview-computer) speaking coach helping a Korean
        learner reach the highest level, OPIc AL. Generate $questionCount OPIc interview
        questions about ${category.promptTopic}, each with a model answer sentence a native
        AL-level speaker would give.

        Requirements for each model answer sentence:
        - Use natural, advanced English: complex/compound sentences, idiomatic expressions,
          and discourse markers (e.g. "what really stood out was...", "as opposed to...").
        - Keep each sentence speakable in one breath (roughly 15-30 words).

        Requirements for the Korean hint:
        - Translate the English sentence into Korean word-for-word, preserving the English
          word order exactly, with " / " separating the chunks that map to English phrases.
          This is NOT natural Korean; it is a literal, English-ordered gloss meant to help the
          learner rehearse English word order.

        $FEW_SHOT_EXAMPLE

        Do not repeat any of these questions already used in this session:
        $avoidSection

        Respond with ONLY a JSON array (no surrounding text) of exactly $questionCount objects,
        each shaped like: {"korean_hint": string, "english_sentence": string}
    """.trimIndent()
}
```

- [ ] **Step 4: Run the test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.example.myapplication.data.remote.PromptBuilderTest"`
Expected: PASS (4 tests)

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/myapplication/data/remote/PromptBuilder.kt app/src/test/java/com/example/myapplication/data/remote/PromptBuilderTest.kt
git commit -m "Add PromptBuilder for OPIc set generation prompts"
```

---

### Task 4: `PracticeSetParser`

**Files:**
- Create: `app/src/main/java/com/example/myapplication/data/remote/PracticeSetParser.kt`
- Test: `app/src/test/java/com/example/myapplication/data/remote/PracticeSetParserTest.kt`

**Interfaces:**
- Consumes: `PracticeCategory`, `PracticeQuestion` (Task 2).
- Produces: `fun parsePracticeSet(rawJson: String, category: PracticeCategory): List<PracticeQuestion>` and `class PracticeSetParseException`, consumed by `PracticeRepository` (Task 6).

- [ ] **Step 1: Write the failing test**

`app/src/test/java/com/example/myapplication/data/remote/PracticeSetParserTest.kt`:

```kotlin
package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PracticeSetParserTest {

    private val validJson = """
        [
          {"korean_hint": "나는 좋아한다 / 산책하는 것을", "english_sentence": "I like taking walks."},
          {"korean_hint": "나는 산다 / 서울에", "english_sentence": "I live in Seoul."}
        ]
    """.trimIndent()

    @Test
    fun `parses valid json array into practice questions`() {
        val result = parsePracticeSet(validJson, PracticeCategory.HOBBY)

        assertEquals(2, result.size)
        assertEquals("I like taking walks.", result[0].englishSentence)
        assertEquals(PracticeCategory.HOBBY, result[0].category)
    }

    @Test
    fun `throws PracticeSetParseException on malformed json`() {
        assertThrows(PracticeSetParseException::class.java) {
            parsePracticeSet("not json", PracticeCategory.HOBBY)
        }
    }

    @Test
    fun `throws PracticeSetParseException on empty array`() {
        assertThrows(PracticeSetParseException::class.java) {
            parsePracticeSet("[]", PracticeCategory.HOBBY)
        }
    }

    @Test
    fun `throws PracticeSetParseException when a field is blank`() {
        val jsonWithBlank = """[{"korean_hint": "", "english_sentence": "I like it."}]"""

        assertThrows(PracticeSetParseException::class.java) {
            parsePracticeSet(jsonWithBlank, PracticeCategory.HOBBY)
        }
    }

    @Test
    fun `tolerates surrounding whitespace`() {
        val paddedJson = "\n  $validJson  \n"

        val result = parsePracticeSet(paddedJson, PracticeCategory.HOUSING)

        assertEquals(2, result.size)
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "com.example.myapplication.data.remote.PracticeSetParserTest"`
Expected: FAIL (compilation error — `parsePracticeSet` and `PracticeSetParseException` are unresolved).

- [ ] **Step 3: Implement `PracticeSetParser`**

`app/src/main/java/com/example/myapplication/data/remote/PracticeSetParser.kt`:

```kotlin
package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
private data class PracticeQuestionDto(
    val korean_hint: String,
    val english_sentence: String
)

class PracticeSetParseException(message: String, cause: Throwable? = null) : Exception(message, cause)

private val json = Json { ignoreUnknownKeys = true }

fun parsePracticeSet(rawJson: String, category: PracticeCategory): List<PracticeQuestion> {
    val dtos = try {
        json.decodeFromString<List<PracticeQuestionDto>>(rawJson.trim())
    } catch (e: Exception) {
        throw PracticeSetParseException("Could not parse practice set JSON", e)
    }

    if (dtos.isEmpty()) {
        throw PracticeSetParseException("Practice set JSON was empty")
    }

    return dtos.map { dto ->
        if (dto.korean_hint.isBlank() || dto.english_sentence.isBlank()) {
            throw PracticeSetParseException("Practice set item had a blank field")
        }
        PracticeQuestion(
            koreanHint = dto.korean_hint,
            englishSentence = dto.english_sentence,
            category = category
        )
    }
}
```

- [ ] **Step 4: Run the test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.example.myapplication.data.remote.PracticeSetParserTest"`
Expected: PASS (5 tests)

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/myapplication/data/remote/PracticeSetParser.kt app/src/test/java/com/example/myapplication/data/remote/PracticeSetParserTest.kt
git commit -m "Add PracticeSetParser for Claude JSON responses"
```

---

### Task 5: `ClaudeApiClient`

**Files:**
- Create: `app/src/main/java/com/example/myapplication/data/remote/ClaudeApiClient.kt`
- Test: `app/src/test/java/com/example/myapplication/data/remote/ClaudeApiClientTest.kt`

**Interfaces:**
- Produces: `interface ClaudePromptSender { suspend fun sendPrompt(apiKey: String, prompt: String): String }`, `class ClaudeApiClient(httpClient: OkHttpClient = OkHttpClient(), baseUrl: String = ...) : ClaudePromptSender`, `class ClaudeApiException`. `ClaudePromptSender` is consumed by `PracticeRepository` (Task 6).

- [ ] **Step 1: Write the failing test**

`app/src/test/java/com/example/myapplication/data/remote/ClaudeApiClientTest.kt`:

```kotlin
package com.example.myapplication.data.remote

import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ClaudeApiClientTest {

    private lateinit var server: MockWebServer
    private lateinit var client: ClaudeApiClient

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        client = ClaudeApiClient(
            httpClient = OkHttpClient(),
            baseUrl = server.url("/v1/messages").toString()
        )
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun `returns text content from a successful response`() = runTest {
        val body = """{"content":[{"type":"text","text":"[{\"korean_hint\":\"h\",\"english_sentence\":\"e\"}]"}]}"""
        server.enqueue(MockResponse(body = body))

        val result = client.sendPrompt(apiKey = "test-key", prompt = "prompt")

        assertEquals("[{\"korean_hint\":\"h\",\"english_sentence\":\"e\"}]", result)
    }

    @Test
    fun `sends the api key header and prompt in the request body`() = runTest {
        server.enqueue(MockResponse(body = """{"content":[{"type":"text","text":"ok"}]}"""))

        client.sendPrompt(apiKey = "secret-key", prompt = "hello prompt")

        val recorded = server.takeRequest()
        assertEquals("secret-key", recorded.headers["x-api-key"])
        assertTrue(recorded.body?.utf8().orEmpty().contains("hello prompt"))
    }

    @Test
    fun `throws ClaudeApiException on non-2xx response`() = runTest {
        server.enqueue(MockResponse(code = 401, body = """{"error":"unauthorized"}"""))

        var thrown: ClaudeApiException? = null
        try {
            client.sendPrompt(apiKey = "bad-key", prompt = "prompt")
        } catch (e: ClaudeApiException) {
            thrown = e
        }
        assertTrue(thrown != null)
    }

    @Test
    fun `throws ClaudeApiException when response has no text block`() = runTest {
        server.enqueue(MockResponse(body = """{"content":[]}"""))

        var thrown: ClaudeApiException? = null
        try {
            client.sendPrompt(apiKey = "test-key", prompt = "prompt")
        } catch (e: ClaudeApiException) {
            thrown = e
        }
        assertTrue(thrown != null)
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "com.example.myapplication.data.remote.ClaudeApiClientTest"`
Expected: FAIL (compilation error — `ClaudeApiClient` and `ClaudeApiException` are unresolved).

- [ ] **Step 3: Implement `ClaudeApiClient`**

`app/src/main/java/com/example/myapplication/data/remote/ClaudeApiClient.kt`:

```kotlin
package com.example.myapplication.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ClaudeApiException(message: String, cause: Throwable? = null) : Exception(message, cause)

interface ClaudePromptSender {
    suspend fun sendPrompt(apiKey: String, prompt: String): String
}

@Serializable
private data class ClaudeRequestBody(
    val model: String = "claude-sonnet-5",
    val max_tokens: Int = 2048,
    val messages: List<ClaudeMessage>
)

@Serializable
private data class ClaudeMessage(val role: String, val content: String)

@Serializable
private data class ClaudeResponseBody(val content: List<ClaudeContentBlock> = emptyList())

@Serializable
private data class ClaudeContentBlock(val type: String, val text: String = "")

class ClaudeApiClient(
    private val httpClient: OkHttpClient = OkHttpClient(),
    private val baseUrl: String = "https://api.anthropic.com/v1/messages"
) : ClaudePromptSender {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun sendPrompt(apiKey: String, prompt: String): String = withContext(Dispatchers.IO) {
        val requestBodyJson = json.encodeToString(
            ClaudeRequestBody(messages = listOf(ClaudeMessage(role = "user", content = prompt)))
        )

        val request = Request.Builder()
            .url(baseUrl)
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", "2023-06-01")
            .post(requestBodyJson.toRequestBody("application/json".toMediaType()))
            .build()

        val response = try {
            httpClient.newCall(request).execute()
        } catch (e: Exception) {
            throw ClaudeApiException("Network request failed", e)
        }

        response.use {
            val bodyText = it.body?.string().orEmpty()
            if (!it.isSuccessful) {
                throw ClaudeApiException("Claude API returned HTTP ${it.code}: $bodyText")
            }
            val parsed = try {
                json.decodeFromString<ClaudeResponseBody>(bodyText)
            } catch (e: Exception) {
                throw ClaudeApiException("Could not parse Claude API response", e)
            }
            parsed.content.firstOrNull { block -> block.type == "text" }?.text
                ?: throw ClaudeApiException("Claude API response had no text content")
        }
    }
}
```

- [ ] **Step 4: Run the test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.example.myapplication.data.remote.ClaudeApiClientTest"`
Expected: PASS (4 tests)

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/myapplication/data/remote/ClaudeApiClient.kt app/src/test/java/com/example/myapplication/data/remote/ClaudeApiClientTest.kt
git commit -m "Add ClaudeApiClient for the Anthropic Messages API"
```

---

### Task 6: `PracticeRepository`

**Files:**
- Create: `app/src/main/java/com/example/myapplication/data/remote/PracticeRepository.kt`
- Test: `app/src/test/java/com/example/myapplication/data/remote/PracticeRepositoryTest.kt`

**Interfaces:**
- Consumes: `buildSetPrompt` (Task 3), `parsePracticeSet`/`PracticeSetParseException` (Task 4), `ClaudePromptSender`/`ClaudeApiException` (Task 5).
- Produces: `interface PracticeRepository { suspend fun generateSet(apiKey: String, category: PracticeCategory, alreadyAskedQuestions: List<String>, questionCount: Int = 5): Result<List<PracticeQuestion>> }` and `class ClaudePracticeRepository(sender: ClaudePromptSender) : PracticeRepository`. Consumed by `PracticeSetViewModel` (Task 11) and `AppContainer` (Task 18).

- [ ] **Step 1: Write the failing test**

`app/src/test/java/com/example/myapplication/data/remote/PracticeRepositoryTest.kt`:

```kotlin
package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private class FakeClaudePromptSender(
    private val response: Result<String>
) : ClaudePromptSender {
    var lastPrompt: String? = null

    override suspend fun sendPrompt(apiKey: String, prompt: String): String {
        lastPrompt = prompt
        return response.getOrThrow()
    }
}

class PracticeRepositoryTest {

    private val validResponseJson = """
        [{"korean_hint": "나는 산다 / 서울에", "english_sentence": "I live in Seoul."}]
    """.trimIndent()

    @Test
    fun `returns parsed questions on success`() = runTest {
        val sender = FakeClaudePromptSender(Result.success(validResponseJson))
        val repository = ClaudePracticeRepository(sender)

        val result = repository.generateSet(
            apiKey = "key",
            category = PracticeCategory.HOUSING,
            alreadyAskedQuestions = emptyList(),
            questionCount = 1
        )

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().size)
        assertEquals("I live in Seoul.", result.getOrThrow()[0].englishSentence)
    }

    @Test
    fun `builds a prompt that mentions the requested category`() = runTest {
        val sender = FakeClaudePromptSender(Result.success(validResponseJson))
        val repository = ClaudePracticeRepository(sender)

        repository.generateSet(
            apiKey = "key",
            category = PracticeCategory.HOUSING,
            alreadyAskedQuestions = emptyList(),
            questionCount = 1
        )

        assertTrue(sender.lastPrompt.orEmpty().contains(PracticeCategory.HOUSING.promptTopic))
    }

    @Test
    fun `wraps network failure into a failed Result`() = runTest {
        val sender = FakeClaudePromptSender(Result.failure(ClaudeApiException("boom")))
        val repository = ClaudePracticeRepository(sender)

        val result = repository.generateSet(
            apiKey = "key",
            category = PracticeCategory.HOUSING,
            alreadyAskedQuestions = emptyList()
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `wraps malformed response into a failed Result`() = runTest {
        val sender = FakeClaudePromptSender(Result.success("not json"))
        val repository = ClaudePracticeRepository(sender)

        val result = repository.generateSet(
            apiKey = "key",
            category = PracticeCategory.HOUSING,
            alreadyAskedQuestions = emptyList()
        )

        assertTrue(result.isFailure)
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "com.example.myapplication.data.remote.PracticeRepositoryTest"`
Expected: FAIL (compilation error — `PracticeRepository`/`ClaudePracticeRepository` are unresolved).

- [ ] **Step 3: Implement `PracticeRepository`**

`app/src/main/java/com/example/myapplication/data/remote/PracticeRepository.kt`:

```kotlin
package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import kotlinx.coroutines.CancellationException

interface PracticeRepository {
    suspend fun generateSet(
        apiKey: String,
        category: PracticeCategory,
        alreadyAskedQuestions: List<String>,
        questionCount: Int = 5
    ): Result<List<PracticeQuestion>>
}

class ClaudePracticeRepository(
    private val sender: ClaudePromptSender
) : PracticeRepository {

    override suspend fun generateSet(
        apiKey: String,
        category: PracticeCategory,
        alreadyAskedQuestions: List<String>,
        questionCount: Int
    ): Result<List<PracticeQuestion>> {
        return try {
            val prompt = buildSetPrompt(category, alreadyAskedQuestions, questionCount)
            val rawResponse = sender.sendPrompt(apiKey, prompt)
            Result.success(parsePracticeSet(rawResponse, category))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

Note: a plain `runCatching { ... }` around the suspend call to `sender.sendPrompt` would also catch
`kotlinx.coroutines.CancellationException`, silently turning a cancelled coroutine (e.g. a ViewModel
clearing mid-request) into an ordinary `Result.failure` instead of letting cancellation propagate —
breaking structured concurrency. The explicit `try/catch` above rethrows `CancellationException` before
the general `catch (e: Exception)` branch (catch-block ordering matters here, since `CancellationException`
is itself an `Exception` subtype).

- [ ] **Step 4: Run the test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.example.myapplication.data.remote.PracticeRepositoryTest"`
Expected: PASS (4 tests)

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/myapplication/data/remote/PracticeRepository.kt app/src/test/java/com/example/myapplication/data/remote/PracticeRepositoryTest.kt
git commit -m "Add PracticeRepository orchestrating prompt, API call, and parsing"
```

---

### Task 7: Room favorites (`FavoriteSentence`, `FavoriteDao`, `AppDatabase`, `FavoriteRepository`)

**Files:**
- Create: `app/src/main/java/com/example/myapplication/data/local/FavoriteSentence.kt`
- Create: `app/src/main/java/com/example/myapplication/data/local/FavoriteDao.kt`
- Create: `app/src/main/java/com/example/myapplication/data/local/AppDatabase.kt`
- Create: `app/src/main/java/com/example/myapplication/data/local/FavoriteRepository.kt`
- Create: `app/src/test/java/com/example/myapplication/data/local/FakeFavoriteDao.kt`
- Test: `app/src/androidTest/java/com/example/myapplication/data/local/FavoriteDaoTest.kt`

**Interfaces:**
- Consumes: `PracticeCategory`, `PracticeQuestion` (Task 2).
- Produces: `FavoriteSentence` entity (`id`, `category`, `koreanHint`, `englishSentence`, `createdAt`); `FavoriteDao` with `insert`, `delete`, `observeAll`, `isFavorite`, `deleteByEnglishSentence`; `FavoriteRepository` with `observeFavorites()`, `addFavorite(PracticeQuestion)`, `removeFavorite(FavoriteSentence)`, `removeFavoriteByQuestion(PracticeQuestion)`, `isFavorite(PracticeQuestion): Boolean`. `FavoriteRepository` and `AppDatabase` are consumed by Tasks 11, 13, 16, 17, 18. The test-only `FakeFavoriteDao` (JVM unit tests only) is consumed by Tasks 11 and 13.

Room DAOs need a real SQLite engine, so this task's test lives in `src/androidTest` and runs against an in-memory Room database.

- [ ] **Step 1: Write the failing test**

`app/src/androidTest/java/com/example/myapplication/data/local/FavoriteDaoTest.kt`:

```kotlin
package com.example.myapplication.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: FavoriteRepository

    private val sampleQuestion = PracticeQuestion(
        koreanHint = "나는 산다 / 서울에",
        englishSentence = "I live in Seoul.",
        category = PracticeCategory.HOUSING
    )

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        repository = FavoriteRepository(database.favoriteDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addFavorite_thenObserveAll_returnsIt() = runBlocking {
        repository.addFavorite(sampleQuestion)

        val favorites = repository.observeFavorites().first()

        assertEquals(1, favorites.size)
        assertEquals("I live in Seoul.", favorites[0].englishSentence)
    }

    @Test
    fun isFavorite_returnsFalseBeforeAddAndTrueAfter() = runBlocking {
        assertFalse(repository.isFavorite(sampleQuestion))

        repository.addFavorite(sampleQuestion)

        assertTrue(repository.isFavorite(sampleQuestion))
    }

    @Test
    fun removeFavorite_deletesIt() = runBlocking {
        repository.addFavorite(sampleQuestion)
        val stored = repository.observeFavorites().first().first()

        repository.removeFavorite(stored)

        assertTrue(repository.observeFavorites().first().isEmpty())
    }

    @Test
    fun removeFavoriteByQuestion_deletesMatchingRow() = runBlocking {
        repository.addFavorite(sampleQuestion)

        repository.removeFavoriteByQuestion(sampleQuestion)

        assertTrue(repository.observeFavorites().first().isEmpty())
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.data.local.FavoriteDaoTest`
Expected: FAIL (compilation error — `AppDatabase`, `FavoriteRepository`, `FavoriteDao` are unresolved). Requires a connected emulator/device.

- [ ] **Step 3: Implement the entity**

`app/src/main/java/com/example/myapplication/data/local/FavoriteSentence.kt`:

```kotlin
package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_sentences")
data class FavoriteSentence(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val koreanHint: String,
    val englishSentence: String,
    val createdAt: Long
)
```

- [ ] **Step 4: Implement the DAO**

`app/src/main/java/com/example/myapplication/data/local/FavoriteDao.kt`:

```kotlin
package com.example.myapplication.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert
    suspend fun insert(favorite: FavoriteSentence): Long

    @Delete
    suspend fun delete(favorite: FavoriteSentence)

    @Query("DELETE FROM favorite_sentences WHERE englishSentence = :englishSentence")
    suspend fun deleteByEnglishSentence(englishSentence: String)

    @Query("SELECT * FROM favorite_sentences ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<FavoriteSentence>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_sentences WHERE englishSentence = :englishSentence)")
    suspend fun isFavorite(englishSentence: String): Boolean
}
```

- [ ] **Step 5: Implement the database**

`app/src/main/java/com/example/myapplication/data/local/AppDatabase.kt`:

```kotlin
package com.example.myapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteSentence::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
```

- [ ] **Step 6: Implement `FavoriteRepository`**

`app/src/main/java/com/example/myapplication/data/local/FavoriteRepository.kt`:

```kotlin
package com.example.myapplication.data.local

import com.example.myapplication.data.model.PracticeQuestion
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(private val dao: FavoriteDao) {

    fun observeFavorites(): Flow<List<FavoriteSentence>> = dao.observeAll()

    suspend fun addFavorite(question: PracticeQuestion) {
        dao.insert(
            FavoriteSentence(
                category = question.category.name,
                koreanHint = question.koreanHint,
                englishSentence = question.englishSentence,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeFavorite(favorite: FavoriteSentence) {
        dao.delete(favorite)
    }

    suspend fun removeFavoriteByQuestion(question: PracticeQuestion) {
        dao.deleteByEnglishSentence(question.englishSentence)
    }

    suspend fun isFavorite(question: PracticeQuestion): Boolean =
        dao.isFavorite(question.englishSentence)
}
```

- [ ] **Step 7: Add the in-memory fake DAO used by later unit tests**

`app/src/test/java/com/example/myapplication/data/local/FakeFavoriteDao.kt`:

```kotlin
package com.example.myapplication.data.local

import kotlinx.coroutines.flow.MutableStateFlow

class FakeFavoriteDao : FavoriteDao {

    private val favorites = MutableStateFlow<List<FavoriteSentence>>(emptyList())
    private var nextId = 1L

    override suspend fun insert(favorite: FavoriteSentence): Long {
        val stored = favorite.copy(id = nextId++)
        favorites.value = favorites.value + stored
        return stored.id
    }

    override suspend fun delete(favorite: FavoriteSentence) {
        favorites.value = favorites.value.filterNot { it.id == favorite.id }
    }

    override suspend fun deleteByEnglishSentence(englishSentence: String) {
        favorites.value = favorites.value.filterNot { it.englishSentence == englishSentence }
    }

    override fun observeAll() = favorites

    override suspend fun isFavorite(englishSentence: String): Boolean =
        favorites.value.any { it.englishSentence == englishSentence }
}
```

- [ ] **Step 8: Run the test to verify it passes**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.data.local.FavoriteDaoTest`
Expected: PASS (4 tests) on a connected emulator/device.

- [ ] **Step 9: Commit**

```bash
git add app/src/main/java/com/example/myapplication/data/local app/src/test/java/com/example/myapplication/data/local app/src/androidTest/java/com/example/myapplication/data/local
git commit -m "Add Room favorites storage"
```

---

### Task 8: `ApiKeyStore` (encrypted settings storage)

**Files:**
- Create: `app/src/main/java/com/example/myapplication/data/settings/ApiKeyStore.kt`
- Create: `app/src/main/java/com/example/myapplication/data/settings/EncryptedApiKeyStore.kt`
- Test: `app/src/androidTest/java/com/example/myapplication/data/settings/EncryptedApiKeyStoreTest.kt`

**Interfaces:**
- Produces: `interface ApiKeyStore { fun getApiKey(): String?; fun setApiKey(apiKey: String); fun clearApiKey() }` and `class EncryptedApiKeyStore(context: Context) : ApiKeyStore`. Consumed by `PracticeSetViewModel` (Task 11), `SettingsViewModel` (Task 12), `AppContainer` (Task 18).

`EncryptedSharedPreferences` requires the Android Keystore, so this is an instrumented test.

- [ ] **Step 1: Write the failing test**

`app/src/androidTest/java/com/example/myapplication/data/settings/EncryptedApiKeyStoreTest.kt`:

```kotlin
package com.example.myapplication.data.settings

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncryptedApiKeyStoreTest {

    private lateinit var store: EncryptedApiKeyStore

    @Before
    fun setUp() {
        store = EncryptedApiKeyStore(ApplicationProvider.getApplicationContext())
        store.clearApiKey()
    }

    @After
    fun tearDown() {
        store.clearApiKey()
    }

    @Test
    fun getApiKey_returnsNullWhenNothingStored() {
        assertNull(store.getApiKey())
    }

    @Test
    fun setApiKey_thenGetApiKey_returnsStoredValue() {
        store.setApiKey("sk-ant-test-123")

        assertEquals("sk-ant-test-123", store.getApiKey())
    }

    @Test
    fun clearApiKey_removesStoredValue() {
        store.setApiKey("sk-ant-test-123")

        store.clearApiKey()

        assertNull(store.getApiKey())
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.data.settings.EncryptedApiKeyStoreTest`
Expected: FAIL (compilation error — `EncryptedApiKeyStore` is unresolved).

- [ ] **Step 3: Implement `ApiKeyStore`**

`app/src/main/java/com/example/myapplication/data/settings/ApiKeyStore.kt`:

```kotlin
package com.example.myapplication.data.settings

interface ApiKeyStore {
    fun getApiKey(): String?
    fun setApiKey(apiKey: String)
    fun clearApiKey()
}
```

- [ ] **Step 4: Implement `EncryptedApiKeyStore`**

`app/src/main/java/com/example/myapplication/data/settings/EncryptedApiKeyStore.kt`:

```kotlin
package com.example.myapplication.data.settings

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

private const val PREFS_FILE_NAME = "opic_practice_secure_prefs"
private const val KEY_API_KEY = "anthropic_api_key"

class EncryptedApiKeyStore(context: Context) : ApiKeyStore {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun getApiKey(): String? = prefs.getString(KEY_API_KEY, null)

    override fun setApiKey(apiKey: String) {
        prefs.edit().putString(KEY_API_KEY, apiKey).apply()
    }

    override fun clearApiKey() {
        prefs.edit().remove(KEY_API_KEY).apply()
    }
}
```

- [ ] **Step 5: Run the test to verify it passes**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.data.settings.EncryptedApiKeyStoreTest`
Expected: PASS (3 tests)

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/example/myapplication/data/settings app/src/androidTest/java/com/example/myapplication/data/settings
git commit -m "Add encrypted API key storage"
```

---

### Task 9: `SpeechPlayer` (TTS for the model sentence)

**Files:**
- Create: `app/src/main/java/com/example/myapplication/audio/SpeechPlayer.kt`
- Create: `app/src/main/java/com/example/myapplication/audio/AndroidSpeechPlayer.kt`
- Test: `app/src/androidTest/java/com/example/myapplication/audio/AndroidSpeechPlayerTest.kt`

**Interfaces:**
- Produces: `interface SpeechPlayer { fun speak(text: String); fun release() }` and `class AndroidSpeechPlayer(context: Context) : SpeechPlayer`. Consumed by `PracticeSetViewModel` (Task 11), `AppContainer` (Task 18).

TTS engine availability varies by emulator, so this task's test is a smoke test confirming the wrapper never throws, rather than asserting audio output.

- [ ] **Step 1: Write the failing test**

`app/src/androidTest/java/com/example/myapplication/audio/AndroidSpeechPlayerTest.kt`:

```kotlin
package com.example.myapplication.audio

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

// TTS engine availability varies by emulator/device, so these are smoke tests:
// the assertion is that the wrapper never throws, not that audio is produced.
@RunWith(AndroidJUnit4::class)
class AndroidSpeechPlayerTest {

    @Test
    fun speak_beforeInitCompletes_doesNotThrow() {
        val player = AndroidSpeechPlayer(ApplicationProvider.getApplicationContext())

        player.speak("Hello there.")
        player.release()
    }

    @Test
    fun release_calledTwice_doesNotThrow() {
        val player = AndroidSpeechPlayer(ApplicationProvider.getApplicationContext())

        player.release()
        player.release()
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.audio.AndroidSpeechPlayerTest`
Expected: FAIL (compilation error — `AndroidSpeechPlayer` is unresolved).

- [ ] **Step 3: Implement `SpeechPlayer`**

`app/src/main/java/com/example/myapplication/audio/SpeechPlayer.kt`:

```kotlin
package com.example.myapplication.audio

interface SpeechPlayer {
    fun speak(text: String)
    fun release()
}
```

- [ ] **Step 4: Implement `AndroidSpeechPlayer`**

`app/src/main/java/com/example/myapplication/audio/AndroidSpeechPlayer.kt`:

```kotlin
package com.example.myapplication.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class AndroidSpeechPlayer(context: Context) : SpeechPlayer {

    private var isReady = false

    private val textToSpeech: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        isReady = status == TextToSpeech.SUCCESS
        if (isReady) {
            textToSpeech.language = Locale.US
        }
    }

    override fun speak(text: String) {
        if (!isReady) return
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "opic_model_sentence")
    }

    override fun release() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        isReady = false
    }
}
```

- [ ] **Step 5: Run the test to verify it passes**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.audio.AndroidSpeechPlayerTest`
Expected: PASS (2 tests)

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/example/myapplication/audio/SpeechPlayer.kt app/src/main/java/com/example/myapplication/audio/AndroidSpeechPlayer.kt app/src/androidTest/java/com/example/myapplication/audio/AndroidSpeechPlayerTest.kt
git commit -m "Add TTS playback for model sentences"
```

---

### Task 10: `VoiceRecorder` / `VoicePlayer` (user recording round trip)

**Files:**
- Create: `app/src/main/java/com/example/myapplication/audio/VoiceRecorder.kt`
- Create: `app/src/main/java/com/example/myapplication/audio/VoicePlayer.kt`
- Create: `app/src/main/java/com/example/myapplication/audio/MediaRecorderVoiceRecorder.kt`
- Create: `app/src/main/java/com/example/myapplication/audio/MediaPlayerVoicePlayer.kt`
- Test: `app/src/androidTest/java/com/example/myapplication/audio/VoiceRecorderPlaybackTest.kt`

**Interfaces:**
- Produces: `interface VoiceRecorder { fun startRecording(outputFile: File); fun stopRecording(); fun isRecording(): Boolean }`, `interface VoicePlayer { fun play(file: File); fun stop() }`, and their `MediaRecorder`/`MediaPlayer`-backed implementations. Consumed by `PracticeSetViewModel` (Task 11), `AppContainer` (Task 18).

- [ ] **Step 1: Write the failing test**

`app/src/androidTest/java/com/example/myapplication/audio/VoiceRecorderPlaybackTest.kt`:

```kotlin
package com.example.myapplication.audio

import android.Manifest
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class VoiceRecorderPlaybackTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO)

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val outputFile = File(context.cacheDir, "voice_recorder_test.m4a")
    private val recorder = MediaRecorderVoiceRecorder(context)
    private val player = MediaPlayerVoicePlayer()

    @After
    fun tearDown() {
        player.stop()
        outputFile.delete()
    }

    @Test
    fun startThenStopRecording_writesANonEmptyFile() {
        recorder.startRecording(outputFile)
        assertTrue(recorder.isRecording())
        Thread.sleep(300)
        recorder.stopRecording()

        assertTrue(outputFile.exists() && outputFile.length() > 0)
        assertFalse(recorder.isRecording())
    }

    @Test
    fun recordedFile_canBePlayedBack() {
        recorder.startRecording(outputFile)
        Thread.sleep(300)
        recorder.stopRecording()

        player.play(outputFile)
        Thread.sleep(200)
        player.stop()
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.audio.VoiceRecorderPlaybackTest`
Expected: FAIL (compilation error — `MediaRecorderVoiceRecorder` and `MediaPlayerVoicePlayer` are unresolved).

- [ ] **Step 3: Implement the interfaces**

`app/src/main/java/com/example/myapplication/audio/VoiceRecorder.kt`:

```kotlin
package com.example.myapplication.audio

import java.io.File

interface VoiceRecorder {
    fun startRecording(outputFile: File)
    fun stopRecording()
    fun isRecording(): Boolean
}
```

`app/src/main/java/com/example/myapplication/audio/VoicePlayer.kt`:

```kotlin
package com.example.myapplication.audio

import java.io.File

interface VoicePlayer {
    fun play(file: File)
    fun stop()
}
```

- [ ] **Step 4: Implement `MediaRecorderVoiceRecorder`**

`app/src/main/java/com/example/myapplication/audio/MediaRecorderVoiceRecorder.kt`:

```kotlin
package com.example.myapplication.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class MediaRecorderVoiceRecorder(
    private val newRecorder: () -> MediaRecorder
) : VoiceRecorder {

    constructor(context: Context) : this(
        newRecorder = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }
        }
    )

    private var recorder: MediaRecorder? = null
    private var recording = false

    override fun startRecording(outputFile: File) {
        val mediaRecorder = newRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }
        recorder = mediaRecorder
        recording = true
    }

    override fun stopRecording() {
        val activeRecorder = recorder
        recorder = null
        recording = false
        try {
            activeRecorder?.stop()
        } catch (e: RuntimeException) {
            // stop() throws if too little data was recorded (e.g. instant tap-release) — the
            // output file may be invalid/empty in this case, which callers should already
            // tolerate, but the recorder must still be released and state must still reset.
        } finally {
            activeRecorder?.release()
        }
    }

    override fun isRecording(): Boolean = recording
}
```

- [ ] **Step 5: Implement `MediaPlayerVoicePlayer`**

`app/src/main/java/com/example/myapplication/audio/MediaPlayerVoicePlayer.kt`:

```kotlin
package com.example.myapplication.audio

import android.media.MediaPlayer
import java.io.File

class MediaPlayerVoicePlayer : VoicePlayer {

    private var player: MediaPlayer? = null

    override fun play(file: File) {
        stop()
        player = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            prepare()
            start()
        }
    }

    override fun stop() {
        player?.apply {
            if (isPlaying) stop()
            release()
        }
        player = null
    }
}
```

- [ ] **Step 6: Run the test to verify it passes**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.audio.VoiceRecorderPlaybackTest`
Expected: PASS (2 tests)

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/example/myapplication/audio/VoiceRecorder.kt app/src/main/java/com/example/myapplication/audio/VoicePlayer.kt app/src/main/java/com/example/myapplication/audio/MediaRecorderVoiceRecorder.kt app/src/main/java/com/example/myapplication/audio/MediaPlayerVoicePlayer.kt app/src/androidTest/java/com/example/myapplication/audio/VoiceRecorderPlaybackTest.kt
git commit -m "Add MediaRecorder/MediaPlayer voice round trip"
```

---

### Task 11: `PracticeSetViewModel` and `PracticeSetUiState`

**Files:**
- Create: `app/src/main/java/com/example/myapplication/ui/set/PracticeSetUiState.kt`
- Create: `app/src/main/java/com/example/myapplication/ui/set/PracticeSetViewModel.kt`
- Create: `app/src/test/java/com/example/myapplication/MainDispatcherRule.kt`
- Test: `app/src/test/java/com/example/myapplication/ui/set/PracticeSetViewModelTest.kt`

**Interfaces:**
- Consumes: `PracticeCategory`/`PracticeQuestion` (Task 2), `PracticeRepository` (Task 6), `FavoriteRepository` (Task 7), `ApiKeyStore` (Task 8), `SpeechPlayer` (Task 9), `VoiceRecorder`/`VoicePlayer` (Task 10).
- Produces: `sealed interface PracticeSetUiState` (`Loading`, `Error(message)`, `Ready(questions, currentIndex, isRecording, hasRecording, isCurrentFavorite, isSetComplete)`) and `class PracticeSetViewModel(...)` with `uiState: StateFlow<PracticeSetUiState>`, `loadSet()`, `startRecording()`, `stopRecording()`, `playMyRecording()`, `playModelSentence()`, `toggleFavorite()`, `nextQuestion()`. Consumed by `PracticeScreen` (Task 16) and `AppContainer`/`NavGraph` (Task 18). `MainDispatcherRule` is reused by Task 13's test.

- [ ] **Step 1: Write the failing test**

`app/src/test/java/com/example/myapplication/MainDispatcherRule.kt`:

```kotlin
package com.example.myapplication

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
```

`app/src/test/java/com/example/myapplication/ui/set/PracticeSetViewModelTest.kt`:

```kotlin
package com.example.myapplication.ui.set

import com.example.myapplication.MainDispatcherRule
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.audio.VoicePlayer
import com.example.myapplication.audio.VoiceRecorder
import com.example.myapplication.data.local.FakeFavoriteDao
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import com.example.myapplication.data.remote.PracticeRepository
import com.example.myapplication.data.settings.ApiKeyStore
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

private val QUESTION_1 = PracticeQuestion("힌트1", "Sentence one.", PracticeCategory.HOBBY)
private val QUESTION_2 = PracticeQuestion("힌트2", "Sentence two.", PracticeCategory.HOBBY)

private class FakePracticeRepository(
    private val result: Result<List<PracticeQuestion>>
) : PracticeRepository {
    override suspend fun generateSet(
        apiKey: String,
        category: PracticeCategory,
        alreadyAskedQuestions: List<String>,
        questionCount: Int
    ): Result<List<PracticeQuestion>> = result
}

private class FakeApiKeyStore(private var key: String?) : ApiKeyStore {
    override fun getApiKey(): String? = key
    override fun setApiKey(apiKey: String) { key = apiKey }
    override fun clearApiKey() { key = null }
}

private class FakeSpeechPlayer : SpeechPlayer {
    var spokenText: String? = null
    override fun speak(text: String) { spokenText = text }
    override fun release() {}
}

private class FakeVoiceRecorder : VoiceRecorder {
    private var recording = false
    override fun startRecording(outputFile: File) { recording = true }
    override fun stopRecording() { recording = false }
    override fun isRecording(): Boolean = recording
}

private class FakeVoicePlayer : VoicePlayer {
    var playedFile: File? = null
    override fun play(file: File) { playedFile = file }
    override fun stop() {}
}

class PracticeSetViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildViewModel(
        result: Result<List<PracticeQuestion>> = Result.success(listOf(QUESTION_1, QUESTION_2)),
        apiKey: String? = "test-key"
    ): PracticeSetViewModel = PracticeSetViewModel(
        category = PracticeCategory.HOBBY,
        practiceRepository = FakePracticeRepository(result),
        favoriteRepository = FavoriteRepository(FakeFavoriteDao()),
        apiKeyStore = FakeApiKeyStore(apiKey),
        speechPlayer = FakeSpeechPlayer(),
        voiceRecorder = FakeVoiceRecorder(),
        voicePlayer = FakeVoicePlayer(),
        recordingFile = File.createTempFile("recording_test", ".m4a").apply { deleteOnExit() }
    )

    @Test
    fun `loadSet without an api key emits an error state`() = runTest {
        val viewModel = buildViewModel(apiKey = null)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is PracticeSetUiState.Error)
    }

    @Test
    fun `loadSet success emits ready state with first question`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value as PracticeSetUiState.Ready
        assertEquals(0, state.currentIndex)
        assertEquals(QUESTION_1, state.questions[0])
        assertFalse(state.isCurrentFavorite)
    }

    @Test
    fun `start then stop recording updates flags`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.startRecording()
        assertTrue((viewModel.uiState.value as PracticeSetUiState.Ready).isRecording)

        viewModel.stopRecording()
        val afterStop = viewModel.uiState.value as PracticeSetUiState.Ready
        assertFalse(afterStop.isRecording)
        assertTrue(afterStop.hasRecording)
    }

    @Test
    fun `toggleFavorite marks then unmarks the current question`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.toggleFavorite()
        advanceUntilIdle()
        assertTrue((viewModel.uiState.value as PracticeSetUiState.Ready).isCurrentFavorite)

        viewModel.toggleFavorite()
        advanceUntilIdle()
        assertFalse((viewModel.uiState.value as PracticeSetUiState.Ready).isCurrentFavorite)
    }

    @Test
    fun `nextQuestion advances index and resets per-question flags`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.nextQuestion()
        advanceUntilIdle()

        val state = viewModel.uiState.value as PracticeSetUiState.Ready
        assertEquals(1, state.currentIndex)
        assertFalse(state.hasRecording)
        assertFalse(state.isSetComplete)
    }

    @Test
    fun `nextQuestion past the last question marks the set complete`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.nextQuestion()
        advanceUntilIdle()
        viewModel.nextQuestion()
        advanceUntilIdle()

        val state = viewModel.uiState.value as PracticeSetUiState.Ready
        assertTrue(state.isSetComplete)
    }

    @Test
    fun `playModelSentence speaks the current question's english sentence`() = runTest {
        val speechPlayer = FakeSpeechPlayer()
        val viewModel = PracticeSetViewModel(
            category = PracticeCategory.HOBBY,
            practiceRepository = FakePracticeRepository(Result.success(listOf(QUESTION_1, QUESTION_2))),
            favoriteRepository = FavoriteRepository(FakeFavoriteDao()),
            apiKeyStore = FakeApiKeyStore("test-key"),
            speechPlayer = speechPlayer,
            voiceRecorder = FakeVoiceRecorder(),
            voicePlayer = FakeVoicePlayer(),
            recordingFile = File.createTempFile("recording_test", ".m4a").apply { deleteOnExit() }
        )
        advanceUntilIdle()

        viewModel.playModelSentence()

        assertEquals("Sentence one.", speechPlayer.spokenText)
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "com.example.myapplication.ui.set.PracticeSetViewModelTest"`
Expected: FAIL (compilation error — `PracticeSetUiState` and `PracticeSetViewModel` are unresolved).

- [ ] **Step 3: Implement `PracticeSetUiState`**

`app/src/main/java/com/example/myapplication/ui/set/PracticeSetUiState.kt`:

```kotlin
package com.example.myapplication.ui.set

import com.example.myapplication.data.model.PracticeQuestion

sealed interface PracticeSetUiState {
    data object Loading : PracticeSetUiState

    data class Error(val message: String) : PracticeSetUiState

    data class Ready(
        val questions: List<PracticeQuestion>,
        val currentIndex: Int,
        val isRecording: Boolean,
        val hasRecording: Boolean,
        val isCurrentFavorite: Boolean,
        val isSetComplete: Boolean
    ) : PracticeSetUiState
}
```

- [ ] **Step 4: Implement `PracticeSetViewModel`**

`app/src/main/java/com/example/myapplication/ui/set/PracticeSetViewModel.kt`:

```kotlin
package com.example.myapplication.ui.set

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.audio.VoicePlayer
import com.example.myapplication.audio.VoiceRecorder
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.remote.PracticeRepository
import com.example.myapplication.data.settings.ApiKeyStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class PracticeSetViewModel(
    private val category: PracticeCategory,
    private val practiceRepository: PracticeRepository,
    private val favoriteRepository: FavoriteRepository,
    private val apiKeyStore: ApiKeyStore,
    private val speechPlayer: SpeechPlayer,
    private val voiceRecorder: VoiceRecorder,
    private val voicePlayer: VoicePlayer,
    private val recordingFile: File
) : ViewModel() {

    private val _uiState = MutableStateFlow<PracticeSetUiState>(PracticeSetUiState.Loading)
    val uiState: StateFlow<PracticeSetUiState> = _uiState.asStateFlow()

    init {
        loadSet()
    }

    fun loadSet() {
        _uiState.value = PracticeSetUiState.Loading
        viewModelScope.launch {
            val apiKey = apiKeyStore.getApiKey()
            if (apiKey.isNullOrBlank()) {
                _uiState.value = PracticeSetUiState.Error("설정에서 API 키를 먼저 입력해주세요.")
                return@launch
            }
            practiceRepository.generateSet(
                apiKey = apiKey,
                category = category,
                alreadyAskedQuestions = emptyList()
            ).onSuccess { questions ->
                _uiState.value = PracticeSetUiState.Ready(
                    questions = questions,
                    currentIndex = 0,
                    isRecording = false,
                    hasRecording = false,
                    isCurrentFavorite = favoriteRepository.isFavorite(questions.first()),
                    isSetComplete = false
                )
            }.onFailure { error ->
                _uiState.value = PracticeSetUiState.Error(error.message ?: "문제를 생성하지 못했습니다.")
            }
        }
    }

    fun startRecording() {
        val current = _uiState.value as? PracticeSetUiState.Ready ?: return
        voiceRecorder.startRecording(recordingFile)
        _uiState.value = current.copy(isRecording = true)
    }

    fun stopRecording() {
        val current = _uiState.value as? PracticeSetUiState.Ready ?: return
        voiceRecorder.stopRecording()
        _uiState.value = current.copy(isRecording = false, hasRecording = true)
    }

    fun playMyRecording() {
        if (recordingFile.exists()) voicePlayer.play(recordingFile)
    }

    fun playModelSentence() {
        val current = _uiState.value as? PracticeSetUiState.Ready ?: return
        speechPlayer.speak(current.questions[current.currentIndex].englishSentence)
    }

    fun toggleFavorite() {
        val current = _uiState.value as? PracticeSetUiState.Ready ?: return
        val question = current.questions[current.currentIndex]
        viewModelScope.launch {
            if (current.isCurrentFavorite) {
                favoriteRepository.removeFavoriteByQuestion(question)
            } else {
                favoriteRepository.addFavorite(question)
            }
            val refreshed = _uiState.value as? PracticeSetUiState.Ready ?: return@launch
            _uiState.value = refreshed.copy(isCurrentFavorite = !current.isCurrentFavorite)
        }
    }

    fun nextQuestion() {
        val current = _uiState.value as? PracticeSetUiState.Ready ?: return
        recordingFile.delete()
        val nextIndex = current.currentIndex + 1
        if (nextIndex >= current.questions.size) {
            _uiState.value = current.copy(isSetComplete = true, isRecording = false, hasRecording = false)
            return
        }
        viewModelScope.launch {
            val isFavorite = favoriteRepository.isFavorite(current.questions[nextIndex])
            _uiState.value = current.copy(
                currentIndex = nextIndex,
                isRecording = false,
                hasRecording = false,
                isCurrentFavorite = isFavorite
            )
        }
    }

    override fun onCleared() {
        speechPlayer.release()
        recordingFile.delete()
    }
}
```

- [ ] **Step 5: Run the test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.example.myapplication.ui.set.PracticeSetViewModelTest"`
Expected: PASS (7 tests)

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/example/myapplication/ui/set/PracticeSetUiState.kt app/src/main/java/com/example/myapplication/ui/set/PracticeSetViewModel.kt app/src/test/java/com/example/myapplication/MainDispatcherRule.kt app/src/test/java/com/example/myapplication/ui/set/PracticeSetViewModelTest.kt
git commit -m "Add PracticeSetViewModel state machine"
```

---

### Task 12: `SettingsViewModel`

**Files:**
- Create: `app/src/main/java/com/example/myapplication/ui/settings/SettingsUiState.kt`
- Create: `app/src/main/java/com/example/myapplication/ui/settings/SettingsViewModel.kt`
- Test: `app/src/test/java/com/example/myapplication/ui/settings/SettingsViewModelTest.kt`

**Interfaces:**
- Consumes: `ApiKeyStore` (Task 8).
- Produces: `data class SettingsUiState(val apiKeyInput: String = "", val isSaved: Boolean = false)` and `class SettingsViewModel(apiKeyStore: ApiKeyStore)` with `uiState: StateFlow<SettingsUiState>`, `onApiKeyChanged(String)`, `saveApiKey()`. Consumed by `SettingsScreen` (Task 15) and `AppContainer`/`NavGraph` (Task 18).

- [ ] **Step 1: Write the failing test**

`app/src/test/java/com/example/myapplication/ui/settings/SettingsViewModelTest.kt`:

```kotlin
package com.example.myapplication.ui.settings

import com.example.myapplication.data.settings.ApiKeyStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private class FakeApiKeyStore(private var key: String? = null) : ApiKeyStore {
    override fun getApiKey(): String? = key
    override fun setApiKey(apiKey: String) { key = apiKey }
    override fun clearApiKey() { key = null }
}

class SettingsViewModelTest {

    @Test
    fun `initial state loads existing key from the store`() {
        val viewModel = SettingsViewModel(FakeApiKeyStore(key = "sk-existing"))

        assertEquals("sk-existing", viewModel.uiState.value.apiKeyInput)
    }

    @Test
    fun `onApiKeyChanged updates input and clears saved flag`() {
        val viewModel = SettingsViewModel(FakeApiKeyStore())

        viewModel.onApiKeyChanged("sk-new-key")

        assertEquals("sk-new-key", viewModel.uiState.value.apiKeyInput)
        assertFalse(viewModel.uiState.value.isSaved)
    }

    @Test
    fun `saveApiKey persists the trimmed key and sets saved flag`() {
        val store = FakeApiKeyStore()
        val viewModel = SettingsViewModel(store)
        viewModel.onApiKeyChanged("  sk-new-key  ")

        viewModel.saveApiKey()

        assertEquals("sk-new-key", store.getApiKey())
        assertTrue(viewModel.uiState.value.isSaved)
    }

    @Test
    fun `saveApiKey with blank input does not persist`() {
        val store = FakeApiKeyStore(key = "sk-existing")
        val viewModel = SettingsViewModel(store)
        viewModel.onApiKeyChanged("   ")

        viewModel.saveApiKey()

        assertEquals("sk-existing", store.getApiKey())
        assertFalse(viewModel.uiState.value.isSaved)
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "com.example.myapplication.ui.settings.SettingsViewModelTest"`
Expected: FAIL (compilation error — `SettingsViewModel` is unresolved).

- [ ] **Step 3: Implement `SettingsUiState`**

`app/src/main/java/com/example/myapplication/ui/settings/SettingsUiState.kt`:

```kotlin
package com.example.myapplication.ui.settings

data class SettingsUiState(
    val apiKeyInput: String = "",
    val isSaved: Boolean = false
)
```

- [ ] **Step 4: Implement `SettingsViewModel`**

`app/src/main/java/com/example/myapplication/ui/settings/SettingsViewModel.kt`:

```kotlin
package com.example.myapplication.ui.settings

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.settings.ApiKeyStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    private val apiKeyStore: ApiKeyStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(apiKeyInput = apiKeyStore.getApiKey().orEmpty())
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onApiKeyChanged(newValue: String) {
        _uiState.value = _uiState.value.copy(apiKeyInput = newValue, isSaved = false)
    }

    fun saveApiKey() {
        val current = _uiState.value
        if (current.apiKeyInput.isBlank()) return
        apiKeyStore.setApiKey(current.apiKeyInput.trim())
        _uiState.value = current.copy(isSaved = true)
    }
}
```

- [ ] **Step 5: Run the test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.example.myapplication.ui.settings.SettingsViewModelTest"`
Expected: PASS (4 tests)

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/example/myapplication/ui/settings/SettingsUiState.kt app/src/main/java/com/example/myapplication/ui/settings/SettingsViewModel.kt app/src/test/java/com/example/myapplication/ui/settings/SettingsViewModelTest.kt
git commit -m "Add SettingsViewModel for API key entry"
```

---

### Task 13: `FavoritesViewModel`

**Files:**
- Create: `app/src/main/java/com/example/myapplication/ui/favorites/FavoritesViewModel.kt`
- Test: `app/src/test/java/com/example/myapplication/ui/favorites/FavoritesViewModelTest.kt`

**Interfaces:**
- Consumes: `FavoriteRepository`/`FavoriteSentence` (Task 7), `MainDispatcherRule` (Task 11).
- Produces: `class FavoritesViewModel(favoriteRepository: FavoriteRepository)` with `favorites: StateFlow<List<FavoriteSentence>>`, `removeFavorite(FavoriteSentence)`. Consumed by `FavoritesScreen` (Task 17) and `AppContainer`/`NavGraph` (Task 18).

- [ ] **Step 1: Write the failing test**

`app/src/test/java/com/example/myapplication/ui/favorites/FavoritesViewModelTest.kt`:

```kotlin
package com.example.myapplication.ui.favorites

import com.example.myapplication.MainDispatcherRule
import com.example.myapplication.data.local.FakeFavoriteDao
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `favorites reflects what is stored in the repository`() = runTest {
        val repository = FavoriteRepository(FakeFavoriteDao())
        repository.addFavorite(PracticeQuestion("힌트", "I live in Seoul.", PracticeCategory.HOUSING))
        val viewModel = FavoritesViewModel(repository)

        advanceUntilIdle()

        assertEquals(1, viewModel.favorites.value.size)
        assertEquals("I live in Seoul.", viewModel.favorites.value[0].englishSentence)
    }

    @Test
    fun `removeFavorite deletes the item from the repository`() = runTest {
        val repository = FavoriteRepository(FakeFavoriteDao())
        repository.addFavorite(PracticeQuestion("힌트", "I live in Seoul.", PracticeCategory.HOUSING))
        val viewModel = FavoritesViewModel(repository)
        advanceUntilIdle()
        val stored = viewModel.favorites.value.first()

        viewModel.removeFavorite(stored)
        advanceUntilIdle()

        assertTrue(viewModel.favorites.value.isEmpty())
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "com.example.myapplication.ui.favorites.FavoritesViewModelTest"`
Expected: FAIL (compilation error — `FavoritesViewModel` is unresolved).

- [ ] **Step 3: Implement `FavoritesViewModel`**

`app/src/main/java/com/example/myapplication/ui/favorites/FavoritesViewModel.kt`:

```kotlin
package com.example.myapplication.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.local.FavoriteSentence
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteSentence>> = favoriteRepository.observeFavorites()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun removeFavorite(favorite: FavoriteSentence) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(favorite)
        }
    }
}
```

- [ ] **Step 4: Run the test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.example.myapplication.ui.favorites.FavoritesViewModelTest"`
Expected: PASS (2 tests)

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/myapplication/ui/favorites/FavoritesViewModel.kt app/src/test/java/com/example/myapplication/ui/favorites/FavoritesViewModelTest.kt
git commit -m "Add FavoritesViewModel"
```

---

### Task 14: `HomeScreen`

**Files:**
- Create: `app/src/main/java/com/example/myapplication/ui/home/HomeScreen.kt`
- Test: `app/src/androidTest/java/com/example/myapplication/ui/home/HomeScreenTest.kt`

**Interfaces:**
- Consumes: `PracticeCategory` (Task 2).
- Produces: `@Composable fun HomeScreen(onCategorySelected: (PracticeCategory) -> Unit, onOpenFavorites: () -> Unit, onOpenSettings: () -> Unit, modifier: Modifier = Modifier)`. Consumed by `NavGraph` (Task 18).

- [ ] **Step 1: Write the failing test**

`app/src/androidTest/java/com/example/myapplication/ui/home/HomeScreenTest.kt`:

```kotlin
package com.example.myapplication.ui.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.myapplication.data.model.PracticeCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tappingACategory_invokesCallbackWithThatCategory() {
        var selected: PracticeCategory? = null
        composeTestRule.setContent {
            HomeScreen(
                onCategorySelected = { selected = it },
                onOpenFavorites = {},
                onOpenSettings = {}
            )
        }

        composeTestRule.onNodeWithText(PracticeCategory.HOBBY.koreanLabel).performClick()

        assertEquals(PracticeCategory.HOBBY, selected)
    }

    @Test
    fun tappingFavoritesButton_invokesCallback() {
        var favoritesOpened = false
        composeTestRule.setContent {
            HomeScreen(
                onCategorySelected = {},
                onOpenFavorites = { favoritesOpened = true },
                onOpenSettings = {}
            )
        }

        composeTestRule.onNodeWithText("즐겨찾기").performClick()

        assertTrue(favoritesOpened)
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.home.HomeScreenTest`
Expected: FAIL (compilation error — `HomeScreen` is unresolved).

- [ ] **Step 3: Implement `HomeScreen`**

`app/src/main/java/com/example/myapplication/ui/home/HomeScreen.kt`:

```kotlin
package com.example.myapplication.ui.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.PracticeCategory

@Composable
fun HomeScreen(
    onCategorySelected: (PracticeCategory) -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("오픽 스피킹 연습") },
                actions = {
                    TextButton(onClick = onOpenFavorites) { Text("즐겨찾기") }
                    TextButton(onClick = onOpenSettings) { Text("설정") }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(PracticeCategory.entries) { category ->
                Card(
                    onClick = { onCategorySelected(category) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(text = category.koreanLabel, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}
```

- [ ] **Step 4: Run the test to verify it passes**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.home.HomeScreenTest`
Expected: PASS (2 tests)

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/myapplication/ui/home/HomeScreen.kt app/src/androidTest/java/com/example/myapplication/ui/home/HomeScreenTest.kt
git commit -m "Add HomeScreen with category list"
```

---

### Task 15: `SettingsScreen`

**Files:**
- Create: `app/src/main/java/com/example/myapplication/ui/settings/SettingsScreen.kt`
- Test: `app/src/androidTest/java/com/example/myapplication/ui/settings/SettingsScreenTest.kt`

**Interfaces:**
- Consumes: `SettingsViewModel`/`SettingsUiState` (Task 12).
- Produces: `@Composable fun SettingsScreen(viewModel: SettingsViewModel, modifier: Modifier = Modifier)`. Consumed by `NavGraph` (Task 18).

- [ ] **Step 1: Write the failing test**

`app/src/androidTest/java/com/example/myapplication/ui/settings/SettingsScreenTest.kt`:

```kotlin
package com.example.myapplication.ui.settings

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.myapplication.data.settings.ApiKeyStore
import org.junit.Rule
import org.junit.Test

private class FakeApiKeyStore(private var key: String? = null) : ApiKeyStore {
    override fun getApiKey(): String? = key
    override fun setApiKey(apiKey: String) { key = apiKey }
    override fun clearApiKey() { key = null }
}

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun enteringAKeyAndTappingSave_showsConfirmation() {
        val viewModel = SettingsViewModel(FakeApiKeyStore())
        composeTestRule.setContent {
            SettingsScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("apiKeyInput").performTextInput("sk-ant-test")
        composeTestRule.onNodeWithText("저장").performClick()

        composeTestRule.onNodeWithText("저장되었습니다.").assertExists()
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.settings.SettingsScreenTest`
Expected: FAIL (compilation error — `SettingsScreen` is unresolved).

- [ ] **Step 3: Implement `SettingsScreen`**

`app/src/main/java/com/example/myapplication/ui/settings/SettingsScreen.kt`:

```kotlin
package com.example.myapplication.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("설정") }) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text("Anthropic API 키")
            OutlinedTextField(
                value = uiState.apiKeyInput,
                onValueChange = viewModel::onApiKeyChanged,
                modifier = Modifier.fillMaxWidth().testTag("apiKeyInput")
            )
            Button(
                onClick = viewModel::saveApiKey,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("저장")
            }
            if (uiState.isSaved) {
                Text("저장되었습니다.")
            }
        }
    }
}
```

- [ ] **Step 4: Run the test to verify it passes**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.settings.SettingsScreenTest`
Expected: PASS (1 test)

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/myapplication/ui/settings/SettingsScreen.kt app/src/androidTest/java/com/example/myapplication/ui/settings/SettingsScreenTest.kt
git commit -m "Add SettingsScreen for API key entry"
```

---

### Task 16: `PracticeScreen` and `SetCompleteScreen`

**Files:**
- Create: `app/src/main/java/com/example/myapplication/ui/set/PracticeScreen.kt`
- Create: `app/src/main/java/com/example/myapplication/ui/set/SetCompleteScreen.kt`
- Test: `app/src/androidTest/java/com/example/myapplication/ui/set/PracticeScreenTest.kt`
- Test: `app/src/androidTest/java/com/example/myapplication/ui/set/SetCompleteScreenTest.kt`

**Interfaces:**
- Consumes: `PracticeSetViewModel`/`PracticeSetUiState` (Task 11), `AppDatabase`/`FavoriteRepository` (Task 7).
- Produces: `@Composable fun PracticeScreen(viewModel: PracticeSetViewModel, onSetComplete: (Int) -> Unit, onBack: () -> Unit, modifier: Modifier = Modifier)` and `@Composable fun SetCompleteScreen(questionCount: Int, onBackToHome: () -> Unit, modifier: Modifier = Modifier)`. Consumed by `NavGraph` (Task 18).

- [ ] **Step 1: Write the failing tests**

`app/src/androidTest/java/com/example/myapplication/ui/set/PracticeScreenTest.kt`:

```kotlin
package com.example.myapplication.ui.set

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.audio.VoicePlayer
import com.example.myapplication.audio.VoiceRecorder
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import com.example.myapplication.data.remote.PracticeRepository
import com.example.myapplication.data.settings.ApiKeyStore
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

private class FakePracticeRepository(
    private val result: Result<List<PracticeQuestion>>
) : PracticeRepository {
    override suspend fun generateSet(
        apiKey: String,
        category: PracticeCategory,
        alreadyAskedQuestions: List<String>,
        questionCount: Int
    ): Result<List<PracticeQuestion>> = result
}

private class FakeApiKeyStore(private var key: String? = "test-key") : ApiKeyStore {
    override fun getApiKey(): String? = key
    override fun setApiKey(apiKey: String) { key = apiKey }
    override fun clearApiKey() { key = null }
}

private class FakeSpeechPlayer : SpeechPlayer {
    override fun speak(text: String) {}
    override fun release() {}
}

private class FakeVoiceRecorder : VoiceRecorder {
    private var recording = false
    override fun startRecording(outputFile: File) { recording = true }
    override fun stopRecording() { recording = false }
    override fun isRecording(): Boolean = recording
}

private class FakeVoicePlayer : VoicePlayer {
    override fun play(file: File) {}
    override fun stop() {}
}

class PracticeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun buildViewModel(questionCount: Int = 2): PracticeSetViewModel {
        val questions = (1..questionCount).map {
            PracticeQuestion("힌트$it", "Sentence $it.", PracticeCategory.HOBBY)
        }
        return PracticeSetViewModel(
            category = PracticeCategory.HOBBY,
            practiceRepository = FakePracticeRepository(Result.success(questions)),
            favoriteRepository = FavoriteRepository(database.favoriteDao()),
            apiKeyStore = FakeApiKeyStore(),
            speechPlayer = FakeSpeechPlayer(),
            voiceRecorder = FakeVoiceRecorder(),
            voicePlayer = FakeVoicePlayer(),
            recordingFile = File.createTempFile("practice_screen_test", ".m4a").apply { deleteOnExit() }
        )
    }

    @Test
    fun recordingAndStopping_revealsModelSentenceAndFavoriteButton() {
        val viewModel = buildViewModel()
        composeTestRule.setContent {
            PracticeScreen(viewModel = viewModel, onSetComplete = {}, onBack = {})
        }

        composeTestRule.onNodeWithText("힌트1").assertExists()
        composeTestRule.onNodeWithText("녹음 시작").performClick()
        composeTestRule.onNodeWithText("녹음 중지").performClick()

        composeTestRule.onNodeWithText("Sentence 1.").assertExists()
        composeTestRule.onNodeWithText("☆ 즐겨찾기").assertExists()
    }

    @Test
    fun tappingNextOnLastQuestion_triggersOnSetComplete() {
        var completed = false
        val viewModel = buildViewModel(questionCount = 1)
        composeTestRule.setContent {
            PracticeScreen(viewModel = viewModel, onSetComplete = { completed = true }, onBack = {})
        }

        composeTestRule.onNodeWithText("다음 문제").performClick()

        composeTestRule.waitUntil(timeoutMillis = 2_000) { completed }
    }
}
```

`app/src/androidTest/java/com/example/myapplication/ui/set/SetCompleteScreenTest.kt`:

```kotlin
package com.example.myapplication.ui.set

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SetCompleteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tappingBackToHome_invokesCallback() {
        var backPressed = false
        composeTestRule.setContent {
            SetCompleteScreen(questionCount = 5, onBackToHome = { backPressed = true })
        }

        composeTestRule.onNodeWithText("오늘 5문장을 연습했습니다. 수고하셨습니다!").assertExists()
        composeTestRule.onNodeWithText("홈으로").performClick()

        assertTrue(backPressed)
    }
}
```

- [ ] **Step 2: Run the tests to verify they fail**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.set.PracticeScreenTest,com.example.myapplication.ui.set.SetCompleteScreenTest`
Expected: FAIL (compilation error — `PracticeScreen` and `SetCompleteScreen` are unresolved).

- [ ] **Step 3: Implement `PracticeScreen`**

`app/src/main/java/com/example/myapplication/ui/set/PracticeScreen.kt`:

```kotlin
package com.example.myapplication.ui.set

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PracticeScreen(
    viewModel: PracticeSetViewModel,
    onSetComplete: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("오늘의 세트") }) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            when (val state = uiState) {
                is PracticeSetUiState.Loading -> Text("문제를 만드는 중입니다...")
                is PracticeSetUiState.Error -> {
                    Text(state.message)
                    Button(onClick = viewModel::loadSet) { Text("다시 시도") }
                }
                is PracticeSetUiState.Ready -> {
                    if (state.isSetComplete) {
                        LaunchedEffect(Unit) { onSetComplete(state.questions.size) }
                    } else {
                        PracticeReadyContent(state = state, viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun PracticeReadyContent(
    state: PracticeSetUiState.Ready,
    viewModel: PracticeSetViewModel
) {
    val question = state.questions[state.currentIndex]

    Text("${state.currentIndex + 1} / ${state.questions.size}")
    Text(question.koreanHint, modifier = Modifier.padding(vertical = 16.dp))

    Button(onClick = {
        if (state.isRecording) viewModel.stopRecording() else viewModel.startRecording()
    }) {
        Text(if (state.isRecording) "녹음 중지" else "녹음 시작")
    }

    if (state.hasRecording) {
        Button(onClick = viewModel::playMyRecording) { Text("내 녹음 듣기") }
        Button(onClick = viewModel::playModelSentence) { Text("모범 문장 듣기") }
        Text(question.englishSentence, modifier = Modifier.padding(vertical = 8.dp))
    }

    Button(onClick = viewModel::toggleFavorite) {
        Text(if (state.isCurrentFavorite) "★ 즐겨찾기 해제" else "☆ 즐겨찾기")
    }

    Button(onClick = viewModel::nextQuestion) { Text("다음 문제") }
}
```

- [ ] **Step 4: Implement `SetCompleteScreen`**

`app/src/main/java/com/example/myapplication/ui/set/SetCompleteScreen.kt`:

```kotlin
package com.example.myapplication.ui.set

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SetCompleteScreen(
    questionCount: Int,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("세트 완료") }) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text("오늘 ${questionCount}문장을 연습했습니다. 수고하셨습니다!")
            Button(onClick = onBackToHome, modifier = Modifier.padding(top = 16.dp)) {
                Text("홈으로")
            }
        }
    }
}
```

- [ ] **Step 5: Run the tests to verify they pass**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.set.PracticeScreenTest,com.example.myapplication.ui.set.SetCompleteScreenTest`
Expected: PASS (3 tests)

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/example/myapplication/ui/set/PracticeScreen.kt app/src/main/java/com/example/myapplication/ui/set/SetCompleteScreen.kt app/src/androidTest/java/com/example/myapplication/ui/set/PracticeScreenTest.kt app/src/androidTest/java/com/example/myapplication/ui/set/SetCompleteScreenTest.kt
git commit -m "Add PracticeScreen and SetCompleteScreen"
```

---

### Task 17: `FavoritesScreen`

**Files:**
- Create: `app/src/main/java/com/example/myapplication/ui/favorites/FavoritesScreen.kt`
- Test: `app/src/androidTest/java/com/example/myapplication/ui/favorites/FavoritesScreenTest.kt`

**Interfaces:**
- Consumes: `FavoritesViewModel` (Task 13), `AppDatabase`/`FavoriteRepository` (Task 7).
- Produces: `@Composable fun FavoritesScreen(viewModel: FavoritesViewModel, modifier: Modifier = Modifier)`. Consumed by `NavGraph` (Task 18).

- [ ] **Step 1: Write the failing test**

`app/src/androidTest/java/com/example/myapplication/ui/favorites/FavoritesScreenTest.kt`:

```kotlin
package com.example.myapplication.ui.favorites

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoritesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun deletingAFavorite_removesItFromTheList() = runBlocking {
        val repository = FavoriteRepository(database.favoriteDao())
        repository.addFavorite(PracticeQuestion("힌트", "I live in Seoul.", PracticeCategory.HOUSING))
        val viewModel = FavoritesViewModel(repository)

        composeTestRule.setContent {
            FavoritesScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("I live in Seoul.").assertExists()
        composeTestRule.onNodeWithText("삭제").performClick()
        composeTestRule.waitUntil(timeoutMillis = 2_000) {
            composeTestRule.onAllNodesWithText("I live in Seoul.").fetchSemanticsNodes().isEmpty()
        }
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.favorites.FavoritesScreenTest`
Expected: FAIL (compilation error — `FavoritesScreen` is unresolved).

- [ ] **Step 3: Implement `FavoritesScreen`**

`app/src/main/java/com/example/myapplication/ui/favorites/FavoritesScreen.kt`:

```kotlin
package com.example.myapplication.ui.favorites

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    modifier: Modifier = Modifier
) {
    val favorites by viewModel.favorites.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("즐겨찾기") }) }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(favorites, key = { it.id }) { favorite ->
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(favorite.koreanHint, modifier = Modifier.padding(16.dp))
                    Text(favorite.englishSentence, modifier = Modifier.padding(horizontal = 16.dp))
                    TextButton(onClick = { viewModel.removeFavorite(favorite) }) {
                        Text("삭제")
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 4: Run the test to verify it passes**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.favorites.FavoritesScreenTest`
Expected: PASS (1 test)

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/myapplication/ui/favorites/FavoritesScreen.kt app/src/androidTest/java/com/example/myapplication/ui/favorites/FavoritesScreenTest.kt
git commit -m "Add FavoritesScreen"
```

---

### Task 18: Navigation graph, dependency wiring, and `MainActivity`

**Files:**
- Create: `app/src/main/java/com/example/myapplication/ui/navigation/Routes.kt`
- Create: `app/src/main/java/com/example/myapplication/ui/navigation/SimpleViewModelFactory.kt`
- Create: `app/src/main/java/com/example/myapplication/ui/navigation/AppNavGraph.kt`
- Create: `app/src/main/java/com/example/myapplication/di/AppContainer.kt`
- Modify: `app/src/main/java/com/example/myapplication/MainActivity.kt`
- Test: `app/src/androidTest/java/com/example/myapplication/MainActivityTest.kt`

**Interfaces:**
- Consumes: every screen and ViewModel from Tasks 6–17.
- Produces: the fully wired app. `AppContainer` is the composition root; `AppNavGraph` is the single entry point rendered by `MainActivity`.

- [ ] **Step 1: Write the failing test**

`app/src/androidTest/java/com/example/myapplication/MainActivityTest.kt`:

```kotlin
package com.example.myapplication

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchingTheApp_showsTheHomeScreenTitle() {
        composeTestRule.onNodeWithText("오픽 스피킹 연습").assertExists()
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.MainActivityTest`
Expected: FAIL (`MainActivity` still renders the old "Hello Android" screen, so the title text is not found).

- [ ] **Step 3: Implement `Routes`**

`app/src/main/java/com/example/myapplication/ui/navigation/Routes.kt`:

```kotlin
package com.example.myapplication.ui.navigation

import com.example.myapplication.data.model.PracticeCategory

sealed class Routes(val route: String) {
    data object Home : Routes("home")
    data object Favorites : Routes("favorites")
    data object Settings : Routes("settings")

    data object Practice : Routes("practice/{category}") {
        fun createRoute(category: PracticeCategory) = "practice/${category.name}"
    }

    data object SetComplete : Routes("set_complete/{questionCount}") {
        fun createRoute(questionCount: Int) = "set_complete/$questionCount"
    }
}
```

- [ ] **Step 4: Implement `SimpleViewModelFactory`**

`app/src/main/java/com/example/myapplication/ui/navigation/SimpleViewModelFactory.kt`:

```kotlin
package com.example.myapplication.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SimpleViewModelFactory<T : ViewModel>(
    private val create: () -> T
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = create() as VM
}
```

- [ ] **Step 5: Implement `AppContainer`**

`app/src/main/java/com/example/myapplication/di/AppContainer.kt`:

```kotlin
package com.example.myapplication.di

import android.content.Context
import androidx.room.Room
import com.example.myapplication.audio.AndroidSpeechPlayer
import com.example.myapplication.audio.MediaPlayerVoicePlayer
import com.example.myapplication.audio.MediaRecorderVoiceRecorder
import com.example.myapplication.audio.SpeechPlayer
import com.example.myapplication.audio.VoicePlayer
import com.example.myapplication.audio.VoiceRecorder
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.FavoriteRepository
import com.example.myapplication.data.remote.ClaudeApiClient
import com.example.myapplication.data.remote.ClaudePracticeRepository
import com.example.myapplication.data.remote.PracticeRepository
import com.example.myapplication.data.settings.ApiKeyStore
import com.example.myapplication.data.settings.EncryptedApiKeyStore
import java.io.File

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val database = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java,
        "opic_practice.db"
    ).build()

    val favoriteRepository = FavoriteRepository(database.favoriteDao())

    val apiKeyStore: ApiKeyStore = EncryptedApiKeyStore(appContext)

    val practiceRepository: PracticeRepository = ClaudePracticeRepository(ClaudeApiClient())

    fun newSpeechPlayer(): SpeechPlayer = AndroidSpeechPlayer(appContext)

    fun newVoiceRecorder(): VoiceRecorder = MediaRecorderVoiceRecorder(appContext)

    fun newVoicePlayer(): VoicePlayer = MediaPlayerVoicePlayer()

    fun newRecordingFile(): File = File(appContext.cacheDir, "current_recording.m4a")
}
```

- [ ] **Step 6: Implement `AppNavGraph`**

`app/src/main/java/com/example/myapplication/ui/navigation/AppNavGraph.kt`:

```kotlin
package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.di.AppContainer
import com.example.myapplication.ui.favorites.FavoritesScreen
import com.example.myapplication.ui.favorites.FavoritesViewModel
import com.example.myapplication.ui.home.HomeScreen
import com.example.myapplication.ui.set.PracticeScreen
import com.example.myapplication.ui.set.PracticeSetViewModel
import com.example.myapplication.ui.set.SetCompleteScreen
import com.example.myapplication.ui.settings.SettingsScreen
import com.example.myapplication.ui.settings.SettingsViewModel

@Composable
fun AppNavGraph(appContainer: AppContainer) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Home.route) {
        composable(Routes.Home.route) {
            HomeScreen(
                onCategorySelected = { category ->
                    navController.navigate(Routes.Practice.createRoute(category))
                },
                onOpenFavorites = { navController.navigate(Routes.Favorites.route) },
                onOpenSettings = { navController.navigate(Routes.Settings.route) }
            )
        }
        composable(
            route = Routes.Practice.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("category")
                ?: PracticeCategory.SELF_INTRODUCTION.name
            val category = PracticeCategory.valueOf(categoryName)
            val viewModel: PracticeSetViewModel = viewModel(
                factory = SimpleViewModelFactory {
                    PracticeSetViewModel(
                        category = category,
                        practiceRepository = appContainer.practiceRepository,
                        favoriteRepository = appContainer.favoriteRepository,
                        apiKeyStore = appContainer.apiKeyStore,
                        speechPlayer = appContainer.newSpeechPlayer(),
                        voiceRecorder = appContainer.newVoiceRecorder(),
                        voicePlayer = appContainer.newVoicePlayer(),
                        recordingFile = appContainer.newRecordingFile()
                    )
                }
            )
            PracticeScreen(
                viewModel = viewModel,
                onSetComplete = { questionCount ->
                    navController.navigate(Routes.SetComplete.createRoute(questionCount)) {
                        popUpTo(Routes.Home.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.SetComplete.route,
            arguments = listOf(navArgument("questionCount") { type = NavType.IntType })
        ) { backStackEntry ->
            val questionCount = backStackEntry.arguments?.getInt("questionCount") ?: 0
            SetCompleteScreen(
                questionCount = questionCount,
                onBackToHome = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Favorites.route) {
            val viewModel: FavoritesViewModel = viewModel(
                factory = SimpleViewModelFactory { FavoritesViewModel(appContainer.favoriteRepository) }
            )
            FavoritesScreen(viewModel = viewModel)
        }
        composable(Routes.Settings.route) {
            val viewModel: SettingsViewModel = viewModel(
                factory = SimpleViewModelFactory { SettingsViewModel(appContainer.apiKeyStore) }
            )
            SettingsScreen(viewModel = viewModel)
        }
    }
}
```

- [ ] **Step 7: Rewrite `MainActivity`**

`app/src/main/java/com/example/myapplication/MainActivity.kt`:

```kotlin
package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.di.AppContainer
import com.example.myapplication.ui.navigation.AppNavGraph
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val appContainer by lazy { AppContainer(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppNavGraph(appContainer = appContainer)
            }
        }
    }
}
```

- [ ] **Step 8: Run the test to verify it passes**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.MainActivityTest`
Expected: PASS (1 test)

- [ ] **Step 9: Run the full test suite**

Run: `./gradlew :app:testDebugUnitTest :app:connectedDebugAndroidTest`
Expected: BUILD SUCCESSFUL, all unit and instrumented tests from Tasks 1–18 pass.

- [ ] **Step 10: Commit**

```bash
git add app/src/main/java/com/example/myapplication/ui/navigation app/src/main/java/com/example/myapplication/di app/src/main/java/com/example/myapplication/MainActivity.kt app/src/androidTest/java/com/example/myapplication/MainActivityTest.kt
git commit -m "Wire navigation graph and dependency container into MainActivity"
```
