package cn.ucai.superwechat.live.ui.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.live.data.model.Wallet;
import cn.ucai.superwechat.ui.BaseActivity;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

/**
 * 显示诺丸币的界面
 */

public class ChargeAcitvity extends BaseActivity {

    @BindView(R.id.left_image)
    ImageView mLeftImage;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.subtitle)
    TextView mSubtitle;
    @BindView(R.id.tv_change_balance)
    TextView mTvChangeBalance;
    @BindView(R.id.live_charge_progressBar)
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_charge);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        pb.setVisibility(View.VISIBLE);
        mLeftImage.setImageResource(R.drawable.rp_back_arrow_yellow);
        mTitle.setText(R.string.balance);
        mSubtitle.setText(R.string.nuo_charge);
        mTvChangeBalance.setText("￥0.00");
    }

    /**
     * 异步加载钱包数据的方法
     */
    private void syncUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetDao.findCharge(getApplicationContext(), EaseUserUtils.getCurrentAppUserInfo().getMUserName(),
                        new OkHttpUtils.OnCompleteListener<String>() {
                            @Override
                            public void onSuccess(String json) {
                                Result result = ResultUtils.getResultFromJson(json, Wallet.class);
                                if (result != null && result.isRetMsg()) {
                                    Wallet wallet = (Wallet) result.getRetData();
                                    double f = wallet.getBalance() / 10.0;
                                    mTvChangeBalance.setText("￥" + String.format("%.2f", f));
                                    // 将更新的余额存入内存和首选项各一份
                                    SuperWeChatHelper.getInstance().updateAppCurrentCharge(wallet.getBalance());
                                } else {
                                    Toast.makeText(ChargeAcitvity.this, "获取钱包余额失败,请重试。", Toast.LENGTH_SHORT).show();
                                }
                                pb.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
            }
        }).start();
    }

    @OnClick(R.id.tv_change_recharge)
    public void onClick() {
        MFGT.gotoRechargeActivity(this);
    }

    @OnClick(R.id.left_image)
    public void onBackClick() {
        MFGT.finish(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncUpdate();
    }
}
