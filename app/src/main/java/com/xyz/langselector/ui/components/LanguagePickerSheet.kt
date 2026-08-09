package com.xyz.langselector.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.overlay.OverlayBottomSheet
import top.yukonga.miuix.kmp.theme.MiuixTheme
import com.xyz.langselector.util.LocaleUtils
import com.xyz.langselector.viewmodel.MainViewModel

@Composable
fun LanguagePickerSheet(
    viewModel: MainViewModel
) {
    val selectedApp by viewModel.selectedApp.collectAsState()
    val currentLocale by viewModel.currentLocale.collectAsState()

    val app = selectedApp ?: return

    OverlayBottomSheet(
        show = true,
        onDismissRequest = { viewModel.dismissLanguagePicker() }
    ) {
        SmallTitle(text = "Select Language")
        Text(
            text = app.name,
            style = MiuixTheme.textStyles.title2,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        val currentDisplay = if (currentLocale.isBlank()) {
            "System Default"
        } else {
            LocaleUtils.getDisplayName(currentLocale)
        }
        Text(
            text = "Current: $currentDisplay",
            style = MiuixTheme.textStyles.body2,
            color = MiuixTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        HorizontalDivider()

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items = LocaleUtils.supportedLanguages, key = { it.tag }) { language ->
                val isSelected = when {
                    language.isSystemDefault -> currentLocale.isBlank()
                    else -> currentLocale == language.tag
                }
                BasicComponent(
                    title = language.displayName,
                    summary = if (language.nativeName != language.displayName) {
                        language.nativeName
                    } else {
                        null
                    },
                    endActions = {
                        if (isSelected) {
                            Text(
                                text = "✓",
                                style = MiuixTheme.textStyles.body1,
                                color = MiuixTheme.colorScheme.primary
                            )
                        }
                    },
                    onClick = { viewModel.setLocale(language) }
                )
                HorizontalDivider()
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                viewModel.forceStopApp(app)
                viewModel.dismissLanguagePicker()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors()
        ) {
            Text(text = "Force Stop App")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
