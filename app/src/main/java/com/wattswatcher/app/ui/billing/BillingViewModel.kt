package com.wattswatcher.app.ui.billing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wattswatcher.app.data.api.*
import com.wattswatcher.app.data.model.Device
import com.wattswatcher.app.data.repository.WattsWatcherRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BillingState(
    val isLoading: Boolean = false,
    val billingData: BillingResponse? = null,
    val isProcessingPayment: Boolean = false,
    val paymentResult: PaymentResponse? = null,
    val currentBillAmount: Double = 0.0,
    val currentUsage: Double = 0.0,
    val isGeneratingBill: Boolean = false,
    val error: String? = null
)

class BillingViewModel(
    private val repository: WattsWatcherRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(BillingState())
    val state: StateFlow<BillingState> = _state.asStateFlow()
    
    init {
        loadBillingData()
        startRealTimeBillUpdates()
    }
    
    private fun loadBillingData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                repository.getBillingData().collect { result ->
                    result.fold(
                        onSuccess = { billingData ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                billingData = billingData,
                                error = null
                            )
                        },
                        onFailure = { exception ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = exception.message ?: "Failed to load billing data"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load billing information"
                )
            }
        }
    }
    
    /**
     * Start real-time bill updates based on device usage
     */
    private fun startRealTimeBillUpdates() {
        viewModelScope.launch {
            try {
                val simulationEngine = repository.getSimulationEngine()
                
                // Combine live data and devices for real-time bill calculation
                combine(
                    simulationEngine.liveData,
                    simulationEngine.devices
                ) { liveData, devices ->
                    // Calculate current bill based on real usage
                    val currentBill = simulationEngine.getCurrentBillEstimate()
                    val currentUsage = simulationEngine.getMonthlyUsage()
                    
                    _state.value = _state.value.copy(
                        currentBillAmount = currentBill,
                        currentUsage = currentUsage
                    )
                }.collect { }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to start real-time bill updates"
                )
            }
        }
    }
    
    fun initiatePayment(amount: Double, method: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isProcessingPayment = true,
                paymentResult = null,
                error = null
            )
            
            try {
                val result = repository.initiatePayment(amount, method)
                result.fold(
                    onSuccess = { paymentResponse ->
                        _state.value = _state.value.copy(
                            isProcessingPayment = false,
                            paymentResult = paymentResponse
                        )
                        
                        // If payment successful, refresh billing data
                        if (paymentResponse.success) {
                            loadBillingData()
                        }
                    },
                    onFailure = { exception ->
                        _state.value = _state.value.copy(
                            isProcessingPayment = false,
                            error = exception.message ?: "Payment failed"
                        )
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isProcessingPayment = false,
                    error = e.message ?: "Payment processing failed"
                )
            }
        }
    }
    
    /**
     * Generate and download bill as PDF/text
     */
    fun generateBillDownload(): String {
        _state.value = _state.value.copy(isGeneratingBill = true)
        
        try {
            val simulationEngine = repository.getSimulationEngine()
            val devices = simulationEngine.devices.value
            val currentBill = simulationEngine.getCurrentBillEstimate()
            val currentUsage = simulationEngine.getMonthlyUsage()
            val currentDate = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date())
            
            val billContent = generateBillContent(
                billAmount = currentBill,
                usage = currentUsage,
                devices = devices,
                generatedDate = currentDate
            )
            
            _state.value = _state.value.copy(isGeneratingBill = false)
            return billContent
            
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isGeneratingBill = false,
                error = "Failed to generate bill: ${e.message}"
            )
            return ""
        }
    }
    
    /**
     * Generate formatted bill content
     */
    private fun generateBillContent(
        billAmount: Double,
        usage: Double,
        devices: List<Device>,
        generatedDate: String
    ): String {
        return buildString {
            appendLine("═══════════════════════════════════════")
            appendLine("           WATTS WATCHER")
            appendLine("         ELECTRICITY BILL")
            appendLine("═══════════════════════════════════════")
            appendLine()
            appendLine("Generated: $generatedDate")
            appendLine("Bill Period: ${getCurrentMonth()}")
            appendLine()
            appendLine("───────────────────────────────────────")
            appendLine("CONSUMPTION SUMMARY")
            appendLine("───────────────────────────────────────")
            appendLine("Total Units Consumed: ${String.format("%.2f", usage)} kWh")
            appendLine("Rate per Unit: ₹4.50")
            appendLine("Total Amount: ₹${String.format("%.2f", billAmount)}")
            appendLine()
            appendLine("───────────────────────────────────────")
            appendLine("DEVICE BREAKDOWN")
            appendLine("───────────────────────────────────────")
            
            devices.forEach { device: Device ->
                val deviceCost = device.monthlyUsage * 4.5
                appendLine("${device.name}:")
                appendLine("  Usage: ${String.format("%.2f", device.monthlyUsage)} kWh")
                appendLine("  Cost: ₹${String.format("%.2f", deviceCost)}")
                appendLine("  Status: ${if (device.isOn) "ON" else "OFF"}")
                appendLine()
            }
            
            appendLine("───────────────────────────────────────")
            appendLine("TARIFF STRUCTURE")
            appendLine("───────────────────────────────────────")
            appendLine("0-100 units: ₹3.50 per unit")
            appendLine("101-200 units: ₹4.00 per unit")
            appendLine("201-300 units: ₹4.50 per unit")
            appendLine("Above 300 units: ₹5.00 per unit")
            appendLine()
            appendLine("───────────────────────────────────────")
            appendLine("PAYMENT INFORMATION")
            appendLine("───────────────────────────────────────")
            appendLine("Due Date: ${getNextDueDate()}")
            appendLine("Late Payment Charges: ₹50 after due date")
            appendLine()
            appendLine("Thank you for using WattsWatcher!")
            appendLine("═══════════════════════════════════════")
        }
    }
    
    private fun getCurrentMonth(): String {
        val calendar = java.util.Calendar.getInstance()
        val format = java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault())
        return format.format(calendar.time)
    }
    
    private fun getNextDueDate(): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 15)
        val format = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        return format.format(calendar.time)
    }
    
    fun refresh() {
        loadBillingData()
    }
    
    fun dismissPaymentResult() {
        _state.value = _state.value.copy(paymentResult = null)
    }
    
    fun dismissError() {
        _state.value = _state.value.copy(error = null)
    }
}