package cn.sharesdk.onekeyshare.themeCustom;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.R;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;


/**
 * TODO<??????>
 *
 * @data: 2014-7-21 ????2:45:38
 * @version: V1.0
 */

public class SharePopupWindow extends PopupWindow {

    private static final String SHARE_TITLE = "美女图片";
    private static final String SHARE_TEXT = "我是分享文字";
    private Context context;
    private PlatformActionListener platformActionListener;
    private ShareParams shareParams;

    public SharePopupWindow(Context cx) {
        this.context = cx;
    }


    public void showShareWindow(int statusBarColor) {
        View view = LayoutInflater.from(context).inflate(R.layout.share_layout, null);
        RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.rl);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePopupWindow.this.dismiss();
            }
        });
        GridView gridView = (GridView) view.findViewById(R.id.share_gridview);
        ShareAdapter adapter = new ShareAdapter(context);
        gridView.setAdapter(adapter);
        gridView.setBackgroundColor(statusBarColor);
        //设置SelectPicPopupWindow的View
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow点击其他地方消失
        this.setOutsideTouchable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
         this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x99000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        gridView.setOnItemClickListener(new ShareItemClickListener(this));

    }

    public PlatformActionListener getPlatformActionListener() {
        return platformActionListener;
    }

    public void setPlatformActionListener(
            PlatformActionListener platformActionListener) {
        this.platformActionListener = platformActionListener;
    }

    private class ShareItemClickListener implements OnItemClickListener {
        private PopupWindow pop;

        public ShareItemClickListener(PopupWindow pop) {
            this.pop = pop;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            share(position);
            pop.dismiss();

        }
    }

    /**
     * ????
     *
     * @param position
     */
    private void share(int position) {

        if (position == 0) {//微信
            share_Weixin();
        } else if (position == 1) {//朋友圈
            share_CircleFriend();
        } else if (position == 2) {//QQ
            share_QQ();
        } else if (position == 3) {//Twitter
            share_Twitter();
        } else {
            if (BuildConfig.DEBUG) Log.e("SharePopupWindow", "position");
        }
    }


    /**
     * ????????????
     *
     * @param shareModel
     */
    public void initShareParams(ShareModel shareModel) {
        if (shareModel != null) {
            ShareParams sp = new ShareParams();
            sp.setShareType(Platform.SHARE_TEXT);
            sp.setShareType(Platform.SHARE_WEBPAGE);

            sp.setTitle(shareModel.getText());
            sp.setText(shareModel.getText());
            sp.setUrl(shareModel.getUrl());
            sp.setImageUrl(shareModel.getImageUrl());
            shareParams = sp;
        }
    }

    /**
     * @param position
     * @return
     */
    private String getPlatform(int position) {
        String platformName = "";
        switch (position) {
            case 0:
                platformName = Wechat.NAME;
                break;
            case 1:
                platformName = WechatMoments.NAME;
                break;
            case 2:
                platformName = QQ.NAME;
                break;
            case 3:
                platformName = Twitter.NAME;
                break;
        }
        return platformName;
    }

    /**
     * 微信
     */
    private void share_Weixin() {
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        Wechat.ShareParams sp = new Wechat.ShareParams();
        sp.setShareType(Platform.SHARE_IMAGE);
        sp.setTitle(SHARE_TITLE);
        sp.setText(SHARE_TEXT);
        sp.setImageUrl(shareParams.getImageUrl());
        wechat.setPlatformActionListener(platformActionListener); // 设置分享事件回调
        wechat.share(sp);
    }

    /**
     * 微信朋友圈
     */
    private void share_CircleFriend() {
        Platform circleFriend = ShareSDK.getPlatform(WechatMoments.NAME);
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.setShareType(Platform.SHARE_IMAGE);// 一定要设置分享属性
        sp.setTitle(SHARE_TITLE);
        sp.setText(SHARE_TEXT);
        sp.setImageUrl(shareParams.getImageUrl());
        sp.setImagePath(null);
        sp.setUrl(shareParams.getUrl());

        circleFriend.setPlatformActionListener(platformActionListener); // 设置分享事件回调
        // 执行图文分享
        circleFriend.share(sp);
    }

    /**
     * QQ
     */
    private void share_QQ() {
        ShareParams sp = new ShareParams();
        sp.setTitle(SHARE_TITLE);
        sp.setText(SHARE_TEXT);
        sp.setImageUrl(shareParams.getImageUrl());
        Platform qq = ShareSDK.getPlatform(context, "QQ");
        qq.setPlatformActionListener(platformActionListener);
        qq.share(sp);
    }

    /**
     * Twitter
     */
    private void share_Twitter() {
        ShareParams sp = new ShareParams();
        sp.setTitle(SHARE_TITLE);
        sp.setText(SHARE_TEXT);
        sp.setImageUrl(shareParams.getImageUrl());
        Platform twitrer = ShareSDK.getPlatform(Twitter.NAME);
        twitrer.setPlatformActionListener(platformActionListener);
        twitrer.share(sp);
    }


    /**
     * QQ空间
     */
    private void qzone() {
        ShareParams sp = new ShareParams();
        sp.setTitle(shareParams.getTitle());
        sp.setTitleUrl(shareParams.getUrl()); // ??????????
        sp.setText(shareParams.getText());
        sp.setImageUrl(shareParams.getImageUrl());
        sp.setComment("????????????????");
        sp.setSite(shareParams.getTitle());
        sp.setSiteUrl(shareParams.getUrl());

        Platform qzone = ShareSDK.getPlatform(context, "QZone");

        qzone.setPlatformActionListener(platformActionListener); // ???÷????????? //
        qzone.share(sp);
    }


    /**
     * ????????
     */
    private void shortMessage() {
        ShareParams sp = new ShareParams();
        sp.setAddress("");
        sp.setText(shareParams.getText() + "?????????" + shareParams.getUrl() + "??????????");

        Platform circle = ShareSDK.getPlatform(context, "ShortMessage");
        circle.setPlatformActionListener(platformActionListener); // ???÷?????????
        // ?????????
        circle.share(sp);
    }


}
