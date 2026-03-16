package com.oms.spendwise.features.budget

import android.content.Context
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.size.Dimension
import com.oms.spendwise.R
import com.oms.spendwise.model.entity.BudgetCategory
import com.oms.spendwise.model.entity.Category
import com.oms.spendwise.model.entity.Transaction
import com.oms.spendwise.model.enum.TransactionType
import com.oms.spendwise.ui.theme.BackgroundElevated
import com.oms.spendwise.ui.theme.Dimens
import com.oms.spendwise.ui.theme.ExpenseRed
import com.oms.spendwise.ui.theme.IncomeGreen
import com.oms.spendwise.ui.theme.PrimaryBlue
import com.oms.spendwise.ui.theme.PrimaryBlueLight
import com.oms.spendwise.ui.theme.RedButton
import com.oms.spendwise.ui.theme.TextPrimary
import com.oms.spendwise.ui.theme.TextSecondary
import com.oms.spendwise.utils.formatTime
import java.util.Currency

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
                color = TextPrimary,
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
                    color = TextSecondary.copy(alpha = 0.4f),
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
    Card(
        modifier = modifier
            .padding(2.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
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
                        tint = if(amountSpent >= amountLimit) ExpenseRed else iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
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
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            if(amountSpent >= amountLimit)
                            Icon(
                                painter = painterResource(R.drawable.icon_warning),
                                contentDescription = "exceed",
                                tint = ExpenseRed,
                                modifier = Modifier
                                    .size(12.dp)
                            )
                        }
                        Text(
                            text = "${currency.symbol}$amountSpent / ${currency.symbol}$amountLimit",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if(amountSpent < amountLimit) TextPrimary else ExpenseRed,
                            fontWeight = FontWeight.Bold
                        )

                    }

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    ) {
                        val progress = if(amountSpent <= amountLimit) {
                            amountSpent.toFloat() / amountLimit.toFloat()
                        } else {
                            1f
                        }
                        drawLine(
                            color = PrimaryBlueLight.copy(alpha = 0.2f),
                            start = Offset(0f, size.height / 2),
                            end = Offset(size.width, size.height / 2),
                            strokeWidth = 8.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        drawLine(
                            color = if(amountSpent >= amountLimit) ExpenseRed else iconColor,
                            start = Offset(0f, size.height / 2),
                            end = Offset(size.width * progress, size.height / 2),
                            strokeWidth = 8.dp.toPx(),
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
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(Dimens.CardCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "REMAINING BALANCE",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Text(
                    text = "${if(remainingBalance<0) "-" else ""}${currency.symbol}${if(remainingBalance<0) remainingBalance*(-1) else remainingBalance}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
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
                        text = "Spent: ${currency.symbol}$balanceSpent",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Text(
                        text = "Limit: ${currency.symbol}$balanceLimit",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                ) {
                    val progress = if(balanceSpent <= balanceLimit) {
                        balanceSpent.toFloat() / balanceLimit.toFloat()
                    } else {
                        1f
                    }
                    drawLine(
                        color = PrimaryBlueLight.copy(alpha = 0.2f),
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = 12.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    drawLine(
                        color = if(balanceSpent >= balanceLimit) ExpenseRed else PrimaryBlue,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width * progress, size.height / 2),
                        strokeWidth = 12.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "$percentSpent% of budget used",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundElevated,
        shape = RoundedCornerShape(22.dp),
        title = {
            Text(
                text = "Delete Budget?",
                color = TextPrimary
            )
        },
        text = {
            Text(
                text = "This action cannot be undone.",
                color = TextSecondary
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = RedButton
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text("Cancel")
            }
        }
    )
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
            color = TextPrimary
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
                    tint = TextPrimary
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = {showMenu = false},
                modifier = Modifier.background(color = Color.White)
            ) {
                DropdownMenuItem(
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.icon_create),
                            contentDescription = "edit",
                            tint = TextPrimary,
                            modifier = Modifier
                                .size(14.dp)
                        )
                    },
                    text = {
                        Text(
                            text = "Edit",
                            color = TextPrimary
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
                            tint = RedButton,
                            modifier = Modifier
                                .size(14.dp)
                        )
                    },
                    text = {
                        Text(
                            text = "Delete",
                            color = RedButton
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