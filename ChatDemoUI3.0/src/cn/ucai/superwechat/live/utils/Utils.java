package cn.ucai.superwechat.live.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import cn.ucai.superwechat.SuperWeChatApplication;

/**
 * Created by wei on 2016/6/2.
 */
public class Utils {
    public static void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) SuperWeChatApplication.getInstance().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) SuperWeChatApplication.getInstance().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }
}
