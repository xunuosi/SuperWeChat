package cn.ucai.superwechat.ui;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.utils.MFGT;

public class CheckMessageActivity extends BaseActivity {

    ProgressDialog progressDialog;
    String addUserName;
    String message="";

    @BindView(R.id.ctitle_ivback)
    ImageView mCtitleIvback;
    @BindView(R.id.ctitle_view_left)
    View mCtitleViewLeft;
    @BindView(R.id.ctitle_tvLeft)
    TextView mCtitleTvLeft;
    @BindView(R.id.ctitle_btRight)
    Button mCtitleBtRight;
    @BindView(R.id.cm_et_message)
    EditText mCmEtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_message);
        ButterKnife.bind(this);
        addUserName = getIntent().getStringExtra(I.User.PASSWORD);
        if (addUserName == null) {
            MFGT.finish(this);
        }
        initView();
    }

    private void initView() {
        mCtitleIvback.setVisibility(View.VISIBLE);
        mCtitleBtRight.setVisibility(View.VISIBLE);
        mCtitleTvLeft.setVisibility(View.VISIBLE);
        mCtitleViewLeft.setVisibility(View.VISIBLE);
        mCtitleTvLeft.setText(R.string.app_name);

        message = "我是" + EaseUserUtils.getCurrentAppUserInfo().getMUserNick();
        mCmEtMessage.setText(message);
    }

    @OnClick({R.id.ctitle_ivback, R.id.ctitle_btRight})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ctitle_ivback:
                MFGT.finish(this);
                break;
            case R.id.ctitle_btRight:
                addContact();
                break;
        }
    }

    /**
     * 环信添加好友的方法
     */
    public void addContact() {
        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {

                try {
                    //demo use a hardcode reason here, you need let user to input if you like
                    //String s = getResources().getString(R.string.Add_a_friend);
                    EMClient.getInstance().contactManager().addContact(addUserName, message);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
                            MFGT.finish(CheckMessageActivity.this);
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
                            MFGT.finish(CheckMessageActivity.this);
                        }
                    });
                }
            }
        }).start();
    }
}
