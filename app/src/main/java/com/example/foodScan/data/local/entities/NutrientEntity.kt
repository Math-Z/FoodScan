package com.example.foodScan.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "nutrients",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class NutrientEntity(
    @PrimaryKey val productId: String,
    val energy: Double?,
    val energyKj: Double?,
    val fat: Double?,
    val saturatedFat: Double?,
    val carbohydrates: Double?,
    val sugar: Double?,
    val protein: Double?,
    val salt: Double?,
    val fiber: Double?,
    val sodium: Double?,
    val fruitsVegetables: Double?
)