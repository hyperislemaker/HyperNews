package com.example.telegramstyle.newsapp.presentation.screens.premium

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.telegramstyle.newsapp.domain.model.SubscriptionPlan

@Composable
fun PremiumPaywallScreen(
    onDismiss: () -> Unit,
    onPurchase: (SubscriptionPlan) -> Unit,
    onRestore: () -> Unit
) {
    var selectedPlan by remember { mutableStateOf(SubscriptionPlan.YEARLY) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Premium'a Geç",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tüm özelliklerin kilidini aç",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Özellikler
        PremiumFeatureItem("Reklamsız deneyim")
        PremiumFeatureItem("Sınırsız RSS kaynağı")
        PremiumFeatureItem("90 gün haber arşivi")
        PremiumFeatureItem("Gelişmiş arama filtreleri")
        PremiumFeatureItem("Özel temalar")
        PremiumFeatureItem("Okuma istatistikleri")

        Spacer(modifier = Modifier.height(24.dp))

        // Plan seçimi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlanCard(
                plan = SubscriptionPlan.MONTHLY,
                price = "₺29.99/ay",
                isSelected = selectedPlan == SubscriptionPlan.MONTHLY,
                onClick = { selectedPlan = SubscriptionPlan.MONTHLY },
                modifier = Modifier.weight(1f)
            )
            PlanCard(
                plan = SubscriptionPlan.YEARLY,
                price = "₺249.99/yıl",
                subtitle = "2 ay bedava",
                isSelected = selectedPlan == SubscriptionPlan.YEARLY,
                onClick = { selectedPlan = SubscriptionPlan.YEARLY },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onPurchase(selectedPlan) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("7 Gün Ücretsiz Dene")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onRestore) {
            Text("Satın alımları geri yükle")
        }

        TextButton(onClick = onDismiss) {
            Text("Şimdilik geç")
        }
    }
}

@Composable
private fun PremiumFeatureItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun PlanCard(
    plan: SubscriptionPlan,
    price: String,
    subtitle: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (plan == SubscriptionPlan.MONTHLY) "Aylık" else "Yıllık",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = price,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
