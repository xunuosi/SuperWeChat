package cn.ucai.superwechat.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

public class FriendProfileActivity extends BaseActivity {
    private static final String TAG = FriendProfileActivity.class.getSimpleName();

    User mUser = null;
    String addUserName;
    //Map<String, User> contactList;
    String username;
    boolean isFriend;

    @BindView(R.id.ctitle_ivback)
    ImageView mCtitleIvback;
    @BindView(R.id.ctitle_view_left)
    View mCtitleViewLeft;
    @BindView(R.id.ctitle_tvLeft)
    TextView mCtitleTvLeft;
    @BindView(R.id.fp_ivAvatar)
    ImageView mFpIvAvatar;
    @BindView(R.id.fp_tvNick)
    TextView mFpTvNick;
    @BindView(R.id.fp_tvAccount)
    TextView mFpTvAccount;
    @BindView(R.id.fp_btn_sendMessage)
    Button mFpBtnSendMessage;
    @BindView(R.id.fp_btn_addContacts)
    Button mFpBtnAddContacts;
    @BindView(R.id.fp_btn_videoTalk)
    Button mFpBtnVideoTalk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        ButterKnife.bind(this);
        username = getIntent().getStringExtra(I.User.USER_NAME);
        if (username == null) {
            MFGT.finish(this);
            return;
        } else {
            mUser = SuperWeChatHelper.getInstance().getAppContactList().get(username);
        }
        initView();
        // 判断是否为好友
        if (mUser == null) {
            isFriend = false;
        } else {
            isFriend = true;
        }
        // 不是好友应去服务器端下载,是好友应该去更新最新数据
        syncAppUserInfo();
    }

    private void syncAppUserInfo() {
        NetDao.findUserByUserName(this, username, new OkHttpUtils.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String json) {
                if (json != null) {
                    Result result = ResultUtils.getResultFromJson(json, User.class);
                    if (result != null && result.isRetMsg()) {
                        User syncUser = (User) result.getRetData();
                        if (syncUser != null) {
                            mUser = syncUser;
                            if (isFriend) {
                                SuperWeChatHelper.getInstance().saveAppContact(syncUser);
                            }
                            addUserName = mUser.getMUserName();
                        } else {
                            syncFailed();
                        }
                    } else {
                        syncFailed();
                    }
                } else {
                    syncFailed();
                }
                setView();
            }

            @Override
            public void onError(String error) {
                syncFailed();
            }
        });
    }

    /**
     * 获取服务器用户信息失败的方法
     */
    private void syncFailed() {
        if (!isFriend && mUser == null) {
            MFGT.finish(this);
        }
    }

    private void initView() {
        mCtitleIvback.setVisibility(View.VISIBLE);
        mCtitleViewLeft.setVisibility(View.VISIBLE);
        mCtitleTvLeft.setVisibility(View.VISIBLE);
        mCtitleTvLeft.setText(R.string.complete_information);
    }

    private void setView() {
        mFpTvAccount.setText(mUser.getMUserName());
        mFpTvNick.setText(mUser.getMUserNick());
        EaseUserUtils.setAppUserAvatar(this, mUser.getMUserName(), mFpIvAvatar);
        // 判断是否已经是好友
        if (isFriend) {
            mFpBtnSendMessage.setVisibility(View.VISIBLE);
            mFpBtnVideoTalk.setVisibility(View.VISIBLE);
        } else {
            mFpBtnAddContacts.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.ctitle_ivback,R.id.fp_btn_sendMessage,R.id.fp_btn_addContacts,R.id.fp_btn_videoTalk})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ctitle_ivback:
                MFGT.finish(this);
                break;
            case R.id.fp_btn_addContacts:
                startAddContacts();
                break;
            case R.id.fp_btn_sendMessage:
                MFGT.gotoChatActivity(this, addUserName);
                break;
            case R.id.fp_btn_videoTalk:
                if (!EMClient.getInstance().isConnected())
                    Toast.makeText(this, R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
                else {
                    startActivity(new Intent(this, VideoCallActivity.class).putExtra("username", username)
                            .putExtra("isComingCall", false));
                }
                break;
        }
    }

    private void startAddContacts() {
        MFGT.gotoCheckMessageActivity(this, addUserName);
    }
}
