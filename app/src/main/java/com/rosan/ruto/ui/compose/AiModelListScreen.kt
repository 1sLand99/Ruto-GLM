package com.rosan.ruto.ui.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rosan.ruto.data.model.AiModel
import com.rosan.ruto.data.model.ai_model.AiCapability
import com.rosan.ruto.data.model.ai_model.AiType
import com.rosan.ruto.ui.viewmodel.AiModelListViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun LlmModelListScreen(navController: NavController, insets: WindowInsets) {
    val viewModel: AiModelListViewModel = koinViewModel()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val models by remember(viewModel.models) {
        viewModel.models.distinctUntilChanged()
    }.collectAsState(initial = emptyList())

    var showModelEditorDialog by remember { mutableStateOf(false) }
    var modelToEdit by remember { mutableStateOf<AiModel?>(null) }
    var selectedIds by remember { mutableStateOf(emptyList<Long>()) }
    val isInSelectionMode = selectedIds.isNotEmpty()

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
                        IconButton(onClick = { selectedIds = models.map { it.id } }) {
                            Icon(Icons.Default.SelectAll, contentDescription = "Select All")
                        }
                    })
                } else {
                    LargeTopAppBar(
                        title = {
                            Column {
                                Text(
                                    "LLM Models",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Manage and configure your AI brains",
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
                    } else {
                        modelToEdit = null
                        showModelEditorDialog = true
                    }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (models.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No models added yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Add your first AI model to get started",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(models, key = { it.id }) { model ->
                        Box(modifier = Modifier.animateItem()) {
                            ModelItem(model = model, isSelected = model.id in selectedIds, onClick = {
                                if (isInSelectionMode) {
                                    toggleSelection(model.id)
                                } else {
                                    modelToEdit = model
                                    showModelEditorDialog = true
                                }
                            }, onLongClick = {
                                toggleSelection(model.id)
                            })
                        }
                    }
                }
            }
        }
    }

    if (showModelEditorDialog) {
        ModelEditorDialog(
            viewModel = viewModel,
            modelToEdit = modelToEdit,
            onDismiss = { showModelEditorDialog = false },
            onConfirm = {
                showModelEditorDialog = false
            })
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
private fun ModelItem(
    model: AiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
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
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .graphicsLayer {
                                if (isSelected) alpha = 0.5f
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {}
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            model.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            model.modelId,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val typeColor =
                        if (model.type == AiType.OPENAI) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

                    Surface(
                        color = typeColor.copy(alpha = 0.15f), shape = CircleShape
                    ) {
                        Text(
                            text = model.type.name,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = typeColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    model.capabilities.forEach { cap ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = cap.name,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
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


@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)
@Composable
private fun ModelEditorDialog(
    viewModel: AiModelListViewModel,
    modelToEdit: AiModel? = null,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val isEditing = modelToEdit != null
    var name by remember { mutableStateOf(modelToEdit?.name ?: "New Model") }
    var baseUrl by remember { mutableStateOf(modelToEdit?.baseUrl ?: "https://api.openai.com/v1/") }
    var modelId by remember { mutableStateOf(modelToEdit?.modelId ?: "gpt-3.5-turbo") }
    var apiKey by remember { mutableStateOf(modelToEdit?.apiKey ?: "") }
    var selectedType by remember { mutableStateOf(modelToEdit?.type ?: AiType.OPENAI) }
    var selectedCapabilities by remember {
        mutableStateOf(
            modelToEdit?.capabilities?.toSet() ?: emptySet()
        )
    }
    var apiKeyVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Edit Model" else "Add Model") },
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
                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = { baseUrl = it },
                    label = { Text("Base URL") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = modelId,
                    onValueChange = { modelId = it },
                    label = { Text("Model ID") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API Key") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (apiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        val image = if (apiKeyVisible) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        IconButton(onClick = { apiKeyVisible = !apiKeyVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = "Toggle API Key Visibility"
                            )
                        }
                    })

                // AiType Chips (单选)
                Text("Model Type", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AiType.entries.forEach { type ->
                        FilterChip(
                            modifier = Modifier.animateContentSize(),
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.name) },
                            leadingIcon = {
                                if (selectedType != type) return@FilterChip
                                Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                            }
                        )
                    }
                }

                // Capabilities Chips (多选)
                Text("Capabilities", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AiCapability.entries.forEach { cap ->
                        val isSelected = selectedCapabilities.contains(cap)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCapabilities =
                                    if (isSelected) selectedCapabilities - cap else selectedCapabilities + cap
                            },
                            label = { Text(cap.name) },
                            leadingIcon = {
                                if (!isSelected) return@FilterChip
                                Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (modelToEdit != null) viewModel.update(
                        modelToEdit.copy(
                            name = name,
                            baseUrl = baseUrl,
                            modelId = modelId,
                            apiKey = apiKey,
                            type = selectedType,
                            capabilities = selectedCapabilities.toList()
                        )
                    ) else viewModel.add(
                        AiModel(
                            name = name,
                            baseUrl = baseUrl,
                            modelId = modelId,
                            apiKey = apiKey,
                            type = selectedType,
                            capabilities = selectedCapabilities.toList()
                        )
                    )
                    onConfirm()
                },
                enabled = name.isNotBlank() && modelId.isNotBlank() && apiKey.isNotBlank()
            ) { Text("Confirm") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}
