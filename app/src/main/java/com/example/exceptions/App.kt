package com.example.exceptions

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast
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
                val thread = data.thread
                val throwableObject = data.throwable ?: Throwable()
                var outString =
                    "\"Exception name: $throwableObject; Thread name: $thread;\\n\""
                throwableObject.stackTrace.forEach { traceObject ->
                    outString += traceObject.toString() + "\n"
                }
                Log.d("ExceptionResult", "Application")
            }
        }

        //Сюда бы конечно хотелось бы прокидывать непосредственно экзепшн, но АПИ нужен от 22, а функционал getParcelable не доступен в этом АПИ
        applicationContext.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.apply {
                    if (action == ExceptionLogManager.customIntentAction) {
                        Log.d("ExceptionIntentFilter", "Application")
                    }
                }
            }
        }, IntentFilter().apply {
            addAction(ExceptionLogManager.customIntentAction)
        })
    }
}