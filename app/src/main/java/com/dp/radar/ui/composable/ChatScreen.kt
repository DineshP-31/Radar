package com.dp.radar.ui.composable

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dp.radar.domain.model.User
import com.dp.radar.ui.UserListIntent
import com.dp.radar.ui.components.BackHandler
import com.dp.radar.ui.components.SkeletonItemList
import com.dp.radar.ui.viewmodel.UserListViewModel

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: UserListViewModel = hiltViewModel(),
    onItemClicked: (User) -> Unit
) {
    val state by viewModel.state.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when {
            state.isLoading -> LoadingIndicator()

            state.error != null -> ErrorView(state.error!!) {
                viewModel.handleIntent(UserListIntent.RetryLoad)
            }

            state.users.isNotEmpty() -> UserListView(users = state.users, onItemClicked)

            else -> EmptyView()
        }
    }
    BackHandler()
}

@Composable
fun UserListView(users: List<User>, onItemClicked: (User) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items = users) { user ->
            UserItem(user = user, onItemClicked = onItemClicked)
        }
    }
}

@Composable
fun ErrorView(errorMessage: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Oops! Could not load data.",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text("Try Again")
        }
    }
}

@Composable
fun EmptyView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No users found.\nTry refreshing later.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun UserItem(
    user: User,
    onItemClicked: (User) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onItemClicked(user) }
            .fillMaxWidth()
    ) {
        Box {
            Log.e("URL", user.avatarUrl)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.avatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = user.username,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = user.username,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                fontSize = 16.sp,
            )

            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "chatMessage",
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
fun LoadingIndicator() {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp), // Space between items
        modifier = Modifier.fillMaxSize()
    ) {
        items(20) {
            SkeletonItemList()
        }
    }
}
