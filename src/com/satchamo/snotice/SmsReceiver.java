package com.satchamo.snotice;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Notification;

public class SmsReceiver extends BroadcastReceiver
{	
	// putting this "secret" message in a text message triggers this app to do something 
	private static final String SIGNAL = "!SNOTICE";

	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		if(bundle == null) return;
		
		String body = getMessages(bundle);
		int signal_position = body.indexOf(SIGNAL);
		// do we care about this message?
		if(signal_position == -1) return; 
		// remove the signal from the body
		body = body.substring(signal_position + SIGNAL.length());
		
		int flag = getNumber(body);
		if(flag == 0) return; // moot
		
		HashMap<String, String> vars = parseMessage(body);
		
		// there should be some way to "register" your notice, instead of putting it in this switch...
		Notice n;
		switch(flag) {
			case 1:
				n = new MoneyNotice(vars, context);
				break;
			case 2:
				n = new UptimeNotice(vars, context);
				break;
			default:
				n = null;
		}

		Notification note = buildNotification(n, context);
		
		NotificationManager mgr = (NotificationManager)context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		mgr.notify(n.getId(), note);
		abortBroadcast(); // stop the sms from going to the inbox
	}
	
	private Notification buildNotification(Notice n, Context context) {
		Notification note = new Notification(n.getIcon(), n.getStatusBarTitle(), System.currentTimeMillis());
		note.sound = n.getSound();
		note.number = n.getNumber();
		Intent notificationIntent = new Intent(context, n.getIntent());
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		note.setLatestEventInfo(context, n.getNotificationTitle(), n.getNotificationText(), contentIntent);
		return note;
	}

	private String getMessages(Bundle bundle) {
		SmsMessage[] msgs = null;
		Object[] pdus = (Object[]) bundle.get("pdus");
		msgs = new SmsMessage[pdus.length];
		String msg = "";
		for (int i=0; i<msgs.length; i++) {
			msg += SmsMessage.createFromPdu((byte[])pdus[i]).getMessageBody().toString() + "\n";  
		}
		return msg;
	}

	private int getNumber(String body) {
		Pattern intsOnly = Pattern.compile("-?\\d+");
		Matcher makeMatch = intsOnly.matcher(body);
		makeMatch.find();
		String inputInt = makeMatch.group();
		try {
			return Integer.parseInt(inputInt, 10);
		} catch(Exception e) {
			return 0;
		}
	}
	
	private HashMap<String, String> parseMessage(String body) {
		HashMap<String, String> vars = new HashMap<String, String>();
		String[] lines = body.split("\\r?\\n");
		for(String line : lines) {
			String[] parts = line.split(":", 2);
			if(parts.length == 2) {
				String key = parts[0].trim().toLowerCase();
				String val = parts[1].trim();
				vars.put(key, val);
			}
		}
		
		return vars;
	}
}