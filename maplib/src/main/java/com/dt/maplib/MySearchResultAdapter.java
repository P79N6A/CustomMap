package com.dt.maplib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.sug.SuggestionResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 包名： com.amap.searchdemo
 * <p>
 * 创建时间：2016/10/19
 * 项目名称：SearchDemo
 *
 * @author guibao.ggb
 * @email guibao.ggb@alibaba-inc.com
 * <p>
 * 类说明：
 */
public class MySearchResultAdapter extends BaseAdapter {

    private List<LatInfoBean> data;

    private Context context;


    private int selectedPosition = 0;

    public MySearchResultAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
    }

    public void setData(List<LatInfoBean> data) {
        this.data = data;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_holder_result, parent, false);

            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.bindView(position);

        return convertView;
    }


    class ViewHolder {
        TextView textTitle;
        TextView textSubTitle;


        public ViewHolder(View view) {
            textTitle = (TextView) view.findViewById(R.id.text_title);
            textSubTitle = (TextView) view.findViewById(R.id.text_title_sub);
        }

        public void bindView(int position) {
            if (position >= data.size())
                return;

            LatInfoBean poiItem = data.get(position);
//            if (position == 0){
//                textTitle.setTextColor(context.getResources().getColor(R.color.view_xieyi));
//                textSubTitle.setTextColor(context.getResources().getColor(R.color.black));
//            }
            textTitle.setText(poiItem.getName());

            textSubTitle.setText(poiItem.getDetails());


        }
    }
}
