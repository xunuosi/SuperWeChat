package cn.ucai.superwechat.live.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.Constant;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.live.data.model.Gift;
import cn.ucai.superwechat.live.data.model.Wallet;
import cn.ucai.superwechat.live.ui.widget.BarrageLayout;
import cn.ucai.superwechat.live.ui.widget.LiveLeftGiftView;
import cn.ucai.superwechat.live.ui.widget.PeriscopeLayout;
import cn.ucai.superwechat.live.ui.widget.RoomMessagesView;
import cn.ucai.superwechat.live.utils.Utils;
import cn.ucai.superwechat.ui.BaseActivity;
import cn.ucai.superwechat.ui.ConversationListFragment;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

/**
 * Created by wei on 2016/6/12.
 */
public abstract class LiveBaseActivity extends BaseActivity {
    protected static final String TAG = "LiveActivity";

    @BindView(R.id.left_gift_view1)
    LiveLeftGiftView leftGiftView;
    @BindView(R.id.left_gift_view2)
    LiveLeftGiftView leftGiftView2;
    @BindView(R.id.message_view)
    RoomMessagesView messageView;
    @BindView(R.id.periscope_layout)
    PeriscopeLayout periscopeLayout;
    @BindView(R.id.bottom_bar)
    View bottomBar;

    @BindView(R.id.barrage_layout)
    BarrageLayout barrageLayout;
    @BindView(R.id.horizontal_recycle_view)
    RecyclerView horizontalRecyclerView;
    @BindView(R.id.audience_num)
    TextView audienceNumView;
    @BindView(R.id.new_messages_warn)
    ImageView newMsgNotifyImage;

    protected String anchorId;
    GiftDetailsDialog gDialog;
    private int charge;
    private int gResId;
    private String gName;
    Dialog payDialog;
    private Gift gift;
    /**
     * 环信聊天室id
     */
    protected String chatroomId = "";
    /**
     * ucloud直播id
     */
    protected String liveId = "";
    protected boolean isMessageListInited;
    protected EMChatRoomChangeListener chatRoomChangeListener;

    volatile boolean isGiftShowing = false;
    volatile boolean isGift2Showing = false;
    List<EMMessage> toShowList = Collections.synchronizedList(new LinkedList<EMMessage>());

    protected EMChatRoom chatroom;
    List<String> memberList = new ArrayList<>();
    private boolean notTip = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onActivityCreate(savedInstanceState);
    }

    protected Handler handler = new Handler();

    protected abstract void onActivityCreate(@Nullable Bundle savedInstanceState);

    protected synchronized void showLeftGiftVeiw(EMMessage message) {
        if (!isGiftShowing) {
            showGift1Derect(message);
        } else if (!isGift2Showing) {
            showGift2Derect(message);
        } else {
            toShowList.add(message);
        }
    }

    private void showGift1Derect(final EMMessage message) {
        final String nick = message.getStringAttribute(I.User.NICK, message.getFrom());
        isGiftShowing = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                leftGiftView.setVisibility(View.VISIBLE);
                leftGiftView.setName(nick);
                leftGiftView.setAvatar(message.getFrom());
                try {
                    leftGiftView.setGiftImageView(message.getIntAttribute(I.Gift.GIFT_RES_ID));
                    leftGiftView.setLeftGiftTvShowGname(message.getStringAttribute(I.Gift.GIFT_NAME));
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                leftGiftView.setTranslationY(0);
                ViewAnimator.animate(leftGiftView)
                        .alpha(0, 1)
                        .translationX(-leftGiftView.getWidth(), 0)
                        .duration(600)
                        .thenAnimate(leftGiftView)
                        .alpha(1, 0)
                        .translationY(-1.5f * leftGiftView.getHeight())
                        .duration(800)
                        .onStop(new AnimationListener.Stop() {
                            @Override
                            public void onStop() {
                                EMMessage pollName = null;
                                try {
                                    pollName = toShowList.remove(0);
                                } catch (Exception e) {

                                }
                                if (pollName != null) {
                                    showGift1Derect(pollName);
                                } else {
                                    isGiftShowing = false;
                                }
                            }
                        })
                        .startDelay(600)
                        .start();
                ViewAnimator.animate(leftGiftView.getGiftImageView())
                        .translationX(-leftGiftView.getGiftImageView().getX(), 0)
                        .duration(1100)
                        .start();
            }
        });
    }

    private void showGift2Derect(final EMMessage message) {
        final String nick = message.getStringAttribute(I.User.NICK, message.getFrom());
        isGift2Showing = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                leftGiftView2.setVisibility(View.VISIBLE);
                leftGiftView2.setName(nick);
                leftGiftView2.setAvatar(message.getFrom());
                try {
                    leftGiftView2.setGiftImageView(message.getIntAttribute(I.Gift.GIFT_RES_ID));
                    leftGiftView2.setLeftGiftTvShowGname(message.getStringAttribute(I.Gift.GIFT_NAME));
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                leftGiftView2.setTranslationY(0);
                ViewAnimator.animate(leftGiftView2)
                        .alpha(0, 1)
                        .translationX(-leftGiftView2.getWidth(), 0)
                        .duration(600)
                        .thenAnimate(leftGiftView2)
                        .alpha(1, 0)
                        .translationY(-1.5f * leftGiftView2.getHeight())
                        .duration(800)
                        .onStop(new AnimationListener.Stop() {
                            @Override
                            public void onStop() {
                                EMMessage pollName = null;
                                try {
                                    pollName = toShowList.remove(0);
                                } catch (Exception e) {

                                }
                                if (pollName != null) {
                                    showGift2Derect(pollName);
                                } else {
                                    isGift2Showing = false;
                                }
                            }
                        })
                        .startDelay(600)
                        .start();
                ViewAnimator.animate(leftGiftView2.getGiftImageView())
                        .translationX(-leftGiftView2.getGiftImageView().getX(), 0)
                        .duration(1100)
                        .start();
            }
        });
    }

    protected void addChatRoomChangeListenr() {
        chatRoomChangeListener = new EMChatRoomChangeListener() {

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                if (roomId.equals(chatroomId)) {
                    EMLog.e(TAG, " room : " + roomId + " with room name : " + roomName + " was destroyed");
                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {// 成员加入时触发的方法
                EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                message.setReceipt(chatroomId);
                message.setFrom(participant);
                EMTextMessageBody textMessageBody = new EMTextMessageBody("来了");
                message.addBody(textMessageBody);
                message.setChatType(EMMessage.ChatType.ChatRoom);
                EMClient.getInstance().chatManager().saveMessage(message);
                messageView.refreshSelectLast();

                onRoomMemberAdded(participant);
            }

            @Override
            public void onMemberExited(String roomId, String roomName, String participant) {
                //                showChatroomToast("member : " + participant + " leave the room : " + roomId + " room name : " + roomName);
                onRoomMemberExited(participant);
            }

            @Override
            public void onMemberKicked(String roomId, String roomName, String participant) {
                if (roomId.equals(chatroomId)) {
                    String curUser = EMClient.getInstance().getCurrentUser();
                    if (curUser.equals(participant)) {
                        EMClient.getInstance().chatroomManager().leaveChatRoom(roomId);
                        showToast("你已被移除出此房间");
                        finish();
                    } else {
                        //                        showChatroomToast("member : " + participant + " was kicked from the room : " + roomId + " room name : " + roomName);
                        onRoomMemberExited(participant);
                    }
                }
            }
        };

        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomChangeListener);
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {

            for (EMMessage message : messages) {
                String username = null;
                // 群组消息
                if (message.getChatType() == EMMessage.ChatType.GroupChat
                        || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }
                // 如果是当前会话的消息，刷新聊天页面
                if (username.equals(chatroomId)) {
                    if (message.getBooleanAttribute(Constant.EXTRA_IS_BARRAGE_MSG, false)) {
                        barrageLayout.addBarrage(((EMTextMessageBody) message.getBody()).getMessage(),
                                message.getFrom());
                    }
                    messageView.refreshSelectLast();
                } else {
                    if (message.getChatType() == EMMessage.ChatType.Chat && message.getTo().equals(EMClient.getInstance().getCurrentUser())) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newMsgNotifyImage.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    //// 如果消息不是和当前聊天ID的消息
                    //EaseUI.getInstance().getNotifier().onNewMsg(message);
                }
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            EMMessage message = messages.get(messages.size() - 1);
            if (Constant.CMD_GIFT.equals(((EMCmdMessageBody) message.getBody()).action())) {
                showLeftGiftVeiw(message);
            }
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            if (isMessageListInited) {
                //                messageList.refresh();
            }
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            if (isMessageListInited) {
                //                messageList.refresh();
            }
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            if (isMessageListInited) {
                messageView.refresh();
            }
        }
    };

    protected void onMessageListInit() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageView.init(chatroomId);
                messageView.setMessageViewListener(new RoomMessagesView.MessageViewListener() {
                    @Override
                    public void onMessageSend(String content) {// 发送信息的方法
                        EMMessage message = EMMessage.createTxtSendMessage(content, chatroomId);
                        // 显示弹幕
                        if (messageView.isBarrageShow) {
                            message.setAttribute(Constant.EXTRA_IS_BARRAGE_MSG, true);
                            barrageLayout.addBarrage(content, EMClient.getInstance().getCurrentUser());
                        }
                        message.setChatType(EMMessage.ChatType.ChatRoom);
                        // 加入发送者的昵称
                        message.setAttribute(I.User.NICK, EaseUserUtils.getCurrentAppUserInfo().getMUserNick());
                        EMClient.getInstance().chatManager().sendMessage(message);
                        message.setMessageStatusCallback(new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                //刷新消息列表
                                messageView.refreshSelectLast();
                            }

                            @Override
                            public void onError(int i, String s) {
                                showToast("消息发送失败！");
                            }

                            @Override
                            public void onProgress(int i, String s) {

                            }
                        });
                    }

                    @Override
                    public void onItemClickListener(final EMMessage message) {
                        //if(message.getFrom().equals(EMClient.getInstance().getCurrentUser())){
                        //    return;
                        //}
                        String clickUsername = message.getFrom();
                        showUserDetailsDialog(clickUsername);
                    }

                    @Override
                    public void onHiderBottomBar() {
                        bottomBar.setVisibility(View.VISIBLE);
                    }
                });
                messageView.setVisibility(View.VISIBLE);
                bottomBar.setVisibility(View.VISIBLE);
                isMessageListInited = true;
                updateUnreadMsgView();
                showMemberList();
            }
        });
    }

    protected void updateUnreadMsgView() {
        if (isMessageListInited) {
            for (EMConversation conversation : EMClient.getInstance()
                    .chatManager()
                    .getAllConversations()
                    .values()) {
                if (conversation.getType() == EMConversation.EMConversationType.Chat
                        && conversation.getUnreadMsgCount() > 0) {
                    newMsgNotifyImage.setVisibility(View.VISIBLE);
                    return;
                }
            }
            newMsgNotifyImage.setVisibility(View.INVISIBLE);
        }
    }

    private void showGiftDetailsDialog() {
        gDialog = GiftDetailsDialog.newInstance();
//        dialog.setUserDetailsDialogListener(
//                new RoomUserDetailsDialog.UserDetailsDialogListener() {
//                    @Override
//                    public void onMentionClick(String username) {
//                        dialog.dismiss();
//                        messageView.getInputView().setText("@" + username + " ");
//                        showInputView();
//                    }
//                });
        gDialog.setGiftDetailsDialogListener(new GiftDetailsDialog.GiftDetailsDialogListener() {
            @Override
            public void onMentionClick(String cName, int gId, int resId, int price) {
                // dialog.dismiss();
                charge = price;
                gName = cName;
                gResId = resId;
                gift = SuperWeChatHelper.getInstance().getAppGiftList().get(gId);
                // 如果已经设置不再弹出支付提示则直接进入支付逻辑方法
                if (!SuperWeChatHelper.getInstance().getAppPayTip()) {
                    showPayConfirmDialog();
                } else {
                    payGift();
                }
            }
        });
        gDialog.show(getSupportFragmentManager(), "GiftDetailsDialog");
    }

    /**
     * 显示确认支付的对话框
     *
     */
    private void showPayConfirmDialog() {
        payDialog = new Dialog(this, R.style.Translucent_Dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.pay_dialog_layout, null);
        payDialog.setContentView(view);
        //获取到当前Activity的Window
        Window dialog_window = payDialog.getWindow();
        //获取到LayoutParams
        WindowManager.LayoutParams dialog_window_attributes = dialog_window.getAttributes();
        //设置宽度
        dialog_window_attributes.width = 800;
        //设置高度
        dialog_window_attributes.height = 600;
        dialog_window.setAttributes(dialog_window_attributes);
        payDialog.show();
        view.findViewById(R.id.pay_dialog_btnPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payGift();
                SuperWeChatHelper.getInstance().setAppPayTip(notTip);
                payDialog.dismiss();
            }
        });
        view.findViewById(R.id.pay_dialog_btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payDialog.dismiss();
            }
        });
        CheckBox cbView = (CheckBox) view.findViewById(R.id.pay_dialog_cb);
        cbView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notTip = isChecked;
            }
        });
    }

    /**
     * 支付判断方法
     */
    private void payGift() {
        int userCharge = SuperWeChatHelper.getInstance().getAppCurrentCharge();
        if (userCharge < charge) {
            showInsufficientBalance();
        } else {
            pay2sendGift();
        }
    }

    /**
     * 支付礼物和发送礼物的方法
     */
    private void pay2sendGift() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String user = SuperWeChatHelper.getInstance().getCurrentUsernName();
                String toUser = anchorId;
                int gId = gift.getId();
                NetDao.sendGift(LiveBaseActivity.this, user, toUser, gId, new OkHttpUtils.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String json) {
                        Result result = ResultUtils.getResultFromJson(json, Wallet.class);
                        if (result != null && result.isRetMsg()) {
                            sendPresentMessage(gName, gResId);
                            Wallet wallet = (Wallet) result.getRetData();
                            SuperWeChatHelper.getInstance().updateAppCurrentCharge(wallet.getBalance());
                        } else {
                            Toast.makeText(LiveBaseActivity.this, "抱歉！网络异常，支付失败。", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(LiveBaseActivity.this, "抱歉！网络异常，支付失败。", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }


    /**
     * 显示余额不足的对话框
     */
    private void showInsufficientBalance() {
        payDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.insufficient_balance)
                .setMessage(R.string.recharge_money_tip);
        builder.setPositiveButton("去充值", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MFGT.gotoRechargeActivity(LiveBaseActivity.this);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showUserDetailsDialog(String username) {
        final RoomUserDetailsDialog dialog =
                RoomUserDetailsDialog.newInstance(username);
        dialog.setUserDetailsDialogListener(
                new RoomUserDetailsDialog.UserDetailsDialogListener() {
                    @Override
                    public void onMentionClick(String username) {
                        dialog.dismiss();
                        messageView.getInputView().setText("@" + username + " ");
                        showInputView();
                    }
                });
        dialog.show(getSupportFragmentManager(), "RoomUserDetailsDialog");
    }

    private void showInputView() {
        bottomBar.setVisibility(View.INVISIBLE);
        messageView.setShowInputView(true);
        messageView.getInputView().requestFocus();
        messageView.getInputView().requestFocusFromTouch();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.showKeyboard(messageView.getInputView());
            }
        }, 200);
    }

    void showMemberList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(LiveBaseActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        horizontalRecyclerView.setLayoutManager(layoutManager);
        horizontalRecyclerView.setAdapter(new AvatarAdapter(LiveBaseActivity.this, memberList));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    chatroom =
                            EMClient.getInstance().chatroomManager().fetchChatRoomFromServer(chatroomId, true);
                    memberList.addAll(chatroom.getMemberList());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        audienceNumView.setText(String.valueOf(memberList.size()));
                        horizontalRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void onRoomMemberAdded(String name) {
        if (!memberList.contains(name)) memberList.add(name);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                audienceNumView.setText(String.valueOf(memberList.size()));
                horizontalRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    private void onRoomMemberExited(String name) {
        memberList.remove(name);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                audienceNumView.setText(String.valueOf(memberList.size()));
                horizontalRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    /**
     * 发送礼物时显示的消息
     */
    public void sendPresentMessage(String gName, int resId) {
        gDialog.dismiss();
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.setReceipt(chatroomId);
        EMCmdMessageBody cmdMessageBody = new EMCmdMessageBody(Constant.CMD_GIFT);
        message.addBody(cmdMessageBody);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        message.setAttribute(I.User.NICK, EaseUserUtils.getCurrentAppUserInfo().getMUserNick());
        message.setAttribute(I.Gift.GIFT_NAME, gName);
        message.setAttribute(I.Gift.GIFT_RES_ID, resId);
        EMClient.getInstance().chatManager().sendMessage(message);
        showLeftGiftVeiw(message);
    }

    @OnClick(R.id.root_layout)
    void onRootLayoutClick() {
        periscopeLayout.addHeart();
    }

    @OnClick(R.id.comment_image)
    void onCommentImageClick() {
        showInputView();
    }

    @OnClick(R.id.present_image)
    void onPresentImageClick() {
        showGiftDetailsDialog();
    }

    @OnClick(R.id.chat_image)
    void onChatImageClick() {
        ConversationListFragment fragment = ConversationListFragment.newInstance(anchorId, false);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.message_container, fragment)
                .commit();
    }

    @OnClick(R.id.screenshot_image)
    void onScreenshotImageClick() {
        Bitmap bitmap = screenshot();
        if (bitmap != null) {
            ScreenshotDialog dialog = new ScreenshotDialog(this, bitmap);
            dialog.show();
        }

    }

    private Bitmap screenshot() {
        // 获取屏幕
        View dView = getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bmp = dView.getDrawingCache();
        return bmp;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class AvatarAdapter extends RecyclerView.Adapter<AvatarViewHolder> {
        List<String> namelist;
        Context context;
//        TestAvatarRepository avatarRepository;

        public AvatarAdapter(Context context, List<String> namelist) {
            this.namelist = namelist;
            this.context = context;
//            avatarRepository = new TestAvatarRepository();
        }

        @Override
        public AvatarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AvatarViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.avatar_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(AvatarViewHolder holder, final int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showUserDetailsDialog(namelist.get(position));
                }
            });
            //暂时使用测试数据
            Glide.with(context)
                    .load(EaseUserUtils.getAppUserInfo(namelist.get(position)).getAvatar())
                    .placeholder(R.drawable.ease_default_avatar)
                    .into(holder.Avatar);
        }

        @Override
        public int getItemCount() {
            return namelist.size();
        }
    }

    static class AvatarViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatar)
        ImageView Avatar;

        public AvatarViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
