package com.example.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    protected fun launchSafe(onError: (Throwable) -> Unit = {}, block: suspend () -> Unit) {
        val handler = CoroutineExceptionHandler { _, throwable -> onError(throwable) }
        viewModelScope.launch(handler) { block() }
    }
}
