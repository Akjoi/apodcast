package com.example.apodcast

import android.util.Log
import android.widget.SeekBar
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.apodcast.media.LOG_TAG
import com.example.apodcast.ui.player.PlayerControllerViewModel

@Composable
fun PlayerControllerView(viewModel: PlayerControllerViewModel) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val curPos = viewModel.curPos.collectAsState()
    val bufPos = viewModel.bufPos.collectAsState()
    val duration = remember {mutableStateOf((267 *  1E3).toLong())}

    Column() {
        TextButton(onClick = { viewModel.changePlayerState() }) {
            Text(text = if (isPlaying) "Стоп" else "Играть")
        }
        Text(text = PlayerControllerViewModel.timestampToMSS(curPos.value))
        Text(text = PlayerControllerViewModel.timestampToMSS(bufPos.value))
        Seekbar(
            position = curPos,
            duration = duration,
            buffer = bufPos,
            onDragEnd = { viewModel.seekTo(it)},
        )
    }


}

@Composable
fun Seekbar(
    modifier: Modifier = Modifier,
    position: State<Long>,
    duration: State<Long>,
    buffer: State<Long>,
    onDragEnd: (progress: Long) -> Unit,
    progressColor: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = Color.Gray,
    bufferColor: Color = Color.Green,
    sliderColor: Color = MaterialTheme.colors.primary,
    progressLineHeight: Dp = 4.dp,
    sliderWidth: Dp = 20.dp,
    sliderHeight: Dp = 12.dp
) {

    val isDragging = remember { mutableStateOf(false) }
    val dragProgress = remember { mutableStateOf(0f) }

    val progressLineHeightPx = with(LocalDensity.current) { progressLineHeight.toPx() }
    val sliderWidthPx = with(LocalDensity.current) { sliderWidth.toPx() }
    val sliderHeightPx = with(LocalDensity.current) { sliderHeight.toPx() }
    val sliderSize = Size(sliderWidthPx, sliderHeightPx)
    val sliderSizeOnDragging = Size(sliderWidthPx * 1.5f, sliderHeightPx * 1.5f)

    Box(
        modifier = modifier
            .height(64.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            val p = it.x / (size.width - sliderWidthPx)
                            dragProgress.value = p
                            isDragging.value = true
                        },
                        onDragEnd = {
                            val newPosition = (dragProgress.value * duration.value).toLong()
                            onDragEnd(newPosition)
                            isDragging.value = false
                        },
                        onHorizontalDrag = { change: PointerInputChange, dragAmount ->
                            change.consume()
                            //calculate progress from 0.0f to 1.0f
                            val newProgress = dragAmount / (size.width - sliderWidthPx)
                            dragProgress.value = (dragProgress.value + newProgress).roundTo1()
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures {
                        val newProgress = it.x / (size.width - sliderWidthPx)
                        dragProgress.value = newProgress.roundTo1()
                        val newPosition = (dragProgress.value * duration.value).toLong()
                        onDragEnd(newPosition)
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val progressFloat = (position.value.toDouble() / duration.value.toDouble()).toFloat()
            val offsetX = (canvasWidth - sliderSize.width) * progressFloat
            val bufferX =  (canvasWidth - sliderSize.width) * (buffer.value.toDouble() / duration.value.toDouble()).toFloat()

            //progress line
            drawLine(
                start = Offset(x = 0f, y = canvasHeight / 2),
                end = Offset(x = offsetX, y = canvasHeight / 2),
                color = progressColor,
                strokeWidth = progressLineHeightPx
            )

            //background
            drawLine(
                start = Offset(x = offsetX, y = canvasHeight / 2),
                end = Offset(x = canvasWidth, y = canvasHeight / 2),
                color = backgroundColor,
                strokeWidth = progressLineHeightPx
            )

            drawLine(
                start = Offset(offsetX, y = canvasHeight / 2),
                end = Offset(x = bufferX, y = canvasHeight / 2),
                color = bufferColor,
                strokeWidth = progressLineHeightPx
            )

            val p = if (isDragging.value) {
                dragProgress.value
            } else {
                progressFloat
            }

            val sliderOffsetX = (canvasWidth - sliderSize.width) * p

            //increase slider size on touch
            val adaptiveSliderSize = if (isDragging.value) sliderSizeOnDragging else sliderSize

            //slider
            drawRoundRect(
                color = sliderColor,
                topLeft = Offset(x = sliderOffsetX, y = canvasHeight / 2 - adaptiveSliderSize.height / 2),
                size = adaptiveSliderSize,
                cornerRadius = CornerRadius(12f, 12f)
            )

        }
    }

}

fun Float.roundTo1() : Float{
    return  when (this) {
        in (0f..1f) -> this
        in (Float.NEGATIVE_INFINITY..0f) -> 0f
        else -> 1f
    }
}


//@Composable
//fun SeekBar(
//    strokeWidth: Float = 32f
//) {
//    Box(modifier = Modifier
//        .fillMaxWidth()
//        .height(100.dp)
//        .padding(4.dp, 8.dp)) {
//        Canvas(
//            Modifier
//                .fillMaxSize()
//                .pointerInput(Unit) {
//                    detectHorizontalDragGestures(
//                        onDragStart = {
//                            Log.i(LOG_TAG, it.toString())
//                        },
//                        onDragEnd = {
//                            Log.i(LOG_TAG, "Drag end")
//                        },
//                        onHorizontalDrag = { change, dragAmount ->
//                            change.consume()
//                            val newProgress = dragAmount / size.width
//
//                            Log.i(LOG_TAG, dragAmount.toString())
//                        }
//                    )
//                }) {
//            Log.i(LOG_TAG, size.toString())
//
//            drawLine(
//                color = Color.Gray,
//                start = Offset(0f, 0f),
//                end = Offset(size.width, 0f),
//                strokeWidth = strokeWidth
//            )
//            drawLine(
//                color = Color.Red,
//                start = Offset(0f, 0f),
//                end = Offset(size.width / 2f, 0f),
//                strokeWidth = strokeWidth
//            )
//            drawRoundRect(
//                size = Size(20f, 40f),
//                color = Color.Green
//            )
//        }
//    }
//
//}
