package cn.dss.rxjavademo;

import android.app.Application;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

/**
 * Created by BG241996 on 2020/5/18.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        initLog();
    }

    // init it in ur application
    public void initLog() {
        LogUtils.Config config = LogUtils.getConfig()
                .setLogSwitch(true)// 设置 log 总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(true)// 设置是否输出到控制台开关，默认开
                .setGlobalTag(null)// 设置 log 全局标签，默认为空
                .setBorderSwitch(false)
                .setLogHeadSwitch(false);
        LogUtils.i(config.toString());
    }
}
