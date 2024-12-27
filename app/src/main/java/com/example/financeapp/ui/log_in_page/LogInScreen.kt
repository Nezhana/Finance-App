package com.example.financeapp.ui.log_in_page


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.models.requests.LoginRequest
import com.example.financeapp.models.responses.LoginResponse
import com.example.financeapp.services.RetrofitClient
import com.example.financeapp.ui.theme.CustomPasswordInput
import com.example.financeapp.ui.theme.CustomTextField
import com.example.financeapp.ui.theme.CustomTextInknutAntiquaFont
import com.example.financeapp.ui.theme.CustomTitleInknutAntiquaFont
import com.example.financeapp.viewmodel.UserViewModel
import org.json.JSONObject
import java.net.SocketTimeoutException


@Composable
fun LogInScreen(
    authorizate: () -> Unit,
    signInScreen: () -> Unit,
    passwordRecoveryScreen: () -> Unit,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    fun showMessageToUser(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun loginUser() {
        val apiService = RetrofitClient.apiService

        val request = LoginRequest(
            email = email,
            password = password
        )

        apiService.loginUser(request).enqueue(object : retrofit2.Callback<LoginResponse>  {
            override fun onResponse(
                call: retrofit2.Call<LoginResponse>,
                response: retrofit2.Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.token?.let {
                        userViewModel.setToken(it)
                        Log.d("debug", "Login successful: $it")
                        authorizate()
                    }
                } else {
                    val jsonObject = JSONObject(response.errorBody()?.string())
                    val errorMessage = jsonObject.optString("message", "An error occurred")
                    showMessageToUser(errorMessage)
                    Log.d("debug", "Login failed: ${jsonObject}")
                }
            }

            override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    Log.d("debug", "Timeout error: ${t.message}")
                    showMessageToUser("The server might be sleeping. Please try again (in 30s).")
                } else {
                    Log.d("debug", "Error: ${t.message}")
                    showMessageToUser("An error occurred: ${t.message}")
                }
            }
        })
    }

    Column(modifier = Modifier.padding(60.dp, 60.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        CustomTitleInknutAntiquaFont(
            text = "FINANCE",
            modifier = Modifier.padding(0.dp, 40.dp)
        )
        email = CustomTextField("Email", Modifier)
        password = CustomPasswordInput("Password", Modifier)
        Column(modifier = Modifier.padding(0.dp, 100.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .border(2.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(25)),
                border = ButtonDefaults.outlinedButtonBorder(false),
                onClick = { 
                    loginUser()
                }
            ) {
                CustomTextInknutAntiquaFont("Авторизуватися")
            }
            TextButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = passwordRecoveryScreen
            ) {
                Text("Відновити пароль")
            }
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                onClick = signInScreen
            ) {
                Text("Створити обліковий запис")
            }
        }
    }
}