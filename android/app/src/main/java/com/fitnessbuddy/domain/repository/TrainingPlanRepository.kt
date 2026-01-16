package com.fitnessbuddy.domain.repository

import com.fitnessbuddy.domain.model.TrainingPlan

/**
 * Repository for persisting and retrieving training plans.
 */
interface TrainingPlanRepository {
    
    /**
     * Saves a training plan for the current user.
     */
    suspend fun saveTrainingPlan(plan: TrainingPlan): Result<Unit>
    
    /**
     * Retrieves the active training plan for the current user.
     * Returns null if no plan exists.
     */
    suspend fun getActiveTrainingPlan(): Result<TrainingPlan?>
    
    /**
     * Checks if the current user has a saved training plan.
     */
    suspend fun hasTrainingPlan(): Boolean
    
    /**
     * Deletes the current user's training plan.
     */
    suspend fun deleteTrainingPlan(): Result<Unit>
}
