package com.meetrend.haopingdian.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.meetrend.haopingdian.R;

/**
 * android implement ios UIEdit 
 * @author tigereye
 *
 */
public class UIEdit extends ViewGroup implements TextWatcher {
	private EditText mEditor;
	private ImageButton mBtn;

	public UIEdit(Context context) {
		super(context);
	}
	
	public UIEdit(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.widget_uiedit, this, true);
		mEditor = (EditText)this.findViewById(R.id.uiedit_et);
		mBtn    = (ImageButton)this.findViewById(R.id.uiedit_ib);
		mBtn.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				mBtn.setVisibility(INVISIBLE);
				mEditor.setText("");
			}
		});
		mEditor.addTextChangedListener(this);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.UIEdit, 0, 0);
		
		try {
			String hint = a.getString(R.styleable.UIEdit_hint);
			mEditor.setHint(hint);
		} finally {
			a.recycle();
		}
	}
	
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {	
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {		
		if (s.length() == 0) {
			mBtn.setVisibility(INVISIBLE);
		} else {
			mBtn.setVisibility(VISIBLE);
		}
	}

}
