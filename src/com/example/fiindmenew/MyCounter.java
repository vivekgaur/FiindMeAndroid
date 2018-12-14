package com.example.fiindmenew;
import android.os.CountDownTimer;
import android.widget.TextView;

public class MyCounter extends CountDownTimer{
	public TextView tv;
	public MyCounter(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}
	public MyCounter(long millisInFuture, long countDownInterval, TextView view) {
		super(millisInFuture, countDownInterval);
		tv = view;
	}
	
	@Override
	public void onFinish() {
		tv.setText("Expired!");
	}
	@Override
	public void onTick(long millisUntilFinished) {
		_convertTime(millisUntilFinished);
	}
	
	public void _convertTime(long milliseconds){
		int seconds = (int) (milliseconds / 1000) % 60 ;
		int minutes = (int) ((milliseconds / (1000*60)) % 60);
		int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
		tv.setText("Time Left: "+ hours + ":" + minutes + ":" + seconds);
	
	}
}