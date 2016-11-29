package com.example.xwn.calendarmanager;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by xwn on 2016/11/27.
 */

public class ClassRecord extends BmobObject{
    private String importantKey;
    private List<Integer> weekNumber;
    private List<Integer> dayNumber;
    private String classTitle;
    private List<Integer> classTime;
    private String classTeacher;
    private String classLocation;

    public String getImportantKey() {
        return importantKey;
    }

    public ClassRecord setImportantKey(String importantKey) {
        this.importantKey = importantKey;
        return this;
    }

    public List<Integer> getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(List<Integer> weekNumber) {
        this.weekNumber = weekNumber;
    }

    public List<Integer> getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(List<Integer> dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getClassTitle() {
        return classTitle;
    }

    public void setClassTitle(String classTitle) {
        this.classTitle = classTitle;
    }

    public List<Integer> getClassTime() {
        return classTime;
    }

    public void setClassTime(List<Integer> classTime) {
        this.classTime = classTime;
    }

    public String getClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(String classTeacher) {
        this.classTeacher = classTeacher;
    }

    public String getClassLocation() {
        return classLocation;
    }

    public void setClassLocation(String classLocation) {
        this.classLocation = classLocation;
    }

    @Override
    public String toString() {
        return "课程名："+classTitle+"\n课程地点："+classLocation+"\n课程老师："+classTeacher+"\n课程时间"+classTime+"\n课程在第"+weekNumber+"周，礼拜"+dayNumber+""+""+"";
    }
}
