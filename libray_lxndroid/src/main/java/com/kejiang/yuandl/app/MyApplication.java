//package com.kejiang.yuandl.app;
//
//import android.app.Application;
//import android.content.Context;
//
//import com.kejiang.yuandl.mylibrary.CustomActivityOnCrash;
//import com.orhanobut.logger.Logger;
//import org.xutils.x;
//
///**
// * Created by yuandl on 2016/5/23 0023.
// */
//public class MyApplication extends Application {
//    @Override
//    public void onCreate() {
//        super.onCreate();
////        CustomActivityOnCrash.install(this);
////        CustomActivityOnCrash.setDebugMode(true);
////        String[] emialTo = {"yuandl@Bluemobi.cn", "kangfh@Bluemobi.cn"};
////        CustomActivityOnCrash.setEmailTo(emialTo);
////        Logger.init("law");
////        x.Ext.init(this);
////        refWatcher = LeakCanary.install(this);
////        refWatcher = installLeakCanary();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        System.gc();
//    }
//
////    public static RefWatcher getRefWatcher(Context context) {
////        MyApplication application = (MyApplication) context.getApplicationContext();
////        return application.refWatcher;
////    }
////
////    protected RefWatcher installLeakCanary() {
////        return RefWatcher.DISABLED;
////    }
//}
