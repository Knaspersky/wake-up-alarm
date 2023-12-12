package com.example.wakeUp.presentation.home

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Alarm
import androidx.compose.material.icons.twotone.AltRoute
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.wakeUp.presentation.sign_in.UserData
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Teleport
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable

@Composable
fun HomeScreen(
    userData: UserData?,
    onProfileClicked: () -> Unit
) {
    var profileClicked by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = profileClicked) {
        if(profileClicked) {
            onProfileClicked()
            profileClicked = false
        }
    }

    Row(
        modifier = Modifier
            .height(42.dp)
            .fillMaxWidth()
            .padding(end = 4.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top,
    ) {
        if(userData?.profilePictureUrl != null) {
            AsyncImage(
                model = userData.profilePictureUrl,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable { profileClicked = !profileClicked },
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier
                .width(16.dp)
                .height(16.dp)
            )
        }
    }

    val navigationBarItems = remember { NavigationBarItems.entries.toTypedArray() }
    var selectedIndex by remember { mutableIntStateOf(1) }

    Scaffold(
        modifier = Modifier
            .padding(all = 12.dp)
            .padding(top = 30.dp),
        bottomBar = {
            AnimatedNavigationBar(
                modifier = Modifier.height(64.dp),
                selectedIndex = selectedIndex,
                cornerRadius = shapeCornerRadius(cornerRadius = 34.dp),
                ballAnimation = Teleport(tween(300)),
                indentAnimation = Height(tween(300)),
                barColor = MaterialTheme.colorScheme.tertiaryContainer,
                ballColor = MaterialTheme.colorScheme.inverseOnSurface
            ) {
                navigationBarItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable { selectedIndex = item.ordinal },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(26.dp),
                            imageVector = item.icon,
                            contentDescription = "Bottom Bar Icon",
                            tint = if(selectedIndex == item.ordinal) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.inversePrimary
                        )
                    }
                }

            }
        }
    ) {

    }
}

enum class NavigationBarItems(val icon: ImageVector) {
    Travel(icon = Icons.TwoTone.AltRoute),
    Home(icon = Icons.TwoTone.Alarm),
    Info(icon = Icons.TwoTone.Info)
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}