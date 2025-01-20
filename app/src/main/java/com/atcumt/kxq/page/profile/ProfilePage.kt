package com.atcumt.kxq.page.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.atcumt.kxq.utils.wdp

@Composable
fun ProfilePage() {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 22.wdp)
    ) {
        Icon(
            Icons.Rounded.Menu,
            contentDescription = "菜单"
        )
    }
}

@Preview
@Composable
fun ProfilePagePreview() {
    MaterialTheme {
        ProfilePage()
    }

}