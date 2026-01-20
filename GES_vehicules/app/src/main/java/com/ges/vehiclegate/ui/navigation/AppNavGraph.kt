package com.ges.vehiclegate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ges.vehiclegate.ui.screen_add.AddVehicleScreen
import com.ges.vehiclegate.ui.screen_edit.EditVehicleScreen
import com.ges.vehiclegate.ui.screen_history.HistoryScreen
import com.ges.vehiclegate.ui.screen_home.HomeScreen
import com.ges.vehiclegate.ui.screen_today.TodayScreen
import com.ges.vehiclegate.ui.screen_email_config.EmailConfigScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onAddVehicle = { navController.navigate(Routes.ADD) },
                onSeeToday = { navController.navigate(Routes.TODAY) },
                onEditVehicle = { id -> navController.navigate(Routes.editRoute(id)) }
            )
        }

        composable(Routes.ADD) {
            AddVehicleScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.TODAY) {
            TodayScreen(
                onBack = { navController.popBackStack() },
                navController = navController
            )
        }

        composable(Routes.EDIT_WITH_ARG) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: 0L
            EditVehicleScreen(
                vehicleId = id,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                onBack = { navController.popBackStack() },
                onConfigEmail = { navController.navigate(Routes.EMAIL_CONFIG) }
            )
        }

        composable(Routes.EMAIL_CONFIG) {
            EmailConfigScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
