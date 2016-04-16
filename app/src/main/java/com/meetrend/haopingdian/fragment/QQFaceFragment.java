package com.meetrend.haopingdian.fragment;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.bean.Faceicon;
import com.meetrend.haopingdian.enumbean.FacePageType;
import com.meetrend.haopingdian.widget.CirclePageIndicator;

/**
 * 
 * 表情
 * */
public class QQFaceFragment extends BaseFragment {
	private ViewPager pager;
	private CirclePageIndicator indicator;
	private List<QQFaceGridFragment> fragmentList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		FacePageType[] array = FacePageType.values();
		View rootView = inflater.inflate(R.layout.fragment_face, container, false);
		
		pager = (ViewPager) rootView.findViewById(R.id.pager_fragment_face);
		indicator = (CirclePageIndicator) rootView.findViewById(R.id.indicator_fragment_face);
		indicator.setPageColor(Color.parseColor("#dedede"));//实体的颜色
		
		fragmentList = new ArrayList<QQFaceGridFragment>();
		for (FacePageType item : array) {
			fragmentList.add(QQFaceGridFragment.newInstance(item));
		}
		FacePagerAdapter adapter = new FacePagerAdapter(mActivity.getSupportFragmentManager());
		pager.setAdapter(adapter);
		pager.setOffscreenPageLimit(array.length);
		indicator.setViewPager(pager);
		return rootView;
	}

	/**
	 * 跟EditText交互
	 * 
	 * @param edit
	 * @param icon
	 */
	public static void input(EditText edit, Faceicon icon) {
		if (edit == null || icon == null) {
			return;
		}
		int start = edit.getSelectionStart();
		int end = edit.getSelectionEnd();
		
		if (start < 0) {
			edit.append(icon.getValueOrEmoji());
		} else {
			edit.getText().replace(Math.min(start, end), Math.max(start, end), icon.getValueOrEmoji(), 0, icon.getValueOrEmoji().length());

		}
	}

	class FacePagerAdapter extends FragmentPagerAdapter {
		public FacePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentList.get(position);
		}

		@Override
		public int getCount() {
			return fragmentList.size();
		}

	}
}