package cn.ucai.superwechat.data;

import android.content.Context;


import java.io.File;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.utils.MD5;


public class NetDao {

    /**
     * 申请注册方法
     * @param mcontext
     * @param username
     * @param nick
     * @param password
     * @param listener
     */
    public static void register(Context mcontext, String username, String nick
            , String password, OkHttpUtils.OnCompleteListener<Result> listener) {

        OkHttpUtils<Result> utils = new OkHttpUtils<>(mcontext);
        utils.setRequestUrl(I.REQUEST_REGISTER)
                .addParam(I.User.USER_NAME, username)
                .addParam(I.User.NICK, nick)
                .addParam(I.User.PASSWORD, MD5.getMessageDigest(password))
                .targetClass(Result.class)
                .post()
                .execute(listener);
    }

    /**
     * 登录方法的
     * @param mcontext
     * @param username
     * @param password
     * @param listener
     */
    public static void login(Context mcontext,String username, String password
                                ,OkHttpUtils.OnCompleteListener<String> listener) {

        OkHttpUtils<String> utils = new OkHttpUtils<>(mcontext);
        utils.setRequestUrl(I.REQUEST_LOGIN)
                .addParam(I.User.USER_NAME, username)
                .addParam(I.User.PASSWORD, MD5.getMessageDigest(password))
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 取消注册的方法
     * @param mcontext
     * @param username
     * @param listener
     */
    public static void unregister(Context mcontext, String username
            ,OkHttpUtils.OnCompleteListener<Result> listener) {

        OkHttpUtils<Result> utils = new OkHttpUtils<>(mcontext);
        utils.setRequestUrl(I.REQUEST_UNREGISTER)
                .addParam(I.User.USER_NAME, username)
                .targetClass(Result.class)
                .execute(listener);
    }

    /**
     * 更新昵称的方法
     * @param mcontext
     * @param username
     * @param newNick
     * @param listener
     */
    public static void updateNick(Context mcontext,String username,String newNick
            ,OkHttpUtils.OnCompleteListener<String> listener) {

        OkHttpUtils<String> utils = new OkHttpUtils<>(mcontext);
        utils.setRequestUrl(I.REQUEST_UPDATE_USER_NICK)
                .addParam(I.User.USER_NAME, username)
                .addParam(I.User.NICK, newNick)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 更新头像的方法
     * @param mcontext
     * @param username
     * @param file
     */
    public static void updateAvatar(Context mcontext, String username, File file
            ,OkHttpUtils.OnCompleteListener<String> listener) {

        OkHttpUtils<String> utils = new OkHttpUtils<>(mcontext);
        utils.setRequestUrl(I.REQUEST_UPDATE_AVATAR)
                .addParam(I.NAME_OR_HXID, username)
                .addParam(I.AVATAR_TYPE,I.AVATAR_TYPE_USER_PATH)
                .addFile2(file)
                .targetClass(String.class)
                .post()
                .execute(listener);
    }

}
