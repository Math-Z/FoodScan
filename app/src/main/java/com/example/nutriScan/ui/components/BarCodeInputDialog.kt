package com.example.nutriScan.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun BarcodeInputDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val isValid = input.length in 8..14 && input.all { it.isDigit() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Saisir un code-barres") },
        text = {
            OutlinedTextField(
                value = input,
                onValueChange = { if (it.length <= 14) input = it },
                label = { Text("Code-barres (8 à 14 chiffres)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { if (isValid) onConfirm(input) }
                ),
                isError = input.isNotEmpty() && !isValid,
                supportingText = {
                    if (input.isNotEmpty() && !isValid) Text("Code invalide")
                },
                modifier = Modifier.focusRequester(focusRequester)
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(input) }, enabled = isValid) {
                Text("Rechercher")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}