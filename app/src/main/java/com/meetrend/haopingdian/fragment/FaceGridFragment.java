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

public class FaceGridFragment extends BaseFragment implements OnItemClickListener {
	private OnFaceiconClickedListener mOnFaceiconClickedListener;
	private OnBackspaceClickedListener mOnBackspaceClickedListener;
	private FacePageType type;
	
	private View rootView;
	//private boolean isPrepare;//做好准备
	//private boolean hasLoad = false;//是否加载过 
	private GridView grid;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		//if (rootView == null) {
			rootView = (GridView)inflater.inflate(R.layout.fragment_emoji, container, false);
			grid = (GridView)rootView.findViewById(R.id.gv_emoji);
			//if (type == null) {
			//	type = FacePageType.EMOJI;
			//} 
			mOnFaceiconClickedListener = (OnFaceiconClickedListener)this.getActivity();
			mOnBackspaceClickedListener = (OnBackspaceClickedListener)this.getActivity();
			
			grid.setAdapter(new FaceGridAdapter(getActivity(), type));
			grid.setOnItemClickListener(this);
			
			//isPrepare = true;
			//requestList();
		//}
		
		
		//ViewGroup parent = (ViewGroup)rootView.getParent();
		//if(parent != null) {
		//	parent.removeView(rootView);
		//}
		
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
	}

	public static FaceGridFragment newInstance(FacePageType type) {
		FaceGridFragment fragment = new FaceGridFragment();
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
			mOnBackspaceClickedListener.onBackspaceClick();
		} else {
			//表情符号点击事件
			mOnFaceiconClickedListener.onFaceiconClick((Faceicon)parent.getItemAtPosition(position));
		}
	}

	//@Override
	//public void requestList() {
		
		//if (!isPrepare || !isVisible || hasLoad) {
		//	return;
		//}
		
		
		
		//hasLoad = true;
	//}
}