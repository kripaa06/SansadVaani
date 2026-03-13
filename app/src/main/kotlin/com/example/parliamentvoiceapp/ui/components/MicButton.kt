package com.example.parliamentvoiceapp.ui.components

import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons

@Composable
fun MicButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Mic,
            contentDescription = "Microphone Button",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}