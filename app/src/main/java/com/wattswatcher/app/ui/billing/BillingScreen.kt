package com.wattswatcher.app.ui.billing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.wattswatcher.app.utils.BillDownloadHelper
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wattswatcher.app.WattsWatcherApplication
import com.wattswatcher.app.data.model.Payment
import com.wattswatcher.app.data.model.Tariff
import com.wattswatcher.app.ui.components.PaymentMethodCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen() {
    val app = LocalContext.current.applicationContext as WattsWatcherApplication
    val viewModel: BillingViewModel = viewModel {
        BillingViewModel(app.repository)
    }
    val state by viewModel.state.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Billing & Payments",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Download Bill Button
                FilledTonalButton(
                    onClick = {
                        val billContent = viewModel.generateBillDownload()
                        if (billContent.isNotEmpty()) {
                            // Copy to clipboard as backup
                            clipboardManager.setText(AnnotatedString(billContent))
                            // Also save to file and show download notification
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
                    Text("Download Bill")
                }
            }
        }
        
        // Real-time Current Bill Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Current Bill (Live)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Updates in real-time",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                        
                        // Live indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        Color.Green,
                                        shape = RoundedCornerShape(50)
                                    )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LIVE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Green
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Current bill amount (real-time)
                    Text(
                        text = "₹${String.format("%.2f", state.currentBillAmount)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Text(
                        text = "${String.format("%.2f", state.currentUsage)} kWh consumed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Pay Now Button
                    Button(
                        onClick = {
                            viewModel.initiatePayment(state.currentBillAmount, "UPI")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.currentBillAmount > 0 && !state.isProcessingPayment
                    ) {
                        if (state.isProcessingPayment) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processing...")
                        } else {
                            Icon(Icons.Default.Payment, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pay Now - ₹${String.format("%.2f", state.currentBillAmount)}")
                        }
                    }
                }
            }
        }
        
        // Historical Bill Summary (from static data)
        state.billingData?.let { billingData ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Previous Bill Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
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
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
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
                                        "Paid" -> Color.Green
                                        "Pending" -> Color(0xFFFF8C00)
                                        else -> Color.Red
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Tariff Structure
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Tariff Structure",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    state.billingData?.tariffStructure?.forEach { tariffSlab ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = tariffSlab.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "₹${tariffSlab.rate}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
        
        // Payment History
        item {
            Text(
                text = "Payment History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        items(state.billingData?.paymentHistory ?: emptyList()) { paymentRecord ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = paymentRecord.period,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = paymentRecord.date,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${String.format("%.2f", paymentRecord.amount)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = paymentRecord.status,
                            style = MaterialTheme.typography.bodySmall,
                            color = when (paymentRecord.status) {
                                "Paid" -> Color.Green
                                "Pending" -> Color(0xFFFF8C00)
                                else -> Color.Red
                            }
                        )
                    }
                }
            }
        }
        
        // Payment Result
        state.paymentResult?.let { result ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (result.success) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (result.success) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (result.success) Color.Green else Color.Red
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (result.success) "Payment Successful!" else "Payment Failed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = result.message,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        if (result.success && result.transactionId != null) {
                            Text(
                                text = "Transaction ID: ${result.transactionId}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        
                        Button(
                            onClick = { viewModel.dismissPaymentResult() },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }
        
        // Error handling
        state.error?.let { error ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
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
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = { viewModel.dismissError() }
                        ) {
                            Text("Dismiss")
                        }
                    }
                }
            }
        }
    }
}