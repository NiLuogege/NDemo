/*
* @Title:  ShareAdapter.java
* @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
* @data:  2014-7-21 ����2:30:32
* @version:  V1.0
*/

package cn.sharesdk.onekeyshare.themeCustom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.well.ndemo.R;


/**
 * TODO< ��������Adapter >
 *
 * @data: 2014-7-21 ����2:30:32
 * @version: V1.0
 */

public class ShareAdapter extends BaseAdapter {

    private static String[] shareNames = new String[]{"΢微信", "朋友圈", "΢QQ", "Twitter"};
    private int[] shareIcons = new int[]{R.drawable.ssdk_oks_classic_wechat, R.drawable.ssdk_oks_classic_wechatmoments, R.drawable.ssdk_oks_classic_qq, R.drawable.ssdk_oks_classic_twitter};
    private LayoutInflater inflater;

    public ShareAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return shareNames.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.share_item, null);
        }
        ImageView shareIcon = (ImageView) convertView.findViewById(R.id.share_icon);
        shareIcon.setImageResource(shareIcons[position]);

        return convertView;
    }
}
