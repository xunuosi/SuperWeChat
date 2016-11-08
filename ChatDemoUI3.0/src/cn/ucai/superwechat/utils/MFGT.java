package cn.ucai.superwechat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.hyphenate.easeui.domain.User;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.ui.AddContactActivity;
import cn.ucai.superwechat.ui.AddFriendActivity;
import cn.ucai.superwechat.ui.ChatActivity;
import cn.ucai.superwechat.ui.CheckMessageActivity;
import cn.ucai.superwechat.ui.FriendProfileActivity;
import cn.ucai.superwechat.ui.LoginActivity;
import cn.ucai.superwechat.ui.MainActivity;
import cn.ucai.superwechat.ui.RegisterActivity;
import cn.ucai.superwechat.ui.SettingsActivity;
import cn.ucai.superwechat.ui.UserProfileActivity;


public class MFGT {
    /**
     * 补间动画关闭当前activity
     * @param activity
     */
    public static void finish(Activity activity){
        activity.finish();
        activity.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
    }

    /**
     * 跳转开启的界面
     * @param context
     */
    public static void gotoMainActivity(Activity context){
        startActivity(context, MainActivity.class);
    }

    /**
     * 重写开启界面的方法
     * @param context
     * @param cls
     */
    public static void startActivity(Context context, Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(context,cls);
        startActivity(context,intent);
    }

    /**
     * 重载开启界面方法
     * @param context
     * @param intent
     */
    public static void startActivity(Context context,Intent intent){
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public static void gotoLogin(Activity activity) {
        startActivity(activity, LoginActivity.class);
    }

    public static void gotoRegister(Activity activity) {
        startActivity(activity, RegisterActivity.class);
    }

    public static void gotoSettingsActivity(Activity activity) {
        startActivity(activity, SettingsActivity.class);
    }

    public static void gotoUserProfileActivity(Activity activity) {
        startActivity(activity, UserProfileActivity.class);
    }

    public static void gotoAddContactActivity(Activity activity) {
        startActivity(activity, AddContactActivity.class);
    }

    public static void gotoAddFriendActivity(Activity activity) {
        startActivity(activity, AddFriendActivity.class);
    }

    public static void gotoFriendProfile(Activity activity, User user) {
        Intent intent = new Intent();
        intent.setClass(activity, FriendProfileActivity.class);
        intent.putExtra(I.User.USER_NAME, user);
        startActivity(activity, intent);
    }

    public static void gotoCheckMessageActivity(Activity activity,String username) {
        Intent intent = new Intent();
        intent.putExtra(I.User.PASSWORD, username);
        intent.setClass(activity, CheckMessageActivity.class);
        startActivity(activity,intent);
    }

    public static void gotoChatActivity(Activity activity, String username) {
        Intent intent = new Intent();
        intent.putExtra("userId", username);
        intent.setClass(activity, ChatActivity.class);
        startActivity(activity, intent);
    }
}
