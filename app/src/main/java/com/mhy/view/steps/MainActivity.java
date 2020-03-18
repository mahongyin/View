package com.mhy.view.steps;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mhy.view.R;
import com.mhy.view.paypass.PayPassDialog;
import com.mhy.view.paypass.PayPassView;
import com.mhy.view.utils.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements StepsView.MyViewClick {
    StepsView stepsView;
    TextView tevNotif;
    TextView tevNotif1;
    public static StepsView.MyViewClick mViewClick;
    private ArrayList<StepBean> mStepBeans = new ArrayList<>();
    private List<Integer> signinlist = new ArrayList<>();
    private List<Integer> signinStates = new ArrayList<>();
    int day = -1;//今天在一周的角标
Activity context;
    String[] week = {"一", "二", "三", "四", "五", "六", "日"};
    int showNum = 5;////VIP 1.2倍 SVIP 2倍  ADD 根据身份 区分

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        //真被虚假数据
        signinlist.add(1);//周一 0未签到  1已签到
        signinlist.add(0);
        signinlist.add(1);
        signinlist.add(0);//假如今天周四
        signinlist.add(0);
        signinlist.add(0);
        signinlist.add(0);

        initView();
    }


    /**
     * 周四和周日显示提示
     */
    private void initView() {
        stepsView = findViewById(R.id.step_up);

        tevNotif = findViewById(R.id.tev_notif);
        tevNotif1 = findViewById(R.id.tev_notif1);
        tevNotif.setText("连续签到有惊喜哦");
        tevNotif1.setText("连续签到积分翻倍");
        stepsView.setViewClick(this);

        if (false) {//超级会员2
            showNum = 10;

        }
        if (false) {//会员1.2
            showNum = 6;

        }
        if (true) {//非会员1
            showNum = 5;

        }
        //是会员就传入会员该增加的积分
        setView(showNum);

    }

    //ADD 前四天是否连续签到 是的话周四 积分翻倍
    public boolean is4LinkSigin() {
        //前三天是否连续 周四 时签则才翻倍
        return signinlist.get(0) == 1 && signinlist.get(1) == 1 && signinlist.get(2) == 1 && signinlist.get(3) == 1;
    }

    //ADD 判断是否连续签到
    public boolean is7LinkSigin() {
        //前6天是否连续 周7时签则才有惊喜
        return signinlist.get(0) == 1 && signinlist.get(1) == 1 && signinlist.get(2) == 1 && signinlist.get(3) == 1 && signinlist.get(4) == 1 && signinlist.get(5) == 1 && signinlist.get(6) == 1;
    }

    /**
     * 设置签到view 服务器获取状态0未完/1已完
     * 自定义状态 0进行中(当天未签及后边)、1已完成(当天已签及前边)、-1 未完成(当天前的)
     * showNum 签到增加积分数
     */
    public void setView(int showNum) {
        //今天是一周第几天的角标  // 周二->postion=1
        day = DateUtil.postionToWeek(DateUtil.getdateForm()) - 1;
        Log.e("周几角标", day + "");
        String upText;
        //清空数据
        signinStates.clear();
        mStepBeans.clear();
        //区分 今天之前 、今天 、今天之后 //0未签,1签
        for (int i = 0; i < signinlist.size(); i++) {
            if (i < day) {
//              今天之前//已签 1  未签0=补签
                signinStates.add(signinlist.get(i));
            } else if (i == day) {
                // 今 如果未签0》-1当前》显示签到
                signinStates.add(signinlist.get(i) == 0 ? StepBean.STEP_CURRENT : StepBean.STEP_COMPLETED);
            } else if (i > day) {
                //今天之后 2
                signinStates.add(StepBean.STEP_TODO);
            }
            //已签1 补签0 今-1 待签2   swith每条状态
            switch (signinStates.get(i)) {
                case StepBean.STEP_UNDO://0 未
                    upText = "补签";
                    break;
                case StepBean.STEP_COMPLETED://1已签
//                        //今天前已签的
                    upText = "+" + showNum;//5 6 10
                    if (is4LinkSigin() && i == 3) {//周四翻倍 前提连续签到
                        upText = "+" + 2 * showNum;//10 12 20
                    }
                    if (is7LinkSigin() && i == 6) {//周日惊喜 前提连续签到
                        upText = "+" + 2 * showNum;//10 12 20
                    }
                    break;
                //2待签
                case StepBean.STEP_TODO:
                    upText = "+" + showNum;
                    break;
                case StepBean.STEP_CURRENT:
                    //-1今
                    upText = "签到";
                    //今天没签到显示签到 签到了就不在这了-》状态就变成1了 > +5
                    break;
                default:
                    upText = "+" + showNum;
                    break;
            }
            // 0未签补签 1已签 -1今(未)签到 2待签
            //每天数据对应载入
            mStepBeans.add(new StepBean(signinStates.get(i), upText, "周" + week[i]));
        }

        //VIew 显示数据
        stepsView.setStepNum(mStepBeans);
        if (day > 3) {//今天时周567显示周日提示
            tevNotif1.setVisibility(View.GONE);
            tevNotif.setVisibility(View.VISIBLE);
        } else {//今天时周4及之前显示周4提示
            tevNotif.setVisibility(View.GONE);
            tevNotif1.setVisibility(View.VISIBLE);
        }

    }

    //postion 点击周几的角标
    @Override
    public void onViewClick(int postion) {
        //如果签到过了就不给点击事件了
        if (signinlist.get(postion) == StepBean.STEP_COMPLETED) {//已签到状态标志1
            Toast.makeText(MainActivity.this, "不可重复签到", Toast.LENGTH_SHORT).show();
        } else {//下面是没签到的
            Log.e("点击", day + "day天postion" + postion);
            //补签
            if (postion < day) { //补签
                //未签 补签
                if (/*"可以补签"*/false) {
                    //今或之前 后面人日子还没到
                    Toast.makeText(MainActivity.this, "补签成功", Toast.LENGTH_SHORT).show();
                    signinlist.set(postion, 1);//更新数据
                    setView(5);//更新view
                    //更新view
                } else {
                    //开会员
                    payDialog();//顺便弹个窗
                }
            }
            if (day == postion) {//签到
                //当天签到
                Toast.makeText(MainActivity.this, "签到成功", Toast.LENGTH_SHORT).show();
                signinlist.set(postion, 1);//更新数据
                setView(5);//更新view
            }
            if (postion > day) {//今天之后
                Toast.makeText(MainActivity.this, "记得明天来签到哦", Toast.LENGTH_SHORT).show();

            }
        }
    }

    /**
     * 1 默认方式
     */
    PayPassDialog dialogPay;

    private void payDialog() {
        dialogPay = new PayPassDialog(this);
        dialogPay.getPayViewPass()
                .setPayClickListener(new PayPassView.OnPayClickListener() {
                    @Override
                    public void onPassFinish(String passContent) {
                        dialogPay.dismiss();
                        //6位 输入完成回调
//                        showToast("输入完成回调"+passContent);
                      //支付成功需关闭
                    }

                    @Override
                    public void onPayClose() {
                        dialogPay.dismiss();
                        //关闭回调
                    }

                    @Override
                    public void onPayForget() {
                        dialogPay.dismiss();
                        //点击忘记密码回调
                        //showToast("忘记密码回调");
                    }
                });
    }

    /**
     * 2 自定义方式
     */
    private void payDialog2() {
        final PayPassDialog dialog = new PayPassDialog(this, R.style.dialog_pay_theme);
        //弹框自定义配置
        dialog.setAlertDialog(false)
                .setWindowSize(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f)
                .setOutColse(false)
                .setGravity(R.style.dialogOpenAnimation, Gravity.BOTTOM);
        //组合控件自定义配置
        PayPassView payView = dialog.getPayViewPass();
        payView.setForgetText("忘记支付密码?");
        payView.setForgetColor(getResources().getColor(R.color.colorAccent));
        payView.setForgetSize(16);
        payView.setPayClickListener(new PayPassView.OnPayClickListener() {
            @Override
            public void onPassFinish(String passContent) {
                //6位输入完成回调
                //showToast("输入完成回调");
            }

            @Override
            public void onPayClose() {
                dialog.dismiss();
                //关闭回调
            }

            @Override
            public void onPayForget() {
                dialog.dismiss();
                //忘记密码回调
               // showToast("忘记密码回调");
            }
        });
    }

}
