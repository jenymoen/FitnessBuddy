import * as functions from "firebase-functions";
import { generateContent } from "../ai/gemini";

interface UserProfile {
    age: number;
    gender: string;
    weight: number;
    height: number;
    goals: string[];
    activities: string[];
    durationWeeks: number;
}

export const generateTrainingPlan = functions.https.onCall(async (data: UserProfile, context) => {
    if (!context.auth) {
        throw new functions.https.HttpsError("unauthenticated", "The function must be called while authenticated.");
    }

    const { age, gender, weight, height, goals, activities, durationWeeks } = data;

    const prompt = `
You are an expert fitness coach AI. Generate a personalized training plan in JSON format.

User Profile:
- Age: ${age}
- Gender: ${gender}
- Weight: ${weight} kg
- Height: ${height} cm
- Fitness Goals: ${goals.join(", ")}
- Preferred Activities: ${activities.join(", ")}
- Plan Duration: ${durationWeeks} weeks

Return ONLY a JSON object with this valid structure (no markdown code blocks):
{
  "planName": "string",
  "weeklySchedule": [
    {
      "week": 1,
      "days": [
        {
          "day": "Monday",
          "workoutType": "string",
          "duration": 45,
          "intensity": "moderate",
          "exercises": [
            {
              "name": "string",
              "sets": 3,
              "reps": 12,
              "restSeconds": 60,
              "notes": "string"
            }
          ]
        }
      ]
    }
  ],
  "progressionNotes": "string"
}

Consider progressive overload, adequate rest days, and variety.
Ensure the plan matches the user's fitness level and goals.
`;

    try {
        const jsonString = await generateContent(prompt);
        // Clean up markdown code blocks if present
        const cleanedJson = jsonString.replace(/```json/g, "").replace(/```/g, "").trim();
        const plan = JSON.parse(cleanedJson);
        return { plan };
    } catch (error) {
        console.error("Plan generation error:", error);
        throw new functions.https.HttpsError("internal", "Failed to generate training plan.");
    }
});
