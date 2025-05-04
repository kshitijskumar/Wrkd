package org.example.project.wrkd.core.local

import kotlinx.coroutines.flow.Flow
import org.example.project.wrkd.core.models.UserData

interface UserDataLocalDataSource {

    suspend fun getCurrentUser(): Flow<UserData?>

    suspend fun updateCurrentUser(data: UserData)
}