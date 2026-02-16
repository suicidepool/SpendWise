package com.oms.spendwise.model.typeConverter

import androidx.room.TypeConverter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

class LocalDbConverters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(date: String?): LocalDate? =
        date?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? =
        dateTime?.toString()

    @TypeConverter
    fun toLocalDateTime(dateTime: String?): LocalDateTime? =
        dateTime?.let { LocalDateTime.parse(it) }

    @TypeConverter
    fun fromDayOfWeek(day: DayOfWeek?): String? = day?.name

    @TypeConverter
    fun toDayOfWeek(day: String?): DayOfWeek? =
        day?.let { DayOfWeek.valueOf(it) }
}