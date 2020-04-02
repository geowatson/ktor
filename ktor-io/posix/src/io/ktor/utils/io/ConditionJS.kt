package io.ktor.utils.io

import io.ktor.utils.io.concurrent.*
import kotlinx.cinterop.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

internal actual class Condition actual constructor(val predicate: () -> Boolean) {
    private var continuation: COpaquePointer? by shared(null)

    actual fun check(): Boolean {
        return predicate()
    }

    actual fun signal() {
        val reference = continuation
        if (reference != null && predicate()) {
            continuation = null

            reference.asStableRef<Continuation<Unit>>().apply {
                get().resume(Unit)
                dispose()
            }
        }
    }

    actual suspend fun await(block: () -> Unit) {
        if (predicate()) return

        return suspendCancellableCoroutine { current ->
            val ref = StableRef.create(current).asCPointer()
            continuation = ref
            block()
        }
    }

    actual suspend fun await() {
        if (predicate()) return

        return suspendCancellableCoroutine { current ->
            val ref = StableRef.create(current).asCPointer()
            continuation = ref
        }
    }
}

