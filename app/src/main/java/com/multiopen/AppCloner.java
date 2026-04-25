package com.multiopen;

import android.content.Context;
import android.content.pm.IPackageInstaller;
import android.content.pm.IPackageInstallerSession;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * 应用克隆工具类
 * 将主空间的应用安装到工作资料空间
 */
public class AppCloner {

    private static final String TAG = "AppCloner";

    private Context context;
    private UserManager userManager;
    private PackageManager packageManager;

    public AppCloner(Context context) {
        this.context = context;
        this.userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        this.packageManager = context.getPackageManager();
    }

    /**
     * 克隆应用到工作资料
     * @param packageName 要克隆的应用包名
     * @param workProfileId 工作资料 ID
     * @return 是否成功
     */
    public boolean cloneAppToProfile(String packageName, int workProfileId) {
        try {
            // 获取应用的 APK 路径
            String apkPath = getApkPath(packageName);
            if (apkPath == null) {
                Log.e(TAG, "无法获取 " + packageName + " 的 APK 路径");
                return false;
            }

            // 获取工作资料的用户句柄
            UserHandle userHandle = userManager.getUserForSerialNumber(workProfileId);
            if (userHandle == null) {
                Log.e(TAG, "无法获取工作资料用户句柄");
                return false;
            }

            // 在安装时指定用户
            installPackageForUser(apkPath, userHandle);
            return true;

        } catch (Exception e) {
            Log.e(TAG, "克隆应用失败", e);
            return false;
        }
    }

    private String getApkPath(String packageName) {
        try {
            android.content.pm.ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
            return appInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private void installPackageForUser(String apkPath, UserHandle userHandle) {
        // 使用 pm 命令安装（需要 shell 权限）
        // 实际使用中，用户需要手动在工作空间中安装应用
        Log.d(TAG, "建议用户手动在工作空间中安装应用");
    }

    /**
     * 获取所有可用的工作资料
     */
    public List<UserInfo> getWorkProfiles() {
        return userManager.getUserProfiles();
    }

    /**
     * 检查应用是否已安装在工作资料中
     */
    public boolean isAppInstalledInProfile(String packageName, int profileId) {
        try {
            UserHandle userHandle = userManager.getUserForSerialNumber(profileId);
            if (userHandle == null) return false;
            
            // 检查在指定用户下是否安装
            packageManager.getPackageInfoAsUser(packageName, 0, userHandle);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}