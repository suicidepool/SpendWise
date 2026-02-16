package com.oms.spendwise.features.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oms.spendwise.data.local.dao.UserDao
import com.oms.spendwise.data.repository.UserRepository
import com.oms.spendwise.model.entity.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val userRepository: UserRepository
): ViewModel(){
    var user by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(true)

    init {
        loadUser()
    }


    fun loadUser(){
        isLoading = true
        viewModelScope.launch {
            try {
                user = userRepository.getUser()
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
            }
        }
    }

    fun addUser(
        name: String,
        profilePic: String,
        currency: String,
        weekStart: DayOfWeek,
        dateOfBirth: LocalDate
    ){
        viewModelScope.launch {
            isLoading = true
            userRepository.addUser(
                name = name,
                profilePic = profilePic,
                currency = currency,
                weekStart = weekStart,
                dateOfBirth = dateOfBirth
            )
            isLoading = false
            loadUser()
        }
    }


    fun updateUserName(name: String){
        val currentUser = user ?: return

        viewModelScope.launch {
            userRepository.updateUser(currentUser.copy(name = name))
            user = currentUser.copy(name = name)
        }
    }

    fun updateUserPic(profilePic: String){
        val currentUser = user ?: return

        viewModelScope.launch {
            userRepository.updateUser(currentUser.copy(profilePic = profilePic))
            user = currentUser.copy(profilePic = profilePic)
        }
    }


    fun updateCurrency(currency: String){
        val currentUser = user ?: return

        viewModelScope.launch {
            userRepository.updateUser(currentUser.copy(currency = currency))
            user = currentUser.copy(currency = currency)
        }
    }

    fun updateWeekStartDay(day: DayOfWeek){
        val currentUser = user ?: return

        viewModelScope.launch {
            userRepository.updateUser(currentUser.copy(weekStart = day))
            user = currentUser.copy(weekStart = day)
        }
    }

    fun resetAllData(){
        val currentUser = user ?: return

        viewModelScope.launch {
            userRepository.deleteUser(currentUser)
            user = null
        }
    }
}