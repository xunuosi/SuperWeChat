package cn.ucai.superwechat.live.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.live.data.model.Gift;
import cn.ucai.superwechat.live.ui.GridMarginDecoration;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

/**
 * 显示礼物列表的Dialog
 */

public class GiftDetailsDialog extends DialogFragment {
    private static final String TAG = GiftDetailsDialog.class.getSimpleName();

    Unbinder unbinder;
    @BindView(R.id.gift_detail_rl)
    RecyclerView mGiftDetailRl;
    @BindView(R.id.gift_detail_tv_showMoney)
    TextView mGiftDetailTvShowMoney;
    @BindView(R.id.gift_detail_tv_send)
    TextView mGiftDetailTvSend;

    private Context context;
    private ArrayList<Gift> mList = new ArrayList<>();
    private GiftAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;

    public static GiftDetailsDialog newInstance() {
        GiftDetailsDialog dialog = new GiftDetailsDialog();
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_gift_details, container, false);
        unbinder = ButterKnife.bind(this, view);
        context = getContext();
        syncLoadGiftInfo();
        initView();
        return view;
    }

    private void initView() {
        mAdapter = new GiftAdapter(context, mList);
        mGridLayoutManager = new GridLayoutManager(context, 4);
        mGiftDetailRl.addItemDecoration(new GridMarginDecoration(6));
        mGiftDetailRl.setAdapter(mAdapter);
        mGiftDetailRl.setLayoutManager(mGridLayoutManager);
        mGiftDetailRl.setHasFixedSize(true);
        mGiftDetailTvShowMoney.setText(String.valueOf(SuperWeChatHelper.getInstance().getAppCurrentCharge()));
    }

    /**
     * 异步加载礼物信息的方法
     */
    private void syncLoadGiftInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<Integer, Gift> giftMap = SuperWeChatHelper.getInstance().getAppGiftList();
                if (giftMap != null && !giftMap.isEmpty()) {
                    ArrayList<Gift> gifts = new ArrayList<>(giftMap.values());
                    Collections.sort(gifts, new Comparator<Gift>() {
                        @Override
                        public int compare(Gift lhs, Gift rhs) {
                            return lhs.getGprice() - rhs.getGprice();
                        }
                    });
                    mAdapter.syncUpdate(gifts);
                } else {
                    NetDao.findGiftInfo(context, new OkHttpUtils.OnCompleteListener<String>() {
                        @Override
                        public void onSuccess(String json) {
                            Result result = ResultUtils.getListResultFromJson(json, Gift.class);
                            if (result != null && result.isRetMsg()) {
                                mList = (ArrayList<Gift>) result.getRetData();
                                SuperWeChatHelper.getInstance().updateAppGiftList(mList);
                                mAdapter.syncUpdate(mList);
                            } else {
                                Toast.makeText(context, getString(R.string.Network_error), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, error);
                        }
                    });
                }
            }
        }).start();
    }


    private GiftDetailsDialogListener dialogListener;

    public void setGiftDetailsDialogListener(GiftDetailsDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @OnClick({R.id.gift_detail_tv_showMoney, R.id.gift_detail_tv_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gift_detail_tv_showMoney:
                break;
            case R.id.gift_detail_tv_send:
                MFGT.gotoRechargeActivity(getContext());
                break;
        }
    }

    interface GiftDetailsDialogListener {
        void onMentionClick(String gName, int gId, int resId, int price);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 使用不带theme的构造器，获得的dialog边框距离屏幕仍有几毫米的缝隙。
        // Dialog dialog = new Dialog(getActivity());
        Dialog dialog = new Dialog(getActivity(), R.style.room_user_details_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // must be called before set content
        dialog.setContentView(R.layout.fragment_room_gift_details);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class GiftAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context mContext;
        private ArrayList<Gift> list;

        public GiftAdapter(Context context, ArrayList<Gift> mList) {
            mContext = context;
            list = new ArrayList<>();
            list.addAll(mList);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            GiftHolder holder = null;
            View layout = LayoutInflater.from(mContext).inflate(R.layout.live_gift_item, parent, false);
            holder = new GiftHolder(layout);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final Gift bean = list.get(position);
            GiftHolder gHolder = (GiftHolder) holder;

            gHolder.mGiftItemTvMoney.setText(bean.getGprice() + "无诺币");
            gHolder.mGiftItemTvTitle.setText(bean.getGname());
            String imgStr = "hani_gift_" + bean.getId();
            final int resId = context.getResources().getIdentifier(imgStr, "drawable", context.getPackageName());
            gHolder.mGiftItemIvShow.setImageResource(resId);

            gHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogListener.onMentionClick(bean.getGname(), bean.getId(), resId, bean.getGprice());
                }
            });
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public void syncUpdate(ArrayList<Gift> newList) {
            list.clear();
            list.addAll(newList);
            notifyDataSetChanged();
        }

    }

    protected class GiftHolder extends RecyclerView.ViewHolder {
        ImageView mGiftItemIvShow;
        TextView mGiftItemTvTitle;
        TextView mGiftItemTvMoney;
        LinearLayout mLayout;

        public GiftHolder(View view) {
            super(view);
            mGiftItemIvShow = (ImageView) view.findViewById(R.id.gift_item_iv_show);
            mGiftItemTvTitle = (TextView) view.findViewById(R.id.gift_item_tv_title);
            mGiftItemTvMoney = (TextView) view.findViewById(R.id.gift_item_tv_money);
            mLayout = (LinearLayout) view.findViewById(R.id.gift_item_layout);
        }
    }

}
