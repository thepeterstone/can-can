package org.terst.cancan.library

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository @Inject constructor() {

    val documents: List<LibraryDocument> = listOf(
        // ── USDA Canning Guide ──────────────────────────────────────────────
        LibraryDocument(
            id = "usda-intro",
            title = "Introduction — Principles of Home Canning",
            category = "USDA Canning Guide",
            description = "Overview of safe home canning practices, equipment, and basic procedures.",
            assetPath = "library/usda_canning_guide/INTRO_principles.pdf"
        ),
        LibraryDocument(
            id = "usda-guide-01",
            title = "Guide 1: Principles of Home Canning",
            category = "USDA Canning Guide",
            description = "Science and safety principles behind the USDA home canning guidelines.",
            assetPath = "library/usda_canning_guide/GUIDE01_principles.pdf"
        ),
        LibraryDocument(
            id = "usda-guide-02",
            title = "Guide 2: Canning Fruit and Fruit Products",
            category = "USDA Canning Guide",
            description = "Selecting, preparing, and canning fruit and fruit products.",
            assetPath = "library/usda_canning_guide/GUIDE02_fruits.pdf"
        ),
        LibraryDocument(
            id = "usda-guide-03",
            title = "Guide 3: Canning Tomatoes",
            category = "USDA Canning Guide",
            description = "Selecting, preparing, and canning tomatoes and tomato products.",
            assetPath = "library/usda_canning_guide/GUIDE03_tomatoes.pdf"
        ),
        LibraryDocument(
            id = "usda-guide-04",
            title = "Guide 4: Canning Vegetables",
            category = "USDA Canning Guide",
            description = "Selecting, preparing, and pressure canning vegetables.",
            assetPath = "library/usda_canning_guide/GUIDE04_vegetables.pdf"
        ),
        LibraryDocument(
            id = "usda-guide-05",
            title = "Guide 5: Canning Poultry, Meats, and Seafoods",
            category = "USDA Canning Guide",
            description = "Preparing and canning poultry, red meats, and seafoods.",
            assetPath = "library/usda_canning_guide/GUIDE05_meats.pdf"
        ),
        LibraryDocument(
            id = "usda-guide-06",
            title = "Guide 6: Fermented Foods and Pickled Vegetables",
            category = "USDA Canning Guide",
            description = "Preparing and canning fermented foods and pickled vegetables.",
            assetPath = "library/usda_canning_guide/GUIDE06_fermented_pickles.pdf"
        ),
        LibraryDocument(
            id = "usda-guide-07",
            title = "Guide 7: Preparing and Canning Jams and Jellies",
            category = "USDA Canning Guide",
            description = "Preparing and canning jams, jellies, and other sweet spreads.",
            assetPath = "library/usda_canning_guide/GUIDE07_jams_jellies.pdf"
        ),

        // ── NCHFP Fact Sheets ───────────────────────────────────────────────
        LibraryDocument(
            id = "nchfp-boiling-water",
            title = "Using Boiling Water Canners",
            category = "NCHFP",
            description = "NCHFP fact sheet on using boiling water canners safely.",
            assetPath = "library/nchfp_factsheets/using_boiling_water_canners.pdf"
        ),
        LibraryDocument(
            id = "nchfp-pressure",
            title = "Using Pressure Canners",
            category = "NCHFP",
            description = "NCHFP fact sheet on using pressure canners safely.",
            assetPath = "library/nchfp_factsheets/using_pressure_canners.pdf"
        ),
        LibraryDocument(
            id = "nchfp-water",
            title = "Home Canning Water",
            category = "NCHFP",
            description = "Water quality considerations for home canning.",
            assetPath = "library/nchfp_factsheets/home_canning_water.pdf"
        ),

        // ── Hawaii CTAHR ────────────────────────────────────────────────────
        LibraryDocument(
            id = "ctahr-edible-plants",
            title = "Edible Plants for Hawai'i Landscapes",
            category = "Hawaii CTAHR",
            description = "Taro, passion fruit, bamboo, and other edible landscape plants in Hawai'i.",
            assetPath = "library/hawaii_ctahr/edible_plants_hawaii_landscapes.pdf"
        ),
        LibraryDocument(
            id = "ctahr-breadfruit",
            title = "Hawaiian Breadfruit — Ethnobotany and Nutrition",
            category = "Hawaii CTAHR",
            description = "Ethnobotany, nutrition, and human ecology of Hawaiian breadfruit.",
            assetPath = "library/hawaii_ctahr/breadfruit_ethnobotany_nutrition.pdf"
        ),
        LibraryDocument(
            id = "ctahr-ulu-postharvest",
            title = "'Ulu (Breadfruit) — Postharvest Handling",
            category = "Hawaii CTAHR",
            description = "'Ulu postharvest handling and nutrition facts.",
            assetPath = "library/hawaii_ctahr/ulu_breadfruit_postharvest.pdf"
        ),
        LibraryDocument(
            id = "ctahr-ten-tropical",
            title = "Ten Tropical Fruits of Potential Value",
            category = "Hawaii CTAHR",
            description = "Guava, sapodilla, and other tropical fruits with commercial potential.",
            assetPath = "library/hawaii_ctahr/ten_tropical_fruits.pdf"
        ),
        LibraryDocument(
            id = "ctahr-twelve-tropical",
            title = "Twelve Fruits With Culinary Uses",
            category = "Hawaii CTAHR",
            description = "Twelve tropical fruits with potential value-added and culinary applications.",
            assetPath = "library/hawaii_ctahr/twelve_tropical_fruits_culinary.pdf"
        ),
        LibraryDocument(
            id = "ctahr-south-pacific",
            title = "South Pacific Island Foods",
            category = "Hawaii CTAHR",
            description = "Description, history, and nutritive value of tropical South Pacific island foods.",
            assetPath = "library/hawaii_ctahr/south_pacific_island_foods.pdf"
        ),
        LibraryDocument(
            id = "ctahr-papaya",
            title = "Papaya — Food Facts and Nutrition",
            category = "Hawaii CTAHR",
            description = "Nutritional content and food facts about papaya.",
            assetPath = "library/hawaii_ctahr/papaya_food_facts.pdf"
        ),
        LibraryDocument(
            id = "ctahr-native-landscapes",
            title = "Best Native Plants for Hawaiian Landscapes",
            category = "Hawaii CTAHR",
            description = "Guide to native Hawaiian plants suitable for landscape use.",
            assetPath = "library/hawaii_ctahr/native_plants_for_landscapes.pdf"
        ),
        LibraryDocument(
            id = "ctahr-native-commercial",
            title = "Commercial Use of Native Hawaiian Plants",
            category = "Hawaii CTAHR",
            description = "Overview of native Hawaiian plants with commercial and cultural value.",
            assetPath = "library/hawaii_ctahr/native_hawaiian_plants_commercial.pdf"
        ),
        LibraryDocument(
            id = "ctahr-native-booklet",
            title = "Native Hawaiian Plants Booklet",
            category = "Hawaii CTAHR",
            description = "Master Gardener guide to native Hawaiian plants.",
            assetPath = "library/hawaii_ctahr/native_plant_booklet.pdf"
        ),

        // ── Food Preservation ───────────────────────────────────────────────
        LibraryDocument(
            id = "fp-home-drying",
            title = "Home Drying of Food",
            category = "Food Preservation",
            description = "Comprehensive guide to home food drying and dehydration (UC ANR).",
            assetPath = "library/food_preservation/home_drying_of_food.pdf"
        ),
        LibraryDocument(
            id = "fp-drying-vegetables",
            title = "Drying Vegetables",
            category = "Food Preservation",
            description = "Methods and best practices for dehydrating vegetables at home.",
            assetPath = "library/food_preservation/drying_vegetables.pdf"
        ),
        LibraryDocument(
            id = "fp-drying-fruits",
            title = "Drying Fruits",
            category = "Food Preservation",
            description = "Methods and best practices for dehydrating fruits at home.",
            assetPath = "library/food_preservation/drying_fruits.pdf"
        ),
        LibraryDocument(
            id = "fp-freeze-drying",
            title = "Freeze Drying For Home Use",
            category = "Food Preservation",
            description = "Introduction to home freeze drying equipment and techniques.",
            assetPath = "library/food_preservation/freeze_drying_home_use.pdf"
        ),
        LibraryDocument(
            id = "fp-pickling",
            title = "Pickling — Vinegar and Fermentation",
            category = "Food Preservation",
            description = "Guide to pickling with vinegar and lacto-fermentation.",
            assetPath = "library/food_preservation/pickling_vinegar_fermentation.pdf"
        ),
        LibraryDocument(
            id = "fp-veg-fermentation",
            title = "Vegetable Fermentation",
            category = "Food Preservation",
            description = "Virginia Tech Extension guide to fermenting vegetables safely.",
            assetPath = "library/food_preservation/vegetable_fermentation_vt.pdf"
        ),
        LibraryDocument(
            id = "fp-home-freezing",
            title = "Home Freezing Guide",
            category = "Food Preservation",
            description = "Comprehensive guide to home freezing methods and best practices (UAF).",
            assetPath = "library/food_preservation/home_freezing_guide.pdf"
        ),
        LibraryDocument(
            id = "fp-freezing-vegetables",
            title = "Freezing Vegetables",
            category = "Food Preservation",
            description = "Home freezing methods for vegetables, including blanching guidelines.",
            assetPath = "library/food_preservation/freezing_vegetables.pdf"
        ),
        LibraryDocument(
            id = "fp-canning-basics",
            title = "Preserve It — Canning Basics",
            category = "Food Preservation",
            description = "UC ANR introduction to canning basics and food preservation fundamentals.",
            assetPath = "library/food_preservation/canning_basics_ucce.pdf"
        ),

        // ── Hawaii Fishing ──────────────────────────────────────────────────
        LibraryDocument(
            id = "hawaii-fishes",
            title = "Fishes of Hawai'i",
            category = "Hawaii Fishing",
            description = "DLNR DAR illustrated guide to Hawaii's nearshore and reef fish species.",
            assetPath = "library/hawaii_fishing/fishes_of_hawaii.pdf"
        ),
        LibraryDocument(
            id = "hawaii-fishing-guide",
            title = "Fishing in Hawai'i",
            category = "Hawaii Fishing",
            description = "DLNR DAR recreational fishing guide covering methods, species, and regulations.",
            assetPath = "library/hawaii_fishing/fishing_in_hawaii.pdf"
        ),
        LibraryDocument(
            id = "hawaii-fishing-regs-2025",
            title = "Hawaii Fishing Regulations — May 2025",
            category = "Hawaii Fishing",
            description = "Current DLNR DAR freshwater and marine fishing regulations effective May 2025.",
            assetPath = "library/hawaii_fishing/fishing_regs_may_2025.pdf"
        ),

        // ── Foraging ────────────────────────────────────────────────────────
        LibraryDocument(
            id = "foraging-northeast",
            title = "Edible Wild Plants of the Northeast US",
            category = "Foraging",
            description = "Edible wild plants native to the Northeast US and Eastern Canada.",
            assetPath = "library/foraging/edible_wild_plants_northeast.pdf"
        ),
        LibraryDocument(
            id = "foraging-ecola",
            title = "Edible Native Plants — Northeast & Eastern Canada",
            category = "Foraging",
            description = "Ecological Landscape Alliance guide to edible native plants of the Northeast.",
            assetPath = "library/foraging/edible_native_plants_ecola.pdf"
        ),
        LibraryDocument(
            id = "foraging-coastal-prairie",
            title = "Edible Wild Plants of the Coastal Prairie",
            category = "Foraging",
            description = "Texas extension guide to edible wild plants of the coastal prairie region.",
            assetPath = "library/foraging/edible_coastal_prairie_plants.pdf"
        ),
    )
}
