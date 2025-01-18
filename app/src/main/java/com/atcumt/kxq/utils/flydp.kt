import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 全局比例变量
var globalScaleX: Float = 1.0f
var globalScaleY: Float = 1.0f

// 为 Int 添加 flydp 扩展属性
val Int.wdp: Dp
    get() = (this * globalScaleX).dp

// 为 Float 添加 flydp 扩展属性
val Double.wdp: Dp
    get() = (this * globalScaleX).dp

// 为 Int 添加 flydp 扩展属性
val Int.hdp: Dp
    get() = (this * globalScaleY).dp

// 为 Float 添加 flydp 扩展属性
val Double.hdp: Dp
    get() = (this * globalScaleY).dp

// 为 Int 添加字体适配扩展
val Int.ssp: TextUnit
    get() = (this * globalScaleX).sp

val Double.ssp: TextUnit
    get() = (this * globalScaleX).sp
