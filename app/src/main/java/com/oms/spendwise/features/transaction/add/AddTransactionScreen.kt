package com.oms.spendwise.features.transaction.add

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.stylusHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.translationMatrix
import coil.size.Dimension
import com.oms.spendwise.model.entity.Transaction
import com.oms.spendwise.model.enum.TransactionType
import com.oms.spendwise.ui.theme.Dimens
import com.oms.spendwise.ui.theme.InputFieldContainer
import com.oms.spendwise.ui.theme.PrimaryBlue
import com.oms.spendwise.ui.theme.TextPrimary
import com.oms.spendwise.ui.theme.TextSecondary
import com.oms.spendwise.R
import com.oms.spendwise.domain.CalculatorEngine
import com.oms.spendwise.features.profile.ProfileViewModel
import com.oms.spendwise.features.transaction.TransactionViewModel
import com.oms.spendwise.model.entity.Category
import com.oms.spendwise.ui.theme.TextHint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import okhttp3.internal.format
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    modifier: Modifier = Modifier,
    transaction: Transaction? = null,
    addTransactionViewModel: AddTransactionViewModel,
    transactionViewModel: TransactionViewModel,
    profileViewModel: ProfileViewModel,
    context: Context
) {
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(
        initialHour = addTransactionViewModel.transactionDateTime.toLocalTime().hour,
        initialMinute = addTransactionViewModel.transactionDateTime.toLocalTime().minute,
        is24Hour = false,
    )
    val scope = rememberCoroutineScope()

    val transactionDate = if(addTransactionViewModel.transactionDateTime.toLocalDate() == LocalDate.now())
        "Today, " + addTransactionViewModel.formatDate(addTransactionViewModel.transactionDateTime.toLocalDate())
    else addTransactionViewModel.formatDate(addTransactionViewModel.transactionDateTime.toLocalDate())

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                modifier = Modifier
                    .statusBarsPadding(),
                title = if(transaction == null) "Add Transaction" else "Edit Transaction"
            ) { }
        },
        bottomBar = {
            CalculatorKeypad(
                modifier = Modifier
                    .navigationBarsPadding(),
                addTransactionViewModel = addTransactionViewModel,
                onAddTransaction = {
                    if(addTransactionViewModel.firstOperand == "0"){
                        scope.launch {
                            Toast.makeText(
                                context,
                                "🫰 Amount is 0",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else if(addTransactionViewModel.selectedCategory == null){
                        scope.launch {
                            Toast.makeText(
                                context,
                                "🙏 select a category",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        transactionViewModel.addTransaction(
                            userId = profileViewModel.user!!.userId,
                            categoryId = addTransactionViewModel.selectedCategory!!.categoryId,
                            amount = addTransactionViewModel.firstOperand.toDouble(),
                            type = addTransactionViewModel.selectedCategory!!.type,
                            note = addTransactionViewModel.note,
                            transactionDateTime = addTransactionViewModel.transactionDateTime,
                            createdAt = LocalDateTime.now(),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            TransactionTypeSelectSection(
                modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
                transactionType = addTransactionViewModel.transactionType,
                onTransactionTypeChange = addTransactionViewModel.onTransactionTypeChange
            )

            Spacer(Modifier.height(12.dp))

            AmountSection(
                modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
                amountText = "${addTransactionViewModel.firstOperand}${addTransactionViewModel.operator?.symbol ?: ""}${addTransactionViewModel.secondOperand}"
            )

            Spacer(Modifier.height(12.dp))


            SelectCategorySection(
                modifier = modifier,
                selectedCategory = addTransactionViewModel.selectedCategory,
                categories = transactionViewModel.categories,
                onSelectedCategoryChange = addTransactionViewModel.onCategoryChange,
                transactionType = addTransactionViewModel.transactionType,
                context = context
            )

            Spacer(Modifier.height(12.dp))

            AddNoteSection(
                modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
                note = addTransactionViewModel.note,
                onNoteChange = addTransactionViewModel.onNoteChange
            )

            Spacer(Modifier.height(12.dp))

            DateSelectSection(
                modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
                transactionDate = transactionDate,
                onDateChange = {
                    datePickerState.selectedDateMillis?.let { mills ->
                        val localDate = addTransactionViewModel.fromMillsToLocalDate(mills)
                        addTransactionViewModel.onTransactionDateTimeChange(
                            addTransactionViewModel.transactionDateTime.with(localDate)
                        )
                    }
                },
                datePickerState = datePickerState
            )

            Spacer(Modifier.height(12.dp))

            TimeSelectSection(
                modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
                transactionTime = addTransactionViewModel.formatTime(addTransactionViewModel.transactionDateTime.toLocalTime()),
                onTimeChange = {
                    val localTime = LocalTime.of(
                        timePickerState.hour,
                        timePickerState.minute
                    )
                    addTransactionViewModel.onTransactionDateTimeChange(
                        addTransactionViewModel.transactionDateTime.with(localTime)
                    )
                },
                timePickerState = timePickerState
            )
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    title: String,
    onCancel: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onCancel
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                modifier = Modifier.size(26.dp),
                tint = TextPrimary
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Box(Modifier.size(42.dp))
    }
}

@Composable
private fun TransactionTypeSelectSection(
    modifier: Modifier = Modifier,
    transactionType: TransactionType,
    onTransactionTypeChange: (TransactionType) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(10.dp))
            .background(color = InputFieldContainer)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        TransactionTypeButton(
            modifier = Modifier.weight(.5f),
            text = "Expense",
            cornerSize = 8.dp,
            isSelected = transactionType == TransactionType.EXPENSE,
            onClick = {onTransactionTypeChange(TransactionType.EXPENSE)}
        )
        TransactionTypeButton(
            modifier = Modifier.weight(.5f),
            text = "Income",
            cornerSize = 8.dp,
            isSelected = transactionType == TransactionType.INCOME,
            onClick = {onTransactionTypeChange(TransactionType.INCOME)}
        )
    }
}

@Composable
private fun TransactionTypeButton(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    cornerSize: Dp = 2.dp,
    onClick: () -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(cornerSize),
        colors = ButtonDefaults.buttonColors(
            containerColor = if(isSelected) Color.White else Color.Transparent,
            contentColor = if(isSelected) PrimaryBlue else TextSecondary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ){
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun AmountSection(
    modifier: Modifier = Modifier,
    amountText: String
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = amountText,
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )
    }
}

@Composable
private fun CalculatorKeypad(
    modifier: Modifier = Modifier,
    addTransactionViewModel: AddTransactionViewModel,
    onAddTransaction: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(Color.White)
    ) {
        //left side
        Column(
            modifier = Modifier
                .weight(4f)
                .padding(top = 12.dp, bottom = 12.dp, start = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            val rows = listOf(
                listOf("7", "8", "9", "÷"),
                listOf("4", "5", "6", "×"),
                listOf("1", "2", "3", "-"),
                listOf("0", "00", ".", "+")
            )

            val operators = listOf("+","-","×","÷")
            val numbers = listOf("1","2","3","4","5","6","7","8","9","0")

            rows.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    row.forEach { key ->
                        if(operators.contains(key)){
                            CalculatorButton(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    when(key){
                                        "+" -> addTransactionViewModel.onOperatorClick(
                                            CalculatorEngine.Operator.ADD
                                        )
                                        "-" -> addTransactionViewModel.onOperatorClick(
                                            CalculatorEngine.Operator.SUBTRACT
                                        )
                                        "×" -> addTransactionViewModel.onOperatorClick(
                                            CalculatorEngine.Operator.MULTIPLY
                                        )
                                        "÷" -> addTransactionViewModel.onOperatorClick(
                                            CalculatorEngine.Operator.DIVIDE
                                        )
                                    }

                                }
                            ){
                                Text(
                                    text = key,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PrimaryBlue
                                )
                            }
                        } else if(numbers.contains(key)) {
                            CalculatorButton(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    addTransactionViewModel.onDigitClick(key)
                                }
                            ){
                                Text(
                                    text = key,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                        } else if(key == "00"){
                            CalculatorButton(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    addTransactionViewModel.onClearClick()
                                }
                            ){
                                Text(
                                    text = key,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                        } else if(key == "."){
                            CalculatorButton(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    addTransactionViewModel.onDecimalClick()
                                }
                            ){
                                Text(
                                    text = key,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                        }

                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Confirm Button (Big Blue)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 12.dp, bottom = 12.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    addTransactionViewModel.onBackspaceClick()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_delete),
                    contentDescription = "Confirm",
                    tint = TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryBlue)
                    .clickable {
                        if (addTransactionViewModel.operator != null && addTransactionViewModel.secondOperand.isNotEmpty()) {
                            addTransactionViewModel.onEqualClick()
                        } else {
                            onAddTransaction()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if(addTransactionViewModel.operator != null && addTransactionViewModel.secondOperand.isNotEmpty()){
                    Icon(
                        painter = painterResource(R.drawable.icon_equals),
                        contentDescription = "Confirm",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                } else{
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirm",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CalculatorButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(InputFieldContainer)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@SuppressLint("DiscouragedApi")
@Composable
private fun SelectCategorySection(
    modifier: Modifier = Modifier,
    transactionType: TransactionType,
    selectedCategory: Category?,
    onSelectedCategoryChange: (Category) -> Unit,
    categories: List<Category>,
    context: Context
){
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "SELECT CATEGORY",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding)
        )

        Spacer(Modifier.height(6.dp))

        LazyHorizontalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            rows = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(Dimens.HorizontalScreenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.HorizontalScreenPadding),
            contentPadding = PaddingValues(horizontal = Dimens.HorizontalScreenPadding)
        ) {

            items(categories.filter { it.type == transactionType.value }) { category ->

                val iconId = context.resources.getIdentifier(
                    category.icon,
                    "drawable",
                    context.packageName
                )

                val iconColor = Color(0xFF000000 + category.colorHex.toLong(16))
                Column(
                    modifier = Modifier
                        .aspectRatio(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(iconColor.copy(0.17f))
                            .clickable { onSelectedCategoryChange(category) }
                            .border(
                                width = 2.dp,
                                color = if (selectedCategory == category) PrimaryBlue else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(iconId),
                            contentDescription = "Confirm",
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

            }
        }
    }

}

@Composable
private fun AddNoteSection(
    modifier: Modifier = Modifier,
    note: String,
    onNoteChange: (String) -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color.Black.copy(alpha = .2f),
            )
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ){
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(Color.White),
            value = note,
            onValueChange = onNoteChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.labelLarge,
            placeholder = {
                Text(
                    text = "Add Note",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextHint
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.icon_note),
                    contentDescription = "note",
                    tint = TextHint,
                    modifier = Modifier.size(16.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateSelectSection (
    modifier: Modifier = Modifier,
    transactionDate: String,
    onDateChange: () -> Unit,
    datePickerState: DatePickerState
    ) {

    var showDatePicker by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color.Black.copy(alpha = .2f),
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = {showDatePicker = true})
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
                .background(
                    color = Color.White
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "date pick",
                tint = TextHint,
                modifier = Modifier
                    .size(18.dp)
            )
            Text(
                text = transactionDate,
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary
            )
        }

    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onDateChange()
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {showDatePicker = false}) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeSelectSection(
    modifier: Modifier = Modifier,
    transactionTime: String,
    onTimeChange: () -> Unit,
    timePickerState: TimePickerState
) {

    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color.Black.copy(alpha = .2f),
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = {showDialog = true})
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
                .background(
                    color = Color.White
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.icon_clock),
                contentDescription = "date pick",
                tint = TextHint,
                modifier = Modifier
                    .size(16.dp)
            )
            Text(
                text = transactionTime,
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary
            )
        }
    }

    if(showDialog){
        AlertDialog(
            onDismissRequest = {showDialog = false},
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Dismiss")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onTimeChange()
                    showDialog = false
                }) {
                    Text("OK")
                }
            },
            text = {
                TimePicker(
                    state = timePickerState
                )
            }
        )
    }
}