package com.dapan.butterknife;

import android.app.Activity;
import android.view.View;

public class Utils {

    public static <T extends View> T getViewById(Activity activity, int viewId) {
        return activity.findViewById(viewId);
    }
}
