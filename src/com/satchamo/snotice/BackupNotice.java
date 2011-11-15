package com.satchamo.snotice;

import java.util.HashMap;
import com.satchamo.snotice.R;
import android.content.Context;
import android.net.Uri;

public class BackupNotice extends Notice {
	protected static final String SOUND_FILE = "file:///sdcard/snotice/sonar.mp3";
	protected static final int ICON = R.drawable.backup;
	protected static final String NOTIFICATION_TITLE = "Backup Failed";
	
	protected static final int ID = 1340;
		
	public BackupNotice(HashMap<String, String> vars, Context context) {
		super(vars, context);
	}
	
	public int getId() {
		return ID;
	}

	public int getIcon() {
		return ICON;
	}
	
	public Uri getSound() {
		return Uri.parse(SOUND_FILE);
	}
	
	public int getNumber() {
		return 0;
	}	
	
	public String getStatusBarTitle() {
		return NOTIFICATION_TITLE;
	}
	
	public String getNotificationTitle() {
		return getStatusBarTitle();
	}
		
	public String getNotificationText() {
		return vars.get("info");
	}
	
	public Class<?> getIntent() {
		return MoneyActivity.class;
	}	
}