package com.zjianhao.holder;

import android.util.Log;

import java.util.ArrayList;

public class SettingHolder {
    public static final int SETTING_TIME = 1;
    public static final int SETTING_LOCATION = 2;
    public static final int SUB_SETTING_YEAR = 1;
    public static final int SUB_SETTING_MONTH = 2;
    public static final int SUB_SETTING_DAY = 3;
    private String cloud_directory;
    private String local_directory;
    private int sort_type = 0;
    private int sort_time_type = SUB_SETTING_YEAR;
    private int timePicker_HourOfDay;  // 업로드 시간설정 할 때, 선택한 Hour을 저장하는 변수
    private int timePicker_Minute; // 선택한 Minute를 저장하는 변수
    private ArrayList Selected_Upload_Day = new ArrayList();  // 체크된 요일들을 저장할 arr
    private String MON = "MON", TUE = "TUE", WED = "WED", THU = "THU", FRI = "FRI", SAT = "SAT", SUN = "SUN";

    public void clear_Selected_Upload_Day() { // Arraylist 클리어
        Selected_Upload_Day.clear();
    }
    public void add_Selected_Upload_Day(String str) { // Arraylist에 원소 추가
        Selected_Upload_Day.add(str);
    }
    public int size_Selected_Upload_Day() { // Arraylist의 크기 반환
        return Selected_Upload_Day.size();
    }
    public Object get_Selected_Upload_Day(int index) { // Arraylist의 해당 인덱스에 해당하는 값 반환
        return Selected_Upload_Day.get(index);
    }

    // 타임피커에서 hour의 getter, setter
    public int getTimePicker_HourOfDay() {
        return timePicker_HourOfDay;
    }
    public boolean setTimePicker_HourOfDay(int timePicker_HourOfDay) {
        this.timePicker_HourOfDay = timePicker_HourOfDay;
        return true;
    }
    // 타임피커에서 minute의 getter, setter
    public int getTimePicker_Minute() {
        return timePicker_Minute;
    }
    public boolean setTimePicker_Minute(int timePicker_Minute) {
        this.timePicker_Minute = timePicker_Minute;
        return true;
    }

    /**
     * sort_time_type getter
     *
     * @return sort_time_type
     */
    public int getSort_time_type() {
        return sort_time_type;
    }

    /**
     * sort_time_type setter
     *
     * @param n
     * @return true(success), false(fail: No Type Exists)
     */
    public boolean setSort_time_type(int n) {
        if (n >= SUB_SETTING_YEAR && n <= SUB_SETTING_DAY && sort_type == SETTING_TIME) {
            sort_time_type = n;
            return true;
        } else {
            return false;
        }
    }

    /**
     * sort_type getter
     *
     * @return sort_type
     */
    public int getSort_type() {
        return sort_type;
    }

    /**
     * sort_type setter
     *
     * @param type
     * @return true(success), false(fail: No Type Exists)
     */
    public boolean setSort_type(int type) {
        if (type >= 1 && type <= 2) {
            sort_type = type;
            return true;
        } else {
            return false; // Not an Option Error
        }
    }

    /**
     * local_directory getter
     *
     * @return local_directory
     */
    public String getLocal_directory() {
        return local_directory;
    }

    /**
     * local_directory setter
     *
     * @param str
     * @return true(success), false(fail: directory doesn't exist)
     */
    public boolean setLocal_directory(String str) {
        /*
        Check Directory Exists
         */
        local_directory = str;
        return true;
    }

    /**
     * cloud_directory getter
     *
     * @return cloud_directory
     */
    public String getCloud_directory() {
        return cloud_directory;
    }

    /**
     * cloud_directory setter
     *
     * @param str
     * @return true(success), false(fail: directory doesn't exist)
     */
    public boolean setCloud_directory(String str) {
        /*
        Check Directory Exists
         */
        cloud_directory = str;
        return true;
    }

    public String getCheckbox(int i) {
        switch (i) {
            case 0:
                return MON;
            case 1:
                return TUE;
            case 2:
                return WED;
            case 3:
                return THU;
            case 4:
                return FRI;
            case 5:
                return SAT;
            case 6:
                return SUN;
            default:
                Log.v("checkBox error", "checkbox error");
                return null;
        }
    }
}