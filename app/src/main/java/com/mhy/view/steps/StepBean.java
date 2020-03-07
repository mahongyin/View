package com.mhy.view.steps;

public class StepBean {
    /**
     * 未签、补签 标志
     */
    public static final int STEP_UNDO = 0;
    /**
     * 当天已签及前边已签
     */
    public static final int STEP_COMPLETED = 1;
    /**
     * 当天未签标志
     */
    public static final int STEP_CURRENT = -1;
    /**
     * 今日后边待签的 和未签的
     */
    public static final int STEP_TODO = 2;
    // 0进行中(当天未签及后边)、1已完成(当天已签及前边)、-1 未完成(当天前的)
    private int state;//0未签补签 1已签 -1今天未签 2待签
    private String number;//上面数字文字
    private String day;//周

    //状态state 上面number上面数字 day下面日期
    public StepBean(int state, String number, String day) {
        this.state = state;
        this.number = number;//签到,补签、积分
        this.day = day;
    }


    public static int getStepUndo() {
        return STEP_UNDO;
    }

    public static int getStepCurrent() {
        return STEP_CURRENT;
    }

    public static int getStepCompleted() {
        return STEP_COMPLETED;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDay() {
        return day == null ? "" : day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
