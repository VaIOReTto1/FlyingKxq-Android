package com.atcumt.kxq.page.component

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.widget.TextView

/**
 * 自定义 TextView，支持逐字符打字机动画效果
 * 支持增量文本更新，适用于 SSE 场景
 */
class Typewriter @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : TextView(context, attrs) {
    private var mText: CharSequence = ""
    private var mIndex: Int = 0
    private var mDelay: Long = 40 // 默认字符延迟 40ms
    private val mHandler = Handler(Looper.getMainLooper())

    private val characterAdder = object : Runnable {
        override fun run() {
            setText(mText.subSequence(0, mIndex++))
            if (mIndex <= mText.length) {
                mHandler.postDelayed(this, mDelay)
            }
        }
    }

    /**
     * 开始动画，显示新文本
     * @param text 要显示的文本
     */
    fun animateText(text: CharSequence) {
        mText = text
        mIndex = 0
        setText("")
        mHandler.removeCallbacks(characterAdder)
        mHandler.postDelayed(characterAdder, mDelay)
    }

    /**
     * 更新文本，继续动画
     * @param newText 新文本
     */
    fun updateText(newText: CharSequence) {
        mHandler.removeCallbacks(characterAdder)
        mText = newText
        if (mIndex > mText.length) {
            mIndex = mText.length
        }
        setText(mText.subSequence(0, mIndex))
        if (mIndex < mText.length) {
            mHandler.postDelayed(characterAdder, mDelay)
        }
    }

    /**
     * 设置字符间延迟
     * @param millis 延迟时间（毫秒）
     */
    fun setCharacterDelay(millis: Long) {
        mDelay = millis
    }
}