package org.example.project.wrkd.core.repo

import kotlinx.coroutines.flow.Flow
import org.example.project.wrkd.core.local.UserDataLocalDataSource
import org.example.project.wrkd.core.models.UserData
import org.example.project.wrkd.di.core.inject

class UserDataRepositoryImpl(
    private val local: UserDataLocalDataSource = inject()
) : UserDataRepository {

    override suspend fun getCurrentUser(): Flow<UserData?> {
        return local.getCurrentUser()
    }

    override suspend fun updateCurrentUser(data: UserData) {
        return local.updateCurrentUser(data)
    }
}