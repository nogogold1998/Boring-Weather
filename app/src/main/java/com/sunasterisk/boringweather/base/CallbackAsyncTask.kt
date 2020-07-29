package com.sunasterisk.boringweather.base

import android.os.AsyncTask

class CallbackAsyncTask<T : Any, R : Any>(
    private val handler: (T) -> R,
    private val onFinishedListener: (Result<R>?) -> Unit
) : AsyncTask<T, Unit, Result<R>>() {

    override fun doInBackground(vararg params: T): Result<R>? {
        return try {
            params.getOrNull(0)?.let { Result.Success(handler(it)) }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun onPostExecute(result: Result<R>?) {
        onFinishedListener(result)
    }
}
