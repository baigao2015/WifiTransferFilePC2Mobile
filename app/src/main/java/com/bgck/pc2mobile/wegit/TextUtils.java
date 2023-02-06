/*
 * Copyright (c) 2019.
 * 贵州日报当代融媒体集团有限责任公司 2019-2023 版权所有.
 * 本软件为贵州日报当代融媒体集团的保密专有信息。您不得透露此类机密信息,
 * 应仅按照您与贵州日报当代融媒体集团签订的许可协议条款使用.
 */
package com.bgck.pc2mobile.wegit;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @version 版本号_ 2023/2/3 8:57
 * @since 2022302
 */
public class TextUtils {

//    public static boolean hashKeyWord() {
//        return StringUtils.isEmpty((String)SPUtils.get(mContext, "TextViewSupportHighlight_KEY", ""));
//    }

    /**
     * // 调用
     * 	// SpannableStringBuilder textString = TextUtilTools.highlight(item.getItemName(), KnowledgeActivity.searchKey);
     * 关键字高亮显示
     *
     * @param targets  须要高亮的关键字
     * @param text	     须要显示的文字
     * @return spannable 处理完后的结果，记得不要toString()，不然没有效果
     */
    public static SpannableStringBuilder highlight(CharSequence text, String[] targets, @ColorInt int color) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span = null;

        if (null == targets || targets.length < 1) {
            return spannable;
        }
        for (int keyIndex = 0; keyIndex < targets.length; keyIndex++) {
            Pattern p = Pattern.compile(targets[keyIndex]);
            Matcher m = p.matcher(text);
            while (m.find()) {
                span = new ForegroundColorSpan(color);// 须要重复！
                spannable.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannable;
    }
    /**
     * // 调用
     * 	// SpannableStringBuilder textString = TextUtilTools.highlight(item.getItemName(), KnowledgeActivity.searchKey);
     * 关键字高亮显示
     *
     * @param target  须要高亮的关键字
     * @param text	     须要显示的文字
     * @return spannable 处理完后的结果，记得不要toString()，不然没有效果
     */
    public static SpannableStringBuilder highlight(CharSequence text, String target, @ColorInt int color) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span = null;

        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(text);
        while (m.find()) {
            span = new ForegroundColorSpan(color);// 须要重复！
            spannable.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    /**
     * 关键字高亮显示
     *
     * @param target  须要高亮的关键字
     * @param text	     须要显示的文字
     * @return spannable 处理完后的结果，记得不要toString()，不然没有效果
     */
    public static SpannableStringBuilder highlight(CharSequence text, String target) {

        return highlight(text, target, Color.RED);
    }
}
