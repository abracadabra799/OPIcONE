package com.example.myapplication.data.remote

import com.example.myapplication.data.model.PracticeCategory

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

    return listOf(
        "You are an OPIc (Oral Proficiency Interview-computer) speaking coach helping a Korean",
        "learner reach the highest level, OPIc AL. Generate $questionCount OPIc interview",
        "questions about ${category.promptTopic}, each with a model answer sentence a native",
        "AL-level speaker would give.",
        "",
        "Requirements for each model answer sentence:",
        "- Use natural, advanced English: complex/compound sentences, idiomatic expressions,",
        "  and discourse markers (e.g. \"what really stood out was...\", \"as opposed to...\").",
        "- Keep each sentence speakable in one breath (roughly 15-30 words).",
        "",
        "Requirements for the Korean hint:",
        "- Translate the English sentence into Korean word-for-word, preserving the English",
        "  word order exactly, with \" / \" separating the chunks that map to English phrases.",
        "  This is NOT natural Korean; it is a literal, English-ordered gloss meant to help the",
        "  learner rehearse English word order.",
        "",
        "Example item:",
        "{\"opic_question\": \"What do you enjoy doing in your free time?\", \"korean_hint\": \"나는 좋아한다 / 산책하는 것을 / 왜냐하면 / 그것이 나를 편안하게 해주기 때문에\", \"english_sentence\": \"I like taking walks because it makes me feel relaxed.\"}",
        "",
        "Do not repeat any of these questions already used in this session:",
        avoidSection,
        "",
        "Respond with ONLY a JSON array (no surrounding text) of exactly $questionCount objects,",
        "each shaped like: {\"opic_question\": string, \"korean_hint\": string, \"english_sentence\": string}"
    ).joinToString(separator = "\n")
}
