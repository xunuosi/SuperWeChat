package com.hyphenate.chatuidemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hyphenate.chatuidemo.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_login_wel, R.id.btn_register_wel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_wel:

                break;
            case R.id.btn_register_wel:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }
}
