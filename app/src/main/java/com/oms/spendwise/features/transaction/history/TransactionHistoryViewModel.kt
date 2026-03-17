package com.oms.spendwise.features.transaction.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.oms.spendwise.model.enum.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor() : ViewModel(){
    var transactionTypeFilter by mutableStateOf<TransactionType?>(null)
    var searchVisibility by mutableStateOf(false)
    var searchText by mutableStateOf("")

    val onTransactionTypeFilterChange = {transactionTypeFilter: TransactionType? ->
        this.transactionTypeFilter = transactionTypeFilter
    }

    val onSearchVisibilityChange = {searchVisibility: Boolean ->
        this.searchVisibility = searchVisibility
    }

    val onSearchTextChange = {text: String ->
        this.searchText = text
    }

    fun reset(){
        transactionTypeFilter = null
        searchVisibility = false
        searchText = ""
    }
}