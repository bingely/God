package com.meetrend.haopingdian.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.adatper.FaceGridAdapter;
import com.meetrend.haopingdian.bean.Faceicon;
import com.meetrend.haopingdian.enumbean.FacePageType;
import com.meetrend.haopingdian.event.QQFaceDelEvent;
import com.meetrend.haopingdian.event.QQFaceInputEvent;

import de.greenrobot.event.EventBus;

public class QQFaceGridFragment extends Fragment implements OnItemClickListener {
	private FacePageType type;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return (GridView)inflater.inflate(R.layout.fragment_emoji, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		GridView grid = (GridView)view.findViewById(R.id.gv_emoji);
		grid.setAdapter(new FaceGridAdapter(view.getContext(), type));
		grid.setOnItemClickListener(this);
		
	}

	public static QQFaceGridFragment newInstance(FacePageType type) {
		QQFaceGridFragment fragment = new QQFaceGridFragment();
		fragment.type = type;
		return fragment;
	}
	public interface OnFaceiconClickedListener {
		public void onFaceiconClick(Faceicon icon);
	}
	
	public interface OnBackspaceClickedListener {
		public void onBackspaceClick();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//删除按钮
		if (position == 20) {
			//mOnBackspaceClickedListener.onBackspaceClick();
			EventBus.getDefault().post(new QQFaceDelEvent());
			
		} else {
			//表情符号点击事件
			QQFaceInputEvent qqFaceInputEvent = new QQFaceInputEvent();
			Faceicon faceicon = (Faceicon)parent.getItemAtPosition(position);
			qqFaceInputEvent.setFaceicon(faceicon);
			EventBus.getDefault().post(qqFaceInputEvent);
			//mOnFaceiconClickedListener.onFaceiconClick((Faceicon)parent.getItemAtPosition(position));
		}
	}
}
