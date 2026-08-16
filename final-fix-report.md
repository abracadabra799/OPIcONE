# Practice experience final fix report

Date: 2026-08-16 (Asia/Seoul)

## Scope

Only the three requested Important findings were changed:

1. `PracticeSetViewModel` now updates favorite state optimistically, serializes favorite mutations, and lets an already-requested Room mutation finish after ViewModel clearing.
2. `SpeakingPracticeContent` now shows unavailable-TTS guidance, the English sentence, and the disabled model-sentence button without requiring a recording or denied microphone permission. The recording controls remain available.
3. `PracticeSetViewModel` now stops an active recorder and the voice player before deleting the recording on next, completion, and `onCleared`.

## TDD evidence

### RED

- `./gradlew testDebugUnitTest --tests 'com.example.myapplication.ui.set.PracticeSetViewModelTest'`
  - Failed as expected: 19 tests run, 4 failures.
  - Failures: immediate favorite completion/clear persistence; audio cleanup on next; audio cleanup on completion; audio cleanup on clear.
- `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.set.PracticeScreenTest`
  - Failed as expected: 8 tests run, 1 failure.
  - Failure: unavailable TTS fallback was absent before recording or permission denial.

### GREEN

- `./gradlew testDebugUnitTest --tests 'com.example.myapplication.ui.set.PracticeSetViewModelTest'`
  - `BUILD SUCCESSFUL`; 19/19 passed.
- `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.set.PracticeScreenTest`
  - `BUILD SUCCESSFUL`; 8/8 passed on `Medium_Phone_API_36.1(AVD) - 16`.

## Fresh full verification

- `./gradlew testDebugUnitTest --rerun-tasks`
  - `BUILD SUCCESSFUL`; 91/91 JVM tests passed.
  - 26/26 Gradle tasks executed.
- `./gradlew connectedDebugAndroidTest`
  - `BUILD SUCCESSFUL`; 46/46 instrumentation tests passed, 0 skipped, on `Medium_Phone_API_36.1(AVD) - 16`.
- `./gradlew assembleDebug lintDebug`
  - `BUILD SUCCESSFUL`.
  - `assembleDebug` succeeded.
  - `lintDebug` succeeded with 0 errors and 27 warnings.
- `git diff --check`
  - Passed with no whitespace errors.

No push was performed.
