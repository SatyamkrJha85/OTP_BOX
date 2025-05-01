package com.satyam.otpbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.satyam.otpbox.ui.MultipleBox.FlipCardOtpScreen
import com.satyam.otpbox.ui.MultipleBox.PinOtpVerificationScreen
import com.satyam.otpbox.ui.MultipleBox.RoundedBoxAnimatedOtp
import com.satyam.otpbox.ui.MultipleBox.SlotMachineOtpScreen
import com.satyam.otpbox.ui.MultipleBox.UnderlineAnimatedOtpBox
import com.satyam.otpbox.ui.theme.OtpBoxTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OtpBoxTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("Secure Verification", fontWeight = FontWeight.Bold) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary
                            ),
                            actions = {
                                IconButton(onClick = { /* Handle help */ }) {
                                    Icon(Icons.Default.Info, contentDescription = "Help")
                                }
                            }
                        )
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp)
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                            .background(MaterialTheme.colorScheme.background),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        // OTP Methods with cards
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(top = 30.dp)
                                .animateContentSize(),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Underlined OTP",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                UnderlinedOtpExample()
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .animateContentSize(),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Boxed OTP",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                BoxedOtpExample()
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .animateContentSize(),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "PIN Verification",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                PinOtpVerificationScreen()
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .animateContentSize(),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Flip Card OTP",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                FlipCardOtpScreen()
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .animateContentSize(),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Slot Machine OTP",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                SlotMachineOtpScreen()
                            }
                        }


                    }
                }
            }
        }
    }
}

@Composable
fun UnderlinedOtpExample() {
    var otpText by remember { mutableStateOf("") }
    val isError = otpText.length == 6 && otpText != "123456"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Underline OTP Style",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        UnderlineAnimatedOtpBox(
            otpText = otpText,
            otpCount = 6,
            onOtpTextChange = { newText -> otpText = newText },
            cellWidth = 48,
            isError = isError
        )

        if (isError) {
            Text(
                text = "Invalid OTP. Please try again.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun BoxedOtpExample() {
    var otpText by remember { mutableStateOf("") }
    val isError = remember(otpText) {
        otpText.length == 4 && otpText != "1234"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Boxed OTP Style",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        RoundedBoxAnimatedOtp(
            otpText = otpText,
            otpCount = 4,
            onOtpTextChange = { otpText = it },
            isError = isError
        )


    }
}