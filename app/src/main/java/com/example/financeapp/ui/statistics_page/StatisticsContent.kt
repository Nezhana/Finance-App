package com.example.financeapp.ui.statistics_page

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.R
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
import com.example.pr4_calc.ui.dropdown.DropdownList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat


fun StatisticsContent(
    userViewModel: UserViewModel
): @Composable () -> Unit {

    var content = @Composable{

        Box() {

            var selected_type by remember { mutableStateOf("") }

            val choises = listOf("Місяць", "Рік")
            var selected_range by remember { mutableStateOf(choises[0]) }

            val monthList = listOf("січень", "лютий", "березень", "квітень", "травень",
                "червень", "липень", "серпень", "вересень", "жовтень", "листопад", "грудень")

            val yearList = listOf("2010", "2011", "2012", "2013", "2014", "2015", "2016",
                "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024")

            var selectedMonthID by remember { mutableStateOf(0) }
            var selectedYearID by remember { mutableStateOf(yearList.size - 2) }

            var monthPickerVisibility by remember { mutableStateOf(false) }
            var yearPickerVisibility by remember { mutableStateOf(false) }

            val chartColors = listOf(chartColor1, chartColor2, chartColor3, chartColor4,
                                     chartColor5, chartColor6, chartColor7, chartColor8,
                                     chartColor9, chartColor10)

            // for testing color index loop:
//            val chartColors = listOf(chartColor1, chartColor2, chartColor3, chartColor4)

            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING


            // convert values to percents, also need summa
            fun convertSummaToPercentage(
                summa: Float,
                values: List<Float>
            ) :List<Float>{
                // formula: Y% = (Xгрн / summa) * 100%
                var persentages = mutableListOf<Float>()
                values.forEach{ value ->
                    val val_to_perc = (value / summa) * 100.0f
                    persentages.add(val_to_perc)
                }
                return persentages
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


                // example of using functions for conversion and make chart data:
                val valList = listOf(550.0f, 190.0f, 140.0f, 69.0f, 51.0f)
                val summa = 1000.0f
                val percList = convertSummaToPercentage(summa, valList)
                Log.d("debug", "Percentage List: $percList")
                val chartsTemp = makeCharts(percList)

                // chart component
                CircleStatistics(
                    modifier = Modifier.padding(top = 30.dp),
                    charts = chartsTemp,
                    size = 200.dp,
                    strokeWidth = 54.dp
                )

                // legend - each category with percentage and actual value
                // need to add categories, "item" is index
                LazyColumn(
                    modifier = Modifier.padding(top = 40.dp).height(162.dp)
                ) {
                    items(valList.size) { item ->
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
                                    text = "category",
                                    modifier = Modifier.weight(0.3f),
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    text = "${df.format(chartsTemp[item].value)} %",
                                    modifier = Modifier.weight(0.3f),
                                    textAlign = TextAlign.Center,
                                    color = chartsTemp[item].color
                                )
                                Text (
                                    text = "${valList[item]} ₴",
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
                    val halfDividerTemp = 48.62f
                    // if title1 = "Картка", then half1 is percentage value of it
                    CustomPercentBar(
                        modifier = Modifier,
                        title1 = "Картка",
                        title2 = "Готівка",
                        half1 = halfDividerTemp,
                        df = df
                    )
                }


            }
        }
    }

    return content

}

data class ChartModel(
    val value: Float,
    val color: Color,
)