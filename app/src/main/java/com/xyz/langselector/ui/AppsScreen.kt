package com.xyz.langselector.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import com.xyz.langselector.viewmodel.MainViewModel
import com.xyz.langselector.viewmodel.ShizukuState
import com.xyz.langselector.ui.components.AppListItem

@Composable
fun AppsScreen(
    viewModel: MainViewModel,
    contentPadding: PaddingValues,
    lazyListState: LazyListState,
    scrollBehavior: ScrollBehavior,
    modifier: Modifier = Modifier
) {
    val shizukuState by viewModel.shizukuState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val nestedScrollConnection = scrollBehavior.nestedScrollConnection

    when (shizukuState) {
        ShizukuState.NOT_RUNNING -> {
            ShizukuNotRunningView(
                viewModel = viewModel,
                contentPadding = contentPadding,
                modifier = modifier
            )
        }
        ShizukuState.NO_PERMISSION -> {
            NoPermissionView(
                viewModel = viewModel,
                contentPadding = contentPadding,
                modifier = modifier
            )
        }
        else -> {
            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(
                    top = contentPadding.calculateTopPadding(),
                    bottom = contentPadding.calculateBottomPadding()
                ),
                modifier = modifier
                    .fillMaxSize()
                    .nestedScroll(nestedScrollConnection)
            ) {
                item(key = "search") {
                    SmallTitle(text = "Search")
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp)
                    )
                }

                if (isLoading) {
                    item(key = "loading") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Loading apps...",
                                style = MiuixTheme.textStyles.body1
                            )
                        }
                    }
                } else {
                    val apps = viewModel.filteredApps
                    if (apps.isEmpty() && searchQuery.isNotBlank()) {
                        item(key = "empty") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No apps found",
                                    style = MiuixTheme.textStyles.body1,
                                    color = MiuixTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    } else {
                        item(key = "app_list_header") {
                            SmallTitle(text = "Apps (${apps.size})")
                        }
                        items(items = apps, key = { it.packageName }) { app ->
                            AppListItem(
                                app = app,
                                isPinned = app.packageName in viewModel.pinnedApps.collectAsState().value,
                                onClick = { viewModel.selectApp(app) },
                                onPinClick = { viewModel.togglePin(app.packageName) }
                            )
                            HorizontalDivider()
                        }
                        item(key = "bottom_spacer") {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    top.yukonga.miuix.kmp.basic.TextField(
        value = query,
        onValueChange = onQueryChange,
        label = "Search apps",
        singleLine = true,
        modifier = modifier
    )
}

@Composable
private fun ShizukuNotRunningView(
    viewModel: MainViewModel,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = PaddingValues(24.dp)
        ) {
            Text(
                text = "Shizuku Not Running",
                style = MiuixTheme.textStyles.title2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please start Shizuku to use this app. Shizuku allows Language Selector to set per-app language for any installed app.",
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.retryConnection() },
                colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.buttonColorsPrimary()
            ) {
                Text(text = "Retry")
            }
        }
    }
}

@Composable
private fun NoPermissionView(
    viewModel: MainViewModel,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = PaddingValues(24.dp)
        ) {
            Text(
                text = "Permission Required",
                style = MiuixTheme.textStyles.title2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Language Selector needs Shizuku permission to set per-app language. Please grant permission to continue.",
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.requestPermission() },
                colors = top.yukonga.miuix.kmp.basic.ButtonDefaults.buttonColorsPrimary()
            ) {
                Text(text = "Grant Permission")
            }
        }
    }
}
