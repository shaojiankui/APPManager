package org.skyfox.appmanager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivityAdapter extends BaseAdapter  implements Filterable {
    private Context context;
    public List  <AppModel> list;
    private List <AppModel>  backData;
    MyFilter mFilter ;
    public MainActivityAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
    }
    public void setDataSource(List list) {
        // TODO Auto-generated method stub
        this.list = list;
        this.backData =  list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (this.list !=null){
            return this.list.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return this.list.indexOf(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_main_list_cell, null);
        } else {
            convertView = convertView;
        }

        ImageView iconImageView = (ImageView)convertView.findViewById(R.id.iconImageView);

        TextView nameTextView = (TextView)convertView.findViewById(R.id.nameTextView);
        TextView packageTextView = (TextView)convertView.findViewById(R.id.packageTextView);
        TextView versionTextView = (TextView)convertView.findViewById(R.id.versionTextView);
        TextView cacheTextView = (TextView)convertView.findViewById(R.id.cacheTextView);


        AppModel app = (AppModel)this.list.get(position);
        iconImageView.setImageDrawable(app.getAppIcon());
        nameTextView.setText(app.getAppName());;
        packageTextView.setText(app.getAppPackageName());;
        cacheTextView.setText(app.getAppSize() + "");
        versionTextView.setText(app.getVersionName());
        return convertView;
    }
    //当ListView调用setTextFilter()方法的时候，便会调用该方法
    @Override
    public Filter getFilter() {
        if (mFilter ==null){
            mFilter = new MyFilter();
        }
        return mFilter;
    }
    //我们需要定义一个过滤器的类来定义过滤规则
    class MyFilter extends Filter{
        //我们在performFiltering(CharSequence charSequence)这个方法中定义过滤规则
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults result = new FilterResults();
            List<AppModel> list ;
            if (charSequence.length()==0){//当过滤的关键字为空的时候，我们则显示所有的数据
                list  = backData;
            }else {//否则把符合条件的数据对象添加到集合中
                list = new ArrayList<>();
                for (AppModel app:backData){
                    if (app.getAppName().contains(charSequence)||app.getAppPackageName().contains(charSequence)){
                        list.add(app);
                    }
                }
            }
            result.values = list; //将得到的集合保存到FilterResults的value变量中
            result.count = list.size();//将集合的大小保存到FilterResults的count变量中
            return result;
        }
        //在publishResults方法中告诉适配器更新界面
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list = (List<AppModel>)filterResults.values;
            Log.d("t","publishResults:"+filterResults.count);
            if (filterResults.count>0){
                notifyDataSetChanged();//通知数据发生了改变
                Log.d("t","publishResults:notifyDataSetChanged");
            }else {
                notifyDataSetInvalidated();//通知数据失效
                Log.d("t","publishResults:notifyDataSetInvalidated");
            }
        }
    }

}
