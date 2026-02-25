package com.hypernews.app.domain.usecase.base

/**
 * Base interface for use cases that execute synchronously.
 * @param P The type of input parameters
 * @param R The type of result
 */
interface UseCase<in P, out R> {
    operator fun invoke(params: P): R
}

/**
 * Base interface for use cases that don't require parameters.
 * @param R The type of result
 */
interface NoParamUseCase<out R> {
    operator fun invoke(): R
}
