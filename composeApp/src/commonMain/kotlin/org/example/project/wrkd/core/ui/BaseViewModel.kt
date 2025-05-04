package org.example.project.wrkd.core.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Suppress("LeakingThis")
abstract class BaseViewModel<DATA, INTENT> : ViewModel() {

    abstract fun initialData(): DATA

    private val _state = MutableStateFlow<DATA>(initialData())
    val state = _state.asStateFlow()

    abstract fun processIntent(intent: INTENT)

    protected fun updateState(block: (DATA) -> DATA) {
        _state.update(block)
    }

    protected val currentState: DATA get() = state.value

}
