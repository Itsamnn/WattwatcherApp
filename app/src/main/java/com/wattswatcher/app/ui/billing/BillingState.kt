package com.wattswatcher.app.ui.billing

import com.wattswatcher.app.data.model.*

data class BillingState(
    val isLoading: Boolean = false,
    val billSummary: BillSummary? = null,
    val tariffStructure: List<Tariff> = emptyList(),
    val paymentHistory: List<Payment> = emptyList(),
    val showPaymentModal: Boolean = false,
    val paymentAmount: String = "",
    val selectedPaymentMethod: String = "UPI",
    val isProcessingPayment: Boolean = false,
    val paymentUrl: String? = null,
    val transactionId: String? = null,
    val error: String? = null
)