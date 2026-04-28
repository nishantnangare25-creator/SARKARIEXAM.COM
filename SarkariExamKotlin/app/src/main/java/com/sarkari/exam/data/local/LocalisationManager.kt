package com.sarkari.exam.data.local

enum class Language(val code: String, val displayName: String, val aiInstruction: String) {
    ENGLISH("en", "English", "Respond in English."),
    HINDI("hi", "हिन्दी", "कृपया हिन्दी में उत्तर दें।"),
    MARATHI("mr", "मराठी", "कृपया मराठीत उत्तर द्या."),
    BENGALI("bn", "বাংলা", "দয়া করে বাংলায় উত্তর দিন।"),
    TELUGU("te", "తెలుగు", "దయచేసి తెలుగులో సమాధానం ఇవ్వండి."),
    TAMIL("ta", "தமிழ்", "தயவுசெய்து தமிழில் பதிலளிக்கவும்."),
    GUJARATI("gu", "ગુજરાતી", "કૃપા કરીને ગુજરાતીમાં જવાબ આપો."),
    KANNADA("kn", "ಕನ್ನಡ", "ದಯವಿಟ್ಟು ಕನ್ನಡದಲ್ಲಿ ಉತ್ತರಿಸಿ."),
    MALAYALAM("ml", "മലയാളം", "ദയവായി മലയാളത്തിൽ മറുപടി നൽകുക."),
    PUNJABI("pa", "ਪੰਜਾਬੀ", "ਕਿਰਪਾ করে ਪੰਜਾਬੀ ਵਿੱਚ ਉੱਤਰ ਦਿਓ।"),
    ODIA("or", "ଓଡ଼ିଆ", "ଦୟାକରି ଓଡ଼ିଆରେ ଉତ୍ତର ଦିଅନ୍ତୁ |"),
    ASSAMESE("as", "অসমীয়া", "অনুগ্ৰহ কৰি অসমীয়াত উত্তৰ দিব।")
}

enum class IndianState(val code: String, val displayName: String) {
    AP("AP", "Andhra Pradesh"),
    AR("AR", "Arunachal Pradesh"),
    AS("AS", "Assam"),
    BR("BR", "Bihar"),
    CG("CG", "Chhattisgarh"),
    GA("GA", "Goa"),
    GJ("GJ", "Gujarat"),
    HR("HR", "Haryana"),
    HP("HP", "Himachal Pradesh"),
    JH("JH", "Jharkhand"),
    KA("KA", "Karnataka"),
    KL("KL", "Kerala"),
    MP("MP", "Madhya Pradesh"),
    MH("MH", "Maharashtra"),
    MN("MN", "Manipur"),
    ML("ML", "Meghalaya"),
    MZ("MZ", "Mizoram"),
    NL("NL", "Nagaland"),
    OR("OR", "Odisha"),
    PB("PB", "Punjab"),
    RJ("RJ", "Rajasthan"),
    SK("SK", "Sikkim"),
    TN("TN", "Tamil Nadu"),
    TG("TG", "Telangana"),
    TR("TR", "Tripura"),
    UP("UP", "Uttar Pradesh"),
    UK("UK", "Uttarakhand"),
    WB("WB", "West Bengal"),
    AN("AN", "Andaman and Nicobar Islands"),
    CH("CH", "Chandigarh"),
    DN("DN", "Dadra and Nagar Haveli and Daman and Diu"),
    DL("DL", "Delhi"),
    JK("JK", "Jammu and Kashmir"),
    LA("LA", "Ladakh"),
    LD("LD", "Lakshadweep"),
    PY("PY", "Puducherry")
}

data class MockTestStrings(
    val title: String,
    val setupTitle: String,
    val setupSubtitle: String,
    val targetExam: String,
    val targetExamHint: String,
    val subject: String,
    val subjectHint: String,
    val startTest: String,
    val questionCount: String,
    val previous: String,
    val next: String,
    val finish: String,
    val resultTitle: String,
    val excellent: String,
    val good: String,
    val tryAgain: String,
    val retake: String,
    val review: String,
    val scoreText: String,
    val aiPrompt: String
)

data class OnboardingStrings(
    val step1Title: String,
    val step1Subtitle: String,
    val step2Title: String,
    val step2Subtitle: String,
    val step3Title: String,
    val step3Subtitle: String,
    val step4Title: String,
    val step4Subtitle: String,
    val continueBtn: String,
    val getStartedBtn: String,
    val hoursDay: String,
    val stateTitle: String,
    val stateSubtitle: String
)

data class SplashStrings(
    val slogan: String
)

data class NotesStrings(
    val title: String,
    val introTitle: String,
    val introSubtitle: String,
    val topics: String,
    val topicsHint: String,
    val generateBtn: String,
    val draftTitle: String,
    val aiPrompt: String
)

data class PlannerStrings(
    val title: String,
    val introTitle: String,
    val introSubtitle: String,
    val hours: String,
    val hoursHint: String,
    val level: String,
    val levelHint: String,
    val generateBtn: String,
    val resultTitle: String,
    val aiPrompt: String
)

data class AnalyticsStrings(
    val title: String,
    val statsTitle: String,
    val statsSubtitle: String,
    val accuracy: String,
    val consistency: String,
    val completion: String,
    val breakdown: String,
    val aiTitle: String,
    val aiSubtitle: String,
    val analyzeBtn: String,
    val aiInsights: String,
    val aiPrompt: String
)

data class CurrentAffairsStrings(
    val title: String,
    val refresh: String,
    val staticHintTitle: String,
    val staticHintSubtitle: String,
    val share: String,
    val source: String,
    val aiPrompt: String,
    val loading: String
)

data class PyqStrings(
    val title: String,
    val searchHint: String,
    val start: String
)

data class AppStrings(
    val common: CommonStrings,
    val dashboard: DashboardStrings,
    val tutor: TutorStrings,
    val mockTest: MockTestStrings,
    val onboarding: OnboardingStrings,
    val splash: SplashStrings,
    val notes: NotesStrings,
    val planner: PlannerStrings,
    val analytics: AnalyticsStrings,
    val pyq: PyqStrings,
    val currentAffairs: CurrentAffairsStrings,
    val settings: SettingsStrings
)

data class CommonStrings(
    val back: String,
    val save: String,
    val logout: String,
    val loading: String,
    val error: String,
    val overview: String
)

data class DashboardStrings(
    val welcome: String,
    val subtitle: String,
    val startTutor: String,
    val mockTest: String,
    val mockTestDesc: String,
    val pyqLibrary: String,
    val pyqLibraryDesc: String,
    val aiNotes: String,
    val aiNotesDesc: String,
    val riyaAi: String,
    val riyaAiDesc: String,
    val currentAffairs: String,
    val currentAffairsDesc: String,
    val performance: String,
    val goals: String,
    val banner1Title: String,
    val banner1Desc: String,
    val banner2Title: String,
    val banner2Desc: String
)

data class TutorStrings(
    val title: String,
    val placeholder: String,
    val thinking: String,
    val intro: String,
    val online: String,
    val chatCleared: String,
    val headerBadge: String
)

data class SettingsStrings(
    val title: String,
    val language: String,
    val selectLanguage: String,
    val apiKey: String,
    val stateLabel: String
)

object LocalisationManager {
    
    fun getStrings(languageCode: String): AppStrings {
        return when (languageCode) {
            "hi" -> hindiStrings
            "mr" -> marathiStrings
            "bn" -> bengaliStrings
            "te" -> teluguStrings
            "ta" -> tamilStrings
            else -> englishStrings
        }
    }

    private val englishStrings = AppStrings(
        common = CommonStrings(
            back = "Back",
            save = "Save",
            logout = "Logout",
            loading = "Loading...",
            error = "An error occurred",
            overview = "OVERVIEW"
        ),
        dashboard = DashboardStrings(
            welcome = "Welcome, Aspirant!",
            subtitle = "Your AI Exam Coach is ready.",
            startTutor = "Talk to Riya AI",
            mockTest = "Mock Tests",
            mockTestDesc = "AI-Powered Simulation",
            pyqLibrary = "PYQ Library",
            pyqLibraryDesc = "Official Archives",
            aiNotes = "AI Notes",
            aiNotesDesc = "Instant Study Material",
            riyaAi = "Riya AI",
            riyaAiDesc = "24/7 Smart Tutor",
            currentAffairs = "Daily Current Affairs",
            currentAffairsDesc = "Stay updated with latest exam news",
            performance = "Performance Analytics",
            goals = "Goals & Exams",
            banner1Title = "Master Exams with AI",
            banner1Desc = "Join 50k+ students and level up.",
            banner2Title = "Daily Free Mock Tests",
            banner2Desc = "Track your progress daily."
        ),
        mockTest = MockTestStrings(
            title = "Mock Test",
            setupTitle = "AI Mock Test",
            setupSubtitle = "Generate a personalized test in seconds",
            targetExam = "Target Exam",
            targetExamHint = "e.g. UPSC, SSC",
            subject = "Subject",
            subjectHint = "e.g. History, Polity",
            startTest = "Start Mock Test",
            questionCount = "Question",
            previous = "Previous",
            next = "Next",
            finish = "Finish",
            resultTitle = "Quiz Result",
            excellent = "Excellent Job!",
            good = "Good Effort!",
            tryAgain = "Keep Practicing!",
            retake = "Retake Test",
            review = "Review Details",
            scoreText = "You scored %d out of %d",
            aiPrompt = "Generate 5 MCQ questions for %s exam, subject %s. Format: Q: Question A) Opt B) Opt C) Opt D) Opt Answer: Letter Explanation: Text. IMPORTANT: Please generate the entire content in English."
        ),
        onboarding = OnboardingStrings(
            step1Title = "Choose your focus",
            step1Subtitle = "Which government exam are you preparing for?",
            step2Title = "Language Selection",
            step2Subtitle = "Select the language for your AI study assistant.",
            step3Title = "Dedication Level",
            step3Subtitle = "How many hours can you dedicate daily?",
            step4Title = "You're all set!",
            step4Subtitle = "Your personalized AI dashboard is ready to boost your preparation.",
            continueBtn = "Continue",
            getStartedBtn = "Get Started",
            hoursDay = "%d hours per day",
            stateTitle = "Select Your State",
            stateSubtitle = "Helping us tailor exams and news for you"
        ),
        splash = SplashStrings(
            slogan = "AI-Powered Success"
        ),
        notes = NotesStrings(
            title = "AI Notes Generator",
            introTitle = "Instant Study Material",
            introSubtitle = "AI-powered notes for any topic",
            topics = "Specific Topics (Optional)",
            topicsHint = "e.g. Mughal Empire, Fundamental Rights",
            generateBtn = "Generate AI Notes",
            draftTitle = "Draft Notes",
            aiPrompt = "Generate detailed study notes for %s exam, subject %s, focusing on topics: %s. Format the output with clear headings and bullet points. IMPORTANT: Please generate the entire content in English."
        ),
        planner = PlannerStrings(
            title = "AI Study Planner",
            introTitle = "Personalized Roadmap",
            introSubtitle = "AI-generated schedule just for you",
            hours = "Hours per Day",
            hoursHint = "e.g. 4 hrs, 6 hrs",
            level = "Current Level",
            levelHint = "e.g. Beginner, Intermediate",
            generateBtn = "Generate Master Plan",
            resultTitle = "Your AI Study Roadmap",
            aiPrompt = "Create a detailed study plan for %s exam preparation. Study hours: %s. Level: %s. Provide a weekly breakdown. IMPORTANT: Please generate the entire content in English."
        ),
        analytics = AnalyticsStrings(
            title = "Performance Analytics",
            statsTitle = "Exam Statistics",
            statsSubtitle = "Detailed breakdown of your progress",
            accuracy = "Accuracy",
            consistency = "Consistency",
            completion = "Completion",
            breakdown = "Subject Breakdown",
            aiTitle = "AI Deep Dive Analysis",
            aiSubtitle = "Get personalized insights and improvement tips based on your mock test patterns.",
            analyzeBtn = "Analyze My Performance",
            aiInsights = "AI Insights",
            aiPrompt = "Analyze my exam performance. Accuracy: %d%%, Consistency: %d%%. Strongest: Current Affairs. Weakest: Economy. Provide Tips. IMPORTANT: Please generate the entire content in English."
        ),
        pyq = PyqStrings(
            title = "PYQ Library",
            searchHint = "Search papers...",
            start = "Start"
        ),
        currentAffairs = CurrentAffairsStrings(
            title = "Current Affairs",
            refresh = "Refresh",
            staticHintTitle = "Daily Static Fact",
            staticHintSubtitle = "The Indian Constitution is the longest written constitution of any sovereign country.",
            share = "Share",
            source = "Source: Sarkari Exam AI",
            aiPrompt = "Generate 3 important current affairs news items for today. Format: Title | Date | Category | Content. Also provide a unique daily static fact.",
            loading = "Updating News..."
        ),
        tutor = TutorStrings(
            title = "Riya AI Tutor",
            placeholder = "Ask Riya anything...",
            thinking = "Riya is thinking...",
            intro = "Hello! I am Riya, your AI Tutor. How can I help you today?",
            online = "Online",
            chatCleared = "Chat cleared! How can I help?",
            headerBadge = "EN"
        ),
        settings = SettingsStrings(
            title = "Settings",
            language = "Language",
            selectLanguage = "Select App Language",
            apiKey = "Custom API Key (BYOK)",
            stateLabel = "Your State"
        )
    )

    private val hindiStrings = AppStrings(
        common = CommonStrings(
            back = "पीछे",
            save = "सहेजें",
            logout = "लॉगआउट",
            loading = "लोड हो रहा है...",
            error = "एक त्रुटि हुई",
            overview = "अवलोकन"
        ),
        dashboard = DashboardStrings(
            welcome = "स्वागत है, अभ्यर्थी!",
            subtitle = "आपका AI एग्जाम कोच तैयार है।",
            startTutor = "रिया AI से बात करें",
            mockTest = "मॉक टेस्ट",
            mockTestDesc = "AI-संचालित सिमुलेशन",
            pyqLibrary = "PYQ लाइब्रेरी",
            pyqLibraryDesc = "आधिकारिक अभिलेखागार",
            aiNotes = "AI नोट्स",
            aiNotesDesc = "त्वरित अध्ययन सामग्री",
            riyaAi = "रिया AI",
            riyaAiDesc = "24/7 स्मार्ट ट्यूटर",
            currentAffairs = "दैनिक समसामयिकी",
            currentAffairsDesc = "नवीनतम परीक्षा समाचारों के साथ अपडेट रहें",
            performance = "प्रदर्शन विश्लेषण",
            goals = "लक्ष्य और परीक्षा",
            banner1Title = "AI के साथ परीक्षा में महारत हासिल करें",
            banner1Desc = "50k+ छात्रों से जुड़ें और आगे बढ़ें।",
            banner2Title = "दैनिक मुफ्त मॉक टेस्ट",
            banner2Desc = "अपनी प्रगति को रोजाना ट्रैक करें।"
        ),
        mockTest = MockTestStrings(
            title = "मॉक टेस्ट",
            setupTitle = "AI मॉक टेस्ट",
            setupSubtitle = "सेकंडों में अपना व्यक्तिगत टेस्ट जेनरेट करें",
            targetExam = "लक्ष्य परीक्षा",
            targetExamHint = "जैसे UPSC, SSC",
            subject = "विषय",
            subjectHint = "जैसे इतिहास, राजनीति",
            startTest = "मॉक टेस्ट शुरू करें",
            questionCount = "प्रश्न",
            previous = "पिछला",
            next = "अगला",
            finish = "समाप्त करें",
            resultTitle = "क्विज़ रिजल्ट",
            excellent = "बेहतरीन काम!",
            good = "अच्छा प्रयास!",
            tryAgain = "अभ्यास जारी रखें!",
            retake = "टेस्ट दोबारा दें",
            review = "विवरण की समीक्षा करें",
            scoreText = "आपने %d में से %d स्कोर किया",
            aiPrompt = "Generate 5 MCQ questions for %s exam, subject %s. Format: Q: Question A) Opt B) Opt C) Opt D) Opt Answer: Letter Explanation: Text. IMPORTANT: Please generate the entire content in Hindi. (Note: Question, Options, and Explanation should be in Hindi script)."
        ),
        onboarding = OnboardingStrings(
            step1Title = "अपना लक्ष्य चुनें",
            step1Subtitle = "आप किस सरकारी परीक्षा की तैयारी कर रहे हैं?",
            step2Title = "भाषा का चयन",
            step2Subtitle = "अपने AI अध्ययन सहायक के लिए भाषा चुनें।",
            step3Title = "समर्पण स्तर",
            step3Subtitle = "आप प्रतिदिन कितने घंटे समर्पित कर सकते हैं?",
            step4Title = "सब तैयार है!",
            step4Subtitle = "आपकी तैयारी को बढ़ावा देने के लिए आपका व्यक्तिगत AI डैशबोर्ड तैयार है।",
            continueBtn = "जारी रखें",
            getStartedBtn = "शुरू करें",
            hoursDay = "%d घंटे प्रति दिन",
            stateTitle = "अपना राज्य चुनें",
            stateSubtitle = "यह हमें आपके क्षेत्र के अनुसार परीक्षा विवरण देने में मदद करेगा"
        ),
        splash = SplashStrings(
            slogan = "AI-संचालित सफलता"
        ),
        notes = NotesStrings(
            title = "AI नोट्स जेनरेटर",
            introTitle = "त्वरित अध्ययन सामग्री",
            introSubtitle = "किसी भी विषय के लिए AI-संचालित नोट्स",
            topics = "विशिष्ट विषय (वैकल्पिक)",
            topicsHint = "जैसे मुगल साम्राज्य, मौलिक अधिकार",
            generateBtn = "AI नोट्स जेनरेट करें",
            draftTitle = "ड्राफ्ट नोट्स",
            aiPrompt = "Generate detailed study notes for %s exam, subject %s, focusing on topics: %s. Format the output with clear headings and bullet points. IMPORTANT: Please generate the entire content in Hindi. (Note: Use Hindi script)."
        ),
        planner = PlannerStrings(
            title = "AI स्टडी प्लानर",
            introTitle = "व्यक्तिगत रोडमैप",
            introSubtitle = "सिर्फ आपके लिए AI-जनरेटेड शेड्यूल",
            hours = "प्रति दिन घंटे",
            hoursHint = "जैसे 4 घंटे, 6 घंटे",
            level = "वर्तमान स्तर",
            levelHint = "जैसे शुरुआती, इंटरमीडिएट",
            generateBtn = "मास्टर प्लान जेनरेट करें",
            resultTitle = "आपका AI स्टडी रोडमैप",
            aiPrompt = "Create a detailed study plan for %s exam preparation. Study hours: %s. Level: %s. Provide a weekly breakdown. IMPORTANT: Please generate the entire content in Hindi. (Note: Use Hindi script)."
        ),
        analytics = AnalyticsStrings(
            title = "प्रदर्शन विश्लेषण",
            statsTitle = "परीक्षा सांख्यिकी",
            statsSubtitle = "आपकी प्रगति का विस्तृत विवरण",
            accuracy = "शुद्धता",
            consistency = "निरंतरता",
            completion = "पूर्णता",
            breakdown = "विषयवार विश्लेषण",
            aiTitle = "AI विस्तृत विश्लेषण",
            aiSubtitle = "अपने मॉक टेस्ट पैटर्न के आधार पर व्यक्तिगत जानकारी और सुधार के टिप्स प्राप्त करें।",
            analyzeBtn = "मेरे प्रदर्शन का विश्लेषण करें",
            aiInsights = "AI अंतर्दृष्टि",
            aiPrompt = "Analyze my exam performance. Accuracy: %d%%, Consistency: %d%%. Strongest: Current Affairs. Weakest: Economy. Provide Tips. IMPORTANT: Please generate the entire content in Hindi. (Note: Use Hindi script)."
        ),
        pyq = PyqStrings(
            title = "PYQ लाइब्रेरी",
            searchHint = "पेपर खोजें...",
            start = "शुरू करें"
        ),
        currentAffairs = CurrentAffairsStrings(
            title = "सामयिकी (Current Affairs)",
            refresh = "रिफ्रेश करें",
            staticHintTitle = "दैनिक स्थिर तथ्य",
            staticHintSubtitle = "भारतीय संविधान किसी भी संप्रभु देश का सबसे लंबा लिखित संविधान है।",
            share = "शेयर करें",
            source = "स्रोत: सरकारी एग्जाम एआई",
            aiPrompt = "आज के लिए 3 महत्वपूर्ण करंट अफेयर्स समाचार आइटम तैयार करें। प्रारूप: शीर्षक | दिनांक | श्रेणी | सामग्री। एक अद्वितीय दैनिक स्थिर तथ्य भी प्रदान करें।",
            loading = "समाचार अपडेट हो रहा है..."
        ),
        tutor = TutorStrings(
            title = "रिया AI ट्यूटर",
            placeholder = "रिया से कुछ भी पूछें...",
            thinking = "रिया सोच रही है...",
            intro = "नमस्ते! मैं रिया हूँ, आपकी AI ट्यूटर। आज मैं आपकी कैसे मदद कर सकती हूँ?",
            online = "ऑनलाइन",
            chatCleared = "चैट साफ़ हो गई! मैं कैसे मदद कर सकती हूँ?",
            headerBadge = "HI"
        ),
        settings = SettingsStrings(
            title = "सेटिंग्स",
            language = "भाषा",
            selectLanguage = "ऐप की भाषा चुनें",
            apiKey = "कस्टम API कुंजी (BYOK)",
            stateLabel = "आपका राज्य"
        )
    )

    private val marathiStrings = AppStrings(
        common = CommonStrings(
            back = "मागे",
            save = "साठवा",
            logout = "लॉगआउट",
            loading = "लोड होत आहे...",
            error = "एक त्रुटी आली",
            overview = "आढावा"
        ),
        dashboard = DashboardStrings(
            welcome = "स्वागत आहे, उमेदवार!",
            subtitle = "तुमचा AI एक्झाम कोच तयार आहे.",
            startTutor = "रिया AI शी बोला",
            mockTest = "मॉक टेस्ट",
            mockTestDesc = "AI-आधारित सिम्युलेशन",
            pyqLibrary = "PYQ लायब्ररी",
            pyqLibraryDesc = "अधिकृत संग्रहण",
            aiNotes = "AI नोट्स",
            aiNotesDesc = "झटपट अभ्यास साहित्य",
            riyaAi = "रिया AI",
            riyaAiDesc = "24/7 स्मार्ट ट्यूटर",
            currentAffairs = "दैनिक चालू घडामोडी",
            currentAffairsDesc = "नवीनतम परीक्षा बातम्यांसह अपडेट राहा",
            performance = "कामगिरी विश्लेषण",
            goals = "ध्येय आणि परीक्षा",
            banner1Title = "AI सह परीक्षेत प्रभुत्व मिळवा",
            banner1Desc = "50k+ विद्यार्थ्यांसह सामील व्हा आणि प्रगती करा.",
            banner2Title = "दैनिक मोफत मॉक टेस्ट",
            banner2Desc = "तुमच्या प्रगतीचा दररोज मागोवा घ्या."
        ),
        mockTest = MockTestStrings(
            title = "मॉक टेस्ट",
            setupTitle = "AI मॉक टेस्ट",
            setupSubtitle = "काही सेकंदात आपला वैयक्तिकृत टेस्ट तयार करा",
            targetExam = "लक्ष्य परीक्षा",
            targetExamHint = "उदा. UPSC, SSC",
            subject = "विषय",
            subjectHint = "उदा. इतिहास, राज्यशास्त्र",
            startTest = "मॉक टेस्ट सुरू करा",
            questionCount = "प्रश्न",
            previous = "मागे",
            next = "पुढील",
            finish = "पूर्ण करा",
            resultTitle = "क्विझ निकाल",
            excellent = "उत्कृष्ट कामगिरी!",
            good = "चांगला प्रयत्न!",
            tryAgain = "सराव सुरू ठेवा!",
            retake = "टेस्ट पुन्हा द्या",
            review = "तपशील पहा",
            scoreText = "तुम्ही %d पैकी %d गुण मिळवले",
            aiPrompt = "Generate 5 MCQ questions for %s exam, subject %s. Format: Q: Question A) Opt B) Opt C) Opt D) Opt Answer: Letter Explanation: Text. IMPORTANT: Please generate the entire content in Marathi. (Note: Question, Options, and Explanation should be in Marathi script)."
        ),
        onboarding = OnboardingStrings(
            step1Title = "तुमचे ध्येय निवडा",
            step1Subtitle = "तुम्ही कोणत्या सरकारी परीक्षेची तयारी करत आहात?",
            step2Title = "भाषा निवड",
            step2Subtitle = "तुमच्या AI अभ्यास सहाय्यकासाठी भाषा निवडा.",
            step3Title = "समर्पण पातळी",
            step3Subtitle = "तुम्ही दररोज किती तास देऊ शकता?",
            step4Title = "तुमची तयारी पूर्ण झाली आहे!",
            step4Subtitle = "तुमची तयारी वाढवण्यासाठी तुमचा वैयक्तिकृत AI डॅशबोर्ड तयार आहे.",
            continueBtn = "सुरू ठेवा",
            getStartedBtn = "सुरू करा",
            hoursDay = "दररोज %d तास",
            stateTitle = "तुमचे राज्य निवडा",
            stateSubtitle = "आम्हाला तुमच्या क्षेत्रातील परीक्षांची माहिती देण्यास मदत होईल"
        ),
        splash = SplashStrings(
            slogan = "AI-आधारित यश"
        ),
        notes = NotesStrings(
            title = "AI नोट्स जनरेटर",
            introTitle = "झटपट अभ्यास साहित्य",
            introSubtitle = "कोणत्याही विषयासाठी AI-आधारित नोट्स",
            topics = "विशिष्ट विषय (पर्यायी)",
            topicsHint = "उदा. मुघल साम्राज्य, मूलभूत हक्क",
            generateBtn = "AI नोट्स तयार करा",
            draftTitle = "ड्राफ्ट नोट्स",
            aiPrompt = "Generate detailed study notes for %s exam, subject %s, focusing on topics: %s. Format the output with clear headings and bullet points. IMPORTANT: Please generate the entire content in Marathi. (Note: Use Marathi script)."
        ),
        planner = PlannerStrings(
            title = "AI स्टडी प्लॅनर",
            introTitle = "वैयक्तिकृत रोडमॅप",
            introSubtitle = "फक्त तुमच्यासाठी AI-जनरेटेड वेळापत्रक",
            hours = "दररोजचे तास",
            hoursHint = "उदा. ४ तास, ६ तास",
            level = "सध्याची पातळी",
            levelHint = "उदा. नवशिक्या, मध्यम",
            generateBtn = "मास्टर प्लॅन तयार करा",
            resultTitle = "तुमचा AI स्टडी रोडमॅप",
            aiPrompt = "Create a detailed study plan for %s exam preparation. Study hours: %s. Level: %s. Provide a weekly breakdown. IMPORTANT: Please generate the entire content in Marathi. (Note: Use Marathi script)."
        ),
        analytics = AnalyticsStrings(
            title = "कामगिरी विश्लेषण",
            statsTitle = "परीक्षा आकडेवारी",
            statsSubtitle = "तुमच्या प्रगतीचा तपशीलवार आढावा",
            accuracy = "अचूकता",
            consistency = "सातत्य",
            completion = "पूर्णता",
            breakdown = "विषयानुसार निकाल",
            aiTitle = "AI सविस्तर विश्लेषण",
            aiSubtitle = "तुमच्या मॉक टेस्ट पॅटर्नवर आधारित वैयक्तिक सल्ले आणि सुधारणेच्या टिप्स मिळवा.",
            analyzeBtn = "माझ्या कामगिरीचे विश्लेषण करा",
            aiInsights = "AI अंतर्दृष्टी",
            aiPrompt = "Analyze my exam performance. Accuracy: %d%%, Consistency: %d%%. Strongest: Current Affairs. Weakest: Economy. Provide Tips. IMPORTANT: Please generate the entire content in Marathi. (Note: Use Marathi script)."
        ),
        pyq = PyqStrings(
            title = "PYQ लायब्ररी",
            searchHint = "पेपर्स शोधा...",
            start = "सुरू करा"
        ),
        tutor = TutorStrings(
            title = "रिया AI ट्यूटर",
            placeholder = "रियाला काहीही विचारा...",
            thinking = "रिया विचार करत आहे...",
            intro = "नमस्कार! मी रिया आहे, तुमची AI ट्यूटर। आज मी तुम्हाला कशी मदत करू शकते?",
            online = "ऑनलाइन",
            chatCleared = "चॅट साफ झाली! मी कशी मदत करू शकते?",
            headerBadge = "MR"
        ),
        settings = SettingsStrings(
            title = "सेटिंग्ज",
            language = "भाषा",
            selectLanguage = "अॅपची भाषा निवडा",
            apiKey = "कस्टम API की (BYOK)",
            stateLabel = "तुमचे राज्य"
        ),
        currentAffairs = CurrentAffairsStrings(
            title = "Current Affairs",
            refresh = "Refresh",
            staticHintTitle = "Daily Static Fact",
            staticHintSubtitle = "The Indian Constitution is the longest written constitution of any sovereign country.",
            share = "Share",
            source = "Source: Sarkari Exam AI",
            aiPrompt = "Generate 3 important current affairs news items for today. Format: Title | Date | Category | Content. Also provide a unique daily static fact.",
            loading = "Updating News..."
        )
    )

    private val bengaliStrings = AppStrings(
        common = CommonStrings(
            back = "পেছনে",
            save = "সংরক্ষণ করুন",
            logout = "লগআউট",
            loading = "লোড হচ্ছে...",
            error = "একটি ত্রুটি ঘটেছে",
            overview = "ওভারভিউ"
        ),
        dashboard = DashboardStrings(
            welcome = "স্বাগতম, পরীক্ষার্থী!",
            subtitle = "আপনার AI পরীক্ষার কোচ প্রস্তুত।",
            startTutor = "Riya AI-এর সাথে কথা বলুন",
            mockTest = "মক টেস্ট",
            mockTestDesc = "AI-চালিত সিমুলেশন",
            pyqLibrary = "PYQ লাইব্রেরি",
            pyqLibraryDesc = "অফিসিয়াল আর্কাইভ",
            aiNotes = "AI নোটস",
            aiNotesDesc = "তাৎক্ষণিক অধ্যয়ন সামগ্রী",
            riyaAi = "Riya AI",
            riyaAiDesc = "২৪/৭ স্মার্ট টিউটর",
            currentAffairs = "দৈনিক কারেন্ট অ্যাফেয়ার্স",
            currentAffairsDesc = "সাম্প্রতিক পরীক্ষার খবরের সাথে আপডেট থাকুন",
            performance = "পারফরম্যান্স অ্যানালিটিক্স",
            goals = "লক্ষ্য ও পরীক্ষা",
            banner1Title = "AI-এর সাথে পরীক্ষায় দক্ষতা অর্জন করুন",
            banner1Desc = "৫০ হাজার+ শিক্ষার্থীর সাথে যোগ দিন এবং এগিয়ে যান।",
            banner2Title = "প্রতিদিন বিনামূল্যে মক টেস্ট",
            banner2Desc = "প্রতিদিন আপনার অগ্রগতি ট্র্যাক করুন।"
        ),
        mockTest = MockTestStrings(
            title = "মক টেস্ট",
            setupTitle = "AI মক টেস্ট",
            setupSubtitle = "সেকেন্ডের মধ্যে আপনার ব্যক্তিগতকৃত টেস্ট তৈরি করুন",
            targetExam = "লক্ষ্য পরীক্ষা",
            targetExamHint = "যেমন: UPSC, SSC",
            subject = "বিষয়",
            subjectHint = "যেমন: ইতিহাস, রাষ্ট্রবিজ্ঞান",
            startTest = "মক টেস্ট শুরু করুন",
            questionCount = "প্রশ্ন",
            previous = "আগের",
            next = "পরবর্তী",
            finish = "শেষ করুন",
            resultTitle = "কুইজ ফলাফল",
            excellent = "চমৎকার কাজ!",
            good = "ভালো প্রচেষ্টা!",
            tryAgain = "অনুশীলন চালিয়ে যান!",
            retake = "আবার টেস্ট দিন",
            review = "বিস্তারিত পর্যালোচনা করুন",
            scoreText = "আপনি %d এর মধ্যে %d স্কোর করেছেন",
            aiPrompt = "Generate %d MCQ questions for %s exam, subject %s. Format: Q: Question A) Opt B) Opt C) Opt D) Opt Answer: Letter Explanation: Text. IMPORTANT: Please generate the entire content in Bengali. (Note: Use Bengali script)."
        ),
        onboarding = OnboardingStrings(
            step1Title = "আপনার লক্ষ্য চয়ন করুন",
            step1Subtitle = "আপনি কোন সরকারি পরীক্ষার জন্য প্রস্তুতি নিচ্ছেন?",
            step2Title = "ভাষা নির্বাচন",
            step2Subtitle = "আপনার AI অধ্যয়ন সহকারীর জন্য ভাষা নির্বাচন করুন।",
            step3Title = "উত্সর্গ স্তর",
            step3Subtitle = "আপনি প্রতিদিন কত ঘন্টা সময় দিতে পারেন?",
            step4Title = "সব প্রস্তুত!",
            step4Subtitle = "আপনার ব্যক্তিগতকৃত AI ড্যাশবোর্ড আপনার প্রস্তুতি বাড়াতে প্রস্তুত।",
            continueBtn = "চালিয়ে যান",
            getStartedBtn = "শুরু করুন",
            hoursDay = "প্রতিদিন %d ঘন্টা",
            stateTitle = "আপনার রাজ্য নির্বাচন করুন",
            stateSubtitle = "আমাদের আপনার জন্য উপযুক্ত পরীক্ষা এবং খবর সরবরাহ করতে সহায়তা করবে"
        ),
        splash = SplashStrings(
            slogan = "AI-চালিত সাফল্য"
        ),
        notes = NotesStrings(
            title = "AI নোটস জেনারেটর",
            introTitle = "তাৎক্ষণিক অধ্যয়ন সামগ্রী",
            introSubtitle = "যেকোনো বিষয়ের জন্য AI-চালিত নোটস",
            topics = "নির্দিষ্ট বিষয় (ঐচ্ছিক)",
            topicsHint = "যেমন: মুঘল সাম্রাজ্য, মৌলিক অধিকার",
            generateBtn = "AI নোটস তৈরি করুন",
            draftTitle = "খসড়া নোটস",
            aiPrompt = "Generate detailed study notes for %s exam, subject %s, focusing on topics: %s. Format the output with clear headings and bullet points. IMPORTANT: Please generate the entire content in Bengali. (Note: Use Bengali script)."
        ),
        planner = PlannerStrings(
            title = "AI স্টাডি প্ল্যানার",
            introTitle = "ব্যক্তিগত রোডম্যাপ",
            introSubtitle = "শুধুমাত্র আপনার জন্য AI-জেনারেটেড সময়সূচী",
            hours = "প্রতিদিন ঘন্টা",
            hoursHint = "যেমন: ৪ ঘন্টা, ৬ ঘন্টা",
            level = "বর্তমান স্তর",
            levelHint = "যেমন: নবীন, মধ্যবর্তী",
            generateBtn = "মাস্টার প্ল্যান তৈরি করুন",
            resultTitle = "আপনার AI স্টাডি রোডম্যাপ",
            aiPrompt = "Create a detailed study plan for %s exam preparation. Study hours: %s. Level: %s. Provide a weekly breakdown. IMPORTANT: Please generate the entire content in Bengali. (Note: Use Bengali script)."
        ),
        analytics = AnalyticsStrings(
            title = "পারফরম্যান্স অ্যানালিটিক্স",
            statsTitle = "পরীক্ষার পরিসংখ্যান",
            statsSubtitle = "আপনার অগ্রগতির বিস্তারিত বিবরণ",
            accuracy = "সঠিকতা",
            consistency = "সঙ্গতি",
            completion = "সমাপ্তি",
            breakdown = "বিষয়ভিত্তিক ব্রেকডাউন",
            aiTitle = "AI গভীর বিশ্লেষণ",
            aiSubtitle = "আপনার মক টেস্ট প্যাটার্নের ভিত্তিতে ব্যক্তিগতকৃত ইনসাইট এবং উন্নতির টিপস পান।",
            analyzeBtn = "আমার পারফরম্যান্স বিশ্লেষণ করুন",
            aiInsights = "AI ইনসাইট",
            aiPrompt = "Analyze my exam performance. Accuracy: %d%%, Consistency: %d%%. Strongest: Current Affairs. Weakest: Economy. Provide Tips. IMPORTANT: Please generate the entire content in Bengali. (Note: Use Bengali script)."
        ),
        pyq = PyqStrings(
            title = "PYQ লাইব্রেরি",
            searchHint = "পেপার খুঁজুন...",
            start = "শুরু করুন"
        ),
        tutor = TutorStrings(
            title = "রিয়া AI টিউটর",
            placeholder = "রিয়াকে যেকোনো কিছু জিজ্ঞাসা করুন...",
            thinking = "রিয়া চিন্তা করছে...",
            intro = "হ্যালো! আমি রিয়া, আপনার AI টিউটর। আজ আমি আপনাকে কীভাবে সাহায্য করতে পারি?",
            online = "অনলাইন",
            chatCleared = "চ্যাট পরিষ্কার করা হয়েছে! আমি কীভাবে সাহায্য করতে পারি?",
            headerBadge = "BN"
        ),
        settings = SettingsStrings(
            title = "সেটিংস",
            language = "ভাষা",
            selectLanguage = "অ্যাপের ভাষা নির্বাচন করুন",
            apiKey = "কাস্টম API কী (BYOK)",
            stateLabel = "আপনার রাজ্য"
        ),
        currentAffairs = CurrentAffairsStrings(
            title = "Current Affairs",
            refresh = "Refresh",
            staticHintTitle = "Daily Static Fact",
            staticHintSubtitle = "The Indian Constitution is the longest written constitution of any sovereign country.",
            share = "Share",
            source = "Source: Sarkari Exam AI",
            aiPrompt = "Generate 3 important current affairs news items for today. Format: Title | Date | Category | Content. Also provide a unique daily static fact.",
            loading = "Updating News..."
        )
    )

    private val teluguStrings = AppStrings(
        common = CommonStrings(
            back = "వెనుకకు",
            save = "సేవ్ చేయండి",
            logout = "లాగౌట్",
            loading = "లోడ్ అవుతోంది...",
            error = "లోపం సంభవించింది",
            overview = "అవలోకనం"
        ),
        dashboard = DashboardStrings(
            welcome = "స్వాగతం, అభ్యర్థి!",
            subtitle = "మీ AI ఎగ్జామ్ కోచ్ సిద్ధంగా ఉంది.",
            startTutor = "రియా AIతో మాట్లాడండి",
            mockTest = "మాక్ టెస్ట్‌లు",
            mockTestDesc = "AI-ఆధారిత సిమ్యులేషన్",
            pyqLibrary = "PYQ లైబ్రరీ",
            pyqLibraryDesc = "అధికారిక ఆర్కైవ్స్",
            aiNotes = "AI నోట్స్",
            aiNotesDesc = "తక్షణ అధ్యయన సామగ్రి",
            riyaAi = "రియా AI",
            riyaAiDesc = "24/7 స్మార్ట్ ట్యూటర్",
            currentAffairs = "రోజువారీ కరెంట్ అఫైర్స్",
            currentAffairsDesc = "తాజా పరీక్ష వార్తలతో అప్‌డేట్‌గా ఉండండి",
            performance = "పెర్ఫార్మెన్స్ అనలిటిక్స్",
            goals = "లక్ష్యాలు & పరీక్షలు",
            banner1Title = "AIతో పరీక్షల్లో పట్టు సాధించండి",
            banner1Desc = "50k+ విద్యార్థులతో చేరండి మరియు ఎదగండి.",
            banner2Title = "రోజువారీ ఉచిత మాక్ టెస్ట్‌లు",
            banner2Desc = "మీ పురోగతిని ప్రతిరోజూ ట్రాక్ చేయండి."
        ),
        mockTest = MockTestStrings(
            title = "మాక్ టెస్ట్",
            setupTitle = "AI మాక్ టెస్ట్",
            setupSubtitle = "సెకన్లలో మీ వ్యక్తిగతీకరించిన పరీక్షను రూపొందించండి",
            targetExam = "లక్ష్యం పరీక్ష",
            targetExamHint = "ఉదా: UPSC, SSC",
            subject = "విషయం",
            subjectHint = "ఉదా: చరిత్ర, పాలిటీ",
            startTest = "మాక్ టెస్ట్ ప్రారంభించండి",
            questionCount = "ప్రశ్న",
            previous = "మునుపటి",
            next = "తదుపరి",
            finish = "ముగించు",
            resultTitle = "క్విజ్ ఫలితం",
            excellent = "అద్భుతమైన పని!",
            good = "మంచి ప్రయత్నం!",
            tryAgain = "అభ్యాసం కొనసాగించండి!",
            retake = "మళ్ళీ పరీక్ష రాయండి",
            review = "వివరాలను సమీక్షించండి",
            scoreText = "మీరు %dకి %d స్కోర్ చేశారు",
            aiPrompt = "Generate 5 MCQ questions for %s exam, subject %s. Format: Q: Question A) Opt B) Opt C) Opt D) Opt Answer: Letter Explanation: Text. IMPORTANT: Please generate the entire content in Telugu. (Note: Use Telugu script)."
        ),
        onboarding = OnboardingStrings(
            step1Title = "మీ లక్ష్యాన్ని ఎంచుకోండి",
            step1Subtitle = "మీరు ఏ ప్రభుత్వ పరీక్షకు సిద్ధమవుతున్నారు?",
            step2Title = "భాష ఎంపిక",
            step2Subtitle = "మీ AI అధ్యయన సహాయకుడి కోసం భాషను ఎంచుకోండి.",
            step3Title = "అంకితభావం స్థాయి",
            step3Subtitle = "మీరు రోజుకు ఎన్ని గంటలు కేటాయించగలరు?",
            step4Title = "అంతా సిద్ధమైంది!",
            step4Subtitle = "మీ ప్రిపరేషన్‌ను పెంచ చేయడానికి మీ వ్యక్తిగతీకరించిన AI డాష్‌బోర్డ్ సిద్ధంగా ఉంది.",
            continueBtn = "కొనసాగించు",
            getStartedBtn = "ప్రారంభించండి",
            hoursDay = "రోజుకు %d గంటలు",
            stateTitle = "మీ రాష్ట్రాన్ని ఎంచుకోండి",
            stateSubtitle = "మీ కోసం పరీక్షలు మరియు వార్తలను రూపొందించడంలో మాకు సహాయపడుతుంది"
        ),
        splash = SplashStrings(
            slogan = "AI-ఆధారిత విజయం"
        ),
        notes = NotesStrings(
            title = "AI నోట్స్ జనరేటర్",
            introTitle = "తక్షణ అధ్యయన సామగ్రి",
            introSubtitle = "ఏదైనా అంశం కోసం AI-ఆధారిత నోट्स",
            topics = "నిర్దిష్ట అంశాలు (ఐచ్ఛికం)",
            topicsHint = "ఉదా: మొఘల్ సామ్రాజ్యం, ప్రాథమిక హక్కులు",
            generateBtn = "AI నోట్స్ రూపొందించండి",
            draftTitle = "డ్రాఫ్ట్ నోట్స్",
            aiPrompt = "Generate detailed study notes for %s exam, subject %s, focusing on topics: %s. Format the output with clear headings and bullet points. IMPORTANT: Please generate the entire content in Telugu. (Note: Use Telugu script)."
        ),
        planner = PlannerStrings(
            title = "AI స్టడీ ప్లానర్",
            introTitle = "వ్యక్తిగత రోడ్‌మ్యాప్",
            introSubtitle = "కేవలం మీ కోసం AI- రూపొందించిన షెడ్యూల్",
            hours = "రోజుకు గంటలు",
            hoursHint = "ఉదా: 4 గంటలు, 6 గంటలు",
            level = "ప్రస్తుత స్థాయి",
            levelHint = "ఉదా: బిగినర్, ఇంటర్మీడియట్",
            generateBtn = "మాస్టర్ ప్లాన్ రూపొందించండి",
            resultTitle = "మీ AI స్టడీ రోడ్‌మ్యాప్",
            aiPrompt = "Create a detailed study plan for %s exam preparation. Study hours: %s. Level: %s. Provide a weekly breakdown. IMPORTANT: Please generate the entire content in Telugu. (Note: Use Telugu script)."
        ),
        analytics = AnalyticsStrings(
            title = "పెర్ఫార్మెన్స్ అనలిటిక్స్",
            statsTitle = "పరీక్ష గణాంకాలు",
            statsSubtitle = "మీ పురోగతి యొక్క వివరణాत्मक విభజన",
            accuracy = "ఖచ్చితత్వం",
            consistency = "స్థిరత్వం",
            completion = "పూర్తి చేయడం",
            breakdown = "విషయాల విభజన",
            aiTitle = "AI డీప్ డైవ్ అనాలిసిస్",
            aiSubtitle = "మీ మాక్ టెస్ట్ ప్యాటర్న్‌ల ఆధారంగా వ్యక్తిగతీకరించిన అంతర్దృష్టులు మరియు మెరుగుదల చిట్కాలను పొందండి.",
            analyzeBtn = "నా పనితీరును విశ్లేషించండి",
            aiInsights = "AI అంతర్దృష్టులు",
            aiPrompt = "Analyze my exam performance. Accuracy: %d%%, Consistency: %d%%. Strongest: Current Affairs. Weakest: Economy. Provide Tips. IMPORTANT: Please generate the entire content in Telugu. (Note: Use Telugu script)."
        ),
        pyq = PyqStrings(
            title = "PYQ లైబ్రరీ",
            searchHint = "పేపర్లను వెతకండి...",
            start = "ప్రారంభించండి"
        ),
        tutor = TutorStrings(
            title = "రియా AI ట్యూటర్",
            placeholder = "రియాను ఏదైనా అడగండి...",
            thinking = "రియా ఆలోచిస్తోంది...",
            intro = "హలో! నేను రియా, మీ AI ట్యూటర్. ఈ రోజు నేను మీకు ఎలా సహాయపడగలను?",
            online = "ఆన్‌లైన్",
            chatCleared = "చాట్ క్లియర్ చేయబడింది! నేను ఎలా సహాయపడగలను?",
            headerBadge = "TE"
        ),
        settings = SettingsStrings(
            title = "సెట్టింగ్‌లు",
            language = "భాష",
            selectLanguage = "యాప్ భాషను ఎంచుకోండి",
            apiKey = "కస్టమ్ API కీ (BYOK)",
            stateLabel = "మీ రాష్ట్రం"
        ),
        currentAffairs = CurrentAffairsStrings(
            title = "Current Affairs",
            refresh = "Refresh",
            staticHintTitle = "Daily Static Fact",
            staticHintSubtitle = "The Indian Constitution is the longest written constitution of any sovereign country.",
            share = "Share",
            source = "Source: Sarkari Exam AI",
            aiPrompt = "Generate 3 important current affairs news items for today. Format: Title | Date | Category | Content. Also provide a unique daily static fact.",
            loading = "Updating News..."
        )
    )

    private val tamilStrings = AppStrings(
        common = CommonStrings(
            back = "பின்னால்",
            save = "சேமி",
            logout = "வெளியேறு",
            loading = "ஏற்றப்படுகிறது...",
            error = "பிழை ஏற்பட்டது",
            overview = "மேலோட்டம்"
        ),
        dashboard = DashboardStrings(
            welcome = "வரவேற்கிறோம், ஆர்வலரே!",
            subtitle = "உங்கள் AI தேர்வு பயிற்சியாளர் தயாராக உள்ளார்.",
            startTutor = "ரியா AI உடன் பேசுங்கள்",
            mockTest = "மாதிரி தேர்வுகள்",
            mockTestDesc = "AI-இயக்கப்படும் உருவகப்படுத்துதல்",
            pyqLibrary = "PYQ நூலகம்",
            pyqLibraryDesc = "அதிகாரப்பூர்வ காப்பகங்கள்",
            aiNotes = "AI குறிப்புகள்",
            aiNotesDesc = "உடனடி ஆய்வுப் பொருள்",
            riyaAi = "ரியா AI",
            riyaAiDesc = "24/7 புத்திசாலித்தனமான ஆசிரியர்",
            currentAffairs = "தினசரி நடப்பு நிகழ்வுகள்",
            currentAffairsDesc = "சமீபத்திய தேர்வு செய்திகளுடன் புதுப்பித்த நிலையில் இருங்கள்",
            performance = "செயல்திறன் பகுப்பாய்வு",
            goals = "இலக்குகள் & தேர்வுகள்",
            banner1Title = "AI மூலம் தேர்வுகளில் தேர்ச்சி பெறுங்கள்",
            banner1Desc = "50k+ மாணவர்களுடன் இணைந்து முன்னேறுங்கள்.",
            banner2Title = "தினசரி இலவச மாதிரி தேர்வுகள்",
            banner2Desc = "உங்கள் முன்னேற்றத்தை தினமும் கண்காணிக்கவும்."
        ),
        mockTest = MockTestStrings(
            title = "மாதிரி தேர்வு",
            setupTitle = "AI மாதிரி தேர்வு",
            setupSubtitle = "நொடிகளில் உங்கள் தனிப்பயனாக்கப்பட்ட தேர்வை உருவாக்குங்கள்",
            targetExam = "இலக்கு தேர்வு",
            targetExamHint = "உதாரணம்: UPSC, SSC",
            subject = "பாடம்",
            subjectHint = "உதாரணம்: வரலாறு, அரசியல்",
            startTest = "மாதிரி தேர்வைத் தொடங்குங்கள்",
            questionCount = "கேள்வி",
            previous = "முந்தைய",
            next = "அடுத்த",
            finish = "முடிக்க",
            resultTitle = "வினாடி வினா முடிவு",
            excellent = "சிறந்த பணி!",
            good = "நல்ல முயற்சி!",
            tryAgain = "தொடர்ந்து பயிற்சி செய்யுங்கள்!",
            retake = "மீண்டும் தேர்வு எழுதுங்கள்",
            review = "விவரங்களை மதிப்பாய்வு செய்யவும்",
            scoreText = "நீங்கள் %d-க்கு %d மதிப்பெண் எடுத்துள்ளீர்கள்",
            aiPrompt = "Generate 5 MCQ questions for %s exam, subject %s. Format: Q: Question A) Opt B) Opt C) Opt D) Opt Answer: Letter Explanation: Text. IMPORTANT: Please generate the entire content in Tamil. (Note: Use Tamil script)."
        ),
        onboarding = OnboardingStrings(
            step1Title = "உங்கள் கவனத்தைத் தேர்வுசெய்க",
            step1Subtitle = "நீங்கள் எந்த அரசுத் தேர்வுக்குத் தயாராகிறீர்கள்?",
            step2Title = "மொழி தேர்வு",
            step2Subtitle = "உங்கள் AI ஆய்வு உதவியாளருக்கான மொழியைத் தேர்ந்தெடுக்கவும்.",
            step3Title = "அர்ப்பணிப்பு நிலை",
            step3Subtitle = "தினமும் எத்தனை மணிநேரம் ஒதுக்க முடியும்?",
            step4Title = "எல்லாம் தயார்!",
            step4Subtitle = "உங்கள் தனிப்பயனாக்கப்பட்ட AI டாஷ்போர்டு உங்கள் தயாரிப்பை அதிகரிக்கத் தயாராக உள்ளது.",
            continueBtn = "தொடரவும்",
            getStartedBtn = "தொடங்குங்கள்",
            hoursDay = "ஒரு நாளைக்கு %d மணிநேரம்",
            stateTitle = "உங்கள் மாநிலத்தைத் தேர்ந்தெடுக்கவும்",
            stateSubtitle = "உங்களுக்கான தேர்வுகள் மற்றும் செய்திகளைத் தனிப்பயனாக்க எங்களுக்கு உதவுகிறது"
        ),
        splash = SplashStrings(
            slogan = "AI-இயக்கப்படும் வெற்றி"
        ),
        notes = NotesStrings(
            title = "AI குறிப்புகள் உருவாக்குபவர்",
            introTitle = "உடனடி ஆய்வுப் பொருள்",
            introSubtitle = "எந்தவொரு தலைப்பிற்கும் AI-இயக்கப்படும் குறிப்புகள்",
            topics = "குறிப்பிட்ட தலைப்புகள் (விருப்பத்தேர்வு)",
            topicsHint = "உதாரணம்: முகலாய பேரரசு, அடிப்படை உரிமைகள்",
            generateBtn = "AI குறிப்புகளை உருவாக்கு",
            draftTitle = "வரைவு குறிப்புகள்",
            aiPrompt = "Generate detailed study notes for %s exam, subject %s, focusing on topics: %s. Format the output with clear headings and bullet points. IMPORTANT: Please generate the entire content in Tamil. (Note: Use Tamil script)."
        ),
        planner = PlannerStrings(
            title = "AI ஆய்வு திட்டமிடுபவர்",
            introTitle = "தனிப்பயனாக்கப்பட்ட சாலைவரைபடம்",
            introSubtitle = "உங்களுக்காக பிரத்யேகமாக AI-உருவாக்கிய அட்டவணை",
            hours = "ஒரு நாளைக்கு மணிநேரம்",
            hoursHint = "உதாரணம்: 4 மணிநேரம், 6 மணிநேரம்",
            level = "தற்போதைய நிலை",
            levelHint = "உதாரணம்: தொடக்கநிலை, இடைநிலை",
            generateBtn = "மாஸ்டர் திட்டத்தை உருவாக்கு",
            resultTitle = "உங்கள் AI ஆய்வு சாலைவரைபடம்",
            aiPrompt = "Create a detailed study plan for %s exam preparation. Study hours: %s. Level: %s. Provide a weekly breakdown. IMPORTANT: Please generate the entire content in Tamil. (Note: Use Tamil script)."
        ),
        analytics = AnalyticsStrings(
            title = "செயல்திறன் பகுப்பாய்வு",
            statsTitle = "தேர்வு புள்ளிவிவரங்கள்",
            statsSubtitle = "உங்கள் முன்னேற்றத்தின் விரிவான விவரம்",
            accuracy = "துல்லியம்",
            consistency = "நிலைத்தன்மை",
            completion = "நிறைவு",
            breakdown = "பாடம் வாரியான விவரம்",
            aiTitle = "AI ஆழமான பகுப்பாய்வு",
            aiSubtitle = "உங்கள் மாதிரி தேர்வு முறைகளின் அடிப்படையில் தனிப்பயனாக்கப்பட்ட நுண்ணறிவு மற்றும் மேம்பாட்டுக் குறிப்புகளைப் பெறுங்கள்.",
            analyzeBtn = "எனது செயல்திறனைப் பகுப்பாய்வு செய்",
            aiInsights = "AI நுண்ணறிவு",
            aiPrompt = "Analyze my exam performance. Accuracy: %d%%, Consistency: %d%%. Strongest: Current Affairs. Weakest: Economy. Provide Tips. IMPORTANT: Please generate the entire content in Tamil. (Note: Use Tamil script)."
        ),
        pyq = PyqStrings(
            title = "PYQ நூலகம்",
            searchHint = "தாள்களைத் தேடுங்கள்...",
            start = "தொடங்கு"
        ),
        tutor = TutorStrings(
            title = "ரியா AI ஆசிரியர்",
            placeholder = "ரியாவிடம் எதையும் கேளுங்கள்...",
            thinking = "ரியா யோசிக்கிறார்...",
            intro = "வணக்கம்! நான் ரியா, உங்கள் AI ஆசிரியர். இன்று நான் உங்களுக்கு எப்படி உதவ முடியும்?",
            online = "ஆன்லைன்",
            chatCleared = "அரட்டை அழிக்கப்பட்டது! நான் எப்படி உதவ முடியும்?",
            headerBadge = "TA"
        ),
        settings = SettingsStrings(
            title = "அமைப்புகள்",
            language = "மொழி",
            selectLanguage = "பயன்பாட்டு மொழியைத் தேர்ந்தெடுக்கவும்",
            apiKey = "தனிப்பயன் API விசை (BYOK)",
            stateLabel = "உங்கள் மாநிலம்"
        ),
        currentAffairs = CurrentAffairsStrings(
            title = "Current Affairs",
            refresh = "Refresh",
            staticHintTitle = "Daily Static Fact",
            staticHintSubtitle = "The Indian Constitution is the longest written constitution of any sovereign country.",
            share = "Share",
            source = "Source: Sarkari Exam AI",
            aiPrompt = "Generate 3 important current affairs news items for today. Format: Title | Date | Category | Content. Also provide a unique daily static fact.",
            loading = "Updating News..."
        )
    )
}
