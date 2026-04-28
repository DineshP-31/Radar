package com.dp.radar.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dp.radar.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadarTopBar(
    title: String,
    navController: NavController? = null,
    enableBackArrow: Boolean = false,
    actions: @Composable (() -> Unit)? = null,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
    ),
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                // Ensure text style is consistent for all titles
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.teal_700),
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
            )
        },
        modifier = modifier,
        colors = colors,
        // The navigation icon is conditional based on the presence of NavController
        navigationIcon = {
            if (enableBackArrow && navController != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = colorResource(R.color.teal_700)
                    )
                }
            }
        },
        // The actions slot is set by the calling screen
        actions = {
            actions?.invoke()
        }
    )
}
