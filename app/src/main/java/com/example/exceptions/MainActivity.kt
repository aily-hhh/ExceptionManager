package com.example.exceptions

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.exceptions.databinding.ActivityMainBinding
import com.example.exceptions.logException.LogsActivity
import com.example.exceptions.logManager.ExceptionLogManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.apply {
            button.setOnClickListener {
                //По кнопке троваем экзепшн
                CoroutineScope(Dispatchers.IO).launch {
                    for (index in 0..array.size)
                        array[index] = 12
                }
            }

            buttonOpenLog.setOnClickListener {
                val intent = Intent(this@MainActivity, LogsActivity::class.java)
                startActivity(intent)
            }
        }
    }
}