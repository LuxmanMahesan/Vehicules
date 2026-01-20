package com.ges.vehiclegate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ges.vehiclegate.domain.model.Gate
import com.ges.vehiclegate.domain.model.VehicleEntry
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VehicleRow(
    entry: VehicleEntry,
    onMarkExit: (Long, Gate) -> Unit,
    onEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val fmt = rememberDateFormatter()
    var showExitDialog by remember { mutableStateOf(false) }
    var selectedGate by remember { mutableStateOf(entry.entryGate) }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.plate,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 26.sp),
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { showExitDialog = true }) {
                        Text("Sortie", fontSize = 16.sp)
                    }
                    OutlinedButton(onClick = { onEdit(entry.id) }) {
                        Text("Editer", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = entry.companyName,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 19.sp),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Chauffeur: ${entry.driverName}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp)
                    )
                    if (!entry.driverPhone.isNullOrBlank()) {
                        Text(
                            text = "Tel: ${entry.driverPhone}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp)
                        )
                    }
                    Text(
                        text = "Destination: ${entry.destination.label}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp)
                    )
                    Text(
                        text = "Contact site: ${entry.siteContact}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp)
                    )
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Entree: ${entry.entryGate.label}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp)
                    )
                    Text(
                        text = "Arrivee: ${fmt.format(Date(entry.arrivalAt))}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp)
                    )
                    if (!entry.notes.isNullOrBlank()) {
                        Text(
                            text = "Note: ${entry.notes}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp)
                        )
                    }
                }
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Sortie du vehicule") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Selectionnez la porte de sortie:")
                    Gate.entries.forEach { gate ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedGate == gate,
                                onClick = { selectedGate = gate }
                            )
                            Text(
                                text = gate.label,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        onMarkExit(entry.id, selectedGate)
                    }
                ) {
                    Text("Confirmer la sortie")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun rememberDateFormatter(): SimpleDateFormat {
    return SimpleDateFormat("HH:mm:ss - dd/MM/yyyy", Locale.getDefault())
}
