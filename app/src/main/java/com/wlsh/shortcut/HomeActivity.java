package com.wlsh.shortcut;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wlsh.shortcut.badge.BadgeUtil;

public class HomeActivity extends AppCompatActivity {

    private EditText edt_num;
    private Button btn_set_badge;
    private Button btn_clear_badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        edt_num = findViewById(R.id.edt_num);
        btn_set_badge = findViewById(R.id.btn_set_badge);
        btn_clear_badge = findViewById(R.id.btn_clear_badge);

        //设置角标
        btn_set_badge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = edt_num.getText().toString().trim();
                if (!TextUtils.isEmpty(num)) {
                    BadgeUtil.setBadgeCount(HomeActivity.this, Integer.parseInt(num));
                }
            }
        });

        //清空角标
        btn_clear_badge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_num.setText("");
                BadgeUtil.cleanBadgeCount(HomeActivity.this);
            }
        });
    }
}
