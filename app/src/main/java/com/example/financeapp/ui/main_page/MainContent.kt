package com.example.financeapp.ui.main_page


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.models.responses.CurrentBalanceCategoriesResponse
import com.example.financeapp.models.responses.CurrentBalanceResponse
import com.example.financeapp.services.RetrofitClient
import com.example.financeapp.ui.theme.CustomCategoryCard
import com.example.financeapp.viewmodel.UserViewModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@OptIn(ExperimentalMaterial3Api::class)
fun MainContent(
    userViewModel: UserViewModel,
    categoryDetailsPage: (categoryId: String) -> Unit,
    addRecordPage: () -> Unit
): @Composable () -> Unit {

    val monthList = listOf("Січень", "Лютий", "Березень", "Квітень", "Травень",
        "Червень", "Липень", "Серпень", "Вересень", "Жовтень", "Листопад", "Грудень")

    var content = @Composable{

        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            val context = LocalContext.current
            val token by userViewModel.token.observeAsState()
            val apiService = RetrofitClient.apiService
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

            var currentBalance by remember {
                mutableStateOf(
                    CurrentBalanceResponse(
                        currency = "",
                        currentMonth = 0,
                        incomeTotal = 0,
                        expenseTotal = 0,
                        total = 0
                    )
                )
            }
            var currentBalanceCategories by remember {
                mutableStateOf(
                    CurrentBalanceCategoriesResponse(
                        currency = "",
                        currentMonth = 0,
                        categories = emptyList()
                    )
                )
            }

            var month by remember { mutableStateOf(monthList[0]) }

            fun showMessageToUser(message: String) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            if( token != null ){
                DisposableEffect(Unit) {
                    // Call 1: getCurrentBalance
                    val call1 = apiService.getCurrentBalance("Bearer $token")
                    call1.enqueue(object : Callback<CurrentBalanceResponse> {
                        override fun onResponse(
                            call: Call<CurrentBalanceResponse>,
                            response: Response<CurrentBalanceResponse>
                        ) {
                            if (response.isSuccessful) {
                                response.body()?.let {
                                    currentBalance = it
                                    month = monthList[it.currentMonth.toInt() - 1]
                                }
                            } else {
                                val jsonObject = JSONObject(response.errorBody()?.string())
                                val errorMessage = jsonObject.optString("message", "An error occurred")
                                showMessageToUser(errorMessage)
                                Log.d("debug", "Main page init failed 1: ${jsonObject}")
                            }
                        }
        
                        override fun onFailure(call: Call<CurrentBalanceResponse>, t: Throwable) {
                            showMessageToUser("Error: ${t.localizedMessage}")
                        }
                    })
                    // Call 2: getCurrentBalanceCategories
                    val call2 = apiService.getCurrentBalanceCategories("Bearer $token")
                    call2.enqueue(object : Callback<CurrentBalanceCategoriesResponse> {
                        override fun onResponse(
                            call: Call<CurrentBalanceCategoriesResponse>,
                            response: Response<CurrentBalanceCategoriesResponse>
                        ) {
                            if (response.isSuccessful) {
                                response.body()?.let {
                                    currentBalanceCategories = it
                                }
                            } else {
                                val jsonObject = JSONObject(response.errorBody()?.string())
                                val errorMessage = jsonObject.optString("message", "An error occurred")
                                showMessageToUser(errorMessage)
                                Log.d("debug", "Main page init failed 2: ${jsonObject}")
                            }
                        }
        
                        override fun onFailure(call: Call<CurrentBalanceCategoriesResponse>, t: Throwable) {
                            showMessageToUser("Error: ${t.localizedMessage}")
                        }
                    })
                    onDispose {}
                }
        
                Column(
                        modifier = Modifier.padding(40.dp, 100.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier
                            .size(width = 350.dp, height = 215.dp)
                            .offset(y = 10.dp),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        shape = RoundedCornerShape(15)
                    )
                    {
                        Text(
                            text = "$month",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp),
                            textAlign = TextAlign.Center,
                        )
                        Column(
                            modifier = Modifier
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceAround
                        ){
                            Row(modifier = Modifier
                                .padding(bottom = 10.dp)
                                .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text= "Дохід"
                                )
                                Text(
                                    text= "+ ${currentBalance.incomeTotal} ${currentBalanceCategories.currency}"
                                )
                            }
                            Row(modifier = Modifier
                                .padding(bottom = 10.dp)
                                .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text= "Витрати"
                                )
                                Text(
                                    text= "- ${currentBalance.expenseTotal} ${currentBalanceCategories.currency}"
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier
                                .padding(bottom = 10.dp)
                                .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text= "Баланс",
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text= "${currentBalance.total} ${currentBalanceCategories.currency}",
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.padding(top = 20.dp).offset(y=0.dp).height(420.dp),
                        contentPadding = PaddingValues(top = 30.dp)
                    )
                    {
                        items(currentBalanceCategories.categories.size) { index ->
                            val item = currentBalanceCategories.categories[index]

//                            val valueWithSign = when (item.type) {
//                                "expense" -> "-${item.value}"
//                                "income" -> "+${item.value}"
//                                else -> item.value.toString()
//                            }

                            CustomCategoryCard(
                                title = item.title,
                                total = "${item.total} ${currentBalanceCategories.currency}",
                                onClick = { categoryDetailsPage(item.categoryId) }
                            )
                        }
                    }
                }
        
                BottomAppBar(
                    modifier = Modifier.padding(top = 40.dp),
                    actions = {},
                    containerColor = MaterialTheme.colorScheme.background,
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = addRecordPage,
                            shape = CircleShape,
                            containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                        ) {
                            Icon(Icons.Filled.Add, "Localized description")
                        }
                    }
                )
            } else {
                Text("No token available.")
            }
        }
    }

    return content

}