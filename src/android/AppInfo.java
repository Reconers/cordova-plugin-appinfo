package org.scriptotek.appinfo;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppInfo extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getAppInfo")) {
                this.getAppInfo(callbackContext);
                return true;
        } else if (action.equals("getVersion")) {
            this.getVersion(callbackContext);
            return true;
        } else if (action.equals("getIdentifier")) {
            this.getIdentifier(callbackContext);
            return true;
        }
        return false;
    }

    private void getAppInfo(CallbackContext callbackContext){

        String packageName = this.cordova.getActivity().getPackageName();
        String versionName = "";
        String versionCode = "";

        PackageManager pm = this.cordova.getActivity().getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
            versionCode = Integer.toString(packageInfo.versionCode);
        } catch (NameNotFoundException e) {
        }

        JSONObject appInfo = new JSONObject();
        try {
            appInfo.put("identifier", packageName);
            appInfo.put("version", versionName);
            appInfo.put("build", versionCode);
            appInfo.put("market", getMarketVersionFast(packageName));
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
        }

        callbackContext.success(appInfo);
    }

    private void getVersion(CallbackContext callbackContext) {

        String versionName;
        String packageName = this.cordova.getActivity().getPackageName();
        PackageManager pm = this.cordova.getActivity().getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
        } catch (NameNotFoundException nnfe) {
            versionName = "";
        }
        callbackContext.success(versionName);

    }

    private void getIdentifier(CallbackContext callbackContext) {

        String packageName = this.cordova.getActivity().getPackageName();
        callbackContext.success(packageName);
    }

    public String getMarketVersionFast(String packageName) {
            String mData = "", mVer = null;

            try {
                URL mUrl = new URL("https://play.google.com/store/apps/details?id="
                        + packageName);
                HttpURLConnection mConnection = (HttpURLConnection) mUrl
                        .openConnection();

                if (mConnection == null)
                    return null;

                mConnection.setConnectTimeout(5000);
                mConnection.setUseCaches(false);
                mConnection.setDoOutput(true);

                if (mConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader mReader = new BufferedReader(
                            new InputStreamReader(mConnection.getInputStream()));

                    while (true) {
                        String line = mReader.readLine();
                        if (line == null)
                            break;
                        mData += line;
                    }

                    mReader.close();
                }

                mConnection.disconnect();

            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

            String startToken = "softwareVersion\">";
            String endToken = "<";
            int index = mData.indexOf(startToken);

            if (index == -1) {
                mVer = null;

            } else {
                mVer = mData.substring(index + startToken.length(), index
                        + startToken.length() + 100);
                mVer = mVer.substring(0, mVer.indexOf(endToken)).trim();
            }

            return mVer;
        }
}