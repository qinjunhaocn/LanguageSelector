package com.xyz.langselector.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import com.xyz.langselector.data.model.AppInfo
import com.xyz.langselector.util.LocaleUtils

@Composable
fun AppListItem(
    app: AppInfo,
    isPinned: Boolean,
    onClick: () -> Unit,
    onPinClick: () -> Unit
) {
    BasicComponent(
        title = if (isPinned) "★ ${app.name}" else app.name,
        summary = app.packageName,
        startAction = {
            app.icon?.let { drawable ->
                val painter = remember(app.packageName) {
                    BitmapPainter(drawable.toBitmap(48, 48).asImageBitmap())
                }
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(40.dp)
                )
            }
        },
        endActions = {
            val localeText = if (app.currentLocale.isBlank()) {
                "System"
            } else {
                LocaleUtils.getDisplayName(app.currentLocale)
            }
            Text(
                text = localeText,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSecondaryContainer
            )
        },
        onClick = onClick
    )
}
