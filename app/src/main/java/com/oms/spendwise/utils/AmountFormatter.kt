package com.oms.spendwise.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

object AmountFormatter {
    fun formatAmount(amount: Double): String{
        val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
        formatter.maximumFractionDigits = 2
        formatter.minimumFractionDigits = 0
        return formatter.format(amount)
    }

    fun formatDecimal(amount: Double): String{
        val formatter = DecimalFormat("0.##")
        return formatter.format(amount)
    }
}