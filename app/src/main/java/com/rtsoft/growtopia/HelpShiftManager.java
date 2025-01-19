package com.rtsoft.growtopia;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.Log;
import com.helpshift.Helpshift;
import com.helpshift.UnsupportedOSVersionException;
import java.util.HashMap;

public class HelpShiftManager {
    private Context baseContext;

    public HelpShiftManager(Context context) {
        this.baseContext = context;
    }

    private void AddAdditionalFields(HashMap<String, Object> hashMap) {
        HashMap hashMap2 = (HashMap) hashMap.get("customIssueFields");
        if (hashMap2 != null) {
            String str = "dropdown";
            SetConfigValue(hashMap2, "game", str, "Growtopia");
            SetConfigValue(hashMap2, "platform", str, "android");
            SetConfigValue(hashMap2, "device", "multiline", getDeviceInfo());
        }
    }

    public static void SetConfigValue(HashMap<String, Object> hashMap, String str, String str2, Object obj) {
        HashMap hashMap2 = new HashMap();
        hashMap2.put("type", str2);
        hashMap2.put("value", obj);
        hashMap.put(str, hashMap2);
    }

    public boolean HandleDeeplink(Intent intent) {
        Uri data = intent.getData();
        if (data == null) {
            return false;
        }
        String str = "helpshift";
        Log.d(str, data.getHost());
        if (!data.getHost().contains(str)) {
            return false;
        }
        HashMap hashMap = new HashMap();
        hashMap.put("enableContactUs", "NEVER");
        str = "faqid";
        if (data.getQueryParameter(str) != null) {
            Helpshift.showSingleFAQ((Activity) this.baseContext, data.getQueryParameter(str), hashMap);
            return true;
        }
        str = "sectionid";
        if (data.getQueryParameter(str) == null) {
            return false;
        }
        Helpshift.showFAQSection((Activity) this.baseContext, data.getQueryParameter(str), hashMap);
        return true;
    }

    public void Init() {
        HashMap hashMap = new HashMap();
        hashMap.put("enableInAppNotification", Boolean.valueOf(false));
        hashMap.put("screenOrientation", Integer.valueOf(6));
        try {
            Helpshift.install((Application) this.baseContext.getApplicationContext(), "ubisoft-mobile_platform_20210608074937628-824b119b8057f82", "ubisoft-mobile.helpshift.com", hashMap);
        } catch (UnsupportedOSVersionException e) {
            Log.e("Helpshift", e.getMessage());
        }
    }

    public void SetLanguage(String str) {
        Helpshift.setLanguage(str);
    }

    public void ShowConversation(HashMap<String, Object> hashMap) {
        AddAdditionalFields(hashMap);
        Helpshift.showConversation((Activity) this.baseContext, hashMap);
    }

    public void ShowFAQs(HashMap<String, Object> hashMap) {
        AddAdditionalFields(hashMap);
        Helpshift.showFAQs((Activity) this.baseContext, hashMap);
    }

    public String getDeviceInfo() {
        return ((("android version:" + VERSION.RELEASE + "(" + VERSION.INCREMENTAL + ")") + ";\nandroid API Level:" + VERSION.SDK_INT) + ";\ndevice:" + Build.DEVICE) + ";\nmodel:" + Build.MODEL;
    }
}
