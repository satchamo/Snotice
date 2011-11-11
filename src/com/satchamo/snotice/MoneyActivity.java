package com.satchamo.snotice;

import com.satchamo.snotice.R;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

public class MoneyActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        updateText();
        NotificationManager mgr = (NotificationManager)getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        mgr.cancelAll();
    }
   
    public void updateText() {
    	SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(this);
        String n = pm.getString("total", "0.00");
        TextView tv = (TextView)findViewById(R.id.message);
        tv.setText("$" + n);
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	// Save UI state changes to the savedInstanceState.
    	// This bundle will be passed to onCreate if the process is
    	// killed and restarted.
    	updateText();
    	//super.onSaveInstanceState(savedInstanceState);
    }
    
    public void onClearClick(View v) {
    	SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(this);
    	SharedPreferences.Editor editor = pm.edit();
    	editor.putString("total", "0.00");
    	editor.commit();
    	updateText();
    }
}