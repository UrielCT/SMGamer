package com.smgamer.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


//// 🔹 Devuelve el paddingScale actual
//val currentPaddingScale: Float
//    @Composable
//    @ReadOnlyComposable
//    get() = LocalPaddingScale.current
//
//// 🔹 Función pura para escalar un Dp sin depender de composición
//fun Dp.scaledBy(scale: Float): Dp = (this.value * scale).dp
//
//// 🔹 Helper de constraints escalados
//fun ConstrainScope.linkToScaled(
//    start: ConstraintLayoutBaseScope.HorizontalAnchor? = null,
//    end: ConstraintLayoutBaseScope.HorizontalAnchor? = null,
//    top: ConstraintLayoutBaseScope.VerticalAnchor? = null,
//    bottom: ConstraintLayoutBaseScope.VerticalAnchor? = null,
//    startMargin: Dp = 0.dp,
//    endMargin: Dp = 0.dp,
//    topMargin: Dp = 0.dp,
//    bottomMargin: Dp = 0.dp,
//    horizontalBias: Float = 0.5f,
//    verticalBias: Float = 0.5f,
//    paddingScale: Float
//) {
//    // 🔹 Si hay start y end, enlazalos juntos
//    if (start != null && end != null) {
//        linkTo(
//            start = start,
//            end = end,
//            startMargin = startMargin * paddingScale,
//            endMargin = endMargin * paddingScale,
//            bias = horizontalBias
//        )
//    } else if (start != null) {
//        start(start, margin = startMargin * paddingScale)
//    } else if (end != null) {
//        end.linkTo(end, margin = endMargin * paddingScale)
//    }
//
//    // 🔹 Si hay top y bottom, enlazalos juntos
//    if (top != null && bottom != null) {
//        linkTo(
//            top = top,
//            bottom = bottom,
//            topMargin = topMargin * paddingScale,
//            bottomMargin = bottomMargin * paddingScale,
//            bias = verticalBias
//        )
//    } else if (top != null) {
//        top.linkTo(top, margin = topMargin * paddingScale)
//    } else if (bottom != null) {
//        bottom.linkTo(bottom, margin = bottomMargin * paddingScale)
//    }
//}







// 🔹 CompositionLocals para proveer las escalas
val LocalPaddingScale = compositionLocalOf { 1f }
val LocalFontScale = compositionLocalOf { 1f }

// 🔹 Extensiones simples para escalar Dp y Sp
fun Dp.scaled(scale: Float): Dp = (this.value * scale).dp
fun TextUnit.scaled(scale: Float): TextUnit = (this.value * scale).sp

// 🔹 Funciones @Composable para no pasar escala manualmente
@Composable
fun scaledPadding(base: Dp): Dp = base.scaled(LocalPaddingScale.current)

@Composable
fun scaledFont(base: TextUnit): TextUnit = base.scaled(LocalFontScale.current)




val CommonPaddingNone = 0.dp
val CommonPaddingOne = 1.dp
val CommonPaddingTwo = 2.dp
val CommonPaddingMicro = 4.dp
val CommonPaddingMicroMin = 6.dp
val CommonPaddingMin = 8.dp
val CommonPaddingTen = 10.dp
val CommonPaddingMinDefault = 12.dp
val CommonPaddingDefault = 16.dp
val CommonPaddingMiddle = 24.dp
val CommonPaddingLarge = 32.dp
val CommonPaddingLarge_med = 48.dp
val CommonPaddingLarge_lm = 54.dp
val SnackBarPaddingBottom = 150.dp
//val CommonPaddingXLarge = 64.dp
//val CommonPaddingListItemVertical = 12.dp
val HomeBottomAppBarHeight = 60.dp
val BottomBarPadding = 64.dp

val PostImageHeight = 180.dp
val FilterImageSize = 80.dp

val DividerThickness = 0.8.dp


val MessageVerticalSpace = 40.dp
val GameBottomPadding = 100.dp

val DropdownHeightInMax = 250.dp
val AlertDialogWidthInMax = 400.dp
val DropdownOffSetY = (-300).dp

val SmallScreenWidth = 360.dp
val MediumScreenWidth = 600.dp

val CommonFontSizeXXLarge = 24.sp
val CommonFontSizeXLarge = 22.sp
val CommonFontSizeLarge = 20.sp
val CommonFontSizeDefault = 18.sp
val CommonFontSizeDefaultMid = 17.sp
val CommonFontSizeMiddle = 16.sp
val CommonFontSizeMiddleMin = 15.sp
val CommonFontSizeMin = 14.sp
val CommonFontSizeMicro = 12.sp
val CommonFontSizeNano = 10.sp

val VersionDialogHeight = 300.dp

