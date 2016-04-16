package com.meetrend.haopingdian.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.meetrend.haopingdian.R;

/**
 * 来自android的SearchView，
 * 
 * @author tigereye
 * 
 */
public class UISearchView extends FrameLayout {
	@SuppressWarnings("unused")
	private static final String TAG = UISearchView.class.getSimpleName();
	private EditText mQueryTextView;
	private ImageView close;
	private OnCloseListener mOnCloseListener;
	private OnQueryTextListener mOnQueryChangeListener;

	public UISearchView(Context context) {
		super(context);
	}

	public UISearchView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public UISearchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_search_view, this, true);

		mQueryTextView = (EditText) findViewById(R.id.et_widget_search_view);
		mQueryTextView.addTextChangedListener(mTextWatcher);
		close = (ImageView) findViewById(R.id.iv_close_search_view);
		close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setVisibility(View.GONE);
				mQueryTextView.setText("");
				if (mOnCloseListener != null) {
					mOnCloseListener.onClose();
				}
			}
		});

	}
	
	public String getText(){
		return mQueryTextView.getText().toString();
	}

	private void onTextChanged(CharSequence newText) {
		boolean hasText = !TextUtils.isEmpty(newText);
		close.setVisibility(hasText ? View.VISIBLE : View.GONE);
		if (mOnQueryChangeListener != null) {
			mOnQueryChangeListener.onQueryTextChange(newText.toString());
		}
	}

	/**
	 * Returns the query string currently in the text field.
	 * 
	 * @return the query string
	 */
	public CharSequence getQuery() {
		return mQueryTextView.getText();
	}

    public void setOnQueryTextListener(OnQueryTextListener listener) {
        mOnQueryChangeListener = listener;
    }
    
	public interface OnQueryTextListener {
		boolean onQueryTextChange(String newText);
	}

	/**
	 * Callback to watch the text field for empty/non-empty
	 */
	private TextWatcher mTextWatcher = new TextWatcher() {

		public void beforeTextChanged(CharSequence s, int start, int before, int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before, int after) {
			UISearchView.this.onTextChanged(s);
		}

		public void afterTextChanged(Editable s) {
		}
	};

	public void setOnCloseListener(OnCloseListener listener) {
		mOnCloseListener = listener;
	}

	public interface OnCloseListener {
		public void onClose();
	}
}
