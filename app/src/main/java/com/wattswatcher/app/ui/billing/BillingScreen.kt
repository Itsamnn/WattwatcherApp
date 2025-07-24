package com.wattswatcher.app.ui.billing

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.wattswatcher.app.WattsWatcherApplication
// Using fully qualified names to avoid conflicts
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
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Billing & Payments",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Current Bill Summary
                item {
                    CurrentBillCard(
                        billSummary = state.billingData?.currentBill,
                        onPayNowClick = { 
                            state.billingData?.currentBill?.amount?.let { amount ->
                                viewModel.initiatePayment(amount, "UPI")
                            }
                        }
                    )
                }
                
                // Tariff Structure
                item {
                    TariffStructureCard(
                        tariffs = state.billingData?.tariffStructure?.map { tariffSlab ->
                            com.wattswatcher.app.data.model.Tariff(
                                slab = tariffSlab.description,
                                rate = tariffSlab.rate
                            )
                        } ?: emptyList()
                    )
                }
                
                // Payment History Header
                item {
                    Text(
                        text = "Payment History",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Payment History List
                items(state.billingData?.paymentHistory ?: emptyList()) { paymentRecord ->
                    PaymentHistoryItem(
                        payment = com.wattswatcher.app.data.model.Payment(
                            date = paymentRecord.date,
                            amount = paymentRecord.amount,
                            status = paymentRecord.status
                        )
                    )
                }
            }
        }
        
        // Payment Result
        state.paymentResult?.let { result ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                    Text(
                        text = if (result.success) "Payment Successful!" else "Payment Failed",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = result.message,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
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
        
        state.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Error: $error",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentBillCard(
    billSummary: com.wattswatcher.app.data.model.BillSummary?,
    onPayNowClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current Bill",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Due: ${billSummary?.dueDate ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Amount Due",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₹${billSummary?.amount?.toInt() ?: 0}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Units Consumed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${billSummary?.unitsConsumed?.toInt() ?: 0} kWh",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Button(
                onClick = onPayNowClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pay Now")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TariffStructureCard(tariffs: List<Tariff>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Tariff Structure",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            tariffs.forEach { tariff ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = tariff.slab,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "₹${tariff.rate}/unit",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (tariff != tariffs.last()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentHistoryItem(payment: Payment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    text = payment.date,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "₹${payment.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Surface(
                color = if (payment.status == "Paid") Color.Green.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = payment.status,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (payment.status == "Paid") Color.Green else Color.Red,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentModal(
    amount: String,
    selectedMethod: String,
    isProcessing: Boolean,
    onAmountChange: (String) -> Unit,
    onMethodSelect: (String) -> Unit,
    onPayClick: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val paymentMethods = listOf("UPI", "Credit Card", "Debit Card", "Net Banking")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Make Payment",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    label = { Text("Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                paymentMethods.forEach { method ->
                    PaymentMethodCard(
                        method = method,
                        isSelected = method == selectedMethod,
                        onSelect = { onMethodSelect(method) }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onPayClick(amount, selectedMethod) },
                enabled = !isProcessing && amount.isNotBlank()
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Pay Securely")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}