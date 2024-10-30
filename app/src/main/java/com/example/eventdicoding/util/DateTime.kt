package com.example.eventdicoding.util

import android.content.Context
import android.os.Build
import com.example.eventdicoding.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTime {

    private const val DATE_FORMAT = "yyyy-MM-dd"
    private const val INDONESIAN_DATE_FORMAT = "dd MMMM yyyy"

    private fun getDayFromDate(dateString: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_FORMAT))
            date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("id", "ID"))
        } else {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val date = sdf.parse(dateString)
            val calendar = Calendar.getInstance().apply { time = date!! }
            calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale("id", "ID"))!!
        }
    }

    private fun convertDateToIndonesianFormat(dateString: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_FORMAT))
            date.format(DateTimeFormatter.ofPattern(INDONESIAN_DATE_FORMAT, Locale("id", "ID")))
        } else {
            val inputFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val outputFormat = SimpleDateFormat(INDONESIAN_DATE_FORMAT, Locale("id", "ID"))
            val date: Date? = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        }
    }

    private fun getIndonesianDateFormat(dateString: String): String {
        val indonesianDay = getDayFromDate(dateString)
        val indonesianDate = convertDateToIndonesianFormat(dateString)
        return "$indonesianDay, $indonesianDate"
    }

    fun convertDate(context: Context, begin: String, end: String): String {
        val (beginDate, beginTime) = begin.split(" ")
        val (endDate, endTime) = end.split(" ")

        val beginFinal = getIndonesianDateFormat(beginDate)
        val endFinal = getIndonesianDateFormat(endDate)

        return if (beginFinal == endFinal) {
            context.getString(R.string.event_date_same_day, beginFinal, beginTime, endTime)
        } else {
            context.getString(R.string.event_date_different_day, beginFinal, beginTime, endFinal, endTime)
        }
    }

    fun convertBeginDate(context: Context, date: String): String {
        val (beginDate, beginTime) = date.split(" ")
        val beginFinal = getIndonesianDateFormat(beginDate)
        return context.getString(R.string.notification_content_text, beginFinal, beginTime)
    }
}
