package com.example.administrator.readphp;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/11.
 */

public class RunntimePermissionHelper {

    //还需申请的权限列表
    private List<String> permissionsList = new ArrayList<String>();
    //申请权限后的返回码
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 2;

    public static boolean checkAndRequestForRunntimePermission(Activity activity, String[]  permissionsNeeded) {
        // 1 检查权限
        ArrayList<String> permissionsNeedRequest = new ArrayList<String>();

        for (String permission : permissionsNeeded) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    == PackageManager.PERMISSION_GRANTED) {
                continue;
            }
            permissionsNeedRequest.add(permission);
        }
        // 2 请求权限
        if (permissionsNeedRequest.size() == 0) {
            return true;
        } else {
            String[] permissions = new String[permissionsNeedRequest.size()];
            permissions = permissionsNeedRequest.toArray(permissions);
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE_ASK_PERMISSIONS);
            return false;
        }
    }
    public static boolean showDeniedPromptIfNeeded(Activity activity,
                                                   String permission) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                permission)) {
            Toast.makeText(
                    activity.getApplicationContext(),
                    "Read SMS Permission denied. You should open it in Settings-->Apps-->"
                            + activity.getPackageName(), Toast.LENGTH_SHORT)
                    .show();
            return true;
        }
        return false;
    }
}
