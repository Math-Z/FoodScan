package com.example.foodScan.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.foodScan.data.domain.model.Product
import com.example.foodScan.data.domain.model.computeNutriScore
import com.example.foodScan.data.domain.model.NutriScoreLetter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductCard(
    product: Product,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val nutriLetter = product.nutriments?.let {
        computeNutriScore(it, product.category).letter
    }

    val scoreColor = nutriLetter?.let { nutriScoreColor(it) } ?: MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 8.dp else 2.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // remove .height(IntrinsicSize.Min)
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(80.dp) // replace .fillMaxHeight()
                    .background(scoreColor)
            )

            // Product image or selection icon
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isSelectionMode) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.size(32.dp)
                    )
                } else if (product.imageUrl != null) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                } else {
                    // Placeholder when no image
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = product.name.first().uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Product info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                product.category?.let {
                    Text(
                        text = it.split(",").first().trim(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (product.allergens.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(product.allergens.take(3)) { allergen ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.errorContainer)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = allergen,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // Right side — Nutri-Score circle + favorite
            Column(
                modifier = Modifier.padding(end = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                nutriLetter?.let { letter ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(scoreColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter.name,
                            color = nutriScoreForeground(letter),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }
                }
                if (!isSelectionMode) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (product.isFavorite)
                                Icons.Filled.Favorite
                            else
                                Icons.Filled.FavoriteBorder,
                            contentDescription = "Favori",
                            tint = if (product.isFavorite)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

fun nutriScoreColor(letter: NutriScoreLetter): Color = when (letter) {
    NutriScoreLetter.A -> Color(0xFF038141)
    NutriScoreLetter.B -> Color(0xFF85BB2F)
    NutriScoreLetter.C -> Color(0xFFFECC02)
    NutriScoreLetter.D -> Color(0xFFEE8100)
    NutriScoreLetter.E -> Color(0xFFE63E11)
}

fun nutriScoreForeground(letter: NutriScoreLetter): Color = when (letter) {
    NutriScoreLetter.C -> Color.Black
    else -> Color.White
}