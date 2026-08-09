package com.xyz.langselector.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import com.xyz.langselector.viewmodel.MainViewModel
import com.xyz.langselector.viewmodel.ShizukuState

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val shizukuState by viewModel.shizukuState.collectAsState()
    val showSystemApps by viewModel.showSystemApps.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding()
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        item(key = "shizuku_section") {
            SmallTitle(text = "Shizuku")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp)
            ) {
                BasicComponent(
                    title = "Status",
                    summary = when (shizukuState) {
                        ShizukuState.READY -> "Running"
                        ShizukuState.NOT_RUNNING -> "Not Running"
                        ShizukuState.NO_PERMISSION -> "Permission Required"
                        ShizukuState.NOT_CHECKED -> "Checking..."
                    }
                )
                if (shizukuState == ShizukuState.NO_PERMISSION) {
                    HorizontalDivider()
                    BasicComponent(
                        title = "Request Permission",
                        onClick = { viewModel.requestPermission() }
                    )
                }
                if (shizukuState == ShizukuState.NOT_RUNNING) {
                    HorizontalDivider()
                    BasicComponent(
                        title = "Retry Connection",
                        onClick = { viewModel.retryConnection() }
                    )
                }
            }
        }

        item(key = "display_section") {
            SmallTitle(text = "Display")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp)
            ) {
                SwitchPreference(
                    title = "Show System Apps",
                    summary = "Include system apps in the list",
                    checked = showSystemApps,
                    onCheckedChange = { viewModel.toggleSystemApps() }
                )
            }
        }

        item(key = "about_section") {
            SmallTitle(text = "About")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp)
            ) {
                BasicComponent(
                    title = "Language Selector",
                    summary = "Version 1.0.0"
                )
                HorizontalDivider()
                BasicComponent(
                    title = "Description",
                    summary = "A per-app language selector using Shizuku, replicating Android 13 App Languages feature"
                )
                HorizontalDivider()
                BasicComponent(
                    title = "Android Version",
                    summary = "Requires Android 13+ (API 33+)"
                )
                HorizontalDivider()
                BasicComponent(
                    title = "Dependencies",
                    summary = "Shizuku, Miuix UI Library"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
