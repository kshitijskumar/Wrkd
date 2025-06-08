package org.example.project.wrkd.core.ui.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    AppButton(
        text = text,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = AppTheme.color.black
        ),
        textColor = AppTheme.color.white,
        enabled = enabled,
        modifier = modifier
    )
}

@Composable
fun AppSecondaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    AppButton(
        text = text,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = AppTheme.color.primaryRed
        ),
        textColor = AppTheme.color.white,
        enabled = enabled,
        modifier = modifier
    )
}

@Composable
fun AppTimerButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    AppButton(
        text = text,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = AppTheme.color.primaryRed
        ),
        textColor = AppTheme.color.white,
        enabled = enabled,
        modifier = modifier
    )
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    colors: ButtonColors,
    textColor: Color,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(AppTheme.corners.buttons),
        colors = colors,
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp,
            hoveredElevation = 0.dp,
            focusedElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            style = AppTheme.typography.button,
            color = textColor,
            modifier = Modifier.padding(AppTheme.dimens.small3)
        )
    }
}