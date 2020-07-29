package com.sunasterisk.boringweather.base

sealed class Result<out R : Any> {
    data class Success<out R : Any>(val data: R) : Result<R>()
    data class Error(val exception: Exception) : Result<Nothing>()
}
