package com.multiopen;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * 多开管理器 - 主界面
 * 基于 Android Work Profile 机制
 * 无需 Root，系统级隔离
 */
public class MainActivity extends Activity {

    private static final String TAG = "MultiOpen";
    private static final int REQUEST_PROVISIONING = 1001;

    private UserManager userManager;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName adminComponent;

    private ListView listView;
    private Button btnCreateProfile;
    private TextView txtInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userManager = (UserManager) getSystemService(Context.USER_SERVICE);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(this, ProfileAdminReceiver.class);

        listView = findViewById(R.id.listView);
        btnCreateProfile = findViewById(R.id.btnCreateProfile);
        txtInfo = findViewById(R.id.txtInfo);

        // 检查工作资料支持
        checkWorkProfileSupport();

        // 加载已存在的工作资料
        loadProfiles();

        btnCreateProfile.setOnClickListener(v -> createWorkProfile());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfiles();
    }

    private void checkWorkProfileSupport() {
        boolean isSupported = devicePolicyManager != null 
            && devicePolicyManager.isProvisioningAllowed(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE);
        
        if (!isSupported) {
            txtInfo.setText("⚠️ 您的设备不支持创建工作资料\n请检查：设置 → 更多设置 → 隐私 → 工作资料");
            btnCreateProfile.setEnabled(false);
        } else {
            txtInfo.setText("✅ 支持工作资料模式\n最多可创建 5 个独立空间");
        }
    }

    private void loadProfiles() {
        List<UserInfo> profiles = userManager.getUserProfiles();
        
        StringBuilder sb = new StringBuilder();
        sb.append("📱 当前用户空间:\n");
        sb.append("总数: ").append(profiles.size()).append(" 个\n\n");
        
        for (int i = 0; i < profiles.size(); i++) {
            UserInfo info = profiles.get(i);
            int userSerial = userManager.getSerialNumberForUser(info.getUserHandle());
            String name = info.getName() != null ? info.getName() : "用户 " + (i + 1);
            boolean isMain = info.getId() == android.os.Process.myUserHandle().hashCode();
            
            sb.append(i + 1).append(". ")
              .append(name)
              .append(isMain ? " (主空间)" : " (工作空间)")
              .append("\n");
        }
        
        txtInfo.append("\n" + sb.toString());
    }

    private void createWorkProfile() {
        try {
            Intent intent = new Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE);
            intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME, adminComponent);
            startActivityForResult(intent, REQUEST_PROVISIONING);
        } catch (Exception e) {
            Toast.makeText(this, "创建工作资料失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Create work profile failed", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_PROVISIONING) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "✅ 工作资料创建成功！", Toast.LENGTH_LONG).show();
                loadProfiles();
            } else {
                Toast.makeText(this, "❌ 取消创建", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void launchAppInProfile(String packageName, int userProfileId) {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                android.os.UserHandle userHandle = userManager.getUserForSerialNumber(userProfileId);
                if (userHandle != null) {
                    // 在指定用户空间中启动应用
                    startActivityAsUser(intent, userHandle);
                } else {
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "请先在工作空间中安装 " + packageName, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Launch failed", e);
            Toast.makeText(this, "启动失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 检查小米双开状态
     */
    public boolean isXiaomiDualAppEnabled(String packageName) {
        try {
            // 小米双开应用包名通常是原包名 + ":second"
            PackageManager pm = getPackageManager();
            pm.getPackageInfo(packageName + ":second", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}