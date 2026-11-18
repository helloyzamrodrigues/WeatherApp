package com.weatherapp

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.weatherapp.ui.theme.WeatherAppTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Scaffold {
                    LoginPage(modifier = Modifier.padding(it))
                }
            }
        }
    }
}
@Composable
fun LoginPage(modifier: Modifier = Modifier) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val activity = LocalActivity.current

    Column(
        modifier = modifier.padding(24.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val fieldModifier = modifier.fillMaxWidth(fraction = 0.9f)
        Text(
            text = "Bem-vindo/a!",
            fontSize = 24.sp
        )
        OutlinedTextField(
            value = email,
            label = { Text(text = "Digite seu e-mail") },
            modifier = fieldModifier,
            onValueChange = { email = it }
        )
        OutlinedTextField(
            value = password,
            label = { Text(text = "Digite sua senha") },
            modifier = fieldModifier,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation()
        )
        Row(
            modifier = fieldModifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        activity?.let {
                            Toast.makeText(it, "Por favor, preencha o e-mail e a senha!", Toast.LENGTH_LONG).show()
                        }
                        return@Button
                    }
                    activity?.let { act ->
                        Firebase.auth.signInWithEmailAndPassword(email.trim(), password)
                            .addOnCompleteListener(act) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(act, "Login OK!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(act, "Login FALHOU!", Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                },
                enabled = email.isNotBlank() && password.isNotBlank()
            ) {
                Text("Login")
            }
            Button(onClick = {
                activity?.startActivity(
                    Intent(activity, RegisterActivity::class.java).setFlags(
                        FLAG_ACTIVITY_SINGLE_TOP
                    )
                )
            }) {
                Text("Registrar")
            }
            Button(
                onClick = { email = ""; password = "" },
                enabled = email.isNotEmpty() || password.isNotEmpty()
            ) {
                Text("Limpar")
            }
        }
    }
}
