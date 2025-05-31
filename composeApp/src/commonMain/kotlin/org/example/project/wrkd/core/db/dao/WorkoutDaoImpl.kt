package org.example.project.wrkd.core.db.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.example.project.wrkd.core.db.dao.mappers.toExerciseTable
import org.example.project.wrkd.core.db.dao.mappers.toSetTable
import org.example.project.wrkd.core.db.dao.mappers.toWorkoutDayTable
import org.example.project.wrkd.core.models.WeekDay
import org.example.project.wrkd.core.models.app.ExerciseResistanceMethod
import org.example.project.wrkd.core.models.entity.DayPlanEntity
import org.example.project.wrkd.core.models.entity.ExercisePlanInfoEntity
import org.example.project.wrkd.core.models.entity.ExerciseSetInfoEntity
import org.example.project.wrkd.core.utils.CoroutinesContextProvider
import org.example.project.wrkd.track.data.dao.WorkoutDao
import srccommonMainsqldelightAppDatabase.Exercise_tableQueries
import srccommonMainsqldelightAppDatabase.GetWorkoutDetails
import srccommonMainsqldelightAppDatabase.GetWorkoutsBetweenTimestamps
import srccommonMainsqldelightAppDatabase.Set_tableQueries
import srccommonMainsqldelightAppDatabase.Workout_day_tableQueries

class WorkoutDaoImpl(
    private val workoutDayTablequeries: Workout_day_tableQueries,
    private val exerciseTableQueries: Exercise_tableQueries,
    private val setTablequeries: Set_tableQueries,
    private val coroutinesContextProvider: CoroutinesContextProvider
) : WorkoutDao {

    override fun getWorkoutDetailsById(workoutId: String): Flow<DayPlanEntity?> {
        return workoutDayTablequeries.getWorkoutDetails(workoutId)
            .asFlow()
            .mapToList(coroutinesContextProvider.io)
            .map { list ->
                val combined = list.map { CombinedWorkoutData(it) }
                combined.toDayPlanEntity()?.find {  entity -> entity.id == workoutId }
            }
    }

    override suspend fun insertWorkoutDetails(workoutDetails: DayPlanEntity) {
        val work = when(workoutDetails) {
            is DayPlanEntity.RestDayEntity -> return // currently we do not support this
            is DayPlanEntity.WorkDayEntity -> workoutDetails
        }
        withContext(coroutinesContextProvider.io) {
            workoutDayTablequeries.transaction {
                workoutDayTablequeries.upsertWorkoutDetails(work.toWorkoutDayTable())
                work.exercises.forEach {
                    exerciseTableQueries.upsertExercise(it.toExerciseTable(work.id))
                    it.sets.forEach { set ->
                        setTablequeries.upsertSet(set.toSetTable(it.exerciseId))
                    }
                }
            }
        }
    }

    override fun getWorkoutBetweenTimestamps(
        lowerLimit: Long,
        upperLimit: Long
    ): Flow<List<DayPlanEntity>> {
        return workoutDayTablequeries.getWorkoutsBetweenTimestamps(
            startedAt = lowerLimit,
            startedAt_ = upperLimit
        )
            .asFlow()
            .mapToList(coroutinesContextProvider.io)
            .map { list ->
                val combined = list.map { CombinedWorkoutData(it) }
                combined.toDayPlanEntity() ?: listOf()
            }
    }

    private fun List<CombinedWorkoutData>.toDayPlanEntity(): List<DayPlanEntity>? {
        if (this.isEmpty()) {
            return null
        }

        val workoutDayGroup = this.groupBy { it.workoutId }

        return workoutDayGroup.map { (workoutId, workoutDay) ->
            val workoutInfo = workoutDay.first() // it will always have atleast 1 item otherwise it wouldnt have grouped

            DayPlanEntity.WorkDayEntity(
                id = workoutId,
                day = workoutInfo.day,
                dayName = workoutInfo.dayName,
                startedAt = workoutInfo.startedAt,
                workoutDuration = workoutInfo.workoutDuration,
                exercises = workoutDay.getAllExercisesForGivenDay(workoutId)
            )
        }
    }

    private fun List<CombinedWorkoutData>.getAllExercisesForGivenDay(workoutId: String): List<ExercisePlanInfoEntity> {
        val relevantEntries = this.filter { it.workoutId == workoutId }
        val exercisesGroup = relevantEntries.groupBy { (it.exerciseId to it.exerciseName) }

        return exercisesGroup.mapNotNull { (exerciseInfo, sets) ->
            val exName = exerciseInfo.second
            val exId = exerciseInfo.first
            if (exId == null || exName == null) {
                null
            } else {
                ExercisePlanInfoEntity(
                    name = exName,
                    exerciseId = exId,
                    sets = sets.getAllSetsForGivenExercise(exId)
                )
            }
        }
    }

    private fun List<CombinedWorkoutData>.getAllSetsForGivenExercise(exerciseId: String): List<ExerciseSetInfoEntity> {
        return this
            .filter { it.exerciseId == exerciseId }
            .mapNotNull {
                ExerciseSetInfoEntity(
                    setId = it.setId ?: return@mapNotNull null,
                    repsCount = it.repsCount?.toInt() ?: return@mapNotNull null,
                    resistanceMethod = it.resistanceMethod ?: return@mapNotNull null,
                    additionalWeight = it.additionalWeight ?: return@mapNotNull null
                )
            }
    }
}

private data class CombinedWorkoutData(
    val workoutId: String,
    val day: WeekDay,
    val dayName: String,
    val startedAt: Long,
    val workoutDuration: Long,
    val exerciseId: String?,
    val exerciseName: String?,
    val setId: String?,
    val repsCount: Long?,
    val resistanceMethod: ExerciseResistanceMethod?,
    val additionalWeight: Long?,
) {
    constructor(
        details: GetWorkoutDetails
    ) : this(
        workoutId = details.workoutId,
        day = details.day,
        dayName = details.dayName,
        startedAt = details.startedAt,
        workoutDuration = details.workoutDuration,
        exerciseId = details.exerciseId,
        exerciseName = details.exerciseName,
        setId = details.setId,
        repsCount = details.repsCount,
        resistanceMethod = details.resistanceMethod,
        additionalWeight = details.additionalWeight
    )

    constructor(
        details: GetWorkoutsBetweenTimestamps
    ) : this(
        workoutId = details.workoutId,
        day = details.day,
        dayName = details.dayName,
        startedAt = details.startedAt,
        workoutDuration = details.workoutDuration,
        exerciseId = details.exerciseId,
        exerciseName = details.exerciseName,
        setId = details.setId,
        repsCount = details.repsCount,
        resistanceMethod = details.resistanceMethod,
        additionalWeight = details.additionalWeight
    )

}