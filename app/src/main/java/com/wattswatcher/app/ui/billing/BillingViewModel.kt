package com.wattswatcher.app.ui.billing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wattswatcher.app.data.api.*
import com.wattswatcher.app.data.repository.WattsWatcherRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BillingState(
    val isLoading: Boolean = false,
    val billingData: BillingResponse? = null,
    val isProcessingPayment: Boolean = false,
    val paymentResult: PaymentResponse? = null,
    val error: String? = null
)

class BillingViewModel(
    private val repository: WattsWatcherRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(BillingState())
    val state: StateFlow<BillingState> = _state.asStateFlow()
    
    init {
        loadBillingData()
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