package com.wattswatcher.app.ui.billing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wattswatcher.app.data.repository.WattsWatcherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val repository: WattsWatcherRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(BillingState())
    val state: StateFlow<BillingState> = _state.asStateFlow()
    
    init {
        fetchBillingData()
    }
    
    private fun fetchBillingData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            // Simulate fetching billing data
            repository.getDashboardData()
                .catch { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { dashboardResponse ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                billSummary = dashboardResponse.billSummary,
                                tariffStructure = generateTariffStructure(),
                                paymentHistory = generatePaymentHistory(),
                                error = null
                            )
                        },
                        onFailure = { error ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = error.message
                            )
                        }
                    )
                }
        }
    }
    
    fun initiatePayment(amount: Double, method: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isProcessingPayment = true)
            
            val result = repository.initiatePayment(amount, method)
            result.fold(
                onSuccess = { paymentResponse ->
                    _state.value = _state.value.copy(
                        isProcessingPayment = false,
                        paymentUrl = paymentResponse.paymentUrl,
                        transactionId = paymentResponse.transactionId
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isProcessingPayment = false,
                        error = error.message
                    )
                }
            )
        }
    }
    
    fun showPaymentModal(show: Boolean) {
        _state.value = _state.value.copy(showPaymentModal = show)
    }
    
    fun updatePaymentAmount(amount: String) {
        _state.value = _state.value.copy(paymentAmount = amount)
    }
    
    fun selectPaymentMethod(method: String) {
        _state.value = _state.value.copy(selectedPaymentMethod = method)
    }
    
    private fun generateTariffStructure() = listOf(
        com.wattswatcher.app.data.model.Tariff("0-100 units", 3.50),
        com.wattswatcher.app.data.model.Tariff("101-200 units", 4.50),
        com.wattswatcher.app.data.model.Tariff("201-300 units", 6.00),
        com.wattswatcher.app.data.model.Tariff("Above 300 units", 7.50)
    )
    
    private fun generatePaymentHistory() = listOf(
        com.wattswatcher.app.data.model.Payment("2024-01-15", 1250.75, "Paid"),
        com.wattswatcher.app.data.model.Payment("2023-12-15", 1180.50, "Paid"),
        com.wattswatcher.app.data.model.Payment("2023-11-15", 1320.25, "Paid"),
        com.wattswatcher.app.data.model.Payment("2023-10-15", 1095.00, "Paid")
    )
}