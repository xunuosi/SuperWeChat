package cn.ucai.superwechat.live.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.ucai.superwechat.R;


/**
 * Created by wei on 2016/7/27.
 */
public class ChatActivity extends BaseActivity{
  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);

    String username = getIntent().getStringExtra("username");

    getSupportFragmentManager().beginTransaction().add(R.id.root, ChatFragment.newInstance(username, true)).commit();
  }
}
