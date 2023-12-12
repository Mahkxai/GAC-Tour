package com.example.gac_tour

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gac_tour.ui.theme.GACTourTheme
import com.example.gac_tour.data.ExamplesProvider
import com.example.gac_tour.model.ExampleCategory
import com.example.gac_tour.model.SpecificExample

/**
 * Activity shown when application is started
 *
 * This activity will generate data for RecyclerView based on the AndroidManifest entries.
 * It uses tags as category and description to order the different entries.
 */
public class ExampleOverviewActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      GACTourTheme {
        OverviewScreen {
          startActivity(Intent().withComponent(packageName, it.name))
        }
      }
    }
  }

  private fun Intent.withComponent(packageName: String, exampleName: String): Intent {
    component = ComponentName(packageName, exampleName)
    return this
  }

  @Composable
  public fun OverviewScreen(navigateToExample: (SpecificExample) -> Unit) {
    val context = LocalContext.current.applicationContext
    val examples =
      if (LocalInspectionMode.current) {
        remember { ExamplesProvider.loadMockExampleEntries() }
      } else {
        remember { ExamplesProvider.loadExampleEntries(context) }
      }
    ExampleScaffold {
      LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
        items(
          items = examples,
          itemContent = {
            when (it) {
              is SpecificExample -> ExampleListItem(
                specificExample = it,
                navigateToExample = navigateToExample
              )
              is ExampleCategory -> ExampleCategoryItem(category = it)
            }
          }
        )
      }
    }
  }

  @Preview("Light Theme", widthDp = 360, heightDp = 640)
  @Composable
  public fun LightPreview() {
    GACTourTheme(darkTheme = false) {
      OverviewScreen { }
    }
  }

  @Preview("Dark Theme", widthDp = 360, heightDp = 640)
  @Composable
  public fun DarkPreview() {
    GACTourTheme(darkTheme = true) {
      OverviewScreen { }
    }
  }
}