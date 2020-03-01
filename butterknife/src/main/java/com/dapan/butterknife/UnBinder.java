package com.dapan.butterknife;

import androidx.annotation.UiThread;

public interface UnBinder {

    @UiThread
    void unbind();

    UnBinder EMPTY = new UnBinder() {
        @Override
        public void unbind() {

        }
    };
}
