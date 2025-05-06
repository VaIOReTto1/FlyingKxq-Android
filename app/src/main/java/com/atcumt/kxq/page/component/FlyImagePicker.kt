package com.atcumt.kxq.page.component

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Composable：在任意 UI 上启用“图片选择器”功能。
 *
 * 设计要点：
 * 1. 完全无状态：展示/消失由外部通过 isPresented 控制，选中后通过回调上报。
 * 2. 单一职责：只关心启动系统“选图”Intent、读取 Bitmap 并回调，不负责 UI 展示。
 * 3. 易测试：通过参数注入 onImagePicked/onDismiss，可用 Fake 回调断言。
 * 4. 无需额外权限：使用 ACTION_GET_CONTENT，AndroidX Activity Compose 支持。
 * 5. 异步加载：借助协程自动切换 IO/Main 线程，避免主线程卡顿。
 */
@Composable
fun FlyImagePickerLauncher(
    isPresented: Boolean,
    onImagePicked: (Bitmap) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // 1. 注册系统“选图”回调
    val launcher = rememberLauncherForActivityResult(GetContent()) { uri: Uri? ->
        // 选图完成
        uri?.let {
            scope.launch {
                // IO 线程解码 Bitmap
                val bmp = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(it)
                        ?.use { stream -> BitmapFactory.decodeStream(stream) }
                }
                bmp?.let(onImagePicked)
            }
        }
        onDismiss()
    }

    // 2. 当 isPresented = true 时自动调起选图
    LaunchedEffect(isPresented) {
        if (isPresented) {
            launcher.launch("image/*")
        }
    }
}
