package sarkari.exam_ai.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import sarkari.exam_ai.ui.components.SarkariCard
import sarkari.exam_ai.ui.theme.*

// ==============================================================================
// ABOUT US SCREEN
// ==============================================================================

@Composable
fun AboutUsScreen(navController: NavController, paddingValues: PaddingValues) {
    AboutBaseScreen(paddingValues = paddingValues) {

        // ── Hero Banner ──────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(listOf(Color(0xFF1A56DB), Color(0xFF6366F1)))
                )
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = sarkari.exam_ai.R.drawable.app_logo),
                        contentDescription = "Sarkari Exam AI Logo",
                        modifier = Modifier.size(44.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "Sarkari Exam AI",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "India's AI-Powered Exam Preparation Partner",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Mission ─────────────────────────────────────────────────────────
        SarkariCard(padding = 20.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AboutSectionTitle("Our Mission", Icons.Default.RocketLaunch, PrimaryBlue)
                Text(
                    "We believe every Indian student deserves a world-class IAS/UPSC/State-exam coach — " +
                    "regardless of their city, income, or background. Sarkari Exam AI was built to be that " +
                    "coach: endlessly patient, always available in your language, and powered by cutting-edge AI.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── What We Offer ────────────────────────────────────────────────────
        SarkariCard(padding = 20.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AboutSectionTitle("What We Offer", Icons.Default.AutoAwesome, AccentGreen)
                FeatureBullet(Icons.Default.SupportAgent, "Riya AI Tutor", "Ask anything, anytime — in Hindi, English, Marathi, and more.")
                FeatureBullet(Icons.Default.Assignment, "Smart Mock Tests", "Adaptive, AI-generated tests aligned with real PYQ patterns.")
                FeatureBullet(Icons.Default.AutoFixHigh, "AI Notes Generator", "Instant, exam-focused notes downloadable as PDFs.")
                FeatureBullet(Icons.Default.CalendarMonth, "Personalized Study Planner", "AI-crafted timetables built around your exam date and strengths.")
                FeatureBullet(Icons.Default.Analytics, "Performance Analytics", "Track subject-wise improvement and readiness score daily.")
                FeatureBullet(Icons.Default.Newspaper, "Current Affairs Feed", "Live, category-filtered updates curated for your target exam.")
                FeatureBullet(Icons.Default.Group, "Study Partner Community", "Connect with fellow aspirants targeting the same exams.")
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Our Vision ───────────────────────────────────────────────────────
        SarkariCard(
            padding = 20.dp,
            borderColor = PrimaryBlue.copy(alpha = 0.2f),
            borderWidth = 1.dp
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AboutSectionTitle("Our Vision", Icons.Default.RemoveRedEye, Color(0xFF8B5CF6))
                Text(
                    "By 2030, we envision 10 million Indian students cracking their dream government jobs — " +
                    "empowered by AI that speaks their language, respects their budget, and adapts to their " +
                    "learning pace. We are building the future of Bharat, one aspirant at a time.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Stats row ────────────────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            AboutStatCard("50K+", "Active Students", Modifier.weight(1f))
            AboutStatCard("15+", "Exams Covered", Modifier.weight(1f))
            AboutStatCard("5+", "Languages", Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        // ── Contact ──────────────────────────────────────────────────────────
        SarkariCard(padding = 20.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AboutSectionTitle("Contact Us", Icons.Default.Email, AccentOrange)
                Text("📧  support@sarkariexamai.com", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                Text("🌐  www.sarkariexamai.com", style = MaterialTheme.typography.bodyMedium, color = PrimaryBlue)
                Spacer(Modifier.height(4.dp))
                Text(
                    "© 2024–2026 Sarkari Exam AI. All rights reserved. Built with ❤️ for Bharat's Future Leaders.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ==============================================================================
// PRIVACY POLICY SCREEN
// ==============================================================================

@Composable
fun PrivacyPolicyScreen(navController: NavController, paddingValues: PaddingValues) {
    AboutBaseScreen(paddingValues = paddingValues) {

        // ── Header ───────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF1A56DB), Color(0xFF3B82F6))))
                .padding(24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Shield, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Privacy Policy", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge, color = Color.White)
                        Text("Effective Date: April 20, 2026", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "Sarkari Exam AI («we», «our», «app») is committed to protecting your privacy. " +
                    "This policy explains what data we collect, how we use it, and your rights as a user — " +
                    "in compliance with India's IT Act 2000, DPDP Act 2023, and Google Play Developer policies.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Section 1: Information We Collect ────────────────────────────────
        PrivacySection(
            number = "1",
            title = "Information We Collect",
            icon = Icons.Default.Storage,
            color = PrimaryBlue
        ) {
            PrivacySubsection("Account & Authentication")
            BulletPoint("Full name, email address, and profile photo — collected via Google Sign-In (OAuth 2.0).")
            BulletPoint("We use Firebase Authentication by Google. We never see or store your Google password.")
            BulletPoint("Your student profile (exam target, language preference, level) is stored in Firebase Firestore.")
            Spacer(Modifier.height(8.dp))
            PrivacySubsection("Usage Data")
            BulletPoint("Study sessions, mock test scores, and feature interactions are recorded to improve personalization.")
            BulletPoint("Device model and OS version are collected for crash reporting and compatibility improvements.")
            Spacer(Modifier.height(8.dp))
            PrivacySubsection("AI Conversations")
            BulletPoint("Questions you ask Riya AI Tutor are processed to generate responses and are cached in Firestore to improve AI response speed.")
            BulletPoint("We do not share individual conversation content with any third party for advertising purposes.")
        }

        Spacer(Modifier.height(12.dp))

        // ── Section 2: How We Use Your Data ──────────────────────────────────
        PrivacySection(
            number = "2",
            title = "How We Use Your Information",
            icon = Icons.Default.Tune,
            color = AccentGreen
        ) {
            BulletPoint("To create and manage your personalized student account.")
            BulletPoint("To deliver AI-generated study plans, notes, and mock tests tailored to your target exam.")
            BulletPoint("To show relevant Current Affairs filtered by your exam category and language.")
            BulletPoint("To track your performance over time and provide meaningful analytics.")
            BulletPoint("To send you optional notifications about exam updates and study reminders (requires your permission).")
            BulletPoint("To ensure app security and prevent misuse of our platform.")
        }

        Spacer(Modifier.height(12.dp))

        // ── Section 3: Authentication (Google Sign-In) ───────────────────────
        PrivacySection(
            number = "3",
            title = "Authentication — Google Sign-In",
            icon = Icons.Default.VerifiedUser,
            color = Color(0xFF4285F4)
        ) {
            Text(
                "We use Google Sign-In (OAuth 2.0), a secure, industry-standard authentication service, " +
                "so you never need to create or remember a separate password for Sarkari Exam AI.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(8.dp))
            BulletPoint("Only your Google name, email, and profile photo are shared with us.")
            BulletPoint("Your Google account credentials are never accessible to our application.")
            BulletPoint("You can revoke access via Google Account → Security → Third-party apps at any time.")
            BulletPoint("We comply fully with Google's OAuth 2.0 and Firebase Authentication terms of service.")
        }

        Spacer(Modifier.height(12.dp))

        // ── Section 4: AI Services ────────────────────────────────────────────
        PrivacySection(
            number = "4",
            title = "AI Services",
            icon = Icons.Default.Psychology,
            color = Color(0xFF8B5CF6)
        ) {
            Text(
                "Sarkari Exam AI's tutoring, note generation, and question-paper analysis features are " +
                "powered by AI language models. Your queries are processed to generate educational content only.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(8.dp))
            BulletPoint("AI requests are encrypted in transit using HTTPS/TLS.")
            BulletPoint("We do not use your conversations to train public AI models.")
            BulletPoint("AI-generated results are educational and should be verified before exam use.")
            BulletPoint("Our AI services are designed to comply with Google Play's AI-content policies.")
        }

        Spacer(Modifier.height(12.dp))

        // ── Section 5: Data Sharing ───────────────────────────────────────────
        PrivacySection(
            number = "5",
            title = "Data Sharing & Third Parties",
            icon = Icons.Default.Share,
            color = AccentOrange
        ) {
            BulletPoint("We do NOT sell, rent, or trade your personal data.")
            BulletPoint("Firebase (Google LLC) — Authentication, Firestore database, and Storage.")
            BulletPoint("We share data with sub-processors only to the extent necessary to deliver our services.")
            BulletPoint("We may disclose data if legally required by Indian courts or law enforcement with valid orders.")
        }

        Spacer(Modifier.height(12.dp))

        // ── Section 6: Data Retention & Deletion ─────────────────────────────
        PrivacySection(
            number = "6",
            title = "Data Retention & Account Deletion",
            icon = Icons.Default.DeleteForever,
            color = AccentRed
        ) {
            BulletPoint("Your data is retained as long as your account is active.")
            BulletPoint("You can request full account deletion by contacting support@sarkariexamai.com.")
            BulletPoint("Upon deletion, all personal data including test history and AI chat logs will be permanently removed within 30 days.")
            BulletPoint("Anonymized, aggregated analytics (no personal identifiers) may be retained for platform improvement.")
        }

        Spacer(Modifier.height(12.dp))

        // ── Section 7: Your Rights ────────────────────────────────────────────
        PrivacySection(
            number = "7",
            title = "Your Rights (DPDP Act 2023)",
            icon = Icons.Default.Gavel,
            color = AccentGreen
        ) {
            Text(
                "Under India's Digital Personal Data Protection Act (DPDP) 2023, you have the right to:",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(8.dp))
            BulletPoint("Access the personal data we hold about you.")
            BulletPoint("Correct inaccurate or incomplete data.")
            BulletPoint("Erase your data (right to be forgotten).")
            BulletPoint("Withdraw consent for data processing at any time.")
            BulletPoint("Nominate a representative for data-related decisions.")
            Spacer(Modifier.height(8.dp))
            Text(
                "To exercise any right, email: support@sarkariexamai.com. We will respond within 72 hours.",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = PrimaryBlue
            )
        }

        Spacer(Modifier.height(12.dp))

        // ── Section 8: Children's Privacy ────────────────────────────────────
        PrivacySection(
            number = "8",
            title = "Children's Privacy",
            icon = Icons.Default.ChildCare,
            color = AccentSaffron
        ) {
            Text(
                "Sarkari Exam AI is intended for students aged 13 and above. We do not knowingly collect " +
                "personal data from children under 13. If you believe a child has registered, contact " +
                "support@sarkariexamai.com and we will delete the data promptly.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }

        Spacer(Modifier.height(12.dp))

        // ── Section 9: Changes to This Policy ────────────────────────────────
        PrivacySection(
            number = "9",
            title = "Updates to This Policy",
            icon = Icons.Default.Update,
            color = TextSecondary
        ) {
            Text(
                "We may update this Privacy Policy periodically. When we do, we will revise the " +
                "\"Effective Date\" at the top and notify you in-app. Continued use of the app after " +
                "changes constitutes acceptance of the updated policy.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }

        Spacer(Modifier.height(20.dp))

        // ── Contact Footer ────────────────────────────────────────────────────
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = PrimaryBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Questions? Contact Us", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                Text("📧  support@sarkariexamai.com", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                Text("🌐  www.sarkariexamai.com/privacy", style = MaterialTheme.typography.bodySmall, color = PrimaryBlue)
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ==============================================================================
// SHARED HELPER COMPOSABLES
// ==============================================================================

@Composable
fun AboutSectionTitle(title: String, icon: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
fun FeatureBullet(icon: ImageVector, title: String, subtitle: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(18.dp).padding(top = 2.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary, lineHeight = 18.sp)
        }
    }
}

@Composable
fun AboutStatCard(value: String, label: String, modifier: Modifier = Modifier) {
    SarkariCard(padding = 16.dp, modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(value, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge, color = PrimaryBlue)
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
fun PrivacySection(number: String, title: String, icon: ImageVector, color: Color, content: @Composable ColumnScope.() -> Unit) {
    SarkariCard(padding = 20.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(number, fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 12.sp)
                }
                Spacer(Modifier.width(10.dp))
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            }
            HorizontalDivider(color = color.copy(alpha = 0.15f))
            content()
        }
    }
}

@Composable
fun PrivacySubsection(title: String) {
    Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
}

@Composable
fun BulletPoint(text: String) {
    Row(modifier = Modifier.padding(start = 4.dp)) {
        Text("• ", fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 14.sp)
        Text(text, style = MaterialTheme.typography.bodySmall, color = TextSecondary, lineHeight = 18.sp)
    }
}

@Composable
fun AboutBaseScreen(paddingValues: PaddingValues, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        content()
    }
}
