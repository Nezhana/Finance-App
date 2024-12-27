package com.example.financeapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.ui.Drawer
import com.example.financeapp.ui.account_page.AccountContent
import com.example.financeapp.ui.add_category_page.AddCategoryContent
import com.example.financeapp.ui.add_record_page.AddRecordContent
import com.example.financeapp.ui.course_page.CourseInfoContent
import com.example.financeapp.ui.group_page.GroupContent
import com.example.financeapp.ui.log_in_page.LogInScreen
import com.example.financeapp.ui.main_page.CategoryDetailsContent
import com.example.financeapp.ui.main_page.MainContent
import com.example.financeapp.ui.password_recovery_page.PasswordRecoveryScreen
import com.example.financeapp.ui.sign_in_page.SignInScreen
import com.example.financeapp.ui.statistics_page.StatisticsContent
import com.example.financeapp.ui.theme.FinanceAppTheme
import com.example.financeapp.viewmodel.UserViewModel

enum class Routes {
    REGISTRATION,
    AUTHORIZATION,
    PASSWORD_RECOVERY,
    MAIN_PAGE,
    ACCOUNT,
    GROUP,
    ADD_RECORD, // I
    ADD_CATEGORY,
    STATISTICS, // I
    COURSE,
}

@Composable
fun MainActivityContent() {
    val userViewModel: UserViewModel = viewModel()

    FinanceAppTheme {
        val navController = rememberNavController()

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.REGISTRATION.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = Routes.REGISTRATION.name) {
                    SignInScreen(
                        register = { navController.navigate(route = Routes.MAIN_PAGE.name) },
                        logInScreen = { navController.navigate(route = Routes.AUTHORIZATION.name) },
                        userViewModel = userViewModel
                    )
                }
                composable(route = Routes.AUTHORIZATION.name) {
                    LogInScreen(
                        authorizate = { navController.navigate(route = Routes.MAIN_PAGE.name )},
                        signInScreen = { navController.navigate(route = Routes.REGISTRATION.name )},
                        passwordRecoveryScreen = { navController.navigate(route = Routes.PASSWORD_RECOVERY.name )},
                        userViewModel = userViewModel
                    )
                }
                composable(route = Routes.PASSWORD_RECOVERY.name) {
                    PasswordRecoveryScreen(
                        redirect = { navController.navigate(route = Routes.MAIN_PAGE.name )},
                        logInScreen = { navController.navigate(route = Routes.AUTHORIZATION.name) },
                        signInScreen = { navController.navigate(route = Routes.REGISTRATION.name )},
                        userViewModel = userViewModel
                    )
                }
                composable(route = Routes.MAIN_PAGE.name) {
                    val content = MainContent(
                        userViewModel = userViewModel,
                        categoryDetailsPage = { categoryId ->
                            navController.navigate("category_details/$categoryId")
                        },
                        addRecordPage = { navController.navigate(route = Routes.ADD_RECORD.name )})
                    Drawer(content, navController)
                }
//                composable(route = Routes.CATEGORY_DETAILS.name) {
//                    val content = CategoryDetailsContent(userViewModel = userViewModel)
//                    Drawer(content, navController)
//                }
                composable(route = "category_details/{categoryId}") { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                    val content = CategoryDetailsContent(userViewModel = userViewModel, categoryId = categoryId)
                    Drawer(content, navController)
                }
                composable(route = Routes.ACCOUNT.name) {
                    val content = AccountContent(
                        logout = { navController.navigate(route = Routes.AUTHORIZATION.name )},
                        deleted = { navController.navigate(route = Routes.REGISTRATION.name )},
                        userViewModel = userViewModel
                    )
                    Drawer(content, navController)
                }
                composable(route = Routes.GROUP.name) {
                    val content = GroupContent(userViewModel = userViewModel)
                    Drawer(content, navController)
                }
                composable(route = Routes.ADD_RECORD.name) {
                    val content = AddRecordContent(
                        userViewModel = userViewModel,
                        mainPage = { navController.navigate(route = Routes.MAIN_PAGE.name )},
                        addCategoryPage = { navController.navigate(route = Routes.ADD_CATEGORY.name )}
                    )
                    Drawer(content, navController)
                }
                composable(route = Routes.COURSE.name) {
                    val content = CourseInfoContent(
                        userViewModel = userViewModel
                    )
                    Drawer(content, navController)
                }
                composable(route = Routes.ADD_CATEGORY.name) {
                    val content = AddCategoryContent(
                        userViewModel = userViewModel,
                        addRecordPage = { navController.navigate(route = Routes.ADD_RECORD.name )}
                    )
                    Drawer(content, navController)
                }
                composable(route = Routes.STATISTICS.name) {
                    val content = StatisticsContent(
                        userViewModel = userViewModel
                    )
                    Drawer(content, navController)
                }
            }
        }
    }
}
