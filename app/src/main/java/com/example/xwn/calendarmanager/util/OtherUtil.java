package com.example.xwn.calendarmanager.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xwn on 2016/11/27.
 */

public class OtherUtil {
    private static final String TAG = "OtherUtil";

    //一个能把Integer数组转化成
    public static String integerListToString(List<Integer> integers){

        StringBuffer stringBuffer = new StringBuffer();
        int i=0,j=1;
        while (true){
            if ((j-i)==(integers.get(j)-integers.get(i))){
                j++;
            }else {
                if (i==j-1) {
                    stringBuffer.append((stringBuffer.length()==0?"":"、")+integers.get(i));
                }else {
                    stringBuffer.append((stringBuffer.length()==0?"":"、")+integers.get(i) + " - " + integers.get(j - 1));
                }
                i=j;
                j=i+1;
            }
            if (j==integers.size()){
                if (i==j-1) {
                    stringBuffer.append((stringBuffer.length()==0?"":"、")+integers.get(i));
                }else {
                    stringBuffer.append((stringBuffer.length()==0?"":"、")+integers.get(i) + " - " + integers.get(j - 1));
                }
                break;
            }
        }
        return stringBuffer.toString();
    }

    //将周次信息转成日期信息
    //输入参数：起始日期、周次、星期几、用于转换的日期格式
    public static Date calculateDateByWeeks(Date startDate, int weeks, int days, SimpleDateFormat sdf){
        long intervalSeconds = ((weeks-1)*7+(days-1))*1000l*60l*60l*24l;
        Date date = new Date(startDate.getTime()+intervalSeconds);
        Log.d(TAG, "calculateDateByWeeks: "+date.getTime());
//        return sdf.format(date);
        return date;
    }
    //将日期信息转成周次信息
    //输入参数：起始日期、需要转换的日期、用户转换的日期格式
    public static String calculateWeeksByDate(Date startDate, Date date, SimpleDateFormat sdf){
        long intervalDays = (date.getTime()-startDate.getTime())/(1000*60*60*24);
        long intervalWeeks = intervalDays/7+1;
        return intervalWeeks+"-"+(intervalDays%7+1);
    }
}
