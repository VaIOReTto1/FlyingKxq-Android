package com.atcumt.kxq.page.ai.component

import androidx.compose.foundation.background
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
    closeDrawer: () -> Unit
) {
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
                text = "暂无会话。",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(16.wdp)
                    .fillMaxWidth()
            )
        } else {
            // 会话列表
            LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                items(uiState.conversations, key = { it.conversationId }) { conv ->
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Outlined.Chat, contentDescription = "会话图标") },
                        label = {
                            Text(
                                text = conv.title.ifBlank { "无标题会话" },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = if (conv.conversationId == uiState.currentConversationId) {
                                    FlyColors.FlyBackground
                                } else {
                                    FlyColors.FlyText
                                },
                            )
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedIconColor = FlyColors.FlyBackground,
                            selectedTextColor = FlyColors.FlyBackground,
                            unselectedIconColor = FlyColors.FlyText,
                            unselectedTextColor = FlyColors.FlyText,
                            selectedContainerColor = FlyColors.FlyMain,
                            unselectedContainerColor = FlyColors.FlyBackground
                        ),
                        selected = conv.conversationId == uiState.currentConversationId,
                        onClick = {
                            if (conv.conversationId != uiState.currentConversationId) {
                                onSelectConversation(conv.conversationId, conv.title)
                            }
                            closeDrawer()
                        },
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                            .padding(horizontal = 12.wdp, vertical = 4.wdp)
                    )
                }
            }
        }
    }
}
