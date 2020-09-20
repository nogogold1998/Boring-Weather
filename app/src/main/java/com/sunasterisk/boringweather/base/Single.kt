package com.sunasterisk.boringweather.base

class Single<T : Any>(value: T? = null) {
    private var _value: T? = value

    var value: T?
        set(value) {
            _value = value
        }
        get() = _value.also { _value = null }

    fun peek() = _value
}
