package org.example.project.wrkd

import android.app.Application
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.project.wrkd.addworkout.ui.DayPlanViewModel
import org.example.project.wrkd.core.models.WeekDay
import org.example.project.wrkd.core.repo.UserDataRepository
import org.example.project.wrkd.di.Test1
import org.example.project.wrkd.di.Test2
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

        val vm = inject<DayPlanViewModel>(args = listOf(DayPlanArgs(WeekDay.Mon)))
        println("InjectStuff: vm: $vm")
//        val t1 = inject<Test1>()
//        val t2 = inject<Test1>() // should have diff hash
//        val t3 = inject<Test1>(name = "myname")
//        val t4 = inject<Test2>()
//        val t5 = inject<Test2>() // same hash
//        println("InjectStuff: first: $t1 -- $t2 :: ${t1.hashCode()} -- ${t2.hashCode()}")
//        println("InjectStuff: second: $t3")
//        println("InjectStuff: third: $t4 -- $t5 :: ${t4.hashCode()} -- ${t5.hashCode()}")
    }

}