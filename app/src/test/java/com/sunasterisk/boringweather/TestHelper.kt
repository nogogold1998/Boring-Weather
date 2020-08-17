package com.sunasterisk.boringweather

object TestHelper {
    const val ONE_CALL_JSON_FILE_PATH = "onecall.json"

    fun readContentFromFilePath(filePath: String) =
        javaClass.classLoader!!.getResource(filePath).readText()
}
