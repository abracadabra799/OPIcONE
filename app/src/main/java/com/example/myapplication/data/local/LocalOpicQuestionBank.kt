package com.example.myapplication.data.local

import com.example.myapplication.data.model.PracticeCategory
import com.example.myapplication.data.model.PracticeQuestion

data class AnswerVariation(
    val koreanHint: String,
    val englishSentence: String
)

data class QuestionTemplate(
    val opicQuestion: String,
    val category: PracticeCategory,
    val variations: List<AnswerVariation>
)

object LocalOpicQuestionBank {

    private val bank: Map<PracticeCategory, List<QuestionTemplate>> = mapOf(
        PracticeCategory.SELF_INTRODUCTION to listOf(
            QuestionTemplate(
                opicQuestion = "Let's start the interview. Can you tell me a little bit about yourself?",
                category = PracticeCategory.SELF_INTRODUCTION,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "의심의 여지 없이 / 제 삶을 가장 잘 대변하는 정체성은 20년 경력의 모바일 미디어 소프트웨어 엔지니어입니다. // 저는 스마트폰에 탑재되는 핵심 오디오 코덱, 비디오 렌더링, 실시간 스트리밍 프레임워크를 개발해 왔으며 / 코딩 그 자체를 여전히 진심으로 사랑합니다. // 특히 최근에는 최신 생성형 AI 도구와 자동화 스크립트를 업무에 적극 도입하여 / 개발 효율과 재미를 동시에 만끽하고 있습니다. // 가정에서는 사춘기 중학교 2학년 딸아이를 양육하며 / 사람과 세상을 있는 그대로 받아들이는 깊은 인내심과 어른으로서의 내적 성장을 배웠습니다. // 또한 7년 전 입양한 소중한 말티즈 반려견과 온순한 토끼는 / 제 지친 일상에 무조건적인 사랑을 선물해 주는 최고의 힐링제입니다. // 여가 시간에는 신나는 음악에 맞춰 줌바 댄스를 추며 체중을 감량하고 있고 / 궁극적으로는 실내외 암벽 등반에 도전하는 멋진 꿈을 키워가고 있습니다. // 모든 것을 고려할 때 / 저는 남들과 비교하지 않고 나만의 속도로 유연하고 행복하게 삶을 채워가고 있습니다.",
                        englishSentence = "Without a shadow of a doubt, the defining hallmark of my life is my twenty-year career as a senior mobile media software engineer. I specialize in architecting core audio-video codecs, hardware-accelerated rendering, and streaming pipelines for smartphones, and I genuinely love coding with all my heart. Lately, I have been passionately experimenting with generative AI automation tools to streamline workflows, making software development more exhilarating than ever. On the personal front, raising my sensitive eighth-grade daughter has taught me profound patience and the wisdom of accepting people just as they are. Furthermore, our seven-year-old Maltese dog and gentle rabbit bring boundless unconditional warmth into our home. To stay fit, I regularly practice high-energy Zumba dance, steadily shedding weight with the ultimate ambition of conquering technical rock climbing. All things considered, I strive to live peacefully without unnecessary comparisons, embracing every single day with gratitude."
                    ),
                    AnswerVariation(
                        koreanHint = "반갑습니다, 저는 기술에 대한 순수한 열정과 따뜻한 일상의 균형을 소중히 여기는 개발자입니다. // 모바일 미디어 시스템 분야에서 20년간 한 우물을 파왔으며 / 끊임없이 등장하는 새로운 기술을 학습하는 것에서 커다란 희열을 느낍니다. // 저는 호수공원이 한눈에 내려다보이는 방 4개짜리 아파트에 살고 있으며 / 거실 서재 창가에서 호수 뷰를 바라보며 코딩하는 시간을 가장 아낍니다. // 예민한 사춘기 딸아이와의 갈등을 겪으며 '다 저마다의 이유가 있다'는 삶의 깊은 이치를 깨달았고 / 덕분에 마음에 넉넉한 여유가 생겼습니다. // 우리 집 말티즈 강아지는 제가 번아웃에 빠질 때마다 곁을 지켜준 둘도 없는 가족이자 영혼의 동반자입니다. // 여기에 신나는 줌바 댄스와 인문학 다큐멘터리 시청까지 곁들이며 / 균형 잡힌 웰빙 라이프스타일을 지속해 나가고 있습니다.",
                        englishSentence = "Hello, I am a seasoned software professional who treasures the delicate harmony between relentless technical innovation and a grounded personal life. Having dedicated two decades to mobile multimedia architectures, I thrive on exploring novel frameworks and leveraging modern AI assistants to automate engineering chores. At home, I reside in a bright four-bedroom apartment overlooking a sprawling lake park, where my favorite workstation is situated right by the scenic panoramic window. Parenting my independent teenage daughter reshaped my perspective, teaching me that everyone acts out of their own internal reasons and instilling profound emotional calmness. Our loyal Maltese dog has been an indispensable emotional anchor, comforting me through every demanding software deadline. Balanced by dynamic Zumba dance workouts and intellectual documentary viewing, I enjoy an authentic lifestyle grounded in peace and vitality."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Please tell me more about your family, pets, and what your daily life looks like.",
                category = PracticeCategory.SELF_INTRODUCTION,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "저희 가족은 저와 중학교 2학년 딸아이, 그리고 7년째 함께 생활하고 있는 말티즈 강아지와 토끼로 이루어져 있습니다. // 솔직히 고백하자면 / 사춘기 딸아이가 워낙 까다롭고 예민해서 많은 육아 갈등과 고충을 겪어야 했습니다. // 하지만 그 어려운 과정 속에서 자녀를 소유물이 아닌 독립된 인격체로 존중하는 법을 배웠고 / 덕분에 제 마음의 그릇이 훨씬 넓어졌습니다. // 7년 전 딸아이의 소원으로 입양했던 말티즈는 / 이제 제가 힘들 때마다 말없이 손등을 핥아주는 제 인생에서 가장 소중한 존재가 되었습니다. // 아침에는 거실 서재에서 호수공원 전망을 보며 AI 미디어 개발을 하고 / 저녁에는 말티즈 산책과 줌바 댄스로 땀을 흘립니다. // 남들과 비교하지 않고 물 흐르듯 유연하게 살아가는 것이 / 제가 하루하루를 온전히 누리는 비결입니다.",
                        englishSentence = "My household consists of my eighth-grade daughter, alongside a white Maltese dog and a gentle rabbit whom we adopted seven years ago at my daughter's plea. To be completely candid, raising a strong-willed, sensitive teenager has presented immense emotional challenges and steep learning curves. However, navigating those friction points taught me to respect her autonomy and deepened my emotional maturity beyond measure. Our Maltese dog, initially brought home for my child, has evolved into my greatest confidant, quietly resting by my side whenever fatigue overwhelms me. My daily routine involves coding at my sunlit living room desk facing the lake, followed by evening dog walks and energetic Zumba sessions. Living authentically without comparing myself to others is the secret behind my grounded daily happiness."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "What are your core personality traits and your outlook on life?",
                category = PracticeCategory.SELF_INTRODUCTION,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "제 성격의 가장 두드러진 특징은 차분한 회복탄력성과 타인에 대한 깊은 공감 능력입니다. // 20대 젊은 시절에는 완벽주의에 사로잡혀 작은 실수에도 자책하고 남들과 끊임없이 비교하며 스스로를 괴롭혔습니다. // 하지만 20년간 수많은 복잡한 모바일 프로젝트를 완수하고 까다로운 사춘기 자녀를 양육하면서 / 모든 사람과 상황에는 저마다의 타당한 이유가 있음을 깨달았습니다. // 이제는 어떤 힘든 일이나 갈등이 닥쳐도 조급해하지 않고 / 마음에 여유를 두고 순리대로 상황을 풀어나갑니다. // 신나는 줌바 댄스로 스트레스를 비워내고 거실 창밖의 호수 뷰를 바라보며 / 나만의 페이스로 살아가는 삶의 태도가 제 가장 큰 자산입니다.",
                        englishSentence = "My core personality traits center around tranquil emotional resilience and deep empathetic understanding. In my early twenties, I was plagued by rigid perfectionism, constantly judging my worth against peers with chronic anxiety. However, leading high-stakes mobile software deliveries for twenty years and parenting an independent daughter taught me that everyone carries their own distinct circumstances and reasons. Nowadays, whenever unexpected friction or setbacks emerge, I remain composed and allow situations to unfold naturally without forcing outcomes. Discharging stress through energetic Zumba while admiring the tranquil lake vista keeps me anchored in serene self-acceptance."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "What motivates you in your career and daily personal growth?",
                category = PracticeCategory.SELF_INTRODUCTION,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "저를 끊임없이 앞으로 나아가게 하는 핵심 원동력은 / 새로운 기술에 대한 순수한 호기심과 어제보다 나아지려는 열정입니다. // 20년 동안 모바일 미디어 SW 개발에 몸담아 왔지만 / 최근의 생성형 AI와 자동화 기술은 저에게 신입 개발자 시절의 설렘을 다시 안겨주었습니다. // 업무에서는 AI 도구를 접목해 최고의 효율을 창출하고 / 퇴근 후에는 줌바와 하체 근력운동으로 건강한 신체를 단련합니다. // 매년 조금씩 체중을 줄여가며 마침내 암벽 등반에 성공하는 제 모습을 상상할 때마다 가슴이 뜁니다. // 지적인 성장과 신체적 도전을 멈추지 않는 것이 / 제 일상을 활기차게 만드는 강력한 에너지입니다.",
                        englishSentence = "The primary catalyst driving my daily life is an unquenchable curiosity for emerging technologies paired with a commitment to continuous self-improvement. Even after two decades in mobile multimedia engineering, the rise of generative AI tools has reignited the raw enthusiasm of my early rookie days. At work, I architect automated workflows to maximize productivity, while after hours, I condition my physical strength through Zumba and lower-body workouts. Envisioning myself scaling technical rock climbing routes after reaching my target weight fills me with immense motivation. Pursuing intellectual innovation alongside physical milestones keeps my everyday existence vibrant and purposeful."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "How do you balance your demanding engineering work with your personal life?",
                category = PracticeCategory.SELF_INTRODUCTION,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "치열한 IT 소프트웨어 업계에서 롱런하기 위해 / 저는 일과 개인 생활 사이에 명확하고 건강한 경계선을 유지하고 있습니다. // 거실 서재에 마련된 나만의 작업대에서 집중하여 개발을 마치면 / 노트북을 닫고 온전히 가족과 반려동물에게 집중합니다. // 매일 저녁 말티즈와 함께 호수공원을 40분간 산책하며 맑은 공기를 마시고 / 주 3회 이상 신나는 줌바 댄스로 땀을 흘리며 업무 스트레스를 완벽히 털어냅니다. // 밤에는 따뜻한 차 한 잔과 함께 인문학 서적을 읽거나 영화 '오디세이' 관련 다큐멘터리를 시청하며 마음을 정돈합니다. // 이처럼 비움과 채움이 조화를 이루는 루틴 덕분에 / 20년 차 엔지니어로서도 번아웃 없이 행복한 에너지를 지속하고 있습니다.",
                        englishSentence = "To sustain long-term excellence in the demanding tech industry, I maintain strict, intentional boundaries between high-intensity coding and personal rejuvenation. Once I conclude my software tasks at my home study, I shut my laptop and dedicate myself entirely to my family and beloved pets. Every evening, taking my Maltese dog on a scenic forty-minute walk around the lake park clears my mind, while high-tempo Zumba sessions effectively incinerate work tension. Before bed, sipping herbal tea while reading literature or watching documentaries about 'The Odyssey' restores mental calm. This disciplined rhythm of working hard and unwinding mindfully preserves my well-being without burnout."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Could you summarize your passions, hobbies, and personal aspirations for the future?",
                category = PracticeCategory.SELF_INTRODUCTION,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "요약하자면, 저는 개발의 즐거움을 사랑하는 20년 차 엔지니어이자 / 줌바 댄스와 반려동물에게서 삶의 에너지를 얻는 사람입니다. // 앞으로의 전문적인 목표는 최신 AI 기술을 미디어 SW에 깊이 있게 통합하여 혁신적인 도구를 지속적으로 만드는 것입니다. // 개인적인 피트니스 목표로는 꾸준한 줌바와 식단 관리로 체중을 더 감량하여 / 버킷리스트인 실내외 암벽 등반(볼더링)을 멋지게 완등하는 것입니다. // 무엇보다 남의 시선에 얽매이지 않고 / 지금처럼 가족, 말티즈, 토끼와 함께 호수 뷰 아파트에서 평온한 행복을 누리며 살아가고 싶습니다.",
                        englishSentence = "To summarize, I am a passionate veteran engineer who thrives on software innovation, energized by dynamic Zumba dance and the unconditional love of my pets. Professionally, my aspiration is to harness advanced AI paradigms to architect next-generation mobile media tools that redefine engineering efficiency. On the fitness front, I am committed to continuing my weight-loss journey to finally conquer indoor and outdoor rock climbing routes. Above all, my ultimate personal goal is to live authentically without comparing myself to others, cherishing simple daily blessings in our lake-view home."
                    )
                )
            )
        ),
        PracticeCategory.HOUSING to listOf(
            QuestionTemplate(
                opicQuestion = "Can you describe your home to me? What does it look like, and which room is your favorite?",
                category = PracticeCategory.HOUSING,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "저는 쾌적한 호수공원 바로 앞에 위치한 방 4개짜리 넓고 아늑한 아파트에 거주하고 있습니다. // 집 전체에서 제가 가장 아끼고 자랑하고 싶은 공간은 / 하루 종일 따스한 햇살이 풍부하게 쏟아져 들어오는 남향 거실입니다. // 거실 창가 한편에는 제 개인 원목 책상과 노트북, 듀얼 모니터, 서재 코너가 완벽하게 갖추어져 있어 재택근무를 쾌적하게 수행할 수 있습니다. // 무엇보다 거실 통유리창 너머로 호수공원까지 막힘없이 확 트인 파노라마 전망은 / 사계절 내내 숨이 멎을 듯 아름다운 절경을 선사합니다. // 창밖의 고요한 호수 물결을 바라보며 코딩을 하거나 책을 읽을 때면 / 세상 그 어떤 고급 리조트 부럽지 않은 평온함과 축복을 느낍니다.",
                        englishSentence = "I reside in a spacious four-bedroom apartment situated directly across from a picturesque lake park. Without a doubt, my absolute favorite space in the entire home is our expansive, south-facing living room, which is flooded with glorious natural sunlight from dawn until dusk. In the sunlit corner by the window, I have established a personal study equipped with an ergonomic wooden desk, dual monitors, and my laptop for seamless remote software engineering. Above all, the floor-to-ceiling panoramic windows offer an unobstructed, breathtaking vista stretching across the shimmering lake park. Gtazing out at the serene waters while writing code or reading literature brings a profound sense of tranquility that rivals any luxury resort."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "What kind of changes or improvements have you made to your home recently?",
                category = PracticeCategory.HOUSING,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "최근 거실 공간을 전면적으로 재배치하여 / 업무 생산성과 힐링을 극대화한 맞춤형 스마트 서재 코너를 완성했습니다. // 창가 바로 옆 명당자리에 넓은 모션 데스크와 모니터 암을 설치하여 / 호수공원 전망을 바로 내려다보며 개발 작업을 할 수 있도록 최적화했습니다. // 또한 책상 주변에 은은한 웜톤 간접 조명과 미니멀한 책장을 배치하여 / 저녁에는 독서와 다큐멘터리 감상을 즐길 수 있는 아늑한 북카페 분위기를 연출했습니다. // 반려견 말티즈와 토끼의 편안한 방석 공간도 책상 옆에 자연스럽게 녹여내어 / 일과 휴식이 완벽히 공존하는 최고의 공간으로 거듭났습니다.",
                        englishSentence = "I recently reorganized our living room layout to establish a dedicated smart home office and library corner that maximizes both productivity and relaxation. Placing an ergonomic standing desk and adjustable monitor arm directly beside the main window allows me to engineer software while soaking in the panoramic lake park views. Additionally, I integrated warm ambient lighting and minimalist bookshelves, cultivating an inviting bookstore atmosphere perfect for evening reading. Positioning cozy resting mats for our Maltese dog and rabbit right beside my workstation completed a harmonious sanctuary where work and leisure seamlessly coexist."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Tell me about the view from your apartment windows and what makes it special.",
                category = PracticeCategory.HOUSING,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "저희 집 거실 창밖으로 펼쳐지는 호수공원 파노라마 전망은 / 매일매일 새로운 감동을 주는 살아있는 예술 작품과도 같습니다. // 봄에는 분홍빛 벚꽃길이, 여름에는 싱그러운 녹음과 반짝이는 호수 수면이, 가을과 겨울에는 고즈넉한 갈대밭과 설경이 눈앞에 파노라마로 펼쳐집니다. // 특히 해 질 무렵 붉은 노을이 하늘과 호수를 오렌지빛으로 물들이는 광경은 / 20년 차 개발자의 모든 업무 피로를 한순간에 씻어내 줍니다. // 답답한 도심 빌딩 숲 대신 탁 트인 하늘과 호수를 매일 감상할 수 있다는 것은 / 우리 가족에게 최고의 행운이자 힐링입니다.",
                        englishSentence = "The sweeping panoramic view of the lake park from our living room windows is like an ever-changing masterpiece of natural art. In spring, delicate cherry blossoms frame the water, summer brings lush greenery and glistening ripples, while autumn and winter showcase golden reeds and pristine snowscapes. Sunset is particularly breathtaking, as radiant amber hues reflect across the tranquil water, instantly washing away all accumulated engineering fatigue. Living with an unobstructed vista of open skies and calm waters rather than congested concrete towers is an extraordinary daily blessing."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "How do you and your family spend time together in your living room?",
                category = PracticeCategory.HOUSING,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "방 4개 구조로 가족 개개인의 독립된 침실이 있지만 / 저녁이나 주말이 되면 온 가족과 반려동물들이 자연스럽게 거실로 모입니다. // 저는 책상에 앉아 노트북으로 코딩을 하거나 교양 유튜브를 보고 / 딸아이는 소파에서 편안하게 휴식을 취하며 각자의 시간을 보냅니다. // 거실 카펫 위에서는 말티즈 강아지가 애교를 부리고 토끼가 조용히 건초를 먹으며 평화로운 온기를 더해줍니다. // 특별히 거창한 대화를 나누지 않더라도 / 따스한 햇살과 호수 뷰를 공유하며 서로의 존재를 느끼는 것만으로도 깊은 유대감과 안식을 얻습니다.",
                        englishSentence = "Although our four-bedroom layout grants private personal rooms, our family and pets naturally congregate in the spacious living room every evening. I code on my laptop or stream educational YouTube essays at my workstation, while my daughter unwinds comfortably on the sofa. Down on the carpet, our Maltese dog playful cuddles while our rabbit peacefully munches hay, infusing the room with gentle harmony. Even without constant conversation, sharing the sunlit space against the lake backdrop fosters immense comfort and familial connection."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "What are the advantages of living near a large lake park?",
                category = PracticeCategory.HOUSING,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "대형 호수공원 바로 곁에 거주하는 것의 가장 독보적인 장점은 / 건강한 웰빙 라이프를 매일 일상처럼 손쉽게 실천할 수 있다는 점입니다. // 현관문을 열고 나가면 3분 만에 아름답게 가꿔진 수변 산책로와 우레탄 러닝 트랙에 진입할 수 있습니다. // 매일 저녁 말티즈를 데리고 안전하게 산책을 시킬 수 있고 / 주말 아침에는 상쾌한 호수 바람을 맞으며 5km 인터벌 러닝을 즐깁니다. // 맑은 공기와 풍부한 녹지 공간 덕분에 / 스트레스 해소는 물론 줌바와 연계한 체중 감량 목표 달성에도 결정적인 도움을 받고 있습니다.",
                        englishSentence = "The most remarkable advantage of living adjacent to a vast lake park is having pristine outdoor recreation seamlessly integrated into daily life. Stepping outside places us onto beautifully maintained waterside trails and rubberized jogging tracks within three minutes. It provides a safe, scenic environment for daily walks with our Maltese dog and invigorating weekend runs along the perimeter. Breathing fresh air surrounded by open greenery accelerates my stress recovery and supports my fitness conditioning immensely."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "If you could add or upgrade one thing in your apartment, what would it be?",
                category = PracticeCategory.HOUSING,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "현재 아파트 환경에 매우 만족하고 있지만 / 만약 한 가지를 업그레이드할 수 있다면 발코니 한편에 전문적인 실내 클라이밍 행보드를 설치하고 싶습니다. // 줌바 댄스와 하체 운동으로 꾸준히 체중을 감량하며 암벽 등반(볼더링) 도전을 준비하고 있기 때문입니다. // 탁 트인 호수공원 전망을 바라보며 손가락 악력과 상체 코어 근육을 단련할 수 있는 전용 트레이닝 존이 있다면 환상적일 것입니다. // 자연 풍경을 감상하며 꿈꾸던 스포츠를 준비하는 홈 트레이닝 공간이 완성된다면 / 일상이 한층 더 에너제틱해질 것입니다.",
                        englishSentence = "While I am thoroughly content with our current apartment, my dream upgrade would be installing a specialized climbing training hangboard near the panoramic balcony. As I consistently shed weight through Zumba to prepare for technical rock climbing, having a dedicated grip-strength station overlooking the lake would be phenomenal. Exercising against such a picturesque natural vista would inspire my fitness journey and elevate my athletic conditioning to new heights."
                    )
                )
            )
        ),
        PracticeCategory.WORK_OR_SCHOOL to listOf(
            QuestionTemplate(
                opicQuestion = "You indicated that you work. Can you describe your job and what your typical responsibilities are?",
                category = PracticeCategory.WORK_OR_SCHOOL,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "저는 20년 동안 모바일 스마트폰에 들어가는 미디어 관련 핵심 소프트웨어를 설계하고 개발해 온 시니어 엔지니어입니다. // 스마트폰에서 4K 고화질 비디오 재생, 초저지연 오디오 스트리밍, 하드웨어 가속 코덱이 끊김 없이 매끄럽게 동작하도록 최적화하는 것이 제 주 업무입니다. // 오랜 경력에도 불구하고 저는 복잡한 아키텍처 문제를 해결하고 코딩하는 그 자체에서 여전히 깊은 순수한 즐거움을 느낍니다. // 특히 최근에는 최신 생성형 AI 코딩 도구들을 업무 파이프라인에 전격 도입하여 / 자동화 스크립트와 생산성 툴을 직접 구축하며 개발 효율을 비약적으로 끌어올리고 있습니다. // 변화하는 기술 트렌드를 즐겁게 흡수하며 고품질 미디어 엔진을 만드는 것이 제 자부심입니다.",
                        englishSentence = "I am a veteran software engineer with two decades of deep specialization in mobile multimedia frameworks for smartphones. My primary responsibility centers around architecting high-performance audio-video streaming engines, multimedia codecs, and hardware-accelerated processing pipelines with near-zero latency. Despite my extensive tenure, I harbor an unyielding passion for programming and genuinely thrill at untangling complex architectural bottlenecks. Lately, integrating generative AI copilots and scripting custom automation utilities has turbocharged my workflow, making development more exciting than ever. Continually evolving alongside cutting-edge technologies to engineer flawless media experiences is my greatest professional pride."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "How do you use AI tools and automation scripts in your daily software engineering workflow?",
                category = PracticeCategory.WORK_OR_SCHOOL,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "저의 일상적인 소프트웨어 개발 워크플로우는 / 지능형 AI 도구와 자체 제작한 자동화 스크립트로 고도화되어 있습니다. // 단순 반복적인 보일러플레이트 코드 작성, 복잡한 미디어 코덱 단위 테스트 생성, 레거시 C++ 코드의 최신 리팩토링 작업을 AI 도구를 통해 신속하게 처리합니다. // 이를 통해 과거 수작업으로 며칠씩 소요되던 지루한 작업 시간을 80% 이상 단축할 수 있게 되었습니다. // 절약된 귀중한 시간과 에너지는 / 고난도 모바일 미디어 시스템 아키텍처 설계와 성능 최적화 같은 핵심 본질에 온전히 집중합니다. // AI를 통해 개발의 생산성과 창의성을 동시에 극대화하는 과정 자체가 / 저에게 커다란 지적 희열을 안겨줍니다.",
                        englishSentence = "My daily engineering workflow is deeply augmented by state-of-the-art generative AI tools and custom automation scripts. I routinely automate repetitive boilerplate coding, comprehensive unit test generation, and legacy C++ media framework refactoring using intelligent AI assistants. This strategic automation slashes turnaround times by over eighty percent on tedious tasks that once took days of manual effort. Consequently, I can channel my mental bandwidth entirely into high-impact system architecture, hardware profiling, and performance tuning. Leveraging AI to amplify both engineering velocity and creative problem-solving brings immense intellectual satisfaction."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Can you tell me about a project or achievement at work that was particularly memorable?",
                category = PracticeCategory.WORK_OR_SCHOOL,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "제 20년 커리어에서 가장 보람차고 기억에 남는 프로젝트는 / 사내 모바일 미디어 검증 파이프라인에 AI 기반 자동화 시스템을 성공적으로 구축했던 일입니다. // 스마트폰 기기별로 수백 가지에 달하는 비디오 코덱 호환성을 수작업으로 테스트하느라 팀 전체가 극심한 야근에 시달리고 있었습니다. // 저는 최신 LLM API와 자동화 스크립트를 결합하여 / 코드 커밋 시 실시간으로 호환성과 메모리 누수를 감지하는 원클릭 검증 시스템을 개발했습니다. // 빌드 검증 시간이 며칠에서 몇 분으로 단축되면서 팀원들로부터 뜨거운 찬사를 받았고 / 20년 도메인 노하우와 최신 AI 기술의 완벽한 결합을 입증한 뜻깊은 성과였습니다.",
                        englishSentence = "A standout milestone in my twenty-year career was spearheading an AI-powered automated validation pipeline for our smartphone multimedia framework. Our engineering team was chronically burdened by manual cross-device testing across hundreds of intricate video codec profiles. To resolve this bottleneck, I architected an intelligent CI/CD automation suite that detects regressions and memory anomalies instantaneously upon code commits. Reducing validation turnaround times from days to mere minutes earned enthusiastic praise from colleagues and stood as a proud testament to merging veteran domain mastery with modern AI innovations."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "What are the biggest challenges you face as a senior mobile media software developer?",
                category = PracticeCategory.WORK_OR_SCHOOL,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "모바일 미디어 소프트웨어 개발에서 가장 까다로운 도전 과제는 / 한정된 스마트폰 배터리와 메모리 제약 속에서 초고화질 미디어를 매끄럽게 처리하는 것입니다. // 발열과 배터리 소모를 철저히 억제하면서도 4K 60프레임 비디오와 입체 공간 음향을 실시간으로 렌더링해야 하므로 극한의 최적화가 필수적입니다. // 저는 20년간 축적된 로우레벨 디버깅 경험과 최신 AI 프로파일링 기법을 총동원하여 미세한 병목 현상을 정밀하게 튜닝합니다. // 하드웨어 한계를 뛰어넘어 사용자에게 완벽한 멀티미디어 몰입감을 선사할 때 / 엔지니어로서 말로 다 할 수 없는 성취감을 느낍니다.",
                        englishSentence = "The most formidable challenge in mobile multimedia engineering is delivering ultra-high-definition rendering under severe thermal and battery constraints. We must process 4K 60fps video streams and spatial audio in real time without triggering thermal throttling or excessive battery drain. To overcome these hurdles, I combine two decades of low-level optimization mastery with modern automated profiling tools to fine-tune memory footprints. Overcoming hardware limitations to deliver a flawless, immersive multimedia experience brings unmatched professional gratification."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "How has the mobile software industry changed over your 20-year career?",
                category = PracticeCategory.WORK_OR_SCHOOL,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "지난 20년 동안 모바일 소프트웨어 산업은 / 단순한 피처폰 임베디드 코딩에서 초연결 온디바이스 AI 시대로 눈부신 대변혁을 겪었습니다. // 20년 전에는 킬로바이트 단위의 메모리를 아끼기 위해 며칠 밤을 새우며 C/C++ 어셈블리 레벨까지 수작업으로 최적화해야 했습니다. // 반면 오늘날에는 NPU 하드웨어 가속과 지능형 AI 코파일럿이 복잡한 연산을 실시간으로 보조해 줍니다. // 변화의 속도가 매우 빠른 업계이지만 / 끊임없이 새로운 프레임워크를 학습하고 도전해 온 덕분에 20년 차에도 최고 수준의 경쟁력을 유지할 수 있었습니다.",
                        englishSentence = "Over my twenty-year career, the mobile software industry has undergone a monumental transformation from primitive feature phone coding to sophisticated on-device AI ecosystems. In the early days, we painstakingly scrutinized every kilobyte of memory, enduring exhausting overnight debugging sessions in low-level C++. Today, dedicated neural processing hardware and AI copilots empower engineers to build highly scalable media architectures seamlessly. Embracing rapid industry shifts with continuous learning has allowed me to thrive at the cutting edge throughout this technological revolution."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "What advice would you give to junior software developers entering the industry?",
                category = PracticeCategory.WORK_OR_SCHOOL,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "후배 개발자들에게 제가 가장 강조하고 싶은 조언은 / 트렌드에 휩쓸려 조급해하지 말고 문제 해결과 코딩 그 자체를 즐기라는 것입니다. // 컴퓨터 구조와 알고리즘 같은 탄탄한 기초 원리를 다지면서 / 최신 AI 자동화 도구를 영리하게 활용할 줄 아는 유연한 태도가 중요합니다. // 또한 남들과 자신의 성취를 비교하며 불안해하기보다 / 어제보다 한 줄 더 나은 코드를 작성하는 나만의 성장에 집중해야 롱런할 수 있습니다. // 개발을 사랑하는 진심과 꾸준함이 뒷받침될 때 / 누구나 대체 불가능한 훌륭한 엔지니어로 성장할 수 있습니다.",
                        englishSentence = "My foremost piece of advice for aspiring developers is to cultivate an authentic love for problem-solving rather than becoming overwhelmed by fast-moving trends. Grounding oneself in fundamental computer science principles while proactively mastering AI productivity tools creates an unshakeable foundation. Furthermore, avoiding anxiety-inducing peer comparisons and focusing on steady daily mastery is the key to enduring career longevity. With genuine passion and persistent curiosity, anyone can evolve into an exceptional, irreplaceable engineer."
                    )
                )
            )
        ),
        PracticeCategory.HOBBY to listOf(
            QuestionTemplate(
                opicQuestion = "What kind of exercise or sports do you enjoy doing to stay healthy?",
                category = PracticeCategory.HOBBY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "저는 건강과 멘탈 관리를 위해 신나는 줌바 댄스를 중심으로 / 하체 근력운동과 호수공원 조깅 및 걷기를 꾸준히 실천하고 있습니다. // 특히 줌바 댄스는 경쾌한 라틴 음악과 다양한 비트에 맞춰 온몸을 움직이는 전신 유산소 운동입니다. // 음악에 몸을 맡기고 땀을 흠뻑 흘리고 나면 / 20년 차 개발자로서 쌓였던 모든 스트레스와 복잡한 잡념이 말끔히 날아갑니다. // 줌바를 꾸준히 한 덕분에 매년 건강하게 체중을 감량하고 있으며 / 궁극적으로는 체중을 더 줄여 버킷리스트인 암벽 등반(볼더링)에 도전하겠다는 꿈을 품고 있습니다. // 운동은 저에게 단순한 체력 유지를 넘어 삶에 무한한 활력과 긍정을 채워주는 최고의 보약입니다.",
                        englishSentence = "To maintain my physical vitality and mental clarity, I enthusiastically practice Zumba dance, complemented by lower-body strength conditioning, running, and lake park walks. Zumba is an electrifying full-body workout synchronized to infectious, upbeat music that instantly elevates my spirits and dissolves accumulated engineering stress. Moving rhythmically to dynamic beats has enabled me to achieve steady, healthy annual weight loss, fueling my ultimate aspiration to take up indoor and outdoor rock climbing once I reach my target weight. For me, regular fitness transcends mere exercise—it is a vital source of pure joy, resilience, and positive daily energy."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "What do you like to do in your free time when you are relaxing at home?",
                category = PracticeCategory.HOBBY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "퇴근 후나 주말 여유 시간에는 / 거실 서재 창가에 앉아 인문학 서적을 읽거나 교양 유튜브 다큐멘터리를 감상하는 정적인 힐링을 즐깁니다. // 특히 요즘은 개봉 예정인 영화 '오디세이'의 웅장한 서사에 깊이 매료되어 / 유튜브에서 관련 역사 다큐, 원작 분석, 감독 인터뷰 영상들을 찾아보는 재미에 푹 빠져 있습니다. // 창밖으로 펼쳐진 호수공원의 황홀한 노을을 배경으로 / 따뜻한 차 한 잔과 함께 깊이 있는 콘텐츠를 탐구하는 시간은 무엇과도 바꿀 수 없는 행복입니다. // 신나는 줌바 댄스로 몸을 깨우고 조용한 독서로 마음을 채우는 이 균형이야말로 / 제 일상을 풍요롭게 가꿔주는 소중한 취미입니다.",
                        englishSentence = "During peaceful evenings at home, I love unwinding in my study corner reading thought-provoking books or exploring educational YouTube documentaries. Lately, I have developed a keen fascination with the upcoming cinematic masterpiece 'The Odyssey,' spending hours watching director masterclasses, production analyses, and historical essays. Savoring rich intellectual content with a warm cup of herbal tea while watching the sun set over the lake park brings sublime serenity to my soul. Balancing dynamic Zumba workouts with contemplative reading enriches my everyday life with deep fulfillment and peace."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "How did you first get started with Zumba dance and why is it special to you?",
                category = PracticeCategory.HOBBY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "제가 줌바 댄스를 처음 접하게 된 것은 / 회사 업무의 과중한 압박과 사춘기 자녀 양육 갈등이 겹쳐 심각한 번아웃과 무기력증을 겪던 힘든 시기였습니다. // 답답한 마음을 풀기 위해 우연히 등록했던 줌바 수업에서 / 신나는 라틴 음악에 맞춰 춤을 추며 억눌렸던 모든 감정과 스트레스가 순식간에 날아가는 기적을 경험했습니다. // 줌바는 저에게 힘든 시기를 웃으며 버텨낼 수 있는 생명수 같은 힐링을 선물해 주었고 / 복잡한 생각과 욕심을 내려놓는 법을 가르쳐 주었습니다. // 매년 조금씩 가벼워지는 몸과 밝아진 표정을 보며 / 줌바는 제 인생을 긍정적으로 바꿔놓은 가장 고마운 운동입니다.",
                        englishSentence = "I first discovered Zumba dance during an emotionally taxing chapter of my life when intense software sprints and teenage parenting friction culminated in severe burnout. Seeking a constructive outlet, I joined a local Zumba class and was immediately captivated by how moving to exhilarating rhythms dissolved suppressed anxiety and replenished my soul. Zumba served as an emotional lifeline that helped me weather demanding life storms, teaching me the invaluable art of mindfulness and letting go. Witnessing my steady annual weight loss and revitalized spirit makes Zumba the most transformative wellness journey of my life."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Tell me about your future goal to try rock climbing or bouldering.",
                category = PracticeCategory.HOBBY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "저의 가장 가슴 뛰는 피트니스 버킷리스트는 / 줌바로 체중을 더 감량하고 근력을 단련하여 실내외 암벽 등반(볼더링)을 완등하는 것입니다. // 가벼워진 몸으로 인공 암벽의 복잡한 홀드를 딛고 한 단계씩 정상으로 올라가는 상상을 하면 언제나 설렙니다. // 암벽 등반은 단순한 완력이 아니라 고도의 집중력, 균형 감각, 그리고 경로를 분석하는 전략적 문제 해결 능력을 요구합니다. // 이는 제가 20년간 사랑해 온 소프트웨어 아키텍처 설계와도 놀라울 만큼 맞닿아 있기에 / 체력을 탄탄히 다져 반드시 멋지게 도전할 것입니다.",
                        englishSentence = "My most exhilarating fitness bucket-list ambition is to venture into technical rock climbing and bouldering once I reach my target weight through Zumba. Scaling vertical climbing walls using one's own body weight requires immense focus, core balance, and strategic problem-solving to decipher complex routes. This cognitive and physical challenge mirrors the architectural problem-solving I love so much in software engineering. Building robust lower-body endurance and shedding weight brings me closer to conquering technical bouldering routes with confidence."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "What kind of YouTube videos, movies, or documentaries do you watch?",
                category = PracticeCategory.HOBBY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "저는 주로 역사적 서사, 우주 과학, 영화 제작 비하인드를 심도 있게 다루는 고품격 교양 유튜브 채널과 다큐멘터리를 즐겨 봅니다. // 특히 요즘은 영화 '오디세이'의 개봉 소식을 접한 이후 / 호메로스의 원작 신화 해석, 영화 세트장 제작기, 배우들의 심층 인터뷰 영상을 꼼꼼히 챙겨보고 있습니다. // 거실 서재 책상에서 호수 뷰를 바라보며 지적 호기심을 충족하고 시네마틱한 영감을 얻는 것은 / 20년 차 개발자로서 누리는 최고의 문화적 사치입니다. // 깊이 있는 콘텐츠를 탐구하는 시간은 / 제 시야를 넓혀주고 일상에 풍요로운 영감을 불어넣어 줍니다.",
                        englishSentence = "I predominantly enjoy watching high-quality YouTube channels and documentary series that explore historical epics, space science, and cinematic production analyses. Recently, captivated by the upcoming 'The Odyssey' film adaptation, I have been deeply immersed in video essays analyzing Homer's mythology, set design artistry, and director masterclasses. Satisfying my intellectual curiosity while gazing at the lake sunset from my study is my favorite cultural indulgence. Engaging with profound storytelling broadens my perspective and enriches my creative engineering mind."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "How do your hobbies contribute to your overall mental and physical wellness?",
                category = PracticeCategory.HOBBY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "신나는 줌바 댄스로 몸의 땀과 독소를 배출하고 / 거실 서재에서 조용히 독서와 다큐멘터리를 감상하는 이중 루틴은 제 웰빙의 완벽한 주춧돌입니다. // 동적인 운동을 통해 체력을 키우고 스트레스를 털어내며 / 정적인 인문학 취미를 통해 내면의 지혜와 차분한 평정심을 채웁니다. // 이러한 건강한 취미 생활 덕분에 / 남들과 비교하며 조급해하지 않고 제 삶을 있는 그대로 사랑하며 행복하게 살아갈 수 있습니다. // 몸과 마음이 모두 건강한 균형을 유지할 때 / 일에서도 가정에서도 최고의 에너지를 발휘할 수 있습니다.",
                        englishSentence = "The harmonious synergy between high-energy Zumba dance workouts and contemplative evening reading forms the bedrock of my holistic wellness. Dynamic exercise incinerates daily stress and builds athletic endurance, while quiet intellectual pursuits nurture emotional serenity and wisdom. These nourishing hobbies empower me to navigate life with authentic self-acceptance, free from toxic comparisons with others. Sustaining this healthy equilibrium between mind and body enables me to bring my absolute best self to both my work and family."
                    )
                )
            )
        ),
        PracticeCategory.PAST_EXPERIENCE to listOf(
            QuestionTemplate(
                opicQuestion = "Can you tell me about a difficult or stressful period in your life and how you managed to overcome it?",
                category = PracticeCategory.PAST_EXPERIENCE,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "돌이켜보면, 회사에서의 과중한 프로젝트 마감 압박과 사춘기에 접어든 중학교 2학년 딸아이를 양육하던 시기가 제 인생에서 가장 힘들고 벅찼던 순간이었습니다. // 딸아이가 유난히 예민하고 까다로워 잦은 갈등과 감정 소모를 겪으며 몸과 마음이 심각하게 번아웃되었습니다. // 하지만 그 위기의 순간에 저를 다시 일으켜 세운 것은 / 신나는 줌바 댄스와 '모든 사람에겐 다 저마다의 이유가 있다'는 마음 비우기의 깨달음이었습니다. // 줌바를 통해 땀을 흘리며 부정적인 감정을 털어내고, 남과 비교하지 않고 흘러가는 대로 수용하는 여유를 배우면서 / 이전보다 훨씬 성숙하고 단단한 어른으로 거듭날 수 있었습니다. // 고통스러웠던 그 시절의 시련이 / 역설적으로 제 인생을 가장 너그럽고 평온하게 만들어 준 위대한 스승이었습니다.",
                        englishSentence = "Looking back, balancing intense corporate software deadlines with raising my sensitive, strong-willed eighth-grade daughter was undeniably the most taxing chapter of my life. Navigating teenage emotional turbulence left me physically drained and emotionally overwhelmed on multiple occasions. However, what pulled me through that crisis was embracing high-energy Zumba dance and cultivating the profound realization that everyone acts out of their own internal reasons. Channelling stress into dance helped me shed toxic perfectionism and adopt an easygoing mindset, ultimately shaping me into a wiser and more resilient individual. That challenging crucible paradoxically became my greatest teacher in cultivating serenity."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Tell me about the experience of adopting your Maltese dog and rabbit seven years ago.",
                category = PracticeCategory.PAST_EXPERIENCE,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "7년 전 딸아이의 간절한 소원으로 하얀 말티즈 강아지와 토끼를 처음 집으로 입양했던 기억은 / 제 인생의 커다란 축복이자 전환점이었습니다. // 처음에는 두 마리의 반려동물을 양육하는 일이 서투르고 배변과 식사 케어가 부담스럽게 느껴지기도 했습니다. // 하지만 시간이 흐르면서 녀석들은 우리 가족에게 없어서는 안 될 가장 소중한 힐링과 사랑의 원천이 되었습니다. // 지치고 피곤한 하루 끝에 문을 열면 꼬리를 격렬하게 흔들며 반겨주는 말티즈를 안아줄 때마다 / 녀석이 없었다면 그 힘든 시기들을 어떻게 버텼을까 싶을 만큼 벅찬 감사를 느낍니다. // 7년의 세월 동안 변함없는 사랑을 나누어준 반려동물들은 / 우리 집의 가장 소중한 보물입니다.",
                        englishSentence = "Welcoming a Maltese puppy and a gentle rabbit into our home seven years ago at my daughter's plea marked a pivotal turning point in my personal journey. While managing two distinct pets initially felt overwhelming, they swiftly evolved into indispensable emotional anchors for our entire family. Greeting my Maltese dog after an exhausting day fills me with boundless warmth; without his unconditional affection, I honestly cannot imagine how I would have weathered demanding life challenges. Over the past seven years, their steadfast companionship has become the most cherished treasure in our household."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Can you share a memorable breakthrough you experienced during your 20-year software career?",
                category = PracticeCategory.PAST_EXPERIENCE,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "제 20년 소프트웨어 엔지니어링 여정에서 가장 짜릿했던 순간은 / 사내 모바일 미디어 렌더링 파이프라인에 AI 자동화 도구를 성공적으로 접목했던 때입니다. // 복잡한 미디어 코덱 호환성 검증을 수작업 대신 지능형 스크립트로 완전 자동화하여 / 검증 소요 시간을 며칠에서 단 몇 분으로 80% 이상 단축시켰습니다. // 20년 동안 축적된 도메인 노하우와 최신 AI 패러다임이 결합했을 때 만들어내는 폭발적인 시너지를 직접 증명한 순간이었습니다. // 동료 엔지니어들의 찬사를 받으며 / 오랜 경력에도 안주하지 않고 끊임없이 혁신할 수 있다는 깊은 자부심과 개발에 대한 열정을 되새겼습니다.",
                        englishSentence = "The most exhilarating breakthrough in my two-decade career was successfully integrating custom generative AI automation tools into our mobile multimedia rendering pipeline. Automating intricate codec validation routines reduced verification latency from days to mere minutes, cutting testing time by over eighty percent. Witnessing the seamless fusion of my veteran domain expertise with modern AI paradigms reaffirmed my deep passion for continuous engineering innovation. Receiving praise from fellow engineers proved that an enduring love for technology keeps an engineer perpetually relevant and empowered."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Tell me about a memorable moment you shared with your daughter that taught you a life lesson.",
                category = PracticeCategory.PAST_EXPERIENCE,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "사춘기 딸아이와 깊은 갈등을 겪던 어느 주말 / 호수공원 벤치에 나란히 앉아 오랜 시간 진솔한 대화를 나누었던 날이 기억에 선명합니다. // 아이의 날 선 반항 뒤에 숨겨진 학업 부담감과 인정받고 싶은 불안감을 온전히 경청하면서 / 부모의 잣대를 강요하기보다 그저 있는 그대로를 품어주는 것이 진정한 사랑임을 깨달았습니다. // '다 저마다의 이유가 있다'는 것을 가슴 깊이 인정하게 되면서 / 아이와의 관계는 물론 제 삶 전체를 대하는 태도가 한결 너그러워졌습니다. // 그날의 대화는 저를 한 인간이자 부모로서 진정으로 성숙하게 만들어 준 값진 이정표였습니다.",
                        englishSentence = "A deeply memorable moment occurred during a heart-to-heart conversation with my teenage daughter on a quiet bench overlooking the lake park after a period of friction. Truly listening to her underlying anxieties rather than imposing parental expectations taught me that unconditional acceptance is the essence of parenting. Realizing that everyone operates out of their own internal reasons brought immense tenderness to my perspective. That candid afternoon transformed me into a wiser, more compassionate parent and grounded individual."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Describe a memorable fitness milestone you achieved through consistent Zumba workouts.",
                category = PracticeCategory.PAST_EXPERIENCE,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "줌바 댄스를 시작한 지 1년이 되던 날 / 체중계에서 뚜렷한 체중 감량을 확인하고 거울 속의 건강하고 활기찬 제 모습을 마주했을 때의 벅찬 희열을 잊을 수 없습니다. // 무리한 굶기 다이어트 대신 신나는 음악에 맞춰 온몸을 흔들며 스트레스를 풀었을 뿐인데 / 몸무게가 줄고 만성 피로가 사라지는 놀라운 신체 변화를 경험했습니다. // 꾸준한 실천이 만들어낸 이 긍정적인 성과는 / 불가능해 보였던 암벽 등반(볼더링)이라는 새로운 버킷리스트에 당당히 도전할 강력한 자신감을 심어주었습니다. // 줌바는 제 몸과 마음에 새로운 생명력을 불어넣어 준 고마운 인생 운동입니다.",
                        englishSentence = "Reaching my first annual milestone with Zumba dance and seeing tangible, healthy weight loss alongside vibrant energy was immensely rewarding. By simply immersing myself in upbeat music rather than enduring restrictive diets, both excess weight and chronic fatigue vanished naturally. Experiencing this transformation firsthand instilled the confidence to pursue my ultimate ambition of technical rock climbing. Zumba has revitalized both my physical stamina and emotional joy in ways I never imagined possible."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Tell me about the day you moved into your current lake-view apartment.",
                category = PracticeCategory.PAST_EXPERIENCE,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "지금 살고 있는 방 4개짜리 아파트로 이사 오던 첫날 / 거실 통유리창을 통해 쏟아져 들어오던 찬란한 햇살과 호수공원의 파노라마 전망을 처음 마주했던 순간이 생생합니다. // 탁 트인 푸른 호수 뷰를 바라보며 거실 창가에 제 개발 작업 책상을 가장 먼저 배치했고 / 우리 가족과 반려동물들이 평화롭게 살아갈 완벽한 보금자리임을 확신했습니다. // 이삿짐 정리를 마치고 창가에 앉아 가족들과 마셨던 따뜻한 차 한 잔의 여유는 / 지금도 제 기억 속에 가장 아름답고 설레는 추억으로 남아 있습니다. // 그날의 평온함은 매일 아침 거실 창문을 열 때마다 변함없이 이어지고 있습니다.",
                        englishSentence = "I vividly recall moving into our four-bedroom apartment and standing mesmerized by the brilliant sunshine streaming through the living room windows overlooking the vast lake park. Positioning my coding desk right by the panoramic glass confirmed that we had found our dream sanctuary for our family and pets. Savoring a warm cup of tea with my family while gazing at the tranquil waters remains one of my fondest memories. That serene joy continues to greet me every single morning as I open the living room blinds."
                    )
                )
            )
        ),
        PracticeCategory.SURVEY_ROLEPLAY to listOf(
            QuestionTemplate(
                opicQuestion = "You want to register for a Zumba fitness class. Call the center and ask three or four questions about the classes.",
                category = PracticeCategory.SURVEY_ROLEPLAY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "안녕하세요, 호수공원 인근 줌바 댄스 피트니스 클래스 수강 등록 관련하여 몇 가지 여쭤보려고 전화드렸습니다. // 제가 퇴근 후 참여할 예정인데 / 평일 저녁 7시나 8시 타임에 개설된 직장인 전용 클래스가 있는지 궁금합니다. // 또한 3개월 정규 등록을 하기 전에 / 강사님의 수업 스타일과 난이도를 확인할 수 있는 1회 무료 체험이나 일일 체험권 이용이 가능한가요? // 추가로 개인 락커룸 이용료와 샤워 시설 구비 여부, 그리고 지하 주차장 2시간 무료 지원 혜택이 적용되는지도 함께 안내 부탁드립니다. // 친절한 답변 기다리겠습니다, 감사합니다.",
                        englishSentence = "Hello, I am calling to inquire about enrolling in your evening Zumba fitness classes near the lake park. Since I plan to attend after work, could you let me know if you offer weekday sessions around 7 or 8 PM tailored for working professionals? Secondly, is it possible to book a one-day trial pass before committing to a multi-month membership so I can experience the instructor's choreography? Lastly, could you outline locker rental fees, shower amenities, and complimentary parking validation? Thank you so much for your assistance."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "You need to take your Maltese dog and rabbit for a routine health checkup. Call the veterinary clinic and ask some questions.",
                category = PracticeCategory.SURVEY_ROLEPLAY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "안녕하세요, 저희 7살 말티즈 강아지와 반려 토끼의 정기 종합 건강검진 예약을 위해 연락드렸습니다. // 이번 주 토요일 오전에 강아지와 소동물 특수 진료를 한 번에 받을 수 있는 예약 슬롯이 남아 있나요? // 말티즈의 관절 엑스레이, 종합 혈액 검사, 스케일링 상담과 함께 토끼의 치아 및 소화기 검진을 진행하려는데 대략적인 소요 시간과 비용이 어떻게 되나요? // 마지막으로 정확한 혈액 검사를 위해 전날 밤 몇 시간 정도 금식을 유지해야 하는지도 미리 알려주시면 감사하겠습니다.",
                        englishSentence = "Hello, I am calling to schedule a comprehensive wellness checkup for my seven-year-old Maltese dog and pet rabbit. Do you have openings this coming Saturday morning where both canine and exotic companion exams can be accommodated concurrently? We would like joint X-rays, bloodwork, and a dental evaluation for our dog alongside gastrointestinal checkups for the rabbit; could you estimate the duration and general fee schedule? Additionally, please let me know how many hours of fasting are required prior to the appointment."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "You want to buy a new ergonomic desk and monitor arm for your living room study. Call the furniture store and ask questions.",
                category = PracticeCategory.SURVEY_ROLEPLAY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "안녕하세요, 거실 홈 오피스 서재에 설치할 전동 높이조절 모션 데스크와 듀얼 모니터 암 구매 건으로 문의드립니다. // 1600 사이즈 친환경 원목 상판 모델의 매장 재고가 확보되어 있는지 확인하고 싶습니다. // 아파트 거실 창가에 설치할 예정인데 / 전문 기사님의 방문 조립 및 배송 서비스 일정을 이번 주말에 맞출 수 있나요? // 듀얼 모터의 무상 AS 보증 기간과 현재 진행 중인 신규 구매 프로모션 할인 혜택도 함께 안내해 주시기 바랍니다.",
                        englishSentence = "Hello, I am calling to inquire about purchasing a height-adjustable standing desk and dual monitor arm for my home study. Do you have the 1600mm solid wood desktop model in stock at your showroom? Since it will be installed in our apartment living room, do you offer professional on-site assembly delivery this coming weekend? Lastly, could you outline the dual-motor warranty period and any current seasonal promotional discounts?"
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "You want to inquire about introductory rock climbing lessons at an indoor bouldering gym. Call and ask questions.",
                category = PracticeCategory.SURVEY_ROLEPLAY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "안녕하세요, 성인 초보자를 위한 실내 볼더링 및 암벽 등반 기초 강습 패키지에 대해 문의하고자 전화드렸습니다. // 직장인을 위해 주말 오전에 운영되는 4주 기초 입문 코스가 개설되어 있는지 궁금합니다. // 강습 등록 시 암벽 전용 클라이밍 슈즈와 초크백 대여가 무료로 포함되어 있나요? // 첫 방문 시 진행되는 1일 안전 교육 및 체험 강습 예약 절차를 상세히 안내해 주시면 감사하겠습니다.",
                        englishSentence = "Hello, I am calling to inquire about your beginner indoor bouldering and rock climbing packages for adults. Do you have four-week foundational courses scheduled on weekend mornings tailored for working professionals? Furthermore, is specialized climbing shoe and chalk bag rental included in the tuition fee? Please guide me through the booking process for the initial safety orientation and trial lesson."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Call a bookstore or cinema customer center to inquire about special screenings or artbooks for 'The Odyssey'.",
                category = PracticeCategory.SURVEY_ROLEPLAY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "안녕하세요, 영화 '오디세이' 특별 기획 상영회 및 공식 비하인드 하드커버 아트북 구매 관련하여 문의드립니다. // 프리미엄 IMAX 특별 상영관 티켓 예매가 오픈되는 정확한 날짜와 시간을 알고 싶습니다. // 또한 감독 인터뷰와 콘셉트 아트가 수록된 공식 양장본 도서의 사전 예약 특전이 제공되는지도 궁금합니다. // 멤버십 포인트 사용 가능 여부와 굿즈 수령 방법도 함께 확인 부탁드립니다.",
                        englishSentence = "Hello, I am calling to inquire about the special screening schedule and official artbook for 'The Odyssey.' Could you tell me when advance ticket reservations for the premium IMAX premiere will open? Additionally, does pre-ordering the hardcover concept artbook qualify for exclusive commemorative merchandise? Please also advise if membership points can be redeemed for this purchase."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "You want to order premium timothy hay and pet supplies for your Maltese and rabbit. Call the pet shop.",
                category = PracticeCategory.SURVEY_ROLEPLAY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "안녕하세요, 7살 말티즈용 관절 영양 사료와 토끼용 최고급 1번초 티모시 건초 대용량 주문 건으로 연락드렸습니다. // 오늘 오후 3시 이전에 주문을 완료하면 내일 오전까지 당일 특급 배송이 가능한가요? // 건초의 최근 수확 상태와 먼지 제거 처리 여부가 궁금합니다. // 혹시 매월 정기 배송을 신청할 경우 정기 구독 할인 혜택이 적용되는지도 함께 안내해 주시기 바랍니다.",
                        englishSentence = "Hello, I am calling to place a bulk order for canine joint supplements and premium first-cut timothy hay for my rabbit. If I finalize the purchase before 3 PM today, is next-morning express courier delivery guaranteed? Could you also confirm the freshness of the hay batch? Lastly, please let me know if setting up a recurring monthly subscription qualifies for promotional discounts."
                    )
                )
            )
        ),
        PracticeCategory.COMPARISON to listOf(
            QuestionTemplate(
                opicQuestion = "How has your approach to work and life philosophy changed from 20 years ago compared to now?",
                category = PracticeCategory.COMPARISON,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "20년 전 사회 초년생 개발자 시절과 오늘날의 제 모습을 비교해 보면 / 삶을 대하는 철학과 마인드셋에서 깊은 성숙과 대전환이 일어났습니다. // 과거에는 성과주의에 집착하고 타인과 끊임없이 비교하며 스스로를 가혹하게 채찍질하곤 했습니다. // 하지만 20년간 수많은 복잡한 모바일 프로젝트를 완수하고 사춘기 딸을 양육하면서 / 모든 일에는 저마다의 타당한 이유가 있으며 순리대로 흘러가게 두는 것이 최고의 지혜임을 깨달았습니다. // 지금은 최신 AI 기술을 활용해 즐겁게 코딩하면서 / 남들과의 비교 없이 호수 뷰 아파트에서 나만의 여유와 평온을 온전히 누리고 있습니다. // 조급함을 내려놓고 유연하게 살아가는 지금의 삶이 훨씬 행복합니다.",
                        englishSentence = "Comparing my perspective as a rookie programmer twenty years ago to my outlook today reveals a profound evolution toward mental maturity and serenity. In my early career, I was consumed by restless perfectionism, constantly measuring my accomplishments against peers with acute anxiety. However, navigating two decades of high-stakes software engineering and raising an independent teenager taught me that everyone operates on their own distinct timeline and context. Today, I passionately harness AI technologies while embracing an unhurried, authentic life free from social comparison. Letting go of rigid expectations and trusting the natural flow of life has brought me immeasurable joy and stability."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Compare how mobile software development was done 20 years ago versus with AI tools today.",
                category = PracticeCategory.COMPARISON,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "20년 전의 모바일 미디어 소프트웨어 개발 환경과 오늘날의 개발 생태계는 / 상상할 수 없을 만큼 비약적인 도약을 이루어냈습니다. // 과거에는 메모리 누수와 코덱 버그를 잡기 위해 며칠 밤을 새우며 C/C++ 로우레벨 코드를 한 줄씩 일일이 수작업으로 디버깅해야 했습니다. // 반면 오늘날에는 / 지능형 생성형 AI 코딩 도구와 자동화 스크립트가 반복적인 보일러플레이트 작성과 유닛 테스트를 순식간에 처리해 줍니다. // 기술의 패러다임이 비약적으로 진화하면서 / 개발자는 단순 코딩 노동을 넘어 창의적인 코어 아키텍처 설계와 사용자 경험 극대화에 온전히 집중할 수 있게 되었습니다. // 20년의 경험에 최신 AI 기술이 더해져 개발이 그 어느 때보다 즐겁습니다.",
                        englishSentence = "The technological landscape of mobile multimedia software engineering has undergone an astounding transformation compared to twenty years ago. In the early days, tracking down elusive memory leaks meant grueling overnight debugging sessions combing through low-level C++ code line by line. Today, advanced generative AI copilots and automated validation scripts handle mundane boilerplate and test creation in mere seconds. This paradigm shift empowers engineers to transcend repetitive syntax chores and concentrate entirely on creative system architecture and performance optimization. Merging my two decades of domain experience with modern AI tools makes software development more exhilarating than ever."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Compare how your fitness habits and hobbies have evolved over the years.",
                category = PracticeCategory.COMPARISON,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "과거의 운동이 의무감 때문에 억지로 마지못해 하던 지루한 헬스였다면 / 지금의 피트니스는 신나는 음악과 함께 삶의 에너지를 충전하는 줌바 댄스로 진화했습니다. // 예전에는 무거운 덤벨을 들며 쉽게 지치고 작심삼일에 그쳐 운동에 재미를 붙이지 못했습니다. // 하지만 줌바를 만난 이후로는 / 신나는 라틴 비트에 맞춰 춤추며 스트레스를 날리고 매년 꾸준하고 건강한 체중 감량을 이루어내고 있습니다. // 여기에 하체 근력운동과 호수공원 러닝을 병행하면서 / 조만간 버킷리스트인 암벽 등반(볼더링)에 도전할 탄탄한 체력을 완성해가고 있습니다. // 억지로 하던 고통스러운 운동에서 기다려지는 힐링의 시간으로 완벽히 탈바꿈했습니다.",
                        englishSentence = "Whereas my past exercise attempts felt like tedious, obligatory gym workouts, my current fitness routine has blossomed into an exhilarating lifestyle anchored by Zumba dance. I used to burn out quickly lifting heavy weights in monotonous gym settings, abandoning routines within days. Discovering Zumba changed everything, enabling me to incinerate stress to vibrant Latin beats while achieving consistent, healthy annual weight loss. Pairing dance with lower-body strength conditioning and lake park running has built the physical endurance needed for my long-awaited rock climbing dream. Fitness has transformed from a painful chore into the most anticipated healing ritual of my day."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Compare your perspective on parenting when your child was young versus now that she is in middle school.",
                category = PracticeCategory.COMPARISON,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "딸아이가 어렸을 때는 부모로서 모든 것을 통제하고 완벽한 틀에 맞춰 양육해야 한다는 무거운 강박이 있었습니다. // 하지만 중학교 2학년 사춘기를 맞이한 예민한 딸아이를 키우며 / 아이에게도 자신만의 생각과 타당한 이유가 있음을 깊이 인정하게 되었습니다. // 아이를 지시하고 통제하려던 부모에서 / 한 걸음 물러서서 묵묵히 경청하고 기다려 주는 따뜻한 조력자로 성장했습니다. // 이러한 양육 태도의 변화는 / 불필요한 갈등을 줄이고 가족 사이에 깊은 신뢰와 편안한 유대감을 형성해 주었습니다.",
                        englishSentence = "When my daughter was younger, I felt compelled to micromanage every detail of her upbringing with rigid parental expectations. However, navigating her eighth-grade adolescent years taught me that she has her own distinct thoughts and valid personal reasons. I evolved from a controlling parent into a compassionate listener who respects her autonomy from a supportive distance. This philosophical shift has eliminated unnecessary household friction and fostered profound harmony, mutual trust, and emotional stability in our family."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Compare how you consume media and cinema content in the past versus today.",
                category = PracticeCategory.COMPARISON,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "과거에는 영화나 미디어를 단순히 극장이나 TV 화면을 통해 일방적으로 수동 소비하는 것에 그쳤습니다. // 하지만 오늘날에는 / 유튜브를 통해 영화 '오디세이' 같은 대작의 역사적 배경, 철학적 상징, 감독 인터뷰 다큐멘터리까지 입체적으로 탐구합니다. // 모바일 미디어 SW를 직접 개발하는 엔지니어의 시각과 인문학적 호기심이 결합하여 / 콘텐츠를 훨씬 더 깊이 있고 풍성하게 향유하게 되었습니다. // 단순한 킬링타임을 넘어 지적 영감을 얻는 고품격 여가로 진화했습니다.",
                        englishSentence = "In the past, consuming cinema was a passive experience confined to movie theaters or scheduled television broadcasts. Today, platforms like YouTube allow me to explore in-depth historical analyses, philosophical themes, and director masterclasses surrounding masterpieces like 'The Odyssey.' Combining my analytical perspective as a mobile multimedia engineer with deep intellectual curiosity allows me to appreciate films multidimensionally. Media consumption has evolved from superficial entertainment into a profound source of intellectual inspiration."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Compare your living environment before moving to your current apartment versus now.",
                category = PracticeCategory.COMPARISON,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "이전에 살던 도심 빌라는 주변 건물에 가로막혀 햇빛이 잘 들지 않고 답답하여 늘 아쉬움이 컸습니다. // 반면 지금 거주하는 방 4개짜리 호수 뷰 아파트는 / 거실 가득 따스한 자연광이 쏟아져 들어오고 창밖으로 푸른 호수공원이 파노라마처럼 펼쳐집니다. // 거실 서재에서 아름다운 풍경을 보며 코딩을 하고 말티즈와 여유를 즐길 수 있는 환경이 조성되면서 / 일상의 피로도가 획기적으로 줄어들었습니다. // 주거 공간의 질적 변화가 / 저와 가족 모두의 삶에 엄청난 행복과 정서적 안정을 가져다주었습니다.",
                        englishSentence = "Our previous residence was situated in a congested neighborhood where neighboring structures blocked natural sunlight, leaving a cramped and gloomy atmosphere. In stark contrast, our current four-bedroom apartment enjoys abundant natural sunlight throughout the day and a breathtaking panoramic view of the lake park. Being able to code at my sunlit living room workstation while admiring the tranquil waters with our Maltese dog has drastically diminished daily stress. This environmental upgrade has elevated our overall happiness and emotional well-being substantially."
                    )
                )
            )
        ),
        PracticeCategory.PROBLEM_SOLVING_ROLEPLAY to listOf(
            QuestionTemplate(
                opicQuestion = "You have an urgent issue with your home study setup where your laptop and external monitor won't connect. Call the IT support center to resolve it.",
                category = PracticeCategory.PROBLEM_SOLVING_ROLEPLAY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "안녕하세요 기술지원팀 담당자님, 재택근무 중 거실 서재 작업대의 고화질 모니터와 랩톱 연결에 긴급한 오류가 발생하여 문의드립니다. // 오늘 중요한 모바일 미디어 SW 코드 배포가 예정되어 있는데 / C타입 썬더볼트 및 HDMI 포트 신호 인식이 갑자기 끊기며 화면이 심하게 깜빡입니다. // 그래픽 드라이버 재설치와 보조 케이블 교체, 하드웨어 리셋을 시도해 보았으나 문제가 지속되고 있습니다. // 혹시 원격 기술 지원을 통해 디스플레이 설정을 즉시 점검해 주시거나 긴급 대체 케이블을 당일 퀵으로 배송해 주실 수 있나요? // 빠른 확인 부탁드립니다.",
                        englishSentence = "Hello technical support, I am experiencing a critical display connection failure at my home workstation while preparing for an urgent media software sprint. My high-resolution external monitor is failing to recognize USB-C Thunderbolt and HDMI signals from my laptop, resulting in persistent screen flickering. I have already attempted reinstalling graphic drivers, swapping cables, and performing hardware power cycles to no avail. Would it be possible to initiate an immediate remote diagnostic session, or dispatch an emergency replacement adapter to my address today? I appreciate your prompt assistance."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "You need to reschedule your pet's veterinary appointment due to an urgent software release. Call the clinic and explain the situation.",
                category = PracticeCategory.PROBLEM_SOLVING_ROLEPLAY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "안녕하세요, 오늘 오후 3시에 예약된 7살 말티즈의 정기 종합 검진 예약을 긴급히 변경하고자 연락드렸습니다. // 회사에서 예기치 못한 모바일 소프트웨어 긴급 핫픽스 릴리즈 일정이 잡혀 / 오늘 병원에 직접 내원하기가 불가능해졌습니다. // 정말 죄송하지만 / 동일한 진료 내용과 담당 수의사 선생님으로 이번 주 토요일 오후 시간대로 예약을 연기해 주실 수 있나요? // 예약 변경에 따른 수수료가 발생한다면 부담할 테니 유연하게 조율해 주시면 대단히 감사하겠습니다.",
                        englishSentence = "Hello, I am calling regarding my Maltese dog's scheduled comprehensive health examination booked for 3 PM this afternoon. Regrettably, an unforeseen emergency software deployment sprint has arisen at my company, making it impossible for me to step away from my workstation today. I sincerely apologize for the short notice; would it be possible to reschedule our appointment to this Saturday afternoon under the same veterinary doctor? I am more than happy to settle any rebooking adjustments, and I truly appreciate your kind flexibility."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "You cannot attend your registered Zumba class due to an unexpected family matter. Call the fitness center to pause your membership.",
                category = PracticeCategory.PROBLEM_SOLVING_ROLEPLAY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "안녕하세요, 저녁 7시 줌바 댄스 클래스 수강생입니다. 갑작스러운 가정 사정으로 인해 향후 2주간 수업 출석이 어려워져 연락드렸습니다. // 결석으로 인한 수업 횟수 차감을 방지하기 위해 / 멤버십 수강권을 2주 동안 일시 정지(홀딩) 처리해 주실 수 있는지 문의드립니다. // 증빙 서류가 필요하다면 이메일로 제출할 테니 확인 후 절차를 안내해 주시기 바랍니다. // 수강 재개 시점에 다시 연락드리겠습니다, 감사합니다.",
                        englishSentence = "Hello, I am a member of your weekday evening Zumba dance class. Due to an urgent family commitment, I will be unable to attend sessions for the next two consecutive weeks. To avoid forfeiting my remaining class credits, could you please place a temporary two-week hold on my membership account? I can provide necessary verification documents via email if required; please guide me through the administrative pause procedure. Thank you for your understanding."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "There is an issue with the window seal in your living room overlooking the lake causing a draft. Call apartment maintenance.",
                category = PracticeCategory.PROBLEM_SOLVING_ROLEPLAY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "안녕하세요, 아파트 관리사무소 시설관리팀이죠? 거실 통유리창 하단 실리콘 실링에 유격이 생겨 외풍과 휘파람 소음이 유입되어 연락드렸습니다. // 거실 서재 책상에서 재택근무로 코딩을 하는데 바람 소리가 심해서 업무에 집중하기 어렵습니다. // 오늘 오후 중으로 시설 기사님이 방문하시어 창틀 실링 상태를 점검하고 특수 실리콘 보수 작업을 진행해 주실 수 있을까요? // 빠른 조치 부탁드립니다, 감사합니다.",
                        englishSentence = "Hello, I am calling apartment maintenance regarding a compromised window seal on our living room panoramic glass. A noticeable cold draft and whistling wind noise are penetrating directly into my home office study area. Would it be possible for a maintenance technician to visit this afternoon to inspect the frame gasket and apply fresh industrial sealant? I would greatly appreciate your prompt attention to this matter so I can work comfortably."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "An online bookstore delivered a damaged artbook of 'The Odyssey'. Call customer service for an exchange.",
                category = PracticeCategory.PROBLEM_SOLVING_ROLEPLAY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "안녕하세요 고객센터 담당자님, 어제 택배로 수령한 '오디세이' 한정판 양장본 아트북 배송 상태 문제로 연락드렸습니다. // 에어캡 포장을 뜯어보니 하드커버 모서리가 심하게 찌그러져 있고 본문 화보 일부가 찢어져 있습니다. // 오랜 시간 기대했던 소장용 도서라 매우 속상한데 / 사진을 즉시 첨부해 드릴 테니 파손 없는 온전한 새 제품으로 맞교환 배송을 신속히 처리해 주실 수 있나요? // 빠른 확인 부탁드립니다.",
                        englishSentence = "Hello customer service, I am calling regarding my order of 'The Odyssey' limited edition hardcover artbook delivered yesterday. Upon opening the package, I discovered the spine was severely crushed and several illustrative pages were creased. Since this is a cherished collector's volume, I would like to request an immediate replacement dispatch. I can upload high-resolution photos of the damaged packaging right away to expedite the return process."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "The pet food delivery for your rabbit and Maltese was delayed. Call the supplier to expedite the shipment.",
                category = PracticeCategory.PROBLEM_SOLVING_ROLEPLAY,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "안녕하세요, 이번 주 초 주문한 토끼용 티모시 건초와 강아지 맞춤형 영양 사료 배송이 예정일보다 3일째 지연되고 있어 문의드립니다. // 집에 남은 사료와 건초가 거의 바닥나서 매우 난감한 비상 상황입니다. // 현재 택배 운송장 조회가 허브 물류센터에 멈춰 있는데 / 물류 담당 부서에 긴급 요청하여 오늘 중으로 당일 퀵 배송을 해주시거나 대체 상품을 당일 발송해 주실 수 있나요? // 빠른 해결 부탁드립니다.",
                        englishSentence = "Hello, I am calling regarding my order for rabbit timothy hay and canine nutrition food placed earlier this week, which is now three days overdue. Our home pet supplies are virtually exhausted, creating an urgent situation for our animals. The tracking status appears stalled at the regional distribution depot; could you escalate this with logistics for immediate express courier delivery today or dispatch an urgent replacement? I appreciate your rapid response."
                    )
                )
            )
        ),
        PracticeCategory.PETS to listOf(
            QuestionTemplate(
                opicQuestion = "Tell me about your pets. What are they, how long have you raised them, and why are they so special to you?",
                category = PracticeCategory.PETS,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "의심의 여지 없이 / 7년 전 딸아이의 간절한 소원으로 입양한 하얀 말티즈 강아지와 온순한 토끼는 우리 가족에게 최고의 축복입니다. // 특히 우리 집 말티즈는 / 강아지가 없었다면 어떻게 그 힘든 번아웃과 육아의 시기들을 버텨냈을까 싶을 정도로 제 인생의 가장 소중한 영혼의 동반자입니다. // 20년 차 개발자로서 겪는 복잡한 업무 스트레스 속에서도 / 현관문을 열면 꼬리를 맹렬히 흔들며 달려오는 녀석을 안아줄 때 모든 피로가 사르르 녹아내립니다. // 거실 카펫 위에서 토끼는 평화롭게 건초를 먹고 말티즈는 제 발치에 기대어 곤히 잠드는 풍경은 / 세상 무엇과도 바꿀 수 없는 따스한 평온을 줍니다. // 녀석들이 건네는 조건 없는 사랑에 보답하기 위해 / 매일 건강하고 행복하게 보살피는 든든한 보호자가 되고자 합니다.",
                        englishSentence = "Without a shadow of a doubt, welcoming our white Maltese dog and gentle rabbit into our home seven years ago was one of the most rewarding decisions our family has ever made. Our Maltese dog, in particular, is an irreplaceable emotional anchor; looking back, I honestly cannot fathom how our household would have navigated demanding software sprints and parenting exhaustion without his comforting presence. Whenever I return home, his ecstatic greetings and perpetually wagging tail instantly dissolve all weariness. Watching our rabbit peacefully munch timothy hay while our Maltese curls up by my feet fills our living room with sublime serenity. Returning their unconditional companionship by ensuring their lifelong health and happiness is one of my greatest life joys."
                    ),
                    AnswerVariation(
                        koreanHint = "저희 집에는 작고 소중한 두 마리의 생명인 7살 말티즈 강아지와 온순한 토끼가 가족으로 함께 살아가고 있습니다. // 활발하게 온몸으로 사랑을 표현하는 말티즈와 / 조용하고 깔끔하게 털을 다듬으며 평온함을 주는 토끼는 상반된 매력으로 집안을 채워줍니다. // 치열하고 빠른 IT 업계에서 일하다가도 / 거실 책상 밑에서 곤히 자고 있는 반려동물들을 바라보면 세상 모든 근심이 사라집니다. // 매일 아침 신선한 건초를 챙겨주고 저녁에는 호수공원을 함께 산책하는 규칙적인 루틴은 / 제 삶을 지탱해 주는 가장 따뜻한 행복의 주춧돌입니다. // 녀석들과 함께한 7년의 세월은 제 인생에서 가장 따뜻하고 눈부신 축복이었습니다.",
                        englishSentence = "Our home is blessed with two precious companions—a seven-year-old spirited Maltese dog and a serene rabbit—who have shared our family journey for seven wonderful years. While our Maltese expresses energetic affection with playful barks, our rabbit brings calming elegance, meticulously grooming his white coat on the carpet. Working in the fast-paced tech industry can be demanding, but glancing down at my pets resting peacefully beneath my desk brings immense tranquility. Caring for them through daily lake walks and fresh hay feeding forms the foundation of my grounded happiness. Sharing seven years with these faithful companions has been the warmest blessing of my life."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "What is your daily routine for taking care of your Maltese dog and rabbit?",
                category = PracticeCategory.PETS,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "반려동물을 건강하고 행복하게 돌보기 위해 / 저는 아침부터 밤까지 철저하고 정성스러운 일과 루틴을 실천하고 있습니다. // 매일 아침 일어나자마자 토끼 케이지에 신선한 최고급 티모시 건초와 깨끗한 물을 채워주고 / 말티즈에게 맞춤형 영양 사료와 관절 영양제를 급여합니다. // 퇴근 후 저녁에는 날씨와 상관없이 반려견과 함께 호수공원으로 나가 40분 동안 활기찬 산책을 즐기며 에너지를 발산시킵니다. // 산책을 마친 후에는 발을 깨끗이 닦아주고 부드러운 브러시로 하얀 털을 정성껏 빗겨주며 피부 건강을 세심하게 살핍니다. // 이 규칙적인 돌봄 루틴은 제 반려동물들의 장수와 행복을 지키는 가장 소중한 일과입니다.",
                        englishSentence = "To ensure my companions thrive in optimal health and happiness, I adhere to a disciplined, attentive daily routine from dawn till dusk. First thing every morning, I replenish an unlimited supply of fresh timothy hay for our rabbit and serve balanced nutritional meals enriched with joint supplements to our Maltese dog. In the evening, regardless of weather conditions, my dog and I head out for a refreshing forty-minute walk along the scenic lake park to explore and expend energy. Afterward, I thoroughly cleanse his paws and groom his silky white coat to prevent tangles while inspecting his skin. Maintaining this attentive routine is my heartfelt commitment to their lifelong vitality."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Can you share a memorable story involving your Maltese dog or rabbit that made you laugh or touched your heart?",
                category = PracticeCategory.PETS,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "가장 잊을 수 없는 감동적인 순간은 / 제가 회사 업무와 사춘기 육아 갈등으로 심한 번아웃을 겪으며 거실 소파에서 홀로 눈물을 흘렸던 날이었습니다. // 평소 장난기 넘치던 말티즈가 / 조용히 다가와 제 무릎에 작은 머리를 기대고 제 손등을 따뜻하게 핥아주었습니다. // 말은 통하지 않지만 '내가 곁에 있으니 다 괜찮아'라고 위로해 주는 듯한 깊은 눈빛에 왈칵 눈물이 쏟아졌습니다. // 작은 생명이 건네는 거대한 위로의 힘을 경험하며 / 왜 반려견이 우리 가족에게 그토록 소중한 존재인지를 다시 한번 깊이 깨달았습니다. // 그날 이후 저는 녀석을 단순한 애완동물이 아닌 영혼의 동반자로 여기고 있습니다.",
                        englishSentence = "The most unforgettable, deeply touching moment occurred on an evening when I was feeling utterly drained from work stress and parenting struggles, sitting quietly on the living room sofa. Sensing my heavy mood, our Maltese dog gently climbed onto my lap, rested his little head against my chest, and softly nudged my hand with his nose. Without a single word, his soulful eyes conveyed profound empathy, reassuring me that everything would be alright. Experiencing such pure, unconditional comfort reaffirmed why he is truly the beating heart of our household. Since that touching evening, I cherish him not merely as a pet, but as an irreplaceable soulmate."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Have you ever experienced an emergency situation where your pet got sick? How did you handle it?",
                category = PracticeCategory.PETS,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "말티즈가 한밤중에 갑자기 구토와 복통 증세를 보여 야간 24시간 동물병원으로 급히 달려갔던 아찔한 경험이 있습니다. // 엑스레이 검사 결과 실수로 삼킨 이물질이 발견되었고 / 다행히 수의사 선생님의 신속한 내시경 시술 덕분에 수술 없이 안전하게 제거할 수 있었습니다. // 밤새 곁을 지키며 수액을 맞히고 간호한 끝에 다음 날 기력을 회복한 녀석을 안아주었을 때 / 보호자로서의 막중한 책임감을 뼈저리게 실감했습니다. // 작은 이상 징후도 놓치지 않고 신속하게 대처하는 것이 반려동물의 생명을 지키는 핵심임을 배웠습니다.",
                        englishSentence = "I faced a frightening emergency when our Maltese suddenly exhibited acute vomiting and abdominal pain in the middle of the night, prompting an urgent dash to a 24-hour veterinary hospital. Diagnostic radiographs revealed an accidentally ingested foreign object, which the veterinarian successfully extracted via emergency endoscopy without invasive surgery. Nursing him back to vitality by his side until morning reinforced the profound, solemn responsibility of pet parenthood. I learned that vigilant monitoring and swift medical intervention are paramount to safeguarding our companions' fragile lives."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "How do your pets interact with each other and bring harmony to your home?",
                category = PracticeCategory.PETS,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "처음에는 강아지와 토끼가 서로 다른 종이라 잘 어울릴 수 있을지 걱정했지만 / 7년이 지난 지금은 서로의 존재를 편안하게 인정하며 평화롭게 공존하고 있습니다. // 호기심 많은 말티즈가 조용히 다가가 코 인사를 건네면 / 온순한 토끼도 편안하게 귀를 뉘이고 곁을 내어줍니다. // 서로 다른 종의 두 생명이 거실 햇살 아래에서 평화롭게 머무는 모습은 / 온 가족의 마음을 따뜻하게 녹여주는 최고의 힐링입니다. // 녀석들의 조화로운 모습은 우리 집안에 언제나 잔잔한 평화와 온기를 불어넣어 줍니다.",
                        englishSentence = "Though I initially harbored concerns about whether a dog and rabbit could coexist harmoniously, seven years together have fostered a serene, affectionate friendship. Our Maltese gently sniffs in greeting, while our rabbit calmly twitches his nose, sharing the sunny living room carpet without friction. Watching two distinct species lounge side by side in peaceful tranquility melts away household stress and fills our home with gentle warmth. Their harmonious coexistence serves as a constant reminder of the beauty of mutual trust and coexistence."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "What advice would you give to someone considering adopting a pet for their child?",
                category = PracticeCategory.PETS,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "자녀를 위해 반려동물을 입양하려는 부모님들에게 / 귀여운 모습 뒤에 따르는 평생의 헌신과 책임감을 충분히 고민하라고 진심으로 조언하고 싶습니다. // 7년 전 딸아이의 간절한 요청으로 시작되었지만 / 결국 매일의 산책, 식사 급여, 위생 관리, 동물병원 케어는 온 가족의 진정한 사랑과 협동이 뒷받침되어야 합니다. // 하지만 진정한 책임감을 가지고 맞이한다면 / 반려동물은 가족 전체의 삶에 상상 그 이상의 깊은 사랑과 정서적 성장을 선물해 줍니다. // 생명을 향한 성숙한 책임감이 전제될 때 비로소 진정한 가족이 될 수 있습니다.",
                        englishSentence = "My heartfelt advice to parents considering adopting a pet for their children is to embrace the immense, lifelong commitment required before taking the leap. Though our journey began at my daughter's earnest request seven years ago, providing daily walks, veterinary care, and grooming demands steadfast family teamwork. However, when approached with genuine dedication, pets enrich family life with boundless unconditional love and emotional growth. Embracing mature responsibility for a living being is the prerequisite for experiencing this life-changing blessing."
                    )
                )
            )
        ),
        PracticeCategory.LOUNGE_REVIEW to listOf(
            QuestionTemplate(
                opicQuestion = "Describe the transportation you usually use. Why do you prefer it?",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "솔직히 / 저는 주로 차로 이동합니다 / 왜냐하면 제 일상에서 가장 편리한 선택이기 때문입니다. // 저는 직장으로 운전하고 / 장을 보러 가며 / 심지어 주말 대부분에도 차를 이용하여 / 많은 시간을 절약합니다. // 제 사무실은 약 30분 거리에 있으며 / 운전하는 것이 대중교통을 이용하는 것보다 훨씬 빠릅니다. // 제가 정말 좋아하는 점은 / 붐비는 버스나 지하철에 대해 걱정할 필요가 없다는 것입니다 / 특히 출퇴근 시간 동안에. // 또한 운전하는 동안 좋아하는 음악을 듣는 것을 즐기며 / 그것이 이동을 훨씬 더 즐겁게 만들어 줍니다. // 다른 장점은 / 버스나 기차 시간표를 확인할 필요 없이 / 언제든지 원하는 시간에 출발할 수 있다는 점입니다. // 물론 교통체증이 때때로 답답할 수 있지만 / 인내심을 갖고 내비게이션 앱을 사용하여 더 빠른 길을 찾으려고 노력합니다. // 장거리 운전을 할 때마다 / 저는 주로 휴게소에 들러 커피를 마시고 다리를 스트레칭합니다. // 이제 운전은 제 일상의 자연스러운 일부가 되어 / 이것 없이 사는 것은 거의 상상하기 어렵습니다. // 그것은 저에게 유연성과 편안함을 모두 제공하며 / 이것은 저에게 매우 중요합니다. // 돌이켜 보면 / 운전하는 법을 배운 것은 / 제가 지금까지 배운 것 중 가장 유용한 기술 중 하나였습니다. // 그것이 바로 제가 다른 어떤 교통수단보다 운전을 여전히 선호하는 이유입니다.",
                        englishSentence = "To be honest, I usually get around by car because it's the most convenient option for my daily routine. I drive to work, go grocery shopping, and even mostly on weekends, so having a car saves me a lot of time. My office is about thirty minutes away, and driving is much faster than taking public transportation. One thing I really like is that I don't have to worry about crowded buses or subway trains, especially during rush hour. I also enjoy listening to my favorite music while driving because it makes the trip much more enjoyable. Another advantage is that I can leave whenever I want without checking a bus or train schedule. Of course, traffic can sometimes be frustrating, especially on Friday evenings, but I try to stay patient and use a navigation app to find a faster route. Whenever I have a long drive, I usually stop at a rest area to grab a cup of coffee and stretch my legs. Driving has become such a natural part of my daily life that I can hardly imagine living without it. It gives me both flexibility and comfort, which are important to me. Looking back, learning how to drive was one of the most useful skills I've ever learned. That's why I still prefer driving over any other form of transportation."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "How has your way of getting around changed over the years? Compare the past and the present.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "제가 학생이었을 때는 / 운전면허가 없었기 때문에 / 거의 전적으로 대중교통에 의존했습니다. // 저는 주로 학교로 버스나 지하철을 탔고 / 통근하는 데 여분의 시간을 쓰는 것을 꺼려하지 않았습니다. // 사실 저는 종종 그 시간을 책을 읽거나 음악을 듣는 데 사용했습니다. // 모든 것은 / 제가 운전면허를 취득하고 첫 차를 구입한 후에 바뀌었습니다. // 처음에는 / 특히 복잡한 도로에서 운전하는 것에 대해 조금 긴장했습니다. // 하지만 경험을 더 쌓으면서 / 저는 운전대 뒤에서 훨씬 더 자신감을 갖게 되었습니다. // 요즘 저는 거의 모든 곳을 운전해서 이동합니다 / 왜냐하면 훨씬 더 편리하고 많은 시간을 절약해 주기 때문입니다. // 또 다른 큰 차이점은 / 버스나 기차 시간표에 대해 걱정하지 않고 / 즉흥적인 계획을 세울 수 있다는 점입니다. // 가끔 대중교통을 이용하더라도 / 대부분의 이동에서 운전이 저의 첫 번째 선택이 되었습니다. // 이것은 제 일상에서 훨씬 더 많은 자유와 유연성을 주었습니다. // 돌이켜 보면 / 운전면허를 취득한 것은 제가 이동하는 방식을 완전히 바꾸어 놓았습니다. // 저는 솔직히 대중교통에만 의존하던 때로 돌아가는 것을 상상할 수 없습니다.",
                        englishSentence = "When I was a student, I depended almost entirely on public transportation because I didn't have a driver's license. I usually took the bus or subway to school, and I didn't mind spending extra time commuting. In fact, I often used that time to read books or listen to music. Everything changed after I got my driver's license and bought my first car. At first, I was a little nervous about driving, especially on busy roads. However, as I gained more experience, I became much more confident behind the wheel. These days, I drive almost everywhere because it's much more convenient and saves me a lot of time. Another big difference is that I can make spontaneous plans without worrying about bus or train schedules. Even though I still use public transportation occasionally, driving has become my first choice for most trips. It has given me much more freedom and flexibility in my daily life. Looking back, getting my driver's license completely changed the way I travel. I honestly can't imagine going back to relying only on public transportation."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Tell me about a time when you had a problem while using transportation.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "몇 달 전 / 저는 퇴근 후 저녁을 먹기 위해 친구를 만나러 운전하고 있었습니다. // 금요일 저녁이었기 때문에 약간의 교통체증을 예상했지만 / 그렇게 심할 줄은 생각하지 못했습니다. // 약 중간쯤 갔을 때 / 저는 작은 접촉사고로 인해 발생한 거대한 교통체증에 갇히게 되었습니다. // 내비게이션 앱은 계속해서 도착 예정 시간을 늘렸고 / 저는 약속 시간에 늦을 것이라는 사실을 깨달았습니다. // 당황하는 대신 / 저는 친구에게 전화를 걸어 상황을 설명하고 늦어짐에 대해 사과했습니다. // 다행히 제 친구는 매우 너그럽게 이해해 주었고 / 조금 늦게 만나는 것을 제안했습니다. // 교통체증 속에 대기하는 동안 / 저는 차분함을 유지하고 시간을 잘 활용하기 위해 음악을 들었습니다. // 약 40분 후에 / 마침내 교통이 다시 움직이기 시작했습니다. // 계획했던 것보다 훨씬 늦게 도착했지만 / 우리는 여전히 함께 훌륭한 저녁 시간을 보냈습니다. // 그 경험은 어떤 상황들은 단순히 제 통제 범위를 벗어난다는 것을 상기시켜 주었습니다. // 그 이후로 / 저는 중요한 약속이 있을 때마다 항상 조금 더 일찍 출발합니다.",
                        englishSentence = "A few months ago, I was driving to meet a friend for dinner after work. Since it was Friday evening, I expected some traffic, but I didn't think it would be too bad. About halfway there, I got stuck in a huge traffic jam caused by a minor accident. The navigation app kept increasing the estimated arrival time, and I realized I was going to be late. Instead of panicking, I called my friend to explain the situation and apologized for the delay. Fortunately, my friend was very understanding and suggested meeting a little later. While I was waiting in traffic, I listened to music to stay relaxed and make good use of the time. After about forty minutes, traffic finally started moving again. I arrived much later than planned, but we still had a great dinner together. That experience reminded me that some situations are simply beyond my control. Since then, I always leave a little earlier whenever I have an important appointment."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Describe how you usually cook a meal. Walk me through the process from beginning to end.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "저는 일주일에 몇 번 집에서 저녁을 주로 요리합니다 / 왜냐하면 신선하고 건강한 음식을 먹는 것을 즐기기 때문입니다. // 제가 만들기 가장 좋아하는 음식 중 하나는 치킨 볶음밥입니다 / 왜냐하면 간단하고 맛있으며 시간이 많이 걸리지 않기 때문입니다. // 요리를 시작하기 전에 / 저는 닭고기, 채소, 밥, 달걀, 그리고 몇 가지 양념과 같은 모든 재료를 준비합니다. // 저는 또한 채소를 미리 씻고 썰어 둡니다 / 왜냐하면 이것이 요리 과정을 훨씬 더 수월하게 만들어 주기 때문입니다. // 모든 것이 준비되면 / 저는 팬을 달구고 닭고기가 노릇노릇한 갈색으로 변할 때까지 먼저 익힙니다. // 그런 다음 채소를 넣고 밥을 넣은 뒤 / 중간 불에서 모든 것을 골고루 볶습니다. // 그 후에 팬에 달걀 하나를 깨 넣고 / 간장과 후추를 넣기 전에 잘 섞어 줍니다. // 맛있는 냄새가 항상 주방을 가득 채우며 / 그것이 주로 제가 가장 좋아하는 순간입니다. // 음식이 준비되면 / 저는 그것을 예쁜 접시에 담는 것을 좋아합니다 / 왜냐하면 훌륭한 플레이팅이 음식을 훨씬 더 즐겁게 만들어 주기 때문입니다. // 저녁 식사 후에는 / 모든 것이 깔끔하고 정돈된 상태를 유지하도록 주방을 즉시 깨끗하게 청소합니다.",
                        englishSentence = "I usually cook dinner at home a few times a week because I enjoy eating fresh and healthy food. One of my favorite dishes to make is chicken fried rice because it's simple, delicious, and doesn't take much time. Before I start cooking, I prepare all the ingredients, such as chicken, vegetables, rice, eggs, and a few seasonings. I also wash and cut the vegetables in advance because it makes the cooking process much smoother. Once everything is ready, I heat up a pan and cook the chicken first until it turns golden brown. Then I add the vegetables, followed by the rice, and stir everything together over medium heat. After that, I crack an egg into the pan and mix it well before adding soy sauce and black pepper. The smell always fills the kitchen, and that's usually my favorite moment. When the meal is ready, I like to put it on a nice plate because good presentation makes the food even more enjoyable. After dinner, I clean the kitchen right away so everything stays neat and organized."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "How has your cooking habit changed over the years? Compare the past and the present.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "제가 어렸을 때는 / 요리를 거의 하지 않았습니다 / 왜냐하면 어머니께서 주로 저를 위해 모든 식사를 준비해 주셨기 때문입니다. // 그 당시에는 / 저는 요리하는 법을 몰랐고 / 요리를 배우는 것에도 큰 관심이 없었습니다. // 모든 것은 / 제가 독립하여 혼자 살기 시작하면서 / 요리가 중요한 생활 기술이라는 것을 깨달았을 때 바뀌었습니다. // 처음에는 / 소금을 너무 많이 넣거나 음식을 태우는 등 많은 실수를 저질렀습니다. // 때로는 음식을 모두 버리고 대신 배달 음식을 시켜 먹어야만 하기도 했습니다. // 하지만 저는 포기하지 않았고 / 점차 요리에 더 자신감을 갖게 되었습니다. // 요즘 저는 일주일에 여러 번 요리하며 / 유튜브나 요리 앱에서 새로운 레시피를 시도하는 것을 즐깁니다. // 저는 또한 과거에 그랬던 것보다 건강한 음식을 먹는 것에 훨씬 더 많은 주의를 기울입니다. // 요리는 제가 돈을 절약하고 동시에 식습관을 개선하는 데 큰 도움이 되었습니다. // 또 다른 큰 차이점은 / 제가 이제 단순히 자신만을 위해 요리하는 대신 / 가족과 친구들을 위해 요리하는 것을 즐긴다는 점입니다. // 돌이켜 보면 / 요리하는 법을 배운 것이 정말 기쁩니다 / 왜냐하면 그것이 제가 가장 좋아하는 취미 중 하나가 되었기 때문입니다. // 저는 요리가 더 이상 귀찮은 허드렛일이 아니라 / 제 일상의 즐거운 부분이라고 굳게 믿습니다.",
                        englishSentence = "When I was younger, I rarely cooked because my mother usually prepared every meal for me. Back then, I didn't know how to cook, and I wasn't very interested in learning. Everything changed when I started living on my own and realized that cooking was an important life skill. At first, I made a lot of mistakes, such as adding too much salt or overcooking the food. Sometimes I even had to throw everything away and order in instead. However, I didn't give up, and I gradually became more confident in cooking. These days, I cook several times a week and enjoy trying new recipes from YouTube or cooking apps. I also pay much more attention to eating healthy food than I did in the past. Cooking has helped me save money and improve my eating habits at the same time. Another big difference is that I now enjoy cooking for my family and friends instead of just cooking for myself. Looking back, I'm really glad I learned how to cook because it has become one of my favorite hobbies. I believe cooking is no longer a chore but an enjoyable part of my daily life."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Tell me about a time when something unexpected happened while you were cooking.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "몇 달 전 / 저는 어느 일요일 저녁에 가족을 위해 저녁을 요리하기로 결심했습니다. // 저는 이전에 한 번도 만들어 본 적이 없음에도 불구하고 / 크림 파스타를 만들어 가족들을 놀라게 해주고 싶었습니다. // 저는 온라인 레시피를 꼼꼼하게 따랐고 / 모든 것이 순조롭게 잘 진행되고 있다고 생각했습니다. // 하지만 소스를 준비하는 동안 / 저는 인지하지 못한 채 실수로 소금을 너무 많이 넣고 말았습니다. // 마침내 파스타 맛을 보았을 때 / 그것은 제가 기대했던 것보다 훨씬 더 짰습니다. // 가족들이 저녁을 기다리고 있었기 때문에 처음에는 당황스러웠습니다. // 포기하는 대신 / 저는 빠른 해결책을 찾기 위해 인터넷을 검색했고 / 약간의 우유를 첨가하면 짠맛을 줄일 수 있다는 것을 발견했습니다. // 다행히도 이것은 제가 기대했던 것보다 훨씬 더 잘 효과가 있었고 / 소스는 훨씬 더 부드러워졌습니다. // 제 가족들은 식사를 매우 맛있게 즐겼습니다. // 그 경험은 무언가 잘못되었을 때 당황하지 않는 법을 저에게 가르쳐 주었습니다. // 되돌아보면 / 그 작은 실수가 사실 제가 더 나은 요리사가 되도록 도와주었습니다.",
                        englishSentence = "A few months ago, I decided to cook dinner for my family on a Sunday evening. I wanted to surprise them by making pasta with cream sauce, even though I had never made it before. I carefully followed an online recipe and thought everything was going well. However, while I was preparing the sauce, I accidentally added too much salt without realizing it. When I finally tasted the pasta, it was much saltier than I expected. At first, I felt embarrassed because my family was waiting for dinner. Instead of giving up, I searched online for a quick solution and found that adding a little milk could reduce the salty taste. Fortunately, it worked much better than I expected, and the sauce became much smoother. My family enjoyed the meal. That experience taught me not to panic when something goes wrong. Looking back, that small mistake actually helped me become a better cook."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Describe where you usually go jogging. Why do you like that place?",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "저는 주로 제 집에서 약 15분 정도 떨어진 호수공원으로 조깅하러 갑니다. // 그곳은 긴 조깅 트랙과 울창한 나무들, 그리고 아름다운 풍경을 갖추고 있어 / 운동하기에 완벽한 장소입니다. // 저는 보통 일주일에 서너 번, 특히 날씨가 더 선선하고 편안한 저녁 시간에 그곳에 갑니다. // 달리기 시작하기 전에 / 저는 항상 몸을 풀고 부상을 방지하기 위해 약 5분 정도 스트레칭을 합니다. // 일단 조깅을 시작하면 / 음악이 저에게 동기부여를 유지해 주고 시간이 쏜살같이 지나가게 만들어 주기 때문에 / 좋아하는 플레이리스트를 재생합니다. // 제가 정말로 즐기는 한 가지는 / 바쁜 하루를 보낸 후 머리를 맑게 정리하는 데 도움이 되는 상쾌한 공기와 평화로운 분위기입니다. // 저는 또한 개를 산책시키거나, 자전거를 타거나, 가족들과 시간을 보내는 사람들을 보는 것을 좋아합니다 / 왜냐하면 이것이 공원에 따뜻하고 편안한 바이브를 선사하기 때문입니다. // 집을 나서기 전에는 가끔 피곤함을 느끼더라도 / 다녀온 후에는 항상 에너지가 충전됨을 느끼기 때문에 거기에 간 것을 결코 후회하지 않습니다. // 주말에는 종종 조금 더 길게 조깅을 하고 / 카페에서 커피 한 잔을 마시며 스스로에게 보상을 합니다. // 시간이 흐르면서 이 공원은 단순히 운동하는 장소 그 이상이 되었습니다. // 이곳은 제가 육체적으로나 정신적으로 모두 재충전하고 일상에서 잠시 벗어나 휴식을 취하는 소중한 공간입니다. // 그것이 바로 조깅을 시작하고 싶어 하는 모든 사람에게 제가 항상 이 장소를 추천하는 이유입니다.",
                        englishSentence = "I usually go jogging at a lake park that is about fifteen minutes away from my house. It has a long jogging trail, lots of trees, and beautiful scenery, so it's a perfect place to exercise. I normally go there three or four times a week, especially in the evening when the weather is cooler and more comfortable. Before I start running, I always spend about five minutes stretching to warm up and avoid injuries. Once I begin jogging, I put on my favorite playlist because music keeps me motivated and makes the time fly. One thing I really enjoy is the fresh air and the peaceful atmosphere, which help me clear my mind after a busy day. I also like watching people walking their dogs, riding bicycles, or spending time with their families because it gives the park a warm and relaxing vibe. Even though I sometimes feel tired before I leave home, I never regret going there because I always feel energized afterward. On weekends, I often jog a little longer and reward myself with a cup of coffee at a cafe. Over time, this park has become much more than just a place to exercise. It's where I recharge both physically and mentally and take a break from my daily routine. That's why I always recommend this place to anyone who wants to start jogging."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "How has your jogging routine changed over the years? Compare the past and the present.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "제가 처음 조깅을 시작했을 때는 / 그것에 대해 그다지 진지하지 않았고 한 달에 한두 번만 나갔습니다. // 그 당시에는 / 저는 조깅이 지루하다고 생각했고 / 단지 10분이나 15분 정도 달린 후에 종종 포기하곤 했습니다. // 저는 또한 스트레칭이나 편안한 페이스로 달리는 것에 대해 잘 알지 못했기 때문에 / 매우 빨리 지쳤습니다. // 팬데믹 기간 동안 활동적으로 머무르고 건강을 돌보는 것이 얼마나 중요한지 깨달았을 때 모든 것이 바뀌었습니다. // 그 이후로 조깅은 제 주간 루틴에서 가장 중요한 부분 중 하나가 되었습니다. // 요즘 저는 집 근처 호수공원에서 일주일에 서너 번 조깅을 합니다. // 저는 항상 뛰기 전에 준비운동을 하고 / 좋아하는 플레이리스트를 들으며 / 너무 빨리 달리려고 무리하는 대신 일정한 페이스를 유지합니다. // 또 다른 큰 차이점은 / 거리나 속도에 집착하기보다 경험 그 자체를 즐긴다는 점입니다. // 조깅은 퇴근 후 스트레스를 해소하고 머리를 맑게 정리하는 제가 가장 좋아하는 방법이 되었습니다. // 저는 또한 이 건강한 습관 덕분에 밤에 잠을 더 잘 자고 하루 종일 더 활력이 넘침을 느낍니다. // 돌이켜 보면 초기에 포기하지 않았던 것이 정말 다행스럽고 기쁩니다. // 이제 저는 공원에서 시간을 보내지 않는 제 한 주를 상상조차 할 수 없습니다.",
                        englishSentence = "When I first started jogging, I wasn't very serious about it and only went out once or twice a month. At that time, I thought jogging was boring, and I often gave up after running for just ten or fifteen minutes. I also didn't know much about stretching or running at a comfortable pace, so I got tired very quickly. Everything changed during the pandemic when I realized how important it was to stay active and take care of my health. Since then, jogging has become one of the most important parts of my weekly routine. These days, I jog three or four times a week at the lake park near my house. I always warm up before running, listen to my favorite playlist, and keep a steady pace instead of trying to run too fast. Another big difference is that I enjoy the experience itself rather than focusing on distance or speed. Jogging has become my favorite way to relieve stress and clear my mind after work. I also sleep better and feel more energetic throughout the day because of this healthy habit. Looking back, I'm glad I didn't give up at the beginning. Now, I can't imagine my week without spending some time at the park."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Tell me about a time when something unexpected happened while you were jogging.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "어느 날 저녁 / 저는 평소처럼 퇴근 후 호수공원으로 조깅하러 나갔습니다. // 날씨는 완벽했고 / 저는 편안한 페이스로 달리면서 좋아하는 플레이리스트를 즐기고 있었습니다. // 달리기를 시작한 지 약 20분쯤 되었을 때 / 난데없이 갑자기 먹구름이 몰려왔습니다. // 미처 상황을 깨닫기도 전에 / 비가 거세게 내리기 시작했고 / 많은 사람들이 비를 피할 쉼터를 찾기 위해 급하게 뛰어갔습니다. // 처음에는 계속 달릴까 생각도 해보았지만 / 길이 미끄러워져 안전을 위해 멈추기로 결정했습니다. // 다행히 공원 근처에 아늑한 카페가 있어서 / 저는 그곳에서 비가 그치기를 기다렸습니다. // 따뜻한 커피를 마시며 쉬는 동안 / 저는 역시 비를 피하러 온 다른 러너와 대화를 나누게 되었습니다. // 우리는 조깅 노하우와 좋아하는 러닝화, 그리고 주변의 멋진 공원들에 대해 이야기를 나누었습니다. // 놀랍게도 나쁜 상황처럼 보였던 일이 오히려 유쾌하고 즐거운 경험으로 바뀌었습니다. // 약 30분 후에 비가 멈추었고 / 저는 아주 좋은 기분으로 집으로 돌아왔습니다. // 돌이켜 보면 / 그날은 인생의 모든 일이 계획대로만 흘러가는 것은 아니라는 소중한 교훈을 가르쳐 주었습니다. // 그날 이후로 / 저는 달리기를 나가기 전에 항상 일기예보를 확인하는 습관을 들였습니다.",
                        englishSentence = "One evening, I went jogging at the lake park after work, just like I usually do. The weather was perfect, and I was enjoying my favorite playlist while running at a comfortable pace. About twenty minutes into my run, dark clouds suddenly appeared out of nowhere. Before I knew it, it started raining heavily, and people rushed to find shelter. At first, I considered continuing my run, but the path became slippery, so I decided to stop for safety. Fortunately, there was a cozy cafe near the park, so I waited there for the rain to stop. While sipping warm coffee, I struck up a conversation with another runner who was also sheltering from the rain. We chatted about jogging tips, our favorite running shoes, and scenic parks in the area. Surprisingly, what seemed like a bad situation turned into a delightful experience. After about thirty minutes, the rain stopped, and I headed home in high spirits. Looking back, that day taught me that not everything in life goes according to plan. Since then, I always make it a habit to check the weather forecast before heading out for a run."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "What kind of weather do you like? Why?",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "저는 개인적으로 선선하고 맑은 날씨를 가장 좋아하며 / 특히 봄과 가을 동안을 좋아합니다. // 그 계절들은 기온이 쾌적하고 / 공기가 신선하고 깨끗하게 느껴지기 때문에 완벽합니다. // 날씨가 좋을 때마다 / 저는 집에 머무는 대신 야외에서 시간을 보내는 것을 좋아합니다. // 예를 들어 저는 종종 호수공원으로 조깅을 가거나, 산책을 하거나, 커피 한 잔과 함께 벤치에서 편안하게 휴식을 취합니다. // 야외에 있는 것은 제 머리를 맑게 하고 / 일상의 스트레스를 잊는 데 큰 도움이 됩니다. // 제가 온화한 날씨를 좋아하는 또 다른 이유는 / 너무 덥거나 너무 춥다고 느끼지 않고 많은 야외 활동을 즐길 수 있다는 점입니다. // 저는 또한 봄의 형형색색 꽃들과 가을의 아름다운 단풍을 보는 것을 사랑합니다 / 왜냐하면 그것들이 저를 편안하게 만들어 주기 때문입니다. // 주말에는 날씨가 쾌적할 때 가끔 친구들을 만나 피크닉을 즐기기도 합니다. // 이와 같은 순간들은 항상 저를 좋은 기분으로 만들어 주고 / 제 배터리를 재충전하도록 도와줍니다. // 그것이 바로 제가 계획을 세울 때마다 맑은 날을 항상 손꼽아 기다리는 이유입니다.",
                        englishSentence = "I personally enjoy cool and sunny weather the most, especially during spring and fall. Those seasons are perfect because the temperature is comfortable, and the air feels fresh and clean. Whenever the weather is nice, I like spending time outdoors instead of staying home. For example, I often go jogging at a lake park, take a walk, or simply relax on a bench with a cup of coffee. Being outside helps me clear my mind and forget about my daily stress. Another reason I like mild weather is that I can enjoy many outdoor activities without feeling too hot or too cold. I also love seeing colorful flowers in spring and beautiful fall leaves in the fall because they make me feel relaxed. On weekends, I sometimes meet my friends and have a picnic when the weather is pleasant. Moments like these always put me in a good mood and help me recharge my batteries. That's why I always look forward to sunny days whenever I make plans."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "How has the weather in your area changed over the years? Compare the past and the present.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "제가 어렸을 때는 / 제 지역의 날씨가 오늘날보다 훨씬 더 예측 가능했습니다. // 뚜렷한 사계절이 존재했고 / 각 계절은 매년 거의 동일한 기간 동안 지속되었습니다. // 여름은 따뜻했지만 견딜 수 없을 정도로 덥지는 않았으며 / 겨울은 때때로 눈을 즐길 수 있을 만큼 충분히 추웠습니다. // 하지만 지난 몇 년 동안 날씨가 꽤 많이 변했다고 생각합니다. // 요즘 여름은 훨씬 더 덥고 / 폭염이 이전보다 훨씬 더 오래 지속되는 것 같습니다. // 다른 한편으로 겨울은 종종 더 짧고 눈이 덜 내리는 것처럼 느껴져 / 조금 실망스럽습니다. // 또 다른 눈에 띄는 변화는 / 갑작스러운 폭우가 훨씬 더 흔해졌다는 점입니다. // 이러한 예측할 수 없는 날씨 상황 때문에 / 저는 야외 계획을 세우기 전에 항상 일기예보를 확인합니다. // 예를 들어 저는 날씨 앱을 먼저 확인하지 않고는 결코 조깅을 가거나 피크닉을 계획하지 않습니다. // 비록 날씨를 바꿀 수는 없지만 / 적어도 이에 대비할 수는 있습니다. // 돌이켜 보면 / 저는 어렸을 때 즐겼던 그 온화한 날씨가 정말 그립습니다. // 저는 단지 미래 세대들도 여전히 아름다운 사계절을 경험할 수 있기를 희망할 뿐입니다.",
                        englishSentence = "When I was younger, the weather in my area was much more predictable than it is today. There were four distinct seasons, and each season lasted for about the same amount of time every year. Summers were warm but not unbearably hot, and winters were cold enough to enjoy snow from time to time. However, I think the weather has changed quite a lot over the past several years. These days, summers are much hotter, and heat waves seem to last much longer than before. On the other hand, winters often feel shorter and less snowy, which is a little disappointing. Another noticeable change is that sudden heavy rain has become much more common. Because of these unpredictable weather conditions, I always check the forecast before making outdoor plans. For example, I never go jogging or plan a picnic without checking the weather app first. Even though I can't change the weather, I can at least prepare for it. Looking back, I really miss the mild weather I enjoyed when I was younger. I just hope future generations will still be able to experience four beautiful seasons."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Tell me about a time when the weather changed your plans.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "지난봄 / 저는 제가 가장 좋아하는 호수공원에서 몇몇 가까운 친구들과 피크닉을 하기로 계획을 세웠습니다. // 일기예보에서는 하루 종일 맑을 것이라고 해서 / 우리는 샌드위치, 과일, 음료를 챙겨 이른 오후에 공원으로 향했습니다. // 처음에는 모든 것이 완벽했고 / 우리는 아름다운 날씨를 마음껏 만끽하고 있었습니다. // 하지만 난데없이 어두운 구름이 나타났고 / 갑자기 비가 매우 거세게 내리기 시작했습니다. // 우리는 서둘러 짐을 챙겨 비를 피할 수 있는 장소를 찾았습니다. // 다행히 공원 근처에 아늑한 카페가 있어서 / 우리는 오후의 남은 시간을 그곳에서 보내기로 결정했습니다. // 우리는 커피와 디저트를 주문하고 몇 시간 동안 이야기를 나누었으며 / 심지어 보드게임도 즐겼습니다. // 결국 우리의 피크닉이 카페 모임으로 바뀌어 버렸기 때문에 우리는 함께 웃었습니다. // 그 경험은 예상치 못한 상황이 때로는 훨씬 더 좋은 추억을 만들어 줄 수도 있다는 것을 가르쳐 주었습니다. // 그 이후로 / 저는 야외 계획을 세울 때마다 항상 플랜 B를 준비해 둡니다.",
                        englishSentence = "Last spring, I made plans to have a picnic with a few close friends at my favorite lake park. The weather forecast said it would be sunny all day, so we packed sandwiches, fruit, and drinks and headed to the park early in the afternoon. At first, everything was perfect, and we were enjoying the beautiful weather. However, out of nowhere, dark clouds appeared, and it suddenly started raining very heavily. We quickly packed up our things and looked for a place to stay dry. Fortunately, there was a cozy cafe near the park, so we decided to spend the rest of the afternoon there instead. We ordered coffee and desserts, talked for hours, and even played a few board games. In the end, we laughed because our picnic had turned into a cafe gathering. That experience taught me that unexpected situations can sometimes create even better memories. Since then, I always have a plan B whenever I make outdoor plans."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "You find out that your friend recently bought a new electronic device. Call your friend and ask three or four questions about it.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "안녕, 나 마이클이야. // 최근에 새로운 태블릿을 샀다고 들었는데 / 나도 사실 하나 구매할까 생각 중이라서 / 이것에 대해 조금 더 이야기해 줄 수 있을까 해서 전화했어. // 첫째로, 어떤 모델을 샀어? // 왜 다른 브랜드 대신 그 특정 모델을 선택했어? // 둘째로, 성능에 대해서도 궁금해. // 멀티태스킹을 하기에 충분히 빨라? // 배터리는 보통 얼마나 오래 지속돼? // 그리고 사용하기 시작한 이후로 기술적인 문제는 전혀 없었어? // 셋째로, 가격에 대해서도 알고 싶어. // 많이 비쌌어? // 할인이나 특별 프로모션 혜택을 받았어? // 그리고 그 돈의 가치가 충분히 있다고 생각해? // 마지막으로, 나 같은 사람에게도 이 기기를 추천해 주고 싶어? // 만약 아니라면 내가 고려해 볼 만한 다른 모델이 있을까? // 너의 소중한 경험을 공유해 줘서 고마워, 조언 정말 감사해. 안녕.",
                        englishSentence = "Hi, this is Michael. I've heard that you recently bought a new tablet, and I'm actually thinking about getting one myself, so I hope you could tell me a little more about it. First of all, what model did you buy, and why did you choose that particular model instead of other brands? Secondly, I'm also curious about its performance: is it fast enough for multitasking, how long does the battery usually last, and have you experienced any technical issues since you started using it? Third, I'd also like to know about the price: was it expensive, did you get any discounts or special promotions, and do you think it's worth the money? Finally, would you recommend it to someone like me, or if not, are there any other models I should consider? Thanks for sharing your experience, I really appreciate your advice. Bye."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "You borrowed your friend's electronic device and accidentally damaged it. Call your friend, explain what happened, and suggest a solution.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "안녕, 나 마이클이야. // 안타까운 일이 발생해서 상황을 너에게 설명하려고 전화했어. // 어제 영상을 보고 작업을 좀 하려고 네 태블릿을 빌렸잖아. // 처음에는 모든 것이 괜찮았어. // 하지만 다른 방으로 옮기는 도중에 실수로 떨어뜨리고 말았어. // 불행히도 바닥에 떨어지면서 화면에 금이 가고 말았어. // 여전히 작동하는지 확인하기 위해 즉시 켜보았어. // 전원은 켜지지만 화면 일부분이 제대로 반응하지 않아. // 네가 최근에 구매한 기기라는 걸 잘 알고 있어서 정말 마음이 무겁고 미안해. // 손상에 대해서는 내가 전적으로 모든 책임을 지고 싶어. // 혹시 어디서 구매했는지 알려줄 수 있을까? // 수리 비용은 내가 기꺼이 전액 부담할게. // 만약 수리가 불가능하다면 새 제품으로 교체하는 비용도 기꺼이 도울게. // 다시 한번 이번 일에 대해 정말 진심으로 미안해. // 내가 어떻게 해주면 좋을지 편하게 알려줘, 안녕.",
                        englishSentence = "Hi, this is Michael. I'm calling because something unfortunate happened, and I wanted to explain the situation to you. Yesterday, I borrowed your tablet to watch a video and do some work. Everything was fine at first, but while I was carrying it to another room, I accidentally dropped it. Unfortunately, the screen cracked when it hit the floor. I immediately checked to see if it was still working; the device turns on, but a part of the screen isn't responding properly. I feel really bad about what happened because I know you recently bought it. I'd like to take full responsibility for the damage. I am wondering if you could tell me where you purchased it; I'd be happy to pay for the repair costs, and if repairing it isn't possible, I'd also be willing to help you replace it with a new one. Again, I'm really sorry about this. Please let me know what you'd like me to do. Bye."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Tell me about a time when an electronic device caused a problem for you. Give me as many details as possible.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "전자기기가 저에게 문제를 일으켰던 경험에 대해 말씀드리고 싶습니다. // 몇 년 전, 중요한 발표 바로 직전에 제 노트북이 갑자기 작동을 멈췄습니다. // 처음에는 배터리가 방전된 줄 알고 충전기에 꽂고 잠시 기다렸습니다. // 하지만 여전히 전원이 켜지지 않았습니다. // 모든 발표 자료가 그 노트북 안에 저장되어 있었기 때문에 저는 당황하기 시작했습니다. // 설상가상으로 발표는 바로 다음 날 아침으로 예정되어 있었습니다. // 저는 스스로 문제를 해결하기 위해 몇 시간을 보냈습니다. // 인터넷을 검색하고, 튜토리얼 영상을 시청하고, 고객센터에 전화까지 해보았습니다. // 불행히도 아무런 효과가 없었습니다. // 결국 저는 친구에게 노트북을 빌렸고 / 클라우드 저장 서비스에서 겨우 파일들을 복구해 낼 수 있었습니다. // 고맙게도 발표는 아주 성공적으로 잘 끝났습니다. // 그 이후로 저는 중요한 파일들을 정기적으로 백업하는 것의 중요성을 뼈저리게 배웠습니다. // 비록 스트레스가 큰 경험이었지만, 저에게 매우 값진 교훈을 가르쳐 주었습니다.",
                        englishSentence = "I'd like to talk about a time when an electronic device caused a problem for me. A few years ago, my laptop suddenly stopped working right before an important presentation. At first, I thought the battery had died, so I plugged it in and waited for a while. However, it still wouldn't turn on. I started to panic because all of my presentation materials were stored on that laptop. To make matters worse, the presentation was scheduled for the next morning. I spent several hours trying to solve the problem on my own. I searched online, watched tutorial videos, and even called customer support. Unfortunately, nothing worked. In the end, I borrowed a laptop from a friend and managed to recover my files from a cloud storage service. Thankfully, the presentation went well. Since then, I've learned the importance of backing up important files regularly. Although it was a stressful experience, it taught me a valuable lesson."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Describe a gym you usually go to. What kind of place is it, and what do you usually do there?",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "제가 집 근처에서 정기적으로 다니는 헬스장에 대해 이야기하고 싶습니다. // 이곳은 매우 광범위한 운동 기구와 편의시설을 완벽하게 갖춘 현대적인 피트니스 센터입니다. // 도보로 약 10분 거리에 불과하기 때문에 바쁜 날에도 쉽게 운동 시간을 낼 수 있습니다. // 헬스장은 깨끗하고 에너지 넘치는 분위기를 지니고 있어 / 항상 제 자신을 더 열심히 채찍질하도록 강한 동기를 부여합니다. // 저는 주로 퇴근 후 일주일에 서너 번 그곳에서 운동합니다. // 제 전형적인 루틴은 웨이트 트레이닝, 유산소 운동, 그리고 스트레칭을 포함합니다. // 스트레스를 받거나 정신적으로 지칠 때마다, 운동은 제 머리를 맑게 하고 배터리를 재충전하는 데 큰 도움이 됩니다. // 제가 정말 좋아하는 한 가지는 트레이너들이 다가가기 쉽고 항상 유용한 조언을 기꺼이 해준다는 점입니다. // 시간이 흐르면서 헬스장에 가는 것은 단순한 습관 이상이 되었으며 / 이제는 제 라이프스타일의 필수적인 일부가 되었습니다. // 저는 규칙적인 운동이 신체적으로 건강하고 정신적으로 예리한 상태를 유지해 준다고 솔직히 믿습니다.",
                        englishSentence = "I'd like to talk about the gym I regularly go to near my house. It's a modern fitness center that's fully equipped with a wide range of workout machines and facilities. Since it's only about ten minutes away on foot, I can easily squeeze in a workout even on busy days. The gym has a clean and energetic atmosphere, which always motivates me to push myself harder. I usually work out there three to four times a week after work. My typical routine includes weight training, cardio exercises, and stretching. Whenever I feel stressed out or mentally drained, exercising helps me clear my head and recharge my batteries. One thing I really like is that the trainers are approachable and always willing to give helpful advice. Over time, going to the gym has become more than just a habit because it's now an essential part of my lifestyle. I honestly believe that regular exercise keeps me both physically fit and mentally sharp."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Tell me about a memorable experience you had at a gym.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "헬스장에서 겪은 가장 기억에 남는 경험은 제가 처음 개인 PT 세션에 등록했을 때 일어났습니다. // 그 당시 저는 몸매와 체력이 완전히 엉망이었고 대부분의 운동 기구를 어떻게 사용하는지 전혀 알지 못했습니다. // 솔직히 말해서 다른 모든 사람들은 너무나 경험이 많고 자신감 넘쳐 보여서 저만 물 밖으로 나온 물고기처럼 어색하게 느껴졌습니다. // 하지만 제 트레이너는 모든 과정마다 저를 격려해 주었고 제가 궤도에서 벗어나지 않도록 세심하게 도와주었습니다. // 처음 몇 주 동안 저는 많이 고군분투했고 심지어 수건을 던지고(포기하고) 싶을 때도 있었습니다. // 하지만 조금씩 제 몸과 에너지 레벨에서 긍정적인 변화를 알아차리기 시작했습니다. // 가장 잊을 수 없는 순간은 한때 불가능하다고 생각했던 고강도 운동 세션을 성공적으로 완수했을 때였습니다. // 운동을 마친 후 저는 제 자신이 진심으로 자랑스러웠고 끈기가 얼마나 중요한지 깨달았습니다. // 그 경험은 꾸준함을 유지하는 것이 결국 좋은 결실을 맺는다는 것을 가르쳐 주었습니다. // 그 이후로 운동은 자신감을 키우고 스트레스를 해소하는 최고의 방법 중 하나가 되었습니다.",
                        englishSentence = "One memorable experience I had at a gym happened when I first signed up for personal training sessions. At that time, I was completely out of shape and had no clue how to use most of the workout machines. To be honest, I felt like a fish out of water because everyone else seemed so experienced and confident. However, my trainer encouraged me every step of the way and helped me stay on track. During the first few weeks, I struggled a lot and even considered throwing in the towel. But little by little, I began to notice positive changes in my body and energy level. The most unforgettable moment was when I successfully completed a high-intensity workout that I once thought was impossible. After finishing it, I felt incredibly proud of myself and realized how important persistence is. That experience taught me that staying consistent can eventually pay off. Since then, working out has become one of the best ways for me to build confidence and relieve stress."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Why do you think gyms are becoming popular these days?",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "요즘 헬스장이 점점 더 인기를 얻고 있는 이유는 더 많은 사람들이 건강과 웰빙에 깊은 관심을 기울이고 있기 때문이라고 생각합니다. // 오늘날 많은 사람들은 바쁜 일상을 살아가며 책상에 앉아 긴 시간을 보내기 때문에 활동적으로 머무를 수 있는 방법을 원합니다. // '건강이 곧 재산'이라는 속담처럼 사람들은 그 생각을 진지하게 받아들이기 시작했습니다. // 또 다른 이유는 소셜 미디어가 피트니스 문화에 커다란 영향을 미쳤기 때문입니다. // 사람들은 온라인에서 운동 영상, 피트니스 인플루언서, 그리고 바디 프로필 변화 이야기에 지속적으로 노출됩니다. // 결과적으로 많은 개인들이 헬스장을 찾고 스스로를 향상시키도록 동기부여를 받습니다. // 요즘 헬스장들은 기본적인 운동 기구 그 이상을 제공합니다. // 예를 들어 많은 체육관들이 요가 수업, 개인 PT, 심지어 웰니스 프로그램까지 운영합니다. // 개인적으로 저는 헬스장에 가는 것이 신체적 건강과 정신적 건강을 모두 향상시킬 수 있어 일석이조의 효과를 준다고 생각합니다. // 비록 정기적으로 운동하는 것이 처음에는 도전일 수 있지만, 저는 이것이 스스로를 위해 할 수 있는 가장 건강한 투자 중 하나라고 믿습니다.",
                        englishSentence = "I think gyms are becoming increasingly popular because more people are paying attention to their health and well-being. These days, many people lead hectic lifestyles and spend long hours sitting at desks, so they want a way to stay active. As the saying goes, health is wealth, and people are beginning to take that idea seriously. Another reason is that social media has greatly influenced fitness culture. People are constantly exposed to workout videos, fitness influencers, and transformation stories online. Consequently, many individuals feel motivated to hit the gym and improve themselves. Gyms these days also offer much more than basic exercise equipment; for example, many gyms provide yoga classes, personal training, and even wellness programs. Personally, I think going to the gym helps people kill two birds with one stone because they can improve both their physical and mental health. Although working out regularly can be a challenge at first, I believe it is one of the healthiest investments people can make for themselves."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "In your background survey, you indicated that you like going out for coffee. When do you usually go to coffee shops? What do you typically order, and what do you do while you are there?",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "제가 가장 좋아하는 일 중 하나는 카페에서 커피 한 잔을 마시는 것이며 / 저는 적어도 일주일에 두 번은 그렇게 합니다. // 저는 주로 퇴근 후에 긴장을 풀고 조용히 독서할 시간을 갖기 위해 커피숍을 방문합니다. // 제 전형적인 루틴은 시나몬을 듬뿍 넣은 카푸치노와 정말 진하고 깊은 다크 초콜릿 케이크를 주문하는 것입니다. // 이런 방식으로 저는 스트레스를 풀 수 있지만 / 동시에 그 대신 약간의 살이 찌기도 합니다. // 무언가가 들어오면 무언가가 나가는 것과 같으며 / 그것은 그만큼 단순한 이치입니다. // 더운 날에는 가끔 커다란 아이스 아메리카노를 마시러 갑니다. // 제가 방문하는 몇몇 커피숍들은 쿠키와 케이크의 훌륭한 선택지를 갖추고 있지만 / 저는 체중을 신경 써야 하기 때문에 그것들을 주문하는 것을 피하려고 노력합니다. // 저는 종종 잡지나 신문을 카페에 가져와서 음료를 음미하며 그것들을 읽습니다. // 그래서 저는 때때로 오랜 시간 동안 머물기도 합니다. // 제 자신만을 위한 이런 시간을 갖는 것은 정말 중요합니다. // 제 일이 워낙 스트레스가 많기 때문에 저는 스트레스를 풀 수 있는 방법들이 필요합니다. // 커피숍에 앉아 있는 것은 저를 좋은 기분으로 만들어 주고 제 문제들을 잊도록 도와줍니다. // 이렇게 하는 것은 제가 쉬고 재충전할 수 있게 해줍니다.",
                        englishSentence = "One of my favorite things to do is grab a coffee at a cafe, and I do so at least twice a week. I usually visit coffee shops after work to unwind and have some quiet time to read. My typical routine involves ordering a cappuccino with lots of cinnamon and a really deep, dark chocolate cake. In this way, I can relieve my stress, but at the same time, I gain some weight instead. It is like when something comes in, something goes out; it's as simple as that. On a hot day, I sometimes go for a large iced Americano. Some of the coffee shops I visit have a great selection of cookies and cakes, but I try to avoid ordering those because I really need to watch my weight. I often bring magazines or newspapers to cafes and read them while savoring my drink. So, I sometimes stick around for a long time. Having this time to myself is really important. Since my job is so stressful, I need ways to de-stress. Sitting in a coffee shop puts me in a good mood and helps me forget about my problems. Doing this allows me to rest and recharge."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "How have cafes changed over the years? How were they in the past and how are they now? Is there anything special about cafes today? Tell me everything about what cafes are like and in what way they are different from the past.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "카페는 수년에 걸쳐 여러 면에서 많이 변화했습니다. // 과거에는 수많은 개인 소유의 커피숍들이 있곤 했습니다. // 하지만 대형 프랜차이즈 카페 체인의 증가로 인해 많은 개인 카페들이 점차 사라지고 있습니다. // 대형 카페 체인들은 커피 애호가들 사이에서 점점 더 인기를 얻고 있습니다. // 그들은 기존 고객을 유지하는 동시에 새로운 고객을 끌어들이는 혁신적인 마케팅 전략을 지속적으로 도입합니다. // 이 대형 카페 체인들은 새로운 음료와 상품을 개발하는 데 많은 시간과 돈을 투자하며 / 이는 과거에는 주로 볼 수 없었던 방식입니다. // 이는 고객들이 단순히 커피 한 잔 이상의 경험을 즐길 수 있도록 만들어 줍니다. // 새로운 시즌 한정 음료와 텀블러, 컵과 같은 세련되게 디자인된 굿즈 상품들은 대형 카페 체인의 매우 효과적인 마케팅 전략임이 입증되었습니다. // 제 생각에 카페는 과거에 비해 훨씬 더 전문적이고 트렌디한 공간으로 진화했습니다.",
                        englishSentence = "Cafes have changed in many ways over the years. In the past, there used to be a bunch of privately owned coffee shops. However, many privately owned coffee shops have been disappearing due to the increase in large coffee shop chains. Coffee shop chains have become increasingly popular among coffee lovers. They employ new marketing strategies consistently, which maintains their existing customers while attracting new customers at the same time. These large coffee shop chains invest their time and money in developing new drinks and products, which was not usually done in the past. This allows the customers to enjoy something more than just coffee. New seasonal drinks and newly designed products like tumblers and cups have also proven to be effective marketing strategies for big coffee shop chains. I guess cafes have become a lot more professional than in the past."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "What was the reason you first started to go to cafes? Do you remember your first visit to a cafe? Please tell me the full story of the day you went to the cafe for the first time.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "제가 처음 카페에 갔던 것은 대학교에 갓 입학한 직후였습니다. // 제가 고등학교를 졸업했을 당시 우리나라에서는 고등학생들이 카페에 가는 것이 일반적인 문화가 아니었습니다. // 음료를 사서 그곳에 몇 시간 동안 머무는 것은 대학생들의 상징과도 같았습니다. // 그래서 카페에 앉아 있는 저는 마치 진짜 대학생이 된 듯한 기분을 느꼈습니다. // 하지만 문을 열고 들어가 메뉴판을 본 순간 저는 몹시 당황하고 놀랐습니다. // 카페라테나 아메리카노 같은 낯선 이름들이 저에게는 너무나 이국적으로 느껴졌습니다. // 그 당시 저는 미국 문화를 좋아했지만 한 번도 가본 적이 없어서 / 마치 뉴요커 같은 느낌을 줄 것 같다는 생각에 아메리카노를 주문했습니다. // 저는 음료가 준비되기를 조바심을 내며 기다렸습니다. // 하지만 음료를 받아 한 모금 맛을 보았을 때 / 너무나 쓴맛 때문에 거의 뱉어낼 뻔했습니다. // 저는 커피가 그렇게 쓰고 강렬한 맛일 줄은 전혀 알지 못했습니다. // 저는 심하게 기침을 하기 시작했고 / 카페 안의 모든 사람들이 너무 심하게 기침하는 저를 쳐다보았습니다. // 너무나 부끄럽고 당황스러워서 저는 가능한 한 빨리 그 카페를 뛰쳐나왔습니다.",
                        englishSentence = "The first time I went to a cafe was just after I started college. Back when I graduated from high school, it was not the norm for high school students to go to cafes in my country. Buying a drink and staying there for a few hours was the mark of a college student. So, there I was in a cafe feeling like a real college student. But the moment I entered and saw the menu, I was very surprised. Names like cafe latte and Americano all seemed very exotic to me. At that time, I liked America but I had never been there, so I picked Americano, thinking that it would make me feel like a New Yorker. I impatiently waited for my drink to be ready. However, when I received my drink and tasted it, I almost threw up because of its bitter taste. I had never known that coffee would be that bitter and strong. I started coughing, and everyone in the cafe stared at me because I was coughing too hard. So I ran out of there as quickly as I could because I was so embarrassed."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Please tell me about the activities people do at a park you often visit. What sort of things do children do? How are they similar to or different from what adults do? Give me as many details as possible.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "제가 사는 곳에서 걸어서 약 10분 거리에 아주 멋진 공원이 있습니다. // 중간 크기의 규모이며 할 수 있는 활동들이 정말 많습니다. // 산책로가 잘 조성되어 있고 스포츠를 즐길 수 있는 다양한 공간들이 구비되어 있습니다. // 이 공원은 모든 사람들을 위한 무언가를 갖추고 있습니다. // 아이들과 어른들 모두 이곳을 사랑하는 것 같습니다. // 아이들은 또래 친구들과 어울려 놀기 위해 공원에 갑니다. // 공원을 찾는 어린아이들은 친구들과 술래잡기 같은 게임을 하는 것을 매우 좋아합니다. // 그들은 그네나 미끄럼틀 같은 놀이기구를 탑니다. // 그들은 또한 모래놀이를 하거나 공을 가지고 놉니다. // 아이들은 항상 소리치고, 웃고, 사방을 뛰어다닙니다. // 조금 더 나이 든 청소년들은 주로 즉석 농구 게임이나 축구를 즐깁니다. // 아이들과 달리 저 같은 어른들은 공원에서 많은 스포츠를 하지는 않지만 / 산책은 확실하게 즐깁니다. // 어른들은 날씨가 선선해지면 저녁 식사 후 친구들과 함께 산책하러 가는 것을 좋아합니다. // 때때로 손을 꼭 잡고 공원을 가로질러 걸어가는 데이트 커플들도 볼 수 있습니다. // 나무 아래에서 편안하게 쉬는 것 역시 공원에서 하루를 보내는 어른들의 아주 흔한 활동입니다.",
                        englishSentence = "There is a nice park about a 10-minute walk from where I live. It's medium-sized, and there are lots of things to do. There's a walking trail, and there are all kinds of areas to play sports. The park has something for everybody. Kids and adults both seem to love it. Children go to parks to hang out with their peers. The young children who visit the park love playing games like tag with their friends. They go on rides such as swings or slides. They also play with the sand or play with a ball there. They are always shouting, laughing, and running around. There are fewer older kids who visit, and those who do typically play pickup games of basketball and soccer. Unlike the kids, adults like me don't play so many sports at the park, but they definitely take walks there. They like to go walking there with their friends after dinner once the weather has cooled off. Occasionally, there are some people on dates walking hand-in-hand through the park. Kicking back under a tree is also a common activity for adults having a day out at the park."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "How did you first start going to parks? What made you visit parks in the first place? Why do you go to parks now? Tell me how your interest in going to parks has changed over the years. Give me all the details.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "저는 평생 동안 공원을 다녀왔지만 / 공원에 가는 것에 대한 저의 관심과 목적은 수년에 걸쳐 많이 변했습니다. // 제가 언제 처음 공원에 가기 시작했는지는 정확히 기억나지 않지만 / 어렸을 때 공원에 자주 갔던 기억은 생생합니다. // 그 당시 저는 주말이나 날씨가 좋을 때 가족들과 소풍을 가기 위해 공원을 찾곤 했습니다. // 저는 또한 공원에서 친구들과 함께 어울려 놀곤 했습니다. // 하지만 요즘 저는 주로 운동을 하기 위해 공원에 갑니다. // 가끔 조깅을 하거나 테니스를 치러 그곳에 갑니다. // 테니스 코트와 같이 어른들을 위한 스포츠 시설들이 아주 잘 갖추어져 있습니다. // 또한 저는 산책을 하거나, 자전거를 타거나, 반려견을 산책시키기도 합니다. // 게다가 벤치에 편안히 앉아 스마트폰으로 동영상을 시청하거나 가벼운 낮잠을 즐길 수도 있습니다. // 봄에는 활짝 핀 벚꽃을 감상하고 공원의 산들바람을 만끽합니다.",
                        englishSentence = "I have been going to parks my entire life, and my interest regarding going to parks has changed a lot over the years. Well, I don't exactly remember when I first started going to parks, but I remember going to parks a lot when I was a kid. Back then, I used to go to parks for picnics with my family on the weekends or when the weather was nice. I also used to hang out with my friends at parks. But these days, I go to parks to get some exercise. I go there to jog or play tennis sometimes. There are a lot of sports facilities for adults such as tennis courts. Also, I take a walk, ride a bicycle, or walk my dog. Plus, I can sit on a bench and watch some movie clips on my smartphone or take a nap. In the spring, I look at the cherry blossoms and enjoy the breeze at the park."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Compare the park you went to as a child to the park today. What differences and similarities do you see? Tell me how the park has changed over the years.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "제가 어렸을 때와 비교하면 공원은 정말 많이 변했습니다. // 제가 더 어렸을 적에 그 공원은 어린이들을 위한 놀이터에 더 가까웠습니다. // 푸른 잔디밭이 아니라 모래바닥이어서 제 친구들과 저는 모래성을 쌓고 놀다가 집에 가기 전에 흙을 털어내야 했습니다. // 그래서 몸과 옷에 흙이 많이 묻었고 어머니께서 그것을 무척 싫어하셨습니다. // 또한 그 공원에는 지금과 같은 맑은 호수 대신에 매우 악취가 나는 늪이 있었습니다. // 물이 고여 있고 지독한 냄새가 났기 때문에 아무도 그 늪 근처에는 가고 싶어 하지 않았습니다. // 고인 물이었기 때문에 모기도 엄청나게 많았습니다. // 하지만 지금 그 공원은 아이들과 어른들 모두가 자연을 만끽할 수 있는 완벽한 장소가 되었습니다. // 놀이터는 안전한 고무 매트 바닥으로 바뀌어 몸에 흙이 달라붙지 않습니다. // 사람들은 호수에 살고 있는 물고기와 오리들에게 먹이를 주고 그들이 살아가는 모습을 관찰할 수 있습니다. // 공원이 완전히 탈바꿈하면서 공원을 찾는 방문객들의 모습도 크게 바뀌었습니다. // 과거에는 공원에 굳이 가려는 사람들이 많지 않았습니다. // 하지만 지금은 점점 더 많은 사람들이 운동을 하거나 일광욕을 즐기기 위해 공원을 찾습니다. // 지금은 다소 붐비는 곳이 되었기에 가끔은 주변에 사람이 많지 않았던 과거의 호젓한 모습이 그립기도 합니다.",
                        englishSentence = "The park has changed a lot since I was a kid. In my younger days, the park was more like a playground for kids. It was sand-based, not with a grassy lawn, so my friends and I made sandcastles and shook the dirt off before going home. So, I got a lot of dirt on my body and clothes, and my mom didn't like it. Also, the park used to have a very smelly swamp instead of the clear lake that it has now. Nobody wanted to go near this swamp because the water was stagnant and smelled awful. Since it was standing water, there were lots of mosquitoes as well. Now, the park is the perfect place for both children and adults to enjoy nature. The playground is rubber-based so there is nothing clinging to the bodies. People can feed the fish and ducks that live in the lake and observe how they live. Because the park has changed up, the visitors to the park have also changed. In the past, there were not many people who bothered to go to the park. More and more people are now going to the park to exercise or sunbathe. It has become rather crowded, and sometimes I miss the way it used to be when there weren't as many people around."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "People set up appointments for various reasons. What sort of appointment do you usually make, social or otherwise? Who do you usually meet with? Where do you usually meet them? Give me as many details as possible.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "제가 사람들을 사교적으로 만나기 위해 약속을 잡을 때는 / 주로 학창 시절을 함께 보낸 친구들이나 이전 직장의 전 동료들과 만납니다. // 그들이 바로 제가 가장 자주 교류하고 어울리는 사람들입니다. // 저는 일주일에 한두 번 정도 외출을 합니다. // 제 전형적인 루틴은 금요일이나 토요일 밤에 나가는 것입니다. // 저는 주로 편안하고 차분한 분위기를 가진 식당에서 만남을 시작합니다. // 도시에는 맛있는 음식점들이 꽤 많기 때문에 / 제 친구들과 저는 여러 곳을 골고루 방문하며 새로운 식당들을 시도해 봅니다. // 우리는 간단히 식사를 하고, 가볍게 술 한잔을 곁들이며 서로의 근황에 대해 이야기를 나눕니다. // 한편 저는 필요에 따라 다른 종류의 개인적인 예약도 잡습니다. // 아프거나 정기 검진이 필요할 때는 병원 예약과 치과 예약을 합니다. // 파마나 염색처럼 새로운 헤어스타일이 필요할 때는 미용사 선생님과 예약을 잡습니다. // 비록 많은 곳들이 예약 없이 방문하는 워크인 서비스를 제공하지만 / 저는 긴 대기 시간을 피하기 위해 사전에 미리 예약하는 것을 훨씬 선호합니다.",
                        englishSentence = "When I make an appointment to meet others socially, it's either with my friends who I went to school together with or with some colleagues from my previous work. Those are the people I socialize with most often. I go out a couple of times a week. My typical routine involves going out on Friday and Saturday nights. I usually start out at a restaurant which has a chilled-out atmosphere. There are quite a few good places to eat in the city, so my friends and I try to mix them up and give new places a try. We grab a bite, have a few drinks, and catch up with one another. On the other hand, I make different kinds of appointments depending on what I need. I make doctor's appointments and dentist's appointments when I am sick or need a checkup. When I need to get a new hairstyle such as a hair perm or hair dye, I make appointments with my hairdresser. Although many places offer walk-in services, I prefer making appointments ahead of time to avoid a long wait."
                    )
                )
            ),
            QuestionTemplate(
                opicQuestion = "Tell me about the way people made an appointment in the past. How was it different from how people do now? Tell me about the details as much as possible.",
                category = PracticeCategory.LOUNGE_REVIEW,
                variations = listOf(
                    AnswerVariation(
                        koreanHint = "음, 제 생각에 예약을 잡는 것은 과거에 비해 훨씬 더 편리하고 쉬워졌습니다. // 아시다시피 엄청난 눈부신 발전과 성장을 거쳤습니다. // 생각해 보면 그 당시에는 사람들이 예약을 하기 위해 직접 매장이나 장소를 방문해야만 했습니다. // 그리고 사람들이 약속을 잡기 위해 일일이 유선 전화를 걸어야만 했던 시절도 있었습니다. // 하지만 지금은 모바일 앱을 사용하여 아주 간단하게 예약할 수 있습니다. // 스마트폰 모바일 앱 덕분에 사람들은 어디에 있든 손쉽게 예약을 진행할 수 있습니다. // 선택할 수 있는 다양한 종류의 전문 앱들이 많이 출시되어 있으며 / 사람들은 이동 중에도 언제 어디서나 간편하게 예약할 수 있습니다. // 따라서 이는 사람들에게 엄청난 시간과 번거로움을 덜어줍니다. // 이 예약 앱들은 요즘 대세이며 모든 사람들이 푹 빠져 있습니다. // 전반적으로 온라인으로 예약을 잡는 방식은 수년에 걸쳐 훌륭하고 품격 있는 업그레이드를 이루어냈습니다.",
                        englishSentence = "Well, I think making appointments has become way easier than in the past. It has gone through a major glow-up, you know. Think about it, people had to visit physical places to make an appointment back then. And there was a time when people had to make a phone call to make an appointment. Now, people can simply do that by using mobile apps. Thanks to those mobile apps, people can make appointments wherever they are. There are a few different types of apps to choose from, and people can even make appointments when they are on the move. So, it saves people tons of time and hassle. These apps are the real deal now; everyone is hooked. Overall, making appointments online has undergone some decent upgrades over the years."
                    )
                )
            )
        )
    )

    fun getQuestions(
        category: PracticeCategory,
        count: Int,
        excludeQuestions: Set<String> = emptySet()
    ): List<PracticeQuestion> {
        val categoryTemplates = bank[category] ?: emptyList()
        val normalizedExclusions = excludeQuestions.map { normalize(it) }.toSet()

        val available = categoryTemplates.filter {
            normalize(it.opicQuestion) !in normalizedExclusions
        }.shuffled()

        val selectedTemplates = if (available.size >= count) {
            available.take(count)
        } else {
            val fallback = categoryTemplates.shuffled()
            val result = LinkedHashSet<QuestionTemplate>(available)
            for (t in fallback) {
                if (result.size >= count) break
                result.add(t)
            }
            result.take(count)
        }

        return selectedTemplates.map { template ->
            val variation = template.variations.random()
            PracticeQuestion(
                opicQuestion = template.opicQuestion,
                koreanHint = variation.koreanHint,
                englishSentence = variation.englishSentence,
                category = template.category
            )
        }
    }

    private fun normalize(question: String): String =
        question.lowercase().replace(Regex("\\s+"), " ").trim()
}
