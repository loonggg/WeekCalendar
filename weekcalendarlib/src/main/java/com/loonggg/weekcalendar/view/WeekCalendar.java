package com.loonggg.weekcalendar.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.loonggg.weekcalendar.R;
import com.loonggg.weekcalendar.base.SimpleBaseAdapter;
import com.loonggg.weekcalendar.entity.CalendarData;
import com.loonggg.weekcalendar.utils.WeekCalendarUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WeekCalendar extends LinearLayout {
    RelativeLayout mIvPrevious;
    TextView mTvYearMouth;
    RelativeLayout mIvNext;
    RelativeLayout month_layout;
    ViewFlipper mRvDay;
    ImageView preBtn, nextBtn;

    private Context context;
    private GridView mGridView = null;
    private OnDateClickListener listener;

    private List<CalendarData> calendarDatas;
    private Map<Integer, List> weeks;
    private int weekPosition;//星期在月份中的位置


    private CalendarData today;
    private CalendarData theDayOfSelected;//被选中的日期
    private CalendarData theDayForShow;//用于展示数据的中间变量

    private int daysSelectedTextColor, todayTextColor, weekTextColor, weekBackgroundColor, monthBackgroundColor, monthTextColor, daysTextColor;
    private boolean hideTodayName = false, isCornerMark = false, isShowMonth = true;
    private float daysTextSize, weekTextSize;
    private Drawable daysSelectedBackground, nextArrowBg, preArrowBg, cornerMarkBg;
    private List<String> selectDateList = null;
    private OnCurrentMonthDateListener onCurrentMonthDateListener;

    public WeekCalendar(Context context) {
        super(context);
        init(context, null);
    }

    public WeekCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * 初始化View
     */
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_calender, this, true);
        mIvPrevious = (RelativeLayout) findViewById(R.id.iv_previous);
        preBtn = (ImageView) findViewById(R.id.pre_btn);
        nextBtn = (ImageView) findViewById(R.id.next_btn);
        mTvYearMouth = (TextView) findViewById(R.id.tv_year_mouth);
        month_layout = (RelativeLayout) findViewById(R.id.month_layout);
        mIvNext = (RelativeLayout) findViewById(R.id.iv_next);
        mRvDay = (ViewFlipper) findViewById(R.id.rv_day);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WeekCalendar);
        daysSelectedTextColor = typedArray.getColor(R.styleable
                .WeekCalendar_daysSelectedTextColor, Color.WHITE);
        todayTextColor = typedArray.getColor(R.styleable
                .WeekCalendar_todayTextColor, Color.GRAY);
        weekTextColor = typedArray.getColor(R.styleable
                .WeekCalendar_weekTextColor, Color.GRAY);
        weekBackgroundColor = typedArray.getColor(R.styleable
                .WeekCalendar_weekBackgroundColor, Color.WHITE);
        monthBackgroundColor = typedArray.getColor(R.styleable.WeekCalendar_monthBackgroundColor, Color.LTGRAY);
        monthTextColor = typedArray.getColor(R.styleable.WeekCalendar_monthTextColor, Color.WHITE);
        daysTextColor = typedArray.getColor(R.styleable.WeekCalendar_daysTextColor, Color.GRAY);
        nextArrowBg = typedArray.getDrawable(R.styleable.WeekCalendar_nextArrowBg);
        preArrowBg = typedArray.getDrawable(R.styleable.WeekCalendar_preArrowBg);
        daysSelectedBackground = typedArray.getDrawable(R.styleable.WeekCalendar_daysSelectedBackground);
        cornerMarkBg = typedArray.getDrawable(R.styleable.WeekCalendar_cornerMarkBg);
        hideTodayName = typedArray.getBoolean(R.styleable.WeekCalendar_hideTodayName, false);
        isCornerMark = typedArray.getBoolean(R.styleable.WeekCalendar_isCornerMark, false);
        daysTextSize = typedArray.getDimension(R.styleable.WeekCalendar_daysTextSize, 16f);
        weekTextSize = typedArray.getDimension(R.styleable.WeekCalendar_weekTextSize, 16f);
        isShowMonth = typedArray.getBoolean(R.styleable.WeekCalendar_isShowMonth, true);
        initDatas();
        initView();
        typedArray.recycle();
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        calendarDatas = new ArrayList<>();
        getToday();//获取当天的数据
        theDayOfSelected = today;
        theDayForShow = today;
        getWholeMonthDatas(theDayOfSelected);
        weekPosition = WeekCalendarUtil.getTheWeekPosition(weeks, theDayOfSelected);
    }

    /**
     * 设置整个月的数据
     */
    private void getWholeMonthDatas(CalendarData data) {
        calendarDatas = WeekCalendarUtil.getWholeMonthDay(data);//获取某天所在的整个月的数据（包含用于显示的上个月的天数和下个月的天数）
        weeks = WeekCalendarUtil.getWholeWeeks(calendarDatas);//获取当月有几个星期，以及每一星期对应的数据星期数据
        if (onCurrentMonthDateListener != null) {
            onCurrentMonthDateListener.onCallbackMonthDate(String.valueOf(data.year), String.valueOf(data.month));
        }
        mTvYearMouth.setText(String.format("%s年%s月", String.valueOf(data.year), String.valueOf(data.month)));
    }

    /**
     * 初始化月控件
     */
    private void initView() {
        if (isShowMonth) {
            month_layout.setVisibility(View.VISIBLE);
        } else {
            month_layout.setVisibility(View.GONE);
        }

        if (nextArrowBg != null) {
            nextBtn.setBackgroundDrawable(nextArrowBg);
        }
        if (preArrowBg != null) {
            preBtn.setBackgroundDrawable(preArrowBg);
        }
        month_layout.setBackgroundColor(monthBackgroundColor);
        mTvYearMouth.setTextColor(monthTextColor);
        mIvPrevious.setOnClickListener(new OnClickListener() {//跳到上一个月
            @Override
            public void onClick(View v) {
                showLastView(false);//显示上一个星期/月
            }
        });
        mIvNext.setOnClickListener(new OnClickListener() {//跳到下一个月
            @Override
            public void onClick(View v) {
                showNextView(false);//显示下一个星期/月
            }
        });
        mGridView = addDayView();
        mGridView.setAdapter(new CalendarAdapter(context, weeks.get(weekPosition)));
        mRvDay.addView(mGridView, 0);
    }

    /**
     * 初始化日期
     */
    private GridView addDayView() {
        LayoutParams params = new LayoutParams(
                AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        final GridView gridView = new GridView(context);
        gridView.setNumColumns(7);
        gridView.setGravity(Gravity.CENTER_VERTICAL);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setVerticalSpacing(1);
        gridView.setHorizontalSpacing(1);
        gridView.setLayoutParams(params);
        return gridView;
    }

    public void setSelectDates(List<String> list) {
        selectDateList = list;
    }


    /**
     * 获取今天的参数
     */
    private void getToday() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        String currentDate = sdf.format(date);
        int year = Integer.parseInt(currentDate.split("-")[0]);
        int month = Integer.parseInt(currentDate.split("-")[1]);
        int day = Integer.parseInt(currentDate.split("-")[2]);
        today = new CalendarData(year, month, day);

    }


    private float mLastX = -1;

    /**
     * scrollview的ontouchEvent和其他的ViewGroup的方法还是很大不同， 该方法一般和ontouchEvent 一起用
     * onInterceptTouchEvent()主要功能是控制触摸事件的分发，例如是子视图的点击事件还是滑动事件。
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = mLastX - event.getRawX(); //X移动坐标
                if (dx > 80) { // 向左滑
                    showNextView(true);//显示下一个星期/月
                    return true;
                } else if (dx < -80) {
                    showLastView(true);//显示上一个星期/月
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }


    /**
     * 显示下一个星期/月的数据
     */
    public void showNextView(boolean isShowNextWeek) {
        GridView mGridView = addDayView();
        mGridView.setAdapter(new CalendarAdapter(context, getNextWeekDatas(isShowNextWeek)));
        mRvDay.addView(mGridView, 1);
        mRvDay.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_in));
        mRvDay.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_out));
        mRvDay.showNext();
        mRvDay.removeViewAt(0);
    }

    /**
     * 显示上一个星期/月的数据
     */
    public void showLastView(boolean isShowLastWeek) {
        GridView mGridView = addDayView();
        mGridView.setAdapter(new CalendarAdapter(context, getLastWeekDatas(isShowLastWeek)));
        mRvDay.addView(mGridView, 1);
        mRvDay.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.push_right_in));
        mRvDay.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.push_right_out));
        mRvDay.showNext();
        mRvDay.removeViewAt(0);
    }

    /**
     * 获取下个星期的数据
     *
     * @param isShowNextWeek true 获取下各星期的数据，如果false 直接获取下各月第一个星期的数据
     * @return
     */
    public List<CalendarData> getNextWeekDatas(boolean isShowNextWeek) {
        if (weekPosition == weeks.size() - 1 || !isShowNextWeek) {//最后一个星期，加载下一个月的数据，或者直接获取下个月
            theDayForShow = (theDayForShow.isNextMonthDay) ? theDayForShow : WeekCalendarUtil.getTheDayOfNextMonth(theDayForShow);
            getWholeMonthDatas(theDayForShow);
            // 为了让数据连贯，直接跳到第二个星期，这里可能没有数据交叉的情况，不跳到第二个星期 判断这个月的第一天是否是星期天
            weekPosition = (WeekCalendarUtil.getWeekdayOfFirstDayInMonth(theDayForShow) == 0 || !isShowNextWeek) ? 0 : 1;
        } else {
            weekPosition++;
        }

        return (List<CalendarData>) weeks.get(weekPosition);
    }

    public List<CalendarData> getLastWeekDatas(boolean isShowLastWeek) {
        if (weekPosition == 0 || !isShowLastWeek) {//第一个星期，加载上一个月的数据,或者直接获取上个月
            theDayForShow = (theDayForShow.isLastMonthDay) ? theDayForShow : WeekCalendarUtil.getTheDayOfLastMonth(theDayForShow);
            getWholeMonthDatas(theDayForShow);
            if (isShowLastWeek) {
                // 为了让数据连贯，直接跳到倒数第二个星期，这里可能没有数据交叉的情况，不跳倒数到第二个星期，判断这个月的最后是否是星期六
                weekPosition = weeks.size() - ((WeekCalendarUtil.getWeekdayOfEndDayInMonth(theDayForShow) == 6) ? 1 : 2);
            } else {//直接获取上个月
                weekPosition = 0;
            }
        } else {
            weekPosition--;
        }
        return (List<CalendarData>) weeks.get(weekPosition);
    }


    /**
     * 日期列表适配器
     */
    public class CalendarAdapter extends SimpleBaseAdapter {
        List<CalendarData> datas;

        public CalendarAdapter(Context context, List<CalendarData> datas) {
            super(context, datas);
            this.datas = datas;
        }

        @Override
        public int getItemResource() {
            return R.layout.item_calendar;
        }

        @Override
        public View getItemView(final int position, View convertView, final ViewHolder viewHolder) {
            final CalendarData calendar = (CalendarData) getItem(position);
            final TextView dayView = (TextView) viewHolder.getView(R.id.tv_calendar_day);
            final TextView weekView = (TextView) viewHolder.getView(R.id.tv_calendar_week);
            ImageView corner_mark_iv = (ImageView) viewHolder.getView(R.id.corner_mark_iv);
            weekView.setText(WeekCalendarUtil.getWeekString(mContext).get(position));
            dayView.setTextSize(daysTextSize);
            weekView.setTextSize(weekTextSize);
            weekView.setBackgroundColor(weekBackgroundColor);
            if (isCornerMark) {
                corner_mark_iv.setBackgroundDrawable(cornerMarkBg);
            } else {
                corner_mark_iv.setVisibility(View.GONE);
            }
            if (!hideTodayName) {
                if (!calendar.isSameDay(today)) {
                    dayView.setText(String.valueOf(calendar.day));
                } else {
                    dayView.setText(String.valueOf("今"));
                }
            } else {
                dayView.setText(String.valueOf(calendar.day));
            }
            if (calendar.isSameDay(theDayOfSelected)) {//被选中的日期是白的
                dayView.setTextColor(daysSelectedTextColor);
                dayView.setBackgroundDrawable(daysSelectedBackground);
            } else if (calendar.isLastMonthDay || calendar.isNextMonthDay) {//上一个月、下一个月的日期是灰色的
                dayView.setTextColor(Color.LTGRAY);
                dayView.setBackgroundDrawable(null);
            } else if (calendar.isSameDay(today)) {//当天的日期是橘黄色的
                dayView.setTextColor(todayTextColor);
                dayView.setText("今");
                dayView.setBackgroundDrawable(null);
            } else {
                dayView.setBackgroundDrawable(null);
                dayView.setTextColor(weekTextColor);
            }

            if (selectDateList != null) {
                for (int i = 0; i < selectDateList.size(); i++) {
                    String dateStr = selectDateList.get(i);
                    String[] dates = dateStr.split("-");
                    CalendarData cd = new CalendarData();
                    cd.year = Integer.parseInt(dates[0]);
                    cd.month = Integer.parseInt(dates[1]);
                    cd.day = Integer.parseInt(dates[2]);
                    if (calendar.isSameDay(cd)) {
                        corner_mark_iv.setVisibility(View.VISIBLE);
                        break;
                    } else {
                        corner_mark_iv.setVisibility(View.GONE);
                    }
                }
            }

            //如果设置了回调，则设置点击事件
            dayView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    theDayOfSelected = datas.get(position);
                    theDayForShow = datas.get(position);
                    notifyDataSetChanged();
                    if (listener != null) {
                        listener.onDateClick(getTheDayOfSelected());
                    }
                }
            });
            return convertView;
        }
    }


    /**
     * 点击选中日期的回调接口
     */
    public interface OnDateClickListener {
        void onDateClick(String time);
    }

    /**
     * 回调当前显示的年月
     */
    public interface OnCurrentMonthDateListener {
        void onCallbackMonthDate(String year, String month);
    }

    /**
     * 设置显示当前月份的回调接口方法
     *
     * @param onCurrentMonthDateListener
     */
    public void setOnCurrentMonthDateListener(OnCurrentMonthDateListener onCurrentMonthDateListener) {
        this.onCurrentMonthDateListener = onCurrentMonthDateListener;
    }

    /**
     * 设置回调接口
     */
    public void setOnDateClickListener(OnDateClickListener listener) {
        this.listener = listener;
    }

    /**
     * 获取被选中日期的日期字符串
     *
     * @return
     */
    private String getTheDayOfSelected() {
        if (theDayOfSelected != null) {
            String sYear = String.valueOf(theDayOfSelected.year);
            String sMonth = String.valueOf(theDayOfSelected.month);
            String sDay = String.valueOf(theDayOfSelected.day);
            return String.format("%s-%s-%s", sYear, (2 > sMonth.length()) ? "0" + sMonth : "" + sMonth, (2 > sDay.length()) ? "0" + sDay : "" + sDay);
        }
        return "";
    }

    /**
     * 判断当前选中日期是否是今天
     *
     * @return
     */
    public boolean isTodayIsSelectedDay() {
        return today.isSameDay(theDayForShow) && today.isSameDay(theDayOfSelected);
    }


    /**
     * 判断显示的是否是今天，如果不是，跳转到今天
     *
     * @return 是否是今天
     */
    public boolean showToday() {
        if (!isTodayIsSelectedDay() || weekPosition != WeekCalendarUtil.getTheWeekPosition(weeks, today)) {//如果显示的不是当天
            int mode = 0;//动画模式（0 没有动画，1 向左动 ， 2 向右动）
            if (theDayForShow.year > today.year || theDayForShow.month > today.month) {//下一个月
                getWholeMonthDatas(today);
                weekPosition = WeekCalendarUtil.getTheWeekPosition(weeks, today);
                mode = 2;
            } else if (theDayForShow.year < today.year || theDayForShow.month < today.month) {//上一个月
                getWholeMonthDatas(today);
                weekPosition = WeekCalendarUtil.getTheWeekPosition(weeks, today);
                mode = 1;
            } else {//本月
                int position = WeekCalendarUtil.getTheWeekPosition(weeks, today);
                if (weekPosition < position) {//上个星期
                    mode = 1;
                } else if (weekPosition > position) {//下个星期
                    mode = 2;
                }
                weekPosition = position;
            }

            theDayOfSelected = today;
            theDayForShow = today;
            GridView mGridView = addDayView();
            mGridView.setAdapter(new CalendarAdapter(context, weeks.get(weekPosition)));
            mRvDay.addView(mGridView, 1);
            if (mode == 2) {
                mRvDay.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.push_right_in));
                mRvDay.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.push_right_out));
            } else if (mode == 1) {
                mRvDay.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_in));
                mRvDay.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_out));
            } else {
                mRvDay.setInAnimation(null);
                mRvDay.setOutAnimation(null);
            }
            mRvDay.showNext();
            mRvDay.removeViewAt(0);
            return false;
        }
        return true;
    }

}
