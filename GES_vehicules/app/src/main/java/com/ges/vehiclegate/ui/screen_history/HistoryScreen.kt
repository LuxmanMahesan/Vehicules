package com.ges.vehiclegate.ui.screen_history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ges.vehiclegate.ui.components.InfoBanner
import com.ges.vehiclegate.util.pdf.PdfOpener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onConfigEmail: () -> Unit
) {
    val context = LocalContext.current
    val dir = context.getExternalFilesDir(null)

    val fichiers = remember {
        val allPdfs = dir?.listFiles()
            ?.filter { it.extension == "pdf" }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()

        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)

        allPdfs.filter { file ->
            if (file.lastModified() < sevenDaysAgo) {
                file.delete()
                false
            } else {
                true
            }
        }.take(14)
    }

    Scaffold(
        topBar = {
            Column {
                InfoBanner()
                TopAppBar(
                    title = { Text("Historique PDF") },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Text("←") }
                    },
                    actions = {
                        TextButton(onClick = onConfigEmail) {
                            Text("⚙️ Email")
                        }
                    }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding)
        ) {
            items(fichiers) { file ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable {
                            PdfOpener.ouvrir(context, file)
                        }
                ) {
                    Text(
                        file.name,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
