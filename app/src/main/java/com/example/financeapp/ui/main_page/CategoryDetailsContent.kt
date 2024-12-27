package com.example.financeapp.ui.main_page

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.models.responses.CategoryDetails
import com.example.financeapp.models.responses.CurrentBalanceCategoriesResponse.Category
import com.example.financeapp.models.responses.CurrentBalanceCategoryResponse
import com.example.financeapp.services.RetrofitClient
import com.example.financeapp.viewmodel.UserViewModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@Composable
fun CategoryDetailsContent(
    userViewModel: UserViewModel,
    categoryId: String
): @Composable () -> Unit {
    val context = LocalContext.current
    val token by userViewModel.token.observeAsState()
    val apiService = RetrofitClient.apiService

    val content = @Composable {

        var category = remember {
            mutableStateOf(
                CurrentBalanceCategoryResponse(
                    category = CategoryDetails(
                        title = "Sample Category",
                        total = 0.0,
                        currency = "USD",
                        records = listOf()
                    )
                )
            )
        }

        fun showMessageToUser(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun formatDate(dateString: String): String {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val date = ZonedDateTime.parse(dateString, formatter)
            val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            return date.format(outputFormatter)
        }

        fun getCategoryInfo() {
            val call = apiService.getCurrentBalanceCategory("Bearer $token", categoryId)
            call.enqueue(object : Callback<CurrentBalanceCategoryResponse> {
                override fun onResponse(
                    call: Call<CurrentBalanceCategoryResponse>,
                    response: Response<CurrentBalanceCategoryResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { responseBody ->
                            category.value = responseBody
                            Log.d("debug", "Category init: ${category.value}")
                        }
                    } else {
                        val jsonObject = JSONObject(response.errorBody()?.string())
                        val errorMessage = jsonObject.optString("message", "An error occurred")
                        showMessageToUser(errorMessage)
                        Log.d("debug", "Editing name failed: ${jsonObject}")
                    }
                }

                override fun onFailure(call: Call<CurrentBalanceCategoryResponse>, t: Throwable) {
                    showMessageToUser("Error: ${t.localizedMessage}")
                }
            })
        }

        if (token != null) {
            LaunchedEffect(Unit) {
                getCategoryInfo()
            }

            Box() {
                Column(
                    modifier = Modifier.padding(40.dp, 100.dp),
                    verticalArrangement = Arrangement.spacedBy(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                )
                {
                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier.size(width = 350.dp, height = 105.dp),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        shape = RoundedCornerShape(30)
                    )
                    {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = category.value.category.title,
                                modifier = Modifier.padding(start = 20.dp),
                                fontSize = 30.sp
                            )
                            Text(
                                text = "${category.value.category.total} ${category.value.category.currency}",
                                modifier = Modifier.padding(end = 20.dp),
                                fontSize = 30.sp
                            )
                        }
                    }

                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier.size(width = 350.dp, height = 570.dp),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        shape = RoundedCornerShape(10)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = 10.dp)
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceAround,
                        )
                        {
                            items(category.value.category.records.size) { index ->
                                val item = category.value.category.records[index]
                                val formattedDate = formatDate(item.date)

                                val valueWithSign = when (item.type) {
                                    "expense" -> "-${item.value}"
                                    "income" -> "+${item.value}"
                                    else -> item.value.toString()
                                }


                                val (date, value) = listOf(formattedDate, item.value)

                                Row(
                                    modifier = Modifier.height(30.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(0.7f),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(item.title)
                                        Text(
                                            "($date)",
                                            modifier = Modifier.padding(start = 10.dp)
                                        )
                                    }
                                    Text(
                                        "$valueWithSign ${category.value.category.currency}",
                                        modifier = Modifier
                                            .weight(0.3f)
                                            .padding(start = 20.dp)
                                    )
                                }
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.primary,
                                    thickness = 2.dp,
                                    modifier = Modifier.padding(top = 5.dp, bottom = 15.dp)
                                )
                            }
                        }

                    }
                }
            }
        }
    }

    return content
}