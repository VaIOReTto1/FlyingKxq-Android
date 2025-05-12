package com.atcumt.kxq.page.ai.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import com.atcumt.kxq.page.ai.viewmodel.ChatState
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.utils.wdp

/**
 * 会话列表抽屉内容，可复用、独立承担 UI 逻辑
 */
@Composable
fun ChatDrawerContent(
    uiState: ChatState,
    onSelectConversation: (id: String, title: String) -> Unit,
    closeDrawer: () -> Unit,
    onDeleteConversation: (String) -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("删除对话") },
            text = { Text("确定要删除这个对话吗？此操作无法撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog?.let { onDeleteConversation(it) }
                        showDeleteDialog = null
                    }
                ) {
                    Text("删除", color = FlyColors.FlyText)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("取消")
                }
            },
            containerColor = FlyColors.FlyBackground,
            titleContentColor = FlyColors.FlyText,
            textContentColor = FlyColors.FlyText.copy(alpha = 0.8f)
        )
    }

    Column(
        modifier = Modifier
            .padding(top = 16.wdp, bottom = 16.wdp)
    ) {
        // 会话列表加载中
        if (uiState.isLoadingConversations && uiState.conversations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.wdp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = FlyColors.FlyMain)
            }
        } else if (uiState.conversations.isEmpty()) {
            // 没有会话时显示提示
            Text(
                text = "暂无会话。点击新建对话开始吧！",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = FlyColors.FlyText,
                modifier = Modifier
                    .padding(16.wdp)
                    .fillMaxWidth()
            )
        } else {
            // 会话列表
            LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                items(uiState.conversations, key = { it.conversationId }) { conv ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.wdp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavigationDrawerItem(
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = conv.title.ifBlank { "无标题会话" },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f) // 关键：Text 占用剩余空间
                                    )
                                    Spacer(modifier = Modifier.width(8.wdp)) // 与 icon 保持间距
                                    Box(
                                        //圆形
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .size(36.wdp)
                                            .clickable {
                                                showDeleteDialog = conv.conversationId
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = "删除对话",
                                            modifier = Modifier.size(20.wdp), // 图标比容器小
                                            //不选中为black
                                            tint =
                                            if (conv.conversationId == uiState.currentConversationId)
                                                FlyColors.FlyBackground.copy(alpha = 0.6f)
                                            else FlyColors.FlyText.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedIconColor = FlyColors.FlyBackground,
                                selectedTextColor = FlyColors.FlyBackground,
                                unselectedIconColor = FlyColors.FlyText,
                                unselectedTextColor = FlyColors.FlyText,
                                selectedContainerColor = FlyColors.FlyMain,
                                unselectedContainerColor = FlyColors.FlyBackground.copy(alpha = 0f)
                            ),
                            selected = conv.conversationId == uiState.currentConversationId,
                            onClick = {
                                if (conv.conversationId != uiState.currentConversationId) {
                                    onSelectConversation(conv.conversationId, conv.title)
                                }
                                closeDrawer()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(
                                    start = NavigationDrawerItemDefaults.ItemPadding.calculateStartPadding(
                                        androidx.compose.ui.unit.LayoutDirection.Ltr
                                    ),
                                    top = NavigationDrawerItemDefaults.ItemPadding.calculateTopPadding(),
                                    end = 0.wdp,
                                    bottom = NavigationDrawerItemDefaults.ItemPadding.calculateBottomPadding()
                                )
                        )
                    }
                }
            }
        }
    }
}
