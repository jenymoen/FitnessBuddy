import { GoogleGenerativeAI } from "@google/generative-ai";
import * as functions from "firebase-functions";

// Initialize Gemini
// Note: Ensure GEMINI_API_KEY is set in Firebase functions config
// commands: firebase functions:config:set gemini.key="YOUR_KEY"
const getGeminiModel = () => {
    const apiKey = functions.config().gemini?.key || process.env.GEMINI_API_KEY;
    if (!apiKey) {
        throw new Error("GEMINI_API_KEY is not set.");
    }
    const genAI = new GoogleGenerativeAI(apiKey);
    return genAI.getGenerativeModel({ model: "gemini-pro" });
};

export const generateContent = async (prompt: string): Promise<string> => {
    try {
        const model = getGeminiModel();
        const result = await model.generateContent(prompt);
        const response = await result.response;
        return response.text();
    } catch (error) {
        console.error("Error generating content:", error);
        throw new Error("Failed to generate content from AI.");
    }
};
