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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
fun PinCircleAnimatedOtp(
    otpText: String,
    otpCount: Int = 6,
    onOtpTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    val focusRequesters = remember { List(otpCount) { FocusRequester() } }
    val focusManager = LocalFocusManager.current

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
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 24.dp)
        ) {
            repeat(otpCount) { index ->
                val hasValue = index < otpText.length
                val isFocused = index == otpText.length || (index == otpText.length - 1 && otpText.length == otpCount)

                val animatedSize by animateDpAsState(
                    targetValue = if (hasValue) 24.dp else if (isFocused) 20.dp else 16.dp,
                    animationSpec = spring(dampingRatio = 0.4f, stiffness = 200f)
                )

                val borderColor = when {
                    isError -> MaterialTheme.colorScheme.error
                    isFocused -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outlineVariant
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (isFocused) 1.5.dp else 1.dp,
                            color = borderColor,
                            shape = CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            focusRequesters[index.coerceIn(0, otpCount - 1)].requestFocus()
                        }
                ) {
                    if (hasValue) {
                        Box(
                            modifier = Modifier
                                .size(animatedSize)
                                .background(
                                    color = if (isError) MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        )
                    }

                    BasicTextField(
                        value = if (index < otpText.length) otpText[index].toString() else "",
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
                            fontSize = 1.sp, // Hidden text field
                            color = Color.Transparent
                        ),
                        modifier = Modifier
                            .size(1.dp)
                            .focusRequester(focusRequesters[index])
                            .onKeyEvent {
                                if (it.key == Key.Backspace && !hasValue && index > 0) {
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
                text = "Invalid verification code",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// Example usage:
@Composable
fun PinOtpVerificationScreen() {
    var otpText by remember { mutableStateOf("") }
    val isError = remember(otpText) { otpText.length == 6 && otpText != "123456" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Pin Circle Box",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )


        PinCircleAnimatedOtp(
            otpText = otpText,
            otpCount = 6,
            onOtpTextChange = { otpText = it },
            isError = isError
        )


    }
}