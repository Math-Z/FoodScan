package com.example.foodScan.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterRow(
    showFavoritesOnly: Boolean,
    excludedAllergens: Set<String>,
    selectedCategories: Set<String>,
    availableAllergens: List<String>,
    availableCategories: List<String>,
    onToggleFavorites: () -> Unit,
    onToggleAllergen: (String) -> Unit,
    onClearAllergens: () -> Unit,
    onToggleCategory: (String) -> Unit,
    onClearCategories: () -> Unit,
    modifier: Modifier = Modifier
) {
    var allergenExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        FilterChip(
            selected = showFavoritesOnly,
            onClick = onToggleFavorites,
            label = { Text("Favoris") },
            leadingIcon = {
                Icon(
                    imageVector = if (showFavoritesOnly) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        )

        Box {
            FilterChip(
                selected = excludedAllergens.isNotEmpty(),
                onClick = { allergenExpanded = true },
                label = {
                    Text(
                        if (excludedAllergens.isEmpty()) "Allergènes"
                        else "Allergènes (${excludedAllergens.size})"
                    )
                },
                trailingIcon = {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            )
            DropdownMenu(
                expanded = allergenExpanded,
                onDismissRequest = { allergenExpanded = false }
            ) {
                if (availableAllergens.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Aucun allergène trouvé", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        onClick = {}
                    )
                } else {
                    if (excludedAllergens.isNotEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Tout effacer", color = MaterialTheme.colorScheme.error) },
                            onClick = { onClearAllergens(); allergenExpanded = false }
                        )
                        HorizontalDivider()
                    }
                    availableAllergens.forEach { allergen ->
                        DropdownMenuItem(
                            text = { Text(allergen) },
                            onClick = { onToggleAllergen(allergen) },
                            trailingIcon = {
                                Checkbox(checked = allergen in excludedAllergens, onCheckedChange = null)
                            }
                        )
                    }
                }
            }
        }

        Box {
            FilterChip(
                selected = selectedCategories.isNotEmpty(),
                onClick = { categoryExpanded = true },
                label = {
                    Text(
                        if (selectedCategories.isEmpty()) "Catégories"
                        else "Catégories (${selectedCategories.size})"
                    )
                },
                trailingIcon = {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            )
            DropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                if (availableCategories.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Aucune catégorie trouvée", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        onClick = {}
                    )
                } else {
                    if (selectedCategories.isNotEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Tout effacer", color = MaterialTheme.colorScheme.error) },
                            onClick = { onClearCategories(); categoryExpanded = false }
                        )
                        HorizontalDivider()
                    }
                    availableCategories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = { onToggleCategory(category) },
                            trailingIcon = {
                                Checkbox(checked = category in selectedCategories, onCheckedChange = null)
                            }
                        )
                    }
                }
            }
        }
    }
}