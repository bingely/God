package com.meetrend.haopingdian.activity;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.tool.FileUtil;
import com.meetrend.haopingdian.tool.ImageUtil;
import com.meetrend.haopingdian.tool.LogUtil;
import com.umeng.socialize.utils.Log;

public class AddPictrueActivity extends Activity {
	
	private static final String TAG = AddPictrueActivity.class.getSimpleName();
	private static final int REQUEST_CODE_IMAGE = 0x110;
	private static final int REQUEST_CODE_CAMERA = 0x120;
	
	@ViewInject(id = R.id.container_layout)
	LinearLayout containerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_addon);
		FinalActivity.initInjectedView(this);
		containerLayout.getBackground().setAlpha(110);

		Window window = this.getWindow();
		WindowManager.LayoutParams windowparams = window.getAttributes();
		windowparams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;

		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();

		windowparams.width = width;
		window.setBackgroundDrawableResource(android.R.color.transparent);
		window.setAttributes((android.view.WindowManager.LayoutParams) windowparams);
	}

	//手机相册
	public void libraryClick(View v) {
		Intent intent = new Intent();
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent();
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE_IMAGE);
		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setType("image/*");
			startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE_IMAGE);
		}
	}

	//照相机
	public void cameraclick(View v) {
		if (!FileUtil.isEnoughSpace()) {
			Toast.makeText(AddPictrueActivity.this, "存储卡空间不足5M", Toast.LENGTH_SHORT).show();
			this.finish();
			return;
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, REQUEST_CODE_CAMERA);
	}

	//取消
	public void cancelClick(View v) {
		this.finish();
		this.overridePendingTransition(R.anim.activity_popup, R.anim.dialog_out_anim);
	}

	@Override
	public void onBackPressed() {
		this.finish();
		this.overridePendingTransition(R.anim.activity_popup, R.anim.dialog_out_anim);
	}

	private String theLarge;//图片路径
	private String mPath;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

		Bitmap bitmap = null;

		if (null == intent){
			finish();
			return;
		}

		switch (requestCode) {
			case REQUEST_CODE_IMAGE: 

				Uri uri = intent.getData();
		
				String imagePath = this.getImagePath(uri);
				
				if (imagePath != null) {
					theLarge = imagePath;
				} else {
					bitmap = this.loadPicasaImageFromGalley(uri);
				}
				
				if (bitmap == null && !TextUtils.isEmpty(theLarge)) {
					bitmap = ImageUtil.loadImgThumbnail(theLarge, 100, 100);
				}
				
				break;
			case REQUEST_CODE_CAMERA: 
				Bundle bundle = intent.getExtras();
				bitmap = (Bitmap)bundle.get("data");

				if (bitmap != null){
					File file = new File(FileUtil.getImageCache()+ System.currentTimeMillis()+".jpeg");
					if (!file.exists()) {
						try{
							file.createNewFile();
						}catch (Exception e){
							Log.i("problem","创建文件失败");
						}
					}
					theLarge = file.getAbsolutePath();
					FileOutputStream fileOutputStream = null;
					try {
						fileOutputStream = new FileOutputStream(file);
					}catch (Exception e){
						Log.i("problem","创建文件输入流失败");
					}
					bitmap.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream);
				}

				break;
		   }
		
		
		
		if (bitmap == null) {
			Toast.makeText(AddPictrueActivity.this, "图片为空", Toast.LENGTH_SHORT).show();
			AddPictrueActivity.this.finish();
			return;
		}
		saveBitmap(bitmap);
		Intent mIntent = new Intent();
		mIntent.putExtra("path", mPath);
		setResult(RESULT_OK, mIntent);
		if (bitmap != null)
			bitmap.recycle();
		AddPictrueActivity.this.finish();

		super.onActivityResult(requestCode, resultCode, intent);
	}
	
	
	/**
	 * 当图片过大时，必须将bitmap转成字节数组来传递
	 */
	private byte[] createBitmap(Bitmap bitmap){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();    
	    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);    
	    return baos.toByteArray();
	}

	/**
	 * 创建缩略图
	 * 
	 * @param largeImagePath
	 *            原始大图路径
	 * @param thumbfilePath
	 *            输出缩略图路径
	 * @param square_size
	 *            输出图片宽度
	 * @param quality
	 *            输出图片质量  （此处是原图80%的比例）
	 * @throws IOException
	 */
	public void createImageThumbnail(String largeImagePath, String thumbfilePath, int square_size, int quality) throws IOException {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		// 原始图片bitmap
		Bitmap cur_bitmap = ImageUtil.getBitmapByPath(largeImagePath, opts);

		if (cur_bitmap == null)
			return;

		// 原始图片的高宽
		int[] cur_img_size = new int[] { cur_bitmap.getWidth(), cur_bitmap.getHeight() };
		// 计算原始图片缩放后的宽高
		//int[] new_img_size = scaleImageSize(cur_img_size, square_size);
		
		float ratio = scaleImageRatio(cur_img_size, square_size);
		//Log.i("hh-------------------ratio-------------------------", ratio+"");
		// 生成缩放后的bitmap
		//Bitmap thb_bitmap = ImageUtil.zoomBitmap(cur_bitmap, new_img_size[0], new_img_size[1]);
		
		Bitmap resultBp = ImageUtil.zoomBitmapFromRatio(cur_bitmap, ratio);
		
		// 生成缩放后的图片文件
		saveImageToSD(thumbfilePath, resultBp, quality);
	}

	/**
	 * 写图片文件到SD卡
	 * 
	 */
	private void saveImageToSD(String filePath, Bitmap bitmap, int quality) throws IOException {
		if (bitmap != null) {
			File file = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
			if (!file.exists()) {
				file.mkdirs();
			}
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
			bitmap.compress(CompressFormat.JPEG, quality, bos);
			bitmap.compress(CompressFormat.PNG, quality, bos);
			bitmap.recycle();
			bos.flush();
			bos.close();
		}
	}

	/**
	 * 计算缩放图片的宽高
	 * 
	 * @param img_size
	 * @param square_size
	 * @return
	 */
	public static int[] scaleImageSize(int[] img_size, int square_size) {
		
		//宽高都小于要求的值，就返回图片原始的大小
		if (img_size[0] <= square_size && img_size[1] <= square_size){
			return img_size;
		}
		
		//若当前图片的宽高都大于要求的值，则按比例计算想要的宽高
		double ratio = square_size / (double) Math.max(img_size[0], img_size[1]);
		
		return new int[] { (int) (img_size[0] * ratio), (int) (img_size[1] * ratio) };
	}
	
	/**
	 * 显示的图片相必原图显示的比例
	 * @param img_size
	 * @param square_size
	 * @return
	 */
	public static float scaleImageRatio(int[] img_size, int square_size) {
		
		//宽高都小于要求的值，就返回图片原始的大小
		if (img_size[0] <= square_size && img_size[1] <= square_size){
			return 1.0f;
		}
		
		//若当前图片的宽高都大于要求的值，则按比例计算想要的宽高
		float ratio = square_size / (float) Math.max(img_size[0], img_size[1]);
		
		return ratio;
	}

	public void saveBitmap(Bitmap bitmap) {
		String savePath = FileUtil.getImageCache();
		String largeFileName = FileUtil.getFileName(theLarge);
		String largePath = savePath + largeFileName;

		if (largeFileName.startsWith("thumb_") && new File(largePath).exists()) {
			mPath = largePath;
		} else {
			String thumbFileName = "thumb_" + largeFileName;
			String theThumbnail = savePath + thumbFileName;
			if (new File(theThumbnail).exists()) {
				mPath = theThumbnail;
			} else {
				try {
					createImageThumbnail(theLarge, theThumbnail, 800, 100);
					mPath = theThumbnail;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private Bitmap mBitMap = null;

	/**
	 * 2014年8月13日
	 * 
	 * @param uri
	 *            E-mail:mr.huangwenwei@gmail.com
	 */
	public Bitmap loadPicasaImageFromGalley(final Uri uri) {
		String[] projection = { MediaColumns.DATA, MediaColumns.DISPLAY_NAME };
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();

			int columIndex = cursor.getColumnIndex(MediaColumns.DISPLAY_NAME);
			if (columIndex != -1) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							mBitMap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}).start();
			}
			cursor.close();
			return mBitMap;
		} else
			return null;
	}

	/**
	 * 获取图片路径 2014年8月12日
	 * 
	 * @param uri
	 * @return E-mail:mr.huangwenwei@gmail.com
	 */
	public String getImagePath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			int columIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			String ImagePath = cursor.getString(columIndex);
			cursor.close();
			return ImagePath;
		}

		return uri.toString();
	}
}