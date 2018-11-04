package com.zjianhao.utils;

/**
 * Created by 张建浩（Clarence) on 2016-6-28 20:41.
 * the author's website:http://www.zjianhao.cn
 * the author's github: https://github.com/zhangjianhao
 */
public class DateUtil {


    public static boolean isDate(String keyword) {
        if (keyword.matches("\\d{4}year"))
            return true;
        if (keyword.matches("\\d{2}month") || keyword.matches("\\dmonth"))
            return true;
        if (keyword.matches("\\d{2}day") || keyword.matches("\\dday"))
            return true;
        if (keyword.matches("\\d{4}-\\d{1,2}") || keyword.matches("\\d{4}year\\d{1,2}month"))
            return true;
        if (keyword.matches("\\d{1,2}-\\d{1,2}") || keyword.matches("\\d{1,2}year\\d{1,2}month*"))
            return true;

        if (keyword.matches("\\d{4}-\\d{1,2}-\\d{1,2}") ||
                keyword.matches("\\d{4}year\\d{1,2}month\\d{1,2}day*"))
            return true;
        return false;
    }


    public static String parseToDate(String keyword){
        keyword = keyword.replace("year","-").replace("month","-").replace("day","");
        String[] split = keyword.split("-");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < split.length; i++) {

            if (Integer.parseInt(split[i]) < 10){
                if (i== split.length-1)
                    builder.append("0"+split[i]);
                else
                    builder.append("0"+split[i]+"-");

            }
            else {
                if (i== split.length-1)
                    builder.append(split[i]);
                else builder.append(split[i]+"-");
            }
        }
        return builder.toString();

    }


}
