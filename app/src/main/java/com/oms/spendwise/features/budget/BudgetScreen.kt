package com.oms.spendwise.features.budget

import android.content.Context
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oms.spendwise.R
import com.oms.spendwise.features.components.DeleteDialog
import com.oms.spendwise.model.entity.BudgetCategory
import com.oms.spendwise.model.entity.Category
import com.oms.spendwise.model.entity.Transaction
import com.oms.spendwise.ui.theme.BackgroundElevated
import com.oms.spendwise.ui.theme.Dimens
import com.oms.spendwise.ui.theme.ExpenseRed
import com.oms.spendwise.ui.theme.PrimaryBlue
import com.oms.spendwise.ui.theme.PrimaryBlueLight
import com.oms.spendwise.ui.theme.AlertRed
import com.oms.spendwise.ui.theme.ChartBlue
import com.oms.spendwise.ui.theme.TextPrimary
import com.oms.spendwise.ui.theme.TextSecondary
import com.oms.spendwise.utils.AmountFormatter
import kotlinx.coroutines.delay
import java.util.Currency
import kotlin.math.round

@Composable
fun BudgetScreen(
    modifier: Modifier = Modifier,
    currency: Currency,
    budgetVM: BudgetViewModel,
    transactions: List<Transaction>,
    categories: List<Category>,
    openSetBudgetScreen: () -> Unit,
    context: Context
) {
    var title by remember { mutableStateOf("Budget")}
    var showDeleteDialog by remember { mutableStateOf(false)}
    val amountSpent by remember { mutableStateOf(budgetVM.getAmountSpent(transactions)) }
    LaunchedEffect(budgetVM.budget) {
        if(budgetVM.budget == null) title = "Budget"
        else{
            val days = budgetVM.getDays(budgetVM.budget!!.startDate, budgetVM.budget!!.endDate)
            title = when (days) {
                7 -> "Weekly Budget"
                30 -> "Monthly Budget"
                365 -> "Yearly Budget"
                else -> "Budget"
            }
        }
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                title = title,
                onDeleteBudget = {
                    showDeleteDialog = true
                },
                onEditBudget = openSetBudgetScreen,
                isBudgetAdded = budgetVM.budget != null
            )
        }
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            if(budgetVM.budget == null){
                AddBudgetSection(
                    onAddClick = openSetBudgetScreen
                )
            }

            budgetVM.budget?.let{ budget ->
                BudgetCard(
                    modifier = Modifier
                        .padding(
                            horizontal = Dimens.HorizontalScreenPadding,
                            vertical = 4.dp
                        ),
                    currency = currency,
                    balanceLimit = budget.amount,
                    balanceSpent = amountSpent,
                    remainingBalance = budget.amount - amountSpent,
                    percentSpent = budgetVM.getPercentage(amountSpent, budget.amount)
                )
                Spacer(Modifier.height(22.dp))
                CategoryBudgetSection(
                    modifier = Modifier
                        .padding(horizontal = Dimens.HorizontalScreenPadding),
                    budgetVM = budgetVM,
                    transactions = transactions,
                    budgetCategories = budgetVM.budgetCategories,
                    context = context,
                    currency = currency,
                    categories = categories
                )
            }

            if(showDeleteDialog){
                DeleteDialog(
                    title = "Delete Budget?",
                    description = "This action cannot be undone.",
                    onConfirm = {
                        budgetVM.deleteBudget()
                        showDeleteDialog = false
                    },
                    onDismiss = {
                        showDeleteDialog = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoryBudgetSection(
    modifier: Modifier = Modifier,
    budgetCategories: List<BudgetCategory>,
    transactions: List<Transaction>,
    categories: List<Category>,
    budgetVM: BudgetViewModel,
    context: Context,
    currency: Currency
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Category Budgets",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(Modifier.height(12.dp))

        if(budgetCategories.isEmpty()){
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Not Category Budget has set",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold
                )
            }
        } else{
            budgetCategories.forEach { budgetCategory ->
                val balanceSpent = budgetVM.getAmountSpent(
                    categoryId = budgetCategory.categoryId,
                    transactions = transactions
                )
                BudgetCategoryItem(
                    currency = currency,
                    amountLimit = budgetCategory.amountLimit,
                    amountSpent = balanceSpent,
                    category = categories.findLast { it.categoryId == budgetCategory.categoryId }!!,
                    context = context,
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(110.dp))
    }
}

@Composable
private fun BudgetCategoryItem(
    modifier: Modifier = Modifier,
    amountSpent: Double,
    amountLimit: Double,
    category: Category,
    context: Context,
    currency: Currency
) {
    val amountSpentFormattedString = remember(amountSpent) { AmountFormatter.formatDecimal(amountSpent) }
    val amountLimitFormattedString = remember(amountLimit) {AmountFormatter.formatDecimal(amountLimit)}
    var animationPlayed by remember { mutableStateOf(false) }
    var isAnimationCompleted by remember { mutableStateOf(false) }
    val progress = if(amountSpent < amountLimit) {
        amountSpent.toFloat() / amountLimit.toFloat()
    } else {
        1f
    }
    val animationProgress by animateFloatAsState(
        targetValue = if(animationPlayed) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 0,
            easing = FastOutSlowInEasing
        ),
        label = "categoryItemAnimation",
        finishedListener = {
            isAnimationCompleted = true
        }
    )
    LaunchedEffect(Unit) {
        delay(200)
        animationPlayed = true
    }
    Card(
        modifier = modifier
            .padding(1.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp),
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val iconId = remember {
                    context.resources.getIdentifier(
                        category.icon,
                        "drawable",
                        context.packageName
                    )
                }

                val iconColor = remember {
                    Color(0xFF000000 + category.colorHex.toLong(16))
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if(amountSpent < amountLimit) iconColor.copy(0.17f) else MaterialTheme.colorScheme.error.copy(0.17f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(iconId),
                        contentDescription = "Confirm",
                        tint = if(amountSpent >= amountLimit) MaterialTheme.colorScheme.error else iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            if(amountSpent >= amountLimit)
                            Icon(
                                painter = painterResource(R.drawable.icon_warning),
                                contentDescription = "exceed",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .size(12.dp)
                            )
                        }
                        Text(
                            text = "${currency.symbol}${if(isAnimationCompleted) amountSpentFormattedString else (amountSpent * animationProgress).toInt()} / ${currency.symbol}${amountLimitFormattedString}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if(amountSpent < amountLimit) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )

                    }

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .padding(horizontal = 3.dp)
                    ) {
                        drawLine(
                            color = PrimaryBlueLight.copy(alpha = 0.2f),
                            start = Offset(0f, size.height / 2),
                            end = Offset(size.width, size.height / 2),
                            strokeWidth = 6.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        if(progress != 0f)
                        drawLine(
                            color = if(amountSpent >= amountLimit) AlertRed else iconColor,
                            start = Offset(0f, size.height / 2),
                            end = Offset(size.width * (progress * animationProgress), size.height / 2),
                            strokeWidth = 6.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                    }
                }
            }
        }
    }

}

@Composable
private fun BudgetCard(
    modifier: Modifier = Modifier,
    currency: Currency,
    balanceLimit: Double,
    balanceSpent: Double,
    remainingBalance: Double,
    percentSpent: Double
) {
    val remainingBalanceMod = if(remainingBalance < 0) remainingBalance * (-1) else remainingBalance
    val amountSpentFormattedString = remember(balanceSpent) { AmountFormatter.formatAmount(balanceSpent) }
    val amountLimitFormattedString = remember(balanceLimit) {AmountFormatter.formatAmount(balanceLimit)}
    val remainingBalanceFormattedString = remember(balanceLimit) {AmountFormatter.formatAmount(remainingBalanceMod)}
    var animationPlayed by remember { mutableStateOf(false) }
    var isAnimationCompleted by remember { mutableStateOf(false) }
    val progress = if(balanceSpent < balanceLimit) {
        balanceSpent.toFloat() / balanceLimit.toFloat()
    } else {
        1f
    }
    val animationProgress by animateFloatAsState(
        targetValue = if(animationPlayed) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = 0,
            easing = FastOutSlowInEasing
        ),
        label = "categoryItemAnimation",
        finishedListener = {
            isAnimationCompleted = true
        }
    )
    LaunchedEffect(Unit) {
        delay(20)
        animationPlayed = true
    }
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(Dimens.CardCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "REMAINING BALANCE",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${if(remainingBalance<0) "-" else ""}${currency.symbol}${if(isAnimationCompleted) remainingBalanceFormattedString else round((remainingBalanceMod * animationProgress) * 100)/100}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(18.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Spent: ${currency.symbol}${if(isAnimationCompleted) amountSpentFormattedString else round((balanceSpent * animationProgress) * 100)/100}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Limit: ${currency.symbol}$amountLimitFormattedString",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .padding(horizontal = 6.dp)
                ) {
                    drawLine(
                        color = PrimaryBlueLight.copy(alpha = 0.2f),
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = 12.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    if(progress != 0f)
                    drawLine(
                        color = if(balanceSpent >= balanceLimit) AlertRed else ChartBlue,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width * (progress * animationProgress), size.height / 2),
                        strokeWidth = 12.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "${if(isAnimationCompleted) percentSpent else round((percentSpent * animationProgress) * 100)/100}% of budget used",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}


@Composable
private fun AddBudgetSection(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = modifier,
            onClick = onAddClick
        ) {
            Text(
                text = "Add Budget",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    title: String,
    isBudgetAdded: Boolean,
    onDeleteBudget: () -> Unit,
    onEditBudget: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if(isBudgetAdded) Arrangement.SpaceBetween else Arrangement.Center
    ) {
        if(isBudgetAdded)
            Box(Modifier.size(42.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )

        if(isBudgetAdded)
        Box{
            IconButton(
                onClick = {showMenu = true}
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_menu_dots),
                    contentDescription = "Close",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = {showMenu = false},
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
            ) {
                DropdownMenuItem(
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.icon_create),
                            contentDescription = "edit",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(14.dp)
                        )
                    },
                    text = {
                        Text(
                            text = "Edit",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        showMenu = false
                        onEditBudget()
                    }
                )

                DropdownMenuItem(
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.icon_trashbin),
                            contentDescription = "delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .size(14.dp)
                        )
                    },
                    text = {
                        Text(
                            text = "Delete",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = {
                        showMenu = false
                        onDeleteBudget()
                    }
                )
            }
        }
    }
}