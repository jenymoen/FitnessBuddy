package com.fitnessbuddy.data.repository

import android.util.Log
import com.fitnessbuddy.domain.model.TrainingPlan
import com.fitnessbuddy.domain.repository.GeminiRepository
import com.fitnessbuddy.ui.onboarding.OnboardingData
import com.fitnessbuddy.ui.onboarding.TrainingPlanPromptBuilder
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of GeminiRepository using Google's Generative AI SDK.
 */
@Singleton
class GeminiRepositoryImpl @Inject constructor(
    private val generativeModel: GenerativeModel
) : GeminiRepository {

    private val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
        coerceInputValues = true
    }

    override suspend fun generateTrainingPlan(onboardingData: OnboardingData): Result<String> {
        return try {
            val prompt = TrainingPlanPromptBuilder.buildPrompt(onboardingData)
            val response = generativeModel.generateContent(prompt)
            val generatedText = response.text
            
            if (generatedText != null) {
                Result.success(generatedText)
            } else {
                Result.failure(Exception("Empty response from AI"))
            }
        } catch (e: Exception) {
            Log.e("GeminiRepository", "Error generating plan", e)
            Result.failure(e)
        }
    }
    
    override suspend fun generateAndParseTrainingPlan(onboardingData: OnboardingData): Result<TrainingPlan> {
        return try {
            val prompt = TrainingPlanPromptBuilder.buildPrompt(onboardingData)
            Log.d("GeminiRepository", "Sending prompt to Gemini...")
            
            val response = generativeModel.generateContent(prompt)
            val generatedText = response.text
            
            if (generatedText.isNullOrBlank()) {
                Log.e("GeminiRepository", "Empty response from AI")
                return Result.failure(Exception("Empty response from AI"))
            }
            
            Log.d("GeminiRepository", "Received response length: ${generatedText.length}")
            Log.d("GeminiRepository", "Response preview: ${generatedText.take(500)}")
            
            // Extract JSON from response - handle markdown code blocks
            val cleanedJson = extractJson(generatedText)
            
            if (cleanedJson.isBlank()) {
                Log.e("GeminiRepository", "Could not extract JSON from response")
                return Result.failure(Exception("Could not parse AI response"))
            }
            
            Log.d("GeminiRepository", "Cleaned JSON: ${cleanedJson.take(300)}")
            
            // Parse JSON to TrainingPlan
            val plan = try {
                json.decodeFromString<TrainingPlan>(cleanedJson)
            } catch (parseError: Exception) {
                Log.e("GeminiRepository", "JSON parse error: ${parseError.message}")
                Log.e("GeminiRepository", "Failed JSON: $cleanedJson")
                throw Exception("Failed to parse training plan: ${parseError.message}")
            }
            
            Log.d("GeminiRepository", "Parsed plan with ${plan.weeks.size} weeks")
            
            Result.success(plan.copy(id = java.util.UUID.randomUUID().toString()))
        } catch (e: Exception) {
            Log.e("GeminiRepository", "Error generating/parsing plan: ${e.message}", e)
            Result.failure(Exception(e.message ?: "Something unexpected happened"))
        }
    }
    
    /**
     * Extracts JSON object from response text, handling markdown code blocks.
     */
    private fun extractJson(text: String): String {
        // Try to find JSON in markdown code block first
        val codeBlockPattern = Regex("```(?:json)?\\s*([\\s\\S]*?)```")
        val codeBlockMatch = codeBlockPattern.find(text)
        if (codeBlockMatch != null) {
            return codeBlockMatch.groupValues[1].trim()
        }
        
        // Try to find JSON object directly
        val jsonObjectPattern = Regex("\\{[\\s\\S]*\\}")
        val jsonMatch = jsonObjectPattern.find(text)
        if (jsonMatch != null) {
            return jsonMatch.value.trim()
        }
        
        // Return cleaned text as fallback
        return text
            .replace("```json", "")
            .replace("```", "")
            .trim()
    }
}
