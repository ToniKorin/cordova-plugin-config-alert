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
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ConfigAlertPlugin extends CordovaPlugin {

    private static final String TAG = "ConfigAlertPlugin";
    private static final String PREFS = "ConfigAlert";
    private static final String SKIP = "skipAlerts";
    private static final String PACKAGE_NAME = "com.huawei.systemmanager";
    private static final String CLASS_NAME = "com.huawei.systemmanager.optimize.process.ProtectActivity";
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
        boolean allPowerSavers  = config.optBoolean("allPowerSavers",false);
        skipKey = config.optString("key", SKIP);
        final SharedPreferences settings = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        boolean skipMessage = settings.getBoolean(skipKey, false);
        if (!skipMessage || forceAlert) {
            Log.v(TAG, "Alert");
            editor = settings.edit();
            packageName = config.optString("package",PACKAGE_NAME);
            className = config.optString("class",CLASS_NAME);
            List<Intent> alertIntents;
            if (allPowerSavers) {
                alertIntents = this.POWERMANAGER_INTENTS;
            } else {
                alertIntents = new ArrayList<Intent>();
                Intent intent = new Intent();
                intent.setClassName(packageName, className);
                alertIntents.add(intent);
            }
            boolean foundCorrectIntent = false;
            for (final Intent intent : alertIntents) {
                if (isCallable(intent)) {
                    foundCorrectIntent = true;
                    new AlertDialog.Builder(cordova.getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(config.optString("title", TITLE))
                            .setMessage(config.optString("message", MESSAGE))
                            .setPositiveButton(config.optString("ok", OK), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    editor.putBoolean(skipKey, true);
                                    editor.apply();
                                    try {
                                        cordova.getActivity().startActivity(intent);
                                    } catch (Exception e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                }
                            })
                            .setNegativeButton(config.optString("cancel", CANCEL), null)
                            .show();
                    break;
                }

            }
            if (!foundCorrectIntent) {
                editor.putBoolean(skipKey, true);
                editor.apply();
            }
        }
    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private static List<Intent> POWERMANAGER_INTENTS = Arrays.asList(
            //new Intent().setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings")),
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerSaverModeActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerConsumptionActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.autostart.AutoStartActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"))
                    .setData(android.net.Uri.parse("mobilemanager://function/entry/AutoStart"))
    );

}
