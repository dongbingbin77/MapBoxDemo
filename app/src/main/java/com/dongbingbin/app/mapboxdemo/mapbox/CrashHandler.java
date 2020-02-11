package com.dongbingbin.app.mapboxdemo.mapbox;

import android.content.Context;

/**
 * Created by ZXJ on 2015/11/2.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LogUtil.e(TAG, "error : " + e.getMessage());
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private static CrashHandler INSTANCE = new CrashHandler();

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;

    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
//        new Thread() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG).show();
//                Looper.loop();
//            }
//        }.start();
        //if(AppConfig.Debug|| WHPlatformFlavor.getWHPlatform().getAppName().equals(WHPlatform.BGSH_OVERSEA.getAppName())){
            try{
                LogUtil.buildLogMessage(ex, mContext);
            }catch (Exception ex1){
                ex1.printStackTrace();
            }
//        } else {
//            try {
//                StringBuilder builder = new StringBuilder();
////                DBManager dbManager = DBManager.getInstance(mContext);
//                AppApplication appApplication = (AppApplication)mContext.getApplicationContext();
//                if (ex != null) {
//                    builder.append("\n")
//                            .append(LogUtil.getLogStyle(LogUtil.ERROR, TAG, "", ex)).append("React Native Version:");
//                    try {
////                        if (appApplication != null && appApplication.getUserInfoContainer() != null){
////                            builder.append("mobile:").append(appApplication.getUserInfoContainer().getMobile());
////                            Gson gson = new Gson();
////                            builder.append("userInfo json:").append(gson.toJson(appApplication.getUserInfoContainer()));
////                        }
//                    }catch(Exception e1){
//                        e1.printStackTrace();
//                    }
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        return false;
    }
}
