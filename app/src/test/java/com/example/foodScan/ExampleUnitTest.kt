package com.example.foodScan

import com.example.foodScan.data.domain.model.Nutriments
import com.example.foodScan.data.domain.model.NutriScoreLetter
import com.example.foodScan.data.domain.model.computeNutriScore
import org.junit.Assert.assertEquals
import org.junit.Test

class NutriScoreUnitTest {

    private fun nutriments(
        energy: Double = 0.0,
        energyKj: Double = 0.0,
        fat: Double = 0.0,
        saturatedFat: Double = 0.0,
        carbohydrates: Double = 0.0,
        sugar: Double = 0.0,
        protein: Double = 0.0,
        salt: Double = 0.0,
        fiber: Double = 0.0,
        sodium: Double = 0.0,
        fruitsVegetables: Double = 0.0
    ) = Nutriments(energy, energyKj, fat, saturatedFat, carbohydrates, sugar, protein, salt, fiber, sodium, fruitsVegetables)

    @Test
    fun `produit tres sucre doit etre E`() {
        val n = nutriments(energyKj = 3500.0, sugar = 60.0, saturatedFat = 10.0, salt = 2.0)
        assertEquals(NutriScoreLetter.E, computeNutriScore(n, null).letter)
    }

    @Test
    fun `produit sain doit etre A`() {
        val n = nutriments(energyKj = 200.0, sugar = 1.0, saturatedFat = 0.5, salt = 0.1, fiber = 5.0, protein = 8.0, fruitsVegetables = 90.0)
        assertEquals(NutriScoreLetter.A, computeNutriScore(n, null).letter)
    }

    @Test
    fun `eau doit toujours etre A`() {
        val n = nutriments(energyKj = 0.0)
        assertEquals(NutriScoreLetter.A, computeNutriScore(n, "eau minérale").letter)
    }

    @Test
    fun `boisson sucree doit etre D ou E`() {
        val n = nutriments(energyKj = 180.0, sugar = 10.0)
        val result = computeNutriScore(n, "boisson gazeuse").letter
        assert(result == NutriScoreLetter.D || result == NutriScoreLetter.E)
    }

    @Test
    fun `addition basique`() {
        assertEquals(4, 2 + 2)
    }
}