package com.example.gactour.ui.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MapMediaUploadPrompt(onCrossClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
//            .offset(y = -80.dp)
        , contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(Color.White)
                .border(1.dp, Color.Gray)
                .padding(8.dp)
        ) {
            Text(
                text = "Upload Media?",
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))  // Spacing between text and icons


            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Check Icon",
                    tint = Color.Green,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { /* Handle Right icon click here */ }
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Icon",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onCrossClicked() }
                )
            }
        }
    }
}