package com.unity.unityaar;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

public class PermissionUtils {
    public static final int ACTION_OTHER_PERMISSION_REQUEST_CODE = 10001;
    public static final String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @TargetApi(Build.VERSION_CODES.M)
    public static String[] hasPermission(Context context) {
        if (permissions != null) {
            ArrayList<String> toApplyList = new ArrayList<>();
            for (String perm : permissions) {
                int check = context.checkSelfPermission(perm);
                Log.d(LocationHelper.TAG, "hasPermission perm = " + perm + " check = " + check);
                if (PackageManager.PERMISSION_GRANTED != check) {
                    toApplyList.add(perm);
                }
            }
            if (toApplyList.size() > 0) {
                String[] newArray = new String[toApplyList.size()];
                return toApplyList.toArray(newArray);
            }
            return null;
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean requestPermission(Activity context, String[] permissions) {
        if (permissions != null) {
            ArrayList<String> toApplyList = new ArrayList<String>();

            for (String perm : permissions) {
                int check = context.checkSelfPermission(perm);
                Log.d(LocationHelper.TAG, "requestPermission perm = " + perm + " check = " + check);
                if (PackageManager.PERMISSION_GRANTED != check) {
                    toApplyList.add(perm);
                }
            }
            String[] tmpList = new String[toApplyList.size()];
            if (!toApplyList.isEmpty()) {
                context.requestPermissions(toApplyList.toArray(tmpList), ACTION_OTHER_PERMISSION_REQUEST_CODE);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean handleResult(int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }
}
