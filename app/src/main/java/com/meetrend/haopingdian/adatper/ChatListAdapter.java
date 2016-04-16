package com.meetrend.haopingdian.adatper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import net.tsz.afinal.bitmap.download.SimpleDownloader;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.MeInfoActivity;
import com.meetrend.haopingdian.activity.MemberInfoActivity;
import com.meetrend.haopingdian.activity.PictrueBrowserActivity;
import com.meetrend.haopingdian.bean.ChatEntity;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.speechrecord.MediaPlayerManager;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.tool.StringUtil;
import com.meetrend.haopingdian.widget.ProgressWheel;

/**
 * 单聊界面 适配器
 * 
 * @author 肖建斌
 * 
 */
public class ChatListAdapter extends BaseAdapter {
	
	private static final String TAG = ChatListAdapter.class.getSimpleName();

	private static final long ONE_MINIUTE = 60 * 1000;
	private List<ChatEntity> mList;
	private Executor mExecutor;
	private String sdcardPath;
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	
	private View voiceView;

	public ChatListAdapter(Context context, List<ChatEntity> list) {
		mLayoutInflater = LayoutInflater.from(context);
		mContext = context;
		mList = list;

		sdcardPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separatorChar
				+ ".dayitea"
				+ File.separatorChar;// 文件保存路径
		File file = new File(sdcardPath);
		if (file.exists() == false) {
			file.mkdir();
		}
		mExecutor = Executors.newFixedThreadPool(2);
	}

	// 聊天信息的显示方向
	enum ORIENTATION {
		LEFT, RIGHT
	};

	private static final int ORIENTATION_TYPE_COUNT = ORIENTATION.values().length;

	// UI显示类型
	@Override
	public int getItemViewType(int position) {
		
		ChatEntity item = mList.get(mList.size() - 1 - position);// 倒序显示
		if (!item.fromUserId.equals(item.userId)) {
			return ORIENTATION.RIGHT.ordinal();
		} else {
			return ORIENTATION.LEFT.ordinal();
		}
		
	}

	public void setList(List<ChatEntity> mlist){
		this.mList = mlist;
	}

	@Override
	public int getViewTypeCount() {
		return ORIENTATION_TYPE_COUNT;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ChatEntity item = mList.get(mList.size() - 1 - position); // 倒序显示
		int type = getItemViewType(position);

		ViewHolder holder = null;
		if (convertView == null || convertView.getTag() == null) {
			holder = new ViewHolder();
			if (type == ORIENTATION.RIGHT.ordinal()) {

				convertView = mLayoutInflater.inflate(R.layout.item_chat_right,
						null);

				holder.time = (TextView) convertView
						.findViewById(R.id.item_chatright_time);// 消息发送时间
				holder.photo = (SimpleDraweeView) convertView
						.findViewById(R.id.item_chatright_photo);// 头像
				holder.content = (TextView) convertView
						.findViewById(R.id.item_chatright_content);
				holder.pb = (ProgressWheel) convertView
						.findViewById(R.id.item_chatright_pb);// 进度条

				holder.image = (SimpleDraweeView) convertView
						.findViewById(R.id.item_chatright_image);// 显示发送的图片
				holder.imgContainerLayout = (RelativeLayout) convertView
						.findViewById(R.id.right_img_container);

				holder.err = (ImageView) convertView
						.findViewById(R.id.item_chatright_failed);
				holder.recordTime = (TextView) convertView
						.findViewById(R.id.right_record_time);// 录音时长显示

			} else {
				convertView = mLayoutInflater.inflate(R.layout.item_chat_left,
						null);
				holder.time = (TextView) convertView
						.findViewById(R.id.item_chatleft_time);
				holder.photo = (SimpleDraweeView) convertView
						.findViewById(R.id.item_chatleft_photo);
				holder.content = (TextView) convertView
						.findViewById(R.id.item_chatleft_content);

				holder.image = (SimpleDraweeView) convertView
						.findViewById(R.id.item_chatleft_image);// 显示发送的图片
				holder.imgContainerLayout = (RelativeLayout) convertView
						.findViewById(R.id.left_img_container);

				holder.recordTime = (TextView) convertView
						.findViewById(R.id.left_record_time);
				holder.unDistributeImg = (ImageView) convertView
						.findViewById(R.id.undistributeimg);

				
				
				
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.photo.setOnClickListener(new PhotoClickLinstener(type, item));

		// 时间相差过短不显示
		if (position != 0) {
			ChatEntity oldItem = mList.get(mList.size() - 1 - position + 1);
			long result = Long.parseLong(item.createTime)
					- Long.parseLong(oldItem.createTime);

			if (result < ONE_MINIUTE) {
				holder.time.setVisibility(View.GONE);
			} else {
				holder.time.setVisibility(View.VISIBLE);
				holder.time.setText(StringUtil.formatDateStr(item.createTime));
			}
		}

		if (type == ORIENTATION.RIGHT.ordinal()) {
			switch (item.status) {
			case SUCCESS:
				holder.pb.setVisibility(View.GONE);
				holder.err.setVisibility(View.GONE);
				break;
			case FAILED:
				holder.pb.setVisibility(View.GONE);
				holder.err.setVisibility(View.VISIBLE);
				break;
			case SENDING:
				holder.pb.setVisibility(View.VISIBLE);
				holder.err.setVisibility(View.GONE);
				break;
			}
		}

		//语音
		if (item.msgType.equals("voice")) {

			holder.recordTime.setVisibility(View.GONE);
			holder.content.setText("");
			holder.content.setVisibility(View.VISIBLE);
			//语音播放
			holder.content.setOnClickListener(voiceClickListener);

			if (type == ORIENTATION.RIGHT.ordinal()) {

				holder.content.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						R.drawable.chat_voice_type_right, 0);

			} else {

				holder.content.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.chat_voice_type_left, 0, 0, 0);
			}

			holder.image.setVisibility(View.GONE);

			String path = item.content;

			if (path.startsWith(File.separator)) {
				holder.content.setTag(path);
			} else {

				String url = Server.BASE_URL + item.content + "&token="
						+ SPUtil.getDefault(mContext).getToken();
				path = sdcardPath + item.fromUserId + "_" + item.createTime
						+ ".amr";

				holder.content.setTag(path);
				//下载语音文件
				mExecutor.execute(new DownloadTask(url, path));
			}

		} else if (item.msgType.equals("image")) {

			holder.image.setVisibility(View.VISIBLE);
			holder.imgContainerLayout.setVisibility(View.VISIBLE);
			holder.content.setVisibility(View.GONE);
			holder.image.setOnClickListener(imageClickListener);

			String path = item.content;
			
			if (path.startsWith(File.separator)) {

				//Bitmap bitmap = ImageUtil.loadImgThumbnail(path, 800, 800);
				//holder.image.setImageBitmap(bitmap);
				holder.image.setTag(item.content);
				
				//此处是针对小米手机出来
				if(path.startsWith("file://")){
					path = path.substring(6);
				}
				
				File file = new File(path);
				Uri uri = Uri.fromFile(file);
				holder.image.setImageURI(uri);

			} else if (path.startsWith("http")) {

				String url = path;
				holder.image.setImageURI(Uri.parse(url));
				holder.image.setTag(url);

			} else {

				// 没有http开头
				String url = Server.BASE_URL + path + "&token=" + SPUtil.getDefault(mContext).getToken();
				
				//Log.e("###############图片###############", url);
						
				holder.image.setImageURI(Uri.parse(url));
				holder.image.setTag(url);

			}

		} else {

			holder.content.setVisibility(View.VISIBLE);
			holder.content.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			holder.content.setText(Html.fromHtml(item.content));
			// 网址，电子邮件，电话号码 有下划线，点击可以响应
			Linkify.addLinks(holder.content, Linkify.WEB_URLS
					| Linkify.EMAIL_ADDRESSES | Linkify.PHONE_NUMBERS);

			holder.image.setVisibility(View.GONE);
			holder.imgContainerLayout.setVisibility(View.GONE);
			holder.content.setOnClickListener(null);

		}

		//avatarLoader.display(holder.photo, Server.BASE_URL + item.avatarId);
		holder.photo.setImageURI(Uri.parse(Server.BASE_URL + item.avatarId));

		// 未分配图标的显示问题
		if (type != ORIENTATION.RIGHT.ordinal()) {
			String managerId = item.managerId;
			if ("".equals(managerId)) {
				holder.unDistributeImg.setVisibility(View.VISIBLE);
			} else {
				holder.unDistributeImg.setVisibility(View.GONE);
			}
		}

		return convertView;
	}
	
	//头像点击事件
	private class PhotoClickLinstener implements OnClickListener{
		
		private int type;
		private ChatEntity chatEntity;
		
		public PhotoClickLinstener(int type,ChatEntity chatEntity){
			this.type = type;
			this.chatEntity = chatEntity;
		}

		@Override
		public void onClick(View v) {
			
			Bundle bundle = new Bundle();
			bundle.putString("id", chatEntity.fromUserId);
			Intent intent = null;
			if (type == 0) {
				//左边
				intent = new Intent(mContext,MemberInfoActivity.class);
				intent.putExtras(bundle);
			}else{
				
				 intent = new Intent(mContext,MeInfoActivity.class);
			}
			
			intent.putExtras(bundle);
			mContext.startActivity(intent);
		}
		
	}
	

	//语音的播放
	private OnClickListener voiceClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//String path = (String) v.getTag();
			//playMusic(path);
			
			
			if (voiceView != null) {
				//		animView.clearAnimation();
				//		animView.setBackgroundResource(R.drawable.adj);
						MediaPlayerManager.pause();
						voiceView = null;
			}
			
			voiceView = v;
			String voicePath = voiceView.getTag().toString();
			if (null == voicePath) {
				Toast.makeText(mContext, "语音不存在", Toast.LENGTH_SHORT).show();
				return;
			}
			MediaPlayerManager.playSound(voicePath,new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					
					//animView.setBackgroundResource(R.drawable.adj);
					//animView.clearAnimation();//清除动画
				}
			});
		}
	};

	private OnClickListener imageClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext, PictrueBrowserActivity.class);
			intent.putExtra("img_url", (String) v.getTag());
			Log.i("----msg_img--",v.getTag()+"");
			mContext.startActivity(intent);
		}
	};

//	private void playMusic(String path) {
//		try {
//			
//			if (mMediaPlayer.isPlaying()) {
//				mMediaPlayer.stop();
//			}
//			
//			mMediaPlayer.reset();
//			mMediaPlayer.setDataSource(path);
//			mMediaPlayer.prepare();
//			mMediaPlayer.start();
//			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
//				@Override
//				public void onCompletion(MediaPlayer mp) {
//
//				}
//			});
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//	}

	class DownloadTask implements Runnable {
		
		private String path;
		private String url;

		public DownloadTask(String url, String path) {
			this.url = url;
			this.path = path;
		}

		@Override
		public void run() {
			SimpleDownloader downloader = new SimpleDownloader();
			byte[] buffer = downloader.download(url);
			FileOutputStream output = null;
			try {
				output = new FileOutputStream(path);
				output.write(buffer);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (output != null) {
					try {
						output.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	final static class ViewHolder {
		public TextView time;
		public SimpleDraweeView photo;
		public SimpleDraweeView image;
		public TextView content;
		public ProgressWheel pb;
		public ImageView err;
		public TextView recordTime;
		public RelativeLayout imgContainerLayout;
		public ImageView unDistributeImg;

	};
}