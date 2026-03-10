package com.oms.spendwise.features.profile.edit

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.oms.spendwise.R
import com.oms.spendwise.features.profile.ProfileViewModel
import com.oms.spendwise.features.profile.complete.ProfileInfoInputSection
import com.oms.spendwise.ui.theme.BackgroundPrimary
import com.oms.spendwise.ui.theme.Dimens
import com.oms.spendwise.ui.theme.InputFieldContainer
import com.oms.spendwise.ui.theme.PrimaryBlue
import com.oms.spendwise.ui.theme.PrimaryBlueLight
import com.oms.spendwise.ui.theme.TextPrimary
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Currency
import kotlin.collections.forEach

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel,
    onSave: () -> Unit,
    onBack: () -> Unit,
    context: Context
){
    var profileImage by remember { mutableStateOf<Uri?>(null) }
    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf<LocalDate?>(null) }
    var currency by remember { mutableStateOf(Currency.getInstance("INR")) }
    val formatDate = { dob:LocalDate? ->
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        dob?.format(formatter) ?: ""
    }

    if(!profileViewModel.isLoading){
        profileViewModel.user?.let { user ->
            profileImage = if(user.profilePic.isNotEmpty()) user.profilePic.toUri() else null
            name = user.name
            dob = user.dateOfBirth
            currency = Currency.getInstance(user.currency)
        }
    }


    Scaffold(
        modifier = modifier
            .padding(horizontal = Dimens.HorizontalScreenPadding),
        topBar = {
            TopBar(
                modifier = Modifier
                    .statusBarsPadding(),
                onBack = onBack
            )
        },
        bottomBar = {
            BottomBar{
                if(name.isNotEmpty() && dob != null && currency != null)
                    profileViewModel.updateUser(
                        name = name,
                        profilePic = profileImage,
                        currency = currency.currencyCode,
                        weekStart = DayOfWeek.SUNDAY,
                        dateOfBirth = dob!!
                    )
                else
                    Toast.makeText(context,"Please Complete all Fields 🥺", Toast.LENGTH_SHORT).show()
                onSave()
            }
        }
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Spacer(modifier.height(28.dp))
            ProfileInfoInputSection(
                profileImage = profileImage,
                name = name,
                dob = formatDate(dob),
                currency = currency,
                currencyList = profileViewModel.currencyList,
                onNameChange = {name = it},
                onDobChange = {dob = it},
                onCurrencyChange = {currency = it},
                onProfileImageChange = {profileImage = it},
                profileViewModel = profileViewModel
            )
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack
        )
        {
            Icon(
                painter = painterResource(R.drawable.icon_back_arrow),
                contentDescription = null,
                modifier = Modifier
                    .size(26.dp),
                tint = TextPrimary
            )
        }
        Text(
            text = "Edit Profile",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.size(32.dp))
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    onSave: () -> Unit
){
    Button(
        onClick = onSave,
        shape = RoundedCornerShape(Dimens.ButtonCornerRadius),
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = "Save",
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}