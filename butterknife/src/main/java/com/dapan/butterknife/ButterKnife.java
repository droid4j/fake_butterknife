package com.dapan.butterknife;

import android.app.Activity;

import java.lang.reflect.Constructor;

public class ButterKnife {

    public UnBinder bind(Activity activity) {
        // XxxActivity_Binding viewBinding = new XxxActivity_Binding(this);
        try {
            Class<? extends UnBinder> bindClass = (Class<? extends UnBinder>)
                    Class.forName(activity.getClass().getName() + "_ViewBinding");
            Constructor<? extends UnBinder> constructor = bindClass.getDeclaredConstructor(activity.getClass());
            return constructor.newInstance(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UnBinder.EMPTY;
    }
}
