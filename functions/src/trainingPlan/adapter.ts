import * as functions from "firebase-functions";
import { generateContent } from "../ai/gemini";

export const adaptWorkoutPlan = functions.https.onCall(async (data: any, context) => {
    if (!context.auth) {
        throw new functions.https.HttpsError("unauthenticated", "Authentication required.");
    }

    const { currentPlan, feedback, missedDispatcher } = data;
    // feedback e.g. "I missed Monday's workout" or "Too intense"

    const prompt = `
You are an expert fitness coach AI. Modify the existing training plan based on user feedback.

Current Plan (JSON):
${JSON.stringify(currentPlan)}

User Feedback: "${feedback}"

Return the MODIFIED plan in JSON format. Maintain the same structure.
Adjust future workouts to accommodate the feedback (e.g., shift schedule, reduce intensity).
    `;

    try {
        const jsonString = await generateContent(prompt);
        const cleanedJson = jsonString.replace(/```json/g, "").replace(/```/g, "").trim();
        return { plan: JSON.parse(cleanedJson) };
    } catch (error) {
        console.error("Adaptation error:", error);
        throw new functions.https.HttpsError("internal", "Failed to adapt plan.");
    }
});
