package com.example.financeapp.ui.main_page


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.models.responses.CurrentBalanceCategoriesResponse
import com.example.financeapp.models.responses.CurrentBalanceResponse
import com.example.financeapp.services.RetrofitClient
import com.example.financeapp.viewmodel.UserViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@OptIn(ExperimentalMaterial3Api::class)
fun MainContent(
    userViewModel: UserViewModel
): @Composable () -> Unit {

    val content = @Composable{

        Box() {
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
                                }
                            } else {
                                showMessageToUser(response.message())
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
                                showMessageToUser(response.message())
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
                    Text(
                        text = "Баланс",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 30.sp,
                        modifier = Modifier.padding(0.dp, 20.dp)
                    )
                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier.size(width = 350.dp, height = 215.dp),
                        colors = CardColors(
                            containerColor = Color(0xFF00ADB5),
                            contentColor = Color(0xFFFFFFFF),
                            disabledContainerColor = Color(0xFF00ADB5),
                            disabledContentColor = Color(0xFFFFFFFF)
                        ),
                    )
                    {
                        Text(
                            text = "Актуальний місяць",
                            modifier = Modifier
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                        )
                        Column(
                            modifier = Modifier
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceAround
                        ){
                            Row(modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text= "Дохід"
                                )
                                Text(
                                    text= "+ ${currentBalance.incomeTotal} ₴"
                                )
                            }
                            Row(modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text= "Витрати"
                                )
                                Text(
                                    text= "- ${currentBalance.expenseTotal} ₴"
                                )
                            }
                            Row(modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text= "Баланс"
                                )
                                Text(
                                    text= "${currentBalance.total} ₴"
                                )
                            }
                        }
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(top = 30.dp)
                    )
                    {
                        items(currentBalanceCategories.categories.size) { index ->
                            val item = currentBalanceCategories.categories[index]
                            ElevatedCard(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 6.dp
                                ),
                                modifier = Modifier
                                    .size(width = 350.dp, height = 57.dp)
                                    .padding(bottom = 10.dp)
                                    .clip(RoundedCornerShape(50)),
                                colors = CardColors(
                                    containerColor = Color(0xFF222831),
                                    contentColor = Color(0xFFFFFFFF),
                                    disabledContainerColor = Color(0xFF222831),
                                    disabledContentColor = Color(0xFFFFFFFF)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.title,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center,
                                    )
                                    Text (
                                        text = "${item.total} ₴",
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }
        
                BottomAppBar(
                    modifier = Modifier.absoluteOffset(y = 786.dp),
                    actions = {},
                    containerColor = MaterialTheme.colorScheme.background,
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { /* do something */ },
                            shape = CircleShape,
                            containerColor = MaterialTheme.colorScheme.secondary,
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

//    Drawer(content)

    return content

}