package com.sunasterisk.boringweather

object TestHelper {
    const val ONE_CALL_JSON_FILE_PATH = "onecall.json"

    const val ONE_CALL_2_JSON_FILE_PATH = "onecall2.json"

    fun readContentFromFilePath(filePath: String) =
        javaClass.classLoader!!.getResource(filePath).readText()
}
