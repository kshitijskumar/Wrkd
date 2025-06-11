package org.example.project.wrkd.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Suppress("LeakingThis")
abstract class BaseViewModel<DATA, INTENT> : ViewModel() {

    abstract fun initialData(): DATA

    private val _state = MutableStateFlow<DATA>(initialData())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.subscriptionCount
                .map { it >= 1 }
                .distinctUntilChanged()
                .collect { subscribersPresent ->
                    if (subscribersPresent) {
                        viewStateActive()
                    } else {
                        viewStateInactive()
                    }
                }
        }
    }

    abstract fun processIntent(intent: INTENT)

    protected fun updateState(block: (DATA) -> DATA) {
        _state.update(block)
    }

    protected open fun viewStateActive() {}

    protected open fun viewStateInactive() {}

    protected val currentState: DATA get() = state.value

}
