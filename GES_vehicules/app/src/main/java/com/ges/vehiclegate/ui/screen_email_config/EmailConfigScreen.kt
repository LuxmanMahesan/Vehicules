package com.ges.vehiclegate.ui.screen_email_config

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ges.vehiclegate.ui.components.InfoBanner
import com.ges.vehiclegate.util.email.EmailConfig
import com.ges.vehiclegate.util.email.GmailSender
import kotlinx.coroutines.launch
import java.io.File

private const val ACCESS_CODE = "4541"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailConfigScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isUnlocked by remember { mutableStateOf(false) }
    var enteredCode by remember { mutableStateOf("") }
    var codeError by remember { mutableStateOf(false) }

    var fromEmail by remember { mutableStateOf(EmailConfig.getFromEmail(context) ?: "") }
    var appPassword by remember { mutableStateOf(EmailConfig.getAppPassword(context) ?: "") }
    var isTesting by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                InfoBanner()
                TopAppBar(
                    title = { Text("Configuration Email") },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Text("\u2190") }
                    }
                )
            }
        }
    ) { padding ->
        if (!isUnlocked) {
            // Ecran de vérification du code
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Acc\u00e8s prot\u00e9g\u00e9",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Entrez le code d'acc\u00e8s pour modifier la configuration email",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = enteredCode,
                    onValueChange = {
                        if (it.length <= 4) {
                            enteredCode = it
                            codeError = false
                        }
                    },
                    label = { Text("Code d'acc\u00e8s") },
                    singleLine = true,
                    isError = codeError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.width(200.dp)
                )

                if (codeError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Code incorrect",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (enteredCode == ACCESS_CODE) {
                            isUnlocked = true
                        } else {
                            codeError = true
                            enteredCode = ""
                        }
                    },
                    enabled = enteredCode.length == 4,
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("Valider")
                }
            }
        } else {
            // Ecran de configuration (contenu original)
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Configuration requise pour l'envoi automatique",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Pour activer l'envoi automatique d'emails, vous devez configurer un compte Gmail avec un mot de passe d'application.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Etapes:",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "1. Allez dans votre compte Google\n2. S\u00e9curit\u00e9 > Validation en deux \u00e9tapes (activez-la si n\u00e9cessaire)\n3. Mots de passe des applications\n4. Cr\u00e9ez un mot de passe pour \"Mail\"\n5. Copiez le mot de passe g\u00e9n\u00e9r\u00e9 ici",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                OutlinedTextField(
                    value = fromEmail,
                    onValueChange = { fromEmail = it },
                    label = { Text("Adresse Gmail exp\u00e9diteur") },
                    placeholder = { Text("exemple@gmail.com") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = appPassword,
                    onValueChange = { appPassword = it },
                    label = { Text("Mot de passe d'application") },
                    placeholder = { Text("xxxx xxxx xxxx xxxx") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Email destinataire: ${EmailConfig.getToEmail(context)}",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (testResult != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (testResult!!.startsWith("\u2705"))
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = testResult!!,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                isTesting = true
                                testResult = null
                                try {
                                    val sender = GmailSender()
                                    val result = sender.sendEmail(
                                        toEmail = EmailConfig.getToEmail(context),
                                        subject = "Test - Configuration Email GES",
                                        body = "Ceci est un email de test. La configuration est correcte!",
                                        fromEmail = fromEmail,
                                        password = appPassword
                                    )
                                    testResult = if (result.isSuccess) {
                                        "\u2705 Email de test envoy\u00e9 avec succ\u00e8s!"
                                    } else {
                                        "\u274c Erreur: ${result.exceptionOrNull()?.message}"
                                    }
                                } catch (e: Exception) {
                                    testResult = "\u274c Erreur: ${e.message}"
                                } finally {
                                    isTesting = false
                                }
                            }
                        },
                        enabled = !isTesting && fromEmail.isNotBlank() && appPassword.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isTesting) "Test..." else "Tester")
                    }

                    Button(
                        onClick = {
                            isSaving = true
                            EmailConfig.saveEmailConfig(context, fromEmail, appPassword)
                            isSaving = false
                            onBack()
                        },
                        enabled = !isSaving && fromEmail.isNotBlank() && appPassword.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isSaving) "Enregistrement..." else "Enregistrer")
                    }
                }

                if (EmailConfig.isConfigured(context)) {
                    OutlinedButton(
                        onClick = {
                            EmailConfig.clearConfig(context)
                            fromEmail = ""
                            appPassword = ""
                            testResult = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Effacer la configuration")
                    }
                }
            }
        }
    }
}
