package com.meetrend.haopingdian.widget;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;

public class MyDatePickerDialog extends DatePickerDialog {

	public MyDatePickerDialog(Context context, OnDateSetListener callBack,
			int year, int monthOfYear, int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
		// TODO Auto-generated constructor stub
	}

	@Override
	public DatePicker getDatePicker() {
		// TODO Auto-generated method stub
		DatePicker mdaPicker = super.getDatePicker();
		mdaPicker.setOnClickListener(null);
		return mdaPicker;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		super.onClick(dialog, which);
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		// TODO Auto-generated method stub
		super.onDateChanged(view, year, month, day);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public Bundle onSaveInstanceState() {
		// TODO Auto-generated method stub
		return super.onSaveInstanceState();
	}

	@Override
	public void updateDate(int year, int monthOfYear, int dayOfMonth) {
		// TODO Auto-generated method stub
		super.updateDate(year, monthOfYear, dayOfMonth);
	}

}
