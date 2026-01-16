package com.fitnessbuddy.data.repository

import android.util.Log
import com.fitnessbuddy.domain.model.TrainingDay
import com.fitnessbuddy.domain.model.TrainingPlan
import com.fitnessbuddy.domain.model.TrainingWeek
import com.fitnessbuddy.domain.repository.TrainingPlanRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingPlanRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : TrainingPlanRepository {

    private val collection get() = firestore.collection("users")
        .document(auth.currentUser?.uid ?: "anonymous")
        .collection("training_plans")

    override suspend fun saveTrainingPlan(plan: TrainingPlan): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            
            val planMap = mapOf(
                "id" to plan.id,
                "eventType" to plan.eventType,
                "targetDistance" to plan.targetDistance,
                "targetDate" to plan.targetDate,
                "totalWeeks" to plan.totalWeeks,
                "currentWeek" to plan.currentWeek,
                "daysToRace" to plan.daysToRace,
                "createdAt" to System.currentTimeMillis(),
                "weeks" to plan.weeks.map { week ->
                    mapOf(
                        "weekNumber" to week.weekNumber,
                        "phase" to week.phase,
                        "theme" to week.theme,
                        "totalDistanceKm" to week.totalDistanceKm,
                        "totalSessions" to week.totalSessions,
                        "completedSessions" to week.completedSessions,
                        "progressPercent" to week.progressPercent,
                        "isCurrentWeek" to week.isCurrentWeek,
                        "isLocked" to week.isLocked,
                        "days" to week.days.map { day ->
                            mapOf(
                                "dayOfWeek" to day.dayOfWeek,
                                "workoutType" to day.workoutType,
                                "title" to day.title,
                                "description" to day.description,
                                "distanceKm" to day.distanceKm,
                                "durationMinutes" to day.durationMinutes,
                                "intensity" to day.intensity,
                                "zone" to day.zone,
                                "pace" to day.pace,
                                "warmup" to day.warmup,
                                "mainSet" to day.mainSet,
                                "cooldown" to day.cooldown,
                                "isRestDay" to day.isRestDay,
                                "isCompleted" to day.isCompleted,
                                "isToday" to day.isToday
                            )
                        }
                    )
                }
            )
            
            collection.document("active").set(planMap).await()
            Log.d("TrainingPlanRepo", "Plan saved successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TrainingPlanRepo", "Error saving plan", e)
            Result.failure(e)
        }
    }

    override suspend fun getActiveTrainingPlan(): Result<TrainingPlan?> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            
            val doc = collection.document("active").get().await()
            
            if (!doc.exists()) {
                return Result.success(null)
            }
            
            val data = doc.data ?: return Result.success(null)
            
            @Suppress("UNCHECKED_CAST")
            val weeks = (data["weeks"] as? List<Map<String, Any>>)?.map { weekData ->
                TrainingWeek(
                    weekNumber = (weekData["weekNumber"] as? Long)?.toInt() ?: 1,
                    phase = weekData["phase"] as? String ?: "",
                    theme = weekData["theme"] as? String ?: "",
                    totalDistanceKm = (weekData["totalDistanceKm"] as? Number)?.toFloat() ?: 0f,
                    totalSessions = (weekData["totalSessions"] as? Long)?.toInt() ?: 0,
                    isCurrentWeek = weekData["isCurrentWeek"] as? Boolean ?: false,
                    isLocked = weekData["isLocked"] as? Boolean ?: false,
                    days = (weekData["days"] as? List<Map<String, Any>>)?.map { dayData ->
                        TrainingDay(
                            dayOfWeek = dayData["dayOfWeek"] as? String ?: "",
                            workoutType = dayData["workoutType"] as? String ?: "",
                            title = dayData["title"] as? String ?: "",
                            description = dayData["description"] as? String ?: "",
                            distanceKm = (dayData["distanceKm"] as? Number)?.toFloat() ?: 0f,
                            durationMinutes = (dayData["durationMinutes"] as? Long)?.toInt() ?: 0,
                            intensity = dayData["intensity"] as? String ?: "",
                            zone = dayData["zone"] as? String ?: "",
                            pace = dayData["pace"] as? String ?: "",
                            warmup = dayData["warmup"] as? String ?: "",
                            mainSet = dayData["mainSet"] as? String ?: "",
                            cooldown = dayData["cooldown"] as? String ?: "",
                            isRestDay = dayData["isRestDay"] as? Boolean ?: false,
                            isCompleted = dayData["isCompleted"] as? Boolean ?: false,
                            isToday = dayData["isToday"] as? Boolean ?: false
                        )
                    } ?: emptyList()
                )
            } ?: emptyList()
            
            val plan = TrainingPlan(
                id = data["id"] as? String ?: "",
                eventType = data["eventType"] as? String ?: "",
                targetDistance = data["targetDistance"] as? String ?: "",
                targetDate = data["targetDate"] as? String ?: "",
                totalWeeks = (data["totalWeeks"] as? Long)?.toInt() ?: 0,
                currentWeek = (data["currentWeek"] as? Long)?.toInt() ?: 1,
                weeks = weeks
            )
            
            Log.d("TrainingPlanRepo", "Plan loaded: ${plan.weeks.size} weeks")
            Result.success(plan)
        } catch (e: Exception) {
            Log.e("TrainingPlanRepo", "Error loading plan", e)
            Result.failure(e)
        }
    }

    override suspend fun hasTrainingPlan(): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val doc = collection.document("active").get().await()
            doc.exists()
        } catch (e: Exception) {
            Log.e("TrainingPlanRepo", "Error checking plan", e)
            false
        }
    }

    override suspend fun deleteTrainingPlan(): Result<Unit> {
        return try {
            collection.document("active").delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
