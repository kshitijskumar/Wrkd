package org.example.project.wrkd.core.ui.compose

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AppTheme {

    val corners: CardCorners = CardCorners()
    val dimens: Dimens = Dimens()
    val color: AppColor = AppColor()
    val typography: Typography = Typography()

}

data class CardCorners(
    val mainCard: Dp = 16.dp,
    val selectable: Dp = 36.dp,
    val textField: Dp = 24.dp,
    val buttons: Dp = 16.dp,
    val dialog: Dp = 8.dp,
    val homeInfoCard: Dp = 16.dp
)

data class Dimens(
    val small0: Dp = 1.dp,
    val small1: Dp = 2.dp,
    val small2: Dp = 4.dp,
    val small3: Dp = 8.dp,
    val small4: Dp = 12.dp,
    val medium1: Dp = 16.dp,
    val medium2: Dp = 24.dp,
    val medium3: Dp = 32.dp,
    val workoutSectionEmptyHeight: Dp = 200.dp,
    val baseScreenTabScreenBottomSpace: Dp = 32.dp
)

data class AppColor(
    val black10: Color = Color(0x1A000000),
    val black60: Color = Color(0x99000000),
    val black87: Color = Color(0xDE000000),
    val black: Color = Color(0xFF000000),
    val primaryRed: Color = Color(0xFFE63946),
    val primaryBeige: Color = Color(0xFFF1FAEE),
    val primaryTeal: Color = Color(0xFFA8DADC),
    val primaryLightBlue: Color = Color(0xFF457B9D),
    val primaryDarkBlue: Color = Color(0xFF1D3557),
    val white: Color = Color(0xFFFFFFFF),
    val lightGrey: Color = Color(0xFFf7f7f7),
    val mediumGrey: Color = Color(0xFFeaeaea),
    val secondaryCardColor: Color = Color(0xFF00a843),
    val primaryCardColor: Color = Color(0xFFef233c),
    val indicatorSelectedColor: Color = Color(0xFFFFFFFF),
    val indicatorUnselectedColor: Color = Color(0xFFFFFFFF).copy(alpha = 0.3f),
    val selectedTabIcon: Color = Color(0xFFFFFFFF),
    val unselectedTabIcon: Color = Color(0xFFFFFFFF).copy(alpha = 0.5f),
    val emptySectionBackground: Color = Color(0xFFf7f7f7),
)

/*

        defaultFontFamily: FontFamily = FontFamily.Default,
        h1: TextStyle = DefaultTextStyle.copy(
            fontWeight = FontWeight.Light,
            fontSize = 96.sp,
            lineHeight = 112.sp,
            letterSpacing = (-1.5).sp
        ),
        h2: TextStyle = DefaultTextStyle.copy(
            fontWeight = FontWeight.Light,
            fontSize = 60.sp,
            lineHeight = 72.sp,
            letterSpacing = (-0.5).sp
        ),
        h3: TextStyle = DefaultTextStyle.copy(
            fontWeight = FontWeight.Normal,
            fontSize = 48.sp,
            lineHeight = 56.sp,
            letterSpacing = 0.sp
        ),
        h4: TextStyle = DefaultTextStyle.copy(
            fontWeight = FontWeight.Normal,
            fontSize = 34.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.25.sp
        ),
        h5: TextStyle = DefaultTextStyle.copy(
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        h6: TextStyle = DefaultTextStyle.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        subtitle1: TextStyle = DefaultTextStyle.copy(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        subtitle2: TextStyle = DefaultTextStyle.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.1.sp
        ),
        body1: TextStyle = DefaultTextStyle.copy(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        body2: TextStyle = DefaultTextStyle.copy(
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        button: TextStyle = DefaultTextStyle.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            letterSpacing = 1.25.sp
        ),
        caption: TextStyle = DefaultTextStyle.copy(
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        overline: TextStyle = DefaultTextStyle.copy(
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            lineHeight = 16.sp,
            letterSpacing = 1.5.sp
        )

 */