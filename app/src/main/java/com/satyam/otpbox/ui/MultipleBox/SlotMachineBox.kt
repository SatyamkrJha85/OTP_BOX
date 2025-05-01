package com.satyam.otpbox.ui.MultipleBox


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SlotMachineOtp(
    otpText: String,
    otpCount: Int = 4,
    onOtpTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    val focusRequesters = remember { List(otpCount) { FocusRequester() } }
    val focusManager = LocalFocusManager.current
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(otpText.length) {
        if (otpText.length < otpCount) {
            focusRequesters[otpText.length.coerceIn(0, otpCount - 1)].requestFocus()
        } else if (otpText.length == otpCount) {
            focusManager.clearFocus()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 24.dp)
        ) {
            repeat(otpCount) { index ->
                val char = if (index < otpText.length) otpText[index].toString() else ""
                val isFocused = otpText.length == index || (index == otpText.length - 1 && otpText.length == otpCount)

                LaunchedEffect(char) {
                    if (char.isNotEmpty()) {
                        animatedProgress.animateTo(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = 0.4f,
                                stiffness = 200f
                            )
                        )
                    } else {
                        animatedProgress.snapTo(0f)
                    }
                }

                val elevation by animateDpAsState(
                    targetValue = if (isFocused) 8.dp else 2.dp,
                    animationSpec = tween(durationMillis = 200)
                )

                val borderColor = when {
                    isError -> MaterialTheme.colorScheme.error
                    isFocused -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outlineVariant
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(48.dp)
                        .height(64.dp)
                        .shadow(elevation, shape = RoundedCornerShape(8.dp))
                        .border(
                            width = if (isFocused) 1.5.dp else 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            focusRequesters[index.coerceIn(0, otpCount - 1)].requestFocus()
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                translationY = animatedProgress.value * -10f
                                alpha = animatedProgress.value
                            }
                    ) {
                        Text(
                            text = char,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    if (char.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Transparent)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )
                    }

                    BasicTextField(
                        value = char,
                        onValueChange = { newChar ->
                            if (newChar.length <= 1 && (newChar.isEmpty() || newChar.all { it.isDigit() })) {
                                val newText = buildString {
                                    append(otpText.take(index))
                                    append(newChar.take(1))
                                    append(otpText.drop(index + 1))
                                }.take(otpCount)

                                if (newText != otpText) {
                                    onOtpTextChange(newText)
                                    if (newChar.isNotEmpty() && index < otpCount - 1) {
                                        focusRequesters[index + 1].requestFocus()
                                    }
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = if (index == otpCount - 1) ImeAction.Done else ImeAction.Next
                        ),
                        textStyle = TextStyle(
                            fontSize = 1.sp,
                            color = Color.Transparent
                        ),
                        modifier = Modifier
                            .size(1.dp)
                            .focusRequester(focusRequesters[index])
                            .onKeyEvent {
                                if (it.key == Key.Backspace && char.isEmpty() && index > 0) {
                                    focusRequesters[index - 1].requestFocus()
                                    true
                                } else {
                                    false
                                }
                            }
                            .semantics {
                                contentDescription = "OTP digit ${index + 1}"
                            }
                    )
                }
            }
        }

        if (isError) {
            Text(
                text = "Invalid code. Please try again.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun SlotMachineOtpScreen() {
    var otpText by remember { mutableStateOf("") }
    val isError = remember(otpText) { otpText.length == 4 && otpText != "1234" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Slot Machine Box",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )


        SlotMachineOtp(
            otpText = otpText,
            otpCount = 4,
            onOtpTextChange = { otpText = it },
            isError = isError
        )


    }
}