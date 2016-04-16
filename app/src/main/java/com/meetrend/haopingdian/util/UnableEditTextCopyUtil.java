package com.meetrend.haopingdian.util;

import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


public class UnableEditTextCopyUtil {
	
	/**
	 * 取消EditText 复制黏贴功能 */
	public static  void setEditHide_Copy_Paste_attr(EditText editText){
		
		if (Build.VERSION.SDK_INT<11) {
			editText.setLongClickable(false);
		} else {
			editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
				
				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}
				
				@Override
				public void onDestroyActionMode(ActionMode mode) {
					
				}
				
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					return false;
				}
				
				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					return false;
				}
			});
		}
		
	}

}
