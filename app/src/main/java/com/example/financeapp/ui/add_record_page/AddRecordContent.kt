package com.example.financeapp.ui.add_record_page


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.models.interfaces.PaymentMethod
import com.example.financeapp.models.interfaces.Record
import com.example.financeapp.models.interfaces.RecordType
import com.example.financeapp.models.interfaces.RepeatingType
import com.example.financeapp.models.requests.RecordRequest
import com.example.financeapp.models.responses.AddRecordResponse
import com.example.financeapp.models.responses.CurrentBalanceCategoriesResponse
import com.example.financeapp.models.responses.MessageResponse
import com.example.financeapp.models.responses.UserDataResponse
import com.example.financeapp.services.RetrofitClient
import com.example.financeapp.ui.dropdown.DropdownList
import com.example.financeapp.ui.theme.CustomChipSelector
import com.example.financeapp.ui.theme.CustomTextField
import com.example.financeapp.ui.theme.CustomTextInknutAntiquaFont
import com.example.financeapp.ui.theme.DatePickerFieldToModal
import com.example.financeapp.viewmodel.UserViewModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException


@Composable
fun AddRecordContent(
    userViewModel: UserViewModel,
    mainPage: () -> Unit,
    addCategoryPage: () -> Unit
): @Composable () -> Unit {

    val context = LocalContext.current
    val token by userViewModel.token.observeAsState()
    val apiService = RetrofitClient.apiService

    val activeTextColor = Color(0xFFFFFFFF)
    val inactiveTextColor = Color(0xFF222831)

    val radioOptions = listOf("Картка", "Готівка")


    val currency = remember { mutableStateOf("UAH") }
    val categories = remember { mutableStateListOf<String>() }
    var categoriesWithIdSet: Set<Pair<String, String>> = emptySet()
    categories.add("+ Додати категорію")

    fun showMessageToUser(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun getCurrency() {
        val call = apiService.getUserData("Bearer $token")
        call.enqueue(object : Callback<UserDataResponse> {
            override fun onResponse(
                call: Call<UserDataResponse>,
                response: Response<UserDataResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        currency.value = responseBody.user.currency
                        Log.d("debug", "Currency init: ${currency.value}")
                    }
                } else {
                    val jsonObject = JSONObject(response.errorBody()?.string())
                    val errorMessage = jsonObject.optString("message", "An error occurred")
                    showMessageToUser(errorMessage)
                    Log.d("debug", "Editing name failed: ${jsonObject}")
                }
            }

            override fun onFailure(call: Call<UserDataResponse>, t: Throwable) {
                showMessageToUser("Error: ${t.localizedMessage}")
            }
        })
    }

    fun getCategories() {
        val call = apiService.getCurrentBalanceCategories("Bearer $token")
        call.enqueue(object : Callback<CurrentBalanceCategoriesResponse> {
            override fun onResponse(
                call: Call<CurrentBalanceCategoriesResponse>,
                response: Response<CurrentBalanceCategoriesResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        Log.d("debug", "Categories API Response: $responseBody")
                        responseBody.categories.forEach { category ->
                            categories.add("${category.title}")
                        }
                        categoriesWithIdSet = responseBody.categories.map { category ->
                            category.title to category.categoryId
                        }.toSet()
                    }
                } else {
                    val jsonObject = JSONObject(response.errorBody()?.string())
                    val errorMessage = jsonObject.optString("message", "An error occurred")
                    showMessageToUser(errorMessage)
                    Log.d("debug", "Adding record failed: ${errorMessage}")
                }
            }

            override fun onFailure(call: Call<CurrentBalanceCategoriesResponse>, t: Throwable) {
                showMessageToUser("Error: ${t.localizedMessage}")
            }
        })
    }

    var tempVal by remember { mutableStateOf("") }

    var selectedType by remember { mutableStateOf(RecordType.EXPENSE) } // Дохід/Витрата
    var summa by remember { mutableStateOf(0.0) } // Введене значення
    var selectedMethod by remember { mutableStateOf(PaymentMethod.CASH) } // Картка/Готівка
    var recordName by remember { mutableStateOf("") } // Назва запису
    var category by remember { mutableStateOf(categories[0]) }
    var date by remember { mutableStateOf("") }
    var repeating by remember { mutableStateOf(false) }
    var repeatingRange by remember { mutableStateOf(RepeatingType.DAILY) }


    fun addRecord() {

        val typeString = selectedType.name.lowercase()
        val methodString = selectedMethod.name.lowercase()
        val repeatingString = repeatingRange.name.lowercase()
        val transformedDate = date.split("/").let { parts ->
            "${parts[1]}.${parts[0]}.${parts[2]}" // Rearrange as MM.DD.YYYY
        }
        val categoryId = categoriesWithIdSet.find { it.first == category }?.second
            ?: throw IllegalArgumentException("Category ID not found for title: $category")

        val request = RecordRequest(
            title = recordName,
            type = typeString,
            value = summa,
            method = methodString,
            date = transformedDate,
            categoryId = categoryId,
            recurrent = repeating,
            repeating = repeatingString
        )

        apiService.addRecord("Bearer $token", request).enqueue(object : retrofit2.Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                if (response.isSuccessful) {
                    showMessageToUser("Record added successfully")
                    mainPage()
                } else {
                    val jsonObject = JSONObject(response.errorBody()?.string())
                    val errorMessage = jsonObject.optString("message", "An error occurred")
                    showMessageToUser(errorMessage)
                    Log.d("debug", "Category adding failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
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

    var content = @Composable{
        if (token != null) {
            DisposableEffect(Unit) {
                getCurrency()
                getCategories()
                onDispose {}
            }

            Box(
                modifier = Modifier
                    .padding(top = 80.dp, start = 40.dp, end = 40.dp)
                    .verticalScroll(rememberScrollState())
            )
            {
                Column {
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {

                            var selected by remember { mutableStateOf("") }
                            // selected = "Дохід" or "Витрати" - need to convert to income/expense
                            selected = CustomChipSelector(Modifier, "Дохід", "/", "Витрати")
                            selectedType = if(selected == "Дохід") {RecordType.INCOME} else {RecordType.EXPENSE}

                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {

                            tempVal = CustomTextField("", Modifier.width(100.dp), fontSize = 30.sp)
                            Text(
                                text = currency.value,
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 30.sp
                            )
                        }
                    }
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Text(
                            "Тип",
                            modifier = Modifier.padding(top = 30.dp, bottom = 20.dp),
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Row {
                            val (selectedOption, onOptionSelected) = remember {
                                mutableStateOf(
                                    radioOptions[0]
                                )
                            }
                            radioOptions.forEach { text ->
                                Row(
                                    Modifier
                                        .width(140.dp)
                                        .selectable(
                                            selected = (text == selectedOption),
                                            onClick = { onOptionSelected(text) },
                                            role = Role.RadioButton
                                        )
                                        .padding(horizontal = 16.dp),
//                            verticalAlignment = Alignment.Start
                                ) {
                                    RadioButton(
                                        selected = (text == selectedOption),
                                        onClick = null, // null recommended for accessibility with screen readers
                                        colors = RadioButtonColors(
                                            selectedColor = MaterialTheme.colorScheme.onSecondary,
                                            unselectedColor = MaterialTheme.colorScheme.primaryContainer,
                                            disabledSelectedColor = MaterialTheme.colorScheme.onSecondary,
                                            disabledUnselectedColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    )
                                    Text(
                                        text = text,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(start = 16.dp),
                                        color = if (text == selectedOption) {
                                            MaterialTheme.colorScheme.onSecondary
                                        } else {
                                            MaterialTheme.colorScheme.primaryContainer
                                        }
                                    )
                                }
                            }
                            selectedMethod = if(selectedOption == "Картка") {PaymentMethod.CARD} else {PaymentMethod.CASH}
                        }
                    }
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Text(
                            "Назва запису",
                            modifier = Modifier.padding(top = 30.dp, bottom = 10.dp),
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        recordName = CustomTextField("", Modifier.padding(0.dp))
                    }
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Text(
                            "Категорії",
                            modifier = Modifier.padding(top = 30.dp, bottom = 20.dp),
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        var selectedIndexDrop by rememberSaveable { mutableStateOf(0) }

//                        val itemListTemp: List<CategoriesResponse.CategoryItem> = categories

//                        val categoryList = categories.map { it.title } + "+ Додати категорію"
                        Log.d("debug", "LIST: $categories")
//                        var ind = 0
//                        while (ind < itemListTemp.size) {
//                            categoryList += itemListTemp[ind].title
//                            ind += 1
//                        }

                        DropdownList(
                            itemList = categories,
                            selectedIndex = selectedIndexDrop,
                            modifier = Modifier,
                            onItemClick = { index ->
                                selectedIndexDrop = index
                                if (categories[selectedIndexDrop] == "+ Додати категорію") {
                                    Log.d("debug", "add category clicked")
                                    addCategoryPage()
                                }
                            }
                        )
                        category = categories[selectedIndexDrop]

//                    CustomCategoryPicker()
                    }
                    Box(
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .fillMaxWidth()
                    ) {
                        date = DatePickerFieldToModal()
                    }

                    Box(
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .fillMaxWidth()
                    ) {
                        var checked by remember { mutableStateOf(false) }
                        var repeatingTypeUA by remember { mutableStateOf("") }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { checked = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary,
                                    uncheckedColor = MaterialTheme.colorScheme.secondary
                                )
                            )
                            Text(
                                "Повторення",
                                color = if (checked) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.secondary
                                }
                            )
                        }

                        if (checked) {
                            val listRepeation = listOf(
                                "Щодня",
                                "Щотижня",
                                "Щомісяця",
                                "Щорічно"
                            )
                            var selectedIndexDrop by remember { mutableStateOf(0) }

                            Column(
                                modifier = Modifier.padding(top = 50.dp)
                            ) {
                                DropdownList(
                                    itemList = listRepeation,
                                    selectedIndex = selectedIndexDrop,
                                    modifier = Modifier,
                                    onItemClick = {
                                        selectedIndexDrop = it
                                        repeatingTypeUA = listRepeation[selectedIndexDrop]
                                    }
                                )
                                CustomTextField("Кількість повторів", Modifier)
                            }

                        } else {
                            Log.d("debug", "Repeating is not checked")
                        }
                        repeating = checked
                        when(repeatingTypeUA){
                            "Щодня" -> repeatingRange = RepeatingType.DAILY
                            "Щотижня" -> repeatingRange = RepeatingType.WEEKLY
                            "Щомісяця" -> repeatingRange = RepeatingType.MONTHLY
                            "Щорічно" -> repeatingRange = RepeatingType.YEARLY
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .fillMaxWidth()
                    ) {
                        OutlinedButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .border(
                                    2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(25)
                                ),
                            border = ButtonDefaults.outlinedButtonBorder(false),
                            onClick = {
                                summa = tempVal.toDouble()
                                Log.d(
                                    "debug",
                                    "Output: $selectedType, $summa, " +
                                            "$selectedMethod, $recordName, " +
                                            "$category, $date, " +
                                            "$repeating, $repeatingRange"
                                )
                                addRecord()
                            }
                        ) {
                            CustomTextInknutAntiquaFont("Додати")
                        }
                    }
                }
            }
        }
    }

    return content

}

