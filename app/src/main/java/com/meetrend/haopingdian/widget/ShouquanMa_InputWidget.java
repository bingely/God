package com.meetrend.haopingdian.widget;


import java.util.Timer;
import java.util.TimerTask;

import com.meetrend.haopingdian.R;
import com.meetrend.haopingdian.util.UnableEditTextCopyUtil;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShouquanMa_InputWidget extends LinearLayout {

	private static final int LENGTH = 6;
	private TextView[] password = new TextView[LENGTH];
	private EditText dymPassEdit;
	
	public InputCompeleListener oneditlistener = null;

	public ShouquanMa_InputWidget(Context context) {
		this(context, null);
	}

	public ShouquanMa_InputWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews(context, attrs);
	}

	public void initViews(Context context, AttributeSet attrs) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.shouquanma_inputpass_widget, this, true);
		
		dymPassEdit = (EditText) view.findViewById(R.id.editpass);
		UnableEditTextCopyUtil.setEditHide_Copy_Paste_attr(dymPassEdit);
		password[0] = (TextView) view.findViewById(R.id.one);
		password[1] = (TextView) view.findViewById(R.id.two);
		password[2] = (TextView) view.findViewById(R.id.three);
		password[3] = (TextView) view.findViewById(R.id.four);
		password[4] = (TextView) view.findViewById(R.id.five);
		password[5] = (TextView) view.findViewById(R.id.six);
		dymPassEdit.requestFocus();
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager imm = (InputMethodManager) dymPassEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			}
		}, 998);
		
		
		OnClickListener listner = new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				InputMethodManager imm = (InputMethodManager) dymPassEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
				dymPassEdit.requestFocus();
				
			}
		};

		for (int i = 0; i < password.length; i++) {
			
			password[i].setOnClickListener(listner);
		}

		dymPassEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				if (dymPassEdit.getText().length() == 6) {

					if (oneditlistener != null) {
						oneditlistener.compeleteCallBack(true);
					}

				} else {
					if (oneditlistener != null) {
						oneditlistener.compeleteCallBack(false);
					}
				}

				String text = dymPassEdit.getText().toString();

				for (int i = 0; i < 6; i++) {
					password[i].setText("");
				}

				for (int i = 0; i < text.length(); i++) {
					password[i].setText(".");
				}
				dymPassEdit.requestFocus();
			}
		});		
	}
	
	public String getPassword() {
		return dymPassEdit.getText().toString();
	}


	public void setOnEditListenr(InputCompeleListener onEditListener) {
		if (onEditListener != null) {
			this.oneditlistener = onEditListener;
		}
	}
	

	/**
	 * 回调：当已经输入六位数
	 */
	public interface InputCompeleListener {

		/**
		 * 
		 * @param 为true已经输入六位数
		 */
		void compeleteCallBack(boolean b);
	}
	
	 /**
	  * 隐藏虚拟键盘
	  * @param v
	  */
    public  void hideKeyboard()
    {
        InputMethodManager imm = ( InputMethodManager ) dymPassEdit.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );     
      if ( imm.isActive( ) ) {     
          imm.hideSoftInputFromWindow( dymPassEdit.getApplicationWindowToken( ) , 0 );   
      }    
    }
    
}

