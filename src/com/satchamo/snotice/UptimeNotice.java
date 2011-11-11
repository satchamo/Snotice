package com.satchamo.snotice;

import java.util.HashMap;
import com.satchamo.snotice.R;
import android.content.Context;
import android.net.Uri;

public class UptimeNotice extends Notice {
	protected static final String DOWN_SOUND_FILE = "file:///sdcard/snotice/alarm.mp3";
	protected static final int DOWN_ICON = R.drawable.down;
	protected static final String DOWN_NOTIFICATION_TITLE = "Site Down";
	
	protected static final String UP_SOUND_FILE = "file:///sdcard/snotice/dialup.mp3";
	protected static final int UP_ICON = R.drawable.up;
	protected static final String UP_NOTIFICATION_TITLE = "Site Up";
	
	protected static final int ID = 1339;
	protected enum State {UP, DOWN};
	protected State state;
		
	public UptimeNotice(HashMap<String, String> vars, Context context) {
		super(vars, context);
		state = vars.get("state").toLowerCase().equals("up") ? State.UP : State.DOWN;		
	}
	
	public int getId() {
		return ID;
	}

	public int getIcon() {
		return state == State.UP ? UP_ICON : DOWN_ICON;
	}
	
	public Uri getSound() {
		return Uri.parse(state == State.UP ? UP_SOUND_FILE : DOWN_SOUND_FILE);
	}
	
	public int getNumber() {
		return 0;
	}	
	
	public String getStatusBarTitle() {
		return state == State.UP ? UP_NOTIFICATION_TITLE : DOWN_NOTIFICATION_TITLE;
	}
	
	public String getNotificationTitle() {
		return getStatusBarTitle();
	}
		
	public String getNotificationText() {
		return "Url: " + vars.get("url");
	}
	
	public Class<?> getIntent() {
		return MoneyActivity.class;
	}	
}