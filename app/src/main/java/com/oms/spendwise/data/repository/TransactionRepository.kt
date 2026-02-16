package com.oms.spendwise.data.repository

import com.oms.spendwise.data.local.dao.TransactionDao
import com.oms.spendwise.model.entity.Transaction
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    suspend fun addTransaction(
        userId: Long,
        categoryId: Long,
        amount: Double,
        type: String,
        note: String = "",
        transactionDateTime: LocalDateTime,
        createdAt: LocalDateTime
    ){
        transactionDao.insertTransaction(
            Transaction(
                userId = userId,
                categoryId = categoryId,
                amount = amount,
                type = type,
                note = note,
                transactionDateTime = transactionDateTime,
                createdAt = createdAt
            )
        )
    }

    suspend fun updateTransaction(transaction: Transaction){
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction){
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteAllTransactions(){
        transactionDao.deleteAllTransactions()
    }

    suspend fun getAllTransactions(): List<Transaction>{
        return transactionDao.getAllTransactions()
    }

    suspend fun getAllTransactions(date: LocalDate): List<Transaction>{
        return transactionDao.getAllTransactions(date)
    }

    suspend fun getTransaction(id: Long): Transaction{
        return transactionDao.getAllTransactions(id)
    }
}