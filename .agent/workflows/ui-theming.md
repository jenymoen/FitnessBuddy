---
description: How to create new UI screens for FitnessBuddy with consistent theming
---

# FitnessBuddy UI Screen Creation

When creating new Composable screens for FitnessBuddy, always use the app's dark theme with accent green styling.

## Theme Colors

Always include these color definitions at the top of new screen files:

```kotlin
// Theme colors (consistent across the app)
private val DarkBackground = Color(0xFF0D0D0D)
private val CardBackground = Color(0xFF1A1A1A)
private val AccentGreen = Color(0xFFCDFF00)
private val TextWhite = Color.White
private val TextGray = Color(0xFF888888)
```

## Screen Structure

1. **Root Column** - Use `DarkBackground` as the background color
2. **TopAppBar** - Use `DarkBackground` as container color, `TextWhite` for text, `AccentGreen` for accent labels
3. **Cards** - Use `CardBackground` with `RoundedCornerShape(16.dp)`
4. **Section Labels** - Use `TextGray` with 11sp font size and Medium weight
5. **Body Text** - Use `TextWhite` for primary text, `TextGray` for secondary
6. **Buttons** - Use `AccentGreen` background with `Color.Black` text

## Example TopAppBar

```kotlin
TopAppBar(
    title = {
        Column {
            Text(
                text = "SECTION LABEL",
                color = AccentGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Screen Title",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    },
    navigationIcon = {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextWhite
            )
        }
    },
    colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
)
```

## Example Button

```kotlin
Button(
    onClick = { /* action */ },
    modifier = Modifier
        .fillMaxWidth()
        .height(56.dp),
    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
    shape = RoundedCornerShape(28.dp)
) {
    Icon(
        Icons.Default.PlayArrow,
        contentDescription = null,
        tint = Color.Black
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
        text = "BUTTON TEXT",
        color = Color.Black,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
}
```

## Reference Files

For examples, see:
- `ui/trainingplan/WorkoutDetailScreen.kt`
- `ui/trainingplan/WeekDetailScreen.kt`
- `ui/workout/ActiveWorkoutScreen.kt`
