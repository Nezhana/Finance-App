package com.example.financeapp.ui.add_category_page

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.ui.theme.CustomTextField
import com.example.financeapp.ui.theme.CustomTextInknutAntiquaFont
import com.example.financeapp.viewmodel.UserViewModel


fun AddCategoryContent(
    userViewModel: UserViewModel,
    addRecordPage: () -> Unit
): @Composable () -> Unit {

    /* function for adding category */

    val content = @Composable {
        Box(modifier = Modifier
            .padding(top = 80.dp, start = 40.dp, end = 40.dp)
            .verticalScroll(rememberScrollState()))
        {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTextField(
                    "Назва категорії",
                    Modifier,
                    20.sp
                )
                Spacer(modifier = Modifier.height(40.dp))
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                        .border(2.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(25)),
                    border = ButtonDefaults.outlinedButtonBorder(false),
                    onClick = {
                        /* use function to add category*/
                        addRecordPage()
                    }
                ) {
                    CustomTextInknutAntiquaFont("Додати")
                }
            }
        }
    }

    return content
}