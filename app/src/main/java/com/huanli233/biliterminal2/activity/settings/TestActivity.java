package com.huanli233.biliterminal2.activity.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.huanli233.biliterminal2.R;
import com.huanli233.biliterminal2.activity.base.BaseActivity;
import com.huanli233.biliterminal2.activity.settings.login.SpecialLoginActivity;
import com.huanli233.biliterminal2.activity.video.local.DownloadListActivity;
import com.huanli233.biliterminal2.api.ConfInfoApi;
import com.huanli233.biliterminal2.service.DownloadService;
import com.huanli233.biliterminal2.util.ThreadManager;
import com.huanli233.biliterminal2.util.MsgUtil;
import com.huanli233.biliterminal2.util.network.NetWorkUtil;

import org.json.JSONArray;

import java.util.Objects;

public class TestActivity extends BaseActivity {

    SwitchMaterial sw_wbi, sw_post;
    EditText input_link, input_data, output;
    MaterialCardView btn_crash, btn_request, btn_cookies, btn_start, btn_download, btn_download_goto, btn_download_clear;

    JSONArray conversation;

    @SuppressLint({"MutatingSharedPrefs", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        sw_wbi = findViewById(R.id.switch_wbi);
        sw_post = findViewById(R.id.switch_post);
        input_link = findViewById(R.id.input_link);
        input_data = findViewById(R.id.input_data);
        output = findViewById(R.id.output_json);
        btn_start = findViewById(R.id.start);
        btn_download = findViewById(R.id.download);
        btn_download_goto = findViewById(R.id.download_goto);
        btn_download_clear = findViewById(R.id.download_clear);

        sw_post.setOnCheckedChangeListener((compoundButton, checked) ->
                input_data.setVisibility(checked ? View.VISIBLE : View.GONE));

        btn_request = findViewById(R.id.request);

        btn_request.setOnClickListener(view -> ThreadManager.run(() -> {
            try {
                String url = input_link.getText().toString();
                if (!url.startsWith("https://") && !url.startsWith("http://"))
                    url = "https://" + url;

                if (sw_wbi.isChecked()) url = ConfInfoApi.signWBI(url);

                runOnUiThread(() -> {
                    output.setText("");
                    MsgUtil.showMsg("发出请求！");
                });
                String result;
                if (sw_post.isChecked()) {
                    String data = input_data.getText().toString();
                    result = Objects.requireNonNull(NetWorkUtil.post(url, data).body()).string();
                } else {
                    result = Objects.requireNonNull(NetWorkUtil.get(url).body()).string();
                }

                runOnUiThread(() -> {
                    output.setText(result);
                    MsgUtil.showMsg("请求成功！");
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    output.setText(e.toString());
                    MsgUtil.showMsg("请求失败！");
                });
                e.printStackTrace();
            }
        }));

        btn_cookies = findViewById(R.id.cookies);
        btn_cookies.setOnClickListener(view -> {
            Intent intent = new Intent(this, SpecialLoginActivity.class);
            intent.putExtra("login", false);
            startActivity(intent);
        });

        btn_start.setOnClickListener(v -> startService(new Intent(this, DownloadService.class)));

        btn_download.setOnClickListener(v -> {
            DownloadService.startDownload("雀魂", "早春赏樱", 501590258L, 294292444L,
                    "https://comment.bilibili.com/294292444.xml",
                    "http://i1.hdslb.com/bfs/archive/321b2291b55f1effc0f0646f593cf47b78ea0e9b.png", 16);

            DownloadService.startDownload("雀魂", "曲水流觞", 501590258L, 294370880L,
                    "https://comment.bilibili.com/294370880.xml",
                    "http://i1.hdslb.com/bfs/archive/321b2291b55f1effc0f0646f593cf47b78ea0e9b.png", 16);

            DownloadService.startDownload("雀魂", "锦绣梦", 501590258L, 493168287L,
                    "https://comment.bilibili.com/493168287.xml",
                    "http://i1.hdslb.com/bfs/archive/321b2291b55f1effc0f0646f593cf47b78ea0e9b.png", 16);


            //startService(new Intent(TestActivity.this,DownloadService.class));
        });

        btn_download_goto.setOnClickListener(view -> {
            Intent intent = new Intent(this, DownloadListActivity.class);
            startActivity(intent);
        });

        btn_download_clear.setOnClickListener(v -> DownloadService.clear());
    }
}