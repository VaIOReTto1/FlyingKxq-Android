package com.atcumt.kxq.page.ai.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.atcumt.kxq.page.component.FlyText
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.utils.wdp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    title: String,
    onMenu: () -> Unit,
    onNew: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onMenu) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "菜单",
                    tint = FlyColors.FlyText
                )
            }
        },
        title = {
            Box(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                FlyText.AppbarTitle(
                    text = title,
                    modifier = Modifier.wrapContentWidth()
                )
            }
        },
        actions = {
            IconButton(onClick = onNew) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "新建对话",
                    tint = FlyColors.FlyText
                )
            }
        },
        colors = topAppBarColors(
        containerColor = FlyColors.FlyBackground,
        titleContentColor = FlyColors.FlyText
        ),
        modifier = Modifier.height(56.wdp)
    )
}