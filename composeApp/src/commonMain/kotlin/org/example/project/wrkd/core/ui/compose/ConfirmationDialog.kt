package org.example.project.wrkd.core.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ConfirmationDialog(
    title: String?,
    message: String?,
    confirmText: String = "Yes",
    cancelText: String? = "No",
    onConfirm: () -> Unit,
    onCancel: (() -> Unit)?,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = AppTheme.color.white,
                    shape = RoundedCornerShape(AppTheme.corners.dialog)
                )
                .padding(
                    horizontal = AppTheme.dimens.medium2,
                    vertical = AppTheme.dimens.medium1
                )
        ) {
            title?.let {
                Text(
                    text = title,
                    style = AppTheme.typography.h6,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.color.black
                )

                Spacer(Modifier.height(AppTheme.dimens.small3))
            }

            message?.let {
                Text(
                    text = message,
                    style = AppTheme.typography.subtitle1,
                    fontWeight = FontWeight.Normal,
                    color = AppTheme.color.black87
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                if (cancelText != null && onCancel != null) {
                    Text(
                        text = cancelText,
                        style = AppTheme.typography.subtitle1,
                        fontWeight = FontWeight.Normal,
                        color = AppTheme.color.black87,
                        modifier = Modifier
                            .clickable(onClick = onCancel)
                            .padding(
                                horizontal = AppTheme.dimens.small2,
                                vertical = AppTheme.dimens.medium1
                            )
                    )

                    Spacer(Modifier.width(AppTheme.dimens.small3))
                }

                Text(
                    text = confirmText,
                    style = AppTheme.typography.subtitle1,
                    fontWeight = FontWeight.Normal,
                    color = AppTheme.color.primaryDarkBlue,
                    modifier = Modifier
                        .clickable(onClick = onConfirm)
                        .padding(
                            horizontal = AppTheme.dimens.small2,
                            vertical = AppTheme.dimens.medium1
                        )
                )
            }
        }
    }
}