package com.example.parliamentvoiceapp.ui.screens

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parliamentvoiceapp.ui.components.*
import com.example.parliamentvoiceapp.ui.theme.*
import com.example.parliamentvoiceapp.viewmodel.VoiceViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
class HomeScreen : ComponentActivity() {
    private val viewModel: VoiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParliamentAppTheme {
                HomeScreenContent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreenContent(viewModel: VoiceViewModel) {
    val micPerm = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val isListening by viewModel.isListening.observeAsState(false)
    val recognizedText by viewModel.correctedText.observeAsState("")
    val rmsDb by viewModel.rmsDb.observeAsState(0f)
    val chatHistoryState = viewModel.chatHistory.observeAsState(emptyList<VoiceViewModel.ChatMessage>())
    val chatHistory = (chatHistoryState.value as? List<VoiceViewModel.ChatMessage>) ?: emptyList()

    LaunchedEffect(Unit) {
        if (!micPerm.status.isGranted) {
            micPerm.launchPermissionRequest()
        }
    }

    if (!micPerm.status.isGranted) {
        PermissionScreen { micPerm.launchPermissionRequest() }
    } else {
        MainChatContainer(
            chatHistory = chatHistory,
            isListening = isListening,
            recognizedText = recognizedText,
            rmsDb = rmsDb,
            onTextChanged = { viewModel.updateText(it) },
            onMicClicked = {
                if (isListening) viewModel.stopListening()
                else viewModel.startListening()
            },
            onSendClicked = {
                if (recognizedText.isNotBlank()) {
                    if (isListening) viewModel.stopListening()
                    viewModel.submitQuery(recognizedText)
                }
            },
            onClearChat = { viewModel.clearChat() }
        )
    }
}

@Composable
private fun MainChatContainer(
    chatHistory: List<VoiceViewModel.ChatMessage>,
    isListening: Boolean,
    recognizedText: String,
    rmsDb: Float,
    onTextChanged: (String) -> Unit,
    onMicClicked: () -> Unit,
    onSendClicked: () -> Unit,
    onClearChat: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepNavy, DarkBlack, DarkBlack)
                )
            )
    ) {
        // Deep background glow adapting to app state
        val bgGlowColor by animateColorAsState(
            targetValue = if (isListening) SaffronOrange.copy(alpha = 0.05f) else MicIdleBlue.copy(alpha = 0.03f),
            animationSpec = tween(1200), label = "bg_glow"
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.radialGradient(listOf(bgGlowColor, Color.Transparent), radius = 1000f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Header Content
            ChatHeader(
                showBack = chatHistory.isNotEmpty(),
                onBackClicked = onClearChat
            )

            // Dynamic Content Area (transitions from empty landing page to chat timeline)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Crossfade(targetState = chatHistory.isEmpty(), animationSpec = tween(600), label = "content") { isEmpty ->
                    if (isEmpty) {
                        LandingScreenView(isListening = isListening, rmsDb = rmsDb)
                    } else {
                        ChatTimelineView(chatHistory = chatHistory)
                    }
                }
            }

            // Central Wave Animation placed right above the input bar when listening
            AnimatedVisibility(
                visible = isListening,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                WaveAnimation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .padding(horizontal = 24.dp),
                    isActive = true,
                    waveColor = SaffronOrange,
                    amplitude = 20f
                )
            }
            
            Spacer(Modifier.height(8.dp))

            // ChatGPT-Style Input Bar
            InputBar(
                text = recognizedText,
                isListening = isListening,
                onTextChanged = onTextChanged,
                onMicClicked = onMicClicked,
                onSendClicked = onSendClicked
            )
        }
    }
}

@Composable
private fun ChatHeader(showBack: Boolean, onBackClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (showBack) {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextSecondary
                )
            }
        }
        Text(
            text = "Parliament Voice",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            ),
            color = TextPrimary
        )
    }
}

@Composable
private fun LandingScreenView(isListening: Boolean, rmsDb: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        // ── Background decorative circles ─────────────────────────────────────
        Box(
            modifier = Modifier
                .offset(x = (-60).dp, y = 80.dp)
                .size(260.dp)
                .alpha(0.08f)
                .background(
                    brush = Brush.radialGradient(listOf(SaffronOrange, Color.Transparent)),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-20).dp)
                .size(200.dp)
                .alpha(0.07f)
                .background(
                    brush = Brush.radialGradient(listOf(MicIdleBlue, Color.Transparent)),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Hero tagline ──────────────────────────────────────────────
            Text(
                text = "Your Voice,",
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Light),
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Parliament's Record.",
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "AI-powered voice transcription for Parliament proceedings",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            // ── Preview VoiceOrb (idle/active) ────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {
                VoiceOrb(isListening = isListening, rmsDb = rmsDb, modifier = Modifier.fillMaxSize())
                if (!isListening) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "TAP TO SPEAK", style = MaterialTheme.typography.labelSmall, color = TextMuted, letterSpacing = 2.sp)
                    }
                }
            }

            Spacer(Modifier.height(36.dp))

            // ── Feature cards row ─────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureCard(emoji = "🎙️", label = "Voice Queries", modifier = Modifier.weight(1f))
                FeatureCard(emoji = "📜", label = "Bill Summaries", modifier = Modifier.weight(1f))
                FeatureCard(emoji = "🤖", label = "AI Assistant", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun FeatureCard(emoji: String, label: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(shape)
            .background(GlassSurface)
            .border(1.dp, GlassBorder, shape)
            .padding(vertical = 16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, fontSize = 22.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ChatTimelineView(chatHistory: List<VoiceViewModel.ChatMessage>) {
    val listState = rememberLazyListState()
    
    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(chatHistory) { message ->
            ChatBubble(message = message)
        }
    }
}

@Composable
private fun ChatBubble(message: VoiceViewModel.ChatMessage) {
    val isUser = message.isUser
    val bubbleColor = if (isUser) SaffronOrange else GlassSurface
    val textColor = if (isUser) DarkBlack else TextPrimary

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // AI Parliament Avatar
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(GoldYellow, SaffronOrange))),
                contentAlignment = Alignment.Center
            ) {
                Text("🏛", fontSize = 18.sp)
            }
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                .background(bubbleColor)
                .then(if (!isUser) Modifier.border(1.dp, GlassBorder, RoundedCornerShape(
                    topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp
                )) else Modifier)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                color = textColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputBar(
    text: String,
    isListening: Boolean,
    onTextChanged: (String) -> Unit,
    onMicClicked: () -> Unit,
    onSendClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .imePadding()
            .background(GlassSurface, RoundedCornerShape(32.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(32.dp))
            .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChanged,
            modifier = Modifier.weight(1f).padding(bottom = 2.dp),
            placeholder = {
                Text(
                    text = "Message...",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = SaffronOrange,
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary)
        )

        Spacer(Modifier.width(8.dp))

        // Dynamic Action Button (Send if text exists, else Mic)
        val hasText = text.isNotBlank()
        
        val buttonColor by animateColorAsState(
            targetValue = when {
                hasText -> GoldYellow
                isListening -> Color(0xFFE53935)
                else -> NavyBlue
            }, label = "btnColor"
        )
        
        val iconColor by animateColorAsState(
            targetValue = if (hasText) DarkBlack else Color.White, label = "iconColor"
        )

        FloatingActionButton(
            onClick = {
                if (hasText) onSendClicked() else onMicClicked()
            },
            containerColor = buttonColor,
            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp),
            shape = CircleShape,
            modifier = Modifier.size(50.dp)
        ) {
            Icon(
                imageVector = when {
                    hasText -> Icons.AutoMirrored.Filled.Send
                    isListening -> Icons.Default.Stop
                    else -> Icons.Default.Mic
                },
                contentDescription = if (hasText) "Send" else "Microphone",
                tint = iconColor,
                modifier = Modifier.size(when {
                    hasText -> 24.dp
                    isListening -> 28.dp
                    else -> 28.dp
                })
            )
        }
    }
}

@Composable
private fun PermissionScreen(onRequest: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(text = "🎙️", fontSize = 52.sp)
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Microphone Access Required",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Parliament Voice needs the microphone to record.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onRequest,
                colors = ButtonDefaults.buttonColors(containerColor = SaffronOrange),
                shape = RoundedCornerShape(50)
            ) {
                Text("Grant Permission", fontWeight = FontWeight.Bold)
            }
        }
    }
}