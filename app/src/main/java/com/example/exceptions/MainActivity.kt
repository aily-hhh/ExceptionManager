package com.example.exceptions

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.exceptions.logManager.ExceptionLogManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        applicationContext.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.apply {
                    if (action == ExceptionLogManager.CUSTOM_INTENT_ACTION) {
                        Log.d("ExceptionIntentFilter", "MainActivity")
                    }
                }
            }
        }, IntentFilter().apply {
            addAction(ExceptionLogManager.CUSTOM_INTENT_ACTION)
        })

        lifecycleScope.launch(Dispatchers.IO) {
            ExceptionLogManager.exceptionHandler.collectLatest { data ->
                data ?: return@collectLatest
                //Так сделано для демонстрации. Код выше работает.
                Log.d("ExceptionResult", "MainActivity")
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    val array = ByteArray(8)

    override fun onStart() {
        super.onStart()
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            //По кнопке троваем экзепшн
            CoroutineScope(Dispatchers.IO).launch {
                for (index in 0..array.size)
                    array[index] = 12
            }
        }
    }
}