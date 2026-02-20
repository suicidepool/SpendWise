package com.oms.spendwise

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.util.TableInfo
import coil.compose.AsyncImage
import com.oms.spendwise.data.repository.BudgetRepository
import com.oms.spendwise.features.profile.ProfileViewModel
import com.oms.spendwise.features.profile.complete.CompleteProfileScreen
import com.oms.spendwise.features.transaction.TransactionViewModel
import com.oms.spendwise.model.enum.TransactionType
import com.oms.spendwise.ui.theme.SpendWiseTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var profileViewModel: ProfileViewModel
    lateinit var transactionViewModel: TransactionViewModel
    @Inject
    lateinit var budgetRepository: BudgetRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            profileViewModel = hiltViewModel()
            transactionViewModel = hiltViewModel()
            transactionViewModel.loadTransactions()
            val scope = rememberCoroutineScope()


//            LaunchedEffect(profileViewModel.isLoading) {
//                if(profileViewModel.isLoading == false){
//                    if(profileViewModel.user == null){
//                        Log.d("MESSAGE","user registered nahi hai")
//                        profileViewModel.addUser(
//                            name = "Rakesh",
//                            profilePic = "demo_pic",
//                            dateOfBirth = LocalDate.of(2006,1,12),
//                            weekStart = DayOfWeek.SUNDAY,
//                            currency = "INR"
//                        )
//                    } else{
//                        Log.d("MESSAGE","user  registered hai")
//                        profileViewModel.userRepository.getUser()
//                    }
//                } else{
//                    Log.d("Message","LOADING DATA")
//                }
//            }


            SpendWiseTheme {
                if(profileViewModel.user == null){
                    CompleteProfileScreen(
                        profileViewModel = profileViewModel,
                        context = this
                    )
                } else{
                    if(profileViewModel.user!!.profilePic.isNotEmpty()){
                        Log.d("hello","ji")
                        AsyncImage(
                            model = Uri.parse(profileViewModel.user!!.profilePic),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }
        }
    }
}