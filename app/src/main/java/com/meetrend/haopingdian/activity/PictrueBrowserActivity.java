package com.meetrend.haopingdian.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import net.tsz.afinal.bitmap.download.SimpleDownloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.FileUtil;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 头像显示和聊天发送图片的显示
 */
public class PictrueBrowserActivity extends BaseActivity {

    private PhotoView imgView;
    public ProgressBar progress;
    private String path;
    private Bitmap bitmap;
    PictrueHandler pictrueHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_view_chat_image);
        tintManager.setStatusBarTintResource(android.R.color.transparent);

        pictrueHandler = new PictrueHandler(PictrueBrowserActivity.this);
        imgView = (PhotoView) this.findViewById(R.id.chat_image);
        progress = (ProgressBar) this.findViewById(R.id.pb_chat_image);

        path = getIntent().getStringExtra("img_url");

        //LogUtil.d(tag, "img_url = " + path);
        if (path.startsWith(File.separator)) {
            bitmap = BitmapFactory.decodeFile(path, null);
            if (bitmap == null) {
                pictrueHandler.sendEmptyMessage(Code.FAILED);
                //showToast(R.string.pic_load_fail);
                return;
            }
        } else {

            new Thread(new Runnable() {

                @Override
                public void run() {
                    SimpleDownloader downloader = new SimpleDownloader();
                    byte[] buffer = downloader.download(path);
                    path = FileUtil.getImageCache() + System.currentTimeMillis() + ".png";
                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream(path);
                        output.write(buffer);
                    } catch (Exception e) {
                        e.printStackTrace();
                        PictrueHandler pictrueHandler = new PictrueHandler(PictrueBrowserActivity.this);

                    }
                    imgView.post(new Runnable() {
                        @Override
                        public void run() {
                            bitmap = BitmapFactory.decodeFile(path, null);
                            if (bitmap == null) {
                                pictrueHandler.sendEmptyMessage(Code.FAILED);
                                //showToast(R.string.pic_load_fail);
                                return;
                            }

                            pictrueHandler.sendEmptyMessage(Code.SUCCESS);
                        }
                    });


                }
            }).start();
        }
    }

    class PictrueHandler extends Handler {

        WeakReference<Activity> mActivityRerence = null;

        public PictrueHandler(Activity activity) {
            mActivityRerence = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final Activity msgActivity = mActivityRerence.get();
            progress.setVisibility(View.GONE);
            imgView.setVisibility(View.VISIBLE);

            switch (msg.what) {
                case Code.FAILED:
                    if (null != msgActivity)
                        imgView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.big_loading_default));
                    break;

                case Code.SUCCESS:

                    if (null != msgActivity) {
                        imgView.setImageBitmap(bitmap);
                        new PhotoViewAttacher(imgView);
                    }
                    break;
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != bitmap) {
            bitmap.recycle();
        }
    }
}