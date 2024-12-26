package com.example.financeapp.ui.statistics_page

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.financeapp.models.responses.CurrentBalanceCategoriesResponse
import com.example.financeapp.models.responses.CurrentBalanceResponse
import com.example.financeapp.services.RetrofitClient
import com.example.financeapp.ui.Drawer
import com.example.financeapp.ui.theme.CustomCategoryCard
import com.example.financeapp.ui.theme.CustomChipSelector
import com.example.financeapp.ui.theme.MonthPicker
import com.example.financeapp.ui.theme.SingleChoiceSegmentedButton
import com.example.financeapp.ui.theme.YearPicker
import com.example.financeapp.viewmodel.UserViewModel
import com.example.pr4_calc.ui.dropdown.DropdownList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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



            Column(
                modifier = Modifier.padding(40.dp, 100.dp),
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
            }
        }
    }
//    Drawer(content)

    return content

}