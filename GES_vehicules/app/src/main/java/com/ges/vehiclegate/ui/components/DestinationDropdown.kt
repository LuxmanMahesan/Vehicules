package com.ges.vehiclegate.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ges.vehiclegate.domain.model.Destination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationDropdown(
    value: Destination,
    onValueChange: (Destination) -> Unit,
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
            label = { Text("Lieu / destination") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Destination.entries.forEach { dest ->
                DropdownMenuItem(
                    text = { Text(dest.label) },
                    onClick = {
                        onValueChange(dest)
                        expanded = false
                    }
                )
            }
        }
    }
}
