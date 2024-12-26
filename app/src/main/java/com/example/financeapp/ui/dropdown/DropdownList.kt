package com.example.pr4_calc.ui.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.financeapp.R

@Composable
fun DropdownList(
    itemList: List<String>,
    selectedIndex: Int,
    modifier: Modifier,
    onItemClick: (Int) -> Unit
) {

    var showDropdown by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val (inactiveColor, activeColor) = listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary)
    var isActive by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        // button
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
                .clickable {
                    showDropdown = true
                    isActive = true
                }
                .height(50.dp)
                .drawBehind {

                    val strokeWidth = 2.0f
                    val y = size.height - strokeWidth / 2

                    drawLine(
                        if(isActive){activeColor}else{inactiveColor},
                        Offset(0f, y),
                        Offset(size.width, y),
                        strokeWidth
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Row (
                modifier = Modifier.padding(start = 12.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = itemList[selectedIndex],
                    modifier = Modifier.padding(3.dp),
                    color = if(isActive){activeColor}else{inactiveColor}
                )
                Icon(
                    painter = painterResource(R.drawable.downarrow),
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier.padding(top = 10.dp),
                    tint = if(isActive){activeColor}else{inactiveColor}
                )
            }

        }

        // dropdown list
        Box {
            if (showDropdown) {
                Popup(
                    alignment = Alignment.TopCenter,
                    properties = PopupProperties(
                        excludeFromSystemGesture = true,
                    ),
                    // to dismiss on click outside
                    onDismissRequest = {
                        showDropdown = false
                        isActive = false
                    }
                ) {

                    Column(
                        modifier = modifier
                            .heightIn(max = 90.dp)
                            .verticalScroll(state = scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        itemList.onEachIndexed { index, item ->
                            if (index != 0) {
                                HorizontalDivider(
                                    thickness = 1.dp, color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.background)
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .clickable {
                                        onItemClick(index)
                                        showDropdown = !showDropdown
                                        isActive = !isActive
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                    }
                }
            }
        }
    }

}