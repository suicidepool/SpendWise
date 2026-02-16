package com.oms.spendwise.data.repository

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.oms.spendwise.data.local.dao.CategoryDao
import com.oms.spendwise.model.entity.Category
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
){

    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }

    suspend fun insertAll(categories: SnapshotStateList<Category>){
        categoryDao.insertAll(categories)
    }

    suspend fun update(category: Category){
        categoryDao.update(category)
    }

    suspend fun delete(category: Category){
        categoryDao.delete(category)
    }

    suspend fun getCategoriesByType(type: String): SnapshotStateList<Category>{
        return categoryDao.getCategoriesByType(type).toMutableStateList()
    }

    suspend fun getCategories() : SnapshotStateList<Category>{
        return categoryDao.getCategories().toMutableStateList()
    }
}