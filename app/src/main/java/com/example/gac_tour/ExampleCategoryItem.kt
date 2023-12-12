package com.example.gac_tour

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gac_tour.model.ExampleCategory

/**
 * Display the [ExampleCategory] as a [Card] in the UI.
 */
@Composable
internal fun ExampleCategoryItem(
  category: ExampleCategory,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier
      .padding(8.dp)
      .fillMaxSize(),
  ) {
    Text(text = category.category, style = typography.headlineSmall)
  }
}