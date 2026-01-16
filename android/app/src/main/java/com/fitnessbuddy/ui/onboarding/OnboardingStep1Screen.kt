package com.fitnessbuddy.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// Theme colors matching the design
private val DarkBackground = Color(0xFF0D0D0D)
private val CardBackground = Color(0xFF1A1A1A)
private val AccentGreen = Color(0xFFCDFF00)
private val TextWhite = Color.White
private val TextGray = Color(0xFF888888)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingStep1Screen(
    viewModel: OnboardingViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    var showWeightDialog by remember { mutableStateOf(false) }
    var showHeightDialog by remember { mutableStateOf(false) }
    var showAgeDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        // Top Bar with back button and title
        TopAppBar(
            title = {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "FitnessBuddy",
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold,
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
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
        
        // Progress indicator
        ProgressSection(
            currentStep = viewModel.currentStep,
            totalSteps = viewModel.totalSteps,
            progressPercent = viewModel.getProgressPercent()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Title and subtitle
        Text(
            text = "Personal Details",
            color = TextWhite,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "We use this to calibrate your\nendurance zones and AI coaching.",
            color = TextGray,
            fontSize = 16.sp,
            lineHeight = 22.sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Weight Card (with highlight border)
        MetricInputCard(
            label = "CURRENT WEIGHT",
            value = if (viewModel.weightUnit == WeightUnit.KG) 
                String.format("%.1f", viewModel.weight) 
            else 
                String.format("%.0f", viewModel.weight),
            unit = viewModel.weightUnit.suffix,
            selectedUnit = viewModel.weightUnit.label,
            units = listOf("kg", "lbs"),
            onUnitChange = { viewModel.toggleWeightUnit() },
            onClick = { showWeightDialog = true },
            isHighlighted = true
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Height Card
        MetricInputCard(
            label = "TOTAL HEIGHT",
            value = if (viewModel.heightUnit == HeightUnit.CM) 
                String.format("%.0f", viewModel.height) 
            else 
                String.format("%.1f", viewModel.height),
            unit = viewModel.heightUnit.suffix,
            selectedUnit = viewModel.heightUnit.label,
            units = listOf("cm", "ft"),
            onUnitChange = { viewModel.toggleHeightUnit() },
            onClick = { showHeightDialog = true },
            isHighlighted = false
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Age Card
        AgeInputCard(
            label = "AGE",
            value = viewModel.age.toString(),
            onClick = { showAgeDialog = true }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Next Button
        Button(
            onClick = {
                viewModel.nextStep()
                onNext()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentGreen
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Next",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
    
    // Weight Dialog
    if (showWeightDialog) {
        NumberInputDialog(
            title = "Enter Weight",
            currentValue = viewModel.weight.toString(),
            suffix = viewModel.weightUnit.suffix,
            onDismiss = { showWeightDialog = false },
            onConfirm = { value ->
                value.toFloatOrNull()?.let { viewModel.weight = it }
                showWeightDialog = false
            },
            isDecimal = true
        )
    }
    
    // Height Dialog
    if (showHeightDialog) {
        NumberInputDialog(
            title = "Enter Height",
            currentValue = viewModel.height.toInt().toString(),
            suffix = viewModel.heightUnit.suffix,
            onDismiss = { showHeightDialog = false },
            onConfirm = { value ->
                value.toFloatOrNull()?.let { viewModel.height = it }
                showHeightDialog = false
            },
            isDecimal = viewModel.heightUnit == HeightUnit.FT
        )
    }
    
    // Age Dialog
    if (showAgeDialog) {
        NumberInputDialog(
            title = "Enter Age",
            currentValue = viewModel.age.toString(),
            suffix = "years",
            onDismiss = { showAgeDialog = false },
            onConfirm = { value ->
                value.toIntOrNull()?.let { viewModel.age = it }
                showAgeDialog = false
            },
            isDecimal = false
        )
    }
}

@Composable
private fun NumberInputDialog(
    title: String,
    currentValue: String,
    suffix: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    isDecimal: Boolean
) {
    var textValue by remember { mutableStateOf(currentValue) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (isDecimal) KeyboardType.Decimal else KeyboardType.Number
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = AccentGreen,
                        unfocusedBorderColor = TextGray,
                        cursorColor = AccentGreen
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    suffix = {
                        Text(
                            text = suffix,
                            color = TextGray,
                            fontSize = 16.sp
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextWhite
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = { onConfirm(textValue) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirm", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressSection(
    currentStep: Int,
    totalSteps: Int,
    progressPercent: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "STEP $currentStep OF $totalSteps",
            color = TextGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "$progressPercent% COMPLETE",
            color = AccentGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    
    // Progress bar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(CardBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progressPercent / 100f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(2.dp))
                .background(AccentGreen)
        )
    }
}

@Composable
private fun MetricInputCard(
    label: String,
    value: String,
    unit: String,
    selectedUnit: String,
    units: List<String>,
    onUnitChange: () -> Unit,
    onClick: () -> Unit,
    isHighlighted: Boolean
) {
    val borderColor = if (isHighlighted) AccentGreen else Color.Transparent
    val borderWidth = if (isHighlighted) 2.dp else 0.dp
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                
                // Unit toggle
                UnitToggle(
                    units = units,
                    selectedUnit = selectedUnit,
                    onUnitChange = onUnitChange
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    color = TextWhite,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = unit,
                    color = TextGray,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun AgeInputCard(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    color = TextWhite,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "yrs",
                    color = TextGray,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun UnitToggle(
    units: List<String>,
    selectedUnit: String,
    onUnitChange: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF2A2A2A))
            .padding(2.dp)
    ) {
        units.forEach { unit ->
            val isSelected = unit == selectedUnit
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isSelected) Color(0xFF3A3A3A) else Color.Transparent)
                    .clickable { if (!isSelected) onUnitChange() }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = unit,
                    color = if (isSelected) TextWhite else TextGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
