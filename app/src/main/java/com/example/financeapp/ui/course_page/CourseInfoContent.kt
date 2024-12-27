package com.example.financeapp.ui.course_page


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.models.responses.CurrenciesResponse
import com.example.financeapp.models.responses.Rates
import com.example.financeapp.services.RetrofitClient
import com.example.financeapp.viewmodel.UserViewModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.reflect.full.memberProperties


@Composable
fun CourseInfoContent(
    userViewModel: UserViewModel
): @Composable () -> Unit {
    val context = LocalContext.current
    val token by userViewModel.token.observeAsState()
    val apiService = RetrofitClient.apiService

    val infoRowModifier = Modifier
        .fillMaxWidth()
        .height(40.dp)
        .padding(top = 10.dp)

    val df = DecimalFormat("##.##")
    df.roundingMode = RoundingMode.CEILING

    val rates = remember {
        mutableStateOf(
            CurrenciesResponse(
                success = true,
                timestamp = 0,
                base = "",
                date = "",
                rates = Rates(
                    UAH = 0,
                    USD = 0,
                    EUR = 0
                )
            )
        )
    }

    val ratesArray = remember { mutableStateOf(emptyArray<Pair<String, Number>>()) }

    fun showMessageToUser(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun getRates() {
        val call = apiService.getMainCurrenciesRates()
        call.enqueue(object : Callback<CurrenciesResponse> {
            override fun onResponse(
                call: Call<CurrenciesResponse>,
                response: Response<CurrenciesResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        rates.value = responseBody
                        Log.d("debug", "Rates init: ${rates.value}")
                    }
                } else {
                    val jsonObject = JSONObject(response.errorBody()?.string())
                    val errorMessage = jsonObject.optString("message", "An error occurred")
                    showMessageToUser(errorMessage)
                    Log.d("debug", "Editing name failed: ${jsonObject}")
                }
            }

            override fun onFailure(call: Call<CurrenciesResponse>, t: Throwable) {
                showMessageToUser("Error: ${t.localizedMessage}")
            }
        })
    }

    val content = @Composable{
        if (token != null) {
            LaunchedEffect(Unit) {
                getRates()
            }

            LaunchedEffect(rates.value) {
                ratesArray.value = Rates::class.memberProperties.map { property ->
                    property.name to (property.get(rates.value.rates) as Number)
                }.toTypedArray()

                Log.d("debug", "ratesArray updated: ${ratesArray.value.contentToString()}")
            }

            Box() {

                val contentColor = MaterialTheme.colorScheme.onSecondary
                val contentColorConst = MaterialTheme.colorScheme.secondary

                Column(
                    modifier = Modifier.padding(top = 60.dp, start = 20.dp, end = 20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Text(
                        text = "Основні курси валют",
                        textAlign = TextAlign.Center,
                        fontSize = 32.sp,
                        modifier = Modifier
                            .padding(0.dp, 20.dp)
                            .fillMaxWidth(),
                        color = contentColor
                    )
                    Column() {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clip(shape = RoundedCornerShape(50))
                                .background(color = MaterialTheme.colorScheme.onPrimary),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                modifier = Modifier
                                    .weight(0.5f)
                                    .padding(horizontal = 20.dp, vertical = 20.dp),
                                text = "Валюта",
                                color = contentColorConst
                            )
                            Row(
                                modifier = Modifier
                                    .weight(0.5f)
                                    .padding(horizontal = 20.dp, vertical = 20.dp),
                            ) {
                                Text(
                                    modifier = Modifier.padding(end = 14.dp),
                                    text = "Купівля",
                                    color = contentColorConst
                                )
                                Text(text = "Продаж", color = contentColorConst)
                            }
                        }

                        Spacer(Modifier.size(20.dp))

                        Column {
                            ratesArray.value.forEach { currency ->
                                Row(
                                    modifier = infoRowModifier,
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .weight(0.5f)
                                            .padding(horizontal = 20.dp),
                                        text = currency.first,
                                        color = contentColor,
                                        fontSize = 26.sp
                                    )
                                    Row(
                                        modifier = Modifier
                                            .weight(0.5f)
                                            .padding(horizontal = 20.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            modifier = Modifier
                                                .weight(0.5f)
                                                .padding(end = 14.dp),
                                            text = "${df.format(currency.second)}",
                                            color = contentColor
                                        )
                                        Text(
                                            modifier = Modifier
                                                .weight(0.5f)
                                                .padding(end = 14.dp),
                                            text = ((currency.second as? Number)?.toInt()?.plus(0.43)?.toString() ?: "N/A"),
                                            color = contentColor
                                        )
                                    }
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(top = 14.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    thickness = 2.dp
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