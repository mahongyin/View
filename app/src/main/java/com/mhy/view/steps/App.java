package com.mhy.view.steps;

import android.app.Activity;
import android.app.Application;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * @author mahongyin 2020-04-14 18:27 @CopyRight mhy.work@qq.com
 * @Project View
 * @Package com.mhy.view.steps
 * @description:
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new StatisticActivityLifecycleCallback());
    }

    class StatisticActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks {
        private int foregroundActivities = 0;
        private boolean isChangingConfiguration;

        /**
         * @param activity
         * @param bundle
         */
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            if (bundle != null) { // 若bundle不为空则程序异常结束
//                Paint mPaint = new Paint();
//                ColorMatrix cm = new ColorMatrix();
//                cm.setSaturation(0);
//                mPaint.setColorFilter(new ColorMatrixColorFilter(cm));
//                activity.getWindow().getDecorView().setLayerType(View.LAYER_TYPE_HARDWARE, mPaint);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            foregroundActivities++;
            if (foregroundActivities == 1 && !isChangingConfiguration) {
                // 应用切到前台

            }
            isChangingConfiguration = false;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            foregroundActivities--;
            if (foregroundActivities == 0) {
                // 应用切到后台

            }
            isChangingConfiguration = activity.isChangingConfigurations();
        }


        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
