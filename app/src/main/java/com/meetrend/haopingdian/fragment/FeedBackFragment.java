package com.meetrend.haopingdian.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;
import org.json.JSONObject;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.activity.AddPictrueActivity;
import com.meetrend.haopingdian.adatper.FeedBackGridAdapter;
import com.meetrend.haopingdian.bean.MeDetail;
import com.meetrend.haopingdian.env.Server;
import com.meetrend.haopingdian.tool.Callback;
import com.meetrend.haopingdian.tool.Code;
import com.meetrend.haopingdian.tool.LogUtil;
import com.meetrend.haopingdian.tool.SPUtil;
import com.meetrend.haopingdian.widget.ProgressDialog;

/**
 * 一键反馈
 * 
 * @author 肖建斌
 * 
 */
public class FeedBackFragment extends BaseFragment {

	private final static int UPLOAD_SUCCESS = 0X148;
	private final static int UPLOAD_FAILED = 0X149;
	private final static int COMMINT_SUCCESS = 0X150;
	private final static int COMMINT_FAILED = 0X151;

	@ViewInject(id = R.id.actionbar_home, click = "onClickHome")
	ImageView mBarHome;

	@ViewInject(id = R.id.actionbar_title)
	TextView mBarTitle;

	@ViewInject(id = R.id.actionbar_action, click = "onClickAction")
	TextView mBarAction;

	@ViewInject(id = R.id.feedback_content)
	EditText mFeedbackContent;

	@ViewInject(id = R.id.submit_ok, click = "onClickSubmit")
	TextView mSubmitOk;
	
	@ViewInject(id = R.id.feedback_gridview)
	GridView mGridView;
	
	private ExecutorService mES = Executors.newSingleThreadExecutor();
	
	//路径，选择图片的路径集合
	private List<String> imgPathList;
	private List<String> idList;
	//
	private FeedBackGridAdapter feedBackGridAdapter = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_feedback, container,
				false);
		FinalActivity.initInjectedView(this, rootView);

		mBarTitle.setText(R.string.title_feedback);
		mBarAction.setVisibility(View.GONE);
		
		imgPathList = new ArrayList<String>();
		idList = new ArrayList<String>();
		feedBackGridAdapter = new FeedBackGridAdapter(idList, getActivity());
		mGridView.setAdapter(feedBackGridAdapter);
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if (position == 0) {
					
					if (imgPathList.size() > 5) {
						showToast("最多可以上传5张");
						return;
					}
					
					startActivityForResult(new Intent(mActivity, AddPictrueActivity.class),0x912);
					mActivity.overridePendingTransition(R.anim.activity_popup, 0);
				}
				
			}
		});

		return rootView;
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			dimissDialog();

			switch (msg.what) {

			case COMMINT_SUCCESS:

				mFeedbackContent.setText("");
				showToast("问题已提交成功");
				getActivity().finish();

				break;

			case COMMINT_FAILED:

				break;

			case UPLOAD_SUCCESS:
				
				feedBackGridAdapter.setGridList(idList);
				feedBackGridAdapter.notifyDataSetChanged();

				break;

			case UPLOAD_FAILED:
				
				showToast((String) msg.obj);
				
				break;
			default:
				showToast("最多只能上传5张图片");
				break;
			}
		}
	};

	// 点击提交问题
	public void onClickSubmit(View v) {
		if (mFeedbackContent != null
				&& !mFeedbackContent.getText().toString().equals("")) {
			submitFeedBack();
		} else {
			showToast(R.string.feedback_content_isnull);
		}
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == mActivity.RESULT_OK) {
			switch (requestCode) {
			case 0x912:
				String image = data.getStringExtra("path");
				showDialog("上传中...");
				mES.execute(new upLoadPicTask(image));
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// 提交
	public void submitFeedBack() {

		AjaxParams params = new AjaxParams();
		params.put("content", mFeedbackContent.getText().toString());
		params.put("token", SPUtil.getDefault(getActivity()).getToken());
		params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());
		params.put("source", "APP");
		
		for (int i = 1; i < imgPathList.size(); i++) {
			
			if (i == 1) {
				params.put("imageOne", imgPathList.get(i));
			}else if (i == 2) {
				params.put("imageTwo", imgPathList.get(i));
			}else if (i == 3) {
				params.put("imageThree", imgPathList.get(i));
			}else if (i == 4) {
				params.put("imageFour", imgPathList.get(i));
			}else if (i == 5) {
				params.put("imageFive", imgPathList.get(i));
			}
			
		}
		
		Callback callback = new Callback(tag, getActivity()) {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);

				if (null != strMsg) {
					showToast(strMsg);
				} else {
					showToast("提交失败");
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				dimissDialog();

				if (isSuccess) {
					mHandler.sendEmptyMessage(COMMINT_SUCCESS);
				} else {
					if (data.has("msg")) {
						showToast(data.get("msg").toString());
					} else {
						showToast("反馈提交失败");
					}
				}

			}
		};

		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(Server.BASE_URL + Server.FEED_BACK, params, callback);
	}

	// 上传图片
	class upLoadPicTask implements Runnable {
		private String absPath;

		public upLoadPicTask(String absPath) {
			this.absPath = absPath;
		}

		@Override
		public void run() {

			AjaxParams params = new AjaxParams();
			params.put("storeId", SPUtil.getDefault(getActivity()).getStoreId());

			try {
				params.put("file", new File(absPath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				LogUtil.w(tag, e.getMessage());
			}

			Callback callback = new Callback(tag, getActivity()) {

				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);

					try {
						if (isSuccess) {

							String imgid = data.get("pictureId").getAsString();
							String url = data.get("url").getAsString();
							imgPathList.add(imgid);
							idList.add(url);

							Message msg = new Message();
							msg.what = UPLOAD_SUCCESS;
							mHandler.sendMessage(msg);

						} else {
							Message message = new Message();
							message.what = UPLOAD_FAILED;
							if (data.has("msg")) {
								message.obj = data.get("msg").toString();
							} else {
								message.obj = "上传失败";
							}

							mHandler.sendMessage(message);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);

					if (null != strMsg) {
						showToast(strMsg);
					}
				}

			};

			FinalHttp http = new FinalHttp();
			http.post(Server.BASE_URL + Server.UPLOAD_URL + "?token="
					+ SPUtil.getDefault(getActivity()).getToken(), params,
					callback);
		}

	}

	public void onClickHome(View v) {
		mActivity.finish();
	}

}
