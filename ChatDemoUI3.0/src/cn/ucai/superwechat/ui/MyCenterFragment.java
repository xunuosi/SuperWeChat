package cn.ucai.superwechat.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.redpacketui.utils.RedPacketUtil;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.Constant;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.utils.MFGT;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCenterFragment extends Fragment {


    @BindView(R.id.mycenter_ivAvatar)
    ImageView mMycenterIvAvatar;
    @BindView(R.id.mycenter_tvNick)
    TextView mMycenterTvNick;
    @BindView(R.id.mycenter_tvAccount)
    TextView mMycenterTvAccount;

    public MyCenterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_center, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 重新构建时判断是否为重新登录，如果为之前用户不需要初始化内容
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        setInfo();
    }

    private void setInfo() {
        EaseUserUtils.setAppCurrentUserAvatar(getActivity(), mMycenterIvAvatar);
        EaseUserUtils.setAppCurrentUserNick(mMycenterTvNick);
        EaseUserUtils.setAppCurrentUsernameWithNo(mMycenterTvAccount);
    }

    /**
     * 保存被销毁前的状态用于重新构建时使用
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        } else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }
    }

    @OnClick({R.id.mycenter_personInfo_layout, R.id.mycenter_setting_layout, R.id.mycenter_wallet_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mycenter_personInfo_layout:
                break;
            case R.id.mycenter_setting_layout:
                MFGT.gotoSettingsActivity(getActivity());
                break;
            //red packet code : 进入零钱页面
            case R.id.mycenter_wallet_layout:
                RedPacketUtil.startChangeActivity(getActivity());
                break;
            //end of red packet code
        }
    }
}
