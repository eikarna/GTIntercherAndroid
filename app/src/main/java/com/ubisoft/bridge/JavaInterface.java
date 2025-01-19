package com.ubisoft.bridge;

import android.app.Activity;

public class JavaInterface {
    static {
        try {
            System.loadLibrary("ubiservices");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Loading library failed: " + e);
        }
    }

    public static int injectActivityJava(Activity activity) {
        return NativeInterface.injectActivity(activity, 0, new String[0]);
    }
}
