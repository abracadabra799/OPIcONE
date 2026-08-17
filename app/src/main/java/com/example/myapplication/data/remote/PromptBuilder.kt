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
        "You are an expert OPIc speaking coach preparing a senior Korean software developer for the highest level, OPIc AL.",
        "Generate $questionCount authentic OPIc interview questions about ${category.promptTopic}, tailored to the candidate's real persona.",
        "",
        "Candidate Profile & Persona:",
        "- Career: 20-year veteran Mobile Media Software Engineer. Truly passionate about coding, and currently experimenting with Generative AI tools and automation scripts to optimize workflows.",
        "- Family & Parenting: Raising an 8th-grade (14-year-old) daughter. The daughter is strong-willed and sensitive, which challenged the speaker but fostered immense emotional maturity, patience, and a peaceful 'go-with-the-flow' philosophy without comparing oneself to others.",
        "- Pets: Raising a beloved Maltese dog and a pet rabbit for 7 years (originally adopted because the daughter wanted them). The Maltese is an irreplaceable, vital emotional anchor.",
        "- Home: A 4-room apartment. Favorite spot is the sunlit living room featuring a personal workstation, laptop, bookshelf, and a breathtaking panoramic Lake Park view.",
        "- Fitness & Wellness: Practices Zumba, lower-body workouts, running, and walking. Zumba was a lifesaver during burnout/parenting stress, bringing pure joy through upbeat music and steady weight loss. Goal is to lose more weight to try rock climbing.",
        "- Leisure: Enjoys reading, educational YouTube channels, documentaries, and is currently hyped about 'The Odyssey' movie interviews/trailers.",
        "",
        "Requirements for each model answer paragraph:",
        "- Provide an authentic, fully fleshed-out AL-level master paragraph composed of 5-7 connected sentences (roughly 100-130 words).",
        "- Structure with the 4-phase AL framework: (1) Engaging introduction/hook, (2) Detailed, vivid real-life narrative, (3) Emotional maturity & personal reflection, (4) Natural wrap-up and forward-looking conclusion.",
        "- Integrate the candidate's authentic persona naturally.",
        "- Use advanced discourse markers (e.g. \"To kick things off...\", \"Without a shadow of a doubt...\", \"What really stood out was...\", \"On top of that...\", \"Looking back on it...\", \"All things considered...\"), complex clauses, and natural idiomatic expressions.",
        "",
        "Requirements for the Korean hint:",
        "- Translate the entire paragraph into Korean following the exact English word order sentence-by-sentence.",
        "- Separate phrase chunks with \" / \" and separate sentences with \" // \".",
        "",
        "Do not repeat any of these questions already used in this session:",
        avoidSection,
        "",
        "Respond with ONLY a JSON array (no surrounding text) of exactly $questionCount objects,",
        "each shaped like: {\"opic_question\": string, \"korean_hint\": string, \"english_sentence\": string}"
    ).joinToString(separator = "\n")
}
