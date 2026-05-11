package com.example.callormessage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.text.input.KeyboardType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CallOrMessage()
        }
    }
}

@Composable
fun CallOrMessage() {

    val context = LocalContext.current

    var phoneNumber by remember { mutableStateOf("") }
    var showCallDialog by remember { mutableStateOf(false) }
    var showSmsDialog by remember { mutableStateOf(false) }


    // call
    val callPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            makeCall(context, phoneNumber)
        } else {
            Toast.makeText(context, "Zəng icazəsi verilmədi", Toast.LENGTH_SHORT).show()
        }
    }

    // message
    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            sendSms(phoneNumber)
            Toast.makeText(context, "Mesaj göndərildi", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "SMS icazəsi verilmədi", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Telefon nömrəsi") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (phoneNumber.isBlank()) {
                    Toast.makeText(context, "Nömrə boş ola bilməz!", Toast.LENGTH_SHORT).show()
                } else {
                    showCallDialog = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zəng et")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (phoneNumber.isBlank()) {
                    Toast.makeText(context, "Nömrə boş ola bilməz!", Toast.LENGTH_SHORT).show()
                } else {
                    showSmsDialog = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mesaj göndər")
        }
    }

    // call
    if (showCallDialog) {
        AlertDialog(
            onDismissRequest = { showCallDialog = false },
            title = { Text("Təsdiq") },
            text = { Text("Zəng etmək istədiyinizə əminsiniz?") },
            confirmButton = {
                TextButton(onClick = {
                    showCallDialog = false

                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CALL_PHONE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        makeCall(context, phoneNumber)
                    } else {
                        callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                    }

                }) {
                    Text("Bəli")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCallDialog = false }) {
                    Text("Xeyr")
                }
            }
        )
    }

    // message
    if (showSmsDialog) {
        AlertDialog(
            onDismissRequest = { showSmsDialog = false },
            title = { Text("Təsdiq") },
            text = { Text("Mesaj göndərmək istədiyinizə əminsiniz?") },
            confirmButton = {
                TextButton(onClick = {
                    showSmsDialog = false

                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.SEND_SMS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        sendSms(phoneNumber)
                        Toast.makeText(context, "Mesaj göndərildi", Toast.LENGTH_SHORT).show()
                    } else {
                        smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                    }

                }) {
                    Text("Bəli")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSmsDialog = false }) {
                    Text("Xeyr")
                }
            }
        )
    }
}

// call function
fun makeCall(context: android.content.Context, phone: String) {
    val intent = Intent(Intent.ACTION_CALL)
    intent.data = Uri.parse("tel:$phone")
    context.startActivity(intent)
}

// message function
fun sendSms(phone: String) {
    val smsManager = SmsManager.getDefault()
    smsManager.sendTextMessage(phone, null, "Bu avtomatik mesajdır!", null, null)
}