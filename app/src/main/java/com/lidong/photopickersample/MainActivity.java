package com.lidong.photopickersample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lidong.photopicker.ImageCaptureManager;
import com.lidong.photopicker.PhotoPickerActivity;
import com.lidong.photopicker.PhotoPreviewActivity;
import com.lidong.photopicker.SelectModel;
import com.lidong.photopicker.intent.PhotoPickerIntent;
import com.lidong.photopicker.intent.PhotoPreviewIntent;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @
 * @author lidong
 * @date 2016-02-29
 */
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks  {

    private static final int REQUEST_CAMERA_CODE = 10;
    private static final int REQUEST_PREVIEW_CODE = 20;
    private ArrayList<String> imagePaths = new ArrayList<>();
    private ImageCaptureManager captureManager; // 相机拍照处理类

    private GridView gridView;
    private GridAdapter gridAdapter;
    private Button mButton;
    private String depp;
    private EditText textView;
    private String TAG =MainActivity.class.getSimpleName();
    private Context mContext;


    private ArrayList<String> langLists = new ArrayList<>();


    String PERMISSION_STORAGE_MSG = "请授予权限，否则影响部分使用功能";
    String[] PERMS = {Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将结果转发给EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 申请成功时调用
     * @param requestCode 请求权限的唯一标识码
     * @param perms 一系列权限
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    /**
     * 申请拒绝时调用
     * @param requestCode 请求权限的唯一标识码
     * @param perms 一系列权限
     */
    @Override
    public void onPermissionsDenied(int requestCode,List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @AfterPermissionGranted(10001)
    public void onPermissionSuccess(){
        Toast.makeText(this,"AfterPermission调用成功了",Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
//            //从设置页面返回，判断权限是否申请。
//            if (EasyPermissions.hasPermissions(this, PERMS)) {
//                Toast.makeText(this, "权限申请成功!", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "权限申请失败!", Toast.LENGTH_SHORT).show();
//
//            }
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gridView);
        mButton = (Button) findViewById(R.id.button);
        textView= (EditText)findViewById(R.id.et_context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy( builder.build() );
        }

        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 3 ? 3 : cols;
        gridView.setNumColumns(cols);

        langLists.add("0000");//预览 0
        langLists.add("11111");//所有图片 1
        langLists.add("22222");//完成 2
        langLists.add("33333");//拍张照片 3
        langLists.add("44444");//选择图片 4
        langLists.add("删除了一张图片5");//删除了一张图片 5
        langLists.add("确定删除嘛6");//确定删除嘛 6
        langLists.add("确3");//确定 7
        langLists.add("取消2");//取消 8
        langLists.add("撤销1");//撤销 9
        langLists.add("无法启用系统相机");//无法启用系统相机 10
        langLists.add("已经达到最高选择数量");//已经到达最高选择数量 11

        mContext = this;



        // preview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    int PERMISSION_STORAGE_CODE = 10001;
                    if (EasyPermissions.hasPermissions(mContext, PERMS)) {
                        // 已经申请过权限，做想做的事
                        String imgs = (String) parent.getItemAtPosition(position);
                        if ("000000".equals(imgs)) {
                            PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
                            intent.setSelectModel(SelectModel.MULTI);
                            intent.setShowCarema(true); // 是否显示拍照
                            intent.setMaxTotal(6); // 最多选择照片数量，默认为6
                            intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                            intent.setLanguage(langLists);
                            startActivityForResult(intent, REQUEST_CAMERA_CODE);
                        } else {
                            PhotoPreviewIntent intent = new PhotoPreviewIntent(MainActivity.this);
                            intent.setCurrentItem(position);
                            intent.setLanguage(langLists);
                            if (imagePaths.contains("000000")) {
                                imagePaths.remove("000000");
                            }
                            intent.setPhotoPaths(imagePaths);
                            startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                        }

                    } else {
                        // 没有申请过权限，现在去申请
                        /**
                         *@param host Context对象
                         *@param rationale  权限弹窗上的提示语。
                         *@param requestCode 请求权限的唯一标识码
                         *@param perms 一系列权限
                         */
                        EasyPermissions.requestPermissions((Activity) mContext, PERMISSION_STORAGE_MSG, PERMISSION_STORAGE_CODE, PERMS);
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        imagePaths.add("000000");
        gridAdapter = new GridAdapter(imagePaths);
        gridView.setAdapter(gridAdapter);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                depp =textView.getText().toString().trim()!=null?textView.getText().toString().trim():"woowoeo";
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        FileUploadManager.uploadMany(imagePaths, depp);
//                        FileUploadManager.upload(imagePaths,depp);
                    }
                }.start();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            //从设置页面返回，判断权限是否申请。
            if (EasyPermissions.hasPermissions(this, PERMS)) {
                Toast.makeText(this, "权限申请成功!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "权限申请失败!", Toast.LENGTH_SHORT).show();

            }
        }
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                // 选择照片
                case REQUEST_CAMERA_CODE:
                    ArrayList<String> list = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
                    Log.d(TAG, "list: " + "list = [" + list.size());
                    loadAdpater(list);
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE:
                    ArrayList<String> ListExtra = data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT);
                    Log.d(TAG, "ListExtra: " + "ListExtra = [" + ListExtra.size());
                    loadAdpater(ListExtra);
                    break;
            }
        }
    }

    private void loadAdpater(ArrayList<String> paths){
        if (imagePaths!=null&& imagePaths.size()>0){
            imagePaths.clear();
        }
        if (paths.contains("000000")){
            paths.remove("000000");
        }
        paths.add("000000");
        imagePaths.addAll(paths);
        gridAdapter  = new GridAdapter(imagePaths);
        gridView.setAdapter(gridAdapter);
        try{
            JSONArray obj = new JSONArray(imagePaths);
            Log.e("--", obj.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class GridAdapter extends BaseAdapter{
        private ArrayList<String> listUrls;
        private LayoutInflater inflater;
        public GridAdapter(ArrayList<String> listUrls) {
            this.listUrls = listUrls;
            if(listUrls.size() == 7){
                listUrls.remove(listUrls.size()-1);
            }
            inflater = LayoutInflater.from(MainActivity.this);
        }

        public int getCount(){
            return  listUrls.size();
        }
        @Override
        public String getItem(int position) {
            return listUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_image, parent,false);
                holder.image = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            final String path=listUrls.get(position);
            if (path.equals("000000")){
                holder.image.setImageResource(R.mipmap.ic_launcher);
            }else {
//                RequestOptions options = new RequestOptions()
//                        .placeholder(R.mipmap.ic_launcher)                //加载成功之前占位图
//                        .error(R.mipmap.ic_launcher)                    //加载错误之后的错误图
//                        .override(400,400)                                //指定图片的尺寸
//                        //指定图片的缩放类型为fitCenter （等比例缩放图片，宽或者是高等于ImageView的宽或者是高。）
////                        .fitCenter()
//                        //指定图片的缩放类型为centerCrop （等比例缩放图片，直到图片的狂高都大于等于ImageView的宽度，然后截取中间的显示。）
//                        .centerCrop()
//                        .circleCrop()//指定图片的缩放类型为centerCrop （圆形）
//                        .skipMemoryCache(true)                            //跳过内存缓存
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)        //缓存所有版本的图像
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)        //跳过磁盘缓存
//                        .diskCacheStrategy(DiskCacheStrategy.DATA)        //只缓存原来分辨率的图片
//                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)    //只缓存最终的图片
//                        ;
                Glide.with(mContext)
                        .load(path)
                        .apply(RequestOptions.placeholderOf(com.lidong.photopicker.R.mipmap.default_error).centerCrop())
                        .into(holder.image);
//                Glide.with(MainActivity.this)
//                        .load(path)
//                        .placeholder(R.mipmap.default_error)
//                        .error(R.mipmap.default_error)
//                        .centerCrop()
//                        .crossFade()
//                        .into(holder.image);
            }
            return convertView;
        }
          class ViewHolder {
             ImageView image;
        }
    }
}
