package org.example.project.wrkd.core.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.wrkd.track.ui.WorkoutTrackerIntent
import org.jetbrains.compose.resources.painterResource
import wrkd.composeapp.generated.resources.Res
import wrkd.composeapp.generated.resources.ic_solid_tick

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
fun AppPrimaryHollowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppHollowButton(
        text = text,
        onClick = onClick,
        textColor = AppTheme.color.black,
        borderColor = AppTheme.color.black,
        modifier = modifier
    )
}

@Composable
fun AppHollowButton(
    text: String,
    onClick: () -> Unit,
    textColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(AppTheme.corners.mainCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = AppTheme.dimens.small1,
                    color = borderColor,
                    shape = RoundedCornerShape(AppTheme.corners.mainCard)
                )
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = onClick
                )
                .padding(
                    horizontal = AppTheme.dimens.medium1,
                    vertical = AppTheme.dimens.medium1
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = AppTheme.typography.button,
                fontWeight = FontWeight.Normal,
                color = textColor
            )
        }
    }
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
fun HorizontalButtons(
    primaryBtnText: String,
    primaryBtnClick: () -> Unit,
    secondaryBtnText: String,
    secondaryBtnClick: () -> Unit,
    primaryEnabled: Boolean = true,
    secondaryEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AppSecondaryButton(
            text = secondaryBtnText,
            onClick = secondaryBtnClick,
            modifier = Modifier
                .weight(1f),
            enabled = secondaryEnabled
        )

        Spacer(Modifier.width(AppTheme.dimens.small3))

        AppPrimaryButton(
            text = primaryBtnText,
            onClick = primaryBtnClick,
            modifier = Modifier
                .weight(1f),
            enabled = primaryEnabled
        )
    }
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