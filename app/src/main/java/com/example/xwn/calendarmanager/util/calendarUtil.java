package com.example.xwn.calendarmanager.util;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import com.example.xwn.calendarmanager.ClassRecord;
import com.example.xwn.calendarmanager.EventRecord;
import com.example.xwn.calendarmanager.MainActivity;
import com.example.xwn.calendarmanager.TimeLast;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by xwn on 2016/11/22.
 */

public class CalendarUtil {
    private final static String TAG = "CalendarUtil";

    //用到的相关URL
    private static String calanderURL = "content://com.android.calendar/calendars";
    private static String calanderEventURL = "content://com.android.calendar/events";
    private static String calanderRemiderURL = "content://com.android.calendar/reminders";

    //查询现有日历账户并返回ID，若查不到结果则返回"error"
    public static String queryAccountName(Context context, String queryName){
        Cursor userCursor = context.getContentResolver().query(Uri.parse(calanderURL), null, null, null, null);

        Log.d(TAG, "Count: " + userCursor.getCount());
        //Toast.makeText(this, "Count: " + userCursor.getCount(), Toast.LENGTH_LONG).show();

        for (userCursor.moveToFirst(); !userCursor.isAfterLast(); userCursor.moveToNext()) {
            String userName1 = userCursor.getString(userCursor.getColumnIndex("name"));
            String userName0 = userCursor.getString(userCursor.getColumnIndex("ACCOUNT_NAME"));
            String userName2 = userCursor.getString(userCursor.getColumnIndex("_id"));
            Log.d(TAG, "NAME: " + userName1 + " -- ACCOUNT_NAME: " + userName0+"-- ID："+userName2);
            if (userName0.equals(queryName)) return userName2;
        }
        return "error";
    }
    //添加新的日历账户,需要外部输入账户名，显示名，用户名（这个用登录者的账号）
    public static String inputAccount(Context context,String accountName,String displayName,String owner_account){
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, "CalendarManager");

        value.put(CalendarContract.Calendars.ACCOUNT_NAME, accountName);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, "com.android.exchange");
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, displayName);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, -9206951);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, owner_account);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = CalendarContract.Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, owner_account)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.android.exchange")
                .build();

        context.getContentResolver().insert(calendarUri, value);
        return queryAccountName(context,accountName);
    }
    //添加事件 需要输入日历的ID，课程的课程名-title，课程的上课老师-description，课程的地点-eventLocation，开始时间-dtstart，结束时间-dtend,
    public static void writeEvent(Context context, String calId, String className, String classTeacher, String classLocation, Date dateStart, Date dateEnd){
        //获取要出入的账户的id
        Cursor userCursor = context.getContentResolver().query(Uri.parse(calanderURL),null,null,null,null);

        ContentValues event = new ContentValues();
        event.put("title",className);
        event.put("description",classTeacher);
        event.put("calendar_id",calId);
        Log.d(TAG, "calId: "+calId);
        event.put("eventLocation",classLocation);

        long start = dateStart.getTime();
        long end = dateEnd.getTime();

        event.put("dtstart",start);
        event.put("dtend",end);
        event.put("hasAlarm",1);

        event.put(CalendarContract.Events.EVENT_TIMEZONE,"Asia/BeiJing");
        //添加事件
        Uri newEvent = context.getContentResolver().insert(Uri.parse(calanderEventURL),event);
        //时间提醒的设定
        long id = Long.parseLong(newEvent.getLastPathSegment());
        ContentValues values = new ContentValues();
        values.put("event_id",id);
        //提前10分钟有提醒
        values.put("minutes",10);
        context.getContentResolver().insert(Uri.parse(calanderRemiderURL),values);
    }
    public static void writeEvent(Context context, String calId, EventRecord eventRecord){
        writeEvent(context,calId,eventRecord.getEventTitle(), eventRecord.getEventDescription(),eventRecord.getEventLocation(),
                new Date(eventRecord.getEventDate().getTime()+eventRecord.getEventStartMilliSecond()),
                new Date(eventRecord.getEventDate().getTime()+eventRecord.getEventStartMilliSecond()+eventRecord.getEventLastMilliSecond()));
    }
    public static void writeEvent(Context context, String calId, ClassRecord classRecord, Date startDate, SimpleDateFormat sdf){
        for (int i=0;i<classRecord.getWeekNumber().size();i++){
            for (int j=0;j<classRecord.getDayNumber().size();j++){
                Date date = OtherUtil.calculateDateByWeeks(startDate,classRecord.getWeekNumber().get(i),classRecord.getDayNumber().get(j),sdf);
                for (int m=0;m<classRecord.getClassTime().size();m++){
                    TimeLast timeLast = new TimeLast(0l,0l);
                    switch (classRecord.getClassTime().get(m)-1){
                        case 0:
                            timeLast = new TimeLast(8l*60l*60l*1000l,100l*60l*1000l);
                            break;
                        case 1:
                            timeLast = new TimeLast((10*60l+10l)*60l*1000l,100l*60l*1000l);
                            break;
                        case 2:
                            timeLast = new TimeLast((14l*60l+0l)*60l*1000l,95l*60l*1000l);
                            break;
                        case 3:
                            timeLast = new TimeLast((15l*60l+55l)*60l*1000l,95l*60l*1000l);
                            break;
                        case 4:
                            timeLast = new TimeLast((18l*60l+30l)*60l*1000l,95l*60l*1000l);
                            break;
                        case 5:
                            timeLast = new TimeLast((20l*60l+15l)*60l*1000l,95l*60l*1000l);
                            break;
                    }
                    Log.d(TAG, "在以下日期中插入: "+sdf.format(date));
                    writeEvent(context,calId,classRecord.getClassTitle(),"老师："+classRecord.getClassTeacher(),classRecord.getClassLocation(),new Date(date.getTime()+timeLast.getStart()),new Date(date.getTime()+timeLast.getStart()+timeLast.getLast()));
                }
            }
        }
    }
}
