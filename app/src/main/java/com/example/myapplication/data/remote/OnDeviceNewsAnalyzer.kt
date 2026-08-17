package com.example.myapplication.data.remote

import com.example.myapplication.data.local.DailyWorldNewsBank
import com.example.myapplication.data.model.NewsVocabulary
import com.example.myapplication.data.model.VocabTag
import com.example.myapplication.data.model.WorldNewsArticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

object NewsPromptBuilder {
    fun buildNewsAnalysisPrompt(rawHeadline: String, rawContent: String): String {
        return """
You are an expert English-Korean educator. Analyze the following news article and produce a JSON object for Korean English learners.

Article Headline: $rawHeadline
Article Content: $rawContent

Respond ONLY with a valid JSON object in this exact schema without markdown wrap:
{
  "headlineKorean": "한국어 번역 헤드라인",
  "summaryPoints": [
    "핵심 요약 문장 1 (한국어)",
    "핵심 요약 문장 2 (한국어)",
    "핵심 요약 문장 3 (한국어)"
  ],
  "keyVocabularies": [
    {
      "word": "vocabulary",
      "phonetic": "/vəˈkæbjələri/",
      "partOfSpeech": "[명사]",
      "meaningKorean": "어휘, 단어",
      "exampleSentence": "Example sentence using vocabulary in context.",
      "tag": "MUST_KNOW"
    }
  ],
  "keySentence": "One important full English sentence from the article for speaking practice.",
  "keySentenceKorean": "그 문장의 한국어 직독직해 번역"
}
""".trimIndent()
    }
}

class OnDeviceNewsAnalyzer(private val engine: OnDeviceLlmEngine) {

    suspend fun analyzeArticle(
        id: String,
        source: String,
        category: String,
        headline: String,
        fullContent: String
    ): WorldNewsArticle? = withContext(Dispatchers.Default) {
        if (!engine.isModelReady()) return@withContext null

        try {
            val prompt = NewsPromptBuilder.buildNewsAnalysisPrompt(headline, fullContent)
            val responseText = engine.generate(prompt)
            val jsonStart = responseText.indexOf('{')
            val jsonEnd = responseText.lastIndexOf('}')
            if (jsonStart == -1 || jsonEnd == -1 || jsonEnd <= jsonStart) return@withContext null

            val json = JSONObject(responseText.substring(jsonStart, jsonEnd + 1))
            val headlineKorean = json.optString("headlineKorean", headline)
            val summaryArray = json.optJSONArray("summaryPoints") ?: JSONArray()
            val summaryPoints = mutableListOf<String>()
            for (i in 0 until summaryArray.length()) {
                summaryPoints.add(summaryArray.getString(i))
            }
            if (summaryPoints.isEmpty()) {
                summaryPoints.add("기사의 핵심 요약입니다.")
            }

            val vocabArray = json.optJSONArray("keyVocabularies") ?: JSONArray()
            val keyVocabularies = mutableListOf<NewsVocabulary>()
            for (i in 0 until vocabArray.length()) {
                val obj = vocabArray.getJSONObject(i)
                val tagStr = obj.optString("tag", "MUST_KNOW")
                val tag = when (tagStr) {
                    "FREQUENT" -> VocabTag.FREQUENT
                    "ADVANCED" -> VocabTag.ADVANCED
                    else -> VocabTag.MUST_KNOW
                }
                keyVocabularies.add(
                    NewsVocabulary(
                        word = obj.optString("word", ""),
                        phonetic = obj.optString("phonetic", "/.../"),
                        partOfSpeech = obj.optString("partOfSpeech", "[단어]"),
                        meaningKorean = obj.optString("meaningKorean", ""),
                        exampleSentence = obj.optString("exampleSentence", ""),
                        tag = tag
                    )
                )
            }

            val keySentence = json.optString("keySentence", headline)
            val keySentenceKorean = json.optString("keySentenceKorean", headlineKorean)

            WorldNewsArticle(
                id = id,
                source = source,
                category = category,
                publishedTime = "온디바이스 AI 실시간 분석",
                headline = headline,
                headlineKorean = headlineKorean,
                summaryPoints = summaryPoints,
                fullScript = fullContent,
                keyVocabularies = keyVocabularies,
                keySentence = keySentence,
                keySentenceKorean = keySentenceKorean
            )
        } catch (_: Exception) {
            null
        }
    }
}
