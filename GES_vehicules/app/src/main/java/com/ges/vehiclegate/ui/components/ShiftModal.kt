package com.ges.vehiclegate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ges.vehiclegate.domain.model.AgentManager
import com.ges.vehiclegate.domain.model.ShiftCalculator
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ShiftModal(
    onAgentConfirmed: (String) -> Unit
) {
    var agentName by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val now = LocalDateTime.now()
    val shiftNumber = ShiftCalculator.getCurrentShiftNumber(now)
    val reportDate = ShiftCalculator.getReportDateForShift(shiftNumber, now)
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Changement de Shift",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                HorizontalDivider()

                Text(
                    text = if (shiftNumber == 1) "Shift n°1 (07h - 19h)" else "Shift n°2 (19h - 07h)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Date du rapport: ${reportDate.format(dateFormatter)}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = AgentManager.SITE_NAME,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = agentName,
                    onValueChange = {
                        agentName = it
                        showError = false
                    },
                    label = { Text("Nom et prénom de l'agent") },
                    placeholder = { Text("Ex: Jean Dupont") },
                    singleLine = true,
                    isError = showError,
                    modifier = Modifier.fillMaxWidth()
                )

                if (showError) {
                    Text(
                        text = "Le nom de l'agent est obligatoire",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (agentName.trim().isBlank()) {
                            showError = true
                        } else {
                            val shiftStartMillis = ShiftCalculator.getShiftStartMillis(
                                shiftNumber,
                                reportDate,
                                ZoneId.systemDefault()
                            )
                            AgentManager.setAgent(
                                name = agentName.trim(),
                                shiftNumber = shiftNumber,
                                shiftStartMillis = shiftStartMillis
                            )
                            onAgentConfirmed(agentName.trim())
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirmer", fontSize = 16.sp)
                }
            }
        }
    }
}
