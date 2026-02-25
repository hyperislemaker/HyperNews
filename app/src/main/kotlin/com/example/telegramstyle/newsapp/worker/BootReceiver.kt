package com.example.telegramstyle.newsapp.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint

/**
 * Broadcast receiver that handles device boot completion.
 * Reschedules background workers after device restart.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            // TODO: Reschedule background sync workers
        }
    }
}
