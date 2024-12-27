package com.example.financeapp.ui.statistics_page


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.financeapp.models.responses.MonthStatisticsResponse
import com.example.financeapp.models.responses.CategoryStatistics
import com.example.financeapp.services.RetrofitClient
import com.example.financeapp.ui.theme.ChartModel
import com.example.financeapp.ui.theme.CircleStatistics
import com.example.financeapp.ui.theme.CustomChipSelector
import com.example.financeapp.ui.theme.CustomPercentBar
import com.example.financeapp.ui.theme.MonthPicker
import com.example.financeapp.ui.theme.SingleChoiceSegmentedButton
import com.example.financeapp.ui.theme.YearPicker
import com.example.financeapp.ui.theme.chartColor1
import com.example.financeapp.ui.theme.chartColor10
import com.example.financeapp.ui.theme.chartColor2
import com.example.financeapp.ui.theme.chartColor3
import com.example.financeapp.ui.theme.chartColor4
import com.example.financeapp.ui.theme.chartColor5
import com.example.financeapp.ui.theme.chartColor6
import com.example.financeapp.ui.theme.chartColor7
import com.example.financeapp.ui.theme.chartColor8
import com.example.financeapp.ui.theme.chartColor9
import com.example.financeapp.viewmodel.UserViewModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat


@Composable
fun StatisticsContent(
    userViewModel: UserViewModel
): @Composable () -> Unit {
    val context = LocalContext.current
    val token by userViewModel.token.observeAsState()
    val apiService = RetrofitClient.apiService

    val chartColors = listOf(chartColor1, chartColor2, chartColor3, chartColor4,
        chartColor5, chartColor6, chartColor7, chartColor8,
        chartColor9, chartColor10)

    // UNUSED
    // convert values to percents, also need summa
//    fun convertSummaToPercentage(
//        summa: Float,
//        values: List<Float>
//    ) :List<Float>{
//        // formula: Y% = (Xгрн / summa) * 100%
//        var persentages = mutableListOf<Float>()
//        values.forEach{ value ->
//            val val_to_perc = (value / summa) * 100.0f
//            persentages.add(val_to_perc)
//        }
//        return persentages
//    }

    fun getPercentageFromResponse(
        categories: List<CategoryStatistics>
    ) :List<Float>{
        var percentages = mutableListOf<Float>()
        categories.forEach{ item ->
            percentages.add(item.percentage.toFloat())
        }
        return percentages
    }

    // make data for chart statistics
    // values = percentage, calculated via convertSummaToPercentage()
    fun makeCharts(
        values: List<Float>
    ) :List<ChartModel>{
        var chartList = mutableListOf<ChartModel>()
        var colorInd = 0
        for (i in 0..(values.size - 1)) {
            if (i > (chartColors.size - 1)) {
                colorInd = 1
            }
            chartList.add(ChartModel(values[i], chartColors[colorInd]))
            colorInd++
        }
        return chartList
    }


    val monthList = listOf("січень", "лютий", "березень", "квітень", "травень",
        "червень", "липень", "серпень", "вересень", "жовтень", "листопад", "грудень")
    val yearList = listOf("2010", "2011", "2012", "2013", "2014", "2015", "2016",
        "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024")
    var monthPickerVisibility by remember { mutableStateOf(false) }
    var yearPickerVisibility by remember { mutableStateOf(false) }

    var selected_type by remember { mutableStateOf("") }
    val choises = listOf("Місяць", "Рік")
    var selected_range by remember { mutableStateOf(choises[0]) }
    var selectedMonthID by remember { mutableStateOf(11) }
    var selectedYearID by remember { mutableStateOf(yearList.size - 1) }

    val state = remember {
        mutableStateOf(
            MonthStatisticsResponse(
                currency = "EUR",
                resolvedMonth = 1,
                resolvedYear = 2024,
                total = 0.0,
                categories = listOf( CategoryStatistics(
                    title = "",
                    total = 0.0,
                    categoryId = "",
                    percentage = 0.0
                )),
                cashPercentage = 0.0,
                cardPercentage = 0.0
            )
        )
    }


    //UNUSED
    // example of using functions for conversion and make chart data:
    // var valList = listOf(550.0f, 190.0f, 140.0f, 69.0f, 51.0f)
    // var summa = 1000.0f
    // var percList = convertSummaToPercentage(summa, valList)
    // var chartsTemp = makeCharts(percList)

    fun showMessageToUser(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun getInitData() {
            val call = apiService.getMonthStatisticDefault("Bearer $token", type="expense")
            call.enqueue(object : Callback<MonthStatisticsResponse> {
                override fun onResponse(
                    call: Call<MonthStatisticsResponse>,
                    response: Response<MonthStatisticsResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { responseBody ->

                            // UNUSED
                            // summa = responseBody.total.toFloat()
                            // valList = responseBody.categories.map { it.total.toFloat() }
                            // percList = convertSummaToPercentage(summa, valList)
                            // chartsTemp = makeCharts(percList)

                            Log.d("debug", "Statistic init: ${responseBody}")
                            state.value = responseBody
                        }
                    } else {
                        val jsonObject = JSONObject(response.errorBody()?.string())
                        val errorMessage = jsonObject.optString("message", "An error occurred")
                        showMessageToUser(errorMessage)
                        Log.d("debug", "Statistic init failed: ${jsonObject}")
                    }
                }

                override fun onFailure(call: Call<MonthStatisticsResponse>, t: Throwable) {
                    showMessageToUser("Error: ${t.localizedMessage}")
                }
            })
    }


    fun getData(type: String, month: String, year: String){
        if(type == "Витрати") {
        } else if(type == "Дохід") {
            Log.d("debug", "Statistic init failed 1")
        }
    }

    val content = @Composable{
        if (token != null) {
            LaunchedEffect(Unit) {
                getInitData()
            }

            Box() {
                Column(
                    modifier = Modifier
                        .padding(start = 40.dp, end = 40.dp, top = 80.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        // selected_type = "Дохід" or "Витрати" - need to convert to income/expense
                        selected_type = CustomChipSelector(Modifier, "Дохід", "/", "Витрати")
                    }

                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        selected_range = choises[SingleChoiceSegmentedButton(
                            modifier = Modifier,
                            listOf("Місяць", "Рік")
                        )]
                        if (selected_range == "Місяць") {
                            TextButton(
                                modifier = Modifier.padding(top = 10.dp),
                                onClick = { monthPickerVisibility = true }
                            ) {
                                Text(
                                    "${monthList[selectedMonthID]} ${yearList[selectedYearID]}",
                                    fontSize = 26.sp
                                )
                            }
                            if (monthPickerVisibility) {
                                MonthPicker(
                                    visible = monthPickerVisibility,
                                    currentMonth = selectedMonthID,
                                    currentYear = selectedYearID,
                                    confirmButtonCLicked = { month_, year_ ->
                                        selectedMonthID = month_
                                        selectedYearID = year_
                                        Log.d("debug", "$month_/$year_")
                                        monthPickerVisibility = false
                                    },
                                    cancelClicked = {
                                        monthPickerVisibility = false
                                    }
                                )
                            }
                        } else {
                            TextButton(
                                modifier = Modifier.padding(top = 10.dp),
                                onClick = { yearPickerVisibility = true }
                            ) {
                                Text(
                                    "${yearList[selectedYearID]}",
                                    fontSize = 26.sp
                                )
                            }
                            if (yearPickerVisibility) {
                                YearPicker(
                                    visible = yearPickerVisibility,
                                    currentYear = selectedYearID,
                                    confirmButtonCLicked = { year_ ->
                                        selectedYearID = year_
                                        Log.d("debug", "$year_")
                                        yearPickerVisibility = false
                                    },
                                    cancelClicked = {
                                        yearPickerVisibility = false
                                    }
                                )
                            }
                        }

                    }

                    // main values used in chart statistics
                    val chartValues = getPercentageFromResponse(state.value.categories)
                    val chartData = makeCharts(chartValues)

                    // chart component
                    CircleStatistics(
                        modifier = Modifier.padding(top = 30.dp),
                        charts = chartData,
                        size = 200.dp,
                        strokeWidth = 54.dp
                    )

                    // legend - each category with percentage and actual value
                    // need to add categories, "item" is index
                    LazyColumn(
                        modifier = Modifier.padding(top = 40.dp).height(162.dp)
                    ) {
                        items(state.value.categories.size) { idx ->
                            val item = state.value.categories[idx]
                            ElevatedCard(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 6.dp
                                ),
                                modifier = Modifier
                                    .size(width = 350.dp, height = 57.dp)
                                    .padding(bottom = 10.dp)
                                    .clip(RoundedCornerShape(50)),
                                colors = CardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondary,
                                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    disabledContentColor = MaterialTheme.colorScheme.onSecondary
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.title,
                                        modifier = Modifier.weight(0.3f),
                                        textAlign = TextAlign.Center,
                                    )
                                    Text(
                                        text = "${item.percentage} %",
                                        modifier = Modifier.weight(0.3f),
                                        textAlign = TextAlign.Center,
                                        color = chartData[idx].color
                                    )
                                    Text (
                                        text = "${item.total} ${state.value.currency}",
                                        modifier = Modifier.weight(0.3f),
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.padding(top = 40.dp)
                    ) {
                        // 1 half (title1), other one calculated {100 - half1}
                        // if title1 = "Картка", then half1 is percentage value of it
                        CustomPercentBar(
                            modifier = Modifier,
                            title1 = "Картка",
                            title2 = "Готівка",
                            half1 = state.value.cardPercentage,
                            half2 = state.value.cashPercentage
                        )
                    }


                }
            }
            }
    }

    return content

}