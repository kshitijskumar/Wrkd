package org.example.project.wrkd.di

import org.example.project.wrkd.core.AppDBIos
import org.example.project.wrkd.core.db.AppDB
import org.example.project.wrkd.di.core.Injector
import org.example.project.wrkd.di.core.Module
import org.example.project.wrkd.di.core.createModule
import org.example.project.wrkd.di.core.inject
import org.example.project.wrkd.di.core.single

actual fun platformModule(): Module {
    return createModule {
        single<AppDB> {
            AppDBIos(
                coroutinesContextProvider = inject()
            )
        }
    }
}

object AppModuleHelper {

    fun setupAppModule() {
        Injector.init(
            modules = listOf(
                appModule(),
                platformModule()
            )
        )
    }

}