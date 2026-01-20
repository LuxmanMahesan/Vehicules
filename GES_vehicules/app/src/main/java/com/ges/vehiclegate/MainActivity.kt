package com.ges.vehiclegate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ges.vehiclegate.domain.model.AgentManager
import com.ges.vehiclegate.service.ShiftScheduler
import com.ges.vehiclegate.ui.components.ShiftModal
import com.ges.vehiclegate.ui.navigation.AppNavGraph
import com.ges.vehiclegate.ui.navigation.Routes
import com.ges.vehiclegate.ui.theme.VehicleGateTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AgentManager.init(this)
        ShiftScheduler.scheduleNextShiftChange(this)

        setContent {
            VehicleGateTheme {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route

                var showShiftModal by remember { mutableStateOf(false) }
                var pendingShiftChange by remember { mutableStateOf(AgentManager.needsShiftChange()) }

                // Verifier si on est sur une page ou on ne doit pas interrompre
                val isOnAddScreen = currentRoute == Routes.ADD
                val isOnEditScreen = currentRoute?.startsWith("edit_vehicle/") == true

                // Afficher la popup seulement si:
                // - Un changement de shift est necessaire
                // - On n'est pas sur l'ecran d'ajout ou d'edition
                LaunchedEffect(pendingShiftChange, isOnAddScreen, isOnEditScreen) {
                    if (pendingShiftChange && !isOnAddScreen && !isOnEditScreen) {
                        showShiftModal = true
                    }
                }

                // Verifier periodiquement si un changement de shift est necessaire
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(30_000L)
                        if (AgentManager.needsShiftChange()) {
                            pendingShiftChange = true
                        }
                    }
                }

                // Verifier aussi a chaque changement de route
                LaunchedEffect(currentRoute) {
                    if (AgentManager.needsShiftChange()) {
                        pendingShiftChange = true
                    }
                }

                if (showShiftModal) {
                    ShiftModal(
                        onAgentConfirmed = { agentName ->
                            showShiftModal = false
                            pendingShiftChange = false
                        }
                    )
                }

                AppNavGraph(navController = navController)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ShiftScheduler.scheduleNextShiftChange(this)
    }
}
