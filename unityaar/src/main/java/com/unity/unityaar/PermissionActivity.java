package com.unity.unityaar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

public class PermissionActivity extends Activity {

    public static void jumpPermission(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, PermissionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String[] noPerArray = PermissionUtils.hasPermission(this);
        if (noPerArray != null) {
            PermissionUtils.requestPermission(this, noPerArray);
            Log.d(LocationHelper.TAG, "request permission noPerArray = " + Arrays.toString(noPerArray));
        } else {
            Log.d(LocationHelper.TAG, "has permission");
            LocationHelper.hasPermission = true;
            finish();
        }
    }
}