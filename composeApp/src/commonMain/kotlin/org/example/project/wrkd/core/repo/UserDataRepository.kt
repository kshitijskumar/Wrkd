package org.example.project.wrkd.core.repo

import kotlinx.coroutines.flow.Flow
import org.example.project.wrkd.core.models.UserData

interface UserDataRepository {

    suspend fun getCurrentUser(): Flow<UserData?>

    suspend fun updateCurrentUser(data: UserData)

}