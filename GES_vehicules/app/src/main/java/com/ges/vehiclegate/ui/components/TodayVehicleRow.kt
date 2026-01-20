package com.ges.vehiclegate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ges.vehiclegate.domain.model.VehicleEntry
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun TodayVehicleRow(
    entry: VehicleEntry,
    onRestore: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val fmt = SimpleDateFormat("HH:mm:ss - dd/MM/yyyy", Locale.getDefault())
    val isOnSite = entry.exitAt == null
    val textColor = if (isOnSite) Color.Red else MaterialTheme.colorScheme.onSurface
    val cardColor = if (isOnSite) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
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
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = if (isOnSite) "SUR SITE" else "SORTI",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isOnSite) Color.Red else MaterialTheme.colorScheme.primary
                )
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
                        fontWeight = FontWeight.SemiBold,
                        color = textColor
                    )
                    Text(
                        text = "Chauffeur: ${entry.driverName}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                        color = textColor
                    )
                    if (!entry.driverPhone.isNullOrBlank()) {
                        Text(
                            text = "Tel: ${entry.driverPhone}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                            color = textColor
                        )
                    }
                    Text(
                        text = "Destination: ${entry.destination.label}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                        color = textColor
                    )
                    Text(
                        text = "Contact site: ${entry.siteContact}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                        color = textColor
                    )
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Entree: ${entry.entryGate.label}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                        color = textColor
                    )
                    if (!isOnSite) {
                        Text(
                            text = "Sortie: ${entry.exitGate.label}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                            color = textColor
                        )
                    }
                    Text(
                        text = "Arrivee: ${fmt.format(Date(entry.arrivalAt))}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                        color = textColor
                    )
                    if (!isOnSite && entry.exitAt != null) {
                        Text(
                            text = "Depart: ${fmt.format(Date(entry.exitAt))}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                            color = textColor
                        )
                        val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(entry.exitAt - entry.arrivalAt)
                        val hours = durationMinutes / 60
                        val minutes = durationMinutes % 60
                        val durationStr = if (hours > 0) "${hours}h ${minutes}min" else "${minutes}min"
                        Text(
                            text = "Duree: $durationStr",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                            color = textColor
                        )
                    }
                    if (!entry.notes.isNullOrBlank()) {
                        Text(
                            text = "Note: ${entry.notes}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                            color = textColor
                        )
                    }
                }
            }

            if (!isOnSite) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { onRestore(entry.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Retablir sur site")
                }
            }
        }
    }
}
