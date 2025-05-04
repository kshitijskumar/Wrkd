package org.example.project.wrkd.core.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.wrkd.core.models.UserData
import org.example.project.wrkd.core.utils.CoreJson
import org.example.project.wrkd.core.utils.decodeSafely
import org.example.project.wrkd.di.core.inject

class UserDataLocalDataSourceImpl(
    private val dataStore: DataStore<Preferences> = inject(name = USER_DATA_DATA_STORE)
) : UserDataLocalDataSource {

    private val userDataPrefsKey by lazy {
        stringPreferencesKey("userData")
    }

    override suspend fun getCurrentUser(): Flow<UserData?> {
        return dataStore.data.map {
            it[userDataPrefsKey]?.let { userDataJson ->
                CoreJson.decodeSafely(userDataJson)
            }
        }
    }

    override suspend fun updateCurrentUser(data: UserData) {
        dataStore.edit {
            it[userDataPrefsKey] = CoreJson.encodeToString(data)
        }
    }

    companion object {
        const val USER_DATA_DATA_STORE = "userdata.preferences_pb"
    }
}