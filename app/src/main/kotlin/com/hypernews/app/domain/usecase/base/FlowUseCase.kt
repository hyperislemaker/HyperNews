package com.hypernews.app.domain.usecase.base

import kotlinx.coroutines.flow.Flow

/**
 * Base interface for use cases that return a Flow for reactive data streams.
 * @param P The type of input parameters
 * @param R The type of result emitted by the Flow
 */
interface FlowUseCase<in P, R> {
    operator fun invoke(params: P): Flow<R>
}

/**
 * Base interface for Flow use cases that don't require parameters.
 * @param R The type of result emitted by the Flow
 */
interface NoParamFlowUseCase<R> {
    operator fun invoke(): Flow<R>
}
