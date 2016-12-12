package com.hyphenate.easeui.utils;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.Group;
import com.hyphenate.easeui.domain.User;

public class EaseUserUtils {

    private static final String TAG = EaseUserUtils.class.getSimpleName();

    static EaseUserProfileProvider userProvider;
    
    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }
    
    /**
     * get EaseUser according username
     * @param username
     */
    public static EaseUser getUserInfo(String username){
        if(userProvider != null)
            return userProvider.getUser(username);
        
        return null;
    }

    /**
     * get AppUser according username
     * @param username
     * @return
     */
    public static User getAppUserInfo(String username){
        if(userProvider != null)
            return userProvider.getAppUser(username);

        return null;
    }

    /**
     * get AppUser according username
     * @return
     */
    public static User getCurrentAppUserInfo(){
        String username = EMClient.getInstance().getCurrentUser();
        if(userProvider != null)
            return userProvider.getAppUser(username);

        return null;
    }
    /**
     * set user avatar
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	User user = getAppUserInfo(username);
        if(user != null && user.getAvatar() != null){
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //use default avatar
                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).into(imageView);
            }
        }else{
            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
        }
    }
    
    /**
     * set user's nickname
     */
    public static void setUserNick(String username,TextView textView){
        if(textView != null){
        	EaseUser user = getUserInfo(username);
        	if(user != null && user.getNick() != null){
        		textView.setText(user.getNick());
        	}else{
        		textView.setText(username);
        	}
        }
    }

    /**
     * set user's nickname
     */
    public static void setAppUserNick(String username,TextView textView){
        if(textView != null){
            User user = getAppUserInfo(username);
            Log.e(TAG, "user:" + user);
            if(user != null && user.getMUserNick() != null){
                textView.setText(user.getMUserNick());
            }else{
                textView.setText(username);
            }
        }
    }

    /**
     * set user avatar
     * @param username
     */
    public static void setAppUserAvatar(Context context, String username, ImageView imageView){
        User user = getAppUserInfo(username);
        if (user == null) {
            user = new User(username);
        }
        if(user.getAvatar() != null){
            try {
                // avatarResId算是键值，头像以键值对的形式缓存在内存和磁盘当中
                int avatarResId = Integer.parseInt(user.getAvatar());
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //use default avatar
                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).into(imageView);
            }
        }else{
            Glide.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }

    /**
     * set Group avatar
     * @param Hxid
     */
    public static void setAppGroupAvatar(Context context, String Hxid, ImageView imageView){
        if(Hxid != null){
            try {
                // avatarResId算是键值，头像以键值对的形式缓存在内存和磁盘当中
                int avatarResId = Integer.parseInt(Group.getGroupIcon(Hxid));
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //use default avatar
                Glide.with(context).load(Group.getGroupIcon(Hxid)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).into(imageView);
            }
        }else{
            Glide.with(context).load(R.drawable.ease_group_icon).into(imageView);
        }
    }

    public static void setAppCurrentUserAvatar(FragmentActivity activity, ImageView imageView) {
        String username = EMClient.getInstance().getCurrentUser();
        setAppUserAvatar(activity, username, imageView);
    }

    public static void setAppCurrentUserNick(TextView tvNick) {
        String username = EMClient.getInstance().getCurrentUser();
        setAppUserNick(username, tvNick);
    }

    public static void setAppCurrentUsernameWithNo(TextView textView) {
        String username = EMClient.getInstance().getCurrentUser();
        setAppUserName("无诺号: " + username, textView);
    }

    public static void setAppUserName(String username,TextView textView) {
        if(textView != null){
            textView.setText(username);
        }
    }
    /**
     * 供外部使用获取直播封面
     * @param Hxid
     * @return
     */
    public static String getLiveIcon(String Hxid) {
        return "http://101.251.196.90:8000/SuperWeChatServerV2.0/downloadAvatar?name_or_hxid=" + Hxid
                + "&avatarType=chatroom_icon&m_avatar_suffix=.jpg";
    }

    /**
     * 设置直播封面
     * @param Hxid
     */
    public static void setLiveAvatar(Context context, String Hxid, ImageView imageView){
        if(Hxid != null){
            String path = getLiveIcon(Hxid);
            try {
                // avatarResId算是键值，头像以键值对的形式缓存在内存和磁盘当中
                int avatarResId = Integer.parseInt(path);
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //use default avatar
                Glide.with(context).load(path).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).into(imageView);
            }
        }else{
            Glide.with(context).load(R.drawable.ease_group_icon).into(imageView);
        }
    }
}
