package com.hypernews.app.worker.base

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Base class for background workers providing common functionality.
 */
abstract class BaseWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    /**
     * Override this to implement the actual work logic.
     */
    abstract suspend fun performWork(): Result

    override suspend fun doWork(): Result {
        return try {
            performWork()
        } catch (e: Exception) {
            handleError(e)
        }
    }

    /**
     * Handle errors during work execution.
     * Override to customize error handling behavior.
     */
    protected open fun handleError(exception: Exception): Result {
        return if (runAttemptCount < MAX_RETRY_COUNT) {
            Result.retry()
        } else {
            Result.failure()
        }
    }

    companion object {
        const val MAX_RETRY_COUNT = 3
    }
}
