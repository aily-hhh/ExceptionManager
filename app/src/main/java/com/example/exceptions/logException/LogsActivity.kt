package com.example.exceptions.logException

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exceptions.databinding.ActivityLogsBinding
import com.example.exceptions.logManager.ExceptionLogManager
import com.example.exceptions.logManager.FileManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class LogsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogsBinding
    private val exceptionsAdapter = ExceptionsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerExceptions.apply {
            adapter = exceptionsAdapter
            layoutManager = LinearLayoutManager(this@LogsActivity)
        }
    }

    override fun onStart() {
        super.onStart()

        val fm = FileManager(applicationContext)
        val itemsStr = fm.readFromFile(Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis.toString() + ".json")

        val list = mutableListOf<ExceptionLogManager.ExceptionData>()
        if (!itemsStr.isNullOrEmpty()) {
            list.addAll(Gson().fromJson(itemsStr, object : TypeToken<List<ExceptionLogManager.ExceptionData>>() {}.type))
        }

        list.sortBy { exceptionData -> exceptionData.timestamp }

        exceptionsAdapter.setDiffer(list)
    }

}