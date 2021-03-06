package com.example.well.ndemo.ui.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.R;
import com.example.well.ndemo.ui.Interface.SucceedOrFaildListener;
import com.example.well.ndemo.utils.ColorUtils;
import com.example.well.ndemo.utils.GlideUtils;
import com.example.well.ndemo.utils.ImageUtils;
import com.example.well.ndemo.utils.MD5Utils;
import com.example.well.ndemo.utils.SettingsUtils;
import com.example.well.ndemo.utils.SnackbarUtils;
import com.example.well.ndemo.utils.ViewUtils;
import com.example.well.ndemo.view.ElasticDragDismissFrameLayout;
import com.example.well.ndemo.view.ParallaxScrimageView;

import org.sufficientlysecure.htmltextview.HtmlResImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.HashMap;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.themeCustom.ShareModel;
import cn.sharesdk.onekeyshare.themeCustom.SharePopupWindow;

/**
 * Created by ${LuoChen} on 2017/3/7 9:40.
 * email:luochen0519@foxmail.com
 */

public class MeiziDetialActivity extends BaseActivity implements Handler.Callback {
    @Bind(R.id.draggable_frame)
    ElasticDragDismissFrameLayout mDraggableFrame;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.shot)
    ParallaxScrimageView mShot;
    @Bind(R.id.hTv)
    HtmlTextView hTv;
    @Bind(R.id.nest)
    NestedScrollView nest;

    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;
    public static final String URL = "url";
    private static final float SCRIM_ADJUSTMENT = 0.075f;
    private Bitmap mBitmap;//显示的图片
    private String mUrl;
    private int mStatusBarColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_meizidetial);
        ButterKnife.bind(this);
        initView();
        initListener();
        initData();
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_meizidetial, menu);
        return true;
    }

    private void initView() {
        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(this);//这个监听会随着控件的拖动设置statusBar的颜色

        initToolbar();

        hTv.setHtml(getString(R.string.news), new HtmlResImageGetter(hTv));
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mUrl = intent.getStringExtra(URL);
            if (!TextUtils.isEmpty(mUrl)) {
                Glide
                        .with(context)
                        .load(mUrl)
                        .skipMemoryCache(false)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
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

    private void getPermission() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        requestPermission(permissions, new PermissionHandler() {
            @Override
            public void onGranted() {
                saveImage();
            }
        });
    }

    /**
     * 分享图片
     */
    private void shareImage() {
        showShare();
    }

    /**
     * 保图片到本地
     */
    private void saveImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                String name = Thread.currentThread().getName();
                String md5String = MD5Utils.encryptMD5ToString(mUrl) + ".jpg";
                ImageUtils.saveImage(mBitmap, md5String, mSucceedOrFaild);
            }
        }).start();
    }

    /**
     * 保存整个页面到本地
     */
    private void saveLongImage() {
        String s = UUID.randomUUID().toString();
        String fileName = s + ".jpg";
        if (BuildConfig.DEBUG) Log.e("MeiziDetialActivity", fileName);
        ImageUtils.saveNestedScrollView(nest, fileName, mSucceedOrFaild);
    }


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
                            mStatusBarColor = getWindow().getStatusBarColor();
                            Palette.Swatch topColor = ColorUtils.getMostPopulousSwatch(palette);
                            if (topColor != null &&
                                    (isDark || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                                mStatusBarColor = ColorUtils.scrimify(topColor.getRgb(),
                                        isDark, SCRIM_ADJUSTMENT);
                                // set a light status bar on M+
                                if (!isDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    ViewUtils.setLightStatusBar(mShot);
                                }
                            }
                            if (mStatusBarColor != getWindow().getStatusBarColor()) {
                                mShot.setScrimColor(mStatusBarColor);
                                ValueAnimator statusBarColorAnim = ValueAnimator.ofArgb(
                                        getWindow().getStatusBarColor(), mStatusBarColor);
                                statusBarColorAnim.addUpdateListener(new ValueAnimator
                                        .AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        getWindow().setStatusBarColor(
                                                (int) animation.getAnimatedValue());
                                    }
                                });
                                statusBarColorAnim.setDuration(600L);
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

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    private void showShare() {
        initShareSDK();

        SharePopupWindow sharePopupWindow = new SharePopupWindow(context);
        sharePopupWindow.setPlatformActionListener(mPlatformActionListener);
        sharePopupWindow.showShareWindow(mStatusBarColor);
        ShareModel shareModel = new ShareModel();
        shareModel.setImageUrl(mUrl);
        sharePopupWindow.initShareParams(shareModel);
        sharePopupWindow.showAtLocation(mDraggableFrame, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @OnClick(R.id.shot)
    void shot() {
        Intent intent = new Intent(context, BigImageActivity.class);
        intent.putExtra(BigImageActivity.EXTRA_URL, mUrl);
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(MeiziDetialActivity.this, mShot, getString(R.string.share_big_image_activity));
        Bundle bundle = compat.toBundle();
        ActivityCompat.startActivity(context, intent, bundle);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityCompat.finishAfterTransition(MeiziDetialActivity.this);
            overridePendingTransition(R.anim.share_translate_prodetail_in, R.anim.share_translate_prodetail_out_down);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SettingsUtils.PREMIERE_REQUEST_CODE_STORAGE) {//外部内存的权限
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//用户同意使用内存权限
                if (BuildConfig.DEBUG)
                    Log.e("MeiziDetialActivity", "onRequestPermissionsResult_true");
            } else {//用户不同意使用内存权限
                if (BuildConfig.DEBUG)
                    Log.e("MeiziDetialActivity", "onRequestPermissionsResult_false");
            }
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

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ActivityCompat.finishAfterTransition(MeiziDetialActivity.this);
            overridePendingTransition(R.anim.share_translate_prodetail_in, R.anim.share_translate_prodetail_out_down);
        }
    };

    Toolbar.OnMenuItemClickListener mOnMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int itemId = item.getItemId();
            switch (itemId) {
                case R.id.save:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getPermission();
                    } else {
                        saveImage();
                    }
                    break;
                case R.id.share:
                    shareImage();
                    break;
                case R.id.saveLongImage:
                    saveLongImage();
                    break;
            }


            return true;
        }
    };


    SucceedOrFaildListener mSucceedOrFaild = new SucceedOrFaildListener() {

        @Override
        public void succeed() {
            SnackbarUtils.showDefaultShortSnackbar(mDraggableFrame, getString(R.string.shareSucceed));
        }

        @Override
        public void faild() {
            SnackbarUtils.showDefaultShortSnackbar(mDraggableFrame, getString(R.string.shareFaild));
        }
    };

    PlatformActionListener mPlatformActionListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            SnackbarUtils.showDefaultLongSnackbar(mDraggableFrame, "分享成功");
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            SnackbarUtils.showDefaultLongSnackbar(mDraggableFrame, "分享失败");
        }

        @Override
        public void onCancel(Platform platform, int i) {
            SnackbarUtils.showDefaultLongSnackbar(mDraggableFrame, "分享已取消");
        }
    };


}
