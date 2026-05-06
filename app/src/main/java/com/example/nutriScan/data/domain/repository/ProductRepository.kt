package com.example.nutriScan.data.domain.repository

import com.example.nutriScan.data.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProduct(barcode: String): Product?
    fun getAllProducts(): Flow<List<Product>>
    suspend fun toggleFavorite(barcode: String)
    suspend fun deleteProduct(barcode: String)
}