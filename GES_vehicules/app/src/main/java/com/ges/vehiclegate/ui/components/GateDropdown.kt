package com.ges.vehiclegate.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ges.vehiclegate.domain.model.Gate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GateDropdown(
    value: Gate,
    onValueChange: (Gate) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = value.label,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Gate.entries.forEach { gate ->
                DropdownMenuItem(
                    text = { Text(gate.label) },
                    onClick = {
                        onValueChange(gate)
                        expanded = false
                    }
                )
            }
        }
    }
}
