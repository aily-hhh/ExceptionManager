package com.example.exceptions

import android.app.Application
import android.util.Log
import com.example.exceptions.logManager.ExceptionLogManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class App : Application() {

    override fun onCreate() {
        ExceptionLogManager(applicationContext).enabled = true
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            ExceptionLogManager.exceptionHandler.collectLatest { data ->
                data ?: return@collectLatest
                Log.d("ExceptionResult", "Application")
            }
        }
    }
}