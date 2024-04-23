package com.example.exceptions.logException

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * Утилиты для работы с датой и временем.
 */
object DateTimeUtils {
    /**
     * Переменная для получения текущей временной зоны на устройстве.
     */
    private val deviceTimeZone: TimeZone get() = TimeZone.getDefault()

    /**
     * Форматтер для отображения времени в формате ЧАС:МИНУТЫ.
     */
    @get:SuppressLint("SimpleDateFormat")
    private val formatTime get () = SimpleDateFormat("HH:mm").apply {
        timeZone = deviceTimeZone
    }

    /**
     * Форматтер для отображения времени в формате ДЕНЬ.МЕСЯЦ.ГОД.
     */
    @get:SuppressLint("SimpleDateFormat")
    private val formatDate get() = SimpleDateFormat("dd.MM.yyyy").apply {
        timeZone = deviceTimeZone
    }

    /**
     * Форматтер для отображения времени в формате ДЕНЬ.МЕСЯЦ.ГОД ЧАС:МИНУТЫ.
     */
    @get:SuppressLint("SimpleDateFormat")
    private val formatDateTime get() = SimpleDateFormat("dd.MM.yyyy HH:mm").apply {
        timeZone = deviceTimeZone
    }


    /**
     * Функция получения Timestamp без даты.
     *
     * @return Timestamp.
     */
    fun currentTimeMillis() = System.currentTimeMillis()

    /**
     * Функция перевода строки в Timestamp.
     *
     * @return Timestamp.
     */
    fun String.toTimestamp() : Long{
        val time = formatDate.parse(this)?.time
        return  time ?: currentTimeMillis()
    }

    /**
     * Функция перевода Timestamp в строку в формате ЧАС:МИНУТЫ.
     *
     * @return Строка в формате ЧАС:МИНУТЫ.
     */
    fun Long.toTime(): String {
        val currentTime = Date(this)
        return formatTime.format(currentTime)
    }

    /**
     * Функция перевода Timestamp в строку в формате ЧАС:МИНУТЫ.
     *
     * @return Строка в формате ДЕНЬ.МЕСЯЦ.ГОД.
     */
    fun Long.toDate(): String {
        val currentDate = Date(this)
        return formatDate.format(currentDate)
    }

    /**
     * Функция перевода Timestamp в строку в формате ДЕНЬ.МЕСЯЦ.ГОД ЧАС:МИНУТЫ.
     *
     * @return Строка в формате ДЕНЬ.МЕСЯЦ.ГОД ЧАС:МИНУТЫ.
     */
    fun Long.toDateTime(): String {
        val currentDate = Date(this)
        return formatDateTime.format(currentDate)
    }

    /**
     * Функция получения Timestamp с датой.
     *
     * @return Timestamp.
     */
    fun currentDateTimeMillis() = Calendar.getInstance().timeInMillis
}