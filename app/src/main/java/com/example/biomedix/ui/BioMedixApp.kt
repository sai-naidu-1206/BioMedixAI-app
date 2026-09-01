package com.example.biomedix.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.biomedix.biokt.BioKt
import com.example.biomedix.data.local.CachedReportEntity

// Color Palette matching the 3D Claymorphic and Neumorphic Design Directives
val DarkBackground = Color(0xFFF3F0FF) // Clean Off-White with soft lavender undertones
val SurfaceDark = Color(0xFFFAF8FF)
val CardBackground = Color(0xFFFFFFFF)
val CyanAccent = Color(0xFF8B5CF6)     // Primary Soft Pastel Violet
val NeonTeal = Color(0xFF6D28D9)       // Deep Indigo-Violet Branding
val EmeraldGreen = Color(0xFF059669)   // Electric Mint Green Accent
val PurpleAccent = Color(0xFFA78BFA)   // Soft Periwinkle
val AlertRed = Color(0xFFDC2626)
val TextMuted = Color(0xFF4B5563)      // Readable Subtitle Muted
val BorderDark = Color(0xFFDDD6FE)     // Soft Lavender Border Accent
val TextCharcoal = Color(0xFF1E1B4B)   // Dark Charcoal for High Contrast Readability

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BioMedixApp(viewModel: BioMedixViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val history by viewModel.historyReports.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val view = LocalView.current

    if (!uiState.isAuthenticated) {
        AuthLoginScreen(uiState = uiState, viewModel = viewModel)
    } else {
        MainConsoleScreen(uiState = uiState, history = history, viewModel = viewModel, onCapture = {
            val bitmap = CaptureUtils.captureView(view)
            CaptureUtils.saveBitmapToGallery(context, bitmap)
        })
    }
}

/**
 * Screen 1: Futuristic BioMedix Core Auth Screen
 */
@Composable
fun AuthLoginScreen(uiState: UiState, viewModel: BioMedixViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

                Image(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = "Centurion University Logo",
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "BIOMEDIX CORE",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = NeonTeal,
                letterSpacing = 2.sp
            )

            Text(
                text = if (uiState.isRegistering) "Researcher Enrollment" else "Secure Access Required",
                fontSize = 13.sp,
                color = TextMuted,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Credentials Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(24.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(BorderDark, BorderDark))
                )
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    if (uiState.isWaitingFor2FA) {
                        // 2FA STEP
                        if (uiState.isShowingQrCode && uiState.qrCodeBitmap != null) {
                            Text(
                                text = "1. Scan this QR in Google Authenticator:",
                                fontWeight = FontWeight.Bold,
                                color = TextCharcoal,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Image(
                                bitmap = uiState.qrCodeBitmap.asImageBitmap(),
                                contentDescription = "2FA QR Code",
                                modifier = Modifier.size(200.dp).align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Secret Key: ${uiState.generatedSecretKey}",
                                color = TextMuted,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        Text(
                            text = "Enter 6-digit Authenticator Code:",
                            fontWeight = FontWeight.Bold,
                            color = TextCharcoal,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = uiState.twoFactorCodeInput,
                            onValueChange = { viewModel.set2FACodeInput(it) },
                            label = { Text("6-digit code") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextCharcoal,
                                unfocusedTextColor = TextCharcoal,
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = BorderDark
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            singleLine = true
                        )
                    } else {
                        // LOGIN/REGISTER STEP
                        // Terminal Username
                        OutlinedTextField(
                            value = uiState.usernameInput,
                            onValueChange = { viewModel.setUsernameInput(it) },
                            label = { Text(if (uiState.isRegistering) "New Username" else "Terminal Username", fontStyle = FontStyle.Italic) },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextCharcoal,
                                unfocusedTextColor = TextCharcoal,
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = BorderDark
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Access Code
                        OutlinedTextField(
                            value = uiState.accessCodeInput,
                            onValueChange = { viewModel.setAccessCodeInput(it) },
                            label = { Text(if (uiState.isRegistering) "Set Access Code" else "Access Code", fontStyle = FontStyle.Italic) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                                    Icon(
                                        imageVector = if (uiState.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = TextMuted
                                    )
                                }
                            },
                            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextCharcoal,
                                unfocusedTextColor = TextCharcoal,
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = BorderDark
                            ),
                            singleLine = true
                        )
                    }

                    if (uiState.authError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.authError!!,
                            color = AlertRed,
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ACTION Button
                    Button(
                        onClick = { viewModel.initializeAccess() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (uiState.isWaitingFor2FA) "VERIFY & LOGIN" else if (uiState.isRegistering) "PROCEED TO 2FA" else "INITIALIZE ACCESS",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    if (!uiState.isWaitingFor2FA) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Enroll link
                        TextButton(
                            onClick = { viewModel.toggleRegistrationMode() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (uiState.isRegistering) "BACK TO LOGIN" else "NEW RESEARCHER? ENROLL HERE",
                                color = NeonTeal,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Google Sign-In Button
                        Button(
                            onClick = { /* Google Identity Logic */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .border(1.dp, BorderDark, RoundedCornerShape(14.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_biomedix_logo), // Placeholder for Google Icon
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Sign in with Google", color = TextCharcoal, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Back to login if in 2FA mode
                        TextButton(
                            onClick = { viewModel.toggleRegistrationMode() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("CANCEL", color = AlertRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "© 2024 CENTURION UNIVERSITY",
                color = Color(0xFF475569),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Screen 2: Main Research Console & Intelligence System
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainConsoleScreen(
    uiState: UiState,
    history: List<CachedReportEntity>,
    viewModel: BioMedixViewModel,
    onCapture: () -> Unit
) {
    val tabTitles = listOf(
        "Console" to Icons.Default.Biotech,
        "PPI Network" to Icons.Default.Hub,
        "3D Protein" to Icons.Default.ViewInAr,
        "CRISPR DL" to Icons.Default.Science,
        "BioKt Workbench" to Icons.Default.Grain,
        "Database" to Icons.Default.CompareArrows,
        "Backend Code" to Icons.Default.Code
    )

    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.img),
                                contentDescription = "CU Logo",
                                modifier = Modifier.size(36.dp).clip(CircleShape).border(1.dp, BorderDark, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "BIOMEDIX AI",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeonTeal,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "CENTURION UNIVERSITY",
                                    fontSize = 9.sp,
                                    color = TextMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = onCapture) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Capture Report", tint = CyanAccent)
                        }
                        IconButton(onClick = { viewModel.setSelectedTab(5) }) {
                            Icon(Icons.Default.AccessTime, contentDescription = "History", tint = CyanAccent)
                        }
                        IconButton(onClick = { viewModel.logout() }) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout", tint = TextMuted)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            containerColor = DarkBackground
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ScrollableTabRow(
                    selectedTabIndex = uiState.selectedTab,
                    containerColor = Color(0xFF0B132B),
                    contentColor = CyanAccent,
                    edgePadding = 8.dp
                ) {
                    tabTitles.forEachIndexed { index, (title, icon) ->
                        Tab(
                            selected = uiState.selectedTab == index,
                            onClick = { viewModel.setSelectedTab(index) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(icon, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(title, fontSize = 11.sp, fontWeight = if (uiState.selectedTab == index) FontWeight.Bold else FontWeight.Normal)
                                }
                            },
                            selectedContentColor = CyanAccent,
                            unselectedContentColor = TextMuted
                        )
                    }
                }

                when (uiState.selectedTab) {
                    0 -> ConsoleDashboardView(uiState, viewModel)
                    1 -> PpiNetworkScreen(uiState)
                    2 -> StructuralMlScreen(uiState, viewModel)
                    3 -> CrisprDlScreen(uiState)
                    4 -> BioKtStudioScreen(uiState, viewModel)
                    5 -> HistoryDatabaseScreen(history, viewModel)
                    6 -> BackendArchitectureScreen()
                }
            }
        }

        // Floating AI Assistant Trigger
        Box(modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)) {
            Button(
                onClick = { viewModel.toggleAiAssistant() },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.height(48.dp).shadow(12.dp, RoundedCornerShape(24.dp))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI ASSISTANT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        if (uiState.isSynchronizing) SynchronizingOverlay()
        if (uiState.isAiAssistantOpen) AiAssistantDialog(uiState = uiState, viewModel = viewModel)
    }
}

/**
 * Main Console Dashboard View
 */
@Composable
fun ConsoleDashboardView(uiState: UiState, viewModel: BioMedixViewModel) {
    val scrollState = rememberScrollState()
    var organismExpanded by remember { mutableStateOf(false) }
    val organisms = listOf("human", "mouse", "rice", "arabidopsis", "yeast", "zebrafish", "fruit fly", "e. coli")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        DiseaseBurdenBar(percentage = uiState.diseaseBurdenPercentage)

        Spacer(modifier = Modifier.height(16.dp))

        // RESEARCH CONSOLE Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(24.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderDark, BorderDark)))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "RESEARCH CONSOLE",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    IconButton(onClick = { viewModel.resetInputs() }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Inputs", tint = CyanAccent)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = uiState.diseaseInput,
                    onValueChange = { viewModel.setDiseaseInput(it) },
                    label = { Text("Disease Target (e.g. Alzheimer's)", fontStyle = FontStyle.Italic) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextCharcoal,
                        unfocusedTextColor = TextCharcoal,
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = BorderDark
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = uiState.modelOrganism,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Model Organism", fontStyle = FontStyle.Italic) },
                        trailingIcon = {
                            IconButton(onClick = { organismExpanded = !organismExpanded }) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextMuted)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().clickable { organismExpanded = !organismExpanded },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextCharcoal,
                            unfocusedTextColor = TextCharcoal,
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = BorderDark
                        )
                    )

                    DropdownMenu(
                        expanded = organismExpanded,
                        onDismissRequest = { organismExpanded = false },
                        modifier = Modifier.background(Color.White).border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                    ) {
                        organisms.forEach { org ->
                            DropdownMenuItem(
                                text = { Text(text = org, color = TextCharcoal, fontSize = 14.sp) },
                                onClick = {
                                    viewModel.setModelOrganism(org)
                                    organismExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.grnaInput,
                    onValueChange = { viewModel.setGrnaInput(it) },
                    label = { Text("gRNA Sequence (e.g. GCTGCTACCGTGAAGTACTG)", fontStyle = FontStyle.Italic) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextCharcoal,
                        unfocusedTextColor = TextCharcoal,
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = BorderDark
                    ),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ANALYSIS MODULES",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ModuleActionCard(
                    title = "TARGET",
                    subtitle = if (uiState.targetCompleted) "VALIDATED ✓" else "Discover Targets",
                    isCompleted = uiState.targetCompleted,
                    isLocked = false,
                    accentColor = Color(0xFFEF4444),
                    icon = { TargetGlowIcon(isCompleted = uiState.targetCompleted) },
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.runTargetDiscovery() }
                )

                ModuleActionCard(
                    title = "DRUG",
                    subtitle = if (uiState.drugCompleted) "ANALYZED ✓" else "Druggability ML",
                    isCompleted = uiState.drugCompleted,
                    isLocked = false,
                    accentColor = Color(0xFFF59E0B),
                    icon = { PillGlowIcon(isCompleted = uiState.drugCompleted) },
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.runDruggabilityAnalysis() }
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ModuleActionCard(
                    title = "CRISPR",
                    subtitle = if (uiState.crisprCompleted) "VERIFIED ✓" else "Genomic DL",
                    isCompleted = uiState.crisprCompleted,
                    isLocked = false,
                    accentColor = PurpleAccent,
                    icon = { DnaHelixGlowIcon(isCompleted = uiState.crisprCompleted) },
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.runCrisprSafety() }
                )

                ModuleActionCard(
                    title = "COMPLETE",
                    subtitle = if (uiState.completeCompleted) "FINISHED ✓" else "End-to-End Run",
                    isCompleted = uiState.completeCompleted,
                    isLocked = false,
                    accentColor = NeonTeal,
                    icon = { MicroscopeGlowIcon(isCompleted = uiState.completeCompleted) },
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.runCompleteAnalysis() }
                )
            }
        }

        if (uiState.activeOutputCard != null) {
            Spacer(modifier = Modifier.height(20.dp))
            TerminalOutputCard(
                title = uiState.outputTitle,
                subtitle = uiState.outputSubtitle,
                formattedJson = uiState.outputFormattedJson,
                onExploreVisuals = {
                    when (uiState.activeOutputCard) {
                        "TARGET" -> viewModel.setSelectedTab(1)
                        "DRUG" -> viewModel.setSelectedTab(2)
                        "CRISPR" -> viewModel.setSelectedTab(3)
                        else -> viewModel.setSelectedTab(1)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

/**
 * Disease Burden Progress Gauge
 */
@Composable
fun DiseaseBurdenBar(percentage: Int) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "burden_anim"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderDark, BorderDark)))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DISEASE BURDEN",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "$percentage%",
                    color = if (percentage == 0) EmeraldGreen else NeonTeal,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                color = if (percentage == 0) EmeraldGreen else NeonTeal,
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("TARGET", color = if (percentage <= 70) EmeraldGreen else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("DRUG", color = if (percentage <= 40) EmeraldGreen else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("CRISPR", color = if (percentage == 0) EmeraldGreen else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * 2x2 Glowing Module Action Card
 */
@Composable
fun ModuleActionCard(
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    isLocked: Boolean,
    accentColor: Color,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(140.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (isCompleted) Color(0xFFF0FDF4) else CardBackground),
        shape = RoundedCornerShape(24.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(if (isCompleted) listOf(EmeraldGreen, EmeraldGreen) else listOf(BorderDark, BorderDark))
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.size(44.dp), contentAlignment = Alignment.Center) { icon() }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isCompleted) "VALIDATED ✓" else title,
                color = if (isCompleted) EmeraldGreen else TextCharcoal,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

/**
 * Fullscreen "SYNCHRONIZING WITH CORE..." Overlay
 */
@Composable
fun SynchronizingOverlay() {
    val transition = rememberInfiniteTransition(label = "sync_pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(animation = tween(800, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "alpha"
    )

    Box(
        modifier = Modifier.fillMaxSize().background(DarkBackground.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(modifier = Modifier.size(80.dp), color = NeonTeal, strokeWidth = 6.dp)
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "SYNCHRONIZING WITH CORE...",
                color = NeonTeal.copy(alpha = alpha),
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Terminal Result Output Box with Tabular Display
 */
@Composable
fun TerminalOutputCard(
    title: String,
    subtitle: String?,
    formattedJson: String,
    onExploreVisuals: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("terminal_output_card"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1424)),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NeonTeal, Color(0xFF38BDF8))))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, color = NeonTeal, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
                if (formattedJson.isNotEmpty()) {
                    TextButton(onClick = onExploreVisuals) {
                        Text("View Visual Graph ➔", color = CyanAccent, fontSize = 12.sp)
                    }
                }
            }

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = subtitle, color = Color.White, fontSize = 13.sp, lineHeight = 20.sp)
            }

            if (formattedJson.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                val lines = formattedJson.split("\n").filter { it.contains(":") }
                
                if (lines.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF050811), RoundedCornerShape(12.dp)).padding(12.dp)) {
                        lines.forEach { line ->
                            val parts = line.split(":", limit = 2)
                            if (parts.size == 2) {
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = parts[0].trim().replace("_", " ").uppercase(), color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    Text(text = parts[1].trim(), color = Color(0xFF93C5FD), fontSize = 12.sp, fontFamily = FontFamily.Monospace, textAlign = TextAlign.End, modifier = Modifier.weight(1.5f))
                                }
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF1E293B)))
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF050811), RoundedCornerShape(12.dp)).padding(14.dp)) {
                        Text(text = formattedJson, color = Color(0xFF93C5FD), fontSize = 12.sp, fontFamily = FontFamily.Monospace, lineHeight = 18.sp)
                    }
                }
            }
        }
    }
}

/**
 * AI Assistant Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantDialog(uiState: UiState, viewModel: BioMedixViewModel) {
    val listState = rememberLazyListState()
    LaunchedEffect(uiState.chatMessages.size) { if (uiState.chatMessages.isNotEmpty()) listState.animateScrollToItem(uiState.chatMessages.size - 1) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.75f)).clickable { viewModel.toggleAiAssistant() }, contentAlignment = Alignment.BottomCenter) {
        Card(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f).clickable(enabled = false) {}.imePadding(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(PurpleAccent, CyanAccent)))
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "BioMedix AI Assistant", color = NeonTeal, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
                        Text(text = "Research Core Active", color = TextMuted, fontSize = 11.sp)
                    }
                    IconButton(onClick = { viewModel.toggleAiAssistant() }) { Icon(Icons.Default.Clear, contentDescription = null, tint = TextMuted) }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(state = listState, modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.chatMessages, key = { it.id }) { msg -> ChatBubble(message = msg) }
                    if (uiState.isAiThinking) {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = NeonTeal, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Synthesizing response...", color = TextMuted, fontSize = 12.sp, fontStyle = FontStyle.Italic)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = uiState.aiQueryInput,
                        onValueChange = { viewModel.setAiQueryInput(it) },
                        placeholder = { Text("Ask about genes, diseases...", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = CyanAccent),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { viewModel.sendAiQuery() })
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(onClick = { viewModel.sendAiQuery() }, modifier = Modifier.size(48.dp).background(CyanAccent, CircleShape)) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart) {
        Box(modifier = Modifier.widthIn(max = 280.dp).background(if (message.isUser) Color(0xFF0284C7) else Color(0xFF1E293B), RoundedCornerShape(16.dp)).padding(14.dp)) {
            Text(text = message.text, color = Color.White, fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}

// --- Visual Tabs: PPI Network, 3D Protein, CRISPR DL, BioKt, Database, Backend Code ---

@Composable
fun PpiNetworkScreen(uiState: UiState) {
    val report = uiState.currentReport
    if (report == null) { EmptyStateView("Run analysis to view interactive PPI graph.") ; return }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "PPI Interaction Network (${report.diseaseName})", color = TextCharcoal, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = "STRING DB Evidence Graph", color = TextMuted, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(12.dp))
        InteractivePpiNetworkGraph(hubResult = report.hubResult, modifier = Modifier.fillMaxWidth().height(300.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Centrality Rankings", color = TextCharcoal, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(report.hubResult.candidateGenes) { gene ->
                val centrality = report.hubResult.centralityScores[gene.symbol] ?: 0f
                val isHub = gene.symbol == report.hubResult.hubGeneSymbol
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (isHub) Color(0xFFEEF2FF) else Color.White), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(gene.symbol, color = if (isHub) NeonTeal else TextCharcoal, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(gene.description, color = TextMuted, fontSize = 11.sp, maxLines = 1)
                        }
                        Text(text = "Score: ${((centrality) * 100).toInt()}%", color = if (isHub) CyanAccent else EmeraldGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
fun StructuralMlScreen(uiState: UiState, viewModel: BioMedixViewModel) {
    val report = uiState.currentReport
    val drug = report?.druggabilityResult
    if (drug == null) { EmptyStateView("Run analysis to view 3D protein structures.") ; return }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(text = "3D Structural ML (${drug.geneSymbol})", color = TextCharcoal, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = "PDB ID: ${drug.pdbId} | Structural Cavity Analysis", color = CyanAccent, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Protein3DStructureViewer(geneSymbol = drug.geneSymbol, pdbId = drug.pdbId ?: "MODEL", pocketFeatures = drug.pocketFeatures, viewMode = uiState.proteinViewMode, modifier = Modifier.fillMaxWidth().height(300.dp))
        Spacer(modifier = Modifier.height(16.dp))
        val feat = drug.pocketFeatures
        if (feat != null) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Pocket Geometric Descriptors", color = Color(0xFFF59E0B), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                         DescriptorItem("Volume", "${feat.pocketVolume} Å³", Modifier.weight(1f))
                        DescriptorItem("Hydrophobicity", "${(feat.hydrophobicityRatio * 100).toInt()}%", Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun CrisprDlScreen(uiState: UiState) {
    val report = uiState.currentReport
    val crispr = report?.crisprResult
    if (crispr == null) { EmptyStateView("Run analysis to view CRISPR safety logs.") ; return }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Genomic Off-Target Evaluation", color = TextCharcoal, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("Target: ${crispr.geneSymbol} | PAM: ${crispr.pamType}", color = EmeraldGreen, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Selected gRNA Protospacer:", color = TextMuted, fontSize = 12.sp)
                Text(crispr.grnaSequence, color = CyanAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("High-Risk Flagged Sites", color = TextCharcoal, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(crispr.flaggedSites) { site ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Locus: ${site.chromosomePosition}", color = PurpleAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Risk: ${(site.riskScore * 100).toInt()}%", color = if (site.riskScore > 0.6f) AlertRed else Color(0xFFF59E0B), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("Site: ${site.targetSequence}", color = TextCharcoal, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
fun BioKtStudioScreen(uiState: UiState, viewModel: BioMedixViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("BioKt Workbench", color = TextCharcoal, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("FASTA Sequence Manipulation", color = TextMuted, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = uiState.fastaInput, onValueChange = { viewModel.setFastaInput(it) }, label = { Text("Enter FASTA Sequence") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextCharcoal, unfocusedTextColor = TextCharcoal))
        if (uiState.parsedFastaRecords.isNotEmpty()) {
            val rec = uiState.parsedFastaRecords.first()
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("BioKt Sequence Metrics", color = CyanAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Length: ${rec.length} bp", color = TextCharcoal, fontSize = 13.sp)
                    Text("GC Content: ${rec.calculateGcContent().toInt()}%", color = TextCharcoal, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Translation:", color = TextMuted, fontSize = 11.sp)
                    Text(BioKt.translate(rec.sequence.take(60)), color = PurpleAccent, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
fun HistoryDatabaseScreen(history: List<CachedReportEntity>, viewModel: BioMedixViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Persistence Hub", color = TextCharcoal, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("${history.size} reports saved", color = TextMuted, fontSize = 11.sp)
            }
            if (history.isNotEmpty()) IconButton(onClick = { viewModel.clearAllHistory() }) { Icon(Icons.Default.Delete, contentDescription = null, tint = AlertRed) }
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (history.isEmpty()) EmptyStateView("No saved reports yet.")
        else LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(history) { item ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(item.diseaseName, color = TextCharcoal, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("HUB: ${item.hubGene}", color = CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(item.verdict, color = TextMuted, fontSize = 12.sp, maxLines = 2)
                    }
                }
            }
        }
    }
}

@Composable
fun BackendArchitectureScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("System Architecture", color = TextCharcoal, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text("Spring Boot + BioKt Integration", color = TextMuted, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(12.dp))
        CodeSnippetCard(title = "Service Layer (BioKt)", code = "class GenomicService {\n  fun analyze(seq: String) {\n    val record = BioKt.parseFasta(seq)\n    return BioKt.translate(record.sequence)\n  }\n}")
    }
}

@Composable
fun CodeSnippetCard(title: String, code: String) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), shape = RoundedCornerShape(14.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF020617), RoundedCornerShape(10.dp)).padding(12.dp)) {
                Text(code, color = Color(0xFFCBD5E1), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun EmptyStateView(message: String) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(text = message, color = Color.Gray, textAlign = TextAlign.Center, fontSize = 14.sp)
    }
}

// --- Custom Glowing Vector Art Drawables ---

@Composable
fun BioCoreGlowingLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width ; val h = size.height ; val cx = w / 2f
        drawLine(color = NeonTeal, start = Offset(cx, h * 0.1f), end = Offset(cx, h * 0.9f), strokeWidth = 10f, cap = StrokeCap.Round)
        val branches = listOf(0.25f to 0.65f, 0.40f to 0.75f, 0.55f to 0.85f, 0.70f to 0.75f)
        branches.forEach { (yFactor, spanFactor) ->
            val y = h * yFactor ; val span = (w * spanFactor) / 2f
            drawLine(color = NeonTeal, start = Offset(cx - span, y), end = Offset(cx + span, y), strokeWidth = 8f, cap = StrokeCap.Round)
        }
    }
}

@Composable fun TargetGlowIcon(isCompleted: Boolean) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val c = Offset(size.width / 2f, size.height / 2f) ; val r = size.minDimension / 2f
        val color = if (isCompleted) EmeraldGreen else Color(0xFFEF4444)
        drawCircle(color = color, radius = r * 0.85f, center = c, style = Stroke(width = 4f))
        drawCircle(color = color, radius = r * 0.5f, center = c, style = Stroke(width = 4f))
        drawCircle(color = color, radius = r * 0.25f, center = c)
    }
}

@Composable fun PillGlowIcon(isCompleted: Boolean) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val c = Offset(size.width / 2f, size.height / 2f)
        drawCircle(color = if (isCompleted) EmeraldGreen else Color(0xFFF59E0B), radius = size.minDimension * 0.35f, center = c)
    }
}

@Composable fun DnaHelixGlowIcon(isCompleted: Boolean) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width ; val h = size.height ; val color = if (isCompleted) EmeraldGreen else PurpleAccent
        drawLine(color = color, start = Offset(w * 0.2f, h * 0.3f), end = Offset(w * 0.8f, h * 0.3f), strokeWidth = 4f, cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.3f, h * 0.5f), end = Offset(w * 0.7f, h * 0.5f), strokeWidth = 4f, cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.2f, h * 0.7f), end = Offset(w * 0.8f, h * 0.7f), strokeWidth = 4f, cap = StrokeCap.Round)
    }
}

@Composable fun MicroscopeGlowIcon(isCompleted: Boolean) {
    Icon(imageVector = Icons.Default.Science, contentDescription = null, tint = if (isCompleted) EmeraldGreen else NeonTeal, modifier = Modifier.fillMaxSize())
}
