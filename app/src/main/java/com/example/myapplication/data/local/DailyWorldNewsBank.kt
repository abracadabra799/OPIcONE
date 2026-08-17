package com.example.myapplication.data.local

import com.example.myapplication.data.model.NewsVocabulary
import com.example.myapplication.data.model.VocabTag
import com.example.myapplication.data.model.WorldNewsArticle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DailyWorldNewsBank {

    fun getTodayFormattedDate(): String {
        return SimpleDateFormat("yyyy.MM.dd (E)", Locale.KOREAN).format(Date())
    }

    val todayNews: List<WorldNewsArticle>
        get() {
            val dateStr = getTodayFormattedDate()
            return listOf(
                WorldNewsArticle(
                    id = "news-1",
                    source = "The Korea Herald",
                    category = "Tech & AI",
                    publishedTime = dateStr,
                    headline = "South Korea Unveils Multi-Billion Dollar Plan to Lead Next-Gen AI Semiconductor Market",
                    headlineKorean = "한국 정부, 차세대 AI 반도체 시장 선도를 위한 수십조 원 규모 마스터플랜 발표",
                    summaryPoints = listOf(
                        "정부는 글로벌 AI 반도체 패권 경쟁에서 우위를 점하기 위해 차세대 뉴로모픽 및 저전력 AI 칩 연구개발에 대규모 민관 합동 펀드를 조성하기로 발표했습니다.",
                        "국내 유수 반도체 기업들과 협력하여 세계 최고 수준의 고대역폭 메모리(HBM) 및 온디바이스 NPU 생태계를 대폭 확장할 계획입니다.",
                        "전문가들은 이번 정책이 글로벌 빅테크 기업들과의 전략적 파트너십을 가속화하고 미래 기술 자립도를 비약적으로 끌어올릴 것으로 전망하고 있습니다."
                    ),
                    fullScript = "South Korea announced a landmark multi-billion dollar initiative on Monday aimed at establishing the country as a dominant global powerhouse in artificial intelligence semiconductors. Under the comprehensive roadmap unveiled by the Ministry of Science and ICT, the government plans to inject substantial public and private funding into advanced neural processing units, high-bandwidth memory architectures, and ultra-low-power computing chips. Industry leaders from Samsung Electronics and SK Hynix pledged close cooperation to construct state-of-the-art semiconductor clusters that integrate specialized AI fabrication lines with academic research centers. Analysts emphasize that securing domestic intellectual property in neuromorphic computing is pivotal to maintaining a competitive edge amidst escalating global chip rivalries.",
                    keyVocabularies = listOf(
                        NewsVocabulary(
                            word = "initiative",
                            phonetic = "/ɪˈnɪʃətɪv/",
                            partOfSpeech = "[명사]",
                            meaningKorean = "계획, 구상, 주도권",
                            exampleSentence = "The government announced a multi-billion dollar initiative to lead the AI semiconductor sector.",
                            tag = VocabTag.MUST_KNOW
                        ),
                        NewsVocabulary(
                            word = "fabrication",
                            phonetic = "/ˌfæbrɪˈkeɪʃn/",
                            partOfSpeech = "[명사]",
                            meaningKorean = "제조, 제작 (반도체 웨이퍼 공정)",
                            exampleSentence = "The new clusters will integrate specialized AI fabrication lines.",
                            tag = VocabTag.FREQUENT
                        ),
                        NewsVocabulary(
                            word = "pivotal",
                            phonetic = "/ˈpɪvətl/",
                            partOfSpeech = "[형용사]",
                            meaningKorean = "중추적인, 극히 중요한",
                            exampleSentence = "Securing domestic intellectual property is pivotal to maintaining a competitive edge.",
                            tag = VocabTag.MUST_KNOW
                        ),
                        NewsVocabulary(
                            word = "neuromorphic",
                            phonetic = "/ˌnjʊərəʊˈmɔːfɪk/",
                            partOfSpeech = "[형용사]",
                            meaningKorean = "인간 뇌 신경망 모방의 (뉴로모픽)",
                            exampleSentence = "Neuromorphic computing chips drastically reduce power consumption for edge devices.",
                            tag = VocabTag.ADVANCED
                        ),
                        NewsVocabulary(
                            word = "escalating",
                            phonetic = "/ˈeskəleɪtɪŋ/",
                            partOfSpeech = "[형용사]",
                            meaningKorean = "고조되는, 격화되는",
                            exampleSentence = "Chipmakers must innovate amidst escalating geopolitical trade rivalries.",
                            tag = VocabTag.FREQUENT
                        )
                    ),
                    keySentence = "Securing domestic intellectual property in next-generation computing is pivotal to maintaining a competitive edge.",
                    keySentenceKorean = "차세대 컴퓨팅 분야에서 독자적인 지식재산권을 확보하는 것은 경쟁 우위를 유지하는 데 중추적인 역할을 합니다."
                ),
                WorldNewsArticle(
                    id = "news-2",
                    source = "CNN Breaking",
                    category = "Global Tech",
                    publishedTime = dateStr,
                    headline = "Global Tech Giants Form International Coalition to Establish Safe On-Device AI Standards",
                    headlineKorean = "글로벌 빅테크 연합체, 안전한 온디바이스 AI 표준 정립을 위한 국제 컨소시엄 출범",
                    summaryPoints = listOf(
                        "구글, 오픈AI, 메타 등 주요 글로벌 IT 기업들이 스마트폰과 로컬 기기에서 구동되는 온디바이스 AI의 개인정보 보호 및 안전 표준을 정립하기 위해 뭉쳤습니다.",
                        "클라우드 전송 없이 기기 내부에서 모든 연산이 완결되도록 암호화 가이드라인과 책임 있는 AI 윤리 헌장을 제정했습니다.",
                        "이를 통해 사용자 데이터 유출 위험을 원천 차단하고 차세대 프라이버시 중심 AI 하드웨어 생태계를 선도할 방침입니다."
                    ),
                    fullScript = "Leading technology titans have officially formed an unprecedented international coalition to standardize on-device artificial intelligence protocols and bolster user privacy safeguards worldwide. The consortium, comprising pioneers across mobile operating systems, chipmakers, and foundation model researchers, published its inaugural framework designed to certify locally executable models that process sensitive personal information without transmitting data to centralized cloud servers. Spokespersons highlighted that keeping inference localized eliminates third-party intercept vulnerabilities while slashing latency to near-zero milliseconds. The initiative represents a pivotal turning point toward decentralized, ethical machine learning that prioritizes consumer sovereignty above all.",
                    keyVocabularies = listOf(
                        NewsVocabulary(
                            word = "coalition",
                            phonetic = "/ˌkəʊəˈlɪʃn/",
                            partOfSpeech = "[명사]",
                            meaningKorean = "연합, 제휴, 연립체",
                            exampleSentence = "Tech giants formed an international coalition to standardize AI safety protocols.",
                            tag = VocabTag.MUST_KNOW
                        ),
                        NewsVocabulary(
                            word = "inaugural",
                            phonetic = "/ɪˈnɔːɡjərəl/",
                            partOfSpeech = "[형용사]",
                            meaningKorean = "첫 번째의, 창간의, 취임의",
                            exampleSentence = "The consortium published its inaugural privacy framework today.",
                            tag = VocabTag.ADVANCED
                        ),
                        NewsVocabulary(
                            word = "sovereignty",
                            phonetic = "/ˈsɒvrənti/",
                            partOfSpeech = "[명사]",
                            meaningKorean = "주권, 자주권, 자기 통제권",
                            exampleSentence = "The standard prioritizes consumer data sovereignty over cloud centralization.",
                            tag = VocabTag.ADVANCED
                        ),
                        NewsVocabulary(
                            word = "decentralized",
                            phonetic = "/diːˈsentrəlaɪzd/",
                            partOfSpeech = "[형용사]",
                            meaningKorean = "탈중앙화된, 분산형의",
                            exampleSentence = "Decentralized on-device processing prevents widespread data leaks.",
                            tag = VocabTag.FREQUENT
                        ),
                        NewsVocabulary(
                            word = "vulnerability",
                            phonetic = "/ˌvʌlnərəˈbɪləti/",
                            partOfSpeech = "[명사]",
                            meaningKorean = "취약점, 상처받기 쉬움",
                            exampleSentence = "Localized computing eliminates network intercept vulnerabilities.",
                            tag = VocabTag.MUST_KNOW
                        )
                    ),
                    keySentence = "Keeping inference localized eliminates third-party intercept vulnerabilities while slashing latency to near-zero milliseconds.",
                    keySentenceKorean = "추론 연산을 기기 내부로 국한하는 것은 제3자의 데이터 탈취 취약점을 제거하는 동시에 지연 시간을 0ms에 가깝게 단축합니다."
                ),
                WorldNewsArticle(
                    id = "news-3",
                    source = "Reuters World",
                    category = "Global Economy",
                    publishedTime = dateStr,
                    headline = "Central Banks Signal Coordinated Soft-Landing as Inflation Cools Across Major Economies",
                    headlineKorean = "세계 주요국 중앙은행, 인플레이션 진정에 따른 경기 연착륙(Soft-Landing) 기대감 공식 시사",
                    summaryPoints = listOf(
                        "미국 연방준비제도와 유럽중앙은행은 최근 소비자물가지수(CPI)가 안정세를 보임에 따라 금리 인하 및 연착륙 궤도에 진입했음을 밝혔습니다.",
                        "고용 지표와 산업 생산성이 견조한 회복세를 유지하고 있어 글로벌 경기 침체에 대한 우려가 크게 완화되고 있습니다.",
                        "글로벌 금융 시장은 완화적 통화 정책에 힘입어 기술주와 친환경 에너지 섹터를 중심으로 강세를 이어가고 있습니다."
                    ),
                    fullScript = "Global central banking authorities signaled synchronized optimism regarding macroeconomic trajectories as persistent inflationary pressures cooled down significantly across Western economies. Following a series of monetary policy briefings, policymakers noted that core consumer price indices have steadily decelerated toward target thresholds without triggering a spike in unemployment. Economists widely commend the measured policy stance for steering major markets toward a coveted soft-landing rather than a protracted economic contraction. Investor sentiment surged in response, boosting equity benchmarks and reinvigorating cross-border venture capital transactions across burgeoning green energy and biotechnology industries.",
                    keyVocabularies = listOf(
                        NewsVocabulary(
                            word = "trajectory",
                            phonetic = "/trəˈdʒektəri/",
                            partOfSpeech = "[명사]",
                            meaningKorean = "궤적, 경로, 발전 추세",
                            exampleSentence = "Central banks signaled optimism regarding macroeconomic trajectories.",
                            tag = VocabTag.MUST_KNOW
                        ),
                        NewsVocabulary(
                            word = "decelerate",
                            phonetic = "/diːˈseləreɪt/",
                            partOfSpeech = "[동사]",
                            meaningKorean = "속도를 줄이다, 완화되다",
                            exampleSentence = "Core consumer price indices have steadily decelerated toward target thresholds.",
                            tag = VocabTag.FREQUENT
                        ),
                        NewsVocabulary(
                            word = "coveted",
                            phonetic = "/ˈkʌvətɪd/",
                            partOfSpeech = "[형용사]",
                            meaningKorean = "누구나 탐내는, 갈망하는",
                            exampleSentence = "Policymakers successfully steered the market toward a coveted soft-landing.",
                            tag = VocabTag.ADVANCED
                        ),
                        NewsVocabulary(
                            word = "protracted",
                            phonetic = "/prəˈtræktɪd/",
                            partOfSpeech = "[형용사]",
                            meaningKorean = "오래 끄는, 장기화된",
                            exampleSentence = "The central bank avoided a protracted economic contraction.",
                            tag = VocabTag.ADVANCED
                        ),
                        NewsVocabulary(
                            word = "reinvigorate",
                            phonetic = "/ˌriːɪnˈvɪɡəreɪt/",
                            partOfSpeech = "[동사]",
                            meaningKorean = "새로운 활력을 불어넣다, 부활시키다",
                            exampleSentence = "Lower interest rates reinvigorated venture capital investments worldwide.",
                            tag = VocabTag.FREQUENT
                        )
                    ),
                    keySentence = "Economists widely commend the measured policy stance for steering major markets toward a coveted soft-landing.",
                    keySentenceKorean = "경제학자들은 주요 시장을 누구나 갈망하던 경제 연착륙으로 이끈 신중한 통화 정책 기조를 널리 호평하고 있습니다."
                ),
                WorldNewsArticle(
                    id = "news-4",
                    source = "BBC News",
                    category = "Climate & Space",
                    publishedTime = dateStr,
                    headline = "James Webb Space Telescope Detects Atmospheric Water Vapor on Earth-Sized Exoplanet",
                    headlineKorean = "제임스 웹 우주망원경, 지구 크기 외계행성에서 대기 수증기 및 온화한 기후 신호 포착",
                    summaryPoints = listOf(
                        "NASA와 유럽우주국(ESA)은 제임스 웹 망원경이 약 40광년 떨어진 지구형 외계행성 대기에서 뚜렷한 수증기 흡수 스펙트럼을 검출했다고 발표했습니다.",
                        "이는 외계 생명체가 서식할 수 있는 거주 가능 구역(Goldilocks Zone) 내 행성에서 액체 상태의 물이 존재할 가능성을 크게 높이는 결정적 증거입니다.",
                        "천문학계는 향후 메탄과 오존 같은 추가 생명 징후(Biosignature) 분자를 탐색하는 후속 관측에 착수했습니다."
                    ),
                    fullScript = "In a breathtaking astronomical milestone, astrophysicists operating the James Webb Space Telescope announced the definitive detection of atmospheric water vapor envelopes surrounding a rocky Earth-sized exoplanet orbiting within its host star's habitable zone. Utilizing high-precision infrared transmission spectroscopy, the international research team observed distinct chemical absorption fingerprints as the planet transited in front of its red dwarf star. The presence of a stable, moisture-rich atmosphere suggests the planet has retained temperate planetary conditions capable of supporting liquid reservoirs on its surface. Scientists describe the discovery as a monumental leap forward in humanity's quest to discover habitable worlds beyond our solar system.",
                    keyVocabularies = listOf(
                        NewsVocabulary(
                            word = "astronomical",
                            phonetic = "/ˌæstrəˈnɒmɪkl/",
                            partOfSpeech = "[형용사]",
                            meaningKorean = "천문학의, 어마어마한",
                            exampleSentence = "The team achieved a breathtaking astronomical milestone.",
                            tag = VocabTag.MUST_KNOW
                        ),
                        NewsVocabulary(
                            word = "definitive",
                            phonetic = "/dɪˈfɪnətɪv/",
                            partOfSpeech = "[형용사]",
                            meaningKorean = "결정적인, 최종적인, 명확한",
                            exampleSentence = "Telescopes provided definitive detection of atmospheric water vapor.",
                            tag = VocabTag.MUST_KNOW
                        ),
                        NewsVocabulary(
                            word = "habitable",
                            phonetic = "/ˈhæbɪtəbl/",
                            partOfSpeech = "[형용사]",
                            meaningKorean = "거주 가능한, 서식에 적합한",
                            exampleSentence = "The exoplanet is located within its host star's habitable zone.",
                            tag = VocabTag.FREQUENT
                        ),
                        NewsVocabulary(
                            word = "temperate",
                            phonetic = "/ˈtempərət/",
                            partOfSpeech = "[형용사]",
                            meaningKorean = "온화한, 기후가 알맞은",
                            exampleSentence = "The planet retained temperate atmospheric conditions conducive to liquid water.",
                            tag = VocabTag.ADVANCED
                        ),
                        NewsVocabulary(
                            word = "spectroscopy",
                            phonetic = "/spekˈtrɒskəpi/",
                            partOfSpeech = "[명사]",
                            meaningKorean = "분광학, 분광 분석법",
                            exampleSentence = "Infrared spectroscopy reveals chemical compositions of distant planets.",
                            tag = VocabTag.ADVANCED
                        )
                    ),
                    keySentence = "The presence of a stable, moisture-rich atmosphere suggests the planet has retained temperate planetary conditions capable of supporting liquid water.",
                    keySentenceKorean = "안정적이고 수분이 풍부한 대기의 존재는 그 행성이 표면에 액체 상태의 물을 유지할 수 있는 온화한 행성 조건을 지니고 있음을 시사합니다."
                ),
                WorldNewsArticle(
                    id = "news-5",
                    source = "Bloomberg",
                    category = "Business & Society",
                    publishedTime = dateStr,
                    headline = "Four-Day Workweek Trials Across Global Corporations Yield Unprecedented Productivity Surges",
                    headlineKorean = "글로벌 대기업들의 주 4일제 시범 도입 결과, 업무 생산성과 직원 만족도 대폭 향상 입증",
                    summaryPoints = listOf(
                        "영국, 독일, 미국의 200여 개 글로벌 기업들이 1년간 진행한 주 4일 근무제(32시간) 실험에서 기업 매출이 평균 15% 이상 증가한 것으로 나타났습니다.",
                        "근로자들의 번아웃과 이직률은 60% 이상 급감하였으며, 집중 근무와 AI 업무 자동화 도입으로 회의 시간이 대폭 단축되었습니다.",
                        "주요 다국적 기업들은 이를 정규 제도로 공식 채택하고 최고 인재를 유치하기 위한 핵심 복지 전략으로 활용하고 있습니다."
                    ),
                    fullScript = "A comprehensive multi-country study examining the efficacy of four-day workweek models has revealed staggering gains in both commercial revenue and workforce well-being. Across more than two hundred multinational participating companies, operational revenues surged by an average of fifteen percent over the twelve-month trial period, while employee burnout rates plummeted by sixty-five percent. Executives attributed the astounding success to the elimination of redundant bureaucratic meetings, paired with the aggressive adoption of generative AI tools that automate administrative tasks. The overwhelming consensus indicates that modern knowledge workers deliver higher quality output when empowered with autonomy, proper rest, and flexible schedules.",
                    keyVocabularies = listOf(
                        NewsVocabulary(
                            word = "efficacy",
                            phonetic = "/ˈefɪkəsi/",
                            partOfSpeech = "[명사]",
                            meaningKorean = "효능, 유효성, 효율성",
                            exampleSentence = "Studies proved the remarkable efficacy of the four-day workweek model.",
                            tag = VocabTag.ADVANCED
                        ),
                        NewsVocabulary(
                            word = "plummet",
                            phonetic = "/ˈplʌmɪt/",
                            partOfSpeech = "[동사]",
                            meaningKorean = "급락하다, 곤두박질치다",
                            exampleSentence = "Employee burnout rates plummeted by sixty-five percent during the trial.",
                            tag = VocabTag.MUST_KNOW
                        ),
                        NewsVocabulary(
                            word = "redundant",
                            phonetic = "/rɪˈdʌndənt/",
                            partOfSpeech = "[형용사]",
                            meaningKorean = "불필요한, 중복되는, 군더더기의",
                            exampleSentence = "Companies achieved success by eliminating redundant bureaucratic meetings.",
                            tag = VocabTag.FREQUENT
                        ),
                        NewsVocabulary(
                            word = "consensus",
                            phonetic = "/kənˈsensəs/",
                            partOfSpeech = "[명사]",
                            meaningKorean = "의견 일치, 합의",
                            exampleSentence = "The general consensus is that flexible schedules maximize employee output.",
                            tag = VocabTag.MUST_KNOW
                        ),
                        NewsVocabulary(
                            word = "autonomy",
                            phonetic = "/ɔːˈtɒnəmi/",
                            partOfSpeech = "[명사]",
                            meaningKorean = "자율성, 자주성",
                            exampleSentence = "Workers deliver superior performance when granted workplace autonomy.",
                            tag = VocabTag.FREQUENT
                        )
                    ),
                    keySentence = "The overwhelming consensus indicates that modern knowledge workers deliver higher quality output when empowered with autonomy and proper rest.",
                    keySentenceKorean = "압도적인 공통 의견은 현대 지식 노동자들이 자율성과 충분한 휴식을 보장받을 때 훨씬 더 높은 품질의 성과를 창출한다는 점을 보여줍니다."
                )
            )
        }

    fun getNews(): List<WorldNewsArticle> = todayNews

    fun getNewsById(id: String): WorldNewsArticle? = todayNews.firstOrNull { it.id == id }
}
