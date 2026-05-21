package org.terst.cancan.reading_room

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadingRoomRepository @Inject constructor() {

    val documents: List<ReadingRoomDocument> = listOf(
        // ── USDA Canning Guide ──────────────────────────────────────────────
        ReadingRoomDocument(
            id = "usda-intro",
            title = "Introduction — Principles of Home Canning",
            category = "USDA Canning Guide",
            description = "Overview of safe home canning practices, equipment, and basic procedures.",
            assetPath = "reading_room/usda_canning_guide/INTRO_principles.pdf"
        ),
        ReadingRoomDocument(
            id = "usda-guide-01",
            title = "Guide 1: Principles of Home Canning",
            category = "USDA Canning Guide",
            description = "Science and safety principles behind the USDA home canning guidelines.",
            assetPath = "reading_room/usda_canning_guide/GUIDE01_principles.pdf"
        ),
        ReadingRoomDocument(
            id = "usda-guide-02",
            title = "Guide 2: Canning Fruit and Fruit Products",
            category = "USDA Canning Guide",
            description = "Selecting, preparing, and canning fruit and fruit products.",
            assetPath = "reading_room/usda_canning_guide/GUIDE02_fruits.pdf"
        ),
        ReadingRoomDocument(
            id = "usda-guide-03",
            title = "Guide 3: Canning Tomatoes",
            category = "USDA Canning Guide",
            description = "Selecting, preparing, and canning tomatoes and tomato products.",
            assetPath = "reading_room/usda_canning_guide/GUIDE03_tomatoes.pdf"
        ),
        ReadingRoomDocument(
            id = "usda-guide-04",
            title = "Guide 4: Canning Vegetables",
            category = "USDA Canning Guide",
            description = "Selecting, preparing, and pressure canning vegetables.",
            assetPath = "reading_room/usda_canning_guide/GUIDE04_vegetables.pdf"
        ),
        ReadingRoomDocument(
            id = "usda-guide-05",
            title = "Guide 5: Canning Poultry, Meats, and Seafoods",
            category = "USDA Canning Guide",
            description = "Preparing and canning poultry, red meats, and seafoods.",
            assetPath = "reading_room/usda_canning_guide/GUIDE05_meats.pdf"
        ),
        ReadingRoomDocument(
            id = "usda-guide-06",
            title = "Guide 6: Fermented Foods and Pickled Vegetables",
            category = "USDA Canning Guide",
            description = "Preparing and canning fermented foods and pickled vegetables.",
            assetPath = "reading_room/usda_canning_guide/GUIDE06_fermented_pickles.pdf"
        ),
        ReadingRoomDocument(
            id = "usda-guide-07",
            title = "Guide 7: Preparing and Canning Jams and Jellies",
            category = "USDA Canning Guide",
            description = "Preparing and canning jams, jellies, and other sweet spreads.",
            assetPath = "reading_room/usda_canning_guide/GUIDE07_jams_jellies.pdf"
        ),

        // ── NCHFP Fact Sheets ───────────────────────────────────────────────
        ReadingRoomDocument(
            id = "nchfp-boiling-water",
            title = "Using Boiling Water Canners",
            category = "NCHFP",
            description = "NCHFP fact sheet on using boiling water canners safely.",
            assetPath = "reading_room/nchfp_factsheets/using_boiling_water_canners.pdf"
        ),
        ReadingRoomDocument(
            id = "nchfp-pressure",
            title = "Using Pressure Canners",
            category = "NCHFP",
            description = "NCHFP fact sheet on using pressure canners safely.",
            assetPath = "reading_room/nchfp_factsheets/using_pressure_canners.pdf"
        ),
        ReadingRoomDocument(
            id = "nchfp-water",
            title = "Home Canning Water",
            category = "NCHFP",
            description = "Water quality considerations for home canning.",
            assetPath = "reading_room/nchfp_factsheets/home_canning_water.pdf"
        ),

        // ── Hawaii CTAHR ────────────────────────────────────────────────────
        ReadingRoomDocument(
            id = "ctahr-edible-plants",
            title = "Edible Plants for Hawai'i Landscapes",
            category = "Hawaii CTAHR",
            description = "Taro, passion fruit, bamboo, and other edible landscape plants in Hawai'i.",
            assetPath = "reading_room/hawaii_ctahr/edible_plants_hawaii_landscapes.pdf"
        ),
        ReadingRoomDocument(
            id = "ctahr-breadfruit",
            title = "Hawaiian Breadfruit — Ethnobotany and Nutrition",
            category = "Hawaii CTAHR",
            description = "Ethnobotany, nutrition, and human ecology of Hawaiian breadfruit.",
            assetPath = "reading_room/hawaii_ctahr/breadfruit_ethnobotany_nutrition.pdf"
        ),
        ReadingRoomDocument(
            id = "ctahr-ulu-postharvest",
            title = "'Ulu (Breadfruit) — Postharvest Handling",
            category = "Hawaii CTAHR",
            description = "'Ulu postharvest handling and nutrition facts.",
            assetPath = "reading_room/hawaii_ctahr/ulu_breadfruit_postharvest.pdf"
        ),
        ReadingRoomDocument(
            id = "ctahr-ten-tropical",
            title = "Ten Tropical Fruits of Potential Value",
            category = "Hawaii CTAHR",
            description = "Guava, sapodilla, and other tropical fruits with commercial potential.",
            assetPath = "reading_room/hawaii_ctahr/ten_tropical_fruits.pdf"
        ),
        ReadingRoomDocument(
            id = "ctahr-twelve-tropical",
            title = "Twelve Fruits With Culinary Uses",
            category = "Hawaii CTAHR",
            description = "Twelve tropical fruits with potential value-added and culinary applications.",
            assetPath = "reading_room/hawaii_ctahr/twelve_tropical_fruits_culinary.pdf"
        ),
        ReadingRoomDocument(
            id = "ctahr-south-pacific",
            title = "South Pacific Island Foods",
            category = "Hawaii CTAHR",
            description = "Description, history, and nutritive value of tropical South Pacific island foods.",
            assetPath = "reading_room/hawaii_ctahr/south_pacific_island_foods.pdf"
        ),
        ReadingRoomDocument(
            id = "ctahr-papaya",
            title = "Papaya — Food Facts and Nutrition",
            category = "Hawaii CTAHR",
            description = "Nutritional content and food facts about papaya.",
            assetPath = "reading_room/hawaii_ctahr/papaya_food_facts.pdf"
        ),
        ReadingRoomDocument(
            id = "ctahr-native-landscapes",
            title = "Best Native Plants for Hawaiian Landscapes",
            category = "Hawaii CTAHR",
            description = "Guide to native Hawaiian plants suitable for landscape use.",
            assetPath = "reading_room/hawaii_ctahr/native_plants_for_landscapes.pdf"
        ),
        ReadingRoomDocument(
            id = "ctahr-native-commercial",
            title = "Commercial Use of Native Hawaiian Plants",
            category = "Hawaii CTAHR",
            description = "Overview of native Hawaiian plants with commercial and cultural value.",
            assetPath = "reading_room/hawaii_ctahr/native_hawaiian_plants_commercial.pdf"
        ),
        ReadingRoomDocument(
            id = "ctahr-native-booklet",
            title = "Native Hawaiian Plants Booklet",
            category = "Hawaii CTAHR",
            description = "Master Gardener guide to native Hawaiian plants.",
            assetPath = "reading_room/hawaii_ctahr/native_plant_booklet.pdf"
        ),

        // ── Food Preservation ───────────────────────────────────────────────
        ReadingRoomDocument(
            id = "fp-home-drying",
            title = "Home Drying of Food",
            category = "Food Preservation",
            description = "Comprehensive guide to home food drying and dehydration (UC ANR).",
            assetPath = "reading_room/food_preservation/home_drying_of_food.pdf"
        ),
        ReadingRoomDocument(
            id = "fp-drying-vegetables",
            title = "Drying Vegetables",
            category = "Food Preservation",
            description = "Methods and best practices for dehydrating vegetables at home.",
            assetPath = "reading_room/food_preservation/drying_vegetables.pdf"
        ),
        ReadingRoomDocument(
            id = "fp-drying-fruits",
            title = "Drying Fruits",
            category = "Food Preservation",
            description = "Methods and best practices for dehydrating fruits at home.",
            assetPath = "reading_room/food_preservation/drying_fruits.pdf"
        ),
        ReadingRoomDocument(
            id = "fp-freeze-drying",
            title = "Freeze Drying For Home Use",
            category = "Food Preservation",
            description = "Introduction to home freeze drying equipment and techniques.",
            assetPath = "reading_room/food_preservation/freeze_drying_home_use.pdf"
        ),
        ReadingRoomDocument(
            id = "fp-pickling",
            title = "Pickling — Vinegar and Fermentation",
            category = "Food Preservation",
            description = "Guide to pickling with vinegar and lacto-fermentation.",
            assetPath = "reading_room/food_preservation/pickling_vinegar_fermentation.pdf"
        ),
        ReadingRoomDocument(
            id = "fp-veg-fermentation",
            title = "Vegetable Fermentation",
            category = "Food Preservation",
            description = "Virginia Tech Extension guide to fermenting vegetables safely.",
            assetPath = "reading_room/food_preservation/vegetable_fermentation_vt.pdf"
        ),
        ReadingRoomDocument(
            id = "fp-home-freezing",
            title = "Home Freezing Guide",
            category = "Food Preservation",
            description = "Comprehensive guide to home freezing methods and best practices (UAF).",
            assetPath = "reading_room/food_preservation/home_freezing_guide.pdf"
        ),
        ReadingRoomDocument(
            id = "fp-freezing-vegetables",
            title = "Freezing Vegetables",
            category = "Food Preservation",
            description = "Home freezing methods for vegetables, including blanching guidelines.",
            assetPath = "reading_room/food_preservation/freezing_vegetables.pdf"
        ),
        ReadingRoomDocument(
            id = "fp-canning-basics",
            title = "Preserve It — Canning Basics",
            category = "Food Preservation",
            description = "UC ANR introduction to canning basics and food preservation fundamentals.",
            assetPath = "reading_room/food_preservation/canning_basics_ucce.pdf"
        ),

        // ── Foraging ────────────────────────────────────────────────────────
        ReadingRoomDocument(
            id = "foraging-northeast",
            title = "Edible Wild Plants of the Northeast US",
            category = "Foraging",
            description = "Edible wild plants native to the Northeast US and Eastern Canada.",
            assetPath = "reading_room/foraging/edible_wild_plants_northeast.pdf"
        ),
        ReadingRoomDocument(
            id = "foraging-ecola",
            title = "Edible Native Plants — Northeast & Eastern Canada",
            category = "Foraging",
            description = "Ecological Landscape Alliance guide to edible native plants of the Northeast.",
            assetPath = "reading_room/foraging/edible_native_plants_ecola.pdf"
        ),
        ReadingRoomDocument(
            id = "foraging-coastal-prairie",
            title = "Edible Wild Plants of the Coastal Prairie",
            category = "Foraging",
            description = "Texas extension guide to edible wild plants of the coastal prairie region.",
            assetPath = "reading_room/foraging/edible_coastal_prairie_plants.pdf"
        ),
    )
}
