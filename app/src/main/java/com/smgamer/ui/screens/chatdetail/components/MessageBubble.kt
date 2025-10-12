package com.smgamer.ui.screens.chatdetail.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.dp
import com.smgamer.ui.screens.chatdetail.ChatMessage
import com.smgamer.ui.screens.chatdetail.MessageStatus
import com.smgamer.ui.theme.CommonFontSizeDefault
import com.smgamer.ui.theme.CommonFontSizeMicro
import com.smgamer.ui.theme.CommonFontSizeMiddle
import com.smgamer.ui.theme.CommonPaddingDefault
import com.smgamer.ui.theme.CommonPaddingDefaultMid
import com.smgamer.ui.theme.CommonPaddingMicro
import com.smgamer.ui.theme.CommonPaddingMicroMin
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.CommonPaddingMinDefault
import com.smgamer.ui.theme.CommonPaddingNone
import com.smgamer.ui.theme.CommonPaddingTwo
import com.smgamer.ui.theme.MaxBubbleWidth
import com.smgamer.ui.theme.scaledFont
import com.smgamer.ui.theme.scaledPadding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


//data class LineLayoutInfo(
//    val lastLineWidth: Float = 0f,
//    val lineCount: Int = 1,
//    val timeWidth: Dp = 0.dp,
//    val timeHeight: Dp = 0.dp
//)

@SuppressLint("NewApi")
@Composable
fun MessageBubble(
    message: ChatMessage,
    showTail: Boolean
) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    val bubbleColor = if (message.isMine) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surfaceVariant

    val textColor = if (message.isMine) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSurfaceVariant

    val density = LocalDensity.current
    val maxBubbleWidthDp = MaxBubbleWidth
    val maxBubbleWidthPx = with(density) { maxBubbleWidthDp.toPx() }

    var lastLineWidthPx by remember { mutableStateOf(0f) }
    var lineCount by remember { mutableStateOf(1) }

    var timeContainerWidthPx by remember { mutableStateOf(CommonPaddingNone) }
    var timeContainerHeightPx by remember { mutableStateOf(CommonPaddingNone) }


    val dynamicPadding by remember(
        lastLineWidthPx,
        timeContainerWidthPx,
        timeContainerHeightPx,
        lineCount
    ) {
        derivedStateOf {
            val totalWidth = lastLineWidthPx.dp + timeContainerWidthPx
            when {
                totalWidth > (maxBubbleWidthPx.dp - 100.dp) ->
                    PaddingValues(bottom = timeContainerHeightPx)

                totalWidth <= (maxBubbleWidthPx.dp - 100.dp) && lineCount == 1 ->
                    PaddingValues(end = timeContainerWidthPx + CommonPaddingMin)

                else -> PaddingValues()
            }
        }
    }


    val bubbleShape = when {
        message.isMine && showTail -> RoundedCornerShape(
            topStart = scaledPadding(CommonPaddingMinDefault),
            topEnd = CommonPaddingNone,
            bottomEnd = scaledPadding(CommonPaddingMinDefault),
            bottomStart = scaledPadding(CommonPaddingMinDefault)
        )

        !message.isMine && showTail -> RoundedCornerShape(
            topStart = CommonPaddingNone,
            topEnd = scaledPadding(CommonPaddingMinDefault),
            bottomEnd = scaledPadding(CommonPaddingMinDefault),
            bottomStart = scaledPadding(CommonPaddingMinDefault)
        )

        else -> RoundedCornerShape(scaledPadding(CommonPaddingMinDefault))
    }

    val bubblePadding = when {
        message.isMine && !showTail -> PaddingValues(end = scaledPadding(CommonPaddingMin))
        !message.isMine && !showTail -> PaddingValues(start = scaledPadding(CommonPaddingMin))
        else -> PaddingValues()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = scaledPadding(CommonPaddingDefault),
                vertical = scaledPadding(CommonPaddingTwo)
            ),
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start
    ) {

        Row(
            modifier = Modifier.padding(bubblePadding),
            verticalAlignment = Alignment.Top
        ) {

            if (showTail && !message.isMine) {
                BubbleTail(color = bubbleColor, isMine = false)
            }

            Box(
                modifier = Modifier
                    .background(bubbleColor, bubbleShape)
                    .padding(
                        start = if (message.isMine) scaledPadding(CommonPaddingMinDefault) else scaledPadding(
                            CommonPaddingMin
                        ),
                        end = if (message.isMine) scaledPadding(CommonPaddingMin) else scaledPadding(
                            CommonPaddingMinDefault
                        ),
                        top = scaledPadding(CommonPaddingMicroMin),
                        bottom = scaledPadding(CommonPaddingMicroMin)
                    )
                    .widthIn(max = maxBubbleWidthDp)
                    .wrapContentWidth(align = if (message.isMine) Alignment.End else Alignment.Start)
            ) {

                Text(
                    text = message.text,
                    fontSize = scaledFont(CommonFontSizeMiddle),
                    color = textColor,
                    style = LocalTextStyle.current.copy(
                        fontSize = scaledFont(CommonFontSizeDefault),
                        lineHeight = scaledFont(CommonFontSizeDefault) * 1.3,
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                    modifier = Modifier
                        .padding(dynamicPadding)
                        .align(Alignment.TopStart),
                    onTextLayout = { layoutResult ->
                        lineCount = layoutResult.lineCount
                        lastLineWidthPx = if (lineCount > 0) {
                            val last = lineCount - 1
                            layoutResult.getLineRight(last) - layoutResult.getLineLeft(last)
                        } else 0f
                    }
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .onGloballyPositioned { coordinates ->
                            timeContainerWidthPx = with(density) { coordinates.size.width.toDp() }
                            timeContainerHeightPx = with(density) { coordinates.size.height.toDp() }
                        },
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = timeFormatter.format(Date(message.timestamp)),
                        fontSize = scaledFont(CommonFontSizeMicro),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(0.dp),
                        style = LocalTextStyle.current.copy(
                            lineHeight = scaledFont(CommonFontSizeMiddle),
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        )
                    )

                    if (message.isMine) {
                        Spacer(modifier = Modifier.width(scaledPadding(CommonPaddingMicro)))
                        Icon(
                            imageVector = when (message.status) {
                                MessageStatus.SENT -> Icons.Default.Check
                                MessageStatus.DELIVERED -> Icons.Default.DoneAll
                                MessageStatus.SEEN -> Icons.Default.DoneAll
                            },
                            contentDescription = null,
                            tint = when (message.status) {
                                MessageStatus.SEEN -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.outline
                            },
                            modifier = Modifier.size(scaledPadding(CommonPaddingDefaultMid))
                        )
                    }
                }
            }

            if (showTail && message.isMine) {
                BubbleTail(color = bubbleColor, isMine = true)
            }
        }
    }
}