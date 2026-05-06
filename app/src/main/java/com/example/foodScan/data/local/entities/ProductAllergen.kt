package com.example.foodScan.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "product_allergens",
    primaryKeys = ["productId", "allergenId"],
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AllergenEntity::class,
            parentColumns = ["id"],
            childColumns = ["allergenId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProductAllergenCrossRef(
    val productId: String,
    val allergenId: String
)