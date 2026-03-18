package com.oms.spendwise.features.budget.add

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oms.spendwise.R
import com.oms.spendwise.features.budget.BudgetViewModel
import com.oms.spendwise.model.entity.Category
import com.oms.spendwise.ui.theme.SuccessGreen
import com.oms.spendwise.ui.theme.Dimens
import com.oms.spendwise.ui.theme.InputFieldContainer
import com.oms.spendwise.ui.theme.PrimaryBlue
import com.oms.spendwise.ui.theme.AlertRed
import com.oms.spendwise.ui.theme.TextPrimary
import com.oms.spendwise.ui.theme.TextSecondary
import com.oms.spendwise.utils.AmountFormatter
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Currency

@Composable
fun SetBudgetScreen(
    modifier: Modifier = Modifier,
    budgetVM: BudgetViewModel,
    userId: Long,
    categories: List<Category>,
    currency: Currency,
    context: Context,
    onBack: () -> Unit
) {

    val scope = rememberCoroutineScope()
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(startDate.plusDays(29)) }
    var budgetDuration by remember { mutableStateOf(budgetVM.getDays(startDate, endDate)) }
    var amount by remember { mutableStateOf("") }
    val selectedCategoryList = remember { mutableStateListOf<Pair<Category, String>>() }
    var remainingAllocation by remember { mutableStateOf(0.0) }
    var isNewBudgetCategoryAdded by remember { mutableStateOf(false) }

    LaunchedEffect(startDate, endDate) {
        budgetDuration = budgetVM.getDays(startDate, endDate)
    }

    LaunchedEffect(budgetVM.budget){
        budgetVM.budget?.let { budget ->
            startDate = budget.startDate
            endDate = budget.endDate
            budgetDuration = budgetVM.getDays(budget.startDate, budget.endDate)
            amount = AmountFormatter.formatDecimal(budget.amount)
        }
    }

    LaunchedEffect(budgetVM.budgetCategories) {
        isNewBudgetCategoryAdded = false
        selectedCategoryList.clear()
        budgetVM.budgetCategories.forEach { item ->
            val category = categories.find { it.categoryId == item.categoryId }
            selectedCategoryList.add(Pair(category!!, AmountFormatter.formatDecimal(item.amountLimit)))
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                modifier = Modifier
                    .statusBarsPadding(),
                onCancel = onBack
            )
        },
        bottomBar = {
            BottomBar(
                modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
                text = if(budgetVM.budget == null) "Add Budget" else "Update",
                add = {
                    var totalAllocated = 0.0
                    for(i in selectedCategoryList){
                        val catAmount = i.second.toDoubleOrNull()
                        if(catAmount != null && catAmount > 0.0){
                            totalAllocated += i.second.toDouble()
                        }
                        else{
                            Toast.makeText(
                                context,
                                "Allocate amount to ${i.first.name}",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@BottomBar
                        }
                    }
                    var remainingToAllocate = 0.0

                    if(amount.toDoubleOrNull() != null){
                        remainingToAllocate = amount.toDouble() - totalAllocated
                    }

                    if(amount.isEmpty() || amount.toDoubleOrNull() == null){
                        Toast.makeText(
                            context,
                            "Please enter budget amount",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if(remainingToAllocate < 0.0){
                        Toast.makeText(
                            context,
                            "Allocate valid amounts to categories",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else {
                        if(budgetVM.budget == null)
                            budgetVM.addBudget(
                                userId = userId,
                                startDate = startDate,
                                endDate = endDate,
                                amount = amount.toDouble(),
                                categories = selectedCategoryList
                            )
                        else
                            budgetVM.updateBudget(
                                startDate = startDate,
                                endDate = endDate,
                                amount = amount.toDouble(),
                                categories = selectedCategoryList
                            )
                        onBack()
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(22.dp))

            BudgetDurationSelectSection(
                modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
                budgetDuration = budgetDuration,
                onBudgetDurationChange = { duration ->
                    endDate = startDate.plusDays(duration.toLong())
                }
            )

            Spacer(Modifier.height(22.dp))

            AmountSection(
                currency = currency,
                amount = amount,
                onAmountChange = {
                    if(it.length > 10) return@AmountSection
                    if(it == "" || it[it.length-1].isDigit() ||  it[it.length-1] == '.' )
                        if(it.contains(".")){
                            val decimalIndex = it.indexOf(".")
                            val decimalSequence = it.subSequence(decimalIndex+1, it.length)
                            amount = if(decimalSequence.length > 2){
                                it.subSequence(0, decimalIndex+3).toString()
                            } else {
                                it
                            }
                        }
                        else{
                            amount = if(amount == "0" && it.isNotEmpty())
                                it[it.length-1].toString()
                            else
                                it
                        }
                    if(amount.toDoubleOrNull() != null)
                        remainingAllocation += (amount.toDouble() - remainingAllocation)
                }
            )

            Spacer(Modifier.height(22.dp))

            SelectCategorySection(
                modifier = modifier,
                selectedCategoryIdList = selectedCategoryList.map { it.first.categoryId},
                categories = categories,
                onSelectedCategoryChange = { category ->
                    scope.launch {
                        if(selectedCategoryList.map { it.first.categoryId }.contains(category.categoryId)){
                            val budgetCategory = selectedCategoryList.findLast { it.first.categoryId == category.categoryId }
                            selectedCategoryList.remove(budgetCategory)
                            isNewBudgetCategoryAdded = false
                        } else {
                            isNewBudgetCategoryAdded = true
                            selectedCategoryList.add(
                                Pair(
                                    category,
                                    ""
                                )
                            )
                        }
                    }
                },
                context = context
            )

            Spacer(Modifier.height(22.dp))

            AllocationDetailsSection(
                modifier = Modifier
                    .padding(horizontal = Dimens.HorizontalScreenPadding),
                selectedCategoryList = selectedCategoryList,
                currency = currency,
                isNewBudgetCategoryAdded = isNewBudgetCategoryAdded,
                amount = amount,
                context = context
            )

        }
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    text: String,
    add: () -> Unit
){
    Button(
        onClick = add,
        shape = RoundedCornerShape(Dimens.ButtonCornerRadius),
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}


@Composable
private fun AllocationDetailsSection(
    modifier: Modifier = Modifier,
    amount: String,
    selectedCategoryList: SnapshotStateList<Pair<Category, String>>,
    isNewBudgetCategoryAdded: Boolean,
    currency: Currency,
    context: Context
) {
    var totalAllocated = 0.0
    for(i in selectedCategoryList){
        if(i.second.toDoubleOrNull() != null)
            totalAllocated += i.second.toDouble()
    }
    var remainingToAllocate = 0.0

    if(amount.toDoubleOrNull() != null){
        remainingToAllocate = amount.toDouble() - totalAllocated
    }


    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = "ALLOCATION DETAILS",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(16.dp))
        if(selectedCategoryList.isNotEmpty()){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, start = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Remaining to allocate",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = "${currency.symbol}${AmountFormatter.formatAmount(remainingToAllocate)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if(remainingToAllocate > 0) MaterialTheme.colorScheme.primary else if(remainingToAllocate < 0) AlertRed else SuccessGreen,
                    fontWeight = FontWeight.Bold

                )
            }
            selectedCategoryList.forEachIndexed { index, budgetItem ->
                BudgetCategoryItem(
                    category = budgetItem.first,
                    amount = budgetItem.second,
                    onAmountChange = {
                        if(it == "" || it[it.length-1].isDigit() ||  it[it.length-1] == '.' )
                            if(it.contains(".")){
                                val decimalIndex = it.indexOf(".")
                                val decimalSequence = it.subSequence(decimalIndex+1, it.length)
                                if(decimalSequence.length > 2){
                                    selectedCategoryList[index] = budgetItem.copy(second = it.subSequence(0, decimalIndex+3).toString())
                                } else {
                                    selectedCategoryList[index] = budgetItem.copy(second = it)
                                }
                            }
                            else
                                if(budgetItem.second == "0" && it.isNotEmpty())
                                    selectedCategoryList[index] = budgetItem.copy(second = it[it.length-1].toString())
                                else
                                    selectedCategoryList[index] = budgetItem.copy(second = it)
                    },
                    isNewBudgetCategoryAdded = isNewBudgetCategoryAdded,
                    context = context,
                    currency = currency
                )
                Spacer(Modifier.height(8.dp))
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            )
            {
                Text(
                    text = "No Category Selected",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                )
            }
        }

    }
}

@Composable
private fun BudgetCategoryItem(
    modifier: Modifier = Modifier,
    category: Category,
    amount: String,
    currency: Currency,
    isNewBudgetCategoryAdded: Boolean,
    onAmountChange: (String) -> Unit,
    context: Context
) {

    val focusRequestor = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if(isNewBudgetCategoryAdded) focusRequestor.requestFocus()
    }

    Card(
        modifier = modifier
            .padding(2.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ){
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Row(
                modifier = Modifier
                    .weight(3f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val iconId = context.resources.getIdentifier(
                    category.icon,
                    "drawable",
                    context.packageName
                )

                val iconColor = Color(0xFF000000 + category.colorHex.toLong(16))

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconColor.copy(0.17f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(iconId),
                        contentDescription = "Confirm",
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedTextField(
                modifier = Modifier
                    .padding(0.dp)
                    .weight(2f)
                    .focusRequester(focusRequestor),
                value = amount,
                onValueChange = onAmountChange,
                placeholder = {
                    Text("0")
                },
                maxLines = 1,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(Dimens.InputFieldCornerRadius),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                textStyle = TextStyle(
                    fontSize = 18.sp
                ),
                leadingIcon = {
                    Text(
                        text = currency.symbol,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },

            )
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
private fun SelectCategorySection(
    modifier: Modifier = Modifier,
    selectedCategoryIdList: List<Long>,
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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

            items(categories) { category ->

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
                                color = if (selectedCategoryIdList.contains(category.categoryId)) MaterialTheme.colorScheme.primary else Color.Transparent,
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            }
        }
    }
}

@Composable
private fun AmountSection(
    modifier: Modifier = Modifier,
    currency: Currency,
    amount: String,
    onAmountChange: (String) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TOTAL BUDGET",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier,
                value = amount,
                onValueChange = onAmountChange,
                placeholder = {
                    Text("0.0")
                },
                maxLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(Dimens.InputFieldCornerRadius),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                textStyle = TextStyle(
                    fontSize = 22.sp
                ),
                leadingIcon = {
                    Text(
                        text = currency.symbol,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            )
        }
    }
}


@Composable
private fun BudgetDurationSelectSection(
    modifier: Modifier = Modifier,
    budgetDuration: Int,
    onBudgetDurationChange: (duration: Int) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(10.dp))
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            BudgetDurationButton(
                modifier = Modifier.weight(.5f),
                text = "Weekly",
                cornerSize = 8.dp,
                isSelected = budgetDuration == 7,
                onClick = {onBudgetDurationChange(6)}
            )
            BudgetDurationButton(
                modifier = Modifier.weight(.5f),
                text = "Monthly",
                cornerSize = 8.dp,
                isSelected = budgetDuration == 30,
                onClick = {onBudgetDurationChange(29)}
            )
            BudgetDurationButton(
                modifier = Modifier.weight(.5f),
                text = "Yearly",
                cornerSize = 8.dp,
                isSelected = budgetDuration == 365,
                onClick = {onBudgetDurationChange(364)}
            )
        }
    }
}

@Composable
private fun BudgetDurationButton(
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
            containerColor = if(isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
            contentColor = if(isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
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
private fun TopBar(
    modifier: Modifier = Modifier,
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
                painter = painterResource(R.drawable.icon_cross),
                contentDescription = "Close",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = "Set Budget",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Box(Modifier.size(42.dp))
    }
}