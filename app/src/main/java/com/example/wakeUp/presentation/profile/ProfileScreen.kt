package com.example.wakeUp.presentation.profile

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Logout
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import com.example.wakeUp.presentation.sign_in.UserData
import com.google.android.gms.location.LocationServices
import androidx.compose.runtime.remember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userData: UserData?,
    perm: String,
    onSignOut: () -> Unit,
    onBack: () -> Unit,
    onSetting: () -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
    var userLoc: Location? by remember { mutableStateOf(null) }

    var settingsClicked by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = settingsClicked) {
        if(settingsClicked) {
            onSetting()
            settingsClicked = false
        }
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        IconButton(onClick = { onBack() }) {
            Icon(
                imageVector = Icons.TwoTone.ArrowBack,
                contentDescription = "Go Back"
            )
        }
    }
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        IconButton(onClick = { settingsClicked = !settingsClicked }) {
            Icon(
                imageVector = Icons.TwoTone.Settings,
                contentDescription = "Settings"
            )
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userData?.profilePictureUrl != null) {
            AsyncImage(
                model = userData.profilePictureUrl,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (userData?.username != null) {
            Text(
                userData.username,
                textAlign = TextAlign.Center,
                fontSize = 36.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        ExtendedFloatingActionButton(
            modifier = Modifier.wrapContentSize(),
            onClick = { onSignOut() }
        ) {
            Icon(
                Icons.TwoTone.Logout,
                contentDescription = "Log Out"
            )
            Text(text = "Sign out")
        }
        Spacer(modifier = Modifier.padding(16.dp))
        Text(text = perm)
        // Retrieve user location when needed (e.g., in a button click) Should be when retrieving calendar time
        if (ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            userLoc = location
        }.addOnFailureListener { exception ->
            // Handle location retrieval failure
            Log.e("LocationError", "Error retrieving location: $exception")
        }
        Spacer(modifier = Modifier.height((-16).dp))
        Spacer(modifier = Modifier.height(16.dp))
        // Display the location
        if (userLoc != null) {
            Text(
                text = "Your Location: ${userLoc!!.latitude}, ${userLoc!!.longitude}",
                textAlign = TextAlign.Center
            )
        } else {
            Text(text = "Location not available")
        }
    }
}
