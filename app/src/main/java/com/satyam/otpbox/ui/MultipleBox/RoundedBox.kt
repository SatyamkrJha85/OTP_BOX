package com.satyam.otpbox.ui.MultipleBox

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
fun RoundedBoxAnimatedOtp(
    otpText: String,
    otpCount: Int = 4,
    onOtpTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    val focusRequesters = remember { List(otpCount) { FocusRequester() } }
    val focusManager = LocalFocusManager.current

    // Handle focus changes
    LaunchedEffect(otpText.length, isError) {
        if (otpText.length < otpCount) {
            focusRequesters[otpText.length.coerceIn(0, otpCount - 1)].requestFocus()
        } else if (otpText.length == otpCount) {
            focusManager.clearFocus()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            repeat(otpCount) { index ->
                val char = if (index < otpText.length) otpText[index].toString() else ""
                val isFocused = otpText.length == index ||
                        (index == otpText.length - 1 && otpText.length == otpCount)

                // Animations
                val animatedScale by animateFloatAsState(
                    targetValue = if (isFocused) 1.03f else 1f,
                    animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f)
                )
                val animatedBorderWidth by animateDpAsState(
                    targetValue = if (isFocused) 2.dp else 1.dp,
                    animationSpec = spring(dampingRatio = 0.7f)
                )

                // Colors
                val borderColor = when {
                    isError -> MaterialTheme.colorScheme.error
                    isFocused -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outline
                }
                val backgroundColor = when {
                    isError -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                    isFocused -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f)
                    else -> MaterialTheme.colorScheme.surface
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .scale(animatedScale)
                        .border(
                            width = animatedBorderWidth,
                            color = borderColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(backgroundColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            focusRequesters[index].requestFocus()
                        }
                ) {
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
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center,
                            color = if (isError) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.Center)
                            .focusRequester(focusRequesters[index])
                            .onKeyEvent { event ->
                                when {
                                    event.key == Key.Backspace && char.isEmpty() && index > 0 -> {
                                        focusRequesters[index - 1].requestFocus()
                                        onOtpTextChange(otpText.dropLast(1))
                                        true
                                    }
                                    event.key == Key.Backspace && char.isNotEmpty() -> {
                                        onOtpTextChange(otpText.take(index) + otpText.drop(index + 1))
                                        true
                                    }
                                    else -> false
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
                text = "Invalid OTP. Please try again.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}