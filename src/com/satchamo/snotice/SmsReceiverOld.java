package com.satchamo.snotice;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.satchamo.snotice.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.app.Activity;
import android.app.Notification;

public class SmsReceiverOld extends BroadcastReceiver
{	
	// putting this "secret" message in a text message triggers this app to do something 
	private static final String SIGNAL = "!APTIBYTE";
	
	// specify the times you want to silence non-critical sound (in military hours)
	private static final int GO_TO_BED_AT = 22;
	private static final int WAKE_UP_AT = 9; 
	
	private static final int MONEY_ID = 1337;
	private static final int UPTIME_ID = 1338;
	
	
	public void onReceive(Context context, Intent intent) 
	{
		Bundle bundle = intent.getExtras();
		if(bundle == null) return;

		String body = getMessages(bundle);
		if(body.indexOf(SIGNAL) == -1) return; // don't care about these
		int flag = getNumber(body);
		if(flag == 0) return; // moot

		NotificationManager mgr = (NotificationManager)context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		Notification note;
		String contentTitle;
		String contentText;
		Intent notificationIntent = new Intent(context, MoneyActivity.class);
		// status message
		// site up/down
		if(flag == -1 || flag == -2)
		{
			if(flag == -1) // site up
			{
				contentTitle = "Site Up";
				note = new Notification(R.drawable.up, contentTitle, System.currentTimeMillis());
				note.sound = Uri.parse("file:///sdcard/money/dialup.mp3");
			} 
			else // site down 
			{
				contentTitle = "Site Down";
				note = new Notification(R.drawable.down, contentTitle, System.currentTimeMillis());
				note.sound = Uri.parse("file:///sdcard/money/alarm.mp3");
			}

			int pos = body.indexOf("URL:");
			int stop = body.indexOf("TIME:");
			contentText = body.substring(pos, stop);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			note.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			mgr.notify(UPTIME_ID, note);
		}
		else // money 
		{
			int money = flag; // flag is a positive int holding the amount of $ earned just now
			int total = updateTotal(money, context);
			note = new Notification(R.drawable.status, "$" + Integer.toString(money), System.currentTimeMillis());
			//note.defaults |= Notification.DEFAULT_SOUND;
			// don't wake me up!
			int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			if(!(hour >= GO_TO_BED_AT || hour <= WAKE_UP_AT))
			{
				note.sound = Uri.parse("file:///sdcard/money/money.mp3");
			}
			note.number = total;
			contentTitle = "Bling Bling";
			contentText = "You made $" + money + ", for a total of $" + total;

			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			note.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			mgr.notify(MONEY_ID, note);
		}

		abortBroadcast(); // stop the sms from going to the inbox
	}	

	private String getMessages(Bundle bundle){
		SmsMessage[] msgs = null;
		Object[] pdus = (Object[]) bundle.get("pdus");
		msgs = new SmsMessage[pdus.length];
		String msg = "";
		for (int i=0; i<msgs.length; i++){
			msg += SmsMessage.createFromPdu((byte[])pdus[i]).getMessageBody().toString() + "\n";  
		}
		return msg;
	}

	private int getNumber(String body)
	{
		Pattern intsOnly = Pattern.compile("-?\\d+");
		Matcher makeMatch = intsOnly.matcher(body);
		makeMatch.find();
		String inputInt = makeMatch.group();
		try {
			return Integer.parseInt(inputInt, 10);
		} catch(Exception e){
			return 0;
		}
	}

	private int updateTotal(int n, Context context){
		SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(context);
		int current_day = Calendar.getInstance().get(Calendar.DATE);
		SharedPreferences.Editor editor = pm.edit();
		
		int last_day = pm.getInt("day", 0);
		int total = pm.getInt("total", 0);

		// start of new day?s
		if(current_day != last_day){
			editor.putInt("day", current_day);
			total = 0;
		}

		total += n;
		// save the new total
		editor.putInt("total", total);
		editor.commit();

		return total;
	}
}