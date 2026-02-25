package com.example.telegramstyle.newsapp.presentation.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.telegramstyle.newsapp.domain.model.ReportedComment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminPanelViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Paneli") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Row
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Bildirilen Yorumlar") },
                    icon = { Icon(Icons.Default.Report, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("İstatistikler") },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Kullanıcılar") },
                    icon = { Icon(Icons.Default.People, contentDescription = null) }
                )
            }

            when (selectedTab) {
                0 -> ReportedCommentsTab(
                    reports = uiState.reportedComments,
                    isLoading = uiState.isLoading,
                    onDeleteComment = { viewModel.deleteComment(it) },
                    onRejectReport = { viewModel.rejectReport(it) }
                )
                1 -> StatisticsTab(
                    totalUsers = uiState.totalUsers,
                    totalComments = uiState.totalComments,
                    totalReactions = uiState.totalReactions,
                    activeUsers = uiState.activeUsers
                )
                2 -> UsersTab(
                    bannedUsers = uiState.bannedUsers,
                    onUnbanUser = { viewModel.unbanUser(it) }
                )
            }
        }
    }
}

@Composable
private fun ReportedCommentsTab(
    reports: List<ReportedComment>,
    isLoading: Boolean,
    onDeleteComment: (ReportedComment) -> Unit,
    onRejectReport: (ReportedComment) -> Unit
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (reports.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Bildirilen yorum yok")
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reports, key = { it.id }) { report ->
                ReportedCommentCard(
                    report = report,
                    onDelete = { onDeleteComment(report) },
                    onReject = { onRejectReport(report) }
                )
            }
        }
    }
}

@Composable
private fun ReportedCommentCard(
    report: ReportedComment,
    onDelete: () -> Unit,
    onReject: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = report.reporterUserName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${report.reportCount} bildirim",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = report.commentContent,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Sebep: ${report.reason}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onReject) {
                    Text("Reddet")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Sil")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Yorumu Sil") },
            text = { Text("Bu yorumu silmek istediğinize emin misiniz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Sil")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}


@Composable
private fun StatisticsTab(
    totalUsers: Int,
    totalComments: Int,
    totalReactions: Int,
    activeUsers: Int
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            StatCard(
                title = "Toplam Kullanıcı",
                value = totalUsers.toString(),
                icon = Icons.Default.People
            )
        }
        item {
            StatCard(
                title = "Aktif Kullanıcı (Son 7 gün)",
                value = activeUsers.toString(),
                icon = Icons.Default.TrendingUp
            )
        }
        item {
            StatCard(
                title = "Toplam Yorum",
                value = totalComments.toString(),
                icon = Icons.Default.Comment
            )
        }
        item {
            StatCard(
                title = "Toplam Tepki",
                value = totalReactions.toString(),
                icon = Icons.Default.ThumbUp
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

@Composable
private fun UsersTab(
    bannedUsers: List<BannedUser>,
    onUnbanUser: (String) -> Unit
) {
    if (bannedUsers.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Engelli kullanıcı yok")
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bannedUsers, key = { it.id }) { user ->
                BannedUserCard(
                    user = user,
                    onUnban = { onUnbanUser(user.id) }
                )
            }
        }
    }
}

@Composable
private fun BannedUserCard(
    user: BannedUser,
    onUnban: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Block,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.userName,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Sebep: ${user.reason}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onUnban) {
                Text("Engeli Kaldır")
            }
        }
    }
}

data class BannedUser(
    val id: String,
    val userName: String,
    val reason: String
)
