package com.example.xwn.calendarmanager;

/**
 * Created by xwn on 2016/11/23.
 */

public class TimeLast {
    private long start;
    private long last;

    public TimeLast(long start, long last) {
        this.start = start;
        this.last = last;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getLast() {
        return last;
    }

    public void setLast(long last) {
        this.last = last;
    }

    @Override
    public String toString() {
        return start+"\t"+last;
    }
}
