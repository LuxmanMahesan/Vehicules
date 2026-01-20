package com.ges.vehiclegate.ui.screen_home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ges.vehiclegate.di.AppModule
import com.ges.vehiclegate.domain.usecase.GetOnSiteVehiclesUseCase
import com.ges.vehiclegate.domain.usecase.MarkVehicleExitUseCase
import com.ges.vehiclegate.ui.components.InfoBanner
import com.ges.vehiclegate.ui.components.VehicleRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddVehicle: () -> Unit,
    onSeeToday: () -> Unit,
    onEditVehicle: (Long) -> Unit
) {
    val context = LocalContext.current
    val repo = remember { AppModule.provideVehicleRepository(context) }
    val dateTimeProvider = remember { AppModule.provideDateTimeProvider() }

    val viewModel = remember {
        HomeViewModel(
            getOnSiteVehicles = GetOnSiteVehiclesUseCase(repo),
            markVehicleExit = MarkVehicleExitUseCase(repo),
            dateTimeProvider = dateTimeProvider
        )
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Column {
                InfoBanner()
                TopAppBar(
                    title = {
                        Text(
                            text = "GES - Accueil Véhicules",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddVehicle,
                text = { Text("Ajouter") },
                icon = { Icon(Icons.Default.Add, contentDescription = "Ajouter un véhicule") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onSeeToday) { Text("Véhicules du shift") }
                Text(
                    text = "Sur site: ${uiState.vehiclesOnSite.size}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            if (uiState.error != null) {
                Spacer(Modifier.height(8.dp))
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else if (uiState.vehiclesOnSite.isEmpty()) {
                Text("Aucun véhicule sur site.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.vehiclesOnSite, key = { it.id }) { entry ->
                        VehicleRow(
                            entry = entry,
                            onMarkExit = { id, gate -> viewModel.markExit(id, gate) },
                            onEdit = { id -> onEditVehicle(id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
