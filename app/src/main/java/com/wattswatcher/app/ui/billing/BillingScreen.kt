package com.wattswatcher.app.ui.billing

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wattswatcher.app.WattsWatcherApplication
import com.wattswatcher.app.ui.animations.WattsWatcherAnimations
import com.wattswatcher.app.ui.theme.WattsWatcherColors
import com.wattswatcher.app.utils.BillDownloadHelper
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToDevices: () -> Unit = {},
    onNavigateToAnalytics: () -> Unit = {}
) {
    val app = LocalContext.current.applicationContext as WattsWatcherApplication
    val viewModel: BillingViewModel = viewModel {
        BillingViewModel(app.repository)
    }
    val state by viewModel.state.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var showPaymentDialog by remember { mutableStateOf(false) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        // Header with Download Button
        item {
            WattsWatcherAnimations.StaggeredAnimation(
                visible = true,
                index = 0
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Billing & Payments",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Manage your electricity bills",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                    
                    FilledTonalButton(
                        onClick = {
                            val billContent = viewModel.generateBillDownload()
                            if (billContent.isNotEmpty()) {
                                clipboardManager.setText(AnnotatedString(billContent))
                                BillDownloadHelper.saveBillToFile(context, billContent)
                            }
                        },
                        enabled = !state.isGeneratingBill
                    ) {
                        if (state.isGeneratingBill) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Download, contentDescription = null)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Download")
                    }
                }
            }
        }
        
        // Real-time Current Bill Card
        item {
            WattsWatcherAnimations.StaggeredAnimation(
                visible = true,
                index = 1
            ) {
                RealTimeBillCard(
                    currentBill = state.currentBillAmount,
                    currentUsage = state.currentUsage,
                    isProcessingPayment = state.isProcessingPayment,
                    onPayClick = { showPaymentDialog = true },
                    onNavigateToDevices = onNavigateToDevices
                )
            }
        }
        
        // Quick Payment Methods
        item {
            WattsWatcherAnimations.StaggeredAnimation(
                visible = true,
                index = 2
            ) {
                QuickPaymentMethods(
                    billAmount = state.currentBillAmount,
                    onPaymentSelect = { method ->
                        viewModel.initiatePayment(state.currentBillAmount, method)
                    }
                )
            }
        }
        
        // Historical Bill Summary
        state.billingData?.let { billingData ->
            item {
                WattsWatcherAnimations.StaggeredAnimation(
                    visible = true,
                    index = 3
                ) {
                    HistoricalBillCard(billingData = billingData)
                }
            }
        }
        
        // Tariff Structure
        item {
            WattsWatcherAnimations.StaggeredAnimation(
                visible = true,
                index = 4
            ) {
                TariffStructureCard(
                    tariffs = state.billingData?.tariffStructure ?: emptyList()
                )
            }
        }
        
        // Payment History
        item {
            Text(
                text = "Payment History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        items(state.billingData?.paymentHistory ?: emptyList()) { paymentRecord ->
            WattsWatcherAnimations.StaggeredAnimation(
                visible = true,
                index = 5
            ) {
                PaymentHistoryCard(paymentRecord = paymentRecord)
            }
        }
        
        // Payment Result
        state.paymentResult?.let { result ->
            item {
                PaymentResultCard(
                    result = result,
                    onDismiss = { viewModel.dismissPaymentResult() }
                )
            }
        }
        
        // Error handling
        state.error?.let { error ->
            item {
                ErrorCard(
                    error = error,
                    onDismiss = { viewModel.dismissError() }
                )
            }
        }
    }
    
    // Payment Options Dialog
    if (showPaymentDialog) {
        PaymentOptionsDialog(
            billAmount = state.currentBillAmount,
            onDismiss = { showPaymentDialog = false },
            onPaymentConfirm = { method ->
                viewModel.initiatePayment(state.currentBillAmount, method)
                showPaymentDialog = false
            }
        )
    }
}

@Composable
private fun RealTimeBillCard(
    currentBill: Double,
    currentUsage: Double,
    isProcessingPayment: Boolean,
    onPayClick: () -> Unit,
    onNavigateToDevices: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            WattsWatcherColors.gradientStart.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Current Bill (Live)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Updates in real-time",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                    
                    // Live indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WattsWatcherAnimations.PulsingIndicator {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(WattsWatcherColors.energyGreen, CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = WattsWatcherColors.energyGreen
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Animated Bill Amount
                WattsWatcherAnimations.AnimatedCounter(
                    targetValue = currentBill.toFloat()
                ) { animatedValue ->
                    Text(
                        text = "₹${String.format("%.2f", animatedValue)}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${String.format("%.2f", currentUsage)} kWh consumed this month",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        modifier = Modifier.weight(1f)
                    )
                    
                    TextButton(
                        onClick = onNavigateToDevices,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text("View Devices")
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Button(
                    onClick = onPayClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = currentBill > 0 && !isProcessingPayment,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WattsWatcherColors.energyGreen
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isProcessingPayment) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Processing...")
                    } else {
                        Icon(Icons.Default.Payment, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pay ₹${String.format("%.2f", currentBill)}")
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickPaymentMethods(
    billAmount: Double,
    onPaymentSelect: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = WattsWatcherColors.cardBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Quick Payment",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PaymentMethodButton(
                    icon = "📱",
                    title = "UPI",
                    subtitle = "Google Pay, PhonePe",
                    onClick = { onPaymentSelect("UPI") },
                    modifier = Modifier.weight(1f)
                )
                
                PaymentMethodButton(
                    icon = "💳",
                    title = "Card",
                    subtitle = "Debit/Credit",
                    onClick = { onPaymentSelect("Card") },
                    modifier = Modifier.weight(1f)
                )
                
                PaymentMethodButton(
                    icon = "🏦",
                    title = "Net Banking",
                    subtitle = "All banks",
                    onClick = { onPaymentSelect("Net Banking") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodButton(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun PaymentOptionsDialog(
    billAmount: Double,
    onDismiss: () -> Unit,
    onPaymentConfirm: (String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("UPI") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Choose Payment Method",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Amount to pay: ₹${String.format("%.2f", billAmount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = WattsWatcherColors.energyGreen
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                PaymentOption(
                    icon = "📱",
                    title = "UPI Payment",
                    subtitle = "Google Pay, PhonePe, Paytm",
                    isSelected = selectedMethod == "UPI",
                    onClick = { selectedMethod = "UPI" }
                )
                
                PaymentOption(
                    icon = "💳",
                    title = "Debit/Credit Card",
                    subtitle = "Visa, Mastercard, RuPay",
                    isSelected = selectedMethod == "Card",
                    onClick = { selectedMethod = "Card" }
                )
                
                PaymentOption(
                    icon = "🏦",
                    title = "Net Banking",
                    subtitle = "All major banks supported",
                    isSelected = selectedMethod == "Net Banking",
                    onClick = { selectedMethod = "Net Banking" }
                )
                
                PaymentOption(
                    icon = "💰",
                    title = "Digital Wallet",
                    subtitle = "Paytm, Amazon Pay, etc.",
                    isSelected = selectedMethod == "Wallet",
                    onClick = { selectedMethod = "Wallet" }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onPaymentConfirm(selectedMethod) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = WattsWatcherColors.energyGreen
                )
            ) {
                Text("Pay Now")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun PaymentOption(
    icon: String,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = WattsWatcherColors.energyGreen
                )
            }
        }
    }
}

@Composable
private fun HistoricalBillCard(
    billingData: com.wattswatcher.app.data.api.BillingResponse
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = WattsWatcherColors.cardBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Previous Bill Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Last Month",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "₹${String.format("%.2f", billingData.currentBill.amount)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${String.format("%.1f", billingData.currentBill.unitsConsumed)} kWh",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = billingData.currentBill.status,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = when (billingData.currentBill.status) {
                            "Paid" -> WattsWatcherColors.billPaid
                            "Pending" -> WattsWatcherColors.billPending
                            else -> WattsWatcherColors.billOverdue
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TariffStructureCard(
    tariffs: List<com.wattswatcher.app.data.api.TariffSlab>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = WattsWatcherColors.cardBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Tariff Structure",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            tariffs.forEach { tariff ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = tariff.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "₹${tariff.rate}/kWh",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = WattsWatcherColors.energyBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentHistoryCard(
    paymentRecord: com.wattswatcher.app.data.api.PaymentRecord
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = WattsWatcherColors.cardBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            when (paymentRecord.status) {
                                "Paid" -> WattsWatcherColors.billPaid
                                "Pending" -> WattsWatcherColors.billPending
                                else -> WattsWatcherColors.billOverdue
                            },
                            CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = paymentRecord.period,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = paymentRecord.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${String.format("%.2f", paymentRecord.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = paymentRecord.status,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (paymentRecord.status) {
                        "Paid" -> WattsWatcherColors.billPaid
                        "Pending" -> WattsWatcherColors.billPending
                        else -> WattsWatcherColors.billOverdue
                    }
                )
            }
        }
    }
}

@Composable
private fun PaymentResultCard(
    result: com.wattswatcher.app.data.api.PaymentResponse,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (result.success) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (result.success) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = if (result.success) WattsWatcherColors.energyGreen else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (result.success) "Payment Successful!" else "Payment Failed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = result.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            if (result.success && result.transactionId != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Transaction ID: ${result.transactionId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (result.success) WattsWatcherColors.energyGreen else MaterialTheme.colorScheme.error
                )
            ) {
                Text("OK")
            }
        }
    }
}

@Composable
private fun ErrorCard(
    error: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    }
}