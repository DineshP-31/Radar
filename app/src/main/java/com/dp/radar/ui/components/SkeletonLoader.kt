package com.dp.radar.com.dp.radar.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dp.radar.R
import com.dp.radar.com.dp.radar.domain.model.User

@Composable
fun SkeletonItemList() {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .clickable {
            }) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("")
                .crossfade(true)
                .build(),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
        Spacer(Modifier.width(4.dp))

        Column {
            Image(
                painter = painterResource(id = R.drawable.ic_user_loader_second),
                contentDescription = "",
                modifier = Modifier
                    .shimmerEffect()
            )
            Spacer(Modifier.height(2.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_user_loader_second),
                contentDescription = "",
                modifier = Modifier
                    .shimmerEffect()
            )
        }
    }

}

@Composable
fun GridItemSkeleton(
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("")
                .crossfade(true)
                .build(),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_user_loader_second),
            contentDescription = "",
            modifier = Modifier
                .shimmerEffect()
        )
    }
}