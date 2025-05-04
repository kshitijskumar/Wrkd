package org.example.project.wrkd.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import org.example.project.wrkd.core.AppDBAndroid
import org.example.project.wrkd.core.db.AppDB
import org.example.project.wrkd.core.local.UserDataLocalDataSourceImpl
import org.example.project.wrkd.di.core.Module
import org.example.project.wrkd.di.core.androidContext
import org.example.project.wrkd.di.core.createModule
import org.example.project.wrkd.di.core.inject
import org.example.project.wrkd.di.core.single

actual fun platformModule(): Module {
    return createModule {
        single(name = UserDataLocalDataSourceImpl.USER_DATA_DATA_STORE) {
            PreferenceDataStoreFactory.createWithPath {
                androidContext().filesDir.resolve(UserDataLocalDataSourceImpl.USER_DATA_DATA_STORE).absolutePath.toPath()
            }
        }

        single<AppDB> {
            AppDBAndroid(
                context = androidContext(),
                coroutinesContextProvider = inject()
            )
        }
    }
}