package com.example.exceptions.logException

import androidx.recyclerview.widget.RecyclerView
import com.example.exceptions.databinding.ItemExceptionBinding
import com.example.exceptions.logManager.ExceptionLogManager

class ExceptionViewHolder(private val binding: ItemExceptionBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: ExceptionLogManager.ExceptionData) {
        binding.textViewThreadValue.text = model.thread
        binding.textViewThrowableValue.text = model.throwable
        binding.textViewTimestampValue.text = model.timestamp.toString()
    }
}