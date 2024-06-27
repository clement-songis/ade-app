package com.chtibizoux.adeapp.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.ui.atLeast
import com.chtibizoux.adeapp.ui.clearFocusKeyboardAction
import com.chtibizoux.adeapp.ui.nextFocus
import com.chtibizoux.adeapp.ui.nextFocusKeyboardAction
import com.chtibizoux.adeapp.ui.submitKeyboardAction
import com.chtibizoux.adeapp.ui.submitOnEnter

data class FieldValueManager(val value: String, val update: (value: String) -> Unit, val isError: Boolean)

@Composable
fun AlarmsSettings(
    repeat: FieldValueManager,
    interval: FieldValueManager,
    timeUntilEvent: FieldValueManager,
    submit: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = repeat.value,
        onValueChange = repeat.update,
        modifier = Modifier
            .nextFocus()
            .fillMaxWidth(),
        label = { Text(stringResource(R.string.repeat_interval)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        keyboardActions = nextFocusKeyboardAction(),
        isError = repeat.isError,
        supportingText = {
            if (repeat.isError) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.number_error),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        singleLine = true
    )

    if (repeat.value.atLeast(1) > 1) {
        OutlinedTextField(
            value = interval.value,
            onValueChange = interval.update,
            modifier = Modifier
                .nextFocus()
                .fillMaxWidth(),
            label = { Text(stringResource(R.string.default_interval)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = nextFocusKeyboardAction(),
            isError = interval.isError,
            supportingText = {
                if (interval.isError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.number_error),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true
        )
    }

    val modifier = Modifier.fillMaxWidth()
    OutlinedTextField(
        value = timeUntilEvent.value,
        onValueChange = timeUntilEvent.update,
        modifier = if (submit == null) modifier.nextFocus() else modifier.submitOnEnter(submit),
        label = { Text(stringResource(R.string.default_alarm_interval)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = if (submit == null) ImeAction.Default else ImeAction.Done
        ),
        keyboardActions = if (submit == null) clearFocusKeyboardAction() else submitKeyboardAction(submit),
        isError = timeUntilEvent.isError,
        supportingText = {
            if (timeUntilEvent.isError) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.number_error),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        singleLine = true
    )
}