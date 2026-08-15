# OpenAI provider integration — final review fix report

## Status

All five final-review findings were addressed in one fix wave. The historical
Task 8 report was left unchanged; this report appends the corrected verification
and range evidence. The pre-existing untracked `.gstack/` directory was not
read, modified, staged, or included in verification diffs.

## Findings addressed

1. `ClaudeApiClient` now maps malformed successful JSON to a cause-free
   `InvalidProviderResponse`. A sentinel regression walks the complete throwable
   cause chain and proves that raw response content is not retained.
2. The settings API-key field is now single-line and uses password keyboard
   options with autocorrection disabled. `ImeAction.Done` provides an observable
   single-line IME semantics assertion in the Compose test. Compose semantics do
   not expose keyboard type or autocorrection directly, so no test-only config
   seam was added.
3. The OpenAI request-shape test now asserts `POST`, `/v1/responses`, JSON content
   type, bearer authorization, the exact JSON key set, and exact decoded
   `model`/`input` values.
4. The encrypted-store fixture removes `selected_ai_provider` directly through
   the existing internal preferences factory before the default assertion. A
   second store instance over the same preferences verifies that `OPENAI`
   selection persists.
5. `SettingsViewModel.selectProvider` now returns early when the selected
   provider is reselected, preserving unsaved plaintext input.

## TDD evidence

### RED — Claude cause sanitization and same-provider selection

Command:

```text
./gradlew testDebugUnitTest \
  --tests 'com.example.myapplication.data.remote.ClaudeApiClientTest.maps malformed success body without retaining response content in cause chain' \
  --tests 'com.example.myapplication.ui.settings.SettingsViewModelTest.selecting current provider preserves unsaved input'
```

Observed before production changes:

```text
2 tests completed, 2 failed
ClaudeApiClientTest ... FAILED at ClaudeApiClientTest.kt:84
SettingsViewModelTest ... FAILED at SettingsViewModelTest.kt:70
BUILD FAILED in 3s
```

The Claude failure showed that the response sentinel remained reachable in the
decoder cause chain. The ViewModel comparison showed that reselecting OpenAI
replaced `"unsaved openai key"` with an empty input.

### RED — single-line Compose semantics

After correcting a missing test assertion import (the first attempt was a test
compile error and is not counted as RED), this command was run against the
multiline production field:

```text
./gradlew connectedDebugAndroidTest \
  '-Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.settings.SettingsScreenTest#apiKeyInput_isSingleLine'
```

Observed:

```text
Starting 1 tests on Medium_Phone_API_36.1(AVD) - 16
FAILED: expected ImeAction = 'Done'
actual semantics: ImeAction = 'Default'
Finished 1 tests ... 1 failure
BUILD FAILED in 12s
```

The final regression name is `apiKeyInput_hasSingleLineImeAction`.

### GREEN — focused JVM tests

Command:

```text
./gradlew testDebugUnitTest \
  --tests 'com.example.myapplication.data.remote.ClaudeApiClientTest' \
  --tests 'com.example.myapplication.data.remote.OpenAiApiClientTest' \
  --tests 'com.example.myapplication.ui.settings.SettingsViewModelTest'
```

Result:

```text
22 tests, 0 failures, 0 errors, 0 skipped
BUILD SUCCESSFUL in 3s
26 actionable tasks: 6 executed, 20 up-to-date
```

The count is the sum of the generated class results: Claude 8, OpenAI 9, and
SettingsViewModel 5.

### GREEN — focused instrumentation tests

Command:

```text
./gradlew connectedDebugAndroidTest \
  '-Pandroid.testInstrumentationRunnerArguments.class=com.example.myapplication.ui.settings.SettingsScreenTest,com.example.myapplication.data.settings.EncryptedAiSettingsStoreTest'
```

Result on `Medium_Phone_API_36.1(AVD) - 16`:

```text
Starting 9 tests
Finished 9 tests
0 skipped, 0 failed
BUILD SUCCESSFUL in 24s
70 actionable tasks: 12 executed, 58 up-to-date
```

## Full verification

### JVM suite

```text
./gradlew testDebugUnitTest
BUILD SUCCESSFUL in 1s
26 actionable tasks: 3 executed, 23 up-to-date
```

Fresh JUnit XML aggregation:

```text
tests=62 failures=0 errors=0 skipped=0
```

### Android instrumentation suite

```text
./gradlew connectedDebugAndroidTest
Starting 28 tests on Medium_Phone_API_36.1(AVD) - 16
Tests 28/28 completed (0 skipped) (0 failed)
BUILD SUCCESSFUL in 46s
70 actionable tasks: 1 executed, 69 up-to-date
```

### Debug APK and lint

```text
./gradlew assembleDebug lintDebug
BUILD SUCCESSFUL in 11s
47 actionable tasks: 8 executed, 39 up-to-date
```

Direct inspection of `app/build/reports/lint-results-debug.xml` reports:

```text
errors=0
warnings=26
```

The 26 warnings are existing project/toolchain warnings and do not fail lint.
This corrects the historical Task 8 report's `warnings=0` summary; the required
criterion is zero lint errors, which remains satisfied.

## Corrected evidence range

The Task 8 command `git diff --check HEAD~7..HEAD` covered only the final seven
commits at that point; it was not the complete integration range. The exact
pre-fix integration review range is:

```text
f368f7f55a9598034b598060192aa35b05e45f28..96eef007ea0cd1b14badcd9044c5736a27c585d5
```

For this final review, `git diff --check
f368f7f55a9598034b598060192aa35b05e45f28` was run with the final-fix working
tree included and exited 0 with no output. `git diff --name-status` over the same
base showed only the planned provider integration code, tests, plan, and design
documents. The final-fix-only range is `96eef00..` the commit containing this
report; its SHA is recorded in the handoff because a commit cannot embed its own
content-derived SHA.

## Security and self-review

- A production-source scan for raw-response/secret markers returned no matches.
- The Claude sentinel test mutates correctly: restoring the decoder exception as
  the cause makes the test fail.
- Removing the same-provider early return makes the unsaved-input test fail.
- Weakening or changing the OpenAI method/path/content type/model/input makes the
  exact request test fail.
- Restoring provider setup in the encrypted-store fixture makes the default test
  false-positive; the fixture now removes the key before construction.
- Removing the explicit single-line IME action makes the Compose semantics test
  fail with `ImeAction = Default`.
- `git diff --check` passed for both the final-fix diff and the complete corrected
  integration range.

## Concerns

- Android lint passes with zero errors but retains 26 pre-existing warnings.
- The settings test verifies observable single-line IME semantics; password
  keyboard type and autocorrection remain production configuration assertions by
  code inspection because Compose does not expose them through semantics.
- `.gstack/` remains an unrelated, untracked directory and is intentionally
  excluded from this commit.
