package com.github.fi3te.iliasdownloader.view.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by wennier on 09.09.2015.
 */
public class KeyboardUtil {

    public static void showKeyboard(Activity activity, EditText editText) {
        if (editText != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            View focus = activity.getCurrentFocus();
            if (focus != null)
                inputManager.hideSoftInputFromWindow(focus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException e) {
            Log.e("", "hideKeyboard", e.fillInStackTrace());
        }
    }

}
