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
    PROBLEM_SOLVING_ROLEPLAY("문제해결 롤플레이", "resolving a problem in a phone-call role-play"),
    PETS("반려동물/반려견", "raising and caring for pets such as dogs and rabbits"),
    LOUNGE_REVIEW("🏛️ 라운지 수강 복습", "lounge class review topics including transportation, cooking, and jogging")
}
