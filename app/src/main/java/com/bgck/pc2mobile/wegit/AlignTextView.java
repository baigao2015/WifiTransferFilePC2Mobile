/*
 * Copyright (c) 2019.
 * 贵州日报当代融媒体集团有限责任公司 2019-2023 版权所有.
 * 本软件为贵州日报当代融媒体集团的保密专有信息。您不得透露此类机密信息,
 * 应仅按照您与贵州日报当代融媒体集团签订的许可协议条款使用.
 */
package com.bgck.pc2mobile.wegit;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ImageSpan;
import android.util.AttributeSet;

import com.bgck.pc2mobile.R;


/**
 * @version 版本号_ 2023/1/19 17:43
 * @since 2022302
 */
public class AlignTextView extends AppCompatTextView {
    private final String TAG = AlignTextView.class.getSimpleName();

    private boolean alignOnlyOneLine;

    public AlignTextView(Context context) {
        this(context, null);
    }

    public AlignTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlignTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AlignTextView);
        alignOnlyOneLine = typedArray.getBoolean(R.styleable.AlignTextView_alignOnlyOneLine, false);
        ColorStateList colorStateList = typedArray.getColorStateList(R.styleable.AlignTextView_android_textColor);
        setTextColor(colorStateList==null?ColorStateList.valueOf(Color.GRAY):colorStateList);
        typedArray.recycle();
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        getPaint().setColor(color);
    }

    @Override
    public void setTextColor(ColorStateList colors) {
        super.setTextColor(colors);
        getPaint().setColor(colors.getDefaultColor());
    }

    protected void onDraw(Canvas canvas) {
        CharSequence content = TextUtils.highlight(getText(), "李炳军");
        if (content instanceof String){
            String text = (String) content;

            Layout layout = getLayout();

            for (int i = 0; i < layout.getLineCount(); ++i) {
                int lineBaseline = layout.getLineBaseline(i) + getPaddingTop();
                int lineStart = layout.getLineStart(i);
                int lineEnd = layout.getLineEnd(i);
                if (alignOnlyOneLine && layout.getLineCount() == 1) {//只有一行
                    String line = text.substring(lineStart, lineEnd);
                    float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, getPaint());
                    this.drawScaledText(canvas, line, lineBaseline, width);
                } else if (i == layout.getLineCount() - 1) {//最后一行
                    canvas.drawText(text.substring(lineStart), getPaddingLeft(), lineBaseline, getPaint());
                    break;
                } else {//中间行
                    String line = text.substring(lineStart, lineEnd);
                    float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, getPaint());
                    this.drawScaledText(canvas, line, lineBaseline, width);
                }
            }

        }else if (content instanceof Spanned){
            Spanned text = (Spanned) content;

            Layout layout = getLayout();

            for (int i = 0; i < layout.getLineCount(); ++i) {
                int lineBaseline = layout.getLineBaseline(i) + getPaddingTop();
                int lineStart = layout.getLineStart(i);
                int lineEnd = layout.getLineEnd(i);
                ImageSpan[] imageSpans = text.getSpans(lineStart, lineEnd, ImageSpan.class);
                if (imageSpans.length > 0){//只适用于一张图片一行
                    imageSpans[0].draw(canvas,text,lineStart,lineEnd,getPaddingLeft(),0,0,layout.getLineBaseline(i),getSpannedPaint(text,lineStart));

                }else if (alignOnlyOneLine && layout.getLineCount() == 1) {//只有一行
                    float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, getPaint());
                    this.drawScaledText(canvas, text, lineStart, lineEnd, lineBaseline, width, true);

                } else if (i == layout.getLineCount() - 1) {//最后一行
                    //canvas.drawText(text, lineStart, text.length(), getPaddingLeft(), lineBaseline, getPaint());
                    float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, getPaint());
                    this.drawScaledText(canvas, text, lineStart, lineEnd, lineBaseline, width,false);
                    break;

                } else {//中间行
                    float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, getPaint());
                    boolean forceNextLine = text.charAt(lineEnd-1) == 10;
                    if (forceNextLine){
                        this.drawScaledText(canvas, text, lineStart, lineEnd, lineBaseline, width,false);
                    }else {
                        this.drawScaledText(canvas, text, lineStart, lineEnd, lineBaseline, width, true);
                    }

                }
            }

        }else {
            super.onDraw(canvas);
        }
    }

    private void drawScaledText(Canvas canvas, String line, float baseLineY, float lineWidth) {
        if (line.length() < 1) {
            return;
        }
        float x = getPaddingLeft();
        boolean forceNextLine = line.charAt(line.length() - 1) == 10;
        int length = line.length() - 1;
        if (forceNextLine || length == 0) {
            canvas.drawText(line, x, baseLineY, getPaint());
            return;
        }

        float d = (getMeasuredWidth() - lineWidth - getPaddingLeft() - getPaddingRight()) / length;

        for (int i = 0; i < line.length(); ++i) {
            String c = String.valueOf(line.charAt(i));
            float cw = StaticLayout.getDesiredWidth(c, getPaint());
            canvas.drawText(c, x, baseLineY, this.getPaint());
            x += cw + d;
        }
    }

    private void drawScaledText(Canvas canvas, Spanned text, int start, int end, float baseLineY, float lineWidth, boolean isAlign) {
        if ((end - start) < 0) {
            return;
        }

        float x = getPaddingLeft();
        int length = end - start;
        if (length == 0){
            canvas.drawText(text, start, end, x, baseLineY, getSpannedPaint(text,start));
            return;
        }

        float d;//每个字符间需要添加的间隔
        if (isAlign){
            d = (getMeasuredWidth() - lineWidth - getPaddingLeft() - getPaddingRight()) / length;
        }else {
            d = 0;
        }

        for (int i = 0; i < length; ++i) {
            float cw = StaticLayout.getDesiredWidth(text,start + i,start + i+1, getSpannedPaint(text,start + i));
            canvas.drawText(text, start + i,start + i + 1, x, baseLineY, getSpannedPaint(text,start + i));
            x += cw + d;
        }
    }

    /**
     * 获取单个字符的式样
     * @param text Spanned
     * @param index 字符的索引
     * @return TextPaint
     */
    private TextPaint getSpannedPaint(Spanned text, int index){
        TextPaint textPaint = new TextPaint();
        textPaint.set(getPaint());

        CharacterStyle[] characterSpans = text.getSpans(index, index+1, CharacterStyle.class);
        if (characterSpans.length > 0){
            for (CharacterStyle span:characterSpans){
                span.updateDrawState(textPaint);
            }
        }

        return textPaint;
    }
}

