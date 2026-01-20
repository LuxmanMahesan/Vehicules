package com.ges.vehiclegate.ui.screen_today

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ges.vehiclegate.di.AppModule
import com.ges.vehiclegate.domain.usecase.GetShiftVehiclesUseCase
import com.ges.vehiclegate.domain.usecase.RestoreVehicleOnSiteUseCase
import com.ges.vehiclegate.ui.components.InfoBanner
import com.ges.vehiclegate.ui.components.TodayVehicleRow
import com.ges.vehiclegate.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    onBack: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val repo = remember { AppModule.provideVehicleRepository(context) }

    val viewModel = remember {
        TodayViewModel(
            getShiftVehicles = GetShiftVehiclesUseCase(repo),
            restoreVehicleOnSite = RestoreVehicleOnSiteUseCase(repo)
        )
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Column {
                InfoBanner()
                TopAppBar(
                    title = { Text("Véhicules du Shift") },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Text("←") }
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp)
                .fillMaxSize()
        ) {

            OutlinedButton(
                onClick = { navController.navigate(Routes.HISTORY) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Historique PDF")
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Total: ${uiState.vehicles.size}",
                style = MaterialTheme.typography.titleMedium
            )

            if (uiState.error != null) {
                Spacer(Modifier.height(8.dp))
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else if (uiState.vehicles.isEmpty()) {
                Text("Aucun véhicule pour ce shift.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.vehicles, key = { it.id }) { entry ->
                        TodayVehicleRow(
                            entry = entry,
                            onRestore = { id -> viewModel.restore(id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
