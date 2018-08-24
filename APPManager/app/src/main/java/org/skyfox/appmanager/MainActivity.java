package org.skyfox.appmanager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    MainActivityAdapter tableAdapter;
    ListView myListView;
    SearchView searchView;
    List<AppModel> list = new ArrayList<AppModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchView = (SearchView) findViewById(R.id.searchView);
        myListView = (ListView) findViewById(R.id.myListView);

//        myListView.setBackgroundResource(R.color.colorAccent);
        tableAdapter = new MainActivityAdapter(this, null);
        myListView.setAdapter(tableAdapter);
        myListView.setTextFilterEnabled(true); // 开启过滤功能
        reloadData();

        // 设置搜索文本监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length()>0){
                    myListView.setFilterText(newText);
                }else{
                    myListView.clearTextFilter();
                }
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppModel app = tableAdapter.list.get(i);
                //
//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                ComponentName cn = new ComponentName(packageName, className);
//                intent.setComponent(cn);
//                startActivity(intent);
                doStartApplicationWithPackageName(app.getAppPackageName());
            }
        });
    }

    private void reloadData() {
        list =  getAllApk(MainActivity.this);
        tableAdapter.setDataSource(list);
    }

    public static List<AppModel> getAllApk(Context context) {
        List<AppModel> appList = new ArrayList<>();
        AppModel app = null;
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> list = packageManager.getInstalledPackages(0);
        for (PackageInfo p : list) {
            app = new AppModel();
            app.setAppIcon(p.applicationInfo.loadIcon(packageManager));
            app.setAppName(packageManager.getApplicationLabel(p.applicationInfo).toString());
            app.setAppPackageName(p.applicationInfo.packageName);
            app.setApkPath(p.applicationInfo.sourceDir);
            app.setVersionName(p.versionName);

            File file = new File(p.applicationInfo.sourceDir);
            app.setAppSize((int) file.length());
            app.setPackageInfo(p);
            int flags = p.applicationInfo.flags;
            //判断是否是属于系统的apk
            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                app.setSystem(true);
            } else {
                app.setSd(true);
            }
            appList.add(app);

        }
        return appList;
    }
    private void doStartApplicationWithPackageName(String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            startActivity(intent);
        }
    }
}
