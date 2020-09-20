package com.example.contactapp.config;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import com.example.contactapp.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.regex.Pattern;

public class Utils {
    public static void showSnackbar(String message, Snackbar snackbar, ViewGroup layout) {
        snackbar = Snackbar.make(layout, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundResource(R.color.colorPrimaryDark);
        snackbar.show();
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static boolean isValidEmail(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String phone){
        if(phone.length()!=10)
            return false;
        else
            return true;
    }

    public static boolean isValidName(String name){
        if(name.length()<4 || name.length()>=30)
            return false;
        else
            return true;
    }

}
