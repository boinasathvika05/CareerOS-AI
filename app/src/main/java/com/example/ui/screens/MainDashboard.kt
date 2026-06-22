package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.CareerViewModel
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainDashboard(viewModel: CareerViewModel) {
    val isRecruiter by viewModel.isRecruiterPortal.collectAsStateWithLifecycle()
    val profile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val portfolioItems by viewModel.portfolioItems.collectAsStateWithLifecycle()
    val roadmap by viewModel.roadmap.collectAsStateWithLifecycle()

    var showProfileEditor by remember { mutableStateOf(false) }

    // State bindings for Profile editor
    var editName by remember { mutableStateOf(profile.name) }
    var editGoals by remember { mutableStateOf(profile.careerGoals) }
    var editSkills by remember { mutableStateOf(profile.currentSkills) }
    var editGithub by remember { mutableStateOf(profile.githubUsername) }
    var editLeetcode by remember { mutableStateOf(profile.leetcodeUsername) }

    // Sync input values with DB state when db finishes loading or changes
    LaunchedEffect(profile) {
        editName = profile.name
        editGoals = profile.careerGoals
        editSkills = profile.currentSkills
        editGithub = profile.githubUsername
        editLeetcode = profile.leetcodeUsername
    }

    // Dynamic radar values calculation
    val radarData = remember(profile, portfolioItems, roadmap) {
        val completedTasksCount = roadmap.count { it.isCompleted }
        
        val dsaScore = (50.0 + (if (profile.leetcodeUsername.isNotBlank()) 15.0 else 0.0) + (profile.leetcodeScore * 0.35)).coerceIn(30.0, 100.0).toInt()
        val devScore = (50.0 + (if (profile.githubUsername.isNotBlank()) 15.0 else 0.0) + (profile.githubScore * 0.35)).coerceIn(30.0, 100.0).toInt()
        val commScore = 75 // Mock interview base competency
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

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.secondary
                                        )
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "CareerOS AI",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Autonomous Career Ecosystem",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    // Dual Account tab-styled Portal Switcher Target
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Button(
                            onClick = { viewModel.isRecruiterPortal.value = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isRecruiter) MaterialTheme.colorScheme.primary else Color.Transparent,
                                contentColor = if (!isRecruiter) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("student_role_button")
                        ) {
                            Text("Student", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.isRecruiterPortal.value = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecruiter) MaterialTheme.colorScheme.primary else Color.Transparent,
                                contentColor = if (isRecruiter) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("recruiter_role_button")
                        ) {
                            Text("Recruiter", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Respecting bottom gestures safe zone padding
            Box(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars))
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isRecruiter) {
                RecruiterPortal(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            } else {
                StudentPortal(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            }
        }
    }
}
