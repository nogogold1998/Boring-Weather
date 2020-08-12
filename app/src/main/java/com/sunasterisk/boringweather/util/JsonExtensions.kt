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
        when {
            e.message == null -> throw e
            e.message!!.matches("""JSONObject\["[a-zA-Z0-9_]+"] not found\.""".toRegex())
                || e.message!!.matches("""JSONObject\["[a-zA-Z0-9_]+"] is not an \w+\.""".toRegex())
                || e.message!!.matches("""No value for \w+""".toRegex()) -> null
            else -> throw e
        }
    }
}

inline fun <R> JSONArray.map(transform: (JSONObject) -> R?): List<R> =
    mutableListOf<R>().also { list ->
        repeat(length()) {
            (get(it) as? JSONObject)?.let(transform)?.let(list::add)
        }
    }

inline fun <reified R> JSONObject.getOrNull(name: String): R? = tryOrNull {
    when (R::class) {
        Int::class -> getInt(name)
        Byte::class -> getInt(name).toByte()
        Short::class -> getInt(name).toShort()
        Long::class -> getLong(name)
        String::class -> getString(name)
        Double::class -> getDouble(name)
        Float::class -> getDouble(name).toFloat()
        Boolean::class -> getBoolean(name)
        JSONObject::class -> getJSONObject(name)
        JSONArray::class -> getJSONArray(name)
        else -> get(name)
    } as? R
}

inline fun <reified R> JSONObject.getOrElse(name: String, default: R): R =
    tryOrNull { getOrNull(name) as? R } ?: default
