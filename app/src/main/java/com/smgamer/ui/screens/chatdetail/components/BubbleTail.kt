package com.smgamer.ui.screens.chatdetail.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import com.smgamer.ui.theme.CommonPaddingMin
import com.smgamer.ui.theme.CommonPaddingMinDefault
import com.smgamer.ui.theme.CommonPaddingTwo
import com.smgamer.ui.theme.scaledPadding


@Composable
fun BubbleTail(color: Color, isMine: Boolean) {
    val cornerRadiusPx = with(LocalDensity.current) { scaledPadding(CommonPaddingTwo).toPx() }

    Canvas(
        modifier = Modifier
            .width(scaledPadding(CommonPaddingMin))
            .height(scaledPadding(CommonPaddingMinDefault))
    ) {
        val w = size.width
        val h = size.height
        val path = Path()

        if (isMine) {
            path.moveTo(0f, 0f)
            path.lineTo(w - cornerRadiusPx, 0f)
            path.quadraticTo(w, 0f, w, cornerRadiusPx)
            path.lineTo(0f, h)
            path.close()
        } else {
            path.moveTo(w, 0f)
            path.lineTo(cornerRadiusPx, 0f)
            path.quadraticTo(0f, 0f, 0f, cornerRadiusPx)
            path.lineTo(w, h)
            path.close()
        }

        drawPath(path = path, color = color)
    }
}