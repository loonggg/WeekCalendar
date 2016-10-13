package com.loonggg.weekcalendar.utils;


import android.content.Context;

import com.loonggg.weekcalendar.R;
import com.loonggg.weekcalendar.entity.CalendarData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WeekCalendarUtil {

    private static int daysOfMonth = 0;      //某月的天数
    private static int dayOfWeek = 0;        //具体某一天是星期几
    private static int eachDayOfWeek = 0;

    /**
     * 获取星期几     * @return
     */
    public static Map<Integer, String> getWeekString(Context mContext) {
        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(1, mContext.getString(R.string.Monday));
        map.put(2, mContext.getString(R.string.Tuesday));
        map.put(3, mContext.getString(R.string.Wednesday));
        map.put(4, mContext.getString(R.string.Thursday));
        map.put(5, mContext.getString(R.string.Friday));
        map.put(6, mContext.getString(R.string.Saturday));
        map.put(0, mContext.getString(R.string.Sunday));
        return map;
    }

    /**
     * 判断是否为闰年
     */
    public static boolean isLeapYear(int year) {
        if (year % 100 == 0 && year % 400 == 0) {
            return true;
        } else if (year % 100 != 0 && year % 4 == 0) {
            return true;
        }
        return false;
    }

    /**
     * 得到某月有多少天数
     */
    public static int getDaysOfMonth(CalendarData day) {
        switch (day.month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                daysOfMonth = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                daysOfMonth = 30;
                break;
            case 2:
                if (isLeapYear(day.year)) {
                    daysOfMonth = 29;
                } else {
                    daysOfMonth = 28;
                }

        }
        return daysOfMonth;
    }

    /**
     * 指定某年中的某月的第一天是星期几
     */
    public static int getWeekdayOfFirstDayInMonth(CalendarData day) {
        Calendar cal = Calendar.getInstance();
        cal.set(day.year, day.month - 1, 1);
        dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return dayOfWeek;
    }

    /**
     * 指定某年中的某月的最后一天是星期几
     */
    public static int getWeekdayOfEndDayInMonth(CalendarData day) {
        CalendarData theDayOfNextMonth = getTheDayOfNextMonth(day);//获取上一个月的这一天
        return getWeekOfLastDay(getWeekdayOfFirstDayInMonth(theDayOfNextMonth));//所在月份的最后一天是星期几;
    }

    /**
     * 获取某一年的某个月的某一天是星期几
     */
    public static int getWeekDay(CalendarData day) {
        Calendar cal = Calendar.getInstance();
        cal.set(day.year, day.month - 1, day.day);
        eachDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return eachDayOfWeek;
    }

    /**
     * 获取某天所在的整个月的数据（包含用于显示的上个月的天数和下个月的天数）
     */
    public static List<CalendarData> getWholeMonthDay(CalendarData day) {
        List<CalendarData> datas = new ArrayList<>();
        int weekdayOfFirstDayInMonth = getWeekdayOfFirstDayInMonth(day);//所在月份的第一天是星期几
        int weekdayOfLastDayInMonth = getWeekdayOfEndDayInMonth(day);//所在月份的最后一天是星期几
        //添加当月的数据（不包括星期几）
        datas.addAll(getWholeMonth(day));
        //添加上个月几天
        for (int i = 0; i < weekdayOfFirstDayInMonth; i++) {
            CalendarData lastDay = getDayOfLastDay(datas.get(0));
            lastDay.isLastMonthDay = true;
            datas.add(0, lastDay);
        }

        //添加下个月的几天
        for (int i = 0; i < 6 - weekdayOfLastDayInMonth; i++) {
            CalendarData nextday = getDayOfNextDay(datas.get(datas.size() - 1));
            nextday.isNextMonthDay = true;
            datas.add(nextday);
        }
        return datas;
    }


    /**
     * 获取所在月的所有数据(不包括星期)
     */
    public static List<CalendarData> getWholeMonth(CalendarData day) {
        List<CalendarData> datas = new ArrayList<>();
        int monthDay = getDaysOfMonth(day);//所在月份的天数
        for (int i = 0; i < monthDay; i++) {//添加当月的所有数据
            CalendarData c = new CalendarData(day.year, day.month, i + 1);
            datas.add(c);
        }
        return datas;
    }


    /**
     * 将月信息装换为map对象,获取当月星期
     */
    public static Map<Integer, List> getWholeWeeks(List<CalendarData> datas) {
        Map<Integer, List> map = new LinkedHashMap<>();
        int weekSize = datas.size() / 7;//当月有几个星期
        for (int i = 0; i < weekSize; i++) {
            List<CalendarData> week = new ArrayList<>();
            for (int j = i * 7; j < i * 7 + 7; j++) {
                week.add(datas.get(j));
                map.put(i, week);
            }
        }
        return map;
    }

    /**
     * 获取某一天所在星期的位置
     * <p/>
     * 如果没有数据，返回0
     */
    public static int getTheWeekPosition(Map<Integer, List> map, CalendarData day) {
        int position =-1;
        for (Map.Entry<Integer, List> entry : map.entrySet()) {
            position++;
            List<CalendarData> list = (List<CalendarData>) entry.getValue();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).day == day.day && list.get(i).month == day.month) {
                    return  position;
                }
            }
        }

        return 0;
    }


    /**
     * 获取上一天的数据
     */
    public static CalendarData getDayOfLastDay(CalendarData theday) {
        CalendarData lastday = new CalendarData();
        lastday.week = getWeekOfLastDay(theday.week);
        CalendarData theDayOfLastMonth = getTheDayOfLastMonth(theday);
        if (theday.day == 1) {
            lastday.day = getDaysOfMonth(theDayOfLastMonth);
            lastday.month = theDayOfLastMonth.month;
            lastday.year = theDayOfLastMonth.year;
        } else {
            lastday.day = theday.day - 1;
            lastday.month = theday.month;
            lastday.year = theday.year;
        }
        return lastday;

    }

    /**
     * 获取下一天的数据
     */
    public static CalendarData getDayOfNextDay(CalendarData theday) {
        CalendarData nextday = new CalendarData();
        CalendarData theDayOfNextMonth = getTheDayOfNextMonth(theday);
        nextday.week = getWeekOfNextDay(theday.week);
        if (theday.day == getDaysOfMonth(theday)) {
            nextday.day = 1;
            nextday.month = theDayOfNextMonth.month;
            nextday.year = theDayOfNextMonth.year;
        } else {
            nextday.day = theday.day + 1;
            nextday.month = theday.month;
            nextday.year = theday.year;
        }
        return nextday;

    }

    /**
     * 获取上一天是星期几
     */
    public static int getWeekOfLastDay(int week) {
        int weekOfLastDay;
        if (week == 0) {//星期天
            weekOfLastDay = 6;
        } else {
            weekOfLastDay = week - 1;
        }
        return weekOfLastDay;

    }

    /**
     * 获取下一天是星期几
     */
    public static int getWeekOfNextDay(int week) {
        int weekOfNextDay;
        if (week == 6) {//星期六
            weekOfNextDay = 0;
        } else {
            weekOfNextDay = week + 1;
        }
        return weekOfNextDay;

    }

    /**
     * 获取下个的这一天()
     */
    public static CalendarData getTheDayOfNextMonth(CalendarData day) {
        CalendarData data = new CalendarData();
        int month = day.month;
        if (month == 12) {
            data.month = 1;
            data.year = day.year + 1;
            data.day = day.day;
        } else {
            data.month = month + 1;
            data.year = day.year;
            data.day = day.day;
        }
        return data;
    }

    /**
     * 获取上个月的这一天
     */
    public static CalendarData getTheDayOfLastMonth(CalendarData day) {
        CalendarData data = new CalendarData();
        int month = day.month;
        if (month == 1) {
            data.month = 12;
            data.year = day.year - 1;
            data.day = day.day;
        } else {
            data.month = month - 1;
            data.year = day.year;
            data.day = day.day;
        }
        return data;
    }

}
