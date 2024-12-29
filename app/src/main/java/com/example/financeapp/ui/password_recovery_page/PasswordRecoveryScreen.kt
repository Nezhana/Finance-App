package com.example.financeapp.ui.password_recovery_page

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.models.requests.PasswordRecoveryRequest
import com.example.financeapp.models.responses.PasswordRecoveryResponse
import com.example.financeapp.services.RetrofitClient
import com.example.financeapp.ui.theme.CustomTextField
import com.example.financeapp.ui.theme.CustomTextInknutAntiquaFont
import com.example.financeapp.ui.theme.CustomTitleInknutAntiquaFont
import com.example.financeapp.viewmodel.UserViewModel
import com.example.financeapp.ui.dropdown.DropdownList
import org.json.JSONObject
import java.net.SocketTimeoutException


@Composable
fun PasswordRecoveryScreen(
    redirect: () -> Unit,
    logInScreen: () -> Unit,
    signInScreen: () -> Unit,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var currency: List<String> = listOf("EUR", "USD", "UAH")
    var selectedIndexDrop by rememberSaveable { mutableStateOf(0) }
    val buttonModifier = Modifier.width(280.dp)

    fun showMessageToUser(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun passwordRecovery() {
        val apiService = RetrofitClient.apiService

        val request = PasswordRecoveryRequest(
            name = name,
            email = email,
            currency = currency[selectedIndexDrop]
        )

        apiService.recoverPassword(request).enqueue(object : retrofit2.Callback<PasswordRecoveryResponse>  {
            override fun onResponse(
                call: retrofit2.Call<PasswordRecoveryResponse>,
                response: retrofit2.Response<PasswordRecoveryResponse>
            ) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.token?.let {
                        userViewModel.setToken(it)
                        Log.d("debug", "Recovered password: $it")
                        redirect()
                    }
                } else {
                    val jsonObject = JSONObject(response.errorBody()?.string())
                    val errorMessage = jsonObject.optString("message", "An error occurred")
                    showMessageToUser(errorMessage)
                    Log.d("debug", "Password recovering failed: ${jsonObject}")
                }
            }

            override fun onFailure(call: retrofit2.Call<PasswordRecoveryResponse>, t: Throwable) {
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
        name = CustomTextField("Ім'я", Modifier)
        email = CustomTextField("Електронна пошта", Modifier)
        DropdownList(currency, selectedIndexDrop, buttonModifier, onItemClick = {
            selectedIndexDrop = it
            val choosed_currency = currency[it]
        })
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
                    passwordRecovery()
                }
            ) {
                CustomTextInknutAntiquaFont("Відновити доступ")
            }
            TextButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = logInScreen
            ) {
                Text("Увійти в обліковй запис", color = MaterialTheme.colorScheme.secondary)
            }
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                onClick = signInScreen
            ) {
                Text("Створити обліковй запис", color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}