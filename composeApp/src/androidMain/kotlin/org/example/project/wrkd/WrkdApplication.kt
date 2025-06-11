package org.example.project.wrkd

import android.app.Application
import android.content.Context
import org.example.project.wrkd.core.models.WeekDay
import org.example.project.wrkd.di.appModule
import org.example.project.wrkd.di.core.Injector
import org.example.project.wrkd.di.core.createModule
import org.example.project.wrkd.di.core.inject
import org.example.project.wrkd.di.core.single
import org.example.project.wrkd.di.platformModule
import org.example.project.wrkd.navigation.args.DayPlanArgs

class WrkdApplication : Application() {

    private val applicationModule by lazy {
        createModule {
            single<Context> { this@WrkdApplication.applicationContext }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Injector.init(
            modules = listOf(
                applicationModule,
                appModule(),
                platformModule()
            )
        )
    }

}