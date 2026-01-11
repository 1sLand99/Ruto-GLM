package com.rosan.ruto.ui.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rosan.ruto.ui.Destinations
import com.rosan.ruto.ui.compose.screen_list.CreateDisplayDialog
import com.rosan.ruto.ui.compose.screen_list.ScreenListItem
import com.rosan.ruto.ui.viewmodel.ScreenListViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenListScreen(navController: NavController, insets: WindowInsets) {
    val viewModel: ScreenListViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedDisplayIds by remember { mutableStateOf(emptySet<Int>()) }
    val isInSelectionMode = selectedDisplayIds.isNotEmpty()

    val navigateToMultiTask = { ids: List<Int> ->
        if (ids.isNotEmpty()) {
            val idsString = ids.joinToString(",")
            navController.navigate("${Destinations.MULTI_TASK_PREVIEW}/$idsString")
        }
    }

    fun toggleSelection(displayId: Int) {
        selectedDisplayIds = if (displayId in selectedDisplayIds) {
            selectedDisplayIds - displayId
        } else {
            selectedDisplayIds + displayId
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadDisplays()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AnimatedContent(
                targetState = isInSelectionMode, transitionSpec = {
                    if (targetState) {
                        slideInVertically { height -> height } togetherWith slideOutVertically { height -> -height }
                    } else {
                        slideInVertically { height -> -height } togetherWith slideOutVertically { height -> height }
                    }
                }, label = "TopAppBar"
            ) { selectionModeActive ->
                if (selectionModeActive) {
                    TopAppBar(
                        title = {
                            AnimatedContent(
                                targetState = selectedDisplayIds.size, transitionSpec = {
                                    if (targetState > initialState) {
                                        (slideInVertically { height -> height } + fadeIn()) togetherWith (slideOutVertically { height -> -height } + fadeOut())
                                    } else {
                                        (slideInVertically { height -> -height } + fadeIn()) togetherWith (slideOutVertically { height -> height } + fadeOut())
                                    }.using(SizeTransform(clip = false))
                                }, label = "TextPushAnimation"
                            ) { targetSize ->
                                Text(text = "$targetSize selected")
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { selectedDisplayIds = emptySet() }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                selectedDisplayIds = uiState.displays.map { it.displayId }.toSet()
                            }) {
                                Icon(Icons.Default.SelectAll, contentDescription = "Select All")
                            }
                        }
                    )
                } else {
                    LargeTopAppBar(
                        title = {
                            Column {
                                Text(
                                    "Screens",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "View and capture device screens",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior,
                        colors = TopAppBarDefaults.largeTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            scrolledContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(
                    visible = isInSelectionMode,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    FloatingActionButton(
                        onClick = {
                            navigateToMultiTask(selectedDisplayIds.toList())
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = "Preview Selected")
                    }
                }
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Screen")
                }
            }
        },
        contentWindowInsets = insets
    ) { padding ->
        if (showCreateDialog) {
            CreateDisplayDialog(
                uiState = uiState,
                onDismiss = { showCreateDialog = false },
                onConfirm = { name, width, height, density ->
                    viewModel.createDisplay(name, width, height, density)
                    showCreateDialog = false
                }
            )
        }

        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            isRefreshing = uiState.displays.isNotEmpty() && uiState.isRefreshing,
            onRefresh = { viewModel.loadDisplays() },
            state = rememberPullToRefreshState()
        ) {
            AnimatedContent(
                targetState = uiState.displays.isEmpty(),
                modifier = Modifier.fillMaxSize(),
                transitionSpec = {
                    (fadeIn(animationSpec = tween(500)) + expandVertically()) togetherWith
                            (fadeOut(animationSpec = tween(500)) + shrinkVertically())
                },
                label = "ContentAnimation"
            ) { isEmpty ->
                if (isEmpty) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Monitor,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No screens found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Pull down to refresh or add a virtual screen",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(
                            start = 24.dp,
                            end = 24.dp,
                            top = 8.dp,
                            bottom = 80.dp
                        )
                    ) {
                        items(uiState.displays, key = { it.displayId }) { display ->
                            Box(modifier = Modifier.animateItem()) {
                                ScreenListItem(
                                    display = display,
                                    isSelected = display.displayId in selectedDisplayIds,
                                    onDelete = { viewModel.release(display.displayId) },
                                    onPreview = { navigateToMultiTask(listOf(display.displayId)) },
                                    onClick = {
                                        if (isInSelectionMode) {
                                            toggleSelection(display.displayId)
                                        } else {
                                            navigateToMultiTask(listOf(display.displayId))
                                        }
                                    },
                                    onLongClick = {
                                        toggleSelection(display.displayId)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
