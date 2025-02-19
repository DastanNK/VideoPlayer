package com.dastan.videoplayer.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dastan.videoplayer.data.model.Screen
import com.dastan.videoplayer.data.model.Video
import com.dastan.videoplayer.domain.VideoCollectionViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dastan.videoplayer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun VideoCollectionScreen(navController: NavController, viewModel: VideoCollectionViewModel) {
    val result by viewModel.videoState
    val isRefreshing = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        PullToRefreshItems(
            result = result,
            isRefreshing = isRefreshing.value,
            onRefresh = { refreshVideos(scope, viewModel, isRefreshing) },
            navController = navController
        )
    }
}

private fun refreshVideos(
    scope: CoroutineScope,
    viewModel: VideoCollectionViewModel,
    isRefreshing: MutableState<Boolean>
) {
    isRefreshing.value = true
    scope.launch {
        viewModel.fetchCategories()
        delay(1000)
        isRefreshing.value = false
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshItems(
    result: List<Video>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val state = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                state = state
            )
        },
    ) {
        if (result.isEmpty()) {
            NoVideosMessage()
        } else {
            Items(result, navController)
        }
    }
}

@Composable
fun NoVideosMessage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Text("No videos available. Pull down to refresh.")
    }
}


@Composable
fun Items(results: List<Video>, navController: NavController) {
    LazyColumn {
        items(results) { result ->
            VideoEachItems(result, navController)
        }

    }
}

@Composable
fun VideoEachItems(result: Video, navController: NavController) {

    Box(
        modifier = Modifier
            .padding(8.dp).fillMaxWidth().wrapContentHeight()
            .background(color = MaterialTheme.colorScheme.background)
            .clickable {
                navController.navigate(Screen.VideoPlayerScreen.createRoute(result))
            }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VideoThumbnail(result.thumb)
            VideoInfo(result.title, result.timeline)
        }
    }
}

@Composable
fun VideoThumbnail(thumbUrl: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(thumbUrl)
            .crossfade(true)
            .error(R.drawable.resource_default)
            .placeholder(R.drawable.placeholder)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .wrapContentHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun VideoInfo(title: String, timeline: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .padding(start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontSize = 14.sp)
        Text(timeline, fontSize = 14.sp)
    }
}
