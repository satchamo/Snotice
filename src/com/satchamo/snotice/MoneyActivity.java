package com.satchamo.snotice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.satchamo.snotice.R;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MoneyActivity extends Activity {
    private static final String COPIED_AUDIO = "COPIED_AUDIO";
    private static final String SOUND_FOLDER = "snotice/";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        updateText();
        NotificationManager mgr = (NotificationManager)getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        mgr.cancelAll();
        
        // copy audio to sd card
        if(canWriteToSdCard()){
        	SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        	boolean copied_audio = p.getBoolean(COPIED_AUDIO, false);
        	if(!copied_audio) {
        		// run in another thread?
        		copyAssets();
        		p.edit().putBoolean(COPIED_AUDIO, true).commit();
        	}
        }
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
    
    private boolean canWriteToSdCard(){
    	 boolean externalStorageAvailable = false;
         boolean externalStorageWriteable = false;
         String state = Environment.getExternalStorageState();

         if (Environment.MEDIA_MOUNTED.equals(state)) {
             // We can read and write the media
             externalStorageAvailable = externalStorageWriteable = true;
         } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
             // We can only read the media
             externalStorageAvailable = true;
             externalStorageWriteable = false;
         } else {
             // Something else is wrong. It may be one of many other states, but all we need
             //  to know is we can neither read nor write
             externalStorageAvailable = externalStorageWriteable = false;
         }        
         return externalStorageWriteable;
    }
    
    private void copyAssets() {
    	File dir = new File("/sdcard/" + SOUND_FOLDER);
    	// have the object build the directory structure, if needed.
    	dir.mkdirs();
    	
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        for(String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
              in = assetManager.open(filename);
              out = new FileOutputStream("/sdcard/" + SOUND_FOLDER + filename);
              copyFile(in, out);
              in.close();
              in = null;
              out.flush();
              out.close();
              out = null;
            } catch(Exception e) {
                Log.e("tag", e.getMessage());
            }       
        }
    }
    
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
          out.write(buffer, 0, read);
        }
    }

}