package com.xyz.langselector.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import com.xyz.langselector.viewmodel.MainViewModel
import com.xyz.langselector.ui.components.LanguagePickerSheet

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    val showLanguagePicker by viewModel.showLanguagePicker.collectAsState()
    val scrollBehavior = MiuixScrollBehavior()
    val lazyListState = rememberLazyListState()

    val titles = listOf("Apps", "Settings")

    Scaffold(
        topBar = {
            TopAppBar(
                title = titles[selectedTabIndex],
                largeTitle = titles[selectedTabIndex],
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTabIndex == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    icon = Icons.Default.List,
                    label = "Apps"
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    icon = Icons.Default.Settings,
                    label = "Settings"
                )
            }
        }
    ) { paddingValues ->
        when (selectedTabIndex) {
            0 -> AppsScreen(
                viewModel = viewModel,
                contentPadding = paddingValues,
                lazyListState = lazyListState,
                scrollBehavior = scrollBehavior,
                modifier = modifier
            )
            1 -> SettingsScreen(
                viewModel = viewModel,
                contentPadding = paddingValues,
                modifier = modifier
            )
        }
        if (showLanguagePicker) {
            LanguagePickerSheet(viewModel = viewModel)
        }
    }
}
