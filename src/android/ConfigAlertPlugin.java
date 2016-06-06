/*
 Author: Toni Korin

 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */
package com.tonikorin.cordova.plugin.configalert;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;


public class ConfigAlertPlugin extends CordovaPlugin {

    private static final String TAG = "ConfigAlertPlugin";
    private static final String PREFS = "ConfigAlert";
    private static final String SKIP = "skipAlert";
    private static final String PACKAGE_NAME = "com.huawei.systemmanager";
    private static final String CLASS_NAME = "optimize.process.ProtectActivity";
    //private static final String PACKAGE_NAME = "com.android.settings";
    //private static final String CLASS_NAME = "Settings";
    private static final String TITLE = "Huawei Protected Apps";
    private static final String MESSAGE = "This app requires to be enabled in 'Protected Apps' to function properly.";
    private static final String OK = "Protected Apps";
    private static final String CANCEL = "Remind";

    private String skipKey;
    private String packageName;
    private String className;
    private Context context;
    private SharedPreferences.Editor editor;
    /**
     * Executes the request.
     *
     * @param action The action to execute.
     * @param args The exec() arguments.
     * @param callback The callback context used when calling back into
     * JavaScript.
     * @return Returning false results in a "MethodNotFound" error.
     *
     * @throws JSONException
     */
    @Override
    public boolean execute(String action, JSONArray args,
            CallbackContext callback) throws JSONException {

        if ( action.equalsIgnoreCase("alert") ) {
            JSONObject config = args.getJSONObject(0);
            ifAlert(config);
            return true;
        }
        return false;
    }

    private void ifAlert(JSONObject config) {
        context = cordova.getActivity().getApplicationContext();
        boolean forceAlert  = config.optBoolean("force",false);
        skipKey = config.optString("key", SKIP);
        final SharedPreferences settings = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        boolean skipMessage = settings.getBoolean(skipKey, false);
        if (!skipMessage || forceAlert) {
            Log.v(TAG, "Alert");
            editor = settings.edit();
            packageName = config.optString("package",PACKAGE_NAME);
            className = config.optString("class",CLASS_NAME);
            Intent intent = new Intent();
            intent.setClassName(packageName, packageName + "." + className);
            if (isCallable(intent)) {

                new AlertDialog.Builder(cordova.getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(config.optString("title",TITLE))
                        .setMessage(config.optString("message",MESSAGE))
                        .setPositiveButton(config.optString("ok",OK), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                openSettings();
                                editor.putBoolean(skipKey, true);
                                editor.apply();
                            }
                        })
                        .setNegativeButton(config.optString("cancel",CANCEL), null)
                        .show();
            } else {
                editor.putBoolean(SKIP, true);
                editor.apply();
            }
        }
    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void openSettings() {
        try {
            String cmd = "am start -n " + packageName + "/." + className;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cmd += " --user " + getUserSerial();
            }
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ignored) {
        }
    }

    private String getUserSerial() {
        //noinspection ResourceType
        Object userManager = context.getSystemService("user");
        if (null == userManager) return "";

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            if (userSerial != null) {
                return String.valueOf(userSerial);
            } else {
                return "";
            }
        } catch (Exception e) {
            Log.e(TAG, "getUserSerial exception:"+e.getMessage());
        }
        return "";
    }
}
