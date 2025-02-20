package com.dastan.videoplayer.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.dastan.videoplayer.R
import com.dastan.videoplayer.data.model.Video
import com.dastan.videoplayer.domain.VideoPlayerViewModel
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerScreen(
    navController: NavController,
    videoPlayerViewModel: VideoPlayerViewModel,
    video: Video,
    wifiState: Boolean
) {

    val context = LocalContext.current
    val activity = context as? Activity
    val windowInsetsController = WindowInsetsControllerCompat(activity?.window!!, activity.window.decorView)

    var hideVideo by remember { mutableStateOf(false) }
    var isPause by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }

    LaunchedEffect(video) {
        videoPlayerViewModel.updateVideo(video)
    }
    HandleFullscreenMode(isFullscreen, activity, windowInsetsController)
    BackHandler {
        hideVideo = true
        if (isFullscreen) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        }
        videoPlayerViewModel.stopVideo()
        navController.popBackStack()
    }


    Column(modifier = Modifier.fillMaxSize()) {
        if (!isFullscreen) {
            BackButton {
                hideVideo = true
                navController.popBackStack()
                videoPlayerViewModel.stopVideo()
            }
        }
        Column(modifier = Modifier.wrapContentHeight()) {
            if (!hideVideo) {
                Column(modifier = if (!isFullscreen) Modifier.height(280.dp) else Modifier.wrapContentHeight()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        VideoPlayerView(videoPlayerViewModel, isFullscreen, wifiState)
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            VideoPlayerControls(videoPlayerViewModel, isFullscreen, isPause, { isPause = !isPause }) {
                                isFullscreen = !isFullscreen
                            }
                        }
                    }
                }
                if (!isFullscreen) {
                    VideoDetailsSection(video)
                }
            }

        }


    }

}

@Composable
fun VideoDetailsSection(video: Video) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(video.title, fontSize = 16.sp, modifier = Modifier.padding(4.dp))
        Text(video.subtitle, fontSize = 14.sp, modifier = Modifier.padding(4.dp))
        Text(video.description, fontSize = 14.sp, modifier = Modifier.padding(4.dp))
    }
}


@Composable
fun BackButton(onBack: () -> Unit) {
    Row(modifier = Modifier.padding(8.dp).fillMaxWidth().height(28.dp)) {
        Icon(
            Icons.Default.Clear,
            contentDescription = "Close",
            modifier = Modifier.clickable { onBack() }
        )
    }
}

@Composable
fun HandleFullscreenMode(
    isFullscreen: Boolean,
    activity: Activity?,
    windowInsetsController: WindowInsetsControllerCompat?
) {
    LaunchedEffect(isFullscreen) {
        activity?.requestedOrientation = if (isFullscreen) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        delay(100)

        windowInsetsController?.let {
            if (isFullscreen) {
                it.hide(WindowInsetsCompat.Type.systemBars())
                it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                it.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }
}


@Composable
fun VideoPlayerView(videoPlayerViewModel: VideoPlayerViewModel, isFullscreen: Boolean, wifiState: Boolean) {
    if (wifiState) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = videoPlayerViewModel.player
                    useController = false
                }
            },
            modifier = if (isFullscreen) Modifier.fillMaxSize()
            else Modifier.fillMaxWidth().height(250.dp)
        )
    } else {
        Image(
            painterResource(R.drawable.placeholder),
            contentDescription = null,
            modifier = if (isFullscreen) Modifier.fillMaxWidth().height(320.dp)
            else Modifier.fillMaxWidth().height(250.dp)
        )
    }

}

@Composable
fun VideoPlayerControls(
    videoPlayerViewModel: VideoPlayerViewModel,
    isFullscreen: Boolean,
    isPause: Boolean,
    onPlayPauseToggle: () -> Unit,
    onFullscreenToggle: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth().height(28.dp).padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = { videoPlayerViewModel.skipBackward(10) }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Rewind 10s")
        }
        IconButton(onClick = {
            if (isPause) videoPlayerViewModel.resumeVideo()
            else videoPlayerViewModel.pauseVideo()
            onPlayPauseToggle()
        }) {
            Icon(
                painter = if (isPause) painterResource(R.drawable.play) else painterResource(R.drawable.pause),
                contentDescription = "Play/Pause"
            )
        }
        IconButton(onClick = { videoPlayerViewModel.skipForward(10) }) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Skip 10s")
        }
        IconButton(onClick = onFullscreenToggle) {
            Icon(
                painter = if (isFullscreen) painterResource(R.drawable.full_screen) else painterResource(R.drawable.fullscreen_exit_icon),
                contentDescription = "Toggle Fullscreen"
            )
        }
    }

}