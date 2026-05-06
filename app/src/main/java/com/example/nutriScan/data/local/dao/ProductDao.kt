package com.example.nutriScan.data.local.dao

import androidx.room.*
import com.example.nutriScan.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutriments(nutriments: NutrientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllergen(allergen: AllergenEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductAllergenCrossRef(crossRef: ProductAllergenCrossRef)

    @Transaction
    @Query("SELECT * FROM products WHERE id = :barcode")
    suspend fun getProductWithDetails(barcode: String): FullProduct?

    @Transaction
    @Query("SELECT * FROM products")
    fun getAllProductsWithDetails(): Flow<List<FullProduct>>

    @Query("UPDATE products SET isFavorite = NOT isFavorite WHERE id = :barcode")
    suspend fun toggleFavorite(barcode: String)

    @Query("DELETE FROM products WHERE id = :barcode")
    suspend fun deleteProduct(barcode: String)
}

data class FullProduct(
    @Embedded val product: ProductEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    )
    val nutriments: NutrientEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ProductAllergenCrossRef::class,
            parentColumn = "productId",
            entityColumn = "allergenId"
        )
    )
    val allergens: List<AllergenEntity>
)