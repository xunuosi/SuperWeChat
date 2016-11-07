package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.utils.MFGT;
import rx.subjects.BehaviorSubject;

public class AddFriendActivity extends AppCompatActivity {

    @BindView(R.id.ctitle_ivback)
    ImageView mCtitleIvback;
    @BindView(R.id.ctitle_view_left)
    View mCtitleViewLeft;
    @BindView(R.id.ctitle_tvLeft)
    TextView mCtitleTvLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mCtitleIvback.setVisibility(View.VISIBLE);
        mCtitleTvLeft.setVisibility(View.VISIBLE);
        mCtitleViewLeft.setVisibility(View.VISIBLE);
        mCtitleTvLeft.setText(R.string.add_friend);
    }

    @OnClick({R.id.af_find_layout,R.id.af_etSearch,R.id.ctitle_ivback})
    public void gotoFindFriend(View view) {
        switch (view.getId()) {
            case R.id.af_find_layout:
                MFGT.gotoAddContactActivity(this);
            case R.id.af_etSearch:
                MFGT.gotoAddContactActivity(this);
                break;
            case R.id.ctitle_ivback:
                MFGT.finish(this);
                break;
        }
    }
}
