package com.dastan.videoplayer

import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dastan.videoplayer.data.model.Screen
import com.dastan.videoplayer.data.model.Video
import com.dastan.videoplayer.domain.VideoCollectionViewModel
import com.dastan.videoplayer.domain.VideoPlayerViewModel
import com.dastan.videoplayer.ui.screens.VideoCollectionScreen
import com.dastan.videoplayer.ui.screens.VideoPlayerScreen
import com.dastan.videoplayer.domain.WifiViewModel
import com.dastan.videoplayer.ui.theme.VideoPlayerTheme
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var wifiReceiver: WifiBroadcastReceiver
    private val wifiViewModel: WifiViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wifiReceiver = WifiBroadcastReceiver(){isConnected->
            wifiViewModel.updateWifiState(isConnected)
        }
        setContent {
            VideoPlayerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MyApp(wifiViewModel)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
        registerReceiver(wifiReceiver, intentFilter)
    }
    override fun onPause() {
        super.onPause()
        unregisterReceiver(wifiReceiver)
    }
}

@Composable
fun MyApp(wifiViewModel: WifiViewModel) {
    val navController = rememberNavController()
    val videoCollectionViewModel: VideoCollectionViewModel = viewModel()
    val videoPlayerViewModel = hiltViewModel<VideoPlayerViewModel>()
    val wifiState by wifiViewModel.wifiState.collectAsState()
    NavHost(navController = navController, startDestination = Screen.VideoCollectionScreen.route) {
        composable(Screen.VideoCollectionScreen.route) {
            VideoCollectionScreen(navController, videoCollectionViewModel)
        }
        composable(
            route = Screen.VideoPlayerScreen.route,
            arguments = listOf(navArgument("video") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            val videoJson = navBackStackEntry.arguments?.getString("video")
            val video = Gson().fromJson(videoJson, Video::class.java)
            VideoPlayerScreen(navController, videoPlayerViewModel, video, wifiState)
        }
    }
}
