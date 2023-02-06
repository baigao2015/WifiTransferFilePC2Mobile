/*
 * Copyright (c) 2019.
 * 贵州日报当代融媒体集团有限责任公司 2019-2023 版权所有.
 * 本软件为贵州日报当代融媒体集团的保密专有信息。您不得透露此类机密信息,
 * 应仅按照您与贵州日报当代融媒体集团签订的许可协议条款使用.
 */
package com.bgck.pc2mobile.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.bgck.pc2mobile.R;
import com.bgck.pc2mobile.wegit.AlignTextView;
import com.bgck.pc2mobile.wegit.XRTextView;

/**
 
 * @author bgck
 * @author (lastest modification by 修改人)
 * @version 版本号_ 2023/2/3 9:24
 * @since 2022302
 */
public class SearchKeyWordActivity extends AppCompatActivity {

    private XRTextView xrtextview = null;
    private AlignTextView textview = null;
    private String content = "abcdefgABCDE李炳军F我要你lfwjkdfl;skjf asljkflskjfls;kjfsljfwfisdlfjsllkjsdfjlskjf546132s1f3sd4f31s3dffslfksjdfljlsadkjflsajdf sdfjklsajdflsa;jdfls 的!@#$%^&*()_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_tv);
        xrtextview = (XRTextView) this.findViewById(R.id.mytextview_tv);
        xrtextview.setText(content);
        textview = (AlignTextView) this.findViewById(R.id.mytextview_tv1);
        textview.setText(content);
    }

}
