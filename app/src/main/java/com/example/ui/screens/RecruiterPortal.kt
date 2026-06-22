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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.viewmodel.CareerViewModel

@Composable
fun RecruiterPortal(viewModel: CareerViewModel, modifier: Modifier = Modifier) {
    val activeTab by viewModel.selectedRecruiterTab.collectAsStateWithLifecycle()

    val tabs = listOf(
        RecruiterTab("Hiring Prefs", Icons.Default.Settings, "Hiring Requirements"),
        RecruiterTab("Talent Marketplace", Icons.Default.Person, "AI Talent Marketplace"),
        RecruiterTab("Invitations", Icons.Default.Send, "Interview pipeline")
    )

    Column(modifier = modifier.fillMaxSize()) {
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
                    onClick = { viewModel.selectedRecruiterTab.value = tab.id },
                    modifier = Modifier.testTag("recruiter_tab_${tab.id.lowercase().replace(" ", "_")}"),
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
                "Hiring Prefs" -> HiringPrefsScreen(viewModel)
                "Talent Marketplace" -> TalentMarketplaceScreen(viewModel)
                "Invitations" -> RecruiterInvitationsScreen(viewModel)
            }
        }
    }
}

private data class RecruiterTab(val id: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val explanation: String)

// ----------------------------------------------------
// 1. Hiring Preferences Requirements Screen
// ----------------------------------------------------
@Composable
fun HiringPrefsScreen(viewModel: CareerViewModel) {
    val prefs by viewModel.recruiterPrefs.collectAsStateWithLifecycle()

    var compText by remember { mutableStateOf(prefs.companyName) }
    var reqText by remember { mutableStateOf(prefs.requirements) }
    var filterText by remember { mutableStateOf(prefs.companyFilters) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Recruiter Portal Settings",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Define your corporate requirements, filters, and required skills. CareerOS AI's matching engine correlates student portfolios, scores and resumes to present the top ranked candidates.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = compText,
            onValueChange = { compText = it },
            label = { Text("Company Name") },
            modifier = Modifier.fillMaxWidth().testTag("rec_co_name"),
            singleLine = true
        )

        OutlinedTextField(
            value = reqText,
            onValueChange = { reqText = it },
            label = { Text("Hiring Objectives & Job Description") },
            modifier = Modifier.fillMaxWidth().height(120.dp).testTag("rec_objectives"),
            maxLines = 5
        )

        OutlinedTextField(
            value = filterText,
            onValueChange = { filterText = it },
            label = { Text("Required Skill Filtering keywords (e.g. Java, Spring, Kotlin)") },
            modifier = Modifier.fillMaxWidth().testTag("rec_filters"),
            singleLine = true
        )

        Button(
            onClick = { viewModel.updateRecruiterSettings(compText, reqText, filterText) },
            modifier = Modifier.fillMaxWidth().testTag("rec_save_prefs_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Text("Lock in Requirements")
            }
        }
    }
}

// ----------------------------------------------------
// 2. AI Talent Marketplace & Candidate Matching
// ----------------------------------------------------
@Composable
fun TalentMarketplaceScreen(viewModel: CareerViewModel) {
    val student by viewModel.studentProfile.collectAsStateWithLifecycle()
    val prefs by viewModel.recruiterPrefs.collectAsStateWithLifecycle()

    // We can simulate an array of candidates including the current logged-in student to showcase how the marketplace functions!
    val mockCandidates = remember(student, prefs) {
        // Compute dynamic match percentage for current student vs recruiter filters
        val filters = prefs.companyFilters.lowercase().split(",").map { it.trim() }.filter { it.isNotBlank() }
        var matchCount = 0
        val studentSkills = student.currentSkills.lowercase()

        filters.forEach { keyword ->
            if (studentSkills.contains(keyword)) matchCount++
        }

        val computedMatch = if (filters.isEmpty()) 80 else {
            val ratio = matchCount.toDouble() / filters.size
            (60 + (ratio * 40)).coerceIn(50.0, 99.0).toInt()
        }

        listOf(
            MarketplaceCandidate(
                name = student.name,
                targetRole = student.careerGoals,
                skills = student.currentSkills.ifBlank { "Kotlin, Java, Compose" },
                atsScore = student.resumeAtsScore.coerceAtLeast(70),
                readinessScore = student.jobReadinessScore.coerceAtLeast(65),
                matchPercent = computedMatch,
                isCurrentStudent = true,
                github = student.githubUsername,
                leetcode = student.leetcodeUsername
            ),
            MarketplaceCandidate(
                name = "Sarah Jenkins",
                targetRole = "Spring Boot Architect / Cloud Expert",
                skills = "Spring Boot, Docker, AWS, PostgreSQL, Java, Microservices",
                atsScore = 91,
                readinessScore = 88,
                matchPercent = if (prefs.companyFilters.lowercase().contains("spring") || prefs.companyFilters.lowercase().contains("java")) 94 else 78,
                isCurrentStudent = false,
                github = "sarahJ-cloud",
                leetcode = "sarah_lc_leet"
            ),
            MarketplaceCandidate(
                name = "Nikhil Sharma",
                targetRole = "Associate Mobile Engineer",
                skills = "Android SDK, Kotlin, Jetpack Compose, Coroutines, Room Dev",
                atsScore = 84,
                readinessScore = 80,
                matchPercent = if (prefs.companyFilters.lowercase().contains("kotlin") || prefs.companyFilters.lowercase().contains("compose")) 88 else 67,
                isCurrentStudent = false,
                github = "nik-sharmaAndroid",
                leetcode = "nikhil_12"
            )
        ).sortedByDescending { it.matchPercent }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "AI Talent Marketplace",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "AI Matches candidate skills, github quality, and problem-solving benchmarks against corporate preferences.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(mockCandidates) { candidate ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (candidate.isCurrentStudent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(text = candidate.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                    if (candidate.isCurrentStudent) {
                                        Text(
                                            text = "You",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                                            modifier = Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(text = candidate.targetRole, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            // Match Score Widget
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${candidate.matchPercent}% Match",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(text = "Talent Sync", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Compentencies: ${candidate.skills}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                                Text(text = "ATS: ${candidate.atsScore}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                Text(text = "Readiness: ${candidate.readinessScore}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(imageVector = Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.secondary)
                                Text(text = "Git: ${candidate.github}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Button(
                            onClick = {
                                viewModel.inviteCandidate(
                                    companyName = prefs.companyName,
                                    roleName = candidate.targetRole,
                                    candidateName = candidate.name,
                                    matchPercent = candidate.matchPercent
                                )
                            },
                            modifier = Modifier.fillMaxWidth().testTag("invite_btn_${candidate.name.lowercase().replace(" ", "_")}"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.MailOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Send Smart Interview Invitation")
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class MarketplaceCandidate(
    val name: String,
    val targetRole: String,
    val skills: String,
    val atsScore: Int,
    val readinessScore: Int,
    val matchPercent: Int,
    val isCurrentStudent: Boolean,
    val github: String,
    val leetcode: String
)

// ----------------------------------------------------
// 3. Recruiter Invitations Pipelines Screen
// ----------------------------------------------------
@Composable
fun RecruiterInvitationsScreen(viewModel: CareerViewModel) {
    val invites by viewModel.invitations.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Hiring pipeline & invitations",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Monitor active outreach progress, accepted dates, and candidate slot reservations.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (invites.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No invitations dispatched in pipeline yet. Use the Talent Marketplace tab to reach out.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(invites) { invite ->
                    val colorBadge = when (invite.status) {
                        "Accepted" -> Color(0xFF4CAF50)
                        "Rejected" -> MaterialTheme.colorScheme.error
                        "Scheduled" -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outline
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = invite.roleName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text(text = "Match Score: ${invite.matchPercentage}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                
                                if (invite.scheduledTime.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Scheduled: ${invite.scheduledTime}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                            }

                            Text(
                                text = invite.status,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = colorBadge,
                                modifier = Modifier.background(colorBadge.copy(alpha = 0.15f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
