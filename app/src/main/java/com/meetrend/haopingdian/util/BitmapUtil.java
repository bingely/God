package com.meetrend.haopingdian.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.meetrend.haopingdian.bean.MyBitmapEntity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

public class BitmapUtil {
	static public Drawable getScaleDraw(String imgPath, Context mContext) {

		Bitmap bitmap = null;
		try {
			Log.d("BitmapUtil",
					"[getScaleDraw]imgPath is " + imgPath.toString());
			File imageFile = new File(imgPath);
			if (!imageFile.exists()) {
				Log.d("BitmapUtil", "[getScaleDraw]file not  exists");
				return null;
			}
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imgPath, opts);

			opts.inSampleSize = computeSampleSize(opts, -1, 800 * 480);
			// Log.d("BitmapUtil","inSampleSize===>"+opts.inSampleSize);
			opts.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(imgPath, opts);

		} catch (OutOfMemoryError err) {
			Log.d("BitmapUtil", "[getScaleDraw] out of memory");

		}
		if (bitmap == null) {
			return null;
		}
		Drawable resizeDrawable = new BitmapDrawable(mContext.getResources(),
				bitmap);
		return resizeDrawable;
	}

	public static void saveMyBitmap(Context mContext, Bitmap bitmap,
			String desName) throws IOException {

		FileOutputStream fOut = null;

		// 没有内存卡
		if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
			LogUtil.d("sdcard doesn't exit, save png to app dir");
			fOut = mContext.openFileOutput(desName + ".png",
					Context.MODE_PRIVATE);
		} else {
			File f = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/Asst/cache/" + desName + ".png");
			f.createNewFile();
			fOut = new FileOutputStream(f);
		}
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 将图片保存至sd卡
	public static String saveBitmapToSD(Context mContext, Bitmap bitmap,
			String desName) {

		try {
			// File f = new
			// File(Environment.getExternalStorageDirectory().getPath() +
			// "/Asst/cache/" + desName + ".png");
			File f = new File(Environment.getExternalStorageDirectory()
					.getPath(), desName + ".png");
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream fOut = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
			// Log.i("------------保存的文件路径------------",f.getAbsolutePath());
			return f.getAbsolutePath();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	static public Bitmap getScaleBitmap(Resources res, int id) {

		Bitmap bitmap = null;
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(res, id, opts);
			opts.inSampleSize = computeSampleSize(opts, -1, 800 * 480);
			opts.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeResource(res, id, opts);
		} catch (OutOfMemoryError err) {
			Log.d("BitmapUtil", "[getScaleBitmap] out of memory");
		}
		return bitmap;
	}

	static public Bitmap getNetScaleBitmap(Bitmap sBitmap) {

		Bitmap bitmap = null;
		InputStream inputStream = Bitmap2InputStream(sBitmap, 100);

		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			// BitmapFactory.decodeResource(res, id, opts);
			opts.inSampleSize = computeSampleSize(opts, -1, 800 * 480);
			opts.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeStream(inputStream, null, opts);
		} catch (OutOfMemoryError err) {
			Log.d("BitmapUtil", "[getScaleBitmap] out of memory");
		}
		return bitmap;
	}

	public static InputStream Bitmap2InputStream(Bitmap bm, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {

		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;

	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	public static Bitmap decodeBitmap(Resources res, int id) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 通过这个bitmap获取图片的宽和高
		Bitmap bitmap = BitmapFactory.decodeResource(res, id, options);
		if (bitmap == null) {
			LogUtil.d("bitmap为空");
		}
		float realWidth = options.outWidth;
		float realHeight = options.outHeight;
		LogUtil.d("真实图片高度：" + realHeight + "宽度:" + realWidth);
		// 计算缩放比
		int scale = (int) ((realHeight > realWidth ? realHeight : realWidth) / 100);
		if (scale <= 0) {
			scale = 1;
		}
		LogUtil.d("scale=>" + scale);
		options.inSampleSize = scale;
		options.inJustDecodeBounds = false;
		// 注意这次要把options.inJustDecodeBounds 设为 false,这次图片是要读取出来的。
		bitmap = BitmapFactory.decodeResource(res, id, options);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		LogUtil.d("缩略图高度：" + h + "宽度:" + w);
		return bitmap;
	}

	public static Bitmap getCombineBitmaps(List<MyBitmapEntity> mEntityList,
			List<Bitmap> list) {

		Bitmap newBitmap = Bitmap.createBitmap(200, 200, Config.ARGB_8888);
		for (int i = 0; i < mEntityList.size(); i++) {
			newBitmap = mixtureBitmap(newBitmap, list.get(i), new PointF(
					mEntityList.get(i).x, mEntityList.get(i).y));
		}
		return newBitmap;
	}

	/**
	 * 将多个Bitmap合并成一个图片。
	 * 
	 *            ... 要合成的图片
	 * @return
	 */
	public static Bitmap combineBitmaps(int columns, Bitmap... bitmaps) {
		if (columns <= 0 || bitmaps == null || bitmaps.length == 0) {
			throw new IllegalArgumentException(
					"Wrong parameters: columns must > 0 and bitmaps.length must > 0.");
		}
		int maxWidthPerImage = 20;
		int maxHeightPerImage = 20;
		for (Bitmap b : bitmaps) {
			maxWidthPerImage = maxWidthPerImage > b.getWidth() ? maxWidthPerImage
					: b.getWidth();
			maxHeightPerImage = maxHeightPerImage > b.getHeight() ? maxHeightPerImage
					: b.getHeight();
		}
		LogUtil.d("maxWidthPerImage=>" + maxWidthPerImage
				+ ";maxHeightPerImage=>" + maxHeightPerImage);
		int rows = 0;
		if (columns >= bitmaps.length) {
			rows = 1;
			columns = bitmaps.length;
		} else {
			rows = bitmaps.length % columns == 0 ? bitmaps.length / columns
					: bitmaps.length / columns + 1;
		}
		Bitmap newBitmap = Bitmap.createBitmap(columns * maxWidthPerImage, rows
				* maxHeightPerImage, Config.ARGB_8888);
		LogUtil.d("newBitmap=>" + newBitmap.getWidth() + ","
				+ newBitmap.getHeight());
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < columns; y++) {
				int index = x * columns + y;
				if (index >= bitmaps.length)
					break;
				LogUtil.d("y=>" + y + " * maxWidthPerImage=>"
						+ maxWidthPerImage + " = " + (y * maxWidthPerImage));
				LogUtil.d("x=>" + x + " * maxHeightPerImage=>"
						+ maxHeightPerImage + " = " + (x * maxHeightPerImage));
				newBitmap = mixtureBitmap(newBitmap, bitmaps[index],
						new PointF(y * maxWidthPerImage, x * maxHeightPerImage));
			}
		}
		return newBitmap;
	}

	/**
	 * Mix two Bitmap as one.
	 * 
	 *            where the second bitmap is painted.
	 * @return
	 */
	public static Bitmap mixtureBitmap(Bitmap first, Bitmap second,
			PointF fromPoint) {

		if (first == null || second == null || fromPoint == null) {
			return null;
		}
		Bitmap newBitmap = Bitmap.createBitmap(first.getWidth(),
				first.getHeight(), Config.ARGB_8888);
		Canvas cv = new Canvas(newBitmap);
		cv.drawBitmap(first, 0, 0, null);
		cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		return newBitmap;
	}

	public static void getScreenWidthAndHeight(Activity mContext) {
		DisplayMetrics metric = new DisplayMetrics();
		mContext.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		int height = metric.heightPixels;
		LogUtil.d("screen width=>" + width + ",height=>" + height);
	}

	/**
	 * 绘制图标右上角的未读消息数量显示
	 * 
	 * @param context
	 *            上下文
	 * @param icon
	 *            需要被添加的icon的资源ID
	 * @param news
	 *            未读的消息数量
	 * @return drawable
	 */
	@SuppressWarnings("unused")
	public static Drawable displayNewsNumber(Context context, int icon,
			String news) {
		// 初始化画布
		int iconSize = (int) context.getResources().getDimension(
				android.R.dimen.app_icon_size);
		// Bitmap contactIcon = Bitmap.createBitmap(iconSize, iconSize,
		// Config.ARGB_8888);
		Bitmap iconBitmap = BitmapFactory.decodeResource(
				context.getResources(), icon);
		Canvas canvas = new Canvas(iconBitmap);
		// 拷贝图片
		Paint iconPaint = new Paint();
		iconPaint.setDither(true);// 防抖动
		iconPaint.setFilterBitmap(true);// 用来对Bitmap进行滤波处理
		Rect src = new Rect(0, 0, iconBitmap.getWidth(), iconBitmap.getHeight());
		Rect dst = new Rect(0, 0, iconBitmap.getWidth(), iconBitmap.getHeight());
		canvas.drawBitmap(iconBitmap, src, dst, iconPaint);
		// 启用抗锯齿和使用设备的文本字距
		Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		countPaint.setColor(Color.RED);
		canvas.drawCircle(iconSize - 13, 20, 10, countPaint);

		Paint textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		// textPaint.setTypeface(Typeface.DEFAULT_BOLD);
		textPaint.setTextSize(19f);
		canvas.drawText(news, iconSize - 18, 27, textPaint);

		return new BitmapDrawable(iconBitmap);
	}

	/**
	 * 文字回执图片
	 * 
	 * @param printText
	 * @param path
	 */
	public static void createTextBitmap(String printText, String path) {

		Rect rect = new Rect();
		Paint paint = new Paint();
		paint.setTextSize(40);
		paint.setColor(Color.BLACK);
		paint.getTextBounds(printText, 0, printText.length(), rect);
		Bitmap bitmap = Bitmap.createBitmap(rect.width() + 30,
				rect.height() + 30, Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);
		canvas.drawText(printText, 10, 40, paint);
		File file = new File(path);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {

			FileOutputStream out = new FileOutputStream(file);
			try {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 图片组合
	 * 
	 * @param bitmap1
	 * @param bitmap2
	 * @param bitmap3
	 * @return
	 */
	public static Bitmap combileMoreBitmaps(Bitmap bitmap1, Bitmap bitmap2,
			Bitmap bitmap3) {

		int bw1 = bitmap1.getWidth();
		int bh1 = bitmap1.getHeight();

		int bw2 = bitmap2.getWidth();
		int bh2 = bitmap2.getHeight();

		int bw3 = bitmap3.getWidth();
		int bh3 = bitmap3.getHeight();

		int newBw = bw1 + bw2 + bw3;
		int newBh = bh1 + bh2 + bh3;

		Bitmap newbp = Bitmap.createBitmap(newBw, newBh, Config.ARGB_8888);
		Canvas canvas = new Canvas(newbp);

		canvas.drawBitmap(bitmap1, 0, 0, null);
		canvas.drawBitmap(bitmap2, 10, bitmap1.getHeight(), null);// 在src的右下角画
		canvas.drawBitmap(bitmap3, 10,
				bitmap1.getHeight() + bitmap2.getHeight(), null);
		canvas.save(Canvas.ALL_SAVE_FLAG);// 保存

		return newbp;
	}

	// 图片转字节数组
	private byte[] StartBmpToPrintCode(Bitmap bitmap) {
		byte temp = 0;
		int j = 7;
		int start = 0;
		if (bitmap != null) {
			int mWidth = bitmap.getWidth();
			int mHeight = bitmap.getHeight();

			int[] mIntArray = new int[mWidth * mHeight];
			byte[] data = new byte[mWidth * mHeight];
			bitmap.getPixels(mIntArray, 0, mWidth, 0, 0, mWidth, mHeight);
			encodeYUV420SP(data, mIntArray, mWidth, mHeight);
			byte[] result = new byte[mWidth * mHeight / 8];
			for (int i = 0; i < mWidth * mHeight; i++) {
				temp = (byte) ((byte) (data[i] << j) + temp);
				j--;
				if (j < 0) {
					j = 7;
				}
				if (i % 8 == 7) {
					result[start++] = temp;
					temp = 0;
				}
			}
			if (j != 7) {
				result[start++] = temp;
			}

			int aHeight = 24 - mHeight % 24;
			byte[] add = new byte[aHeight * 48];
			byte[] nresult = new byte[mWidth * mHeight / 8 + aHeight * 48];
			System.arraycopy(result, 0, nresult, 0, result.length);
			System.arraycopy(add, 0, nresult, result.length, add.length);

			byte[] byteContent = new byte[(mWidth / 8 + 4)
					* (mHeight + aHeight)];// 打印数组
			byte[] bytehead = new byte[4];// 每行打印头
			bytehead[0] = (byte) 0x1f;
			bytehead[1] = (byte) 0x10;
			bytehead[2] = (byte) (mWidth / 8);
			bytehead[3] = (byte) 0x00;
			for (int index = 0; index < mHeight + aHeight; index++) {
				System.arraycopy(bytehead, 0, byteContent, index * 52, 4);
				System.arraycopy(nresult, index * 48, byteContent,
						index * 52 + 4, 48);

			}
			return byteContent;
		}
		return null;

	}

	// 转换图片格式
	public void encodeYUV420SP(byte[] yuv420sp, int[] rgba, int width,
			int height) {
		// //final int frameSize = width * height;
		int r, g, b, y;// , u, v;
		int index = 0;
		// //int f = 0;
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				r = (rgba[index] & 0xff000000) >> 24;
				g = (rgba[index] & 0xff0000) >> 16;
				b = (rgba[index] & 0xff00) >> 8;

				// rgb to yuv
				y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
				/*
				 * u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128; v =
				 * ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;
				 */
				// clip y
				// yuv420sp[index++] = (byte) ((y < 0) ? 0 : ((y > 255) ? 255 :
				// y));
				byte temp = (byte) ((y < 0) ? 0 : ((y > 255) ? 255 : y));
				yuv420sp[index++] = temp > 0 ? (byte) 1 : (byte) 0;

				// {
				// if (f == 0) {
				// yuv420sp[index++] = 0;
				// f = 1;
				// } else {
				// yuv420sp[index++] = 1;
				// f = 0;
				// }

				// }

			}

		}
		// //f = 0;
	}

	/**
	 * 将字符串形式表示的十六进制数转换为byte数组
	 */
	public byte[] hexStringToBytes(String hexString) {
		hexString = hexString.toLowerCase();
		String[] hexStrings = hexString.split(" ");
		byte[] bytes = new byte[hexStrings.length];
		for (int i = 0; i < hexStrings.length; i++) {
			char[] hexChars = hexStrings[i].toCharArray();
			bytes[i] = (byte) (charToByte(hexChars[0]) << 4 | charToByte(hexChars[1]));
		}
		return bytes;
	}

	private byte charToByte(char c) {
		return (byte) "0123456789abcdef".indexOf(c);
	}

	// 缩放图片
	public Bitmap resizeImage(Bitmap bitmap, int w, int h) {
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;

		float scaleWidth = ((float) newWidth) / width;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleWidth);
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);
		return resizedBitmap;
	}

	public static byte[] readImage(String path) throws Exception {
		URL url = new URL(path);
		// 记住使用的是HttpURLConnection类
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		// 如果运行超过5秒会自动失效 这是android规定
		conn.setConnectTimeout(5 * 1000);
		InputStream inStream = conn.getInputStream();
		// 调用readStream方法
		return readStream(inStream);
	}

	public static byte[] readStream(InputStream inStream) throws Exception {
		// 把数据读取存放到内存中去
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}

}