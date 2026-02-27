package com.oms.spendwise.domain

import android.util.Log
import javax.inject.Inject
import kotlin.text.dropLast


class CalculatorEngine @Inject constructor() {
    private var currentInput = "0"
    private var firstOperand = ""
    private var operator: Operator? = null
    private var resetInput = false

    enum class Operator(val symbol: String){
        ADD("+"), SUBTRACT("-"), MULTIPLY("×"), DIVIDE("÷")
    }

    fun setCurrentInput(input: Double){
        currentInput = input.toString()
        firstOperand = ""
        operator = null
        resetInput = false
    }

    fun inputDigit(digit: String): String{
        if(resetInput){
            currentInput = digit
            resetInput = false
        } else {
            currentInput = if(currentInput == "0") digit else currentInput + digit
        }
        return formatNumber(currentInput)
    }

    fun inputDecimal() : String {
        if(resetInput){
            currentInput = "0."
            resetInput = false
            return currentInput
        }

        if(!currentInput.contains(".")){
            currentInput += "."
        }

        return currentInput
    }

    fun setOperator(operator: Operator) : String {
        if(currentInput.toDoubleOrNull() != null){
            firstOperand = currentInput
        } else{
            return currentInput
        }
        this.operator = operator
        resetInput = true
        return currentInput
    }

    fun calculate(): String{
        val secondOperand = currentInput.toDoubleOrNull() ?: return currentInput
        val firstOperand = this.firstOperand.toDoubleOrNull() ?: return  currentInput

        val operator = this.operator ?: return currentInput

        val result = when (operator){
            Operator.ADD -> firstOperand + secondOperand
            Operator.SUBTRACT -> firstOperand - secondOperand
            Operator.MULTIPLY -> firstOperand * secondOperand
            Operator.DIVIDE -> {
                if(secondOperand == 0.0) return error()
                firstOperand / secondOperand
            }
        }

        currentInput = formatResult(result)
        this.firstOperand = ""
        this.operator = null
        resetInput = true

        return currentInput
    }

    fun clear(): String{
        currentInput = "0"
        firstOperand = ""
        operator = null
        resetInput = false
        return currentInput
    }

    fun backSpace() : String {
        if(currentInput.isNotEmpty()){
            currentInput = currentInput.dropLast(1)
        } else if(operator != null){
            operator = null
        } else {
            firstOperand = if(firstOperand.length > 1){
                firstOperand.dropLast(1)
            } else{
                currentInput = "0"
                ""
            }
        }

        return currentInput
    }

    private fun error(): String{
        clear()
        return "Error"
    }

    private fun formatResult(value: Double) : String{
        return if(value % 1 == 0.0){
            value.toLong().toString()
        } else {
            value.toString()
        }
    }

    private fun formatNumber(input: String): String{
        val decimalIndex = input.indexOf('.')

        if (decimalIndex == -1) return input

        val decimalPartLength = input.length - decimalIndex - 1

        return if (decimalPartLength > 2) {
            input.take(decimalIndex + 3)
        } else {
            input
        }
    }
}