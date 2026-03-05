package com.oms.spendwise.features.transaction.calendar

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class CalendarViewModel @Inject constructor() : ViewModel() {
    var yearMonth by mutableStateOf<YearMonth>(YearMonth.now())
    var calendar by mutableStateOf<List<LocalDate?>>(emptyList())

    init {
        createCalendar()
    }

    fun getFormatedYearMonth() : String {
        return yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))
    }

    val shiftNextMonth = {
        yearMonth = yearMonth.plusMonths(1)
        createCalendar()
    }

    val shiftPrevMonth = {
        yearMonth = yearMonth.minusMonths(1)
        createCalendar()
    }

    fun createCalendar(){
        val tempCalendar:MutableList<LocalDate?> = mutableListOf()
        val totalDays = yearMonth.lengthOfMonth()
        var day = 1
        var i = yearMonth.atDay(1).dayOfWeek.value

        if(i != DayOfWeek.SUNDAY.value)
            for(x in 1 ..i){
                tempCalendar.add(null)
            }
        else {
            i = 1
        }

        while (i < 35 && day <= totalDays){
            tempCalendar.add(yearMonth.atDay(day))
            i++
            day++
        }
        i = 0

        while(day <= totalDays){
            tempCalendar[i] = yearMonth.atDay(day)
            i++
            day++
        }
        calendar = tempCalendar
        Log.d("DATES",calendar.toString())
    }
}