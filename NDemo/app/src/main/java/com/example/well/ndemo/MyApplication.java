package com.example.well.ndemo;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.example.well.ndemo.net.rxretrofit.RxRetrofitApp;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class MyApplication extends Application{
    public  Context context;
    public static MyApplication mApp = null;

    @Override
    public void onCreate() {
        super.onCreate();
        context =this;
        mApp=this;
        init();

    }

    private void init() {
        RxRetrofitApp.init(this);
        initBugly();
    }

    public static MyApplication getInstence(){
        return mApp;
    }

    private void initBugly() {

        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setAppChannel("myChannel");  //设置渠道
        strategy.setAppVersion("1.0.0");      //App的版本
        strategy.setAppPackageName("com.example.well.ndemo");  //App的包名
        strategy.setAppReportDelay(10000);   //默认为10s,改为20s

        //下面四句代码是为了避免在多进程情况下,每个进程下的Bugly都会进行数据上报，造成不必要的资源浪费,所以控制只在主进程下进行数据上报
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
         // 设置是否为上报进程
        strategy.setUploadProcess(processName == null || processName.equals(packageName));

        CrashReport.initCrashReport(getApplicationContext(), "6e986c7885", true,strategy);//点三个参数 建议在测试阶段建议设置成true，发布时设置为false。
    }


    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
