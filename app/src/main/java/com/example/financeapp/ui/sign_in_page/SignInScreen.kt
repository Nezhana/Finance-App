package com.example.financeapp.ui.sign_in_page

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.financeapp.models.requests.RegisterRequest
import com.example.financeapp.models.responses.RegisterResponse
import com.example.financeapp.services.RetrofitClient
import com.example.financeapp.ui.theme.CustomPasswordInput
import com.example.financeapp.ui.theme.CustomTextField
import com.example.financeapp.ui.theme.CustomTextInknutAntiquaFont
import com.example.financeapp.ui.theme.CustomTitleInknutAntiquaFont
import com.example.financeapp.viewmodel.UserViewModel
import com.example.pr4_calc.ui.dropdown.DropdownList
import java.net.SocketTimeoutException


@Composable
fun SignInScreen(
    register: () -> Unit,
    logInScreen: () -> Unit,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var referalCode by remember { mutableStateOf("") }
    var currency: List<String> = listOf("EUR", "USD", "UAH")
    var selectedIndexDrop by rememberSaveable { mutableStateOf(0) }
    val buttonModifier = Modifier.width(280.dp)

    fun showMessageToUser(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun registerUser() {
        val apiService = RetrofitClient.apiService

        val request = RegisterRequest(
            name = name,
            email = email,
            password = password,
            referalCode = if (referalCode.isEmpty()) null else referalCode,
            currency = currency[selectedIndexDrop]
        )

        apiService.registerUser(request).enqueue(object : retrofit2.Callback<RegisterResponse> {
            override fun onResponse(
                call: retrofit2.Call<RegisterResponse>,
                response: retrofit2.Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    registerResponse?.token?.let {
                        userViewModel.setToken(it)
                        Log.d("debug", "Registration successful: $it")
                        register()
                    }
                } else {
                    Log.d("debug", "Registration failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<RegisterResponse>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    Log.d("debug", "Timeout error: ${t.message}")
                    showMessageToUser("The server might be sleeping. Please try again.")
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
        email = CustomTextField("Email", Modifier)
        password = CustomPasswordInput("Password", Modifier)
        DropdownList(currency, selectedIndexDrop, buttonModifier, onItemClick = {
            selectedIndexDrop = it
            val choosed_currency = currency[it]
        })
        referalCode = CustomTextField("Реферальний код", Modifier)
        Column(modifier = Modifier.padding(0.dp, 100.dp).height(300.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .border(2.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(25)),
                onClick = { 
                    registerUser()
                },
                border = ButtonDefaults.outlinedButtonBorder(false)
            ) {
                CustomTextInknutAntiquaFont("Зареєструватися")
            }
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                onClick = logInScreen,
            ) {
                Text("Увійти в обліковий запис")
            }
        }
    }
}