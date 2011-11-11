package com.satchamo.snotice;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import com.satchamo.snotice.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

public class MoneyNotice extends Notice {
	protected static final String SOUND_FILE = "file:///sdcard/snotice/money.mp3";
	protected static final int ICON = R.drawable.status;
	protected static final int ID = 1338;
	
	// specify the times you want to silence the sound (in military hours)
	private static final int GO_TO_BED_AT = 22;
	private static final int WAKE_UP_AT = 9;
	
	BigDecimal amount;
	BigDecimal total;
	
	public MoneyNotice(HashMap<String, String> vars, Context context) {
		super(vars, context);
		amount = new BigDecimal(vars.get("amount"));
		total = updateTotal(amount);
	}
	
	public int getId() {
		return ID;
	}
	
	public int getIcon() {
		return R.drawable.icon;
	}

	public Uri getSound() {
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		// don't wake me up!
		if(!(hour >= GO_TO_BED_AT || hour <= WAKE_UP_AT)) {
			return Uri.parse(SOUND_FILE);
		}
		return null;
	}
	
	public int getNumber() {
		return total.intValue();
	}	

	public String getStatusBarTitle() {
		return "$" + amount.toPlainString();
	}
	
	public String getNotificationTitle() {
		return "You just made $" + amount.toPlainString();
	}
		
	public String getNotificationText() {
		return "Your total is $" + total.toPlainString();
	}
	
	public Class<?> getIntent() {
		return MoneyActivity.class;
	}	
	
	private BigDecimal updateTotal(BigDecimal amount) {
		SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(context);
		int current_day = Calendar.getInstance().get(Calendar.DATE);
		SharedPreferences.Editor editor = pm.edit();
		
		int last_day = pm.getInt("day", 0);
		total = new BigDecimal(pm.getString("total", "0.00"));

		// start of new day?
		if(current_day != last_day) {
			editor.putInt("day", current_day);
			total = new BigDecimal("0.00");
		}

		total = total.add(amount);
		// save the new total
		editor.putString("total", total.toString());
		editor.commit();

		return total;
	}
}