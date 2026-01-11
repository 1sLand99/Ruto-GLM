package com.rosan.ruto.ui.compose

import android.view.DisplayInfo
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.twotone.Forum
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rosan.ruto.data.model.AiModel
import com.rosan.ruto.data.model.ConversationModel
import com.rosan.ruto.device.DeviceManager
import com.rosan.ruto.ui.Destinations
import com.rosan.ruto.ui.viewmodel.ConversationListViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
    navController: NavController,
    insets: WindowInsets,
    viewModel: ConversationListViewModel = koinViewModel()
) {
    val conversations by viewModel.conversations.collectAsState(initial = emptyList())
    val aiModels by viewModel.aiModels.collectAsState(initial = emptyList())
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var selectedIds by remember { mutableStateOf(emptyList<Long>()) }
    val isInSelectionMode = selectedIds.isNotEmpty()

    var showDialog by remember { mutableStateOf(false) }

    fun toggleSelection(id: Long) {
        selectedIds = if (id in selectedIds) selectedIds - id else selectedIds + id
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
                    TopAppBar(title = {
                        AnimatedContent(
                            targetState = selectedIds.size, transitionSpec = {
                                if (targetState > initialState) {
                                    (slideInVertically { height -> height } + fadeIn()) togetherWith (slideOutVertically { height -> -height } + fadeOut())
                                } else {
                                    (slideInVertically { height -> -height } + fadeIn()) togetherWith (slideOutVertically { height -> height } + fadeOut())
                                }.using(SizeTransform(clip = false))
                            }, label = "TextPushAnimation"
                        ) { targetSize ->
                            Text(text = "$targetSize selected")
                        }
                    }, navigationIcon = {
                        IconButton(onClick = { selectedIds = emptyList() }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }, actions = {
                        IconButton(onClick = { selectedIds = conversations.map { it.id } }) {
                            Icon(Icons.Default.SelectAll, contentDescription = "Select All")
                        }
                    })
                } else {
                    LargeTopAppBar(
                        title = {
                            Column {
                                Text(
                                    "Conversations",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "History of your AI interactions",
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
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isInSelectionMode) {
                        if (selectedIds.isEmpty()) return@FloatingActionButton
                        viewModel.remove(selectedIds)
                        selectedIds = emptyList()
                    } else showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                AnimatedContent(
                    targetState = isInSelectionMode, transitionSpec = {
                        if (targetState) {
                            slideInVertically { height -> height } togetherWith slideOutVertically { height -> -height }
                        } else {
                            slideInVertically { height -> -height } togetherWith slideOutVertically { height -> height }
                        }
                    }, label = "floatingActionButton"
                ) { selectionMode ->
                    if (selectionMode) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    } else {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }
        }, contentWindowInsets = insets
    ) { padding ->
        AnimatedContent(
            targetState = conversations.isEmpty(),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            transitionSpec = {
                (fadeIn(animationSpec = tween(500)) + expandVertically()) togetherWith
                        (fadeOut(animationSpec = tween(500)) + shrinkVertically())
            },
            label = "ContentAnimation"
        ) { isEmpty ->
            if (isEmpty) {
                EmptyConversation()
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
                    items(conversations, key = { it.id }) { conversation ->
                        Box(modifier = Modifier.animateItem()) {
                            ConversationListItem(
                                aiModels = aiModels,
                                conversation = conversation,
                                isSelected = conversation.id in selectedIds,
                                onClick = {
                                    if (isInSelectionMode) toggleSelection(conversation.id)
                                    else navController.navigate("${Destinations.CONVERSATION}/${conversation.id}")
                                },
                                onLongClick = { toggleSelection(conversation.id) })
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        CreateConversationDialog(
            aiModels = aiModels,
            onDismiss = { showDialog = false },
            onConfirm = {
                viewModel.add(it)
                showDialog = false
            })
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun CreateConversationDialog(
    aiModels: List<AiModel>,
    onDismiss: () -> Unit,
    onConfirm: (conversationModel: ConversationModel) -> Unit,
) {
    var name by remember { mutableStateOf("New Conversation") }
    var aiId by remember { mutableStateOf<Long?>(null) }

    var isTaskConversation by remember { mutableStateOf(false) }
    var selectedDisplayId by remember { mutableStateOf<Int?>(null) }

    val deviceManager = koinInject<DeviceManager>()
    var displays by remember { mutableStateOf(emptyList<DisplayInfo>()) }
    LaunchedEffect(Unit) {
        displays = deviceManager.getDisplayManager().displays
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Conversation") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text("AI Model", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    aiModels.forEach { aiModel ->
                        FilterChip(
                            selected = aiModel.id == aiId,
                            onClick = { aiId = aiModel.id },
                            label = { Text(aiModel.name) },
                            leadingIcon = {
                                if (aiModel.id == aiId) {
                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                                }
                            }
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Task Mode", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Enable screen-specific automation",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isTaskConversation,
                        onCheckedChange = {
                            isTaskConversation = it
                            if (!it) selectedDisplayId = null
                        }
                    )
                }

                AnimatedVisibility(visible = isTaskConversation) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Select Target Screen", style = MaterialTheme.typography.labelLarge)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            displays.forEach { display ->
                                FilterChip(
                                    selected = selectedDisplayId == display.displayId,
                                    onClick = { selectedDisplayId = display.displayId },
                                    label = { Text("Display ${display.displayId} (${display.name})") },
                                    leadingIcon = {
                                        if (selectedDisplayId == display.displayId) {
                                            Icon(
                                                Icons.Default.Monitor,
                                                null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    aiId?.let {
                        val model = ConversationModel(
                            aiId = it,
                            name = name,
                            displayId = selectedDisplayId
                        )
                        onConfirm(model)
                    }
                },
                enabled = aiId != null && (!isTaskConversation || selectedDisplayId != null)
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationListItem(
    aiModels: List<AiModel>,
    conversation: ConversationModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val scale by animateFloatAsState(if (isSelected) 0.96f else 1f, label = "scale")
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        }, label = "backgroundColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick, onLongClick = onLongClick
                )
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {}
                    Icon(
                        imageVector = Icons.TwoTone.Forum,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        conversation.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Model: ${aiModels.find { it.id == conversation.aiId }?.name ?: "Unknown"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun EmptyConversation() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.TwoTone.Forum,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No conversations yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "Start a new AI session to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
