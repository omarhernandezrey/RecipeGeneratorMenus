package com.example.recipe_generator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.recipe_generator.data.Recipe
import com.example.recipe_generator.ui.theme.Primary
import com.example.recipe_generator.ui.theme.Secondary

@Composable
fun EditorialRecipeCard(recipe: Recipe, onFavoriteClick: (String) -> Unit = {}) {
    var isFavorite by remember { mutableStateOf(recipe.isFavorite) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Image Section with Favorite Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color.Gray.copy(alpha = 0.3f))
            ) {
                AsyncImage(
                    model = recipe.imageUrl,
                    contentDescription = recipe.title,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Favorite Button
                Button(
                    onClick = {
                        isFavorite = !isFavorite
                        onFavoriteClick(recipe.id)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(48.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            // Content Section
            Column(
                modifier = Modifier.padding(32.dp)
            ) {
                // Title
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1B1B1E),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Info Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Time Chip
                    Row(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFF5F3F7),
                                shape = RoundedCornerShape(50.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Time",
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "${recipe.timeInMinutes} min",
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    // Calories Chip
                    Row(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFF5F3F7),
                                shape = RoundedCornerShape(50.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Calories",
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "${recipe.calories} Cal",
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    // Difficulty Chip
                    Surface(
                        modifier = Modifier
                            .background(
                                color = if (recipe.difficulty == "Fácil") Secondary else Color(0xFFCBC3D9).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(50.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        color = Color.Transparent
                    ) {
                        Text(
                            text = recipe.difficulty,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (recipe.difficulty == "Fácil") Color(0xFF006F64) else Color(0xFF494456)
                        )
                    }
                }
            }
        }
    }
}











