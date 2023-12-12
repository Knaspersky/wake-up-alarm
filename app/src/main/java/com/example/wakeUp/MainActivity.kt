package com.example.wakeUp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.wakeUp.ui.theme.ComposeGoogleSignInCleanArchitectureTheme
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wakeUp.presentation.home.HomeScreen
import com.example.wakeUp.presentation.profile.ProfileScreen
import com.example.wakeUp.presentation.settings.SettingsScreen
import com.example.wakeUp.presentation.sign_in.GoogleAuthUiClient
import com.example.wakeUp.presentation.sign_in.SignInScreen
import com.example.wakeUp.presentation.sign_in.SignInViewmodel
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {


    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var perm: String

    override fun onCreate(savedInstanceState: Bundle?) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        super.onCreate(savedInstanceState)

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    perm = "Fine Location Access"
                    Log.d("Main Activity Location", "Fine Location Access Granted")
                }

                permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    perm = "Coarse Location Access"
                    Log.d("Main Activity Location", "Coarse Location Access Granted")
                }

                else -> {
                    // No location access granted.
                    perm = "No Location Access"
                    Log.d("Main Activity Location", "No Location Access Granted")
                }
            }
        }
        locationPermissionRequest.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        setContent {
            var darkTheme by rememberSaveable { mutableStateOf(false) }
            ComposeGoogleSignInCleanArchitectureTheme(darkTheme = darkTheme) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "sign_in") {
                        composable("sign_in") {
                            val viewModel = viewModel<SignInViewmodel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(key1 = Unit) {
                                if(googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate("home")
                                }
                            }

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if(result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                        }
                                    }
                                }
                            )
                            
                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if(state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in Successful",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("home")
                                    viewModel.resetState()
                                }
                            }

                            SignInScreen(
                                state = state,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )
                        }
                        composable("profile") {
                            ProfileScreen(
                                userData = googleAuthUiClient.getSignedInUser(),
                                perm = perm,
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()
                                        Toast.makeText(
                                            applicationContext,
                                            "Signed out",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        navController.popBackStack()
                                        navController.navigate("sign_in")
                                    }
                                },
                                onBack = {
                                    navController.popBackStack()
                                    navController.navigate("home")
                                },
                                onSetting = { navController.navigate("settings") }
                            )
                        }
                        composable("home") {
                            HomeScreen(
                                userData = googleAuthUiClient.getSignedInUser(),
                                onProfileClicked = { navController.navigate("profile") }
                            )
                        }
                        composable("settings") {

                            SettingsScreen(
                                darkTheme = darkTheme,
                                onThemeUpdated = { darkTheme = !darkTheme },
                                onBack = { navController.navigate("profile") }
                            )
                        }
                    }
                }
            }
        }
    }
}
