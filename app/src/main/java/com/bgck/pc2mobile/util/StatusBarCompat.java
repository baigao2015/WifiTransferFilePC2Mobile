package com.bgck.pc2mobile.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 透明状态栏
 *
 * 新增另外一种 沉浸式状态栏：参考https://github.com/aqi00/note
 * 用武之地：
 *     1.可除去或新增顶部状态栏，如setStatusBarColor2里的removexx和addxx
 *     2.可增加顶部状态栏高度一自定义颜色的空布局
 * <span style="color:red;">
 *     注意：新增第二种用法如下：
 * </span>
 *     <pre>
 *         StatusBarCompat.fullScreen(this); //全屏(其实此处已经默认了状态栏全透明)
 *         StatusBarCompat.setStatusBarColor(this, Color.TRANSPARENT, true); // 状态栏透明
 *     </pre>
 *
 */
public class StatusBarCompat {

    private static final int COLOR_TRANSLUCENT = Color.parseColor("#00000000");

    public static final int DEFAULT_COLOR_ALPHA = 112;

    public static final String TAG_FAKE_STATUS_BAR_VIEW = "statusBarView";
    public static final String TAG_MARGIN_ADDED = "marginAdded";

    /**
     * set statusBarColor
     * @param statusColor color
     * @param alpha 0 - 255
     */
    public static void setStatusBarColor(Activity activity, int statusColor, int alpha) {
        setStatusBarColor(activity, calculateStatusBarColor(statusColor, alpha));
    }
    public static void setStatusBarColor(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //First translucent status bar.
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //After LOLLIPOP not translucent status bar
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //Then call setStatusBarColor.
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(statusColor);
                //set child View not fill the system window
                View mChildView = mContentView.getChildAt(0);
                if (mChildView != null) {
                    ViewCompat.setFitsSystemWindows(mChildView, true);
                }
            } else {
                ViewGroup mDecorView = (ViewGroup) window.getDecorView();
                if (mDecorView.getTag() != null && mDecorView.getTag() instanceof Boolean && (Boolean)mDecorView.getTag()) {
                    //if has add fake status bar view
                    View mStatusBarView = mDecorView.getChildAt(0);
                    if (mStatusBarView != null) {
                        mStatusBarView.setBackgroundColor(statusColor);
                    }
                } else {
                    int statusBarHeight = getStatusBarHeight(activity);
                    //add margin
                    View mContentChild = mContentView.getChildAt(0);
                    if (mContentChild != null) {
                        ViewCompat.setFitsSystemWindows(mContentChild, false);
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mContentChild.getLayoutParams();
                        lp.topMargin += statusBarHeight;
                        mContentChild.setLayoutParams(lp);
                    }
                    //add fake status bar view
                    View mStatusBarView = new View(activity);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
                    layoutParams.gravity = Gravity.TOP;
                    mStatusBarView.setLayoutParams(layoutParams);
                    mStatusBarView.setBackgroundColor(statusColor);
                    mDecorView.addView(mStatusBarView, 0);
                    mDecorView.setTag(true);
                }
            }
        }
    }

    public static void translucentStatusBar(Activity activity) {
        translucentStatusBar(activity, false);
    }

    /**
     * change to full screen mode
     * @param hideStatusBarBackground hide status bar alpha Background when SDK > 21, true if hide it
     */
    public static void translucentStatusBar(Activity activity, boolean hideStatusBarBackground) {
        Window window = activity.getWindow();
        ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);

        //set child View not fill the system window
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = getStatusBarHeight(activity);

            //First translucent status bar.
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //After LOLLIPOP just set LayoutParams.
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                if (hideStatusBarBackground) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setStatusBarColor(COLOR_TRANSLUCENT);
                } else {
                    window.setStatusBarColor(calculateStatusBarColor(COLOR_TRANSLUCENT, DEFAULT_COLOR_ALPHA));
                }
                //must call requestApplyInsets, otherwise it will have space in screen bottom
                if (mChildView != null) {
                    ViewCompat.requestApplyInsets(mChildView);
                }
            } else {
                ViewGroup mDecorView = (ViewGroup) window.getDecorView();
                if (mDecorView.getTag() != null && mDecorView.getTag() instanceof Boolean && (Boolean)mDecorView.getTag()) {
                    mChildView = mDecorView.getChildAt(0);
                    //remove fake status bar view.
                    mContentView.removeView(mChildView);
                    mChildView = mContentView.getChildAt(0);
                    if (mChildView != null) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mChildView.getLayoutParams();
                        //cancel the margin top
                        if (lp != null && lp.topMargin >= statusBarHeight) {
                            lp.topMargin -= statusBarHeight;
                            mChildView.setLayoutParams(lp);
                        }
                    }
                    mDecorView.setTag(false);
                }
            }
        }
    }

    //Get status bar height
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelOffset(resId);
        }
        return result;
    }

    //Get alpha color
    private static int calculateStatusBarColor(int color, int alpha) {
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    public static boolean setStatusBarDarkFont(Activity activity, boolean darkFont) {
        if (setMIUIStatusBarFont(activity, darkFont)) {
            setDefaultStatusBarFont(activity, darkFont);
            return true;
        } else if (setMeizuStatusBarFont(activity, darkFont)) {
            setDefaultStatusBarFont(activity, darkFont);
            return true;
        } else {
            return setDefaultStatusBarFont(activity, darkFont);
        }
    }

    private static boolean setMeizuStatusBarFont(Activity activity, boolean darkFont) {
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (darkFont) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            activity.getWindow().setAttributes(lp);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    private static boolean setMIUIStatusBarFont(Activity activity, boolean dark) {
        Window window = activity.getWindow();
        Class<?> clazz = window.getClass();
        try {
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if (dark) {
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag);
            }
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    private static boolean setDefaultStatusBarFont(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            View decorView = window.getDecorView();

            if (dark) {
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            return true;
        }
        return false;
    }

    /**<p>
     * Description: [把页面内容顶到状态栏内部，看起来状态栏就像是悬浮在页面之上]
     * <p>
     *
     * @param activity
     * @return
     *
     * Created by [CK] [2020/8/10 10:38]
     * Midified by [修改人] [修改时间]
     *
     * ${tags}
     */
    public static void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                // 两个标志位要结合使用，表示让应用的主体内容占用系统状态栏的空间
                // 第三个标志位可让底部导航栏变透明View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                // 底部导航栏也可以弄成透明的
                //int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
                //attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
            // 需要把状态栏颜色设置透明，这样才有悬浮的效果
            setStatusBarColor2(activity, Color.TRANSPARENT);
        }
    }

    /**<p>
     * Description: [重置状态栏。即把状态栏颜色恢复为系统默认的黑色]
     * <p>
     *
     * @param activity
     * @return
     *
     * Created by [CK] [2020/8/10 10:39]
     * Midified by [修改人] [修改时间]
     *
     * ${tags}
     */
    public static void resetStatusBar(Activity activity) {
        setStatusBarColor2(activity, Color.BLACK);
    }

    /**<p>
     * Description: [设置状态栏的背景色。对于Android4.4和Android5.0以上版本要区分处理]
     * <p>
     *
     * @param activity
     * @param color
     * @return
     *
     * Created by [CK] [2020/8/10 10:40]
     * Midified by [修改人] [修改时间]
     *
     * ${tags}
     */
    public static void setStatusBarColor2(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setStatusBarColor(color);
                // 底部导航栏颜色也可以由系统设置
                //activity.getWindow().setNavigationBarColor(color);
            } else {
                setKitKatStatusBarColor(activity, color);
            }
            if (color == Color.TRANSPARENT || color == Color.DKGRAY) { // 透明或深灰背景表示要悬浮状态栏
                removeMarginTop(activity);
            } else { // 其它背景表示要恢复状态栏
                addMarginTop(activity);
            }
        }
    }

    /**<p>
     * Description: [设置状态栏的背景色。对于Android4.4和Android5.0以上版本要区分处理]
     * <p>
     *
     * @param activity
     * @param color
     * @param isRemoveMarginTopFlag
     * @return
     *
     * Created by [CK] [2020/8/10 10:41]
     * Midified by [修改人] [修改时间]
     *
     * ${tags}
     */
    public static void setStatusBarColor2(Activity activity, int color, boolean isRemoveMarginTopFlag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setStatusBarColor(color);
                // 底部导航栏颜色也可以由系统设置
                //activity.getWindow().setNavigationBarColor(color);
            } else {
                setKitKatStatusBarColor(activity, color);
            }
            if (color == Color.TRANSPARENT || color == Color.DKGRAY || isRemoveMarginTopFlag) { // 透明或深灰背景表示要悬浮状态栏
                removeMarginTop(activity);
            } else { // 其它背景表示要恢复状态栏
                addMarginTop(activity);
            }
        }
    }

    /**<p>
     * Description: [添加顶部间隔，留出状态栏的位置]
     * <p>
     *
     * @param activity
     * @return
     *
     * Created by [CK] [2020/8/10 10:41]
     * Midified by [修改人] [修改时间]
     *
     * ${tags}
     */
    private static void addMarginTop(Activity activity) {
        Window window = activity.getWindow();
        ViewGroup contentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View child = contentView.getChildAt(0);
        if (null != child && null != child.getTag() && !TAG_MARGIN_ADDED.equals(child.getTag())) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) child.getLayoutParams();
            // 添加的间隔大小就是状态栏的高度
            params.topMargin += getStatusBarHeight(activity);
            child.setLayoutParams(params);
            child.setTag(TAG_MARGIN_ADDED);
        }
    }

    // 移除顶部间隔，霸占状态栏的位置
    private static void removeMarginTop(Activity activity) {
        Window window = activity.getWindow();
        ViewGroup contentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View child = contentView.getChildAt(0);
        if (null != child && null != child.getTag() && TAG_MARGIN_ADDED.equals(child.getTag())) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) child.getLayoutParams();
            // 移除的间隔大小就是状态栏的高度
            params.topMargin -= getStatusBarHeight(activity);
            child.setLayoutParams(params);
            child.setTag(null);
        }
    }

    /**<p>
     * Description: [对于Android4.4，系统没有提供设置状态栏颜色的方法，只能手工搞个假冒的状态栏来占坑]
     * <p>
     *
     * @param activity
     * @param statusBarColor
     * @return
     *
     * Created by [CK] [2020/8/10 10:41]
     * Midified by [修改人] [修改时间]
     *
     * ${tags}
     */
    private static void setKitKatStatusBarColor(Activity activity, int statusBarColor) {
        Window window = activity.getWindow();
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        // 先移除已有的冒牌状态栏
        View fakeView = decorView.findViewWithTag(TAG_FAKE_STATUS_BAR_VIEW);
        if (fakeView != null) {
            decorView.removeView(fakeView);
        }
        // 再添加新来的冒牌状态栏
        View statusBarView = new View(activity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(statusBarColor);
        statusBarView.setTag(TAG_FAKE_STATUS_BAR_VIEW);
        decorView.addView(statusBarView);
    }


    /**<p>
     * Description: [为布局文件中新增的状态栏布局设置背景色和高度]
     * <p>
     *
     * @param activity
     * @param view
     * @param color
     * @return
     *
     * Created by [CK] [2020/8/10 10:41]
     * Midified by [修改人] [修改时间]
     *
     * ${tags}
     */
    public static void setStatusViewAttr(Activity activity, View view, @ColorInt int color) {
        if (view == null || activity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = getStatusBarHeight(activity);
            view.setLayoutParams(layoutParams);

            view.setBackgroundColor(color);
        }
    }

    /**<p>
     * Description: [无需提交背景颜色]
     * <p>
     *
     * @param activity
     * @param view
     * @return
     *
     * Created by [CK] [2021/2/9 11:16]
     * Midified by [修改人] [修改时间]
     *
     * ${tags}
     */
    public static void setStatusViewAttr(Activity activity, View view) {
        if (view == null || activity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = getStatusBarHeight(activity);
            view.setLayoutParams(layoutParams);
        }
    }
}