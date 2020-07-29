package com.sunasterisk.boringweather.util

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * try to get something in [tryBlock] if the value with given name is exists then it's returned, else
 * return null
 * @param tryBlock the block try to get a value
 * @return [R]? - if it exists else null (not found)
 * @throws JSONException if the exception is not [JSONException]: JSONObject[".."] not found.
 */
inline fun <R> JSONObject.tryOrNull(tryBlock: JSONObject.() -> R): R? {
    return try {
        this.tryBlock()
    } catch (e: JSONException) {
        if (e.message?.matches("""JSONObject\["[a-zA-Z0-9_]+"] not found\.""".toRegex()) == true) {
            null
        } else throw e
    }
}

/**
 * try to get something in [tryBlock] if the value with given name is exists then it's returned, else
 * return [default] value
 * @param default type [R]
 * @param tryBlock the block try to get a value
 * @return [R] - if it exists else [default]
 * @throws JSONException if the exception is not [JSONException]: JSONObject[".."] not found.
 */
inline fun <R> JSONObject.tryOrElse(default: R, tryBlock: JSONObject.() -> R): R =
    tryOrNull(tryBlock) ?: default

inline fun <R> JSONArray.map(transform: (JSONObject) -> R): List<R> = List(length()) { index ->
    with(get(index) as JSONObject) { transform(this) }
}


