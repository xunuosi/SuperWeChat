package cn.ucai.superwechat.ui;


import android.app.ProgressDialog;
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
import cn.ucai.superwechat.utils.MFGT;

public class FriendProfileActivity extends BaseActivity {
    private static final String TAG = FriendProfileActivity.class.getSimpleName();

    User mUser;
    String addUserName;
    Map<String, User> contactList;
    ProgressDialog progressDialog;

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
        mUser = (User) getIntent().getSerializableExtra(I.User.USER_NAME);
        addUserName = mUser.getMUserName();
        initView();
    }

    private void initView() {
        mCtitleIvback.setVisibility(View.VISIBLE);
        mCtitleViewLeft.setVisibility(View.VISIBLE);
        mCtitleTvLeft.setVisibility(View.VISIBLE);
        mCtitleTvLeft.setText(R.string.complete_information);

        mFpTvAccount.setText(mUser.getMUserName());
        mFpTvNick.setText(mUser.getMUserNick());
        EaseUserUtils.setAppUserAvatar(this, mUser.getMUserName(), mFpIvAvatar);
        // 判断是否已经是好友
        contactList = SuperWeChatHelper.getInstance().getAppContactList();
        if (contactList.containsKey(mUser.getMUserName())) {
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
                break;
            case R.id.fp_btn_videoTalk:
                break;
        }
    }

    private void startAddContacts() {
        MFGT.gotoCheckMessageActivity(this);
    }

    /**
     * 环信添加好友的方法
     *
     */
    public void addContact(){
        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {

                try {
                    //demo use a hardcode reason here, you need let user to input if you like
                    String s = getResources().getString(R.string.Add_a_friend);
                    EMClient.getInstance().contactManager().addContact(addUserName, s);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
                            MFGT.finish(FriendProfileActivity.this);
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
                            MFGT.finish(FriendProfileActivity.this);
                        }
                    });
                }
            }
        }).start();
    }
}
