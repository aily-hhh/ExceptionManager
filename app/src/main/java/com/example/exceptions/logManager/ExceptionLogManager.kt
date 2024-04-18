package com.example.exceptions.logManager

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.Calendar

class ExceptionLogManager(context: Context) {

    private val baseExceptionHandler: Thread.UncaughtExceptionHandler =
        Thread.getDefaultUncaughtExceptionHandler() as Thread.UncaughtExceptionHandler
    private lateinit var lastException: Pair<Thread?, Throwable?>

    data class ExceptionData(
        val thread: String? = null,
        val throwable: String? = null,
        val timestamp: Long? = null,
    )

    var enabled = false
    var isShowLog = true

    private val gson = Gson()
    private val listType = object : TypeToken<List<ExceptionData>>() {}.type
    private val calendar = Calendar.getInstance()
    private val countDay = 7
    private val dayInMilliseconds = 86400000L

    private val fileManager = FileManager(context)
    private val currentFileName get() = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis.toString() + ".json"


    companion object {
        private val _exceptionHandler: MutableStateFlow<ExceptionData?> = MutableStateFlow(null)
        val exceptionHandler: StateFlow<ExceptionData?> get() = _exceptionHandler.asStateFlow()

        const val CUSTOM_INTENT_ACTION = "exceptions.logManager"
        const val EXCEPTION_KEY = "exceptionKey"
    }

    init {

        Thread.setDefaultUncaughtExceptionHandler { thread: Thread?, throwable: Throwable? ->
            throwable?.let { throwableObject ->
                if (enabled) {
                    val newExceptionData = ExceptionData(
                        thread.toString(),
                        throwable.toString(),
                        calendar.timeInMillis
                    )
                    val listData = convertDataToList(fileManager.readFromFile(currentFileName))
                    listData.add(newExceptionData)
                    val outString = gson.toJson(listData, listType)
                    fileManager.writeToFile(outString, currentFileName)
                    lastException = Pair(thread, throwable)
                    if (isShowLog) {
                        val intent = Intent(CUSTOM_INTENT_ACTION).apply {
                            putExtra(EXCEPTION_KEY, throwableObject.toString())
                        }
                        context.sendBroadcast(intent)
                        _exceptionHandler.value = newExceptionData
                    } else {
                        throwLastExceptionToDefaultHandler()
                    }
                }
            }
        }

        fileManager.getFileListFromDirectory().forEach { currentFile ->
            if (currentFile.toLong() / dayInMilliseconds > countDay) {
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