package org.example.project.wrkd.core.db

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import org.example.project.wrkd.core.db.dao.WorkoutDaoImpl
import org.example.project.wrkd.core.utils.CoroutinesContextProvider
import org.example.project.wrkd.track.data.dao.WorkoutDao
import src.commonMain.sqldelight.AppDatabase.AppDatabase
import srccommonMainsqldelightAppDatabase.Set_table
import srccommonMainsqldelightAppDatabase.Workout_day_table

abstract class AppDB(
    private val coroutinesContextProvider: CoroutinesContextProvider
) {

    protected val dbName: String = "AppDatabase"

    val workoutDao: WorkoutDao by lazy {
        WorkoutDaoImpl(
            workoutDayTablequeries = db.workout_day_tableQueries,
            exerciseTableQueries = db.exercise_tableQueries,
            setTablequeries = db.set_tableQueries,
            coroutinesContextProvider = coroutinesContextProvider
        )
    }

    abstract fun createSqlDriver(): SqlDriver

    private val db: AppDatabase by lazy {
        createDatabase()
    }

    private fun createDatabase(): AppDatabase {
        return AppDatabase(
            driver = createSqlDriver(),
            Set_tableAdapter = Set_table.Adapter(EnumColumnAdapter()),
            Workout_day_tableAdapter = Workout_day_table.Adapter(EnumColumnAdapter())
        )
    }

}