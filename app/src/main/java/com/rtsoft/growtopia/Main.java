package com.rtsoft.growtopia;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.rtsoft.growtopia.HeightProvider.HeightListener;
import com.ubisoft.bridge.JavaInterface;
import java.io.File;
import java.util.Arrays;

public class Main extends SharedActivity {
    public static HelpShiftManager helpshiftManager;
    public static Main mainApp;
    public AppReviewManager appReviewManager = new AppReviewManager(this);
    public AppsFlyerManager appsflyerManager = new AppsFlyerManager(this);
    private FirebaseAnalytics firebaseAnalytics;
    public FirebaseCrashlyticsManager firebaseCrashlyticsManager = new FirebaseCrashlyticsManager();
    public GoogleSignInHelper googleSignInHelper = new GoogleSignInHelper(this);
    private HeightProvider heightProvider;
    public IronSourceManager ironSourceManager = new IronSourceManager(this);
    public NativeAppInterface nativeAppInterface = new NativeAppInterface();
    public WebViewManager webViewManager = new WebViewManager(this);

    public static AppReviewManager GetAppReviewManager() {
        return mainApp.appReviewManager;
    }

    public static AppsFlyerManager GetAppsflyerManager() {
        return mainApp.appsflyerManager;
    }

    public static FirebaseCrashlyticsManager GetFirebaseCrashlyticsManager() {
        return mainApp.firebaseCrashlyticsManager;
    }

    public static GoogleSignInHelper GetGoogleSignInHelper() {
        return mainApp.googleSignInHelper;
    }

    public static Object GetHelpShiftManager() {
        return helpshiftManager;
    }

    public static Object GetIronSourceManager() {
        return mainApp.ironSourceManager;
    }

    public static WebViewManager GetWebViewManager() {
        return mainApp.webViewManager;
    }

    public static boolean HandleDeeplink(Intent intent) {
        final Uri data = intent.getData();
        if (data == null) {
            return false;
        }
        Log.d("URL host", data.getHost());
        Log.d("URL data", data.toString());
        Log.d("URL Path", data.getPath());
        Log.d("URL Scheme", data.getScheme());
        Log.d("URL Fragment", data.getSchemeSpecificPart());
        mainApp.mGLView.post(new Runnable() {
            public void run() {
                NativeAppInterface nativeAppInterface = Main.mainApp.nativeAppInterface;
                NativeAppInterface.OnDeepLinkProcess(data.getSchemeSpecificPart());
            }
        });
        return true;
    }

    void OnKeyboardHeightChanged(int i) {
        if (this.webViewManager.IsVisible()) {
            this.webViewManager.MoveView(i);
            return;
        }
        m_KeyBoardHeight = i;
        String str = "NIRMAN";
        Log.d(str, "Keyboard height = " + m_KeyBoardHeight);
        if (m_KeyBoardHeight > 0 && !m_editText.isFocused()) {
            Log.d(str, "KeyboardX opening...");
            UpdateEditBoxInView(true, false);
        } else if (m_KeyBoardHeight == 0 && m_editText.isFocused()) {
            Log.d(str, "KeyboardX closing...");
            SharedActivity sharedActivity = SharedActivity.app;
            if (!SharedActivity.passwordField) {
                sharedActivity = SharedActivity.app;
                SharedActivity.nativeOnKey(1, 500000, 0);
            }
            nativeCancelBtnPressed();
            UpdateEditBoxInView(false, false);
            if (Looper.myLooper() != Looper.getMainLooper()) {
                nativeUpdateConsoleLogPos((float) m_KeyBoardHeight);
            }
        }
        if (m_editText.isFocused()) {
            UpdateEditBoxRootViewPosition();
        }
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        this.googleSignInHelper.handleSignInResult(i, i2, intent);
    }

    protected void onCreate(Bundle bundle) {
        mainApp = this;
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        helpshiftManager = new HelpShiftManager(this);
        dllname = "growtopia";
        this.BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArv12FD/xxuAJ3/B8Jgx78985UN/FitcQD5C21eIS5D+98yr7dy9sw8R2fSTFZKExBZVAfatgDH7s6fb9vfHi43szfpdXs3ZL2hsa7DeCWRyVSTD6o/i14vgwInv1S/dgLAwQth3PDXWF+zYXOlL+umOt9K9eqQo5CZhkwl9JAmMHlazvbhSGAldV5QsdY3pK5wmg/w2873abgYsGdI3B9wL75kgZW9tV2O6efiIbXlevktGOMup3Ql2H4Rcpa3ZeDtGl+YTQbEUQTYiYBDtFGCyqksXeM6+kCnaF97Ss5wA0w5ID9WJLkziXI4iGBMRd0a7s+vVniwpx771oGcJxewIDAQAB";
        securityEnabled = false;
        IAPEnabled = true;
        HookedEnabled = false;
        PackageName = "com.rtsoft.growtopia";
        FirebaseCrashlytics.getInstance().log(((((("android version:" + System.getProperty("os.version") + "(" + VERSION.INCREMENTAL + ")") + "; android API Level:" + VERSION.SDK_INT) + "; CurrentABI:" + System.getProperty("os.arch")) + "; SupportedABIs:" + Arrays.toString(Build.SUPPORTED_ABIS)) + "; device:" + Build.DEVICE) + "; model:" + Build.MODEL);
        if (!new StringBuilder(Environment.getExternalStorageDirectory().toString() + File.separatorChar + "windows" + File.separatorChar + "BstSharedFolder").exists()) {
            System.loadLibrary(dllname);
            super.onCreate(bundle);
            JavaInterface.injectActivityJava(this);
            this.heightProvider = new HeightProvider(this).setHeightListener(new HeightListener() {
                public void onHeightChanged(int i) {
                    Main.this.OnKeyboardHeightChanged(i);
                }
            });
            this.ironSourceManager.OnCreate();
            this.appReviewManager.OnCreate();
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return (i == 4 && this.webViewManager.IsVisible()) ? true : super.onKeyDown(i, keyEvent);
    }

    protected void onPause() {
        super.onPause();
        this.heightProvider.OnPause();
        this.ironSourceManager.onPause();
    }

    protected void onResume() {
        super.onResume();
        this.heightProvider.OnResume();
        this.ironSourceManager.onResume();
    }

    protected void onStart() {
        super.onStart();
    }
}
