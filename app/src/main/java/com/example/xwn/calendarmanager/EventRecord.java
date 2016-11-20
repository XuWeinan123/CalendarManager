package com.example.xwn.calendarmanager;

import java.util.Date;
import cn.bmob.v3.BmobObject;

/**
 * Created by xwn on 2016/11/19.
 */

public class EventRecord extends BmobObject {
    private String eventTitle;
    private Date eventDate;
    private int eventWeek;
    private int eventDay;
    private String eventLocation;
    public EventRecord(){

    }
    public EventRecord(String eventTitle, Date eventDate, int eventWeek, int eventDay, String eventLocation) {
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventWeek = eventWeek;
        this.eventDay = eventDay;
        this.eventLocation = eventLocation;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public int getEventWeek() {
        return eventWeek;
    }

    public void setEventWeek(int eventWeek) {
        this.eventWeek = eventWeek;
    }

    public int getEventDay() {
        return eventDay;
    }

    public void setEventDay(int eventDay) {
        this.eventDay = eventDay;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    @Override
    public String toString() {
        return "事件标题"+eventTitle+"\n事件地点"+eventLocation+"\n事件周次"+eventWeek+"\n事件周几"+eventDay+"\n事件日期"+eventDate;
    }
}
