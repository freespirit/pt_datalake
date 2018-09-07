package com.phototagger.datalake.domain

interface CompletableUseCase<I, O> {
    fun execute(input: I, onSuccess: (output: O) -> Unit, onError: (error: Exception) -> Unit)
}