package com.example.myweather2;

import java.text.ParseException;
import java.util.Calendar;

/**
 * Created by 董少龙 on 2019/9/30.
 */

public class TimeNow {
    private String res;

    public TimeNow()throws ParseException{
        Calendar today = Calendar.getInstance();
        Lunar lunar = new Lunar(today);
        int month=today.get(Calendar.MONTH)+1;
        int day=today.get(Calendar.DATE);
        int week=today.get(Calendar.DAY_OF_WEEK)-1;
        String week1=new String("日一二三四五六");
        String week2=week1.substring(week,week+1);
        res=month+"月"+day+"日"+" 周"+week2+" 农历"+lunar.toString();
    }

    public String getRes() {
        return res;
    }
}
