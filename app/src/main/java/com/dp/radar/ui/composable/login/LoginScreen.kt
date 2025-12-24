package com.dp.radar.ui.composable.login

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dp.radar.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (name: String, email: String) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(R.color.primary_background),
    ) {
        val context = LocalContext.current
        context as Activity


        val signInClient = remember {
            Identity.getSignInClient(context)
        }

        // Launcher for result
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val credential = signInClient.getSignInCredentialFromIntent(result.data)
                val email = credential.id // email address
                val token = credential.googleIdToken
                val name: String = credential.givenName ?: ""

                if (token != null) {
                    onLoginSuccess(name, email)
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginSection()

            OutlinedButton(
                onClick = {
                    val request = BeginSignInRequest.Builder()
                        .setGoogleIdTokenRequestOptions(
                            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId("901628457606-fporkifmmrr5ttrep0cdtlgheamg05ng.apps.googleusercontent.com")
                                .setFilterByAuthorizedAccounts(false)
                                .build()
                        )
                        .build()

                    signInClient.beginSignIn(request)
                        .addOnSuccessListener { result ->
                            val intentSenderRequest =
                                IntentSenderRequest.Builder(result.pendingIntent.intentSender)
                                    .build()
                            launcher.launch(intentSenderRequest)
                        }
                        .addOnFailureListener { re ->
                            Log.e("Error", re.message.toString())
                            Toast.makeText(
                                context,
                                "Have you set up Gmail on your device?",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(50.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google_login),
                    contentDescription = "Google Icon",
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Continue with Google", fontSize = 20.sp)
            }
        }
    }

}

@Composable
fun LoginSection() {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Text(
            text = "Already Registered?",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
        )

        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email ID") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* Handle login */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Login", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(contentAlignment = Alignment.Center) {
            Text(text = "OR", textAlign = TextAlign.Center)
        }
    }
}
