package org.example.project.wrkd.addworkout

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.example.project.wrkd.core.models.app.DayPlanAppModel
import org.example.project.wrkd.core.models.WeekDay

class WorkoutRoutinePlanner {

    private val map = ConcurrentMutableMap<WeekDay, DayPlanAppModel>()

    suspend fun updatePlanForDay(
        day: WeekDay,
        planAppModel: DayPlanAppModel
    ) {
        map.put(day, planAppModel)
    }

    suspend fun getPlanForDay(day: WeekDay): DayPlanAppModel? {
        return map.get(day)
    }

    suspend fun copyPlan(copyFrom: WeekDay, copyFor: WeekDay): Boolean {
        val planToCopy = map.get(copyFrom) ?: return false
        map.put(copyFor, planToCopy)
        return true
    }
}

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