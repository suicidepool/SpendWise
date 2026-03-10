package com.oms.spendwise.features.profile.profile

import android.net.Uri
import android.util.Log
import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.oms.spendwise.R
import com.oms.spendwise.features.profile.ProfileViewModel
import com.oms.spendwise.model.entity.User
import com.oms.spendwise.ui.theme.Dimens
import com.oms.spendwise.ui.theme.PrimaryBlue
import com.oms.spendwise.ui.theme.RedButton
import com.oms.spendwise.ui.theme.TextPrimary
import com.oms.spendwise.ui.theme.TextSecondary
import com.oms.spendwise.utils.formatDate
import java.time.LocalDate
import java.util.Currency
import androidx.core.net.toUri
import com.oms.spendwise.ui.theme.BackgroundElevated

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    profileVM: ProfileViewModel,
    onEditProfileClick: () -> Unit,
    resetAllData: () -> Unit
) {
    var showResetDataDialog by remember { mutableStateOf(false) }

    profileVM.user?.let { user ->
        Scaffold(
            modifier = modifier,
            topBar = {
                TopBar()
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                UserDetailsSection(
                    modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
                    user = user
                )
                PreferencesSection(
                    modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
                    currency = Currency.getInstance(user.currency)
                )
                Spacer(Modifier.height(22.dp))
                SystemAndDataSection(
                    modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
                    onResetDataClick = {showResetDataDialog = true},
                    onEditProfileClick = onEditProfileClick
                )
                if(showResetDataDialog)
                    ClearAllDataDialog(
                        onConfirm = {
                            resetAllData()
                            showResetDataDialog = false
                        },
                        onDismiss = {
                            showResetDataDialog = false
                        }
                    )
            }
        }
    }
}

@Composable
private fun ClearAllDataDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundElevated,
        shape = RoundedCornerShape(22.dp),
        title = {
            Text(
                text = "Delete all Data?",
                color = TextPrimary
            )
        },
        text = {
            Text(
                text = "This action cannot be undone.",
                color = TextSecondary
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = RedButton
                )
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun UserDetailsSection(
    modifier: Modifier = Modifier,
    user: User
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        ProfileImage(
            profileImage = if(user.profilePic.isNotEmpty()) user.profilePic.toUri() else null
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = user.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        BirthDate(
            dob = user.dateOfBirth
        )
        Spacer(Modifier.height(36.dp))
    }
}

@Composable
private fun PreferencesSection(
    modifier: Modifier = Modifier,
    currency: Currency
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "PREFERENCES",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        PreferenceItem(
            icon = R.drawable.icon_cash,
            title = "Currency",
            description = "Your default currency",
            value = "${currency.currencyCode} (${currency.symbol})"
        )
    }
}

@Composable
private fun SystemAndDataSection(
    modifier: Modifier = Modifier,
    onResetDataClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "SYSTEM & DATA",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        Card(
            modifier = Modifier
                .padding(1.dp)
                .fillMaxWidth()
                .clickable(
                    onClick = onEditProfileClick
                ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        )
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(TextSecondary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_create),
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Change Profile Details",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary.copy(alpha = 0.8f)
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .padding(1.dp)
                .fillMaxWidth()
                .clickable(
                    onClick = onResetDataClick
                ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        )
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(RedButton.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_trashbin),
                        contentDescription = null,
                        tint = RedButton,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    Text(
                        text = "Reset All Data",
                        style = MaterialTheme.typography.bodyLarge,
                        color = RedButton,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "This action cannot be undone",
                        style = MaterialTheme.typography.labelSmall,
                        color = RedButton.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}


@Composable
private fun PreferenceItem(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    title: String,
    description: String,
    value: String,
) {
    Card(
        modifier = modifier
            .padding(1.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ){
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PrimaryBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold
            )
        }
    }

}

@Composable
private fun BirthDate(
    modifier: Modifier = Modifier,
    dob: LocalDate
) {
    Box(
        modifier = modifier
            .background(
                color = PrimaryBlue.copy(alpha = 0.1f),
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ){
        Row(
            modifier = modifier
                .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.icon_cake),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = PrimaryBlue
            )
            Text(
                text = formatDate(dob),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall,
                color = PrimaryBlue
            )
        }
    }
}

@Composable
private fun ProfileImage(
    profileImage: Uri?
) {
    profileImage?.let {
        Log.d("PROFILE_IMAGE",it.toString())
    }
    Card(
        modifier = Modifier
            .size(120.dp),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        if (profileImage != null) {
            Log.d("PROFILE_IMAGE", profileImage.toString())
            AsyncImage(
                model = profileImage,
                contentDescription = "profile picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Log.d("PROFILE_IMAGE", "default")
            Image(
                painter = painterResource(R.drawable.image_empty_profile),
                contentDescription = "profile picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}


@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(82.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}