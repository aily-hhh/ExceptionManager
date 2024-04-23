package com.example.exceptions.logManager

import android.content.Context
import android.util.Log
import com.example.exceptions.logException.DateTimeUtils.toDateTime
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.Locale

class ExceptionLogManager(context: Context) {

    private val baseExceptionHandler: Thread.UncaughtExceptionHandler =
        Thread.getDefaultUncaughtExceptionHandler() as Thread.UncaughtExceptionHandler
    private lateinit var lastException: Pair<Thread?, Throwable?>

    data class ExceptionData(
        val thread: String? = null,
        val throwable: String? = null,
        val timestamp: String? = null,
    )

    var enabled = false
    var isShowLog = true

    private val gson = Gson()
    private val listType = object : TypeToken<List<ExceptionData>>() {}.type
    private val countDay = 7
    private val dayInMilliseconds = 86400000L

    private val fileManager = FileManager(context)
    private val currentFileName
        get() = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis.toString() + ".json"


    companion object {
        private val _exceptionHandler: MutableStateFlow<ExceptionData?> = MutableStateFlow(null)
        val exceptionHandler: StateFlow<ExceptionData?> get() = _exceptionHandler.asStateFlow()
    }

    init {
        Thread.setDefaultUncaughtExceptionHandler { thread: Thread?, throwable: Throwable? ->
            throwable?.let { throwableObject ->
                if (enabled) {
                    val newExceptionData = ExceptionData(
                        thread.toString(),
                        throwable.toString(),
                        System.currentTimeMillis().toDateTime()
                    )
                    val listData = convertDataToList(fileManager.readFromFile(currentFileName))
                    listData.add(newExceptionData)
                    val outString = gson.toJson(listData, listType)
                    fileManager.writeToFile(outString, currentFileName)
                    lastException = Pair(thread, throwable)
                    if (isShowLog) {
                        _exceptionHandler.value = newExceptionData
                    } else {
                        throwLastExceptionToDefaultHandler()
                    }
                }
            }
        }

        fileManager.getFileListFromDirectory().forEach { currentFile ->
            if (currentFile.lowercase(Locale.getDefault()).endsWith(".json") &&
                    fileManager.getFileNameWithoutExtension(currentFile).toLong() / dayInMilliseconds > countDay * dayInMilliseconds) {
                val result = fileManager.getFileNameWithoutExtension(currentFile).toLong() / dayInMilliseconds
                Log.d("deleteFile", result.toString())
                fileManager.deleteLocalFile(currentFileName)
            }
        }
    }

    private fun throwLastExceptionToDefaultHandler() {
        if ((lastException.first != null) and (lastException.second != null))
            baseExceptionHandler.uncaughtException(lastException.first!!, lastException.second!!)
    }

    private fun convertDataToList(dataString: String?): MutableList<ExceptionData> {
        val list = mutableListOf<ExceptionData>()
        if (!dataString.isNullOrEmpty()) {
            list.addAll(gson.fromJson(dataString, listType))
        }
        return list
    }

}