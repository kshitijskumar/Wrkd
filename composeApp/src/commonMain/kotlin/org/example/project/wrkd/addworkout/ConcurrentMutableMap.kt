package org.example.project.wrkd.addworkout

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ConcurrentMutableMap<K, V> {

    private val internalMap = mutableMapOf<K, V>()
    private val mutex = Mutex()

    suspend fun put(key: K, value: V) {
        mutex.withLock {
            internalMap[key] = value
        }
    }

    suspend fun get(key: K): V? {
        return mutex.withLock {
            internalMap[key]
        }
    }

    suspend fun clearAll() {
        mutex.withLock {
            internalMap.clear()
        }
    }

    suspend fun remove(key: K) {
        mutex.withLock {
            internalMap.remove(key)
        }
    }

}