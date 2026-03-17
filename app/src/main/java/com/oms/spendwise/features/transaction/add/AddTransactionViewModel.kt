package com.oms.spendwise.features.transaction.add

import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.lifecycle.ViewModel
import com.oms.spendwise.domain.CalculatorEngine
import com.oms.spendwise.model.entity.Category
import com.oms.spendwise.model.enum.TransactionType
import com.oms.spendwise.utils.AmountFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalQueries.localDate
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val calculator: CalculatorEngine
) : ViewModel() {
    var firstOperand by mutableStateOf("0")
    var secondOperand by mutableStateOf("")
    var operator by mutableStateOf<CalculatorEngine.Operator?>(null)
    var selectedCategory by mutableStateOf<Category?>(null)
    var transactionType by mutableStateOf(TransactionType.EXPENSE)
    var note by mutableStateOf("")
    var transactionDateTime by mutableStateOf<LocalDateTime>(LocalDateTime.now())


    val onTransactionDateTimeChange = {localDateTime: LocalDateTime ->
        this.transactionDateTime = localDateTime
    }

    fun setAmount(amount: String){
        calculator.setCurrentInput(amount)
        firstOperand = amount
        secondOperand = ""
        operator = null
    }

    val onCategoryChange = {category: Category? ->
        this.selectedCategory = category
    }

    val onTransactionTypeChange = {transactionType: TransactionType ->
        this.transactionType = transactionType
    }

    val onNoteChange = {text: String ->
        this.note = text
    }

    fun fromMillsToLocalDate(mills: Long): LocalDate{
        return Instant.ofEpochMilli(mills)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }


    fun onDigitClick(digit: String){
        if(operator == null)
            firstOperand = calculator.inputDigit(digit)
        else
            secondOperand = calculator.inputDigit(digit)
    }

    fun onDecimalClick(){
        if(operator == null)
            firstOperand = calculator.inputDecimal()
        else
            secondOperand = calculator.inputDecimal()
    }

    fun onOperatorClick(operator: CalculatorEngine.Operator){
        if(firstOperand.isNotEmpty() && secondOperand.isNotEmpty() && this.operator != null){
            firstOperand = calculator.calculate()
            secondOperand = ""
        }
        this.operator = operator
        calculator.setOperator(operator)
    }

    fun onEqualClick(){
        firstOperand = calculator.calculate()
        secondOperand = ""
        operator = null
    }

    fun onClearClick(){
        firstOperand = calculator.clear()
        secondOperand = ""
        operator = null
    }

    fun onBackspaceClick(){
        calculator.backSpace()
        if(secondOperand.isNotEmpty()){
            secondOperand = secondOperand.dropLast(1)
        } else if(operator != null){
            operator = null
        } else {
            firstOperand = if(firstOperand.length > 1){
                firstOperand.dropLast(1)
            } else{
                "0"
            }
        }
    }

    fun clearData(){
        onClearClick()
        onCategoryChange(null)
        onTransactionTypeChange(TransactionType.EXPENSE)
        onNoteChange("")
        onTransactionDateTimeChange(LocalDateTime.now())
    }
}