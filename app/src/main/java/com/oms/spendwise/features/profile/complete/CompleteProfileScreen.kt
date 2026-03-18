package com.oms.spendwise.features.profile.complete

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.oms.spendwise.R
import com.oms.spendwise.features.profile.ProfileViewModel
import com.oms.spendwise.ui.theme.Dimens
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Currency

@Composable
fun CompleteProfileScreen(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel,
    onContinue: () -> Unit,
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


    Scaffold(
        modifier = modifier
            .padding(horizontal = Dimens.HorizontalScreenPadding),
        topBar = {
            TopBar(
                modifier = Modifier.statusBarsPadding()
                    .padding(top = 22.dp),
                onBack = {}
            )
        },
        bottomBar = {
            BottomBar{
                if(name.isNotEmpty() && dob != null && currency != null){
                    profileViewModel.addUser(
                        name = name,
                        profilePic = profileImage,
                        currency = currency.currencyCode,
                        weekStart = DayOfWeek.SUNDAY,
                        dateOfBirth = dob!!
                    )
                    onContinue()
                }
                else
                    Toast.makeText(context,"Please Complete all Fields 🥺", Toast.LENGTH_SHORT).show()
            }
        }
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Spacer(modifier.height(34.dp))
            TitlePart()
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
    ) {
            Icon(
                painter = painterResource(R.drawable.icon_back),
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clickable(
                        enabled = true,
                        onClick = onBack
                    )
                    .padding(PaddingValues(0.dp)),
                tint = MaterialTheme.colorScheme.primary
            )
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    onContinue: () -> Unit
){
    Button(
        onClick = onContinue,
        shape = RoundedCornerShape(Dimens.ButtonCornerRadius),
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = "Continue",
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
private fun TitlePart(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = "Complete Profile",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Set up your profile to get started",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfoInputSection(
    modifier: Modifier = Modifier,
    profileImage: Uri?,
    name: String,
    dob: String,
    currency: Currency,
    currencyList: List<Currency>,
    onNameChange: (String) -> Unit,
    onDobChange: (LocalDate) -> Unit,
    onCurrencyChange: (Currency) -> Unit,
    onProfileImageChange: (Uri?) -> Unit,
    profileViewModel: ProfileViewModel
){

    var showDatePicker by remember { mutableStateOf(false) }
    var showCurrencyDropdown by remember { mutableStateOf(false) }
    var showModalBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val toggleDatePicker = {
        showDatePicker = !showDatePicker
    }
    val datePickerState = rememberDatePickerState()
    var uri by remember { mutableStateOf<Uri?>(null) }
    val cropLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract()
    ) { result ->
        if(result.isSuccessful){
            result.uriContent?.let{onProfileImageChange(it)}
        }
    }
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            cropLauncher.launch(
                CropImageContractOptions(
                    uri = uri,
                    cropImageOptions = CropImageOptions().apply {
                        aspectRatioX = 1
                        aspectRatioY = 1
                        fixAspectRatio = true
                        cropShape = CropImageView.CropShape.OVAL
                        guidelines = CropImageView.Guidelines.ON
                        cropMenuCropButtonTitle = "Done"
                    }
                )
            )
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if(success){
            uri?.let{
                cropLauncher.launch(
                    CropImageContractOptions(
                        uri = it,
                        cropImageOptions = CropImageOptions().apply {
                            aspectRatioX = 1
                            aspectRatioY = 1
                            fixAspectRatio = true
                            cropShape = CropImageView.CropShape.OVAL
                            guidelines = CropImageView.Guidelines.ON
                            cropMenuCropButtonTitle = "Done"
                        }
                    )
                )
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if(granted){
            uri?.let{cameraLauncher.launch(it)}
        }
    }


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //image input
        Box(
            modifier = Modifier
                .size(100.dp),
            contentAlignment = Alignment.BottomEnd
        ){
            if(profileImage != null){
                AsyncImage(
                    model = profileImage,
                    contentDescription = "profile picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else{
                Image(
                    painter = painterResource(R.drawable.image_empty_profile),
                    contentDescription = "profile picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            FilledIconButton(
                onClick = {
                    showModalBottomSheet = true
                },
                colors = IconButtonDefaults.filledIconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_create),
                    contentDescription = null,
                    modifier = Modifier
                        .size(12.dp)
                )
            }
        }

        if(showModalBottomSheet){
            ModalBottomSheet(
                onDismissRequest = {showModalBottomSheet = false},
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = Dimens.HorizontalScreenPadding,
                            end = Dimens.HorizontalScreenPadding,
                            bottom = 22.dp
                        ),
                ) {
                    Text(
                        text = "Choose Option",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(12.dp))
                    profileImage?.let{
                        Row(
                            modifier = Modifier.clickable(
                                onClick = {
                                    onProfileImageChange(null)
                                    showModalBottomSheet = false
                                }
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.icon_cross),
                                contentDescription = "Choose from Gallery",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Remove Profile Picture"
                            )
                        }
                        Spacer(Modifier.height(22.dp))
                    }
                    Row(
                        modifier = Modifier.clickable(
                            onClick = {
                                uri = profileViewModel.createImageUri()
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                                showModalBottomSheet = false
                            }
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icon_camera),
                            contentDescription = "Take Photo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Take Photo"
                        )
                    }
                    Spacer(Modifier.height(22.dp))
                    Row(
                        modifier = Modifier.clickable(
                            onClick = {
                                imageLauncher.launch("image/*")
                                showModalBottomSheet = false
                            }
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icon_picture),
                            contentDescription = "Choose from Gallery",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Choose from Gallery"
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        //name input
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Name",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp)
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = name,
                onValueChange = onNameChange,
                placeholder = {
                    Text("Name")
                },
                maxLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(Dimens.InputFieldCornerRadius),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )
        }

        Spacer(Modifier.height(10.dp))

        // date Input
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Date of birth",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp)
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = dob,
                onValueChange = {},
                placeholder = {
                    Text("DD/MM/YY")
                },
                readOnly = true,
                trailingIcon = {
                    IconButton(
                        onClick = toggleDatePicker
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icon_calendar),
                            contentDescription = "date of birth",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                maxLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(Dimens.InputFieldCornerRadius),
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = toggleDatePicker,
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { mills ->
                            val date = Instant.ofEpochMilli(mills)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDobChange(date)
                        }
                        toggleDatePicker()
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = toggleDatePicker) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }

        }

        Spacer(Modifier.height(10.dp))

        //currency input
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Currency",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp)
            )

            ExposedDropdownMenuBox(
                expanded = showCurrencyDropdown,
                onExpandedChange = {showCurrencyDropdown = !showCurrencyDropdown}
            ) {
                OutlinedTextField(
                    value = "${currency.symbol} ${currency.currencyCode}",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCurrencyDropdown)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(Dimens.InputFieldCornerRadius),
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = showCurrencyDropdown,
                    onDismissRequest = {showCurrencyDropdown = false },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    currencyList.forEach {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "${it.symbol} ${it.currencyCode}",
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onCurrencyChange(it)
                                showCurrencyDropdown = false
                            }
                        )
                    }
                }
            }
        }

    }
}
