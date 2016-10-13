package com.loonggg.weekcalendar.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 适配器Adapter父类，继承它可简化适配器书写
 */
public abstract class SimpleBaseAdapter<T> extends BaseAdapter {
    protected Context mContext;
    /**
     * 数据列表
     */
    protected List data;

    protected SimpleBaseAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.data = data == null ? new ArrayList() : new ArrayList<T>(data);
    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (position >= data.size()) {
            return null;
        }
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 虚函数，继承此类的需要指定item的layout布局文件
     */
    public abstract int getItemResource();

    /**
     * 虚函数，继承此类的需要通过此函数绑定数据
     */
    public abstract View getItemView(int position, View convertView, ViewHolder holder);

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (null == view) {
            view = View.inflate(mContext, getItemResource(), null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            if (view.getTag() != null) {
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(mContext, getItemResource(), null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            }
        }
        return getItemView(position, view, holder);
    }

    /**
     * 添加elem中的所有数据
     *
     * @param elem list数据
     */
    public void addAll(List elem) {
        if (elem == null) {
            elem = new ArrayList();
        }
        data.addAll(elem);
        notifyDataSetChanged();
    }

    /**
     * 删除某个数据
     *
     * @param index 指定位置
     */
    public void remove(int index) {
        data.remove(index);
        notifyDataSetChanged();
    }


    /**
     * 替换所有数据
     *
     * @param elem list数据
     */
    public void replaceAll(List elem) {
        data.clear();
        if (elem != null) {
            data.addAll(elem);
        }
        notifyDataSetChanged();
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    /**
     * 更新所有数据
     */
    public void notifyDataChanged() {
        notifyDataSetChanged();
    }

    /**
     * 静态ViewHolder，优化加载数据
     */
    public class ViewHolder {
        private SparseArray<View> views = new SparseArray<View>();
        private View convertView;

        public ViewHolder(View convertView) {
            this.convertView = convertView;
        }

        public <T extends View> T getView(int resId) {
            View v = views.get(resId);
            if (null == v) {
                v = convertView.findViewById(resId);
                views.put(resId, v);
            }
            return (T) v;
        }
    }
}
