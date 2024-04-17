package com.example.exceptions

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.example.exceptions.logManager.ExceptionLogManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.acra.BuildConfig
import org.acra.data.StringFormat
import org.acra.ktx.initAcra

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

        //Сюда бы конечно хотелось бы прокидывать непосредственно экзепшн, но АПИ нужен от 22, а функционал getParcelable не доступен в этом АПИ
        applicationContext.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.apply {
                    if (action == ExceptionLogManager.CUSTOM_INTENT_ACTION) {
                        Log.d("ExceptionIntentFilter", "Application")
                    }
                }
            }
        }, IntentFilter().apply {
            addAction(ExceptionLogManager.CUSTOM_INTENT_ACTION)
        })
    }
}