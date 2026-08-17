package com.example.myapplication.data.model

data class WorldNewsArticle(
    val id: String,
    val source: String,
    val category: String,
    val publishedTime: String,
    val headline: String,
    val headlineKorean: String,
    val summaryPoints: List<String>,
    val fullScript: String,
    val keyVocabularies: List<NewsVocabulary>,
    val keySentence: String,
    val keySentenceKorean: String
)

data class NewsVocabulary(
    val word: String,
    val phonetic: String,
    val partOfSpeech: String,
    val meaningKorean: String,
    val exampleSentence: String,
    val tag: VocabTag
)

enum class VocabTag(val label: String) {
    MUST_KNOW("핵심 필수"),
    FREQUENT("빈출 단어"),
    ADVANCED("고급 시사")
}
