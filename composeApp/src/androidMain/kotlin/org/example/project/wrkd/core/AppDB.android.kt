package org.example.project.wrkd.core

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.example.project.wrkd.core.db.AppDB
import org.example.project.wrkd.core.utils.CoroutinesContextProvider
import src.commonMain.sqldelight.AppDatabase.AppDatabase

class AppDBAndroid(
    private val context: Context,
    coroutinesContextProvider: CoroutinesContextProvider
) : AppDB(coroutinesContextProvider) {

    override fun createSqlDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = context,
            name = dbName,
            callback = object : AndroidSqliteDriver.Callback(AppDatabase.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )
    }
}