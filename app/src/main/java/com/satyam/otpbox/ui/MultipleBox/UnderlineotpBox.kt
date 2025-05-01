package com.satyam.otpbox.ui.MultipleBox

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun UnderlineAnimatedOtpBox(
    otpText: String,
    otpCount: Int = 6,
    onOtpTextChange: (String) -> Unit,
    cellWidth: Int = 48,
    isError: Boolean = false
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val focusRequesters = remember { List(otpCount) { FocusRequester() } }

    // Auto-focus the first empty cell when composable appears
    LaunchedEffect(Unit) {
        if (otpText.length < otpCount) {
            focusRequesters[otpText.length].requestFocus()
        }
    }

    // Handle focus when text changes
    LaunchedEffect(otpText) {
        if (otpText.length < otpCount) {
            focusRequesters[otpText.length].requestFocus()
        } else if (otpText.length == otpCount) {
            focusManager.clearFocus()
        }
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(otpCount) { index ->
            val char = if (index < otpText.length) otpText[index].toString() else ""
            val isFocused = otpText.length == index || (index == otpText.length - 1 && otpText.length == otpCount)

            val animatedAlpha by animateFloatAsState(
                targetValue = if (isFocused) 1f else 0.6f,
                animationSpec = tween(durationMillis = 150)
            )

            val animatedLineHeight by animateDpAsState(
                targetValue = if (isFocused) 2.dp else 1.dp,
                animationSpec = tween(durationMillis = 150)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(cellWidth.dp)
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
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = if (index < otpCount - 1) ImeAction.Next else ImeAction.Done
                    ),
                    textStyle = TextStyle(
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        color = if (isError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .focusRequester(focusRequesters[index])
                        .onKeyEvent { event ->
                            if (event.key == Key.Backspace && char.isEmpty() && index > 0) {
                                focusRequesters[index - 1].requestFocus()
                                true
                            } else {
                                false
                            }
                        }
                        .semantics { contentDescription = "OTP digit ${index + 1}" }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(animatedLineHeight)
                        .background(
                            color = when {
                                isError -> MaterialTheme.colorScheme.error
                                isFocused -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            },
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }

            if (index < otpCount - 1) {
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

