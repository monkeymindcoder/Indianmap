package com.example.data

import androidx.compose.ui.geometry.Offset

data class MapPoint(val x: Float, val y: Float) {
    fun toOffset(width: Float, height: Float, zoom: Float, pan: Offset): Offset {
        // Map points are scaled to fit a normalized 100x100 grid centered on the canvas
        // Preserve aspect ratio
        val scaleX = width / 100f
        val scaleY = height / 100f
        val scale = minOf(scaleX, scaleY)
        
        val offsetX = (width - 100f * scale) / 2f
        val offsetY = (height - 100f * scale) / 2f
        
        val basePointX = offsetX + x * scale
        val basePointY = offsetY + y * scale
        
        // Apply zoom and pan relative to the center of the canvas
        val centerX = width / 2f
        val centerY = height / 2f
        
        return Offset(
            x = (basePointX - centerX) * zoom + centerX + pan.x,
            y = (basePointY - centerY) * zoom + centerY + pan.y
        )
    }
}

data class District(
    val id: String,
    val name: String,
    val relativeX: Float, // offset from state center (0..1)
    val relativeY: Float,
    val elevationMeters: Int,
    val temperatureCelsius: Int,
    val annualRainfallMm: Int,
    val climateSummary: String,
    val historicInsight: String,
    val primaryAttraction: String
)

data class StateData(
    val id: String,
    val name: String,
    val capital: String,
    val centerX: Float,
    val centerY: Float,
    val points: List<MapPoint>,
    val elevationMeters: Int,
    val averageTemp: Int,
    val annualRainfall: Int,
    val topography: String,
    val culture: String,
    val historicalLegacy: String,
    val districts: List<District>
)

object IndiaMapData {
    val states = listOf(
        StateData(
            id = "JK",
            name = "Jammu & Kashmir & Ladakh",
            capital = "Srinagar & Leh",
            centerX = 48f,
            centerY = 14f,
            points = listOf(
                MapPoint(45f, 9f), MapPoint(51f, 8f), MapPoint(56f, 11f),
                MapPoint(54f, 15f), MapPoint(49f, 18f), MapPoint(44f, 18f),
                MapPoint(42f, 14f), MapPoint(43f, 11f)
            ),
            elevationMeters = 3800,
            averageTemp = 12,
            annualRainfall = 650,
            topography = "Himalayan ranges, high altitude cold desert, deep river gorges, and tectonic valleys.",
            culture = "Melange of Sufi, Buddhist, and Hindu lifestyles. Famous for Pashmina weaving, Kahwa tea, and woodcrafts.",
            historicalLegacy = "Ancient Silk Road gateway, seat of Kushan empire developments, and modern Himalayan borderlands.",
            districts = listOf(
                District("SRI", "Srinagar", 0f, 0f, 1585, 14, 690, "Pleasant summer, snowy winters", "Srinagar was established by Emperor Ashoka.", "Dal Lake & Shalimar Mogul Garden"),
                District("LEH", "Leh Ladakh", 4f, -2f, 3524, 6, 110, "Arid alpine cold desert climate", "Historic capital of the Himalayan Kingdom of Ladakh.", "Shanti Stupa & Pangong Tso Lake"),
                District("JAM", "Jammu", -2f, 3f, 327, 22, 1100, "Subtropical warm summers and mild winters", "Ancient Dogra dynasty headquarters.", "Raghunath Temple & Bahu Fort")
            )
        ),
        StateData(
            id = "HP",
            name = "Himachal & Uttarakhand",
            capital = "Shimla & Dehradun",
            centerX = 51f,
            centerY = 20f,
            points = listOf(
                MapPoint(49f, 18f), MapPoint(54f, 15f), MapPoint(58f, 19f),
                MapPoint(61f, 22f), MapPoint(55f, 24f), MapPoint(51f, 24f),
                MapPoint(48f, 21f)
            ),
            elevationMeters = 2200,
            averageTemp = 15,
            annualRainfall = 1400,
            topography = "Sivalik foothills and Greater Himalayas. Pine, cedar forests, and perennial glacial river sources.",
            culture = "Himachali Pahari architecture, rich apple orchard valleys, and sacred Ganges/Yamuna char dham traditions.",
            historicalLegacy = "Devbhumi (Land of the Gods). Ancient hermitages of Rishis and scenic capital for colonial British viceroys.",
            districts = listOf(
                District("SIM", "Shimla", -1f, -1f, 2205, 14, 1480, "Temperate with misty summers", "Summer capital of British India.", "The Ridge & Jakhoo Hill"),
                District("DHA", "Dharamshala", -2f, -2f, 1457, 18, 2900, "Heavy precipitation and cool alpine winters", "Home of the Dalai Lama and Tibetan Government in Exile.", "Tsuglagkhang Buddhist Complex"),
                District("DEH", "Dehradun", 2f, 2f, 435, 21, 1900, "Humid subtropical valleys", "Sanskrit origin meaning 'camp in valley'.", "Forest Research Institute & Mussoorie")
            )
        ),
        StateData(
            id = "PB",
            name = "Punjab & Haryana",
            capital = "Chandigarh",
            centerX = 43f,
            centerY = 21f,
            points = listOf(
                MapPoint(41f, 18f), MapPoint(48f, 21f), MapPoint(50f, 24f),
                MapPoint(46f, 26f), MapPoint(41f, 25f), MapPoint(38f, 22f)
            ),
            elevationMeters = 250,
            averageTemp = 24,
            annualRainfall = 700,
            topography = "Flat alluvial plains fed by the Sutlej, Beas, and Ravi rivers. Ideal for hyper-agriculture.",
            culture = "Vibrant Bhangra beats, community kitchens (Langar), heroic martial traditions, and legendary hospitality.",
            historicalLegacy = "Battlefield of Mahabharata (Kurukshetra) and the crucial Indus Valley civilization archaeological zones.",
            districts = listOf(
                District("AMR", "Amritsar", -2f, -2f, 234, 23, 700, "Semi-arid with extreme summers/winters", "Founded by Sikh Guru Ram Das in 1577.", "The Golden Temple (Harmandir Sahib)"),
                District("LUD", "Ludhiana", 0f, 0f, 244, 24, 750, "Industrially warm subtropical", "Named in 1480 after Lodi Dynasty rulers.", "Gurudwara Charan Kanwal"),
                District("KUR", "Kurukshetra", 2f, 2f, 260, 24, 800, "Semi-arid Gangetic plains", "Mythological site of the Mahabharata war.", "Brahma Sarovar Sacred Lake")
            )
        ),
        StateData(
            id = "RJ",
            name = "Rajasthan",
            capital = "Jaipur",
            centerX = 31f,
            centerY = 31f,
            points = listOf(
                MapPoint(25f, 24f), MapPoint(38f, 22f), MapPoint(41f, 25f),
                MapPoint(39f, 32f), MapPoint(37f, 36f), MapPoint(33f, 36f),
                MapPoint(28f, 35f), MapPoint(24f, 30f)
            ),
            elevationMeters = 340,
            averageTemp = 29,
            annualRainfall = 450,
            topography = "Arid western Thar sands, rolling Aravalli mountains (oldest fold mountains), and dry deciduous forests.",
            culture = "Stunning colorful turbans, Ghoomar dance, spicy Dal Baati Choorma, and fine miniature frescoes.",
            historicalLegacy = "Cradle of valiant Rajput kingdoms, Mewar-Marwar dynasties, and legendary palaces.",
            districts = listOf(
                District("JAI", "Jaipur", 2f, -1f, 431, 25, 650, "Hot semi-arid desert fringe", "The Pink City established in 1727 by Maharaja Sawai Jai Singh II.", "Hawa Mahal & Amber Palace"),
                District("UDA", "Udaipur", 1f, 2f, 598, 24, 610, "Warm lake-moderated plain", "City of Lakes founded by Maharana Udai Singh II in 1559.", "Lake Palace & City Palace Complex"),
                District("JAS", "Jaisalmer", -3f, 0f, 225, 29, 210, "Extreme hyper-arid Thar sands", "Golden sandstone city situated on the Trikuta Hill.", "Jaisalmer Living Fort & Sam Sand Dunes")
            )
        ),
        StateData(
            id = "GJ",
            name = "Gujarat",
            capital = "Gandhinagar",
            centerX = 21f,
            centerY = 40f,
            points = listOf(
                MapPoint(16f, 35f), MapPoint(28f, 35f), MapPoint(31f, 40f),
                MapPoint(27f, 46f), MapPoint(21f, 45f), MapPoint(18f, 40f)
            ),
            elevationMeters = 150,
            averageTemp = 28,
            annualRainfall = 800,
            topography = "Salt marshes of Kutch (the Rann), peninsular Kathiawar hills, and a long rich Arabian Sea coastline.",
            culture = "Garba and Dandiya Raas festive dances, absolute Gujarati Thali gastronomy, and entrepreneurial pioneering.",
            historicalLegacy = "Port of Lothal (Indus Valley 2400 BC), Solanki structural marvels, and core land of Mahatma Gandhi.",
            districts = listOf(
                District("AHE", "Ahmedabad", 2f, 0f, 53, 27, 800, "Hot semi-arid climate", "Founded by Sultan Ahmed Shah on Sabarmati banks.", "Sabarmati Gandhi Ashram & Adalaj Stepwell"),
                District("BHU", "Bhuj", -2f, -2f, 110, 28, 350, "Arid Kutch salt-fringe", "Historic capital of the princely State of Cutch.", "Rann of Kutch marsh desert"),
                District("SUR", "Surat", 2f, 3f, 13, 27, 1200, "Humid coastal maritime", "Historic trade hub with the British and Portuguese empires.", "Dutch Cemeteries & Dumas Beach")
            )
        ),
        StateData(
            id = "UP",
            name = "Uttar Pradesh",
            capital = "Lucknow",
            centerX = 53f,
            centerY = 29f,
            points = listOf(
                MapPoint(46f, 26f), MapPoint(55f, 24f), MapPoint(60f, 26f),
                MapPoint(64f, 31f), MapPoint(58f, 34f), MapPoint(48f, 32f),
                MapPoint(41f, 25f)
            ),
            elevationMeters = 180,
            averageTemp = 26,
            annualRainfall = 1050,
            topography = "Low fertile plains of Gangetic basin, Doab lands, and southern Vindhya sandstone ranges.",
            culture = "Kathak classical dance, Awadhi cuisine, Kumbh Mela congregation, and Banarasi silk weaving.",
            historicalLegacy = "Birthplace of Hinduism (Varanasi, Ayodhya), Ashokan pillars, and peak of Mughal architecture (Taj Mahal).",
            districts = listOf(
                District("AGR", "Agra", -3f, 1f, 171, 26, 750, "Hot semi-arid plains", "The capital of Mughal Emperors Akbar and Shah Jahan.", "The Taj Mahal & Agra Fort"),
                District("VAR", "Varanasi", 3f, 2f, 80, 26, 1110, "Humid subtropical", "One of the oldest continuously inhabited cities in the world.", "Kashi Vishwanath Temple & Ganga Ghats"),
                District("LUC", "Lucknow", -1f, 0f, 123, 25, 960, "Warm humid subtropical plains", "The cultural capital of Awadh Nawabs.", "Bara Imambara & Rumi Darwaza")
            )
        ),
        StateData(
            id = "MP",
            name = "Madhya Pradesh",
            capital = "Bhopal",
            centerX = 46f,
            centerY = 39f,
            points = listOf(
                MapPoint(39f, 32f), MapPoint(48f, 32f), MapPoint(58f, 34f),
                MapPoint(61f, 39f), MapPoint(56f, 45f), MapPoint(44f, 45f),
                MapPoint(37f, 36f)
            ),
            elevationMeters = 500,
            averageTemp = 27,
            annualRainfall = 1100,
            topography = "Central highlands, Narmada rift valley, Vindhyachals, and dense tiger forests of Kanha and Bandhavgarh.",
            culture = "Bhil and Gond tribal wood arts, spicy Poha Jalebi, and historical marble canyon boat-ways.",
            historicalLegacy = "Heart of India. Features Sanchi Stupa (Emperor Ashoka) and Khajuraho erotic Chandela temple complexes.",
            districts = listOf(
                District("BHO", "Bhopal", -1f, 0f, 527, 25, 1150, "Humid subtropical lake wind", "Founded by Raja Bhoj around the lakes.", "Upper Lake & Sanchi Stupa nearby"),
                District("IND", "Indore", -3f, 1f, 553, 26, 950, "Mild climate with cool Malwa breeze", "Ruled by the Holkar queen Ahilyabai.", "Lal Bagh Palace & Sarafa Food Street"),
                District("KHA", "Khajuraho", 2f, -2f, 283, 27, 1000, "Subtropical forested", "Built by the Chandela Dynasty between 950-1050 AD.", "UNESCO Khajuraho Temples")
            )
        ),
        StateData(
            id = "BI",
            name = "Bihar",
            capital = "Patna",
            centerX = 65f,
            centerY = 31f,
            points = listOf(
                MapPoint(60f, 26f), MapPoint(70f, 27f), MapPoint(72f, 32f),
                MapPoint(68f, 35f), MapPoint(63f, 35f), MapPoint(58f, 34f)
            ),
            elevationMeters = 53,
            averageTemp = 26,
            annualRainfall = 1200,
            topography = "Flat Indo-Gangetic floodplains, rich fertile soil fed by seasonal Himalayan streams.",
            culture = "Madhubani painting (folk geometric art), Chhath Puja (solar rites), and unique Litti Chokha delicacy.",
            historicalLegacy = "Epicenter of Maurya and Gupta golden eras. Land where Buddha attained enlightenment at Bodh Gaya.",
            districts = listOf(
                District("PAT", "Patna", 0f, 0f, 53, 26, 1150, "Humid subtropical plains", "Ancient Pataliputra, metropolis of dynasties.", "Golghar & Patna Museum"),
                District("GAY", "Gaya & Bodhgaya", 0f, 2f, 113, 26, 1100, "Vindhyan valley edge", "Where Siddhartha Gautama became the Buddha.", "Mahabodhi Temple & Vishnupad Temple"),
                District("NAL", "Nalanda", 1f, 1f, 67, 26, 1120, "Flat plains", "The world's first residential international university.", "Ancient Ruins of Nalanda University")
            )
        ),
        StateData(
            id = "NE",
            name = "Northeast States",
            capital = "Guwahati & Shillong",
            centerX = 81f,
            centerY = 28f,
            points = listOf(
                MapPoint(70f, 27f), MapPoint(78f, 23f), MapPoint(86f, 22f),
                MapPoint(89f, 26f), MapPoint(85f, 32f), MapPoint(81f, 33f),
                MapPoint(74f, 29f)
            ),
            elevationMeters = 1500,
            averageTemp = 20,
            annualRainfall = 2800,
            topography = "Tropical green rainforest hills, valley of Brahmaputra River, and Khasi-Jaintia cloud plateaus.",
            culture = "Vibrant Bihu dances, Hornbill festival, eco-living root bridges, and beautiful hand-woven bamboo basketry.",
            historicalLegacy = "The undefeated Ahom Kingdom of Assam; pristine tribal domains independent of mainstream royal states.",
            districts = listOf(
                District("GUW", "Guwahati", -2f, 0f, 55, 23, 1720, "Warm tropical humid valley", "Ancient city of temples on the Brahmaputra bank.", "Kamakhya Shakti Temple"),
                District("SHI", "Shillong", -1f, 1f, 1525, 17, 2400, "Cool pine hills and misty tablelands", "Called Scotland of the East under colonial times.", "Elephant Falls & Laitlum Canyons"),
                District("CHE", "Cherrapunji", -1f, 2f, 1484, 16, 11777, "Extremely wet with continuous monsoons", "Historically the wettest place on Earth.", "Double Decker Living Root Bridges")
            )
        ),
        StateData(
            id = "WB",
            name = "West Bengal & Odisha",
            capital = "Kolkata & Bhubaneswar",
            centerX = 67f,
            centerY = 42f,
            points = listOf(
                MapPoint(64f, 31f), MapPoint(72f, 32f), MapPoint(74f, 38f),
                MapPoint(70f, 43f), MapPoint(64f, 49f), MapPoint(59f, 46f),
                MapPoint(56f, 45f), MapPoint(61f, 39f)
            ),
            elevationMeters = 80,
            averageTemp = 27,
            annualRainfall = 1600,
            topography = "Sundarbans mangrove delta, coastal Bay of Bengal plains, and dynamic Mahanadi delta terrains.",
            culture = "Renaissance literature, Baul mystical music, Odissi classical dance, Rasgulla feast, and Durga Puja.",
            historicalLegacy = "Kalinga empire of Emperor Ashoka, British East India Company mercantile launchpad, and Tagore’s legacy.",
            districts = listOf(
                District("KOL", "Kolkata", 1f, -1f, 9, 26, 1600, "Humid coastal maritime delta", "The cultural Capital of India and former British capital.", "Victoria Memorial & Howrah Bridge"),
                District("BHU", "Bhubaneswar", -2f, 2f, 45, 27, 1450, "Hot humid coastal plains", "The ancient Temple City of Kalinga.", "Lingaraj Ancient Temple & Udayagiri Caves"),
                District("PUI", "Puri", -2f, 3f, 10, 27, 1500, "Maritime sea breeze plain", "Sacred Dham on the Arabian/Bay coast.", "Lord Jagannath Temple & Golden Beach")
            )
        ),
        StateData(
            id = "MH",
            name = "Maharashtra",
            capital = "Mumbai",
            centerX = 37f,
            centerY = 50f,
            points = listOf(
                MapPoint(27f, 46f), MapPoint(34f, 46f), MapPoint(44f, 45f),
                MapPoint(46f, 49f), MapPoint(43f, 55f), MapPoint(35f, 56f),
                MapPoint(29f, 53f)
            ),
            elevationMeters = 600,
            averageTemp = 27,
            annualRainfall = 1800,
            topography = "The basaltic Deccan Trap Plateau, western Sahyadri Ghat scarps, and tropical Konkan beach plains.",
            culture = "Lavani folk dance, royal war-fort architectures, Ganesh Utsav, and high-energy cinema/industrial hub.",
            historicalLegacy = "Seat of Chhatrapati Shivaji Maharaj's Maratha Kingdom, ancient Satavahana lineages, and Buddhist cave carvings.",
            districts = listOf(
                District("MUM", "Mumbai", -3f, 0f, 14, 27, 2200, "Warm humid tropical coastal", "Finance and entertainment capital of India.", "Gateway of India & Marine Drive"),
                District("PUN", "Pune", -2f, 1f, 560, 24, 750, "Temperate plateau weather", "The historical seat of the Maratha Peshwas.", "Shaniwar Wada & Aga Khan Palace"),
                District("NAG", "Nagpur", 2f, -1f, 310, 27, 1100, "Dry tropical continental", "The Orange City and geographical center of India.", "Deekshabhoomi stupa & Ambazari Lake")
            )
        ),
        StateData(
            id = "AP",
            name = "Andhra Pradesh & Telangana",
            capital = "Amaravati & Hyderabad",
            centerX = 49f,
            centerY = 60f,
            points = listOf(
                MapPoint(44f, 45f), MapPoint(56f, 45f), MapPoint(59f, 49f),
                MapPoint(53f, 59f), MapPoint(54f, 66f), MapPoint(48f, 68f),
                MapPoint(43f, 55f), MapPoint(46f, 49f)
            ),
            elevationMeters = 220,
            averageTemp = 28,
            annualRainfall = 950,
            topography = "Eastern coastal delta plains of Godavari and Krishna, and semi-arid inland granite plateau rocks.",
            culture = "Kuchipudi classical dance, aromatic Biryanis, intricate Bidri metal ornaments, and rich Telugu literary arts.",
            historicalLegacy = "Empire of the Satavahanas, Kakatiya architects, and the incredibly wealthy Nizams of Golconda.",
            districts = listOf(
                District("HYD", "Hyderabad", -2f, -2f, 542, 26, 830, "Semi-arid plateau climate", "The City of Pearls founded by Quli Qutb Shah.", "Charminar & Golconda Fort"),
                District("VIZ", "Visakhapatnam", 2f, 1f, 4, 28, 980, "Tropical wet and humid coast", "Strategic harbor and beach metropolis.", "Araku Valley & INS Kursura Museum"),
                District("TIR", "Tirupati", 1f, 3f, 161, 28, 930, "Hot humid plains", "Home to the world's richest spiritual temple site.", "Sri Venkateswara Temple on Tirumala Hills")
            )
        ),
        StateData(
            id = "KA",
            name = "Karnataka",
            capital = "Bengaluru",
            centerX = 38f,
            centerY = 67f,
            points = listOf(
                MapPoint(29f, 53f), MapPoint(35f, 56f), MapPoint(43f, 55f),
                MapPoint(45f, 59f), MapPoint(43f, 69f), MapPoint(37f, 74f),
                MapPoint(32f, 71f), MapPoint(30f, 60f)
            ),
            elevationMeters = 800,
            averageTemp = 24,
            annualRainfall = 1200,
            topography = "Western Malnad forests, moist coastal plains, and the elevated dry Mysore plateau slopes.",
            culture = "Yakshagana folk theatre, rich sandalwood crafts, fragrant filter coffee, and Mysore Silk weaving houses.",
            historicalLegacy = "Seat of Vijayanagara empire ruins at Hampi, structural Chalukyas, and the Kadambas.",
            districts = listOf(
                District("BEN", "Bengaluru", 2f, 2f, 920, 22, 900, "Delightful year-round temperate", "Silicon Valley of India, known for lakes and gardens.", "Mysore Palace & Hampi Ruins nearby"),
                District("MYS", "Mysuru", 1f, 3f, 763, 23, 780, "Warm subtropical", "Historic city in the foothills of Chamundi Hills.", "Mysore Palace & Chamundeshwari Temple"),
                District("HAM", "Hampi (Hospet)", 0f, -1f, 428, 26, 680, "Hot dry tropical", "Capital of the historic Vijayanagara Empire.", "Virupaksha Temple & stone chariot")
            )
        ),
        StateData(
            id = "KL",
            name = "Kerala",
            capital = "Thiruvananthapuram",
            centerX = 37f,
            centerY = 80f,
            points = listOf(
                MapPoint(32f, 71f), MapPoint(37f, 74f), MapPoint(40f, 80f),
                MapPoint(39f, 87f), MapPoint(36f, 87f), MapPoint(34f, 81f)
            ),
            elevationMeters = 300,
            averageTemp = 27,
            annualRainfall = 3100,
            topography = "Lush green coastal backwaters, moist tea/plantation slopes of Cardamom Hills (Anamudi - highest South peak).",
            culture = "Kathakali classical dance-drama, backwater houseboat races (Snake boats), and Ayurveda therapies.",
            historicalLegacy = "God’s Own Country. Ancient global spice trade nexus with Romans, Chinese, Arabs, and European navigators.",
            districts = listOf(
                District("VOC", "Kochi", -0.5f, 0f, 2, 27, 3010, "Tropical wet maritime monsoon", "The Queen of the Arabian Sea, colonial spice trade hub.", "Dutch Palace & Chinese Fishing Nets"),
                District("TVM", "Trivandrum", 0.5f, 2f, 10, 27, 2100, "Warm humid coastal", "The Evergreen City of India.", "Sree Padmanabhaswamy Temple"),
                District("WYB", "Wayanad", -0.5f, -2f, 700, 21, 2600, "Cool highland mountain breeze", "Historic spice plantations and prehistoric caves.", "Edakkal Caves & Banasura Sagar Dam")
            )
        ),
        StateData(
            id = "TN",
            name = "Tamil Nadu",
            capital = "Chennai",
            centerX = 44f,
            centerY = 79f,
            points = listOf(
                MapPoint(37f, 74f), MapPoint(43f, 69f), MapPoint(48f, 68f),
                MapPoint(48f, 74f), MapPoint(46f, 82f), MapPoint(43f, 88f),
                MapPoint(39f, 87f), MapPoint(40f, 80f)
            ),
            elevationMeters = 150,
            averageTemp = 29,
            annualRainfall = 990,
            topography = "Coromandel coastal flat plains, Nilgiri hills (Blue mountains) in west, and Palani forest ranges.",
            culture = "Oldest living language Tamil, Bharatanatyam classical dance, Carnatic music, and magnificent stone gopurams.",
            historicalLegacy = "Cradle of the majestic Chola maritime empire, Pallavas (Mahabalipuram carvings), and Pandyan stone temples.",
            districts = listOf(
                District("CHE", "Chennai", 2f, -2f, 6, 29, 1400, "Hot humid maritime coastal", "Gateway to the South, famous for cultural academy seasons.", "Marina Beach & Kapaleeshwarar Temple"),
                District("MAD", "Madurai", -1f, 1f, 101, 29, 850, "Hot dry interior tropical", "Ancient City of Temples continuously inhabited.", "Meenakshi Amman Temple"),
                District("OOT", "Ooty (Udhagamandalam)", -2f, -1f, 2240, 14, 1240, "Cool subalpine mountain climate", "The Queen of Hill Stations in the Nilgiri ranges.", "Ooty Botanical Gardens & Toy Train")
            )
        )
    )
}
