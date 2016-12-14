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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.live.data.model.Gift;
import cn.ucai.superwechat.live.ui.GridMarginDecoration;
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
    }

    /**
     * 异步加载礼物信息的方法
     */
    private void syncLoadGiftInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetDao.findGiftInfo(context, new OkHttpUtils.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String json) {
                        Result result = ResultUtils.getListResultFromJson(json, Gift.class);
                        if (result != null && result.isRetMsg()) {
                            mList = (ArrayList<Gift>) result.getRetData();
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
        }).start();
    }


    private RoomUserDetailsDialog.UserDetailsDialogListener dialogListener;

    public void setUserDetailsDialogListener(RoomUserDetailsDialog.UserDetailsDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @OnClick({R.id.gift_detail_tv_showMoney, R.id.gift_detail_tv_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gift_detail_tv_showMoney:
                break;
            case R.id.gift_detail_tv_send:
                break;
        }
    }

    interface GiftDetailsDialogListener {
        void onMentionClick(String username);
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
            Gift bean = list.get(position);
            GiftHolder gHolder = (GiftHolder) holder;

                gHolder.mGiftItemTvMoney.setText(bean.getGprice() + "无诺币");
                gHolder.mGiftItemTvTitle.setText(bean.getGname());
                Glide.with(context)
                        .load(bean.getGurl())
                        .placeholder(R.color.placeholder)
                        .into(gHolder.mGiftItemIvShow);

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

        public GiftHolder(View view) {
            super(view);
            mGiftItemIvShow = (ImageView) view.findViewById(R.id.gift_item_iv_show);
            mGiftItemTvTitle = (TextView) view.findViewById(R.id.gift_item_tv_title);
            mGiftItemTvMoney = (TextView) view.findViewById(R.id.gift_item_tv_money);
        }
    }

}
