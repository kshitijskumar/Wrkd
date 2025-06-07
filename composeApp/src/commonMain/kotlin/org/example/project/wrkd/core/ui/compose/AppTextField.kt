package org.example.project.wrkd.core.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PlatformImeOptions
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun AppTextField(
    value: String,
    textChangeFilter: (String) -> String = { it },
    onValueEntered: (String) -> Unit,
    placeholder: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    colors: TextFieldColors = TextFieldDefaults.appTextFieldColors(),
    shape: Shape = RoundedCornerShape(AppTheme.corners.textField),
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var textFieldValue by remember { mutableStateOf(TextFieldValue(value, TextRange(value.length))) }
    LaunchedEffect(value) {
        if (value != textFieldValue.text) {
            textFieldValue = textFieldValue.copy(text = value, selection = TextRange(value.length))
        }
    }
    TextField(
        value = textFieldValue,
        onValueChange = {
            val filteredText = textChangeFilter.invoke(it.text)
            textFieldValue = textFieldValue.copy(text = filteredText, selection = TextRange(filteredText.length))
            if (filteredText != value) {
                onValueEntered.invoke(filteredText)
            }
        },
        modifier = modifier,
        placeholder = placeholder,
        keyboardOptions = keyboardOptions.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        shape = shape,
        colors = colors,
    )
}

@Composable
fun TextFieldDefaults.appTextFieldColors(
    backgroundColor: Color = AppTheme.color.lightGrey,
    focusedIndicatorColor: Color = Color.Transparent,
    unfocusedIndicatorColor: Color = Color.Transparent,
): TextFieldColors {
    return this.textFieldColors(
        backgroundColor = backgroundColor,
        focusedIndicatorColor = focusedIndicatorColor,
        unfocusedIndicatorColor = unfocusedIndicatorColor,
    )
}

@Composable
fun AppTextFieldPlaceholder(
    text: String
) {
    Text(
        text = text,
        color = AppTheme.color.black60,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun AppTextFieldWithTrailingContent(
    value: String,
    trailingContent: @Composable () -> Unit,
    textChangeFilter: (String) -> String = { it },
    onValueEntered: (String) -> Unit,
    placeholder: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    colors: TextFieldColors = TextFieldDefaults.appTextFieldColors(),
    shape: Shape = RoundedCornerShape(AppTheme.corners.textField),
    trailingContentShape: Shape = RoundedCornerShape(
        topEnd = AppTheme.corners.textField,
        bottomEnd = AppTheme.corners.textField
    ),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .background(
                color = colors.backgroundColor(true).value,
                shape = shape
            )
            .clip(shape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppTextField(
            value = value,
            textChangeFilter = textChangeFilter,
            onValueEntered = onValueEntered,
            placeholder = placeholder,
            keyboardOptions = keyboardOptions,
            colors = colors,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .padding(AppTheme.dimens.small2)
                .fillMaxHeight()
                .background(
                    color = AppTheme.color.white,
                    shape = trailingContentShape
                )
                .padding(
                    horizontal = AppTheme.dimens.medium1
                ),
            contentAlignment = Alignment.Center
        ) {
            trailingContent.invoke()
        }
    }
}

@Composable
fun AppTextFieldTrailingText(
    text: String,
    textColor: Color = AppTheme.color.black87,
    style: TextStyle = AppTheme.typography.subtitle2,
    fontWeight: FontWeight = FontWeight.SemiBold,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = textColor,
        style = style,
        fontWeight = fontWeight,
        textAlign = TextAlign.Start,
        modifier = modifier
    )
}