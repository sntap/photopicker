package com.lidong.photopicker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.lidong.photopicker.widget.ViewPagerFixed;

import java.util.ArrayList;

/**
 * Created by foamtrace on 2015/8/25.
 */
public class PhotoPreviewActivity extends AppCompatActivity implements PhotoPagerAdapter.PhotoViewClickListener{

    public static final String EXTRA_PHOTOS = "extra_photos";
    public static final String EXTRA_CURRENT_ITEM = "extra_current_item";

    /** 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合  */
    public static final String EXTRA_RESULT = "preview_result";

    /** 预览请求状态码 */
    public static final int REQUEST_PREVIEW = 99;

    private ArrayList<String> paths;
    private ViewPagerFixed mViewPager;
    private PhotoPagerAdapter mPagerAdapter;
    private int currentItem = 0;



    /** 默认选择的数据集 */
    public static final String EXTRA_LANGUAGE_LIST = "language_list";
    // 结果数据
    private ArrayList<String> languageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_preview);

        initViews();

        paths = new ArrayList<>();
        ArrayList<String> pathArr = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);
        if(pathArr != null){
            paths.addAll(pathArr);
        }

        ArrayList<String> lantmp = getIntent().getStringArrayListExtra(EXTRA_LANGUAGE_LIST);
        if(lantmp != null && lantmp.size() > 0) {
            languageList.addAll(lantmp);
        }
        getSupportActionBar().setTitle(languageList.get(0));

        currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);

        mPagerAdapter = new PhotoPagerAdapter(this, paths);
        mPagerAdapter.setPhotoViewClickListener(this);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(currentItem);
        mViewPager.setOffscreenPageLimit(5);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateActionBarTitle();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        updateActionBarTitle();
    }

    private void initViews(){
        mViewPager = (ViewPagerFixed) findViewById(R.id.vp_photos);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.pickerToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void OnPhotoTapListener(View view, float v, float v1) {
        onBackPressed();
    }

    public void updateActionBarTitle() {
        getSupportActionBar().setTitle(
                getString(R.string.image_index, mViewPager.getCurrentItem() + 1, paths.size()));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT, paths);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        // 删除当前照片
        if(item.getItemId() == R.id.action_discard){
            final int index = mViewPager.getCurrentItem();
            final String deletedPath =  paths.get(index);
            String deleteinfo = languageList.get(5);
            if(deleteinfo==null){
                deleteinfo = getResources().getString(R.string.deleted_a_photo); ;
            }

            String confirminfo = languageList.get(6);
            if(confirminfo==null){
                confirminfo = getResources().getString(R.string.confirm_to_delete); ;
            }

            String sureinfo = languageList.get(7);
            if(sureinfo==null){
                sureinfo = getResources().getString(R.string.yes);
            }

            String cancelinfo = languageList.get(8);
            if(cancelinfo==null){
                cancelinfo = getResources().getString(R.string.cancel);
            }

            String undoinfo = languageList.get(9);
            if(undoinfo==null){
                undoinfo = getResources().getString(R.string.undo);
            }



            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), deleteinfo,
                    Snackbar.LENGTH_LONG);
            if(paths.size() <= 1){
                // 最后一张照片弹出删除提示
                // show confirm dialog
                new AlertDialog.Builder(this)
                        .setTitle(confirminfo)
                        .setPositiveButton(sureinfo, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                paths.remove(index);
                                onBackPressed();
                            }
                        })
                        .setNegativeButton(cancelinfo, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }else{
                snackbar.show();
                paths.remove(index);
                mPagerAdapter.notifyDataSetChanged();
            }

            snackbar.setAction(undoinfo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (paths.size() > 0) {
                        paths.add(index, deletedPath);
                    } else {
                        paths.add(deletedPath);
                    }
                    mPagerAdapter.notifyDataSetChanged();
                    mViewPager.setCurrentItem(index, true);
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}
