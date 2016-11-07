package cn.ucai.superwechat.ui;


import android.os.Bundle;

import com.hyphenate.easeui.domain.User;

import butterknife.ButterKnife;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.utils.L;

public class FriendProfileActivity extends BaseActivity {
    private static final String TAG = FriendProfileActivity.class.getSimpleName();

    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        ButterKnife.bind(this);
        mUser = (User) getIntent().getSerializableExtra(I.User.USER_NAME);
        if (mUser != null) {
            L.e(TAG, "user:" + mUser);
        }
    }
}
