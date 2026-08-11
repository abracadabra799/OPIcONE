# OPIc 스피킹 연습 앱 설계

## 배경 및 목표

OPIc(Oral Proficiency Interview-computer) 1등급(AL) 취득을 목표로 하는 사용자를 위한
안드로이드 스피킹 연습 앱. 오픽 주요 코모(콤보) 영역별 질문에 대해, 영어식 어순으로 재배열된
한국어 힌트를 먼저 보여주고 그 어순대로 영어 문장을 말하는 연습을 반복한다. 매 세트마다
새 질문/모범답안을 즉석 생성해 반복 노출로 인한 지루함을 줄이고, 고급 표현(복문, 관용구,
담화 표지)이 포함된 모범답안으로 "고급스러운 영어"를 접하게 한다.

## 범위

- 카테고리: 1등급 목표에 맞춘 고난도 카테고리 포함 (자기소개, 거주지/집, 직장/학교,
  취미/여가활동, 과거 경험, 서베이/돌발상황 롤플레이, 비교·대비, 문제해결 롤플레이 등)
- 질문 및 모범답안은 Anthropic Claude API로 매 세트 즉석 생성 (사전 번들 데이터 없음)
- 로컬 영구 저장 대상은 사용자가 즐겨찾기한 문장뿐이며, 그 외 세트 데이터는 세션 메모리에만 존재

## 아키텍처

MVVM + Jetpack Compose 단일 액티비티 구조.

- **UI (Compose)**: 화면별 Composable + ViewModel
- **Repository 계층**:
  - `PracticeRepository`: LLM API 호출로 카테고리별 질문/모범답안 세트 생성
  - `FavoriteRepository`: 즐겨찾기 문장 CRUD (Room)
- **로컬 저장소**: Room DB. 즐겨찾기 문장만 영구 저장
- **네트워크**: OkHttp로 Anthropic Messages API 직접 호출 (단일 엔드포인트라 Retrofit 불필요)
- **TTS**: Android 내장 `TextToSpeech` (US 로케일)로 모범 문장 재생
- **녹음/재생**: `MediaRecorder`로 사용자 발화 녹음, `MediaPlayer`로 재생

## 화면 흐름

```
[홈] → 카테고리 선택
   ↓
[오늘의 세트] → 선택 카테고리 내 새 질문 5개를 즉석 생성 (로딩 상태 표시)
   ↓ (문제 1개씩 순차 진행)
[연습 화면]
   1. 영어 어순 그대로 직역한 한국어 힌트 표시
   2. 🎙 녹음 버튼 → 사용자가 그 어순대로 영어로 말하기 (RECORD_AUDIO 권한 필요)
   3. 녹음 종료 → "모범 문장 듣기" 버튼 (TTS 재생)
   4. 내 녹음 재생 ▶ / 모범 문장 재생 ▶ 나란히 비교
   5. ⭐ 즐겨찾기 토글 → 다음 문제로 이동
   ↓
[세트 완료] → 오늘 연습한 5문장 요약, 즐겨찾기 개수 표시
[즐겨찾기 탭] → Room에 저장된 문장 다시 듣기/연습
[설정] → Anthropic API 키 입력/저장
```

## LLM 프롬프트 전략

- 매 세트 요청 시 카테고리와 "이번 세션에서 이미 나온 질문 목록"을 프롬프트에 포함해 중복 회피
- "OPIc AL(1등급) 수준"을 명시하고, 복문·관용표현·담화 표지(예: "what really stood out was…",
  "as opposed to…")를 포함한 모범답안을 요구
- 응답은 항상 JSON 배열로 강제 요청:
  `{ korean_ordered_hint, english_sentence, category }[]`
- 한국어 힌트가 영어 어순을 그대로 따르는 직역 스타일임을 프롬프트 예시(few-shot 1~2개)로 고정해
  형식 흔들림 방지

## 데이터 모델 (Room)

`FavoriteSentence`
- `id: Long` (PK, autoGenerate)
- `category: String`
- `koreanHint: String`
- `englishSentence: String`
- `createdAt: Long`

세트 자체(오늘 생성된 문제들)는 Room에 저장하지 않고 ViewModel 메모리에만 유지한다.
앱을 재시작하면 새 세트를 생성한다.

## 오디오 처리

- **TTS**: `TextToSpeech` 초기화 후 모범 문장 재생. 엔진 미지원 언어 등 초기화 실패 시
  텍스트만 표시하고 재생 버튼 비활성화
- **녹음**: `MediaRecorder`로 캐시 디렉터리에 m4a 임시 파일 녹음. 문제 전환/세트 종료 시
  임시 파일 삭제 (영구 보관하지 않음)
- **권한**: RECORD_AUDIO 런타임 권한을 최초 녹음 시도 시 요청. 거부 시 녹음/비교 기능 없이
  힌트-모범답안 열람만 가능하도록 축소 동작

## API 키 관리

- 설정 화면에서 사용자가 Anthropic API 키를 직접 입력
- `EncryptedSharedPreferences`에 저장 (기기 내부에만 보관, 서버 전송 없음)
- 키가 없는 상태에서 세트 생성을 시도하면 설정 화면으로 안내

## 에러 처리

- API 실패(네트워크 오류, 키 오류, rate limit 등): 스낵바로 원인 안내 + 재시도 버튼
- 응답 JSON 파싱 실패: 자동 1회 재요청, 재실패 시 사용자에게 실패 안내 및 재시도 유도

## 테스트 전략

- 프롬프트 빌더 및 JSON 파싱 로직을 순수 함수로 분리해 유닛 테스트
- ViewModel 상태 전이(세트 로딩 → 완료, 녹음 시작 → 종료) 유닛 테스트
- Room DAO(즐겨찾기 CRUD) 테스트
