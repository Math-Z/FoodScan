package com.example.foodScan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodScan.ui.components.BarcodeInputDialog
import com.example.foodScan.ui.components.FilterRow
import com.example.foodScan.ui.components.ProductCard
import com.example.foodScan.ui.components.ProductDetailSheet
import com.example.foodScan.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: ProductViewModel, modifier: Modifier = Modifier) {

    val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val availableAllergens by viewModel.availableAllergens.collectAsStateWithLifecycle()
    val availableCategories by viewModel.availableCategories.collectAsStateWithLifecycle()
    val showFavoritesOnly by viewModel.showFavoritesOnly.collectAsStateWithLifecycle()
    val excludedAllergens by viewModel.excludedAllergens.collectAsStateWithLifecycle()
    val selectedCategories by viewModel.selectedCategories.collectAsStateWithLifecycle()
    val selectedBarcodes by viewModel.selectedBarcodes.collectAsStateWithLifecycle()
    val isSelectionMode by viewModel.isSelectionMode.collectAsStateWithLifecycle()

    var showBarcodeDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<com.example.foodScan.data.domain.model.Product?>(null) }
    val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            if (!isSelectionMode) {
                FloatingActionButton(onClick = { showBarcodeDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Saisir un code-barres")
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {

            // Header — switches between title and selection toolbar
            if (isSelectionMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { viewModel.clearSelection() }) {
                        Text("Annuler")
                    }
                    Text(
                        text = "${selectedBarcodes.size} sélectionné(s)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(
                        onClick = { viewModel.deleteSelected() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Supprimer")
                    }
                }
            } else {
                Text(
                    text = "Produits scannés",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
                )
            }

            // Filters — hidden during selection mode
            if (!isSelectionMode) {
                FilterRow(
                    showFavoritesOnly = showFavoritesOnly,
                    excludedAllergens = excludedAllergens,
                    selectedCategories = selectedCategories,
                    availableAllergens = availableAllergens,
                    availableCategories = availableCategories,
                    onToggleFavorites = { viewModel.toggleFavoritesFilter() },
                    onToggleAllergen = { viewModel.toggleAllergenExclusion(it) },
                    onClearAllergens = { viewModel.clearAllergenFilters() },
                    onToggleCategory = { viewModel.toggleCategorySelection(it) },
                    onClearCategories = { viewModel.clearCategoryFilters() },
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Product list
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSelectionMode) "" else "Aucun produit trouvé",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(products, key = { it.barcode }) { product ->
                        ProductCard(
                            product = product,
                            isSelected = product.barcode in selectedBarcodes,
                            isSelectionMode = isSelectionMode,
                            onToggleFavorite = { viewModel.toggleFavorite(product.barcode) },
                            onClick = {
                                if (isSelectionMode) {
                                    viewModel.toggleSelection(product.barcode)
                                } else {
                                    selectedProduct = product
                                    scope.launch { detailSheetState.show() }
                                }
                            },
                            onLongClick = {
                                viewModel.toggleSelection(product.barcode)
                            }
                        )
                    }
                }
            }
        }

        // Barcode dialog
        if (showBarcodeDialog) {
            BarcodeInputDialog(
                onConfirm = { barcode ->
                    viewModel.loadProduct(barcode)
                    showBarcodeDialog = false
                },
                onDismiss = { showBarcodeDialog = false }
            )
        }

        // Detail sheet
        selectedProduct?.let { product ->
            ModalBottomSheet(
                onDismissRequest = { selectedProduct = null },
                sheetState = detailSheetState
            ) {
                ProductDetailSheet(
                    product = product,
                    onToggleFavorite = {
                        viewModel.toggleFavorite(product.barcode)
                        selectedProduct = products.find { it.barcode == product.barcode }
                    }
                )
            }
        }
    }
}