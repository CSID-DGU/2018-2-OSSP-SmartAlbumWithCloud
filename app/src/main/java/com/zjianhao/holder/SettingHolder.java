package com.zjianhao.holder;

public class SettingHolder {
    public static final int SETTING_TIME = 1;
    public static final int SETTING_LOCATION =2;
    public static final int SUB_SETTING_YEAR = 1;
    public static final int SUB_SETTING_MONTH = 2;
    public static final int SUB_SETTING_DAY = 3;


    private String cloud_directory;
    private String local_directory;
    private int sort_type = 0;
    private int sort_time_type = SUB_SETTING_YEAR;

    /**
     * sort_time_type getter
     * @return sort_time_type
     */
    public int getSort_time_type(){
        return sort_time_type;
    }
    /**
     * sort_time_type setter
     * @param n
     * @return true(success), false(fail: No Type Exists)
     */
    public boolean setSort_time_type(int n){
        if( n>=SUB_SETTING_YEAR && n<=SUB_SETTING_DAY && sort_type == SETTING_TIME){
            sort_time_type = n;
            return true;
        }else{
            return false;
        }
    }
    /**
     * sort_type getter
     * @return sort_type
     */
    public int getSort_type(){
        return sort_type;
    }
    /**
     * sort_type setter
     * @param type
     * @return true(success), false(fail: No Type Exists)
     */
    public boolean setSort_type(int type){
        if(type>=1 && type <=2) {
            sort_type = type;
            return true;
        }else{
            return false; // Not an Option Error
        }
    }
    /**
     * local_directory getter
     * @return local_directory
     */
    public String getLocal_directory() {
        return local_directory;
    }
    /**
     * local_directory setter
     * @param str
     * @return true(success), false(fail: directory doesn't exist)
     */
    public boolean setLocal_directory(String str){
        /*
        Check Directory Exists
         */
        local_directory = str;
        return true;
    }

    /**
     * cloud_directory getter
     * @return cloud_directory
     */
    public String getCloud_directory(){
        return cloud_directory;
    }
    /**
     * cloud_directory setter
     * @param str
     * @return true(success), false(fail: directory doesn't exist)
     */
    public boolean setCloud_directory(String str){
        /*
        Check Directory Exists
         */
        cloud_directory = str;
        return true;
    }
}
