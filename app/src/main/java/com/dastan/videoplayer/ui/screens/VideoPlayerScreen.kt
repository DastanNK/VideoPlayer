package com.dastan.videoplayer.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    var isPause by rememberSaveable { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }

    LaunchedEffect(video) {
        if (videoPlayerViewModel.currentVideo.value != video) {
            videoPlayerViewModel.updateVideo(video)
        }
        if (!isPause) {
            videoPlayerViewModel.resumeVideo()
        }
    }

    HandleFullscreenMode(isFullscreen, activity, windowInsetsController)
    BackHandler {
        exitVideoScreen(
            navController,
            videoPlayerViewModel,
            activity,
            windowInsetsController,
            isFullscreen
        ) { hideVideo = true }
    }
    ObserveOrientation { isLandscape ->
        isFullscreen = isLandscape
    }

    ObserveLifecycle(videoPlayerViewModel)

    Column(
        modifier = if (!isFullscreen) Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState()) else Modifier.fillMaxSize()
    ) {
        if (!isFullscreen) {
            BackButton {
                exitVideoScreen(navController, videoPlayerViewModel, activity, windowInsetsController, isFullscreen) {
                    hideVideo = true
                }
            }
        }
        VideoPlayerScreenContent(
            videoPlayerViewModel = videoPlayerViewModel,
            isFullscreen = isFullscreen,
            hideVideo = hideVideo,
            wifiState = wifiState,
            isPause = isPause,
            onPauseToggle = { isPause = !isPause },
            onFullscreenToggle = { isFullscreen = !isFullscreen },
            video = video,
            modifier = if (!isFullscreen) Modifier.wrapContentHeight() else Modifier.fillMaxSize()
        )


    }

}

@Composable
private fun VideoPlayerScreenContent(
    videoPlayerViewModel: VideoPlayerViewModel,
    isFullscreen: Boolean,
    hideVideo: Boolean,
    wifiState: Boolean,
    isPause: Boolean,
    onPauseToggle: () -> Unit,
    onFullscreenToggle: () -> Unit,
    video: Video,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (!hideVideo) {
            Column(modifier = modifier) {
                Box(
                    modifier = modifier
                ) {
                    VideoPlayerView(videoPlayerViewModel, isFullscreen, wifiState)

                    Column(
                        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 4.dp),
                    ) {
                        VideoPlayerControls(
                            videoPlayerViewModel,
                            isFullscreen,
                            isPause,
                            onPauseToggle,
                            onFullscreenToggle
                        )
                        VideoPlayerSeekBar(videoPlayerViewModel, isFullscreen)

                    }
                }
            }
            if (!isFullscreen) {
                VideoDetailsSection(video)
            }
        }
    }

}


@Composable
fun ObserveLifecycle(videoPlayerViewModel: VideoPlayerViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> videoPlayerViewModel.rememberPosition()
                Lifecycle.Event.ON_RESUME -> videoPlayerViewModel.restorePosition()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

private fun exitVideoScreen(
    navController: NavController,
    videoPlayerViewModel: VideoPlayerViewModel,
    activity: Activity?,
    windowInsetsController: WindowInsetsControllerCompat?,
    isFullscreen: Boolean,
    onHideVideo: () -> Unit
) {


    if (isFullscreen) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
    } else {
        onHideVideo()
        videoPlayerViewModel.stopVideo()
        videoPlayerViewModel.clearVideo()
        navController.popBackStack()
    }

}

@Composable
fun ObserveOrientation(
    onOrientationChange: (Boolean) -> Unit
) {
    val configuration = LocalConfiguration.current
    LaunchedEffect(configuration.orientation) {
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        onOrientationChange(isLandscape)
    }
}

@Composable
fun VideoDetailsSection(video: Video) {
    Column {
        Text(video.title, fontSize = 16.sp, modifier = Modifier.padding(4.dp))
        Text(video.subtitle, fontSize = 14.sp, modifier = Modifier.padding(4.dp))
        Text(video.description, fontSize = 14.sp, modifier = Modifier.padding(4.dp))
    }
}


@Composable
fun BackButton(onBack: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(28.dp)) {
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
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

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
    val duration by videoPlayerViewModel.duration.collectAsState()
    val currentPosition by videoPlayerViewModel.currentPosition.collectAsState()
    Row(
        modifier = Modifier.fillMaxWidth().height(20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(videoPlayerViewModel.formatTime(currentPosition.toLong()), fontSize = 12.sp, color = Color.White)
        IconButton(onClick = { videoPlayerViewModel.skipBackward(10) }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Rewind 10s", tint = Color.White)
        }
        IconButton(onClick = {
            if (isPause) videoPlayerViewModel.resumeVideo()
            else videoPlayerViewModel.pauseVideo()
            onPlayPauseToggle()
        }) {
            Icon(
                painter = if (isPause) painterResource(R.drawable.play) else painterResource(R.drawable.pause),
                contentDescription = "Play/Pause",
                tint = Color.White
            )
        }
        IconButton(onClick = { videoPlayerViewModel.skipForward(10) }) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Skip 10s", tint = Color.White)
        }
        IconButton(onClick = onFullscreenToggle) {
            Icon(
                painter = if (isFullscreen) painterResource(R.drawable.full_screen) else painterResource(R.drawable.fullscreen_exit_icon),
                contentDescription = "Toggle Fullscreen", tint = Color.White
            )
        }
        Text(videoPlayerViewModel.formatTime(duration.toLong()), fontSize = 12.sp, color = Color.White)

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerSeekBar(videoPlayerViewModel: VideoPlayerViewModel, isFullscreen:Boolean) {
    val duration by videoPlayerViewModel.duration.collectAsState()
    val currentPosition by videoPlayerViewModel.currentPosition.collectAsState()
    var sliderPosition by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var isSeeking by remember { mutableStateOf(false) }

    val displayPosition = if (isDragging || isSeeking) sliderPosition else currentPosition
    var sliderWidth by remember { mutableStateOf(0) }

    LaunchedEffect(currentPosition, sliderPosition, isSeeking) {
        if (isSeeking && kotlin.math.abs(currentPosition - sliderPosition) < 1f) {
            isSeeking = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (isDragging || isSeeking) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset {
                            IntOffset(
                                x = ((displayPosition / duration) * sliderWidth).toInt(),
                                y = 0
                            )
                        }
                        .background(Color.Black, RoundedCornerShape(2.dp))

                ) {
                    Text(
                        text = videoPlayerViewModel.formatTime(displayPosition.toLong()),
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            Slider(
                value = displayPosition,
                onValueChange = { newPosition ->
                    sliderPosition = newPosition
                    isDragging = true
                },
                onValueChangeFinished = {
                    videoPlayerViewModel.seekTo(sliderPosition)
                    isDragging = false
                    isSeeking = true
                },
                valueRange = 0f..duration,
                modifier = Modifier.fillMaxWidth().onGloballyPositioned { coordinates ->
                    sliderWidth = coordinates.size.width
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color.Red,
                    activeTrackColor = Color.Red,
                    inactiveTrackColor = Color.Gray.copy(alpha = 0.5f)
                ),
                thumb = {
                    val scale = animateFloatAsState(if (isDragging || isSeeking) 1.5f else 1f)
                    Box(
                        modifier = Modifier
                            .size(16.dp * scale.value)
                            .background(Color.Red, CircleShape)
                    )
                }
            )
        }
    }
}

