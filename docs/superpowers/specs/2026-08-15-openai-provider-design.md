# OpenAI 제공자 연동 설계

## 목표

기존 Anthropic Claude 기반 OPIc 세트 생성 기능에 OpenAI Responses API를 추가한다.
사용자는 설정에서 Claude 또는 OpenAI를 직접 선택하고 자신의 API 키를 저장한다. 세트 생성은
선택된 제공자만 호출하며, 제공자 장애 시 다른 제공자로 자동 전환하지 않는다.

OpenAI 연동은 Codex SDK가 아니라 Responses API를 사용한다. Codex SDK는 로컬 코드 저장소를
다루는 서버 측 코딩 에이전트용이고, 이 앱은 구조화된 문장 세트를 생성하는 Android 클라이언트다.

## 범위

- AI 제공자 선택: `CLAUDE`, `OPENAI`
- 제공자별 API 키 암호화 저장
- OpenAI Responses API 클라이언트 및 응답 파서
- 제공자 선택에 따른 단일 호출 라우팅
- 공통 프롬프트, 결과 검증, 파싱 실패 1회 재시도
- API 키 누락 시 설정 화면 이동 동작
- 제공자별 오류를 사용자용 오류로 정규화

이번 범위에서 제외한다.

- Claude 실패 시 OpenAI 자동 대체
- 세트마다 제공자를 선택하는 UI
- 사용자가 모델 ID를 직접 입력하는 기능
- Codex SDK, Codex CLI 또는 ChatGPT 구독 인증 사용
- 앱 개발자가 소유한 공용 API 키 또는 별도 백엔드 프록시

## 아키텍처

`PracticeSetViewModel`과 화면은 제공자별 API 형식을 알지 않는다. `PracticeRepository`가 설정에서
선택된 제공자와 키를 읽고 `PracticeAiProvider` 구현 하나를 선택한다.

```text
PracticeSetViewModel
        |
        v
PracticeRepository
        |
        v
PracticeAiProvider
   |-- ClaudePracticeProvider
   `-- OpenAiPracticeProvider
```

### 공통 타입

```kotlin
enum class AiProvider { CLAUDE, OPENAI }

interface PracticeAiProvider {
    val provider: AiProvider
    suspend fun generate(apiKey: String, prompt: String): String
}
```

Provider는 원격 API 응답에서 모델이 생성한 텍스트를 추출해 반환한다. JSON 배열을
`PracticeQuestion`으로 변환하는 일은 공통 Repository가 담당한다. 이 경계 덕분에 두 제공자가
동일한 파서와 결과 검증 규칙을 사용한다.

### 모델

- Claude: 기존 모델 설정을 유지한다.
- OpenAI: `gpt-5.6-terra`를 고정 사용한다.

모델 ID는 각 Provider 내부 상수로 관리한다. 사용자 설정이나 화면에 모델 ID를 노출하지 않는다.

## 설정 저장소

기존 API 키 저장소를 다음 계약으로 확장한다.

```kotlin
interface AiSettingsStore {
    fun getSelectedProvider(): AiProvider
    fun setSelectedProvider(provider: AiProvider)
    fun getApiKey(provider: AiProvider): String?
    fun setApiKey(provider: AiProvider, apiKey: String)
    fun clearApiKey(provider: AiProvider)
}
```

`EncryptedSharedPreferences`에는 선택 제공자와 제공자별 키를 서로 다른 키로 저장한다. 기존
Anthropic 키가 있는 사용자는 기본 제공자를 `CLAUDE`로 간주하고 기존 키를 계속 사용할 수 있어야
한다. 저장된 실제 키는 설정 화면에 다시 채우지 않는다. 화면은 키 등록 여부만 표시한다.

## 설정 UI

설정 화면은 다음 상태를 표시한다.

- Claude/OpenAI 제공자 선택 컨트롤
- 현재 선택된 제공자의 API 키 입력 필드
- 저장 버튼
- 선택된 제공자의 키 등록 여부
- 선택된 제공자의 키 삭제 버튼

제공자를 바꾸면 입력 중인 평문 키를 비우고 새 제공자의 등록 여부를 읽는다. 저장 성공 메시지는
현재 선택 제공자를 명시한다. 한 제공자의 키를 저장하거나 삭제해도 다른 제공자의 키에는 영향을
주지 않는다.

## 세트 생성 데이터 흐름

1. 사용자가 설정에서 제공자와 해당 API 키를 저장한다.
2. 홈에서 연습 카테고리를 선택한다.
3. `PracticeSetViewModel`이 Repository에 세트 생성을 요청한다.
4. Repository가 선택 제공자와 해당 키를 설정 저장소에서 읽는다.
5. 키가 없으면 `MissingApiKey(provider)` 오류를 반환한다.
6. 키가 있으면 공통 `PromptBuilder`로 프롬프트를 만든다.
7. 선택된 `PracticeAiProvider` 하나만 호출한다.
8. 공통 파서가 응답 텍스트를 `PracticeQuestion` 목록으로 변환한다.
9. 파싱 실패 시 같은 Provider에 같은 프롬프트로 정확히 한 번 재요청한다.
10. 성공한 질문 목록을 기존 연습 화면에 전달한다.

Repository는 네트워크 오류나 인증 오류를 재시도하지 않는다. 다른 제공자로도 전환하지 않는다.

## OpenAI Responses API

OpenAI Provider는 다음 요청을 보낸다.

- URL: `https://api.openai.com/v1/responses`
- 인증: `Authorization: Bearer <OPENAI_API_KEY>`
- 모델: `gpt-5.6-terra`
- 입력: 공통 OPIc 프롬프트

Responses API의 `output` 배열에는 메시지 외에 다른 항목이 포함될 수 있으므로 고정 인덱스를
가정하지 않는다. `type == "message"`인 항목의 content 가운데 `type == "output_text"`인 텍스트를
순서대로 합친다. 출력 텍스트가 없으면 제공자 응답 오류로 처리한다. 알 수 없는 필드는 무시한다.

## 오류 모델과 UI

공통 오류 타입은 최소한 다음 경우를 구분한다.

- `MissingApiKey(provider)`: 해당 제공자 키 없음
- `AuthenticationFailed(provider)`: HTTP 401 또는 인증 거부
- `RateLimited(provider)`: HTTP 429
- `NetworkFailure(provider, cause)`: 연결 또는 시간 초과
- `ProviderFailure(provider, statusCode, message)`: 그 외 비성공 응답. `message`는 원격 응답
  본문이 아닌 앱에서 정의한 정제된 설명만 담는다.
- `InvalidProviderResponse(provider, cause)`: 텍스트 추출 실패
- `InvalidPracticeSet(cause)`: 두 번째 JSON 파싱도 실패

화면 동작은 다음과 같다.

- 키 없음: 제공자 이름과 함께 안내하고 설정 이동 버튼 표시
- 인증 실패: 해당 제공자의 키 확인 안내와 설정 이동 버튼 표시
- 요청 제한: 잠시 후 재시도 안내
- 네트워크 실패: 연결 확인과 수동 재시도 버튼 표시
- 파싱 실패: 내부적으로 한 번 재요청한 뒤 실패하면 수동 재시도 버튼 표시

원격 응답 본문이나 API 키를 사용자 메시지, 로그, 예외 문자열에 그대로 포함하지 않는다.

## 의존성 연결

`AppContainer`가 두 Provider와 설정 저장소를 만들고 Repository에 전달한다. 새로운 DI 프레임워크는
도입하지 않는다. 기존의 수동 의존성 조립 방식을 유지한다.

## 테스트 전략

### 단위 테스트

- OpenAI 요청이 올바른 URL, Bearer 인증 헤더, 모델, 입력을 사용한다.
- Responses API의 여러 output/content 항목에서 `output_text`만 안전하게 합친다.
- 출력 텍스트가 없거나 응답 JSON이 잘못되면 제공자 응답 오류를 반환한다.
- 선택된 Provider 하나만 호출되고 다른 Provider는 호출되지 않는다.
- Claude/OpenAI 키 저장, 조회, 삭제가 서로 격리된다.
- 기존 Anthropic 키가 있을 때 기본 제공자가 Claude로 유지된다.
- 첫 파싱 실패 뒤 같은 Provider를 한 번 더 호출한다.
- 두 번째 파싱 실패 뒤 더 호출하지 않는다.
- 네트워크, 인증, 요청 제한 오류는 자동 재시도하지 않는다.
- coroutine 취소는 일반 실패로 변환하지 않고 다시 던진다.

### Android 계측 및 Compose 테스트

- 제공자 변경 시 API 키 입력이 비워지고 등록 상태가 갱신된다.
- 키 저장과 삭제 UI가 선택된 제공자에만 영향을 준다.
- API 키 누락 오류에서 설정 이동 버튼이 노출되고 동작한다.
- 기존 Claude 설정과 연습 화면 테스트가 계속 통과한다.

### 완료 기준

- JVM 단위 테스트와 Android 계측 테스트 전체 통과
- Debug APK 빌드 성공
- Android lint 오류 0건
- Claude와 OpenAI 각각 MockWebServer 기반 성공/오류 경로 검증
- API 키나 원격 응답 본문이 로그 및 사용자 오류 메시지에 노출되지 않음

## 마이그레이션과 호환성

Room 스키마 변경은 없다. 기존 `EncryptedSharedPreferences` 파일을 계속 사용한다. 기존 Anthropic 키
저장 키를 읽는 호환 경로를 유지하며, 새 저장 형식으로의 즉시 파괴적 마이그레이션은 하지 않는다.
앱 업데이트 후 기존 사용자는 별도 설정 없이 Claude를 계속 사용할 수 있다.
