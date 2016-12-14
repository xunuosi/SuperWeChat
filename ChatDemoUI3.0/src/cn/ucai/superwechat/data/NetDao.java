package cn.ucai.superwechat.data;

import android.content.Context;


import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 查询用户信息的方法
     * @param mcontext
     * @param username
     * @param listener
     */
    public static void findUserByUserName(Context mcontext,String username
            ,OkHttpUtils.OnCompleteListener<String> listener) {

        OkHttpUtils<String> utils = new OkHttpUtils<>(mcontext);
        utils.setRequestUrl(I.REQUEST_FIND_USER)
                .addParam(I.User.USER_NAME, username)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 添加好友
     * @param mcontext
     * @param username
     * @param addUsername
     * @param listener
     */
    public static void addContact(Context mcontext,String username,String addUsername
            ,OkHttpUtils.OnCompleteListener<String> listener) {

        OkHttpUtils<String> utils = new OkHttpUtils<>(mcontext);
        utils.setRequestUrl(I.REQUEST_ADD_CONTACT)
                .addParam(I.Contact.USER_NAME, username)
                .addParam(I.Contact.CU_NAME, addUsername)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 删除好友
     * @param mcontext
     * @param username
     * @param deleteName
     * @param listener
     */
    public static void deleteContact(Context mcontext,String username,String deleteName
            ,OkHttpUtils.OnCompleteListener<String> listener) {

        OkHttpUtils<String> utils = new OkHttpUtils<>(mcontext);
        utils.setRequestUrl(I.REQUEST_DELETE_CONTACT)
                .addParam(I.Contact.USER_NAME, username)
                .addParam(I.Contact.CU_NAME, deleteName)
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 下载好友
     *
     * @param mcontext
     * @param listener
     */
    public static void loadContact(Context mcontext, OkHttpUtils.OnCompleteListener<String> listener) {

        OkHttpUtils<String> utils = new OkHttpUtils<>(mcontext);
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME, EMClient.getInstance().getCurrentUser())
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 新建群组的方法
     * @param mcontext
     * @param listener
     */
    public static void createGroup(Context mcontext, EMGroup emGroup,boolean isPublic,boolean isMember
            , OkHttpUtils.OnCompleteListener<String> listener) {

        OkHttpUtils<String> utils = new OkHttpUtils<>(mcontext);
        utils.setRequestUrl(I.REQUEST_CREATE_GROUP)
                .addParam(I.Group.HX_ID,emGroup.getGroupId())
                .addParam(I.Group.NAME,emGroup.getGroupName())
                .addParam(I.Group.DESCRIPTION,emGroup.getDescription())
                .addParam(I.Group.OWNER,emGroup.getOwner())
                .addParam(I.Group.IS_PUBLIC,String.valueOf(isPublic))
                .addParam(I.Group.ALLOW_INVITES,String.valueOf(isMember))
                .targetClass(String.class)
                .post()
                .execute(listener);
    }

    /**
     * 新建群组的方法(带群图标)
     *
     * @param mcontext
     * @param listener
     */
    public static void createGroup(Context mcontext, File file, EMGroup emGroup,boolean isPublic,boolean isMember
            , OkHttpUtils.OnCompleteListener<String> listener) {

        OkHttpUtils<String> utils = new OkHttpUtils<>(mcontext);
        utils.setRequestUrl(I.REQUEST_CREATE_GROUP)
                .addParam(I.Group.HX_ID,emGroup.getGroupId())
                .addParam(I.Group.NAME,emGroup.getGroupName())
                .addParam(I.Group.DESCRIPTION,emGroup.getDescription())
                .addParam(I.Group.OWNER,emGroup.getOwner())
                .addParam(I.Group.IS_PUBLIC,String.valueOf(isPublic))
                .addParam(I.Group.ALLOW_INVITES,String.valueOf(isMember))
                .addFile2(file)
                .targetClass(String.class)
                .post()
                .execute(listener);
    }

    public static void addGroupMembers(Context mcontext, String members, EMGroup emGroup
            , OkHttpUtils.OnCompleteListener<String> listener) {

        OkHttpUtils<String> utils = new OkHttpUtils<>(mcontext);
        utils.setRequestUrl(I.REQUEST_ADD_GROUP_MEMBERS)
                .addParam(I.Member.USER_NAME,members)
                .addParam(I.Member.GROUP_HX_ID,emGroup.getGroupId())
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * 下载礼物信息的请求方法
     *
     * @param mcontext
     * @param listener
     */
    public static void findGiftInfo(Context mcontext, OkHttpUtils.OnCompleteListener<String> listener) {

        OkHttpUtils<String> utils = new OkHttpUtils<>(mcontext);
        utils.setRequestUrl(I.REQUEST_ALL_GIFTS)
                .targetClass(String.class)
                .execute(listener);
    }
}
