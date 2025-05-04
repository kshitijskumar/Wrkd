package org.example.project.wrkd.core

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import org.example.project.wrkd.core.db.AppDB
import org.example.project.wrkd.core.utils.CoroutinesContextProvider
import src.commonMain.sqldelight.AppDatabase.AppDatabase

class AppDBIos(
    coroutinesContextProvider: CoroutinesContextProvider
) : AppDB(coroutinesContextProvider) {

    override fun createSqlDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = AppDatabase.Schema,
            name = dbName,
            onConfiguration = { config: DatabaseConfiguration ->
                config.copy(
                    extendedConfig = DatabaseConfiguration.Extended(foreignKeyConstraints = true)
                )
            }
        )
    }
}