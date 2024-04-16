package com.example.exceptions.logManager

import android.annotation.SuppressLint
import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar

class FileManager(
    private val context: Context,
    private val path: String? = null
) {

    private var currentFileName = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy")

    @SuppressLint("SimpleDateFormat")
    private val timeFormat = SimpleDateFormat("HH:mm")


    private val countDay = 7
    private val dayInMilliseconds = 86400000L

    private val sdCardPath by lazy { context.obbDir }
    private val directoryName by lazy { "${context.packageName}.LOG" }
    private var directoryFail = false

    fun cleanOldFiles() {
        if (sdCardPath != null) {
            getFileListFromDirectory().forEach { currentFile ->
                if (currentFile.toLong() / dayInMilliseconds > countDay) {
                    File(filePath() + "/$currentFile").delete()
                }
            }
        }
    }

    private fun filePath() : String {
        return if (path.isNullOrEmpty()) {
            "${sdCardPath}/$directoryName/"
        } else {
            "${path}/$directoryName/"
        }
    }

    private fun getFileListFromDirectory(): List<String> {
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

    fun readFromFile(file: String = "$currentFileName"): String {
        try {
            File(filePath()).apply {
                directoryFail = if (!exists()) !mkdir() else false
            }
        } catch (exception: java.lang.Exception) {
            directoryFail = true
        }
        if (directoryFail) return "Ошибка создания каталога"
        var returnString = ""
        File(filePath() + "/$file").apply {
            try {
                returnString = bufferedReader().use { it.readText(); }
            } catch (ex: Exception) {
                try {
                    createNewFile()
                    readFromFile()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return returnString
    }

    fun writeToFile(data: String) {
        val oldFileData = readFromFile()
        val dotString =
            "\n-----------------------------${timeFormat.format(Calendar.getInstance().time)}-----------------------------\n"
        File(filePath() + "$currentFileName").writeText(oldFileData + dotString + data)
    }
}