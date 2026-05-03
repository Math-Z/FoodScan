package com.example.foodScan.data.domain.model

enum class NutriScoreLetter { A, B, C, D, E }

data class NutriScore(
    val letter: NutriScoreLetter,
    val score: Int
)

// Catégorie du produit pour adapter les seuils
enum class NutriScoreCategory {
    GENERAL,
    BEVERAGE,
    WATER,
    CHEESE,
    FAT
}

fun resolveCategory(category: String?): NutriScoreCategory {
    if (category == null) return NutriScoreCategory.GENERAL
    val c = category.lowercase()
    return when {
        c.contains("eau") || c.contains("water") -> NutriScoreCategory.WATER
        c.contains("boisson") || c.contains("beverage") ||
                c.contains("jus") || c.contains("soda") ||
                c.contains("juice") || c.contains("drink") -> NutriScoreCategory.BEVERAGE
        c.contains("fromage") || c.contains("cheese") -> NutriScoreCategory.CHEESE
        c.contains("huile") || c.contains("oil") ||
                c.contains("beurre") || c.contains("butter") ||
                c.contains("margarine") -> NutriScoreCategory.FAT
        else -> NutriScoreCategory.GENERAL
    }
}

fun computeNutriScore(nutriments: Nutriments, category: String?): NutriScore {
    val cat = resolveCategory(category)

    // Eau — toujours A
    if (cat == NutriScoreCategory.WATER) return NutriScore(NutriScoreLetter.A, -15)

    val negativePoints = when (cat) {
        NutriScoreCategory.BEVERAGE -> computeNegativePointsBeverage(nutriments)
        else -> computeNegativePointsGeneral(nutriments)
    }

    val positivePoints = when (cat) {
        NutriScoreCategory.BEVERAGE -> computePositivePointsBeverage(nutriments)
        else -> computePositivePointsGeneral(nutriments)
    }

    val total = negativePoints - positivePoints

    val letter = when (cat) {
        NutriScoreCategory.CHEESE -> when {
            total <= -1 -> NutriScoreLetter.A
            total <= 2  -> NutriScoreLetter.B
            total <= 10 -> NutriScoreLetter.C
            total <= 18 -> NutriScoreLetter.D
            else        -> NutriScoreLetter.E
        }
        NutriScoreCategory.BEVERAGE -> when {
            total <= 1  -> NutriScoreLetter.B
            total <= 5  -> NutriScoreLetter.C
            total <= 9  -> NutriScoreLetter.D
            else        -> NutriScoreLetter.E
        }
        NutriScoreCategory.FAT -> when {
            total <= -6 -> NutriScoreLetter.A
            total <= 2  -> NutriScoreLetter.B
            total <= 10 -> NutriScoreLetter.C
            total <= 18 -> NutriScoreLetter.D
            else        -> NutriScoreLetter.E
        }
        else -> when {
            total <= -1 -> NutriScoreLetter.A
            total <= 2  -> NutriScoreLetter.B
            total <= 10 -> NutriScoreLetter.C
            total <= 18 -> NutriScoreLetter.D
            else        -> NutriScoreLetter.E
        }
    }

    return NutriScore(letter, total)
}

// --- Points négatifs ---

private fun computeNegativePointsGeneral(n: Nutriments): Int {
    return energyPoints(n.energyKj, ENERGY_THRESHOLDS_GENERAL) +
            saturatedFatPoints(n.saturatedFat, SATURATED_FAT_THRESHOLDS_GENERAL) +
            sugarPoints(n.sugar, SUGAR_THRESHOLDS_GENERAL) +
            saltPoints(n.salt, SALT_THRESHOLDS_GENERAL)
}

private fun computeNegativePointsBeverage(n: Nutriments): Int {
    return energyPoints(n.energyKj, ENERGY_THRESHOLDS_BEVERAGE) +
            saturatedFatPoints(n.saturatedFat, SATURATED_FAT_THRESHOLDS_GENERAL) +
            sugarPoints(n.sugar, SUGAR_THRESHOLDS_BEVERAGE) +
            saltPoints(n.salt, SALT_THRESHOLDS_GENERAL)
}

// --- Points positifs ---

private fun computePositivePointsGeneral(n: Nutriments): Int {
    return fiberPoints(n.fiber) +
            proteinPoints(n.protein, PROTEIN_THRESHOLDS_GENERAL) +
            fruitsPoints(n.fruitsVegetables, FRUITS_THRESHOLDS_GENERAL)
}

private fun computePositivePointsBeverage(n: Nutriments): Int {
    return fiberPoints(n.fiber) +
            proteinPoints(n.protein, PROTEIN_THRESHOLDS_GENERAL) +
            fruitsPoints(n.fruitsVegetables, FRUITS_THRESHOLDS_BEVERAGE)
}

// --- Helpers ---

private fun thresholdPoints(value: Double, thresholds: List<Double>): Int {
    var points = 0
    for (threshold in thresholds) {
        if (value > threshold) points++ else break
    }
    return points
}

private fun energyPoints(kj: Double, thresholds: List<Double>) = thresholdPoints(kj, thresholds)
private fun saturatedFatPoints(v: Double, t: List<Double>) = thresholdPoints(v, t)
private fun sugarPoints(v: Double, t: List<Double>) = thresholdPoints(v, t)
private fun saltPoints(v: Double, t: List<Double>) = thresholdPoints(v, t)
private fun fiberPoints(v: Double) = thresholdPoints(v, FIBER_THRESHOLDS)
private fun proteinPoints(v: Double, t: List<Double>) = thresholdPoints(v, t)
private fun fruitsPoints(v: Double, t: List<Double>) = thresholdPoints(v, t)

// --- Seuils officiels Nutri-Score 2023 ---

private val ENERGY_THRESHOLDS_GENERAL = listOf(335.0, 670.0, 1005.0, 1340.0, 1675.0, 2010.0, 2345.0, 2680.0, 3015.0, 3350.0)
private val ENERGY_THRESHOLDS_BEVERAGE = listOf(30.0, 60.0, 90.0, 120.0, 150.0, 180.0, 210.0, 240.0, 270.0, 300.0)

private val SATURATED_FAT_THRESHOLDS_GENERAL = listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)

private val SUGAR_THRESHOLDS_GENERAL  = listOf(3.4, 6.8, 10.2, 13.6, 17.0, 20.4, 23.8, 27.2, 30.6, 34.0)
private val SUGAR_THRESHOLDS_BEVERAGE = listOf(1.5, 3.0, 4.5, 6.0, 7.5, 9.0, 10.5, 12.0, 13.5, 15.0)

private val SALT_THRESHOLDS_GENERAL = listOf(0.2, 0.4, 0.6, 0.8, 1.0, 1.2, 1.4, 1.6, 1.8, 2.0)

private val FIBER_THRESHOLDS = listOf(0.9, 1.9, 2.8, 3.7, 4.7)

private val PROTEIN_THRESHOLDS_GENERAL = listOf(1.6, 3.2, 4.8, 6.4, 8.0)

private val FRUITS_THRESHOLDS_GENERAL  = listOf(40.0, 60.0, 80.0)
private val FRUITS_THRESHOLDS_BEVERAGE = listOf(40.0, 60.0, 80.0)