# Practice Experience Completion Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete the practice loop by generating and showing real OPIc questions, avoiding duplicates for the current app process, summarizing completed sets, and supporting both instant TTS and full speaking practice from favorites.

**Architecture:** Add a process-memory `PracticeSessionStore` shared by the repository and set ViewModels, while Room continues to persist only favorites. The repository owns the two-call generation budget and atomic unique-question acceptance. A stateless `SpeakingPracticeContent` is shared by normal and favorite practice screens; their ViewModels keep separate state and lifecycles.

**Tech Stack:** Kotlin 2.2.10, Android/Jetpack Compose Material 3, Room 2.8.4, coroutines/StateFlow, kotlinx.serialization, Android TextToSpeech/MediaRecorder/MediaPlayer, JUnit 4, MockWebServer, AndroidX Room and Compose testing.

## Global Constraints

- The generated item schema contains exact JSON keys `opic_question`, `korean_hint`, and `english_sentence`.
- Question history exists only for the current app process; do not persist it in Room or preferences.
- Normalize duplicate questions by trimming, collapsing consecutive whitespace, and lowercasing with `Locale.ROOT`.
- A set-generation request may call exactly one selected AI provider at most twice total; never fall back to another provider.
- Authentication, rate-limit, network, and other provider errors are not retried.
- Failed or incomplete generation must not add partial questions to session history.
- Room migrates explicitly from version 1 to 2 by adding nullable `opicQuestion TEXT`; existing favorite rows must survive.
- Existing favorites with no question remain usable and show `저장된 질문 없음 — 힌트를 보고 답해보세요.`.
- TTS unavailability disables speech actions but leaves text and recording practice usable.
- Temporary recordings are deleted when the question completes or its ViewModel is cleared.
- Do not add a DI framework or persist practice sets outside process memory.

---

## File Structure

- Modify `data/model/PracticeQuestion.kt`: add the actual OPIc question.
- Modify `data/remote/PracticeSetParser.kt` and `PromptBuilder.kt`: enforce and request the three-field schema.
- Create `data/session/PracticeSessionStore.kt`: normalized question history and completed-set snapshot.
- Modify `data/remote/PracticeRepository.kt`: two-call parsing/dedup/refill algorithm and atomic history write.
- Modify Room favorite entity/DAO/repository/database and add `DatabaseMigrations.kt`.
- Modify `audio/SpeechPlayer.kt` and `AndroidSpeechPlayer.kt`: observable speech availability.
- Create `ui/practice/SpeakingPracticeContent.kt`: stateless shared question/hint/record/play content.
- Modify normal practice ViewModel/screen and create completed-set ViewModel/state.
- Modify favorites ViewModel/screen for TTS and create favorite-practice ViewModel/state/screen.
- Modify routes, navigation, and `AppContainer` to share session state and build screen-scoped audio objects.

---

### Task 1: Actual OPIc Question Model, Prompt, and Parser

**Files:**
- Modify: `app/src/main/java/com/example/myapplication/data/model/PracticeQuestion.kt`
- Modify: `app/src/main/java/com/example/myapplication/data/remote/PromptBuilder.kt`
- Modify: `app/src/main/java/com/example/myapplication/data/remote/PracticeSetParser.kt`
- Modify: `app/src/test/java/com/example/myapplication/data/remote/PromptBuilderTest.kt`
- Modify: `app/src/test/java/com/example/myapplication/data/remote/PracticeSetParserTest.kt`
- Modify: `app/src/androidTest/java/com/example/myapplication/data/local/FavoriteDaoTest.kt`
- Modify: `app/src/androidTest/java/com/example/myapplication/ui/favorites/FavoritesScreenTest.kt`
- Modify: `app/src/androidTest/java/com/example/myapplication/ui/set/PracticeScreenTest.kt`
- Modify: `app/src/test/java/com/example/myapplication/data/model/PracticeCategoryTest.kt`
- Modify: `app/src/test/java/com/example/myapplication/ui/favorites/FavoritesViewModelTest.kt`
- Modify: `app/src/test/java/com/example/myapplication/ui/set/PracticeSetViewModelTest.kt`

**Interfaces:**
- Consumes: existing `PracticeCategory` and pure prompt/parser functions.
- Produces: `PracticeQuestion(opicQuestion, koreanHint, englishSentence, category)` and a strict three-field JSON schema used by every later task.

- [ ] **Step 1: Write failing parser and prompt tests**

Use named arguments in all `PracticeQuestion` fixtures. Add these focused assertions:

```kotlin
private val validJson = """
    [{
      "opic_question": "Tell me about your home.",
      "korean_hint": "나는 산다 / 서울에",
      "english_sentence": "I live in Seoul."
    }]
""".trimIndent()

@Test
fun `parses actual OPIc question`() {
    val item = parsePracticeSet(validJson, PracticeCategory.HOUSING).single()
    assertEquals("Tell me about your home.", item.opicQuestion)
}

@Test
fun `rejects missing or blank OPIc question`() {
    val missing = """[{"korean_hint":"힌트","english_sentence":"Answer."}]"""
    val blank = """[{"opic_question":"  ","korean_hint":"힌트","english_sentence":"Answer."}]"""
    assertThrows(PracticeSetParseException::class.java) {
        parsePracticeSet(missing, PracticeCategory.HOUSING)
    }
    assertThrows(PracticeSetParseException::class.java) {
        parsePracticeSet(blank, PracticeCategory.HOUSING)
    }
}

@Test
fun `prompt requires exact three-field shape`() {
    val prompt = buildSetPrompt(PracticeCategory.HOUSING, emptyList(), 1)
    assertTrue(prompt.contains("\"opic_question\""))
    assertTrue(prompt.contains("\"korean_hint\""))
    assertTrue(prompt.contains("\"english_sentence\""))
}
```

- [ ] **Step 2: Run focused tests and verify RED**

Run: `./gradlew testDebugUnitTest --tests '*PracticeSetParserTest' --tests '*PromptBuilderTest'`

Expected: compilation/assertion failure because `opicQuestion` and `opic_question` are absent.

- [ ] **Step 3: Add the field and strict parser DTO**

```kotlin
data class PracticeQuestion(
    val opicQuestion: String,
    val koreanHint: String,
    val englishSentence: String,
    val category: PracticeCategory
)

@Serializable
private data class PracticeQuestionDto(
    val opic_question: String,
    val korean_hint: String,
    val english_sentence: String
)
```

In `parsePracticeSet`, reject an item when any of the three trimmed values is blank and map all three values. In `PromptBuilder`, update the example and exact output shape to contain the three keys. Keep remote input out of surfaced exception messages.

- [ ] **Step 4: Migrate every existing constructor to named arguments**

Use this exact fixture shape throughout unit and instrumentation tests:

```kotlin
PracticeQuestion(
    opicQuestion = "Tell me about item $index.",
    koreanHint = "힌트$index",
    englishSentence = "Sentence $index.",
    category = PracticeCategory.HOBBY
)
```

Update repository fake JSON responses to include `opic_question`; do not add a default value that permits production questions without it.

- [ ] **Step 5: Run parser, prompt, and full JVM tests**

Run: `./gradlew testDebugUnitTest --tests '*PracticeSetParserTest' --tests '*PromptBuilderTest'`

Run: `./gradlew testDebugUnitTest`

Expected: PASS.

- [ ] **Step 6: Commit the schema change**

```bash
git add app/src/main/java/com/example/myapplication/data/model/PracticeQuestion.kt app/src/main/java/com/example/myapplication/data/remote/PracticeSetParser.kt app/src/main/java/com/example/myapplication/data/remote/PromptBuilder.kt app/src/test app/src/androidTest
git commit -m "Add actual OPIc questions to practice sets"
```

---

### Task 2: Process Session Store and Two-Call Unique Generation

**Files:**
- Create: `app/src/main/java/com/example/myapplication/data/session/PracticeSessionStore.kt`
- Create: `app/src/test/java/com/example/myapplication/data/session/PracticeSessionStoreTest.kt`
- Modify: `app/src/main/java/com/example/myapplication/data/remote/AiProvider.kt`
- Modify: `app/src/main/java/com/example/myapplication/data/remote/PracticeRepository.kt`
- Modify: `app/src/test/java/com/example/myapplication/data/remote/PracticeRepositoryTest.kt`
- Modify: `app/src/main/java/com/example/myapplication/di/AppContainer.kt`

**Interfaces:**
- Consumes: three-field `PracticeQuestion`, `PracticeAiProvider`, `AiSettingsStore`, `buildSetPrompt`, `parsePracticeSet`.
- Produces: `PracticeSessionStore`, `CompletedPracticeSet`, `CompletedPracticeItem`, `InsufficientUniqueQuestions`, and `PracticeRepository.generateSet(category, questionCount)` with session history internalized.

- [ ] **Step 1: Write failing session normalization and atomicity tests**

```kotlin
class PracticeSessionStoreTest {
    @Test fun `normalizes case trim and repeated whitespace`() {
        val store = PracticeSessionStore()
        store.addAskedQuestions(listOf("  Tell   Me About Your HOME. "))
        assertTrue(store.containsQuestion("tell me about your home."))
    }

    @Test fun `new store starts with empty process history`() {
        val first = PracticeSessionStore().apply { addAskedQuestions(listOf("Question")) }
        val second = PracticeSessionStore()
        assertEquals(1, first.askedQuestions().size)
        assertTrue(second.askedQuestions().isEmpty())
    }
}
```

- [ ] **Step 2: Run session tests and verify RED**

Run: `./gradlew testDebugUnitTest --tests '*PracticeSessionStoreTest'`

Expected: compilation failure for missing session types.

- [ ] **Step 3: Implement the in-memory store and immutable completion snapshot**

```kotlin
data class CompletedPracticeItem(
    val question: PracticeQuestion,
    val isFavorite: Boolean
)

data class CompletedPracticeSet(val items: List<CompletedPracticeItem>)

class PracticeSessionStore {
    private val normalizedAsked = linkedSetOf<String>()
    private var completed: CompletedPracticeSet? = null

    fun askedQuestions(): List<String> = normalizedAsked.toList()
    fun containsQuestion(question: String): Boolean = normalizeQuestion(question) in normalizedAsked
    fun addAskedQuestions(questions: List<String>) {
        normalizedAsked += questions.map(::normalizeQuestion)
    }
    fun saveCompletedSet(value: CompletedPracticeSet) { completed = value.copy(items = value.items.toList()) }
    fun lastCompletedSet(): CompletedPracticeSet? = completed
}

internal fun normalizeQuestion(value: String): String =
    value.trim().replace(Regex("\\s+"), " ").lowercase(Locale.ROOT)
```

- [ ] **Step 4: Write failing repository tests for dedup, refill, budget, and atomic history**

Use a fake provider that records every prompt and returns queued results. Define these helpers before the tests:

```kotlin
private fun json(vararg questions: String): String = questions.joinToString(
    prefix = "[", postfix = "]", separator = ","
) { question ->
    """{"opic_question":"$question","korean_hint":"힌트","english_sentence":"Answer for $question"}"""
}

private fun repository(
    store: PracticeSessionStore,
    selectedProvider: PracticeAiProvider
): PracticeRepository = DefaultPracticeRepository(
    providers = setOf(selectedProvider),
    settingsStore = FakeAiSettingsStore(
        selected = selectedProvider.provider,
        keys = mutableMapOf(selectedProvider.provider to "test-key")
    ),
    sessionStore = store
)
```

Add exact cases:

```kotlin
@Test fun `filters session and response duplicates then refills once`() = runTest {
    val store = PracticeSessionStore().apply { addAskedQuestions(listOf("Old question")) }
    val provider = FakeProvider(AiProvider.OPENAI, listOf(
        Result.success(json("Old question", "New question", " new   QUESTION ")),
        Result.success(json("Another question"))
    ))
    val result = repository(store, provider).generateSet(PracticeCategory.HOUSING, 2)
    assertEquals(listOf("New question", "Another question"),
        result.getOrThrow().map(PracticeQuestion::opicQuestion))
    assertEquals(2, provider.callCount)
    assertTrue(provider.prompts[1].contains("new question", ignoreCase = true))
}

@Test fun `two malformed responses consume only two calls and do not update history`() = runTest {
    val store = PracticeSessionStore()
    val provider = FakeProvider(AiProvider.OPENAI,
        listOf(Result.success("bad"), Result.success("still bad")))
    val error = repository(store, provider).generateSet(PracticeCategory.HOUSING, 2).exceptionOrNull()
    assertTrue(error is InvalidPracticeSet)
    assertEquals(2, provider.callCount)
    assertTrue(store.askedQuestions().isEmpty())
}

@Test fun `insufficient second response returns typed failure without partial history`() = runTest {
    val store = PracticeSessionStore()
    val provider = FakeProvider(AiProvider.OPENAI, listOf(
        Result.success(json("Only one")), Result.success(json(" only   ONE "))
    ))
    val error = repository(store, provider).generateSet(PracticeCategory.HOUSING, 2).exceptionOrNull()
    assertTrue(error is InsufficientUniqueQuestions)
    assertEquals(2, provider.callCount)
    assertTrue(store.askedQuestions().isEmpty())
}
```

Retain tests for selected-provider-only routing, key lookup, provider-error no retry, safe cause chains, and cancellation rethrow.

- [ ] **Step 5: Implement the two-call algorithm**

Add:

```kotlin
class InsufficientUniqueQuestions : PracticeGenerationException(
    "서로 다른 문제를 충분히 만들지 못했습니다. 다시 시도해주세요."
)
```

Change the public repository contract to:

```kotlin
suspend fun generateSet(
    category: PracticeCategory,
    questionCount: Int = 5
): Result<List<PracticeQuestion>>
```

For attempts `0 until 2`, build a prompt using session history plus accepted questions, parse the response, normalize-filter duplicates, and stop once `questionCount` is reached. A parse error consumes that attempt and continues only when one attempt remains. Any provider exception exits immediately. Add accepted questions to the store only after a complete set is assembled.

- [ ] **Step 6: Wire one shared store in AppContainer and mechanically migrate callers**

```kotlin
val practiceSessionStore = PracticeSessionStore()

val practiceRepository: PracticeRepository = DefaultPracticeRepository(
    providers = setOf(ClaudeApiClient(), OpenAiApiClient()),
    settingsStore = aiSettingsStore,
    sessionStore = practiceSessionStore
)
```

Remove `alreadyAskedQuestions` at every repository call/fake signature. Task 5 will inject the same store into `PracticeSetViewModel` for completion snapshots.

- [ ] **Step 7: Run focused and full JVM suites**

Run: `./gradlew testDebugUnitTest --tests '*PracticeSessionStoreTest' --tests '*PracticeRepositoryTest'`

Run: `./gradlew testDebugUnitTest`

Expected: PASS and provider call counts never exceed two.

- [ ] **Step 8: Commit session generation**

```bash
git add app/src/main/java/com/example/myapplication/data/session app/src/main/java/com/example/myapplication/data/remote app/src/main/java/com/example/myapplication/di/AppContainer.kt app/src/test app/src/androidTest
git commit -m "Avoid duplicate questions during app sessions"
```

---

### Task 3: Favorite Question Persistence and Room 1→2 Migration

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`
- Modify: `app/src/main/java/com/example/myapplication/data/local/FavoriteSentence.kt`
- Modify: `app/src/main/java/com/example/myapplication/data/local/FavoriteDao.kt`
- Modify: `app/src/main/java/com/example/myapplication/data/local/FavoriteRepository.kt`
- Create: `app/src/main/java/com/example/myapplication/data/local/DatabaseMigrations.kt`
- Modify: `app/src/main/java/com/example/myapplication/data/local/AppDatabase.kt`
- Modify: `app/src/main/java/com/example/myapplication/di/AppContainer.kt`
- Create: `app/src/androidTest/java/com/example/myapplication/data/local/AppDatabaseMigrationTest.kt`
- Modify: existing favorite DAO/repository tests and fakes.

**Interfaces:**
- Consumes: `PracticeQuestion.opicQuestion`.
- Produces: Room version 2, `FavoriteSentence.opicQuestion: String?`, `FavoriteRepository.getFavorite(id)`, and `MIGRATION_1_2`.

- [ ] **Step 1: Add Room migration-test dependency and schema export directory**

```toml
androidx-room-testing = { group = "androidx.room", name = "room-testing", version.ref = "room" }
```

```kotlin
androidTestImplementation(libs.androidx.room.testing)
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
```

Set `exportSchema = true` on `AppDatabase` so `MigrationTestHelper` can validate version 1→2.
Before changing the entity or database version, run `./gradlew kspDebugKotlin` and verify that
`app/schemas/com.example.myapplication.data.local.AppDatabase/1.json` is created. Keep that version-1
schema file, then make the version-2 changes in Step 4 and run `./gradlew kspDebugKotlin` again to create
`2.json`.

- [ ] **Step 2: Write the failing migration and persistence tests**

```kotlin
@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {
    @get:Rule val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(), AppDatabase::class.java
    )

    @Test fun migration1To2_preservesExistingFavoriteWithNullQuestion() {
        helper.createDatabase("migration-test", 1).apply {
            execSQL("INSERT INTO favorite_sentences (id, category, koreanHint, englishSentence, createdAt) VALUES (1, 'HOUSING', '힌트', 'Answer.', 1)")
            close()
        }
        helper.runMigrationsAndValidate("migration-test", 2, true, MIGRATION_1_2).use { db ->
            db.query("SELECT opicQuestion, englishSentence FROM favorite_sentences WHERE id = 1").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertTrue(cursor.isNull(0))
                assertEquals("Answer.", cursor.getString(1))
            }
        }
    }
}
```

Add DAO tests that insert/query a non-null question and repository tests that verify `addFavorite` copies it.

- [ ] **Step 3: Run the migration test and verify RED**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.data.local.AppDatabaseMigrationTest`

Expected: failure because schema version 2 and `MIGRATION_1_2` do not exist.

- [ ] **Step 4: Implement schema, DAO lookup, repository mapping, and migration**

```kotlin
data class FavoriteSentence(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val opicQuestion: String? = null,
    val koreanHint: String,
    val englishSentence: String,
    val createdAt: Long
)

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE favorite_sentences ADD COLUMN opicQuestion TEXT")
    }
}
```

Add `@Query("SELECT * FROM favorite_sentences WHERE id = :id LIMIT 1") suspend fun findById(id: Long): FavoriteSentence?`, expose it through `FavoriteRepository.getFavorite(id)`, copy `opicQuestion` when adding, set database version 2, and add `.addMigrations(MIGRATION_1_2)` in `AppContainer`.

- [ ] **Step 5: Run Room/favorite tests**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.data.local.AppDatabaseMigrationTest,com.example.myapplication.data.local.FavoriteDaoTest`

Run: `./gradlew testDebugUnitTest --tests '*FavoritesViewModelTest'`

Expected: PASS with old row preserved and new question round-tripped.

- [ ] **Step 6: Commit Room migration**

```bash
git add gradle/libs.versions.toml app/build.gradle.kts app/schemas app/src/main/java/com/example/myapplication/data/local app/src/main/java/com/example/myapplication/di/AppContainer.kt app/src/test app/src/androidTest
git commit -m "Persist questions with favorite sentences"
```

---

### Task 4: Observable TTS Availability

**Files:**
- Modify: `app/src/main/java/com/example/myapplication/audio/SpeechPlayer.kt`
- Modify: `app/src/main/java/com/example/myapplication/audio/AndroidSpeechPlayer.kt`
- Modify: `app/src/androidTest/java/com/example/myapplication/audio/AndroidSpeechPlayerTest.kt`
- Modify: all fake `SpeechPlayer` implementations in tests.

**Interfaces:**
- Consumes: Android `TextToSpeech` initialization and `setLanguage(Locale.US)` result.
- Produces: `SpeechAvailability`, `SpeechPlayer.availability: StateFlow<SpeechAvailability>`, safe no-op `speak` when unavailable.

- [ ] **Step 1: Write failing contract tests**

Define fake expectations in JVM/Compose fixtures:

```kotlin
private class FakeSpeechPlayer(
    initial: SpeechAvailability = SpeechAvailability.Available
) : SpeechPlayer {
    private val mutableAvailability = MutableStateFlow(initial)
    override val availability: StateFlow<SpeechAvailability> = mutableAvailability
    val spoken = mutableListOf<String>()
    override fun speak(text: String) { if (availability.value == SpeechAvailability.Available) spoken += text }
    override fun release() = Unit
}
```

Extend the Android player test to wait for a terminal availability and assert it is `Available` or `Unavailable`, never permanently `Initializing`.

- [ ] **Step 2: Run audio tests and verify RED**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.audio.AndroidSpeechPlayerTest`

Expected: compilation failure for missing availability types.

- [ ] **Step 3: Implement the availability contract**

```kotlin
sealed interface SpeechAvailability {
    data object Initializing : SpeechAvailability
    data object Available : SpeechAvailability
    data object Unavailable : SpeechAvailability
}

interface SpeechPlayer {
    val availability: StateFlow<SpeechAvailability>
    fun speak(text: String)
    fun release()
}
```

`AndroidSpeechPlayer` starts at `Initializing`. On successful initialization, call `setLanguage(Locale.US)` and emit `Available` only when the result is neither `LANG_MISSING_DATA` nor `LANG_NOT_SUPPORTED`; otherwise emit `Unavailable`. `speak` checks `Available`; `release` shuts down and emits `Unavailable`.

- [ ] **Step 4: Update all fakes and run audio/full tests**

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.audio.AndroidSpeechPlayerTest`

Run: `./gradlew testDebugUnitTest`

Expected: PASS.

- [ ] **Step 5: Commit availability support**

```bash
git add app/src/main/java/com/example/myapplication/audio app/src/test app/src/androidTest
git commit -m "Expose text to speech availability"
```

---

### Task 5: Shared Speaking Content and Normal Practice Completion Snapshot

**Files:**
- Create: `app/src/main/java/com/example/myapplication/ui/practice/SpeakingPracticeContent.kt`
- Modify: `app/src/main/java/com/example/myapplication/ui/set/PracticeScreen.kt`
- Modify: `app/src/main/java/com/example/myapplication/ui/set/PracticeSetUiState.kt`
- Modify: `app/src/main/java/com/example/myapplication/ui/set/PracticeSetViewModel.kt`
- Modify: `app/src/test/java/com/example/myapplication/ui/set/PracticeSetViewModelTest.kt`
- Modify: `app/src/androidTest/java/com/example/myapplication/ui/set/PracticeScreenTest.kt`
- Modify: `app/src/main/java/com/example/myapplication/ui/navigation/AppNavGraph.kt`

**Interfaces:**
- Consumes: `PracticeQuestion`, `SpeechAvailability`, `PracticeSessionStore`, `FavoriteRepository`.
- Produces: stateless `SpeakingPracticeContent`, visible actual question, and immutable completed-set snapshot before navigation.

- [ ] **Step 1: Add failing ViewModel completion-snapshot tests**

```kotlin
@Test fun `finishing last item stores questions and favorite flags`() = runTest {
    val session = PracticeSessionStore()
    val vm = buildViewModel(sessionStore = session, questions = listOf(QUESTION_1, QUESTION_2))
    advanceUntilIdle()
    vm.toggleFavorite()
    advanceUntilIdle()
    vm.nextQuestion()
    advanceUntilIdle()
    vm.nextQuestion()
    advanceUntilIdle()
    val completed = requireNotNull(session.lastCompletedSet())
    assertEquals(listOf(true, false), completed.items.map(CompletedPracticeItem::isFavorite))
    assertEquals(listOf(QUESTION_1, QUESTION_2), completed.items.map(CompletedPracticeItem::question))
}
```

Add a failure test proving no snapshot is saved until the last item completes.

- [ ] **Step 2: Run ViewModel tests and verify RED**

Run: `./gradlew testDebugUnitTest --tests '*PracticeSetViewModelTest'`

Expected: failure because `PracticeSetViewModel` does not accept/save session snapshots.

- [ ] **Step 3: Track per-item favorite state and save completion atomically**

Add `favoriteQuestionKeys: Set<String>` to `Ready`, using `englishSentence` as the existing Room identity. Update toggle/next behavior from that set. On final `nextQuestion`, save:

```kotlin
sessionStore.saveCompletedSet(
    CompletedPracticeSet(current.questions.map { question ->
        CompletedPracticeItem(
            question = question,
            isFavorite = question.englishSentence in current.favoriteQuestionKeys
        )
    })
)
```

Inject `PracticeSessionStore` from `AppContainer`. Change `onSetComplete` to `() -> Unit`; the completion route no longer carries a count.

- [ ] **Step 4: Create stateless shared speaking content**

```kotlin
@Composable
fun SpeakingPracticeContent(
    opicQuestion: String?,
    koreanHint: String,
    englishSentence: String,
    isRecording: Boolean,
    hasRecording: Boolean,
    isFavorite: Boolean,
    speechAvailability: SpeechAvailability,
    canRecordAudio: Boolean,
    recordAudioPermissionDenied: Boolean,
    onToggleRecording: () -> Unit,
    onRequestRecordAudioPermission: () -> Unit,
    onPlayRecording: () -> Unit,
    onPlayModelSentence: () -> Unit,
    onToggleFavorite: (() -> Unit)?,
    onNext: () -> Unit,
    nextLabel: String
)
```

Render the question first or the exact legacy fallback, preserve permission behavior, disable the TTS button unless `Available`, and show `이 기기에서는 영어 음성 재생을 사용할 수 없습니다.` for `Unavailable`. Do not give this Composable ownership of players, files, or coroutines.

- [ ] **Step 5: Delegate PracticeScreen ready content and add Compose assertions**

Add tests that assert `Tell me about hobby 1.` is visible and that unavailable TTS displays the message with a disabled `모범 문장 듣기` button. Preserve recording, favorite, settings-error, and completion callback tests.

- [ ] **Step 6: Run normal-practice tests**

Run: `./gradlew testDebugUnitTest --tests '*PracticeSetViewModelTest'`

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.set.PracticeScreenTest`

Expected: PASS.

- [ ] **Step 7: Commit shared practice content**

```bash
git add app/src/main/java/com/example/myapplication/ui/practice app/src/main/java/com/example/myapplication/ui/set app/src/main/java/com/example/myapplication/ui/navigation/AppNavGraph.kt app/src/test app/src/androidTest
git commit -m "Show OPIc questions during speaking practice"
```

---

### Task 6: Completed Set Summary

**Files:**
- Create: `app/src/main/java/com/example/myapplication/ui/set/SetCompleteViewModel.kt`
- Create: `app/src/main/java/com/example/myapplication/ui/set/SetCompleteUiState.kt`
- Modify: `app/src/main/java/com/example/myapplication/ui/set/SetCompleteScreen.kt`
- Create: `app/src/test/java/com/example/myapplication/ui/set/SetCompleteViewModelTest.kt`
- Modify: `app/src/androidTest/java/com/example/myapplication/ui/set/SetCompleteScreenTest.kt`
- Modify: `app/src/main/java/com/example/myapplication/ui/navigation/Routes.kt`
- Modify: `app/src/main/java/com/example/myapplication/ui/navigation/AppNavGraph.kt`

**Interfaces:**
- Consumes: `PracticeSessionStore.lastCompletedSet()`.
- Produces: `SetCompleteUiState.Empty` and `Summary(items, favoriteCount)`, with a route containing no serialized set data.

- [ ] **Step 1: Write failing state tests**

```kotlin
@Test fun `summary exposes items and favorite count`() {
    val store = PracticeSessionStore().apply {
        saveCompletedSet(CompletedPracticeSet(listOf(
            CompletedPracticeItem(QUESTION_1, true),
            CompletedPracticeItem(QUESTION_2, false)
        )))
    }
    val state = SetCompleteViewModel(store).uiState.value as SetCompleteUiState.Summary
    assertEquals(2, state.items.size)
    assertEquals(1, state.favoriteCount)
}

@Test fun `missing snapshot exposes empty state`() {
    assertTrue(SetCompleteViewModel(PracticeSessionStore()).uiState.value is SetCompleteUiState.Empty)
}
```

- [ ] **Step 2: Run tests and verify RED**

Run: `./gradlew testDebugUnitTest --tests '*SetCompleteViewModelTest'`

Expected: missing-type compilation failure.

- [ ] **Step 3: Implement state/ViewModel and summary UI**

The summary screen displays `오늘 N문장을 연습했습니다.`, `즐겨찾기 M개`, and one card per item containing question, hint, English sentence, and `★ 즐겨찾기` when true. Empty state displays `완료한 연습 세트가 없습니다.` plus `홈으로`.

Use `Routes.SetComplete.route = "set_complete"`; build its ViewModel with the shared store.

- [ ] **Step 4: Add Compose tests with concrete summary content**

```kotlin
@Test fun summary_showsQuestionsSentencesAndFavoriteCount() {
    composeTestRule.setContent {
        SetCompleteScreen(
            state = SetCompleteUiState.Summary(
                items = listOf(CompletedPracticeItem(QUESTION_1, true)),
                favoriteCount = 1
            ),
            onBackToHome = {}
        )
    }
    composeTestRule.onNodeWithText("오늘 1문장을 연습했습니다.").assertExists()
    composeTestRule.onNodeWithText("즐겨찾기 1개").assertExists()
    composeTestRule.onNodeWithText(QUESTION_1.opicQuestion).assertExists()
    composeTestRule.onNodeWithText(QUESTION_1.koreanHint).assertExists()
    composeTestRule.onNodeWithText(QUESTION_1.englishSentence).assertExists()
    composeTestRule.onNodeWithText("★ 즐겨찾기").assertExists()
}
```

- [ ] **Step 5: Run completion tests and commit**

Run: `./gradlew testDebugUnitTest --tests '*SetCompleteViewModelTest'`

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.set.SetCompleteScreenTest`

Expected: PASS.

```bash
git add app/src/main/java/com/example/myapplication/ui/set app/src/main/java/com/example/myapplication/ui/navigation app/src/test app/src/androidTest
git commit -m "Summarize completed practice sets"
```

---

### Task 7: Favorite Instant TTS

**Files:**
- Modify: `app/src/main/java/com/example/myapplication/ui/favorites/FavoritesViewModel.kt`
- Modify: `app/src/main/java/com/example/myapplication/ui/favorites/FavoritesScreen.kt`
- Modify: `app/src/test/java/com/example/myapplication/ui/favorites/FavoritesViewModelTest.kt`
- Modify: `app/src/androidTest/java/com/example/myapplication/ui/favorites/FavoritesScreenTest.kt`
- Modify: `app/src/main/java/com/example/myapplication/ui/navigation/AppNavGraph.kt`

**Interfaces:**
- Consumes: `FavoriteRepository`, screen-scoped `SpeechPlayer`, `SpeechAvailability`.
- Produces: favorite-card `바로 듣기`, unavailable guidance, and `onPracticeFavorite(id)` navigation callback.

- [ ] **Step 1: Write failing ViewModel TTS tests**

```kotlin
@Test fun `playFavorite speaks selected English sentence when available`() = runTest {
    val player = FakeSpeechPlayer(SpeechAvailability.Available)
    val vm = FavoritesViewModel(FavoriteRepository(FakeFavoriteDao()), player)
    vm.playFavorite(FavoriteSentence(1, "HOUSING", "Question", "힌트", "Answer.", 1))
    assertEquals(listOf("Answer."), player.spoken)
}

@Test fun `clearing ViewModel releases speech player`() {
    val player = FakeSpeechPlayer()
    val vm = FavoritesViewModel(FavoriteRepository(FakeFavoriteDao()), player)
    vm.clearForTest()
    assertTrue(player.released)
}
```

Add `@VisibleForTesting internal fun clearForTest() = onCleared()` and use it only from the lifecycle
unit test; production cleanup continues to be driven by the ViewModel lifecycle.

- [ ] **Step 2: Run focused tests and verify RED**

Run: `./gradlew testDebugUnitTest --tests '*FavoritesViewModelTest'`

Expected: constructor/method failures for missing player integration.

- [ ] **Step 3: Implement FavoritesViewModel speech ownership**

Expose the player's availability as `StateFlow`, call `speak(favorite.englishSentence)` only when available, and release in `onCleared`.

- [ ] **Step 4: Add card actions and Compose tests**

Change the screen signature to include `onPracticeFavorite: (Long) -> Unit`. Each card displays its question or legacy fallback, `바로 듣기`, `전체 연습`, and `삭제`. Tests click both actions, assert the spoken sentence/captured ID, and verify unavailable TTS disables `바로 듣기` with the exact guidance copy.

- [ ] **Step 5: Wire the screen player and run tests**

Construct `FavoritesViewModel(appContainer.favoriteRepository, appContainer.newSpeechPlayer())` in navigation.

Run: `./gradlew testDebugUnitTest --tests '*FavoritesViewModelTest'`

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.favorites.FavoritesScreenTest`

Expected: PASS.

- [ ] **Step 6: Commit favorite instant playback**

```bash
git add app/src/main/java/com/example/myapplication/ui/favorites app/src/main/java/com/example/myapplication/ui/navigation/AppNavGraph.kt app/src/test app/src/androidTest
git commit -m "Add instant playback to favorites"
```

---

### Task 8: Favorite Full Speaking Practice and Navigation

**Files:**
- Create: `app/src/main/java/com/example/myapplication/ui/favorites/FavoritePracticeUiState.kt`
- Create: `app/src/main/java/com/example/myapplication/ui/favorites/FavoritePracticeViewModel.kt`
- Create: `app/src/main/java/com/example/myapplication/ui/favorites/FavoritePracticeScreen.kt`
- Create: `app/src/test/java/com/example/myapplication/ui/favorites/FavoritePracticeViewModelTest.kt`
- Create: `app/src/androidTest/java/com/example/myapplication/ui/favorites/FavoritePracticeScreenTest.kt`
- Modify: `app/src/main/java/com/example/myapplication/ui/navigation/Routes.kt`
- Modify: `app/src/main/java/com/example/myapplication/ui/navigation/AppNavGraph.kt`
- Modify: `app/src/main/java/com/example/myapplication/di/AppContainer.kt`

**Interfaces:**
- Consumes: `FavoriteRepository.getFavorite(id)`, shared `SpeakingPracticeContent`, screen-scoped speech/record/play objects.
- Produces: `Routes.FavoritePractice`, favorite lookup/error state, and
  `FavoritePracticeViewModel(favoriteId: Long, favoriteRepository: FavoriteRepository, speechPlayer: SpeechPlayer, voiceRecorder: VoiceRecorder, voicePlayer: VoicePlayer, recordingFile: File)`.

- [ ] **Step 1: Write failing ViewModel state and lifecycle tests**

```kotlin
@Test fun `loads favorite and exposes legacy question fallback`() = runTest {
    val dao = FakeFavoriteDao().apply {
        insert(FavoriteSentence(category = "HOUSING", opicQuestion = null,
            koreanHint = "힌트", englishSentence = "Answer.", createdAt = 1))
    }
    val vm = buildFavoritePracticeViewModel(1, FavoriteRepository(dao))
    advanceUntilIdle()
    val ready = vm.uiState.value as FavoritePracticeUiState.Ready
    assertEquals(null, ready.favorite.opicQuestion)
}

@Test fun `missing favorite exposes not found state`() = runTest {
    val vm = buildFavoritePracticeViewModel(999, FavoriteRepository(FakeFavoriteDao()))
    advanceUntilIdle()
    assertTrue(vm.uiState.value is FavoritePracticeUiState.NotFound)
}

@Test fun `record stop play and complete update one item flow`() = runTest {
    val dao = FakeFavoriteDao().apply {
        insert(FavoriteSentence(category = "HOUSING", opicQuestion = "Question",
            koreanHint = "힌트", englishSentence = "Answer.", createdAt = 1))
    }
    val vm = FavoritePracticeViewModel(
        favoriteId = 1,
        favoriteRepository = FavoriteRepository(dao),
        speechPlayer = FakeSpeechPlayer(),
        voiceRecorder = FakeVoiceRecorder(),
        voicePlayer = FakeVoicePlayer(),
        recordingFile = File.createTempFile("favorite-practice", ".m4a")
    )
    advanceUntilIdle()
    vm.startRecording(); vm.stopRecording(); vm.playMyRecording(); vm.complete()
    assertTrue((vm.uiState.value as FavoritePracticeUiState.Ready).isComplete)
}
```

Use these exact fakes and helper for the first two tests:

```kotlin
private class FakeSpeechPlayer : SpeechPlayer {
    override val availability = MutableStateFlow<SpeechAvailability>(SpeechAvailability.Available)
    override fun speak(text: String) = Unit
    override fun release() = Unit
}

private class FakeVoiceRecorder : VoiceRecorder {
    private var recording = false
    override fun startRecording(outputFile: File) { recording = true }
    override fun stopRecording() { recording = false }
    override fun isRecording(): Boolean = recording
}

private class FakeVoicePlayer : VoicePlayer {
    var played: File? = null
    override fun play(file: File) { played = file }
    override fun stop() = Unit
}

private fun buildFavoritePracticeViewModel(
    id: Long,
    repository: FavoriteRepository
) = FavoritePracticeViewModel(
    favoriteId = id,
    favoriteRepository = repository,
    speechPlayer = FakeSpeechPlayer(),
    voiceRecorder = FakeVoiceRecorder(),
    voicePlayer = FakeVoicePlayer(),
    recordingFile = File.createTempFile("favorite-practice", ".m4a")
)
```

- [ ] **Step 2: Run focused tests and verify RED**

Run: `./gradlew testDebugUnitTest --tests '*FavoritePracticeViewModelTest'`

Expected: missing-type compilation failure.

- [ ] **Step 3: Implement the favorite-practice ViewModel**

Use states `Loading`, `NotFound`, and `Ready(favorite, isRecording, hasRecording, isComplete, speechAvailability)`. Load the item by ID, delegate recorder/player/speech actions, prevent TTS calls when unavailable, delete the recording on completion/clear, and release speech/player resources.

- [ ] **Step 4: Implement screen and Compose tests**

The screen delegates ready content to `SpeakingPracticeContent` with no favorite toggle, `nextLabel = "연습 완료"`, and `onNext = viewModel::complete`. A `LaunchedEffect` calls `onComplete` after completion. `NotFound` shows `즐겨찾기 문장을 찾을 수 없습니다.` and `목록으로`.

Compose tests cover actual question display, exact legacy fallback, recording buttons, unavailable TTS, completion callback, and not-found return.

- [ ] **Step 5: Add route and runtime permission wiring**

```kotlin
data object FavoritePractice : Routes("favorite_practice/{favoriteId}") {
    fun createRoute(favoriteId: Long) = "favorite_practice/$favoriteId"
}
```

Parse `favoriteId` as `NavType.LongType`, create the ViewModel with a unique cache file such as `favorite_recording_$favoriteId.m4a`, and apply the same record-audio permission flow used by normal practice. On completion/back, pop to Favorites. The Favorites card callback navigates to this route.

- [ ] **Step 6: Run favorite-practice tests**

Run: `./gradlew testDebugUnitTest --tests '*FavoritePracticeViewModelTest' --tests '*FavoritesViewModelTest'`

Run: `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.favorites.FavoritePracticeScreenTest,com.example.myapplication.ui.favorites.FavoritesScreenTest`

Expected: PASS.

- [ ] **Step 7: Commit full favorite practice**

```bash
git add app/src/main/java/com/example/myapplication/ui/favorites app/src/main/java/com/example/myapplication/ui/navigation app/src/main/java/com/example/myapplication/di/AppContainer.kt app/src/test app/src/androidTest
git commit -m "Add full speaking practice for favorites"
```

---

### Task 9: Full Regression, Security, Migration, and Build Verification

**Files:**
- Modify only files implicated by a concrete failing check below.

**Interfaces:**
- Consumes: complete Tasks 1–8 implementation.
- Produces: verified practice experience with no schema loss, retry-budget breach, secret leak, or audio lifecycle regression.

- [ ] **Step 1: Run the complete JVM suite freshly**

Run: `./gradlew testDebugUnitTest --rerun-tasks`

Expected: all unit tests PASS with zero failures/errors/skips.

- [ ] **Step 2: Run all Android instrumentation and migration tests**

Run: `./gradlew connectedDebugAndroidTest`

Expected: all tests PASS on the connected emulator/device, including Room 1→2 migration.

- [ ] **Step 3: Build debug APK and run lint**

Run: `./gradlew assembleDebug lintDebug`

Expected: `BUILD SUCCESSFUL` and lint error count 0. Record existing warning count separately.

- [ ] **Step 4: Audit schema and persistence boundaries**

Run:

```bash
rg -n 'version = 2|MIGRATION_1_2|ADD COLUMN opicQuestion TEXT' app/src/main app/schemas
rg -n 'PracticeSessionStore' app/src/main
rg -n 'PracticeSessionStore|CompletedPracticeSet' app/src/main/java/com/example/myapplication/data/local app/src/main/java/com/example/myapplication/data/settings || true
```

Expected: database version/migration/schema matches version 2; session types appear in session/repository/UI/DI code but never Room entities, DAOs, or settings storage.

- [ ] **Step 5: Audit remote-call budget and secret safety tests**

Run:

```bash
rg -n 'callCount|InsufficientUniqueQuestions|remote-secret-sentinel' app/src/test
rg -n 'bodyText.*throw|throw.*bodyText|apiKey.*Log|Log\..*apiKey' app/src/main || true
```

Expected: tests assert maximum two calls and safe cause chains; production scan finds no raw body/API key logging.

- [ ] **Step 6: Review final diff and working tree**

Run: `git diff --check 49a56425aba59f3b176212743f1b4faf3bb0b1d0..HEAD && git status --short`

Expected: no whitespace errors and only planned files. The fixed SHA is the approved design commit immediately
before implementation; never substitute `HEAD~N`.

- [ ] **Step 7: Commit only concrete verification fixes**

If a verification command exposes a real regression, add a targeted failing test, make the minimal fix, rerun the affected command and all Steps 1–3, then commit the exact changed source/test files with subject `Fix practice experience regressions`. If no files change, do not create an empty commit.
