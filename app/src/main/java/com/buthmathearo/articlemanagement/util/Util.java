package com.buthmathearo.articlemanagement.util;

import android.app.Activity;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by buthmathearo on 12/9/15.
 */
public class Util {
    private static SweetAlertDialog pDialog;

    public static final String baseUrl = "http://hrdams.herokuapp.com";
    public static final String TAG = "OLO_RESULT";
    public static final int KEY_EDIT_ARTICLE = 222;
    public static final int KEY_ADD_ARTICLE = 111;
    public static final int PICK_IMAGE_REQUEST = 444;

    public static void showAlertDialog(Activity activity,String title, String msg, boolean showCancelbutton, int dialogType) {
        pDialog = new SweetAlertDialog(activity, dialogType);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);
        pDialog.setContentText(msg);
        pDialog.setCancelable(true);
        pDialog.showCancelButton(showCancelbutton);
        pDialog.show();
    }

    public static void showAlertDialog(Activity activity, String msg, boolean showCancelbutton, int dialogType) {
        pDialog = new SweetAlertDialog(activity, dialogType);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(msg);
        pDialog.setCancelable(true);
        pDialog.showCancelButton(showCancelbutton);
        pDialog.show();
    }

    public static void hideAlertDialog() {
        if (pDialog != null) {
            pDialog.cancel();
        }
    }


}
