package com.example.financeapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.Routes
import com.example.financeapp.ui.account_page.AccountScreen
import com.example.financeapp.ui.log_in_page.LogInScreen
import com.example.financeapp.ui.main_page.MainScreen
import com.example.financeapp.ui.sign_in_page.SignInScreen
import com.example.financeapp.ui.theme.CustomTextInknutAntiquaFont
import com.example.financeapp.ui.theme.onBackgroundLight
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Drawer(
    content: @Composable () -> Unit,
    navController: NavHostController
) {

//    val items = listOf("Акаунт", "Баланс", "Додати запис", "Статистика", "Курси валют", "Група")
    val items = listOf(
        MenuRoutes(name = "Акаунт", route = { navController.navigate(route = Routes.ACCOUNT.name) }),
        MenuRoutes(name = "Баланс", route = { navController.navigate(route = Routes.MAIN_PAGE.name) }),
        MenuRoutes(name = "Додати запис", route = { navController.navigate(route = Routes.ADD_RECORD.name) }),
        MenuRoutes(name = "Додати категорію", route = { navController.navigate(route = Routes.ADD_CATEGORY.name) }),
        MenuRoutes(name = "Статистика", route = { navController.navigate(route = Routes.STATISTICS.name) }),
        MenuRoutes(name = "Курси валют", route = { navController.navigate(route = Routes.COURSE.name) }),
        MenuRoutes(name = "Група", route = { navController.navigate(route = Routes.GROUP.name) }),)
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val selectedItem = remember { mutableStateOf(items[1]) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet (
                drawerContainerColor = MaterialTheme.colorScheme.primaryContainer,
                drawerContentColor = MaterialTheme.colorScheme.onSecondary,
            ) {
                IconButton(
                    onClick = { scope.launch {
                        drawerState.close()
                    } })
                {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Menu Button",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
//                Text(
//                    text = "Username",
//                    modifier = Modifier.padding(16.dp),
//                    color = MaterialTheme.colorScheme.primary
//                )
//                HorizontalDivider(
//                    thickness = 2.dp,
//                    color = MaterialTheme.colorScheme.onPrimary
//                )
                Spacer(Modifier.height(40.dp))
                items.forEach { item ->
                    NavigationDrawerItem(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        label = { Text(
                            text = item.name,
                            color = MaterialTheme.colorScheme.onSecondary
                        ) },
                        selected = selectedItem.value == item,
                        onClick = {
                            scope.launch {
                                selectedItem.value = item
                                drawerState.close()
                                item.route()
                            }
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                // ...other drawer items
            }
        },
        gesturesEnabled = false
    )
    {
        TopAppBar(
            modifier = Modifier.clip(
                RoundedCornerShape(
                    bottomStartPercent = 50,
                    bottomEndPercent = 50
                )
            ),
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.primary,
                scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                CustomTextInknutAntiquaFont(
                    text = "Finance",
                    modifier = Modifier
                        .padding(start = 180.dp)
                        .offset(y = 0.dp),
//                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis

                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .offset(y = 0.dp)
                )
                {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu Button"
                    )
                }
            },
            scrollBehavior = scrollBehavior,
        )
        content()
    }
}

data class MenuRoutes(
    val name: String,
    val route: () -> Unit
)