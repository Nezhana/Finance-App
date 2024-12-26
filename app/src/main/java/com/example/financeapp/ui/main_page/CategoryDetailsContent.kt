package com.example.financeapp.ui.main_page

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.models.responses.CurrentBalanceCategoriesResponse.Category
import com.example.financeapp.viewmodel.UserViewModel

fun CategoryDetailsContent(
    userViewModel: UserViewModel,
    categoryId: String = ""
): @Composable () -> Unit {

    val content = @Composable {

        val category = Category(
            title = "Розваги",
            total = -700.0,
            categoryId = "1"
        )

        val itemListTemp: List<TempRecord> = listOf(
            TempRecord(title = "Ігри", value = -200.0, date = "12/09", categoryId = category.categoryId),
            TempRecord(title = "Ігри", value = -300.0, date = "24/09", categoryId = category.categoryId),
            TempRecord(title = "Парк розваг", value = -100.0, date = "26/09", categoryId = category.categoryId),
            TempRecord(title = "", value = -50.0, date = "28/09", categoryId = category.categoryId),
            TempRecord(title = "", value = -50.0, date = "29/0", categoryId = category.categoryId),
        )

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
                            text = "Розваги",
                            modifier = Modifier.padding(start = 20.dp),
                            fontSize = 30.sp
                        )
                        Text(
                            text = "-700 ₴",
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
                        modifier = Modifier.fillMaxWidth().offset(y=10.dp).padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceAround,
//                        contentPadding = PaddingValues(top = 30.dp)
                    )
                    {
                        items(itemListTemp.size) { index ->
                            val item = itemListTemp[index]
                            val (date, value) = listOf(item.date, item.value)

                            Row(
                                modifier = Modifier.height(30.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(0.7f),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Text(item.title)
                                    Text(
                                        "($date)",
                                        modifier = Modifier.padding(start = 10.dp)
                                    )
                                }
                                Text(
                                    "$value ₴",
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

    return content
}

data class TempRecord(
    val title: String,
    val type: RecordType = RecordType.EXPENSE,
    val value: Double,
    val method: PaymentMethod = PaymentMethod.CASH,
    val date: String,
    val categoryId: String,
    val recurrent: Boolean = false,
    val repeating: RepeatingType? = null
)

enum class RecordType {
    INCOME, EXPENSE
}

enum class PaymentMethod {
    CASH, CARD
}

enum class RepeatingType {
    DAILY, WEEKLY, MONTHLY, YEARLY
}