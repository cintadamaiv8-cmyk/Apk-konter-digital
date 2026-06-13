package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun SplashScreen(onNavigateToDashboard: () -> Unit) {
    var progress by remember { mutableStateOf(0) }
    var systemReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val totalTime = 2500L
        val interval = 50L
        val steps = totalTime / interval
        for (i in 1..steps) {
            delay(interval)
            progress = ((i.toFloat() / steps.toFloat()) * 100).toInt()
        }
        progress = 100
        systemReady = true
        delay(1000) // Show "System Ready" briefly
        onNavigateToDashboard()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = com.example.R.drawable.img_app_logo_1781377515277),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "SHINFOX STORE ULTRA X",
                style = MaterialTheme.typography.headlineMedium,
                color = CosmicPurple
            )
            Spacer(modifier = Modifier.height(32.dp))
            if (systemReady) {
                Text(
                    text = "System Ready",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SuccessGreen
                )
            } else {
                Text(
                    text = "Loading System...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = StarlightSilver
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$progress%",
                    style = MaterialTheme.typography.headlineSmall,
                    color = NebulaCyan
                )
            }
        }
    }
}
