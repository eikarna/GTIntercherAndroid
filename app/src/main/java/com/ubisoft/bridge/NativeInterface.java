package com.ubisoft.bridge;

import android.app.Activity;

/* compiled from: JavaInterface */
class NativeInterface {
    NativeInterface() {
    }

    public static final native int injectActivity(Activity activity, int i, String[] strArr);
}
