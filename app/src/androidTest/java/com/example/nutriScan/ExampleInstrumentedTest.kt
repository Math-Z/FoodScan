package com.example.nutriScan

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nutriScan.data.local.AppDatabase
import com.example.nutriScan.data.local.dao.ProductDao
import com.example.nutriScan.data.local.entities.ProductEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDaoInstrumentedTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: ProductDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Base de données en mémoire — ne persiste pas, idéale pour les tests
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.productDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun inserterEtRecupererUnProduit() = runBlocking {
        val product = ProductEntity(
            id = "3017620422003",
            name = "Nutella",
            imageUrl = null,
            category = "Pâtes à tartiner",
            isFavorite = false
        )
        dao.insertProduct(product)
        val result = dao.getProductWithDetails("3017620422003")
        assertNotNull(result)
        assertEquals("Nutella", result?.product?.name)
    }

    @Test
    fun supprimerUnProduit() = runBlocking {
        val product = ProductEntity(
            id = "123456789",
            name = "Test",
            imageUrl = null,
            category = null,
            isFavorite = false
        )
        dao.insertProduct(product)
        dao.deleteProduct("123456789")
        val result = dao.getProductWithDetails("123456789")
        assertNull(result)
    }

    @Test
    fun toggleFavoriBasculeLEtat() = runBlocking {
        val product = ProductEntity(
            id = "987654321",
            name = "Produit test",
            imageUrl = null,
            category = null,
            isFavorite = false
        )
        dao.insertProduct(product)
        dao.toggleFavorite("987654321")
        val result = dao.getProductWithDetails("987654321")
        assertTrue(result?.product?.isFavorite == true)
    }

    @Test
    fun listeProduitsEstVideInitialement() = runBlocking {
        val products = dao.getAllProductsWithDetails().first()
        assertTrue(products.isEmpty())
    }
}