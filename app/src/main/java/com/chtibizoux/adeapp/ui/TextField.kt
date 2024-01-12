package com.chtibizoux.adeapp.ui

import android.view.KeyEvent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalFocusManager

fun Modifier.nextFocus() = composed {
    val focusManager = LocalFocusManager.current
    this.onPreviewKeyEvent {
        if (it.key == Key.Tab && it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
            focusManager.moveFocus(FocusDirection.Down)
            true
        } else {
            false
        }
    }
}

fun Modifier.submitOnEnter(onEnter: () -> Unit): Modifier {
    return this.onPreviewKeyEvent {
        if (it.key == Key.Enter) {
            onEnter()
            true
        } else {
            false
        }
    }
}

@Composable
fun nextFocusKeyboardAction(): KeyboardActions {
    val focusManager = LocalFocusManager.current
    return KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
}

@Composable
fun clearFocusKeyboardAction(): KeyboardActions {
    val focusManager = LocalFocusManager.current
    return KeyboardActions(onDone = { focusManager.clearFocus() })
}

fun submitKeyboardAction(onDone: () -> Unit): KeyboardActions {
    return KeyboardActions(onDone = { onDone() })
}
