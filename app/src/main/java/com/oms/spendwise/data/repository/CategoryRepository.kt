package com.oms.spendwise.data.repository

import com.oms.spendwise.data.local.dao.CategoryDao
import com.oms.spendwise.model.entity.Category
import javax.inject.Inject

class CategoryRepository @Inject  constructor(
    private val categoryDao: CategoryDao
){

    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }

    suspend fun insertAll(categories: List<Category>){
        categoryDao.insertAll(categories)
    }

    suspend fun update(category: Category){
        categoryDao.update(category)
    }

    suspend fun delete(category: Category){
        categoryDao.delete(category)
    }

    suspend fun getCategoriesByType(type: String): List<Category>{
        return categoryDao.getCategoriesByType(type)
    }

    suspend fun getCategories() : List<Category>{
        return categoryDao.getCategories()
    }
}