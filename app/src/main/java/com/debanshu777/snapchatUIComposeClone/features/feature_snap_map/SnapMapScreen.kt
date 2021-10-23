package com.debanshu777.snapchatUIComposeClone.features.feature_snap_map

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.debanshu777.snapchatUIComposeClone.R
import com.debanshu777.snapchatUIComposeClone.common.utils.ThemeColors
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.MapStyleOptions
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@Preview
fun SnapMapScreen() {
    val mapView = rememberMapViewWithLifecycle()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(
                RoundedCornerShape(
                    bottomEnd = 10.dp,
                    bottomStart = 10.dp
                )
            ),
    ) {
        val context = LocalContext.current
        AndroidView(
            {mapView}
        ){
            mapView->
            CoroutineScope(Dispatchers.Main).launch {
                val map= mapView.awaitMap()
                map.uiSettings.isZoomControlsEnabled= true
                map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        context, R.raw.style_json))
            }
        }
        Box(modifier = Modifier
            .rotate(180f)
            .fillMaxWidth()
            .height(80.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                       ThemeColors.MAP_DARK_GRADIENT
                    ),
                    startY = 10f
                ),
            )
        )
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView= remember{
        MapView(context).apply {
            id=com.google.maps.android.ktx.R.id.map_frame
        }
    }
    val lifeCycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle){
        lifecycle.addObserver(lifeCycleObserver)
        onDispose {
            lifecycle.removeObserver(lifeCycleObserver)
        }
    }
    return mapView
}

@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember {
        LifecycleEventObserver{_,event ->
            when(event){
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY-> mapView.onDestroy()
                else -> throw IllegalStateException()
            }

        }
    }