package com.atcumt.kxq.page.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.atcumt.kxq.R
import com.atcumt.kxq.page.component.FlyDivider
import com.atcumt.kxq.page.component.FlyText
import com.atcumt.kxq.page.login.view.RegisterPage.BackButton
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.ui.theme.KxqTheme
import com.atcumt.kxq.utils.AdaptiveScreen
import com.atcumt.kxq.utils.ssp
import com.atcumt.kxq.utils.wdp

@Composable
fun ProfileEditPage() {
    Column {
        ProfileEditAppBar()
        ProfileEditContent()
    }
}

@Composable
fun ProfileEditAppBar() {
    val navController = rememberNavController()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.wdp)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(navController) // 返回按钮
        }
        FlyText.AppbarTitle(
            text = "编辑个人信息",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ProfileEditContent() {
    val navController = rememberNavController()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data("https://qlogo2.store.qq.com/qzone/1004275481/1004275481/100")
                    .apply {
                        crossfade(true)
                        placeholder(R.drawable.ic_launcher_foreground)
                        error(R.drawable.ic_launcher_foreground)
                        // 关键优化：缩小图片尺寸到实际显示大小
//                        size(13) // 单位：像素（根据实际需求调整）
                        // 可选：启用内存缓存
                        memoryCacheKey(MemoryCache.Key("https://qlogo2.store.qq.com/qzone/1004275481/1004275481/100"))
                    }.build()
            ),
            contentDescription = "avatar",
            modifier = Modifier
                .padding(vertical = 12.wdp)
                .clip(CircleShape)
                .background(Color.Black)
                .width(60.wdp)
                .height(60.wdp), // 圆形裁剪
            contentScale = ContentScale.Crop
        )
        FlyDivider()
        repeat(5) {
            ProfileEdit(
                title = "标题",
                content = "内容",
                onClick = {
                    navController.navigate("name")
                }
            )
        }
    }
}

@Composable
fun ProfileEdit(title: String, content: String, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.wdp).padding(start = 17.wdp, end = 12.wdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FlyText(
                title,
                color = FlyColors.FlyTextGray,
                fontSize = 16.ssp,
                fontWeight = FontWeight.W500,
                modifier = Modifier.padding(end = 48.wdp)
            )
            FlyText(
                content,
                color = FlyColors.FlyText,
                fontSize = 16.ssp,
                fontWeight = FontWeight.W500,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Rounded.ArrowForwardIos,
                contentDescription = "favorite",
                modifier = Modifier.width(18.wdp).clickable { onClick() },
                tint = FlyColors.FlyTextGray
            )
        }
        FlyDivider()
    }
}

@Preview
@Composable
fun PreviewProfileEditPage() {
    KxqTheme {
        AdaptiveScreen {
            ProfileEditPage()
        }
    }
}