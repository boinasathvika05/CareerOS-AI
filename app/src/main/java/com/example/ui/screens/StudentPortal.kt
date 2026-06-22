package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.viewmodel.CareerViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.util.*

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.Path
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StudentPortal(viewModel: CareerViewModel, modifier: Modifier = Modifier) {
    val activeTab by viewModel.selectedStudentTab.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // Sidebar items mapping
    val tabs = listOf(
        TabInfo("Overview", Icons.Default.Home, "My Profile & Career Stats"),
        TabInfo("AI Mentor", Icons.Default.Edit, "AI Mentor (Virtual Faculty)"),
        TabInfo("Roadmap", Icons.Default.List, "Interactive Learning Roadmap"),
        TabInfo("Resume AI", Icons.Default.Edit, "Resume ATS Intelligence"),
        TabInfo("Git/LeetCode", Icons.Default.Settings, "GitHub & LeetCode Analyzer"),
        TabInfo("Job Tracker", Icons.Default.Settings, "Job Application Tracker"),
        TabInfo("Portfolio", Icons.Default.AccountCircle, "Achievement Portfolio"),
        TabInfo("Mock Interview", Icons.Default.Person, "AI Mock Interviewer"),
        TabInfo("Interviews", Icons.Default.Check, "Recruiter Invitations")
    )

    Column(modifier = modifier.fillMaxSize()) {
        // Horizontal scroll category selection
        ScrollableTabRow(
            selectedTabIndex = tabs.indexOfFirst { it.id == activeTab }.coerceAtLeast(0),
            edgePadding = 12.dp,
            divider = {},
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
        ) {
            tabs.forEach { tab ->
                val selected = tab.id == activeTab
                Tab(
                    selected = selected,
                    onClick = { viewModel.selectedStudentTab.value = tab.id },
                    modifier = Modifier.testTag("tab_${tab.id.lowercase().replace(" ", "_")}"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = tab.id,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (activeTab) {
                "Overview" -> StudentOverviewScreen(viewModel)
                "AI Mentor" -> MentorScreen(viewModel)
                "Roadmap" -> RoadmapScreen(viewModel)
                "Resume AI" -> ResumeIntelligenceScreen(viewModel)
                "Git/LeetCode" -> GitLeetCodeScreen(viewModel)
                "Job Tracker" -> JobTrackerScreen(viewModel)
                "Portfolio" -> PortfolioScreen(viewModel)
                "Mock Interview" -> MockInterviewScreen(viewModel)
                "Interviews" -> StudentInvitationsScreen(viewModel)
            }
        }
    }
}

private data class TabInfo(val id: String, val icon: ImageVector, val desc: String)

// ----------------------------------------------------
// 1. AI Mentor (Virtual Faculty) Screen
// ----------------------------------------------------
@Composable
fun MentorScreen(viewModel: CareerViewModel) {
    val messages by viewModel.mentorMessages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isMentorLoading.collectAsStateWithLifecycle()
    var questionText by remember { mutableStateOf("") }
    val listState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "AI Mentor (Virtual Faculty)",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Ask about Spring Boot, data structures, backend engineering or systems design to generate explanation, exercise, and project recommendations.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Chat Message History
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(listState)
                .padding(bottom = 12.dp)
        ) {
            messages.forEach { msg ->
                val isUser = msg.role == "User"
                ListItem(
                    headlineContent = {
                        Text(
                            text = if (isUser) "You" else "AI Mentor • Faculty",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isUser) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                            )
                        )
                    },
                    supportingContent = {
                        Text(
                            text = msg.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = if (isUser) Icons.Default.Person else Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isUser) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = if (isUser) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                )
            }

            if (isLoading) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "AI Faculty is formulating explanation...", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Input Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = questionText,
                onValueChange = { questionText = it },
                placeholder = { Text("Ask: 'Explain Inversion of Control in Spring Boot'") },
                modifier = Modifier.weight(1f).testTag("mentor_prompt_input"),
                shape = RoundedCornerShape(16.dp),
                maxLines = 3,
                textStyle = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = {
                    if (questionText.isNotBlank()) {
                        viewModel.askMentorQuestion(questionText)
                        questionText = ""
                    }
                },
                enabled = !isLoading && questionText.isNotBlank(),
                modifier = Modifier.testTag("mentor_ask_button").height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send prompt")
            }

            IconButton(
                onClick = { viewModel.clearMentorHistory() },
                modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear History", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// ----------------------------------------------------
// 2. Interactive Learning Roadmap Screen
// ----------------------------------------------------
@Composable
fun RoadmapScreen(viewModel: CareerViewModel) {
    val steps by viewModel.roadmap.collectAsStateWithLifecycle()
    val isLoading by viewModel.isRoadmapLoading.collectAsStateWithLifecycle()
    val profile by viewModel.studentProfile.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Personalized Learning Roadmap",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "AI generated milestones mapping target role: ${profile.careerGoals}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { viewModel.generateCareerRoadmap() },
                enabled = !isLoading,
                modifier = Modifier.testTag("roadmap_generate_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Regen", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (steps.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Icon(imageVector = Icons.Default.List, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "No roadmap milestones available.", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text(text = "Press 'Regen' dynamically to let Gemini analyze your profile goals & build daily/weekly chapters.", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(steps) { step ->
                    val colorBrush = when (step.type) {
                        "Daily Task" -> MaterialTheme.colorScheme.primary
                        "Weekly Goal" -> MaterialTheme.colorScheme.secondary
                        "Project" -> MaterialTheme.colorScheme.tertiary
                        "Interview Prep" -> Color(0xFFE57373)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (step.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(12.dp)
                            ),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (step.isCompleted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = step.isCompleted,
                                onCheckedChange = { viewModel.toggleRoadmapStepCompleted(step) },
                                modifier = Modifier.testTag("step_check_${step.id}")
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "W${step.week}D${step.day}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = colorBrush,
                                        modifier = Modifier
                                            .background(colorBrush.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )

                                    Text(
                                        text = step.type,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = colorBrush
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = step.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = if (step.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                    )
                                )

                                Text(
                                    text = step.desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 3. Resume Intelligence Screen
// ----------------------------------------------------
@Composable
fun ResumeIntelligenceScreen(viewModel: CareerViewModel) {
    val profile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val isLoading by viewModel.isResumeLoading.collectAsStateWithLifecycle()
    val uploadedFileName by viewModel.uploadedFileName.collectAsStateWithLifecycle()
    val uploadedFileSize by viewModel.uploadedFileSize.collectAsStateWithLifecycle()
    val uploadedText by viewModel.uploadedTextState.collectAsStateWithLifecycle()

    var rawText by remember { mutableStateOf("") }

    LaunchedEffect(uploadedText) {
        if (uploadedText.isNotBlank()) {
            rawText = uploadedText
        }
    }

    val context = LocalContext.current
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            viewModel.loadResumeFromUri(context, it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Resume Intelligence (ATS Analyzer)",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Select/Drop a professional resume, try our pre-loaded industry presets, or paste text descriptions directly below. Gemini parses ATS criteria contextually to extract competencies.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Drop file area
        val primaryColor = MaterialTheme.colorScheme.primary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { fileLauncher.launch("*/*") }
                .testTag("file_upload_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f)
            ),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share, 
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "Drag & Drop Resume File Here",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Click to browse system files (TXT, PDF, DOCX)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Preset Chips
        Text(
            text = "Select Industry Presets:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "Senior_Spring_Boot_Architect.pdf" to "Spring Boot Architect",
                "Alex_Kotlinov_Mobile_Engineer.pdf" to "Android Kotlin Dev",
                "Jane_Dev_FullStack_React.docx" to "Fullstack React Pro"
            ).forEach { (fileName, label) ->
                SuggestionChip(
                    onClick = {
                        viewModel.loadResumeFromUri(context, android.net.Uri.parse("preset://$fileName"))
                    },
                    label = { Text(label) },
                    icon = { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    modifier = Modifier.testTag("preset_${label.replace(" ", "_")}")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Selected File Meta Info
        if (uploadedFileName != null) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text(
                                text = uploadedFileName ?: "",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = uploadedFileSize ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            viewModel.uploadedFileName.value = null
                            viewModel.uploadedFileSize.value = null
                            viewModel.uploadedTextState.value = ""
                            rawText = ""
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear file", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        Text(
            text = "Or paste Resume Text manually:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
        )

        OutlinedTextField(
            value = rawText,
            onValueChange = { rawText = it },
            placeholder = { Text("Paste professional work descriptions, education details, and technology stack headers...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .testTag("resume_input_text"),
            shape = RoundedCornerShape(12.dp),
            maxLines = 10
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (rawText.isNotBlank()) {
                    viewModel.uploadAndAnalyzeResume(rawText)
                }
            },
            enabled = !isLoading && rawText.isNotBlank(),
            modifier = Modifier.fillMaxWidth().testTag("resume_analyze_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyzing Resume structure...")
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null)
                    Text("Calculate ATS Integrity Score")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ATS Assessment Card Score
        if (profile.resumeAtsScore > 0) {
            Text(
                text = "Intelligence Outcome Report",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(68.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        ) {
                            Text(
                                text = "${profile.resumeAtsScore}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "ATS Score Evaluation",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = when (profile.resumeAtsScore) {
                                    in 85..100 -> "EXCELLENT alignment. Minimal adjustments needed."
                                    in 70..84 -> "GOOD BASE. Strengthen metric accomplishments."
                                    else -> "IMMEDIATE REVISIONS recommended to bypass standard ATS filters."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Overview:",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = profile.resumeAnalysis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Recommendations & Extracted Insights:",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = profile.careerReport,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 4. GitHub & LeetCode profile evaluation Screen
// ----------------------------------------------------
@Composable
fun GitLeetCodeScreen(viewModel: CareerViewModel) {
    val profile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val isGitLoading by viewModel.isGithubLoading.collectAsStateWithLifecycle()
    val isLeetLoading by viewModel.isLeetcodeLoading.collectAsStateWithLifecycle()

    var gitInput by remember { mutableStateOf(profile.githubUsername) }
    var leetInput by remember { mutableStateOf(profile.leetcodeUsername) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "GitHub & LeetCode Profiler",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Analyze code index quality (GitHub repo architectures) and logical DSA problem metrics (LeetCode badges) with AI diagnostics.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // GitHub Row
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(text = "GitHub Repository Intelligence", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = gitInput,
                        onValueChange = { gitInput = it },
                        label = { Text("GitHub Username") },
                        modifier = Modifier.weight(1f).testTag("github_input"),
                        singleLine = true
                    )
                    Button(
                        onClick = { viewModel.analyzeGitHubProfile(gitInput) },
                        enabled = !isGitLoading && gitInput.isNotBlank(),
                        modifier = Modifier.testTag("github_analyze_button").height(56.dp)
                    ) {
                        if (isGitLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        } else {
                            Text("Assess")
                        }
                    }
                }

                if (profile.githubScore > 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Score: ${profile.githubScore}/100",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(text = "Analysis: ${profile.githubAnalysis}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // LeetCode Row
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Text(text = "LeetCode Performance Insight", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = leetInput,
                        onValueChange = { leetInput = it },
                        label = { Text("LeetCode Username") },
                        modifier = Modifier.weight(1f).testTag("leetcode_input"),
                        singleLine = true
                    )
                    Button(
                        onClick = { viewModel.analyzeLeetCodeProfile(leetInput) },
                        enabled = !isLeetLoading && leetInput.isNotBlank(),
                        modifier = Modifier.testTag("leetcode_analyze_button").height(56.dp)
                    ) {
                        if (isLeetLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        } else {
                            Text("Assess")
                        }
                    }
                }

                if (profile.leetcodeScore > 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Score: ${profile.leetcodeScore}/100",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Text(text = "Analysis: ${profile.leetcodeAnalysis}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// ----------------------------------------------------
// 5. Job Application Tracker Screen
// ----------------------------------------------------
@Composable
fun JobTrackerScreen(viewModel: CareerViewModel) {
    val list by viewModel.jobApplications.collectAsStateWithLifecycle()

    var showAddForm by remember { mutableStateOf(false) }
    var compName by remember { mutableStateOf("") }
    var rName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var statusSelection by remember { mutableStateOf("Applied") }

    val statusOptions = listOf("Applied", "OA Round", "Technical Interview", "HR Interview", "Offer", "Rejection")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Job Application Tracker",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Track active pipeline stages from Applied through to Offer or Rejection.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { showAddForm = !showAddForm },
                modifier = Modifier.testTag("job_track_toggle_form").background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            ) {
                Icon(
                    imageVector = if (showAddForm) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = "Toggle tracker form",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(visible = showAddForm) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Add Target Job Role", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = compName,
                            onValueChange = { compName = it },
                            label = { Text("Company Name") },
                            modifier = Modifier.weight(1f).testTag("job_input_company"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = rName,
                            onValueChange = { rName = it },
                            label = { Text("Role Title") },
                            modifier = Modifier.weight(1f).testTag("job_input_role"),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Internal Notes (Assessment notes...)") },
                        modifier = Modifier.fillMaxWidth().testTag("job_input_notes"),
                        maxLines = 2
                    )

                    // Simple status radio/button row
                    Text(text = "Select Application Stage:", style = MaterialTheme.typography.labelSmall)
                    ScrollableTabRow(
                        selectedTabIndex = statusOptions.indexOf(statusSelection),
                        edgePadding = 0.dp,
                        divider = {},
                        containerColor = Color.Transparent
                    ) {
                        statusOptions.forEach { st ->
                            Tab(
                                selected = statusSelection == st,
                                onClick = { statusSelection = st },
                                text = { Text(st, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (compName.isNotBlank() && rName.isNotBlank()) {
                                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                viewModel.addJobTrack(
                                    company = compName,
                                    role = rName,
                                    status = statusSelection,
                                    notes = notes,
                                    dateApplied = sdf.format(Date())
                                )
                                compName = ""
                                rName = ""
                                notes = ""
                                statusSelection = "Applied"
                                showAddForm = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("job_save_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Add to pipeline")
                    }
                }
            }
        }

        if (list.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No active applications in pipeline. Click '+' to start tracking.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(list) { app ->
                    val statusColor = when (app.status) {
                        "Offer" -> Color(0xFF4CAF50)
                        "Rejection" -> MaterialTheme.colorScheme.error
                        "Technical Interview", "HR Interview" -> MaterialTheme.colorScheme.primary
                        "OA Round" -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = app.companyName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text(text = app.roleName, style = MaterialTheme.typography.bodySmall)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = app.status,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = statusColor,
                                        modifier = Modifier.background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                    IconButton(
                                        onClick = { viewModel.deleteJobTrack(app) },
                                        modifier = Modifier.size(24.dp).testTag("job_delete_${app.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove Tracker", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            if (app.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Notes: ${app.notes}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Date Registered: ${app.dateApplied}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)

                            // Quick progress state change buttons
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                statusOptions.forEach { opt ->
                                    if (opt != app.status) {
                                        Button(
                                            onClick = { viewModel.updateJobTrackStatus(app, opt) },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text(text = "Move $opt", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 6. Achievement Portfolio Screen
// ----------------------------------------------------
@Composable
fun PortfolioScreen(viewModel: CareerViewModel) {
    val items by viewModel.portfolioItems.collectAsStateWithLifecycle()

    var showForm by remember { mutableStateOf(false) }
    var categorySelection by remember { mutableStateOf("Projects") }
    var titleText by remember { mutableStateOf("") }
    var organizationText by remember { mutableStateOf("") }
    var descText by remember { mutableStateOf("") }

    val categories = listOf("Projects", "Certifications", "Internships", "Research papers", "Hackathons", "Awards")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Achievement Portfolio",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "A consolidated index of your hackathons, certs, research, and project archives.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { showForm = !showForm },
                modifier = Modifier.testTag("portfolio_toggle_form").background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            ) {
                Icon(
                    imageVector = if (showForm) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = "Toggle portfolio form",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(visible = showForm) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Register Portfolio Entry", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)

                    // Tabs selection for Category
                    ScrollableTabRow(
                        selectedTabIndex = categories.indexOf(categorySelection),
                        edgePadding = 0.dp,
                        divider = {},
                        containerColor = Color.Transparent
                    ) {
                        categories.forEach { cat ->
                            Tab(
                                selected = categorySelection == cat,
                                onClick = { categorySelection = cat },
                                text = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = titleText,
                        onValueChange = { titleText = it },
                        label = { Text("Title / Designation") },
                        modifier = Modifier.fillMaxWidth().testTag("portfolio_input_title"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = organizationText,
                        onValueChange = { organizationText = it },
                        label = { Text("Organization / Institution") },
                        modifier = Modifier.fillMaxWidth().testTag("portfolio_input_org"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = descText,
                        onValueChange = { descText = it },
                        label = { Text("Impact & Technology highlights") },
                        modifier = Modifier.fillMaxWidth().testTag("portfolio_input_desc"),
                        maxLines = 3
                    )

                    Button(
                        onClick = {
                            if (titleText.isNotBlank() && organizationText.isNotBlank()) {
                                val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                                viewModel.addPortfolioEntry(
                                    category = categorySelection,
                                    title = titleText,
                                    org = organizationText,
                                    desc = descText,
                                    date = sdf.format(Date())
                                )
                                titleText = ""
                                organizationText = ""
                                descText = ""
                                showForm = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("portfolio_save_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Add to Portfolio")
                    }
                }
            }
        }

        if (items.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No items registered. Click '+' to design your professional marketplace card.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { port ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    val catColor = when (port.category) {
                                        "Projects" -> MaterialTheme.colorScheme.primary
                                        "Certifications" -> MaterialTheme.colorScheme.secondary
                                        "Internships" -> MaterialTheme.colorScheme.tertiary
                                        else -> Color(0xFFE57373)
                                    }
                                    Box(modifier = Modifier.size(8.dp).background(catColor, CircleShape))
                                    Text(
                                        text = port.category,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = catColor
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.removePortfolioEntry(port) },
                                    modifier = Modifier.size(24.dp).testTag("portfolio_delete_${port.id}")
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove Portfolio", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = port.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            Text(text = "Issued by: ${port.organization}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            
                            if (port.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = port.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Date: ${port.date}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 7. AI Mock Interviewer Room Screen
// ----------------------------------------------------
@Composable
fun MockInterviewScreen(viewModel: CareerViewModel) {
    val messages by viewModel.mockInterviewMessages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isMockInterviewLoading.collectAsStateWithLifecycle()
    val profile by viewModel.studentProfile.collectAsStateWithLifecycle()

    var userResponseText by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Spring Boot Architect") }
    val roles = listOf("Spring Boot Architect", "Android Kmp Engineer", "Full Stack Developer", "Systems Architect")

    val chatScroll = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "AI Mock Interviewer Room",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Synthesizes live technical or behavioral adaptive evaluations. Speak response directly below to proceed.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Select Role header selector
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            roles.forEach { role ->
                val selected = selectedRole == role
                FilterChip(
                    selected = selected,
                    onClick = {
                        selectedRole = role
                        viewModel.startMockInterview(role)
                    },
                    label = { Text(role, fontSize = 11.sp) },
                    modifier = Modifier.testTag("role_chip_${role.replace(" ", "_")}")
                )
            }
        }

        // Chat logs
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(chatScroll)
                .padding(bottom = 12.dp)
        ) {
            messages.forEach { msg ->
                val isUser = msg.role == "User"
                ListItem(
                    headlineContent = {
                        Text(
                            text = if (isUser) "You (Candidate)" else "AI Lead Interviewer",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isUser) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                            )
                        )
                    },
                    supportingContent = {
                        Text(
                            text = msg.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = if (isUser) Icons.Default.Person else Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isUser) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = if (isUser) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                )
            }

            if (isLoading) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Interviewer is computing feedback & adaptive question...", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Input response bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = userResponseText,
                onValueChange = { userResponseText = it },
                placeholder = { Text("Speak or answer: 'JVM threads are mapped directly to OS kernel schemas...'") },
                modifier = Modifier.weight(1f).testTag("mock_reply_input"),
                shape = RoundedCornerShape(16.dp),
                maxLines = 3,
                textStyle = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = {
                    if (userResponseText.isNotBlank()) {
                        viewModel.sendMockInterviewResponse(userResponseText, selectedRole)
                        userResponseText = ""
                    }
                },
                enabled = !isLoading && userResponseText.isNotBlank(),
                modifier = Modifier.testTag("mock_send_button").height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send response")
            }

            IconButton(
                onClick = { viewModel.clearMockInterviewHistory() },
                modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset Interview Session", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// ----------------------------------------------------
// 8. Student received Recruiter Invitations Screen
// ----------------------------------------------------
@Composable
fun StudentInvitationsScreen(viewModel: CareerViewModel) {
    val invites by viewModel.invitations.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Recruiter Invitation System",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Review incoming interview offers sent directly by recruiters browsing the talent marketplace.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (invites.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No invitations received yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(invites) { invite ->
                    val colorBadge = when (invite.status) {
                        "Accepted" -> Color(0xFF4CAF50)
                        "Rejected" -> MaterialTheme.colorScheme.error
                        "Scheduled" -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.secondary
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = invite.companyName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text(text = invite.roleName, style = MaterialTheme.typography.bodySmall)
                                }

                                Text(
                                    text = invite.status,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = colorBadge,
                                    modifier = Modifier.background(colorBadge.copy(alpha = 0.15f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Required: ${invite.requiredSkills}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Text(text = "Talent Match Integrity: ${invite.matchPercentage}%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }

                            if (invite.scheduledTime.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                                    Text(text = "Time slot: ${invite.scheduledTime}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                }
                            }

                            if (invite.status == "Pending") {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.updateStudentInvitationStatus(invite, "Accepted") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                        modifier = Modifier.weight(1f).testTag("accept_${invite.id}"),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Accept", color = Color.White)
                                    }

                                    Button(
                                        onClick = { 
                                            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 3); set(Calendar.HOUR_OF_DAY, 14); set(Calendar.MINUTE, 0) }
                                            viewModel.updateStudentInvitationStatus(invite, "Scheduled", sdf.format(cal.time) + " PM") 
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        modifier = Modifier.weight(1.2f).testTag("schedule_${invite.id}"),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Schedule Slot")
                                    }

                                    Button(
                                        onClick = { viewModel.updateStudentInvitationStatus(invite, "Rejected") },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                        modifier = Modifier.weight(1f).testTag("reject_${invite.id}"),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Decline", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 9. Student Overview & Career Metrics Diagnostics
// ----------------------------------------------------
data class RadarItem(val name: String, val score: Int)

@Composable
fun StudentOverviewScreen(viewModel: CareerViewModel) {
    val profile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val portfolioItems by viewModel.portfolioItems.collectAsStateWithLifecycle()
    val roadmap by viewModel.roadmap.collectAsStateWithLifecycle()

    var showProfileEditor by remember { mutableStateOf(false) }

    var editName by remember { mutableStateOf(profile.name) }
    var editGoals by remember { mutableStateOf(profile.careerGoals) }
    var editSkills by remember { mutableStateOf(profile.currentSkills) }
    var editGithub by remember { mutableStateOf(profile.githubUsername) }
    var editLeetcode by remember { mutableStateOf(profile.leetcodeUsername) }

    LaunchedEffect(profile) {
        editName = profile.name
        editGoals = profile.careerGoals
        editSkills = profile.currentSkills
        editGithub = profile.githubUsername
        editLeetcode = profile.leetcodeUsername
    }

    val radarData = remember(profile, portfolioItems, roadmap) {
        val completedTasksCount = roadmap.count { it.isCompleted }
        
        val dsaScore = (50.0 + (if (profile.leetcodeUsername.isNotBlank()) 15.0 else 0.0) + (profile.leetcodeScore * 0.35)).coerceIn(30.0, 100.0).toInt()
        val devScore = (50.0 + (if (profile.githubUsername.isNotBlank()) 15.0 else 0.0) + (profile.githubScore * 0.35)).coerceIn(30.0, 100.0).toInt()
        val commScore = 75
        val projScore = (50 + (portfolioItems.count { it.category == "Projects" } * 10)).coerceIn(40, 100)
        val sysDesignScore = (55 + (completedTasksCount * 5)).coerceIn(45, 100)
        val probSolvingScore = ((dsaScore + devScore) / 2).coerceIn(40, 100)

        listOf(
            RadarItem("DSA", dsaScore),
            RadarItem("Development", devScore),
            RadarItem("Comm", commScore),
            RadarItem("Projects", projScore),
            RadarItem("Sys Design", sysDesignScore),
            RadarItem("Problem Solving", probSolvingScore)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hi, ${profile.name} 👋",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = profile.careerGoals.ifBlank { "Setup career preferences" },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showProfileEditor = !showProfileEditor },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("edit_profile_toggle")
                    ) {
                        Text(if (showProfileEditor) "Collapse" else "Edit Goal Profiles", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                AnimatedVisibility(visible = showProfileEditor) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Configure Career Contexts", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))

                            OutlinedTextField(
                                value = editName,
                                onValueChange = { editName = it },
                                label = { Text("Student Name") },
                                modifier = Modifier.fillMaxWidth().testTag("edit_profile_name"),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = editGoals,
                                onValueChange = { editGoals = it },
                                label = { Text("Primary Career Target") },
                                modifier = Modifier.fillMaxWidth().testTag("edit_profile_goals"),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = editSkills,
                                onValueChange = { editSkills = it },
                                label = { Text("Core Skills (Comma separated)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = editGithub,
                                    onValueChange = { editGithub = it },
                                    label = { Text("GitHub User") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = editLeetcode,
                                    onValueChange = { editLeetcode = it },
                                    label = { Text("LeetCode User") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.updateProfileInfo(editName, editGoals, editSkills, editGithub, editLeetcode)
                                    showProfileEditor = false
                                },
                                modifier = Modifier.fillMaxWidth().testTag("profile_save_changes"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Save Profile Contexts")
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Job Readiness Score", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${profile.jobReadinessScore.coerceAtLeast(60)}/100",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("Based on Resume & Profile", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Placement Probability", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${profile.placementProbability.coerceAtLeast(55)}%",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF4CAF50)
                            )
                            Text("Dynamic Market Sync", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text("AI Skill Radar Metrics", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                        Text(
                            text = "Represents real-time capabilities across core DSA, development structures, mock evaluations, and system design integrations.",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        val primaryColor = MaterialTheme.colorScheme.primary
                        val secondaryColor = MaterialTheme.colorScheme.secondary
                        Canvas(
                            modifier = Modifier
                                .size(240.dp)
                                .testTag("skills_radar_canvas")
                        ) {
                            drawRadarChart(radarData, primaryColor, secondaryColor)
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawRadarChart(
    items: List<RadarItem>,
    primaryColor: Color,
    secondaryColor: Color
) {
    if (items.isEmpty()) return

    val center = Offset(size.width / 2f, size.height / 2f)
    val maxRadius = (size.width.coerceAtMost(size.height) / 2f) * 0.72f

    val sides = items.size
    val angleStep = (2 * Math.PI) / sides

    val webLevels = 4
    for (level in 1..webLevels) {
        val radius = maxRadius * (level.toFloat() / webLevels)
        val path = Path()
        
        for (i in 0 until sides) {
            val angle = i * angleStep - Math.PI / 2
            val x = center.x + radius * cos(angle).toFloat()
            val y = center.y + radius * sin(angle).toFloat()
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()
        drawPath(
            path = path,
            color = Color.LightGray.copy(alpha = 0.4f),
            style = Stroke(width = 1.dp.toPx())
        )
    }

    for (i in 0 until sides) {
        val angle = i * angleStep - Math.PI / 2
        val x = center.x + maxRadius * cos(angle).toFloat()
        val y = center.y + maxRadius * sin(angle).toFloat()
        
        drawLine(
            color = Color.LightGray.copy(alpha = 0.5f),
            start = center,
            end = Offset(x, y),
            strokeWidth = 1.dp.toPx()
        )
    }

    val labelPaint = android.graphics.Paint().apply {
        isAntiAlias = true
        textSize = 9.dp.toPx()
        color = android.graphics.Color.DKGRAY
        textAlign = android.graphics.Paint.Align.CENTER
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    for (i in 0 until sides) {
        val angle = i * angleStep - Math.PI / 2
        val labelRadius = maxRadius + 14.dp.toPx()
        val x = center.x + labelRadius * cos(angle).toFloat()
        val y = center.y + labelRadius * sin(angle).toFloat()
        
        drawContext.canvas.nativeCanvas.drawText(
            items[i].name + " (${items[i].score})",
            x,
            y + 3.dp.toPx(),
            labelPaint
        )
    }

    val scorePath = Path()
    val pointsList = mutableListOf<Offset>()
    
    for (i in 0 until sides) {
        val angle = i * angleStep - Math.PI / 2
        val normalizedScore = items[i].score.coerceIn(0, 100) / 100f
        val scoreRadius = maxRadius * normalizedScore
        val x = center.x + scoreRadius * cos(angle).toFloat()
        val y = center.y + scoreRadius * sin(angle).toFloat()
        
        val offset = Offset(x, y)
        pointsList.add(offset)
        
        if (i == 0) {
            scorePath.moveTo(x, y)
        } else {
            scorePath.lineTo(x, y)
        }
    }
    scorePath.close()

    drawPath(
        path = scorePath,
        brush = Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.45f),
                secondaryColor.copy(alpha = 0.15f)
            )
        )
    )

    drawPath(
        path = scorePath,
        color = primaryColor,
        style = Stroke(width = 2.dp.toPx())
    )

    pointsList.forEach { pt ->
        drawCircle(
            color = secondaryColor,
            radius = 4.dp.toPx(),
            center = pt
        )
        drawCircle(
            color = Color.White,
            radius = 2.dp.toPx(),
            center = pt
        )
    }
}
