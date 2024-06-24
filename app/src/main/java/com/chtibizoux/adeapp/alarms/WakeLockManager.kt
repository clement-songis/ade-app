package com.chtibizoux.adeapp.alarms

import android.content.Context
import android.os.PowerManager

class WakeLockManager(context: Context) {
    companion object {
        private const val TAG = "ADEApp:AlarmAlertWakeLock"
    }

    private val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private val wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)

    fun acquire() {
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/)
    }

    fun release() {
        if (!wakeLock.isHeld) {
            println("Wake lock already released")
            return
        }
        wakeLock.release()
    }
}