package com.example.exceptions.logException

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.exceptions.databinding.ItemExceptionBinding
import com.example.exceptions.logManager.ExceptionLogManager

class ExceptionsAdapter: RecyclerView.Adapter<ExceptionViewHolder>() {

    private val callback: DiffUtil.ItemCallback<ExceptionLogManager.ExceptionData> =
        object : DiffUtil.ItemCallback<ExceptionLogManager.ExceptionData>() {
            override fun areItemsTheSame(
                oldItem: ExceptionLogManager.ExceptionData,
                newItem: ExceptionLogManager.ExceptionData
            ): Boolean {
                return oldItem.timestamp == newItem.timestamp
            }

            override fun areContentsTheSame(
                oldItem: ExceptionLogManager.ExceptionData,
                newItem: ExceptionLogManager.ExceptionData
            ): Boolean {
                return when {
                    oldItem.thread == newItem.thread -> true
                    oldItem.throwable == newItem.throwable -> true
                    else -> oldItem.timestamp == newItem.timestamp
                }
            }
        }

    private val differ = AsyncListDiffer(this, callback)

    fun setDiffer(list: List<ExceptionLogManager.ExceptionData>) {
        differ.submitList(list.toList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExceptionViewHolder {
        return ExceptionViewHolder(
            ItemExceptionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ExceptionViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }
}