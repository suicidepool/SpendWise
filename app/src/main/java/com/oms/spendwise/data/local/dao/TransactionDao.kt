package com.oms.spendwise.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.oms.spendwise.model.entity.Transaction
import java.time.LocalDate
import java.time.LocalDateTime


@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM `transaction`")
    suspend fun deleteAllTransactions()

    @Query("SELECT * FROM `transaction` ORDER BY transactionDateTime DESC")
    suspend fun getAllTransactions(): List<Transaction>

    @Query("SELECT * FROM `transaction` WHERE transactionDateTime = :date || '%'")
    suspend fun getAllTransactions(date: LocalDate): List<Transaction>

    @Query("SELECT * FROM `transaction` WHERE transactionId = :id")
    suspend fun getAllTransactions(id: Long): Transaction
}