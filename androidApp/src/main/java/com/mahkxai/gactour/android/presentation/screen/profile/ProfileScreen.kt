package com.mahkxai.gactour.android.presentation.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mahkxai.gactour.android.presentation.navigation.BottomBarNavGraph
import com.ramcosta.composedestinations.annotation.Destination

@BottomBarNavGraph
@Destination
@Composable
fun ProfileScreen() {
    ProfileScreenContent()
}

@Composable
fun ProfileScreenContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = Icons.Default.Construction, contentDescription = "Work in Progress")
        Text("Feature Work In Progress")
    }
}

