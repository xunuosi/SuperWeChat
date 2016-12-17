package cn.ucai.superwechat.live.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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
 * 充值金额的界面
 */

public class RechargeActivity extends BaseActivity {

    @BindView(R.id.et_recharge_amount)
    EditText mEtRechargeAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_recharge);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_recharge)
    public void onClick() {
        checkInfo();
    }

    private void checkInfo() {
        String n = mEtRechargeAmount.getText().toString();
        if (n.equals("")) {
            Toast.makeText(this, R.string.recharge_money_not_null, Toast.LENGTH_SHORT).show();
            return;
        } else if (n.indexOf("0") == 0) {
            Toast.makeText(this, R.string.recharge_invalid, Toast.LENGTH_SHORT).show();
            return;
        } else if (Integer.parseInt(n) == 0) {
            Toast.makeText(this, R.string.recharge_money_not_zero, Toast.LENGTH_SHORT).show();
            return;
        }
        gotoRecharge(n);
    }

    private void gotoRecharge(final String n) {
        final ProgressBar pb = new ProgressBar(this);
        pb.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String username = SuperWeChatHelper.getInstance().getCurrentUsernName();
                NetDao.recharege(RechargeActivity.this, username, n, new OkHttpUtils.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String json) {
                        Result result = ResultUtils.getResultFromJson(json, Wallet.class);
                        pb.setVisibility(View.GONE);
                        if (result != null && result.isRetMsg()) {
                            Toast.makeText(RechargeActivity.this, R.string.recharge_success, Toast.LENGTH_SHORT).show();
                            Wallet wallet = (Wallet) result.getRetData();
                            SuperWeChatHelper.getInstance().updateAppCurrentCharge(wallet.getBalance());
                        } else {
                            Toast.makeText(RechargeActivity.this, R.string.recharge_fail, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        }).start();
        MFGT.finish(this);
    }
}
