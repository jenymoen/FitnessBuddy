Act as a Senior Full-Stack Developer and AI Solutions Architect. I need you to design and provide the foundational code structure for a cross-platform fitness application called "AdaptiveFit" (or suggest a better name).

**Project Overview:**
A smart fitness tracker that uses AI to generate and adapt training plans based on user biometrics, activity preferences, and real-time health data.

**Target Platforms:**
1.  **Android App:** Native (Kotlin/Jetpack Compose).
2.  **Web Dashboard:** Responsive web app (React or Next.js).
3.  **Backend:** Firebase (for Auth/Database) or a similar scalable cloud solution.

**Core Features & Technical Requirements:**

1.  **User Onboarding & Profile:**
    * Create a clean UI to capture: Weight, Height, Age, Gender, Fitness Goals, and Activity Preferences (e.g., Running, Weightlifting, Yoga).
    * Store this data securely in a cloud database accessible by both Web and Android.

2.  **Health Connect Integration (Crucial):**
    * The Android app must utilize the Google Health Connect API.
    * It must read data aggregated from wearables (Oura Ring, Garmin, Apple Watch, Pulse Belts, etc.) via Health Connect.
    * Specific metrics to track: Heart rate, steps, sleep data, and active calories burned.

3.  **AI Training Plan Generator:**
    * Create a backend function that takes the User Profile + Fitness Goals and sends a prompt to an LLM (like Gemini API or OpenAI API).
    * The AI must return a structured JSON training plan (daily schedule, exercise types, duration, intensity).

4.  **Adaptive Schedule Logic (The "Smart" Feature):**
    * Implement logic to detect if a scheduled workout was missed (based on lack of logged activity in Health Connect).
    * If a day is missed, trigger an "Adapt Plan" workflow with two user options:
        * **Option A (Shift):** Push the schedule forward by one day (extend the end date).
        * **Option B (Intensify):** Keep the original end date but ask the AI to recalculate the remaining days with higher intensity to make up for the lost volume.

5.  **Cross-Platform Sync:**
    * Users must be able to log in on the Web to view their dashboard and progress charts.
    * Users must use the Android app for data tracking and Health Connect syncing.
    * Data must sync in real-time.

**Your Task:**
Please provide:
1.  **Tech Stack Recommendation:** Confirm the best libraries for Health Connect and AI integration.
2.  **Database Schema:** How to structure the User, WorkoutPlan, and DailyActivity data.
3.  **Prompt Engineering:** The specific system prompt I should send to the AI API to generate the workout JSON.
4.  **Code Scaffolding:** Create the basic file structure and the specific Kotlin code snippet for initializing the Health Connect client on Android.