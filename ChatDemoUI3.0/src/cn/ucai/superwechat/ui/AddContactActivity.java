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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.domain.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

public class AddContactActivity extends BaseActivity {
    private static final String TAG = AddContactActivity.class.getSimpleName();

    @BindView(R.id.ac_etSearch)
    EditText mAcEtSearch;
    @BindView(R.id.ac_tv_showMessage)
    TextView mAcTvShowMessage;
    @BindView(R.id.ac_showSearch_Layout)
    LinearLayout mAcShowSearchLayout;
    @BindView(R.id.ac_view_empty)
    View mAcViewEmpty;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_add_contact);
        ButterKnife.bind(this);
        setListener();
    }

    private void setListener() {
        // 监听EditText的实时录入
        mAcEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAcShowSearchLayout.setVisibility(View.VISIBLE);
                mAcViewEmpty.setVisibility(View.VISIBLE);
                mAcTvShowMessage.setText(mAcEtSearch.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mAcEtSearch.getText().length() == 0) {
                    mAcShowSearchLayout.setVisibility(View.GONE);
                    mAcViewEmpty.setVisibility(View.GONE);
                }
            }
        });
    }

    @OnClick({R.id.ac_ivback, R.id.ac_showSearch_Layout})
    public void onClickToChoose(View view) {
        switch (view.getId()) {
            case R.id.ac_ivback:
                MFGT.finish(this);
                break;
            case R.id.ac_showSearch_Layout:
                // 根据输入用户名查询用户信息
                searchUserInfo();
                break;
        }
    }

    private void searchUserInfo() {
        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        final String username = mAcTvShowMessage.getText().toString().trim();
        NetDao.findUserByUserName(this, username
                , new OkHttpUtils.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        progressDialog.dismiss();
                        if (s != null) {
                            Result result = ResultUtils.getResultFromJson(s, User.class);
                            L.e(TAG, "searchAppUser,result=" + result);
                            if (result != null && result.isRetMsg()) {
                                User user = (User) result.getRetData();
                                if (user != null) {
                                    MFGT.gotoFriendProfile(AddContactActivity.this, user.getMUserName());
                                }
                            } else {
                                CommonUtils.showShortToast(R.string.msg_104);
                            }
                        } else {
                            CommonUtils.showShortToast(R.string.msg_104);
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

	/*
    public void searchContact(View v) {
		final String name = editText.getText().toString();
		String saveText = searchBtn.getText().toString();
		
		if (getString(R.string.button_search).equals(saveText)) {
			toAddUsername = name;
			if(TextUtils.isEmpty(name)) {
				new EaseAlertDialog(this, R.string.Please_enter_a_username).show();
				return;
			}

		} 
	}	*/
    /*
    public void addContact(View view){
		if(EMClient.getInstance().getCurrentUser().equals(nameText.getText().toString())){
			new EaseAlertDialog(this, R.string.not_add_myself).show();
			return;
		}
		
		if(SuperWeChatHelper.getInstance().getContactList().containsKey(nameText.getText().toString())){
		    //let the user know the contact already in your contact list
		    if(EMClient.getInstance().contactManager().getBlackListUsernames().contains(nameText.getText().toString())){
		        new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
		        return;
		    }
			new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
			return;
		}
		
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
					EMClient.getInstance().contactManager().addContact(toAddUsername, s);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s1 = getResources().getString(R.string.send_successful);
							Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s2 = getResources().getString(R.string.Request_add_buddy_failure);
							Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();
	}*/

}
