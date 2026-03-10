package com.oms.spendwise.features.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oms.spendwise.data.local.dao.UserDao
import com.oms.spendwise.data.repository.UserRepository
import com.oms.spendwise.model.entity.User
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Currency
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val userRepository: UserRepository,
    @param:ApplicationContext val context: Context
): ViewModel(){
    var user by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(true)

    init {
        loadUser()
    }

    val currencyList = listOf(
        Currency.getInstance("INR"),
        Currency.getInstance("USD"),
        Currency.getInstance("EUR")
    )


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
        profilePic: Uri?,
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

    fun createImageFile(): File{
        val fileName = "profile_image"
        return File.createTempFile(
            fileName,
            ".jpg",
            context.cacheDir
        )
    }

    fun createImageUri(): Uri{
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            createImageFile()
        )
    }

    fun updateUser(
        name: String,
        profilePic: Uri?,
        currency: String,
        weekStart: DayOfWeek,
        dateOfBirth: LocalDate
    ){
        viewModelScope.launch {
            userRepository.updateUser(
                name = name,
                profilePic = profilePic,
                currency = currency,
                weekStart = weekStart,
                dateOfBirth = dateOfBirth
            )
            loadUser()
        }
    }

    fun resetAllData(){
        val currentUser = user ?: return

        viewModelScope.launch {
            Log.d("USER",currentUser.toString())
            userRepository.deleteUser(currentUser)
            user = null
        }
    }
}