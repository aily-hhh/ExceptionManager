package com.example.exceptions.logManager

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExceptionLogManager(context: Context) {

    private val baseExceptionHandler: Thread.UncaughtExceptionHandler =
        Thread.getDefaultUncaughtExceptionHandler() as Thread.UncaughtExceptionHandler
    private lateinit var lastException: Pair<Thread?, Throwable?>

    data class ExceptionData(
        val thread: Thread? = null,
        val throwable: Throwable? = null,
    )

    var enabled = false
    var isShowLog = true

    private val fileManager = FileManager(context)


    companion object {
        private val _exceptionHandler : MutableStateFlow<ExceptionData?> = MutableStateFlow(null)
        val exceptionHandler: StateFlow<ExceptionData?> get() = _exceptionHandler.asStateFlow()

        val customIntentAction = "com.example.exceptions.logManager"
        val exceptionKey = "exceptionKey"
    }

    init {

        Thread.setDefaultUncaughtExceptionHandler { thread: Thread?, throwable: Throwable? ->
            throwable?.let { throwableObject ->
                if (enabled) {
                    var outString =
                        "\"Exception name: $throwableObject; Thread name: $thread;\\n\""
                    throwableObject.stackTrace.forEach { traceObject ->
                        outString += traceObject.toString() + "\n"
                    }
                    fileManager.writeToFile(outString)
                    lastException = Pair(thread, throwable)
                    if (isShowLog) {
                        val intent = Intent(customIntentAction).apply {
                            putExtra(exceptionKey, throwableObject.toString())
                        }
                        context.sendBroadcast(intent)
                        _exceptionHandler.value = ExceptionData(thread, throwable)
                    } else {
                        throwLastExceptionToDefaultHandler()
                    }
                }
            }
        }

        fileManager.cleanOldFiles()
        fileManager.writeToFile("System started\n")
    }

    private fun throwLastExceptionToDefaultHandler() {
        if ((lastException.first != null) and (lastException.second != null))
            baseExceptionHandler.uncaughtException(lastException.first!!, lastException.second!!)
    }

}