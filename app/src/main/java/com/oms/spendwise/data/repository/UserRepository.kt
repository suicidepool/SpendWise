package com.oms.spendwise.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import com.oms.spendwise.data.local.dao.UserDao
import com.oms.spendwise.model.entity.User
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.internal.http2.Http2Connection
import java.io.File
import java.io.FileOutputStream
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.Throws

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    @param:ApplicationContext private val context: Context
) {
    suspend fun getUser(): User{
        return userDao.getUser() ?: throw Exception("user not registered")
    }

    suspend fun addUser(
        name: String,
        profilePic: Uri?,
        currency: String,
        weekStart: DayOfWeek,
        dateOfBirth: LocalDate
    ) {
        val currentUser = userDao.getUser()

        if(currentUser == null){
            var compressedProfilePic:Uri? = null
            profilePic?.let {
                val profilePicBitmap = uriToBitmap(profilePic)
                compressedProfilePic = compressAndSaveImage(profilePicBitmap)
            }
            userDao.insertUser(
                User(
                    name = name,
                    profilePic = compressedProfilePic?.toString() ?: "",
                    currency = currency,
                    weekStart = weekStart,
                    dateOfBirth = dateOfBirth,
                    createdAt = LocalDateTime.now()
                )
            )
            return
        }

        throw Exception("user already registered")
    }

    suspend fun updateUser(
        name: String,
        profilePic: Uri?,
        currency: String,
        weekStart: DayOfWeek,
        dateOfBirth: LocalDate
    ){
        val currentUser = userDao.getUser()

        if(currentUser != null){
            var compressedProfilePic:Uri? = null
            profilePic?.let {
                val profilePicBitmap = uriToBitmap(profilePic)
                compressedProfilePic = compressAndSaveImage(profilePicBitmap)
            }
            userDao.updateUser(
                User(
                    userId = currentUser.userId,
                    name = name,
                    profilePic = compressedProfilePic?.toString() ?: "",
                    currency = currency,
                    weekStart = weekStart,
                    dateOfBirth = dateOfBirth,
                    createdAt = LocalDateTime.now()
                )
            )
            return
        }

        throw Exception("user not registered")
    }

    suspend fun deleteUser(user: User){
        userDao.deleteUser(user)
    }

    private fun uriToBitmap(uri:Uri): Bitmap{
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    private fun compressAndSaveImage(bitmap:Bitmap): Uri{
        val fileName = "profile_image.jpg"
        val file = File(context.filesDir, fileName)

        val outputStream = FileOutputStream(file)

        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            80,
            outputStream
        )

        outputStream.flush()
        outputStream.close()

        return file.toUri()
    }
}