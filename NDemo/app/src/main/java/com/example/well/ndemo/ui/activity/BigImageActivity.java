package com.example.well.ndemo.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.well.ndemo.R;
import com.example.well.ndemo.utils.ColorUtils;
import com.example.well.ndemo.utils.GlideUtils;
import com.example.well.ndemo.view.PinchImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ${LuoChen} on 2017/5/17 16:55.
 * email:luochen0519@foxmail.com
 */

public class BigImageActivity extends BaseActivity {
    @Bind(R.id.pIv)
    PinchImageView pIv;
    @Bind(R.id.ll_toot)
    LinearLayout ll_toot;

    public static final String EXTRA_URL = "extra_bitmap";
    private static final float SCRIM_ADJUSTMENT = 0.075f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        pIv.setOnClickListener(mOnClickListener);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra(EXTRA_URL);
            if (!TextUtils.isEmpty(url)) {
                Glide
                        .with(context)
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .listener(mRequestListener)
                        .into(pIv);
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
            Bitmap bitmap = GlideUtils.getBitmap(resource);
            setStatusBarColor(bitmap);
            return false;
        }
    };

    /**
     * 设置状态栏的颜色和动画
     *
     * @param bitmap
     */
    private void setStatusBarColor(final Bitmap bitmap) {
        int dimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, BigImageActivity.this.getResources().getDisplayMetrics());//dp转sp
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
                            Palette.Swatch topColor = ColorUtils.getMostPopulousSwatch(palette);
                            if (topColor != null && (isDark || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                                int scrimify = ColorUtils.scrimify(topColor.getRgb(), isDark, SCRIM_ADJUSTMENT);
                                ll_toot.setBackgroundColor(scrimify);
                            }
                        }
                    }
                });
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ActivityCompat.finishAfterTransition(BigImageActivity.this);
        }
    };

}
