package com.meetrend.haopingdian.util;

public class LineChartViewUtil {

    public static  long makeYKEDUMaxValue(long max,int chushu){

        long maxt = max;

        while (true) {
            if (max/4%chushu == 0 && max > maxt) {
                break;
            }
            max++;
        }
        return max;
    }

}
