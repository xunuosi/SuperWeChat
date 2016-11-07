package cn.ucai.superwechat.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.db.UserDao;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

public class UserProfileActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = UserProfileActivity.class.getSimpleName();

    private static final int REQUESTCODE_PICK = 1;
    private static final int REQUESTCODE_CUTTING = 2;
    @BindView(R.id.ctitle_ivback)
    ImageView mCtitleIvback;
    @BindView(R.id.ctitle_tvLeft)
    TextView mCtitleTvLeft;
    @BindView(R.id.user_profile_ivAvatar)
    ImageView mUserProfileIvAvatar;
    @BindView(R.id.user_profile_tvNick)
    TextView mUserProfileTvNick;
    @BindView(R.id.user_profile_tvAccount)
    TextView mUserProfileTvAccount;
    @BindView(R.id.ctitle_view_left)
    View mCtitleViewLeft;

    private ProgressDialog dialog;
    User user = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_user_profile);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mCtitleIvback.setVisibility(View.VISIBLE);
        mCtitleTvLeft.setVisibility(View.VISIBLE);
        mCtitleViewLeft.setVisibility(View.VISIBLE);
        mCtitleTvLeft.setText(R.string.userinfo_txt_title);

        user = EaseUserUtils.getCurrentAppUserInfo();
        if (user == null) {
            finish();
        }
        mUserProfileTvAccount.setText(user.getMUserName());
        mUserProfileTvNick.setText(user.getMUserNick());
        EaseUserUtils.setAppCurrentUserAvatar(this, mUserProfileIvAvatar);
    }

    public void asyncFetchUserInfo(String username) {
        SuperWeChatHelper.getInstance().getUserProfileManager().asyncGetUserInfo(username, new EMValueCallBack<EaseUser>() {

            @Override
            public void onSuccess(EaseUser user) {
                if (user != null) {
                    SuperWeChatHelper.getInstance().saveContact(user);
                    if (isFinishing()) {
                        return;
                    }
                    mUserProfileTvNick.setText(user.getNick());
                    if (!TextUtils.isEmpty(user.getAvatar())) {
                        Glide.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.em_default_avatar).into(mUserProfileIvAvatar);
                    } else {
                        Glide.with(UserProfileActivity.this).load(R.drawable.em_default_avatar).into(mUserProfileIvAvatar);
                    }
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
            }
        });
    }


    private void uploadHeadPhoto() {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(UserProfileActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }


    private void updateRemoteNick(final String nickName) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_nick), getString(R.string.dl_waiting));
        new Thread(new Runnable() {

            @Override
            public void run() {
                boolean updatenick = SuperWeChatHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(nickName);
                if (UserProfileActivity.this.isFinishing()) {
                    return;
                }
                if (!updatenick) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
                                    .show();
                            dialog.dismiss();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 调用AppServe改变昵称的方法
                            startUpdateAppServeNick(nickName);

                            dialog.dismiss();
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_success), Toast.LENGTH_SHORT)
                                    .show();
                            mUserProfileTvNick.setText(nickName);
                        }
                    });
                }
            }
        }).start();
    }

    private void startUpdateAppServeNick(String nickName) {
        NetDao.updateNick(this, user.getMUserName(), nickName
                , new OkHttpUtils.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String json) {
                        L.e(TAG, "updateNick,result:" + json);
                        if (json != null) {
                            Result result = ResultUtils.getResultFromJson(json, User.class);
                            User user = (User) result.getRetData();
                            updateLocalSQLNick(user);
                        } else {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
                                    .show();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        L.e("updateNick,error:" + error);
                        Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
                                .show();
                        dialog.dismiss();
                    }
                });
    }

    private void updateLocalSQLNick(User newuser) {
        user = newuser;
        SuperWeChatHelper.getInstance().saveAppContact(newuser);
        EaseUserUtils.setAppCurrentUserNick(mUserProfileTvNick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    startUpdateAvatar(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startUpdateAvatar(final Intent picData) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
        dialog.show();
        File file = saveBitmapFile(picData);

        NetDao.updateAvatar(this, user.getMUserName(), file, new OkHttpUtils.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s != null) {
                    Result result = ResultUtils.getResultFromJson(s, User.class);
                    L.e(TAG, "result=" + result);
                    if (result != null && result.isRetMsg()) {
                        // 将更新头像的用户保存到内存和硬盘中
                        User u = (User) result.getRetData();
                        SuperWeChatHelper.getInstance().saveAppContact(u);

                        setPicToView(picData);
                    } else {
                        dialog.dismiss();
                        CommonUtils.showShortMsgToast(result != null ? result.getRetCode() : -1);
                    }
                } else {
                    dialog.dismiss();
                    CommonUtils.showShortToast(R.string.toast_updatephoto_fail);
                }
            }

            @Override
            public void onError(String error) {
                L.e(TAG, "error=" + error);
                dialog.dismiss();
                CommonUtils.showShortToast(R.string.toast_updatephoto_fail);
            }
        });
    }


    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * save the picture data
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(getResources(), photo);
            mUserProfileIvAvatar.setImageDrawable(drawable);
            dialog.dismiss();
            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 更新云端的头像暂时不需要
     * @param data
     */
    private void uploadUserAvatar(final byte[] data) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
        new Thread(new Runnable() {

            @Override
            public void run() {
                final String avatarUrl = SuperWeChatHelper.getInstance().getUserProfileManager().uploadUserAvatar(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (avatarUrl != null) {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_fail),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        }).start();
    }


    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    @OnClick({R.id.ctitle_ivback, R.id.user_profile_avatarLayout, R.id.user_profile_nickLayout, R.id.user_profile_account})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ctitle_ivback:
                MFGT.finish(this);
                break;
            case R.id.user_profile_avatarLayout:
                uploadHeadPhoto();
                break;
            case R.id.user_profile_nickLayout:
                startUpdateNick();
                break;
            case R.id.user_profile_account:
                CommonUtils.showShortToast(R.string.toast_account_not_change);
                break;
        }
    }

    private void startUpdateNick() {
        final EditText editText = new EditText(this);
        // 初始化当前用户的昵称
        editText.setText(user.getMUserNick());
        new AlertDialog.Builder(this).setTitle(R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info).setView(editText)
                .setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nickString = editText.getText().toString().trim();
                        if (TextUtils.isEmpty(nickString)) {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (nickString.equals(user.getMUserNick())) {
                            CommonUtils.showShortToast(R.string.toast_sameNick);
                            return;
                        }
                        updateRemoteNick(nickString);
                    }
                }).setNegativeButton(R.string.dl_cancel, null).show();
    }

    public File saveBitmapFile(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            String imagePath = EaseImageUtils.getImagePath(user.getMUserName() + I.AVATAR_SUFFIX_JPG);
            File file = new File(imagePath);//将要保存图片的路径
            L.e("file path=" + file.getAbsolutePath());
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }
        return null;
    }
}
