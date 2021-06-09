package com.chivalrous.sdk.utils

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import androidx.annotation.NonNull
import java.util.regex.Pattern

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/2 16:45
 * @UpdateRemark:   更新说明：
 */
object StringUtils {
    /**
     * 校验密码，至少包含大小写字母、数字及特殊符号的两种
     */
    fun passwordAvailable(@NonNull str: String): Boolean {
        val pattern =
            Pattern.compile("^(?![A-Z]+\$)(?![a-z]+\$)(?!\\d+\$)(?![\\W_]+\$)\\S{8,16}\$")
        return pattern.matcher(str).matches()
    }

    /**
     * 校验IPV4/IPV6 地址是否正确
     */
    fun checkIpAddress(@NonNull str: String): Boolean {
        val pattern =
            Pattern.compile("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\$")
        return pattern.matcher(str).matches()
    }


    /**
     * 获取 不同字号 XX/XX ,颜色样式字符串
     *
     * @param text
     * @return
     */
    fun makeTextHighLightStyle(
        text: String,
        mark: String,
        firstColor: Int,
        lastColor: Int,
        firstSize: Int,
        lastSize: Int
    ): Spannable? {
        val textSpan: Spannable = SpannableStringBuilder(text)
        textSpan.setSpan(
            AbsoluteSizeSpan(firstSize),
            0,
            text.indexOf(mark) + 1,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        textSpan.setSpan(
            ForegroundColorSpan(firstColor),
            0,
            text.indexOf(mark) + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textSpan.setSpan(
            AbsoluteSizeSpan(lastSize),
            text.indexOf(mark) + 1,
            text.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        textSpan.setSpan(
            ForegroundColorSpan(lastColor),
            text.indexOf(mark) + 1,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return textSpan
    }

    /**
     * 获取 不同字号和颜色样式字符串---自定义分割符
     *
     * @param text
     * @return
     */
    fun getTextMultiColor(
        text: String,
        firstColor: Int,
        lastColor: Int,
        mark: String
    ): Spannable? {
        val textSpan: Spannable = SpannableStringBuilder(text)
        textSpan.setSpan(
            ForegroundColorSpan(firstColor),
            0,
            text.indexOf(mark),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        textSpan.setSpan(
            ForegroundColorSpan(lastColor),
            text.indexOf(mark),
            text.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return textSpan
    }
}