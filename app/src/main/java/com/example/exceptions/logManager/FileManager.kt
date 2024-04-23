package com.example.exceptions.logManager

import android.content.Context
import java.io.File

class FileManager(
    private val context: Context,
    private val path: String? = null
) {

    private val sdCardPath by lazy { context.obbDir }
    private val directoryName by lazy { "${context.packageName}.LOG" }
    private var directoryFail = false

    private fun filePath(): String {
        return if (path.isNullOrEmpty()) {
            "${sdCardPath}/$directoryName/"
        } else {
            "${path}/$directoryName/"
        }
    }

    fun getFileListFromDirectory(): List<String> {
        val outList: MutableList<String> = mutableListOf()
        if (directoryFail) return outList
        val directory = File(filePath()).apply {
            directoryFail = if (!exists()) !mkdir() else false
        }.listFiles()?.toList()
        directory?.forEach { fileObj ->
            if (fileObj.isFile)
                outList += fileObj.name
        }
        return outList
    }

    fun readFromFile(file: String): String? {
        try {
            File(filePath()).apply {
                directoryFail = if (!exists()) !mkdir() else false
            }
        } catch (exception: java.lang.Exception) {
            directoryFail = true
        }
        if (directoryFail) return null
        var returnString = ""
        File(filePath() + "/$file").apply {
            try {
                returnString = bufferedReader().use { it.readText(); }
            } catch (ex: Exception) {
                try {
                    createNewFile()
                    readFromFile(file)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return returnString
    }

    fun writeToFile(data: String, fileName: String) {
        File(filePath() + fileName).writeText(data)
    }

    fun deleteLocalFile(fileName: String): Boolean {
        val file = File(filePath() + "/$fileName")
        return if (file.exists()) file.deleteRecursively() else true
    }

    fun getFileNameWithoutExtension(filePath: String): String {
        val dotIndex = filePath.lastIndexOf(".")
        if (dotIndex > 0) {
            return filePath.substring(1, dotIndex)
        }
        return filePath.substring(1)
    }

}