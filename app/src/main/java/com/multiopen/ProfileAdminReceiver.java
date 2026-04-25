package com.multiopen;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 设备管理员接收器
 * 用于管理工作资料的创建和删除
 */
public class ProfileAdminReceiver extends DeviceAdminReceiver {

    private static final String TAG = "ProfileAdmin";

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Log.d(TAG, "工作资料管理员已启用");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Log.d(TAG, "工作资料管理员已禁用");
    }

    @Override
    public void onProfileProvisioningComplete(Context context, Intent intent) {
        super.onProfileProvisioningComplete(context, intent);
        Log.d(TAG, "工作资料配置完成");
        
        // 可以在这里自动安装应用
        // installAppsToWorkProfile(context);
    }

    public static CharSequence onDisableRequested(Context context, Intent intent) {
        return "禁用后将删除工作资料及其中的所有数据";
    }

    public static CharSequence onPasswordChanged(Context context, Intent intent, int type) {
        return null;
    }

    public static CharSequence onPasswordFailed(Context context, Intent intent) {
        return null;
    }

    public static CharSequence onPasswordSucceeded(Context context, Intent intent) {
        return null;
    }
}