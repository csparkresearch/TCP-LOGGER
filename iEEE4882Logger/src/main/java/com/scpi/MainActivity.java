package com.scpi;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Socket socket;
	EditText ip,port,command;
	private static final int SERVERPORT = 8889;
	private static final String SERVER_IP = "10.0.0.13";
	Common comm;
	public Builder about_dialog;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    //MenuItem refresh = menu.getItem(R.id.menu_refresh);
	    //refresh.setEnabled(true);
	    return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch(item.getItemId())	    
	    {
	    case R.id.credits:
	    	//display_about_dialog();
	    	about_dialog.show();
	    	break;
	    }
	    return true;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		



		ip = (EditText) findViewById(R.id.ip);
		port = (EditText) findViewById(R.id.port);
		command = (EditText) findViewById(R.id.command);
        about_dialog = new AlertDialog.Builder(this);
        
        about_dialog.setMessage("e-mail:jithinbp@gmail.com.\n https://github.com/jithinbp \n IISER Mohali, India");
        about_dialog.setTitle("Developed by Jithin B.P");
        about_dialog.setCancelable(true);
        
		//new Thread(new ClientThread()).start();

	}
	public int toInt(EditText txt) {
		String val = txt.getText().toString();

		if (val == null || val.isEmpty()) {
			return 8888;
		} else {
			return Integer.parseInt(val);
		}
	}
    @Override
    protected void onResume(){
    	super.onResume();
    	final Context context = this;
    	comm=Common.getInstance();
    	
		Button logger_button = (Button) findViewById(R.id.load_logger);
 		logger_button.setOnClickListener(new OnClickListener() {
 			@Override
			public void onClick(View v) {
    				String h = ip.getText().toString();
    				String c = command.getText().toString();
    				Log.e("host",h);
    				comm.make_connection(h,toInt(port),c);
    				SystemClock.sleep(1000);
    				if(comm.connected){
    					Intent intent = new Intent(context, Logger.class);
    					startActivity(intent);   
    				}
    				else{
    					Toast.makeText(getBaseContext(),"No device found. check ip address and port.",Toast.LENGTH_SHORT).show();
    				}
 			
 			}
 		});
	
    }
	class ClientThread implements Runnable {

		@Override
		public void run() {

			try {
				InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

				socket = new Socket(serverAddr, SERVERPORT);

			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

	}



}
