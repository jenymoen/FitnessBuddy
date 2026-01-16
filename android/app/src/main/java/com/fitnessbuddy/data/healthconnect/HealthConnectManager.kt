package com.fitnessbuddy.data.healthconnect

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthConnectManager @Inject constructor(
    private val healthConnectClient: HealthConnectClient?
) {
    val PERMISSIONS = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class)
    )

    suspend fun hasAllPermissions(): Boolean {
        if (healthConnectClient == null) return false
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        return granted.containsAll(PERMISSIONS)
    }

    suspend fun readSteps(startTime: Instant, endTime: Instant): Long {
        if (healthConnectClient == null) return 0
        
        return try {
            val request = ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            val response = healthConnectClient.readRecords(request)
            response.records.sumOf { it.count }
        } catch (e: Exception) {
            // Return 0 if we can't read steps (permissions not granted, etc.)
            0
        }
    }
    
    /**
     * Reads the latest heart rate sample from Health Connect.
     * Looks for heart rate data in the last 5 minutes.
     * 
     * @return The latest heart rate in BPM, or null if no data available
     */
    suspend fun readLatestHeartRate(): Long? {
        if (healthConnectClient == null) return null
        
        return try {
            val endTime = Instant.now()
            val startTime = endTime.minus(5, ChronoUnit.MINUTES)
            
            val request = ReadRecordsRequest(
                recordType = HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            val response = healthConnectClient.readRecords(request)
            
            // Get the most recent heart rate sample
            response.records
                .flatMap { record -> record.samples }
                .maxByOrNull { it.time }
                ?.beatsPerMinute
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Provides a flow of heart rate updates.
     * Polls Health Connect every 5 seconds for new heart rate data.
     * 
     * @return Flow emitting heart rate values in BPM
     */
    fun getHeartRateUpdates(): Flow<Long?> = flow {
        while (true) {
            val heartRate = readLatestHeartRate()
            emit(heartRate)
            delay(5000L) // Poll every 5 seconds
        }
    }
    
    /**
     * Reads today's exercise sessions from Health Connect.
     * Returns sessions from other apps like Strava, Garmin, Samsung Health, etc.
     */
    suspend fun readTodaysExerciseSessions(): List<ExerciseSessionInfo> {
        if (healthConnectClient == null) return emptyList()
        
        return try {
            val endTime = Instant.now()
            val startTime = endTime.truncatedTo(ChronoUnit.DAYS) // Start of today
            
            val request = ReadRecordsRequest(
                recordType = ExerciseSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            val response = healthConnectClient.readRecords(request)
            
            response.records.map { record ->
                ExerciseSessionInfo(
                    id = record.metadata.id,
                    title = record.title ?: getExerciseTypeName(record.exerciseType),
                    exerciseType = record.exerciseType,
                    startTime = record.startTime,
                    endTime = record.endTime,
                    durationMinutes = java.time.Duration.between(record.startTime, record.endTime).toMinutes(),
                    notes = record.notes,
                    sourceApp = record.metadata.dataOrigin.packageName
                )
            }.sortedByDescending { it.startTime }
        } catch (e: Exception) {
            android.util.Log.e("HealthConnectManager", "Error reading exercise sessions", e)
            emptyList()
        }
    }
    
    private fun getExerciseTypeName(exerciseType: Int): String {
        return when (exerciseType) {
            ExerciseSessionRecord.EXERCISE_TYPE_RUNNING -> "Running"
            ExerciseSessionRecord.EXERCISE_TYPE_WALKING -> "Walking"
            ExerciseSessionRecord.EXERCISE_TYPE_BIKING -> "Cycling"
            ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL -> "Swimming (Pool)"
            ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_OPEN_WATER -> "Swimming (Open Water)"
            ExerciseSessionRecord.EXERCISE_TYPE_HIKING -> "Hiking"
            ExerciseSessionRecord.EXERCISE_TYPE_YOGA -> "Yoga"
            ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING -> "Strength Training"
            ExerciseSessionRecord.EXERCISE_TYPE_ELLIPTICAL -> "Elliptical"
            ExerciseSessionRecord.EXERCISE_TYPE_ROWING_MACHINE -> "Rowing"
            ExerciseSessionRecord.EXERCISE_TYPE_STAIR_CLIMBING -> "Stair Climbing"
            else -> "Exercise"
        }
    }
}

/**
 * Data class representing an exercise session from Health Connect.
 */
data class ExerciseSessionInfo(
    val id: String,
    val title: String,
    val exerciseType: Int,
    val startTime: Instant,
    val endTime: Instant,
    val durationMinutes: Long,
    val notes: String?,
    val sourceApp: String
)

