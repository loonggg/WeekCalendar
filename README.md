# WeekCalendar
周日历，Weekly Calendar。
## 效果图
![](https://raw.githubusercontent.com/loonggg/WeekCalendar/master/image/ss.gif)
## 使用方法（usage）
### Step 1. Add the JitPack repository to your build file
```java
Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```

### Step 2. Add the dependency
```java
	dependencies {
	    compile 'com.github.loonggg:WeekCalendar:v1.2'
	}
  ```
  
### Step 3. There are a few xml attributes to customise the calendar
* daysTextSize 设置日期中“天day”的文字大小
* todayTextColor 设置日期中当天文字的颜色
* daysTextColor 设置日期中“天day”的文字颜色
* daysSelectedTextColor 设置选中的日期的文字颜色
* daysSelectedBackground 设置选中的日期的背景
* weekTextSize 设置星期栏的字体大小
* weekTextColor 设置星期一栏中的字体颜色
* weekBackgroundColor 设置星期一栏背景
* hideTodayName 是否显示当天日期时的日期为“今”字，否则显示日期数字
* isCornerMark 是否显示选中的日期中的右上角的角标
* cornerMarkBg 设置角标图片
* monthBackgroundColor 设置年月栏的背景色
* monthTextColor 设置年月字体颜色
* nextArrowBg 设置年月栏下一个月的按钮背景
* preArrowBg 设置上一个月的按钮背景
* isShowMonth 是否显示年月一栏

#### Example
```xml
 <com.loonggg.weekcalendar.view.WeekCalendar
        android:id="@+id/week_calendar"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:background="#ffffff"
        android:paddingBottom="10dp"
        app:cornerMarkBg="@mipmap/order_book_icon"
        app:daysSelectedBackground="@drawable/green_oval_bg"
        app:isCornerMark="true"
        app:monthBackgroundColor="#8F83F1"
        app:nextArrowBg="@mipmap/white_right_arrow"
        app:preArrowBg="@mipmap/white_left_arrow" />
 ```
### Step 4. Impelement Listener 
 ```java
weekCalendar = (WeekCalendar) findViewById(R.id.week_calendar);
List<String> list = new ArrayList<>();
list.add("2016-09-13");
list.add("2016-10-13");
list.add("2016-10-11");
list.add("2016-10-10");
list.add("2016-10-16");
//传入已经预约或者曾经要展示选中的时间列表
weekCalendar.setSelectDates(list);
//设置日历点击事件
weekCalendar.setOnDateClickListener(new WeekCalendar.OnDateClickListener() {
      @Override
      public void onDateClick(String time) {
          Toast.makeText(MainActivity.this, time, Toast.LENGTH_SHORT).show();
      }
});
 ```

