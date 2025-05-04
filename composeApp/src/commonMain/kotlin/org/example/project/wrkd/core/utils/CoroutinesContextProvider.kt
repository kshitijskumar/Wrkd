package org.example.project.wrkd.core.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.coroutines.CoroutineContext

interface CoroutinesContextProvider {

    val main: CoroutineContext
    val io: CoroutineContext
    val default: CoroutineContext

}

class CoroutinesContextProviderImpl : CoroutinesContextProvider {

    override val main: CoroutineContext get() = Dispatchers.Main
    override val io: CoroutineContext get() = Dispatchers.IO
    override val default: CoroutineContext get() = Dispatchers.Default
}