package com.example.xwn.calendarmanager;

import java.util.Date;
import cn.bmob.v3.BmobObject;

/**
 * Created by xwn on 2016/11/19.
 */

public class EventRecord extends BmobObject {
    private String eventTitle;
    private Date eventDate;
    private String eventLocation;
    private String eventDescription;
    private long eventStartMilliSecond;
    private long eventLastMilliSecond;
    public EventRecord(){

    }
    public EventRecord(String eventTitle, Date eventDate, String eventLocation,String eventDescription,long eventStartMilliSecond,long eventLastMilliSecond) {
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.eventDescription = eventDescription;
        this.eventStartMilliSecond = eventStartMilliSecond;
        this.eventLastMilliSecond = eventLastMilliSecond;
    }
    public EventRecord(String eventTitle, Date eventDate, String eventLocation,String eventDescription,TimeLast timeLast) {
        this(eventTitle,eventDate,eventLocation,eventDescription,timeLast.getStart(),timeLast.getLast());
    }

    public long getEventStartMilliSecond() {
        return eventStartMilliSecond;
    }

    public void setEventStartMilliSecond(long eventStartMilliSecond) {
        this.eventStartMilliSecond = eventStartMilliSecond;
    }

    public long getEventLastMilliSecond() {
        return eventLastMilliSecond;
    }

    public void setEventLastMilliSecond(long eventLastMilliSecond) {
        this.eventLastMilliSecond = eventLastMilliSecond;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
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


    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    @Override
    public String toString() {
        return "事件标题"+eventTitle+"\n事件地点"+eventLocation+"\n事件日期"+eventDate+"\n事件描述"+eventDescription;
    }
}
