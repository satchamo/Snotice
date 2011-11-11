package com.satchamo.snotice;

import java.util.HashMap;
import android.content.Context;
import android.net.Uri;

public abstract class Notice {
	protected HashMap<String, String> vars;
	protected Context context;
	
	public Notice(HashMap<String, String> vars, Context context) {
		this.vars = vars;
		this.context = context;
	}
	
	// returns the unique id for the notification message (so you can cancel it later)
	public abstract int getId();
	// return the icon to show on the status bar
	public abstract int getIcon();
	// return the URI to the sound to be played when the notification is created
	public abstract Uri getSound();	
	// returns the number to display on the status bar (like the number of messages or something)
	public abstract int getNumber();		
	// returns the title to display on the status bar	
	public abstract String getStatusBarTitle();
	// returns the title of the notification to be displayed on the notification panel
	public abstract String getNotificationTitle();
	// returns the text to display below the notification title
	public abstract String getNotificationText();
	// returns the Activity class that should be called when the notification is clicked on
	public abstract Class<?> getIntent();
}
