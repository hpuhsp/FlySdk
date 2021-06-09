package com.chivalrous.sdk.utils

import com.blankj.utilcode.util.TimeUtils
import java.util.*

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/12/25 14:51
 * @UpdateRemark:   更新说明：
 */
object DateUtils {
    /**
     * 定义常用日期格式
     */
    const val DEFAULT_DATE_PATTERN = "yyyy-MM-dd"
    const val DEFAULT_TIME_PATTERN = "HH:mm:ss"
    const val COMPLETE_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss"
    const val CHINA_DATE_PATTERN = "yyyy年MM月dd日"
    const val SLASH_DATE_PATTERN = "yyyy/MM/dd"
    const val MONTH_DAY_PATTERN_A = "MM/dd"
    const val MONTH_DAY_PATTERN_B = "MM月dd日"
    const val MONTH_DAY_PATTERN_C = "MM-dd"
    const val YEAR_MONTH_PATTERN_A = "yyyy-MM"
    const val YEAR_MONTH_PATTERN_B = "yyyy/MM"
    const val YEAR_MONTH_PATTERN_C = "yyyy年MM月"


    /**
     * 获取当前日期
     */
    fun getCurrentDate(pattern: String): String {
        return TimeUtils.millis2String(System.currentTimeMillis(), pattern)
    }

    /**
     * 获取所在月的第一天
     */
    fun getMonthFirstDay(timeInMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
        return calendar.timeInMillis
    }

    /**
     * 获取所在月的最后一天
     */
    fun getMonthLastDay(timeInMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return calendar.timeInMillis
    }

    /**
     * 获取当前时间所在月有多少周
     * @param monthStamp
     * @return
     */
    private fun weeksOfMonth(monthStamp: Long): Int {
        val calendar = GregorianCalendar()
        calendar.timeInMillis = monthStamp
        calendar.firstDayOfWeek = Calendar.MONDAY
        return calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)
    }

    /**
     * 根据pattern返回对应时间格式
     */
    fun millis2String(millis: Long, pattern: String): String {
        return TimeUtils.millis2String(millis, pattern)
    }
}

