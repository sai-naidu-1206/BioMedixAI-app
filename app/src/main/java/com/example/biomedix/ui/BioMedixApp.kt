package com.example.biomedix.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    if (!uiState.isAuthenticated) {
        AuthLoginScreen(uiState = uiState, viewModel = viewModel)
    } else {
        MainConsoleScreen(uiState = uiState, history = history, viewModel = viewModel)
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

            // Glowing Stylized Bio-DNA Emblem
            BioCoreGlowingLogo(modifier = Modifier.size(100.dp))

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "BIOMEDIX CORE",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = NeonTeal,
                letterSpacing = 2.sp
            )

            Text(
                text = if (uiState.isRegistering) "Create New Account" else "Secure Access Required",
                fontSize = 13.sp,
                color = TextMuted,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Credentials Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(20.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(Color(0xFF1E293B), Color(0xFF38BDF8).copy(alpha = 0.4f)))
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Terminal Username
                    OutlinedTextField(
                        value = uiState.usernameInput,
                        onValueChange = { viewModel.setUsernameInput(it) },
                        label = { Text(if (uiState.isRegistering) "New Username" else "Terminal Username", fontStyle = FontStyle.Italic) },
                        leadingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (uiState.authError != null) {
                                Icon(Icons.Default.Error, contentDescription = null, tint = AlertRed)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextCharcoal,
                            unfocusedTextColor = TextCharcoal,
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = BorderDark,
                            focusedLabelColor = CyanAccent,
                            unfocusedLabelColor = TextMuted
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

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
                                    tint = if (uiState.authError != null) AlertRed else TextMuted
                                )
                            }
                        },
                        visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("access_code_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextCharcoal,
                            unfocusedTextColor = TextCharcoal,
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = BorderDark,
                            focusedLabelColor = CyanAccent,
                            unfocusedLabelColor = TextMuted
                        ),
                        singleLine = true
                    )

                    if (uiState.authError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = uiState.authError,
                            color = AlertRed,
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // INITIALIZE ACCESS Button
                    Button(
                        onClick = { viewModel.initializeAccess() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("initialize_access_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(listOf(Color(0xFF00F2FE), Color(0xFF4FACFE))),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (uiState.isRegistering) "COMPLETE ENROLLMENT" else "INITIALIZE ACCESS",
                                color = Color(0xFF050811),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Enroll link
                    Text(
                        text = if (uiState.isRegistering) "Back to Login" else "New Researcher? ENROLL HERE",
                        color = Color(0xFFC084FC),
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleRegistrationMode() }
                    )
                }
            }

            if (uiState.authError != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .background(Color(0xFF2A1515), RoundedCornerShape(20.dp))
                        .border(1.dp, AlertRed.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = AlertRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ACCESS DENIED: Invalid Credentials", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "v2.4.0-SECURITY_ENFORCED",
                color = Color(0xFF475569),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
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
    viewModel: BioMedixViewModel
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
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "BIOMEDIX",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                fontStyle = FontStyle.Italic,
                                color = NeonTeal,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "GENOMIC INTELLIGENCE SYSTEM",
                                fontSize = 10.sp,
                                color = TextMuted,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.setSelectedTab(5) }) {
                            Icon(Icons.Default.AccessTime, contentDescription = "History", tint = CyanAccent)
                        }
                        IconButton(onClick = { viewModel.logout() }) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout", tint = TextMuted)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
                )
            },
            containerColor = DarkBackground
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Navigation Tabs
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

        // Floating Purple AI Assistant Trigger Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 20.dp)
        ) {
            Button(
                onClick = { viewModel.toggleAiAssistant() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .height(48.dp)
                    .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFFA855F7))
                    .testTag("ai_assistant_fab")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "AI Assistant",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI ASSISTANT",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Loading Overlay: "SYNCHRONIZING WITH CORE..."
        if (uiState.isSynchronizing) {
            SynchronizingOverlay()
        }

        // Interactive AI Assistant Bottom Sheet / Dialog
        if (uiState.isAiAssistantOpen) {
            AiAssistantDialog(uiState = uiState, viewModel = viewModel)
        }
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
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Disease Burden Gauge Bar
        DiseaseBurdenBar(percentage = uiState.diseaseBurdenPercentage)

        Spacer(modifier = Modifier.height(16.dp))

        // RESEARCH CONSOLE Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(18.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderDark, BorderDark)))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "RESEARCH CONSOLE",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Disease Target Input
                OutlinedTextField(
                    value = uiState.diseaseInput,
                    onValueChange = { viewModel.setDiseaseInput(it) },
                    label = { Text("Disease Target", fontStyle = FontStyle.Italic) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("disease_target_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextCharcoal,
                        unfocusedTextColor = TextCharcoal,
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = BorderDark,
                        focusedLabelColor = CyanAccent,
                        unfocusedLabelColor = TextMuted
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Model Organism Dropdown Selector
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { organismExpanded = !organismExpanded }
                            .testTag("organism_dropdown"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextCharcoal,
                            unfocusedTextColor = TextCharcoal,
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = BorderDark,
                            focusedLabelColor = CyanAccent,
                            unfocusedLabelColor = TextMuted
                        )
                    )

                    DropdownMenu(
                        expanded = organismExpanded,
                        onDismissRequest = { organismExpanded = false },
                        modifier = Modifier
                            .background(Color(0xFF0F172A))
                            .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                    ) {
                        organisms.forEach { org ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = org,
                                        color = if (uiState.modelOrganism == org) NeonTeal else Color.White,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 14.sp
                                    )
                                },
                                onClick = {
                                    viewModel.setModelOrganism(org)
                                    organismExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // gRNA Sequence Input
                OutlinedTextField(
                    value = uiState.grnaInput,
                    onValueChange = { viewModel.setGrnaInput(it) },
                    label = { Text("gRNA Sequence (required for CRISPR)", fontStyle = FontStyle.Italic) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("grna_seq_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextCharcoal,
                        unfocusedTextColor = TextCharcoal,
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = BorderDark,
                        focusedLabelColor = EmeraldGreen,
                        unfocusedLabelColor = TextMuted
                    ),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ANALYSIS MODULES Title
        Text(
            text = "ANALYSIS MODULES",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 2x2 Interactive Module Action Cards (Video Exact Match)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Card 1: TARGET (Discover Targets)
                ModuleActionCard(
                    title = "TARGET",
                    subtitle = if (uiState.targetCompleted) "COMPLETED ✓" else "Discover Targets",
                    isCompleted = uiState.targetCompleted,
                    isLocked = false,
                    accentColor = Color(0xFFEF4444),
                    icon = {
                        TargetGlowIcon(isCompleted = uiState.targetCompleted)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("module_target_card"),
                    onClick = { viewModel.runTargetDiscovery() }
                )

                // Card 2: DRUG (Analyze Druggability)
                ModuleActionCard(
                    title = "DRUG",
                    subtitle = if (uiState.drugCompleted) "COMPLETED ✓" else if (!uiState.targetCompleted) "DRUG (LOCKED)" else "Analyze Druggability",
                    isCompleted = uiState.drugCompleted,
                    isLocked = false,
                    accentColor = Color(0xFFF59E0B),
                    icon = {
                        PillGlowIcon(isCompleted = uiState.drugCompleted)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("module_drug_card"),
                    onClick = { viewModel.runDruggabilityAnalysis() }
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Card 3: CRISPR (CRISPR Safety)
                ModuleActionCard(
                    title = "CRISPR",
                    subtitle = if (uiState.crisprCompleted) "COMPLETED ✓" else if (!uiState.targetCompleted) "CRISPR (LOCKED)" else "CRISPR Safety",
                    isCompleted = uiState.crisprCompleted,
                    isLocked = false,
                    accentColor = Color(0xFFA855F7),
                    icon = {
                        DnaHelixGlowIcon(isCompleted = uiState.crisprCompleted)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("module_crispr_card"),
                    onClick = { viewModel.runCrisprSafety() }
                )

                // Card 4: COMPLETE (Complete Analysis)
                ModuleActionCard(
                    title = "COMPLETE",
                    subtitle = if (uiState.completeCompleted) "COMPLETED ✓" else "Complete Analysis",
                    isCompleted = uiState.completeCompleted,
                    isLocked = false,
                    accentColor = Color(0xFF00F2FE),
                    icon = {
                        MicroscopeGlowIcon(isCompleted = uiState.completeCompleted)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("module_complete_card"),
                    onClick = { viewModel.runCompleteAnalysis() }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Dynamic Output Section (Video Match: Targets Found, Druggability Analysis Complete, Treatment Workflow Complete, etc.)
        if (uiState.activeOutputCard != null) {
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

        Spacer(modifier = Modifier.height(80.dp))
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
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderDark, BorderDark)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (percentage == 0) EmeraldGreen else NeonTeal,
                trackColor = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("TARGET", color = if (percentage <= 70) EmeraldGreen else TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                Text("▶", color = Color(0xFF475569), fontSize = 10.sp)
                Text("DRUG", color = if (percentage <= 40) EmeraldGreen else TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                Text("▶", color = Color(0xFF475569), fontSize = 10.sp)
                Text("CRISPR", color = if (percentage == 0) EmeraldGreen else TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
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
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color(0xFFECFDF5) else CardBackground
        ),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                if (isCompleted) listOf(EmeraldGreen, CyanAccent)
                else listOf(BorderDark, BorderDark)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(42.dp),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isCompleted) "COMPLETED ✓" else title,
                color = if (isCompleted) EmeraldGreen else TextCharcoal,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic
            )

            if (!isCompleted) {
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
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
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground.copy(alpha = 0.92f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(90.dp),
                color = NeonTeal,
                strokeWidth = 6.dp,
                trackColor = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(28.dp))

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
 * Terminal Result Output Box
 */
@Composable
fun TerminalOutputCard(
    title: String,
    subtitle: String?,
    formattedJson: String,
    onExploreVisuals: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("terminal_output_card"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1424)),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(NeonTeal, Color(0xFF38BDF8)))
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = NeonTeal,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )

                if (formattedJson.isNotEmpty()) {
                    TextButton(onClick = onExploreVisuals) {
                        Text("View Visual Graph ➔", color = CyanAccent, fontSize = 11.sp)
                    }
                }
            }

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    color = Color.White,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }

            if (formattedJson.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Attempt to parse simple key-value pairs for tabular display
                val lines = formattedJson.split("\n").filter { it.contains(":") && !it.trim().startsWith("{") && !it.trim().startsWith("}") && !it.trim().startsWith("[") }
                
                if (lines.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF050811), RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        lines.forEach { line ->
                            val parts = line.split(":", limit = 2)
                            if (parts.size == 2) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = parts[0].trim().replace("\"", "").replace("_", " ").uppercase(),
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = parts[1].trim().replace("\"", "").replace(",", ""),
                                        color = Color(0xFF93C5FD),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.weight(1.2f)
                                    )
                                }
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF1E293B)))
                            }
                        }
                    }
                } else {
                    // Fallback for complex JSON
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF050811), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = formattedJson,
                            color = Color(0xFF93C5FD),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * AI Assistant Full Interactive Sheet / Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantDialog(uiState: UiState, viewModel: BioMedixViewModel) {
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.chatMessages.size) {
        if (uiState.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.chatMessages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { viewModel.toggleAiAssistant() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clickable(enabled = false) {}
                .imePadding(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(listOf(PurpleAccent, CyanAccent))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "BioMedix AI Assistant",
                            color = NeonTeal,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        )
                        Text(
                            text = "Ask anything about diseases, genes, or CRISPR safety.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }

                    IconButton(onClick = { viewModel.toggleAiAssistant() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Chat Messages List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.chatMessages, key = { it.id }) { msg ->
                        ChatBubble(message = msg)
                    }

                    if (uiState.isAiThinking) {
                        item {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(modifier = Modifier.size(14.dp), color = NeonTeal, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("BioMedix AI is synthesizing...", color = TextMuted, fontSize = 11.sp, fontStyle = FontStyle.Italic)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.aiQueryInput,
                        onValueChange = { viewModel.setAiQueryInput(it) },
                        placeholder = { Text("Type your query...", fontStyle = FontStyle.Italic, color = Color(0xFF64748B)) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_chat_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = BorderDark
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { viewModel.sendAiQuery() })
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { viewModel.sendAiQuery() },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF38BDF8), CircleShape)
                            .testTag("ai_send_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF050811))
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    if (message.isUser) Color(0xFF0284C7) else Color(0xFF1E293B),
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (message.isUser) 14.dp else 2.dp,
                        bottomEnd = if (message.isUser) 2.dp else 14.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                lineHeight = 18.sp
            )
        }
    }
}

// --- Visual Tabs: PPI Network, 3D Protein, CRISPR DL, BioKt, Database, Backend Code ---

@Composable
fun PpiNetworkScreen(uiState: UiState) {
    val report = uiState.currentReport
    if (report == null) {
        EmptyStateView("Run Target Discovery or Complete Analysis on the Console tab to view interactive PPI graph.")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Protein-Protein Interaction Network (${report.diseaseName})",
            color = TextCharcoal,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Constructed from STRING DB API. Drag to pan nodes.",
            color = TextMuted,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        InteractivePpiNetworkGraph(
            hubResult = report.hubResult,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Centrality Rankings & Candidate Genes", color = TextCharcoal, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(report.hubResult.candidateGenes) { gene ->
                val centrality = report.hubResult.centralityScores[gene.symbol] ?: 0f
                val isHub = gene.symbol == report.hubResult.hubGeneSymbol

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isHub) Color(0xFF0C2A4D) else CardBackground
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(gene.symbol, color = if (isHub) Color.White else TextCharcoal, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                if (isHub) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "TOP HUB",
                                        color = CyanAccent,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .background(Color(0xFF0284C7).copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(gene.description, color = TextMuted, fontSize = 11.sp, maxLines = 1)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Score: ${((centrality) * 100).toInt()}%",
                                color = if (isHub) CyanAccent else EmeraldGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
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

    if (drug == null) {
        EmptyStateView("Run Druggability or Complete Analysis to view 3D protein structure & ML pocket.")
        return
    }

    val modes = listOf("Backbone Ribbon", "Binding Pocket", "Atomic Spheres")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "3D Structure & Pocket ML (${drug.geneSymbol})",
            color = TextCharcoal,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "PDB ID: ${drug.pdbId} | Resolution: ${drug.resolution ?: 2.0} Å",
            color = CyanAccent,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            modes.forEach { mode ->
                FilterChip(
                    selected = uiState.proteinViewMode == mode,
                    onClick = { viewModel.setProteinViewMode(mode) },
                    label = { Text(mode, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0284C7),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF1E293B),
                        labelColor = TextMuted
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Protein3DStructureViewer(
            geneSymbol = drug.geneSymbol,
            pdbId = drug.pdbId ?: "HOMOLOGY",
            pocketFeatures = drug.pocketFeatures,
            viewMode = uiState.proteinViewMode,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        val feat = drug.pocketFeatures
        if (feat != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Candidate Binding Pocket Geometric Descriptors", color = Color(0xFFF59E0B), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DescriptorItem("Cavity Volume", "${feat.pocketVolume} Å³", Modifier.weight(1f))
                        DescriptorItem("Hydrophobicity", "${(feat.hydrophobicityRatio * 100).toInt()}%", Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DescriptorItem("Cavity Depth", "${feat.depth} Å", Modifier.weight(1f))
                        DescriptorItem("Lining Residues", "${feat.liningResidueCount} amino acids", Modifier.weight(1f))
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

    if (crispr == null) {
        EmptyStateView("Run CRISPR Safety or Complete Analysis on the Console tab to view off-target deep learning.")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("CRISPR/Cas9 Genomic Deep Learning Evaluation", color = TextCharcoal, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text("Target Gene: ${crispr.geneSymbol} | PAM: ${crispr.pamType}", color = EmeraldGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Query gRNA Protospacer (20bp):", color = TextMuted, fontSize = 11.sp)
                Text(crispr.grnaSequence, color = CyanAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("GC Content: ${(crispr.gcContent * 10).toInt() / 10.0}%", color = TextCharcoal, fontSize = 11.sp)
                    Text("PAM Sites Scanned: ${crispr.totalSitesScanned}", color = TextCharcoal, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Flagged Potential Off-Target Cleavage Sites (${crispr.flaggedSites.size})", color = TextCharcoal, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(crispr.flaggedSites) { site ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B2E)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Locus: ${site.chromosomePosition}", color = PurpleAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Risk: ${(site.riskScore * 100).toInt()}%", color = if (site.riskScore > 0.6f) AlertRed else Color(0xFFF59E0B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Site: ${site.targetSequence} [${site.pam}]", color = Color.White, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
fun BioKtStudioScreen(uiState: UiState, viewModel: BioMedixViewModel) {
    val records = uiState.parsedFastaRecords
    val firstSeq = records.firstOrNull()?.sequence ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("BioKt Bioinformatics Sequence Workbench", color = TextCharcoal, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text("Powered by BioKt Kotlin library for FASTA parsing & genomics.", color = TextMuted, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = uiState.fastaInput,
            onValueChange = { viewModel.setFastaInput(it) },
            label = { Text("FASTA Sequence Input") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextCharcoal,
                unfocusedTextColor = TextCharcoal,
                focusedBorderColor = CyanAccent,
                unfocusedBorderColor = BorderDark
            ),
            maxLines = 6
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (firstSeq.isNotEmpty()) {
            val gc = records.first().calculateGcContent()
            val counts = records.first().nucleotideCounts()
            val rComp = BioKt.reverseComplement(firstSeq.take(60))
            val translated = BioKt.translate(firstSeq.take(60))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("BioKt Sequence Metrics", color = CyanAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Length: ${firstSeq.length} bp | GC Content: ${(gc * 10).toInt() / 10.0}%", color = TextCharcoal, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Nucleotide Distribution: A=${counts['A']} T=${counts['T']} G=${counts['G']} C=${counts['C']}", color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Reverse Complement (First 60bp):", color = TextMuted, fontSize = 11.sp)
                    Text(rComp, color = EmeraldGreen, fontSize = 12.sp, fontFamily = FontFamily.Monospace)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Translation (Amino Acid Sequence):", color = TextMuted, fontSize = 11.sp)
                    Text(translated, color = PurpleAccent, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
fun HistoryDatabaseScreen(history: List<CachedReportEntity>, viewModel: BioMedixViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Persistence & History Hub", color = TextCharcoal, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("${history.size} runs stored locally in Room Database", color = TextMuted, fontSize = 11.sp)
            }
            if (history.isNotEmpty()) {
                IconButton(onClick = { viewModel.clearAllHistory() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear All", tint = AlertRed)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (history.isEmpty()) {
            EmptyStateView("No runs saved in local persistence database yet.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(history) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.diseaseName, color = TextCharcoal, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Hub: ${item.hubGene}", color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(item.verdict, color = TextMuted, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Druggability: ${(item.druggabilityScore * 100).toInt()}%", color = CyanAccent, fontSize = 11.sp)
                                Text("CRISPR Safety: ${(item.crisprSafetyScore * 100).toInt()}%", color = EmeraldGreen, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackendArchitectureScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Spring Boot + BioKt Backend Architecture", color = TextCharcoal, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text("Ready-to-deploy Spring Boot Kotlin source code with BioKt & PostgreSQL JPA.", color = TextMuted, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(10.dp))

        CodeSnippetCard(
            title = "1. build.gradle.kts (Spring Boot + BioKt)",
            code = """
plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("io.github.shankarnvs:biokt:1.0.0")
}
            """.trimIndent()
        )

        Spacer(modifier = Modifier.height(10.dp))

        CodeSnippetCard(
            title = "2. PipelineController.kt (REST API /api/v1/pipeline)",
            code = """
package com.biomedix.controller

import com.biomedix.service.PipelineOrchestrationService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/pipeline")
@CrossOrigin(origins = ["*"])
class PipelineController(
    private val orchestrationService: PipelineOrchestrationService
) {
    @PostMapping("/run")
    suspend fun executePipeline(
        @RequestBody request: PipelineRequest
    ): IntegratedReportDto {
        return orchestrationService.runFullPipeline(
            diseaseName = request.diseaseName,
            grnaSequence = request.grnaSequence
        )
    }
}
            """.trimIndent()
        )
    }
}

@Composable
fun CodeSnippetCard(title: String, code: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, color = CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF020617), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(code, color = Color(0xFFCBD5E1), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun EmptyStateView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color(0xFF64748B),
            fontSize = 13.sp,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

// --- Custom Glowing Vector Art Drawables ---

@Composable
fun BioCoreGlowingLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f

        drawLine(
            color = NeonTeal,
            start = Offset(cx, h * 0.1f),
            end = Offset(cx, h * 0.9f),
            strokeWidth = 10f,
            cap = StrokeCap.Round
        )

        val branches = listOf(
            0.25f to 0.65f,
            0.40f to 0.75f,
            0.55f to 0.85f,
            0.70f to 0.75f
        )

        branches.forEach { (yFactor, spanFactor) ->
            val y = h * yFactor
            val span = (w * spanFactor) / 2f
            drawLine(
                color = NeonTeal,
                start = Offset(cx - span, y),
                end = Offset(cx + span, y),
                strokeWidth = 8f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun TargetGlowIcon(isCompleted: Boolean) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f
        drawCircle(
            color = if (isCompleted) EmeraldGreen else Color(0xFFEF4444),
            radius = r * 0.85f,
            center = c,
            style = Stroke(width = 4f)
        )
        drawCircle(
            color = if (isCompleted) EmeraldGreen else Color(0xFFEF4444),
            radius = r * 0.5f,
            center = c,
            style = Stroke(width = 4f)
        )
        drawCircle(
            color = if (isCompleted) EmeraldGreen else Color(0xFFEF4444),
            radius = r * 0.25f,
            center = c
        )
    }
}

@Composable
fun PillGlowIcon(isCompleted: Boolean) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val c = Offset(size.width / 2f, size.height / 2f)
        drawCircle(
            color = if (isCompleted) EmeraldGreen else Color(0xFFF59E0B),
            radius = size.minDimension * 0.35f,
            center = c
        )
    }
}

@Composable
fun DnaHelixGlowIcon(isCompleted: Boolean) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val color = if (isCompleted) EmeraldGreen else Color(0xFFA855F7)

        drawLine(color = color, start = Offset(w * 0.2f, h * 0.3f), end = Offset(w * 0.8f, h * 0.3f), strokeWidth = 4f, cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.3f, h * 0.5f), end = Offset(w * 0.7f, h * 0.5f), strokeWidth = 4f, cap = StrokeCap.Round)
        drawLine(color = color, start = Offset(w * 0.2f, h * 0.7f), end = Offset(w * 0.8f, h * 0.7f), strokeWidth = 4f, cap = StrokeCap.Round)
    }
}

@Composable
fun MicroscopeGlowIcon(isCompleted: Boolean) {
    Icon(
        imageVector = Icons.Default.Science,
        contentDescription = "Complete",
        tint = if (isCompleted) EmeraldGreen else NeonTeal,
        modifier = Modifier.fillMaxSize()
    )
}
