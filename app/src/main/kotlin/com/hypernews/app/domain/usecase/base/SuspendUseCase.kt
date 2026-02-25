package com.hypernews.app.domain.usecase.base

/**
 * Base interface for use cases that execute asynchronously with suspend functions.
 * @param P The type of input parameters
 * @param R The type of result
 */
interface SuspendUseCase<in P, R> {
    suspend operator fun invoke(params: P): R
}

/**
 * Base interface for suspend use cases that don't require parameters.
 * @param R The type of result
 */
interface NoParamSuspendUseCase<R> {
    suspend operator fun invoke(): R
}
