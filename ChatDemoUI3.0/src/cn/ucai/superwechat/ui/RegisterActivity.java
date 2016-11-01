/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.MFGT;

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.ctitle_ivback)
    ImageView mCtitleIvback;
    @BindView(R.id.ctitle_tvTitle)
    TextView mCtitleTvTitle;
    @BindView(R.id.et_username)
    EditText mEtUsername;
    @BindView(R.id.et_nick)
    EditText mEtNick;
    @BindView(R.id.et_pwd)
    EditText mEtPassword;
    @BindView(R.id.et_cpwd)
    EditText mEtRepassword;

    RegisterActivity mContext;
    String username;
    String pwd;
    ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_register);
        ButterKnife.bind(this);
        mCtitleIvback.setVisibility(View.VISIBLE);
        mCtitleTvTitle.setVisibility(View.VISIBLE);
        mContext = this;
    }

    public void register() {
        username = mEtUsername.getText().toString().trim();
        pwd = mEtPassword.getText().toString().trim();
        String nick = mEtNick.getText().toString().trim();
        String confirm_pwd = mEtRepassword.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            CommonUtils.showShortToast(R.string.User_name_cannot_be_empty);
            mEtUsername.requestFocus();
            return;
        } else if (!username.matches("[a-zA-Z]\\w{5,15}")) {
            CommonUtils.showShortToast(R.string.illegal_user_name);
            mEtUsername.requestFocus();
            return;
        }else if (TextUtils.isEmpty(nick)) {
            CommonUtils.showShortToast(R.string.toast_nick_not_isnull);
            mEtNick.requestFocus();
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            CommonUtils.showShortToast(R.string.Password_cannot_be_empty);
            mEtPassword.requestFocus();
            return;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            CommonUtils.showShortToast(R.string.Confirm_password_cannot_be_empty);
            mEtRepassword.requestFocus();
            return;
        } else if (!pwd.equals(confirm_pwd)) {
            CommonUtils.showShortToast(R.string.Two_input_password);
            return;
        }

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
            pd = new ProgressDialog(this);
            pd.setMessage(getResources().getString(R.string.Is_the_registered));
            pd.show();

            registerHXService();

        }
    }

    private void registerHXService() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // call method in SDK
                    EMClient.getInstance().createAccount(username, pwd);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            // save current user
                            SuperWeChatHelper.getInstance().setCurrentUserName(username);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            int errorCode = e.getErrorCode();
                            if (errorCode == EMError.NETWORK_ERROR) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MFGT.finish(this);
    }

    @OnClick({R.id.ctitle_ivback, R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ctitle_ivback:
                MFGT.finish(this);
                break;
            case R.id.btn_register:
                register();
                break;
        }
    }
}
