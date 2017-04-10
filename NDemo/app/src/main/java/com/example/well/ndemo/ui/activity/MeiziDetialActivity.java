package com.example.well.ndemo.ui.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.webkit.WebView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.R;
import com.example.well.ndemo.ui.Interface.SucceedOrFaild;
import com.example.well.ndemo.utils.ColorUtils;
import com.example.well.ndemo.utils.GlideUtils;
import com.example.well.ndemo.utils.ImageUtils;
import com.example.well.ndemo.utils.MD5Utils;
import com.example.well.ndemo.utils.SnackbarUtils;
import com.example.well.ndemo.utils.ViewUtils;
import com.example.well.ndemo.view.ElasticDragDismissFrameLayout;
import com.example.well.ndemo.view.ParallaxScrimageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ${LuoChen} on 2017/3/7 9:40.
 * email:luochen0519@foxmail.com
 */

public class MeiziDetialActivity extends BaseActivity {
    @Bind(R.id.draggable_frame)
    ElasticDragDismissFrameLayout mDraggableFrame;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.shot)
    ParallaxScrimageView mShot;
    @Bind(R.id.webWv)
    WebView webWv;

    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;
    public static final String URL = "url";
    private static final float SCRIM_ADJUSTMENT = 0.075f;
    private Bitmap mBitmap;//显示的图片
    private String mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_meizidetial);
        ButterKnife.bind(this);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(this);//这个监听会随着控件的拖动设置statusBar的颜色

        initToolbar();

        webWv.loadUrl("file:///android_asset/test.html");

    }

    /**
     * 初始化Toolbar
     */
    private void initToolbar() {
        setSupportActionBar(mToolbar);//兼容低版本的actionBar
        mToolbar.setTitle(R.string.app_name);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(mOnClickListener);
//        mToolbar.inflateMenu(R.menu.menu_activity_meizidetial);
        mToolbar.setOnMenuItemClickListener(mOnMenuItemClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_meizidetial, menu);
        return true;
    }

    /**
     * 分享图片
     */
    private void shareImage() {

    }

    /**
     * 保存图片到本地
     */
    private void saveImage() {
        String md5String = MD5Utils.encryptMD5ToString(mUrl) + ".jpg";
        ImageUtils.saveImage(mBitmap, md5String,mSucceedOrFaild);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mUrl = intent.getStringExtra(URL);
            if (!TextUtils.isEmpty(mUrl)) {
                Glide
                        .with(context)
                        .load(mUrl)
                        .listener(mRequestListener)
                        .into(mShot);
            }
        }
    }

    private void initListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//如果系统版本大于21
            getWindow().getSharedElementEnterTransition().addListener(mTransitionListener);
        }

    }

    private RequestListener mRequestListener = new RequestListener<String, GlideDrawable>() {

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

            mBitmap = GlideUtils.getBitmap(resource);

            setStatusBarColor(mBitmap);

            setMshotColor(mBitmap);
            return false;
        }
    };

    /**
     * 设置状态栏的颜色和动画
     *
     * @param bitmap
     */
    private void setStatusBarColor(final Bitmap bitmap) {
        int dimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, MeiziDetialActivity.this.getResources().getDisplayMetrics());//dp转sp
        Palette.from(bitmap)
                .clearFilters()//清除过滤器
                .setRegion(0, 0, bitmap.getWidth() - 1, dimension)
                .generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        boolean isDark;
                        int lightness = ColorUtils.isDark(palette);//获取到主要颜色
                        if (lightness == ColorUtils.LIGHTNESS_UNKNOWN) {//说明不知道是不是暗色的
                            isDark = ColorUtils.isDark(bitmap, bitmap.getWidth() / 2, 0);
                        } else {
                            isDark = lightness == ColorUtils.IS_DARK;
                        }


                        /*这里设置了状态栏的颜色和动画*/
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            //获取到状态栏颜色
                            int statusBarColor = getWindow().getStatusBarColor();
                            Palette.Swatch topColor = ColorUtils.getMostPopulousSwatch(palette);
                            if (topColor != null &&
                                    (isDark || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                                statusBarColor = ColorUtils.scrimify(topColor.getRgb(),
                                        isDark, SCRIM_ADJUSTMENT);
                                // set a light status bar on M+
                                if (!isDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    ViewUtils.setLightStatusBar(mShot);
                                }
                            }
                            if (statusBarColor != getWindow().getStatusBarColor()) {
                                mShot.setScrimColor(statusBarColor);
                                ValueAnimator statusBarColorAnim = ValueAnimator.ofArgb(
                                        getWindow().getStatusBarColor(), statusBarColor);
                                statusBarColorAnim.addUpdateListener(new ValueAnimator
                                        .AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        getWindow().setStatusBarColor(
                                                (int) animation.getAnimatedValue());
                                    }
                                });
                                statusBarColorAnim.setDuration(1000L);
                                statusBarColorAnim.setInterpolator(
                                        new AccelerateInterpolator());
                                statusBarColorAnim.start();
                            }
                        }


                    }
                });
    }

    /**
     * 设置图片的前景色和背景色
     *
     * @param bitmap
     */
    private void setMshotColor(Bitmap bitmap) {
        Palette.from(bitmap)
                .clearFilters()
                .generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {

                        // slightly more opaque ripple on the pinned image to compensate
                        // for the scrim
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                            mShot.setForeground(ViewUtils.createRipple(palette, 0.3f, 0.6f,
                                    ContextCompat.getColor(MeiziDetialActivity.this, R.color.mid_grey),
                                    true));
                        }
                    }
                });

        // TODO should keep the background if the image contains transparency?!
        mShot.setBackground(null);
    }

    private Transition.TransitionListener mTransitionListener = new Transition.TransitionListener() {

        @Override
        public void onTransitionStart(Transition transition) {
            if (BuildConfig.DEBUG) Log.e("MeiziDetialActivity", "onTransitionStart");

            //mToolbar.animate().alpha(0).setDuration(5000).setInterpolator(new LinearOutSlowInInterpolator());

        }

        @Override
        public void onTransitionEnd(Transition transition) {

        }

        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        mDraggableFrame.addListener(chromeFader);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDraggableFrame.removeListener(chromeFader);
    }


    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MeiziDetialActivity.this.finish();
        }
    };

    Toolbar.OnMenuItemClickListener mOnMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int itemId = item.getItemId();
            switch (itemId) {
                case R.id.save:
                    saveImage();
                    break;
                case R.id.share:
                    shareImage();
                    break;
            }


            return true;
        }
    };

    SucceedOrFaild mSucceedOrFaild= new SucceedOrFaild() {

        @Override
        public void succeed() {
            SnackbarUtils.showDefaultShortSnackbar(mDraggableFrame,getString(R.string.shareSucceed));
        }

        @Override
        public void faild() {
            SnackbarUtils.showDefaultShortSnackbar(mDraggableFrame,getString(R.string.shareFaild));
        }
    };

}
