package com.scpi;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import explib.ejPlot;
 
public class Logger extends Activity {
	int duration=30,length=0;
	float[] Y=new float[10000],X=new float[10000];
	Button button;
	Common comm;
	ejPlot ejplot;
	SeekBar timebase;
	String filename = new String();
	private Handler mHandler;
	private TextView msg;
	private EditText INTERVAL,YMIN,YMAX;
	private double ymin=0, ymax=1;
    private boolean running=false;
    private long start_time=0;
    
    public Builder about_dialog;
	private File dataDirectory;

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.logger, menu);
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
	    case R.id.save:
	    	//display_about_dialog();
	    	dumpToFile();
	    	break;
	    }
	    return true;
	}
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logger);
		Toast.makeText(getBaseContext(),"Data logger",Toast.LENGTH_SHORT).show();
		
		
		dataDirectory = new File(Environment.getExternalStorageDirectory()+"/SCPI_DATA_LOGGER/");
		Log.e("DIR",dataDirectory.getName());
		dataDirectory.mkdirs();
        
		about_dialog = new AlertDialog.Builder(this);
        
        about_dialog.setMessage("e-mail:jithinbp@gmail.com.\n https://github.com/jithinbp \n IISER Mohali, India");
        about_dialog.setTitle("Developed by Jithin B.P");
        about_dialog.setCancelable(true);
        
		comm=Common.getInstance();
     	setTitle(comm.title);
		msg = (TextView) findViewById(R.id.msg);
		INTERVAL = (EditText) findViewById(R.id.INTERVAL);
		YMIN = (EditText) findViewById(R.id.ymin);
		YMAX = (EditText) findViewById(R.id.ymax);
		
		TextView identity = (TextView) findViewById(R.id.identity);
		identity.setText(comm.identity);
     	LinearLayout plot=(LinearLayout)findViewById(R.id.plot);
     	
     	ejplot = new ejPlot(this, plot);
	    ejplot.xlabel="Time";
	    ejplot.ylabel="Units";
	    ejplot.setWorld(0, duration, 0, 30);
        
    	mHandler = new Handler();

		     	
	}
    
	public int toInt(EditText txt) {
		String val = txt.getText().toString();

		if (val == null || val.isEmpty()) {
			return 100;
		} else {
			return Integer.parseInt(val);
		}
	}

	public double toDouble(String str) {
		if (str == null || str.isEmpty()) {
			return 0.0;
		} else {
			return Double.parseDouble(str);
		}
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		running=false;
		
		Toast.makeText(getBaseContext(),"RETURNING TO MAIN MENU",Toast.LENGTH_SHORT).show();
	}
 
	public void autoscale(View v){
		if(length<2)return;
		
		ejplot.setWorld(0, duration, ymin,( Math.abs(ymax-ymin)<0.0001)?(ymax+0.0001):ymax );
		
	}
	
	public void start(View v){
		if(!comm.connected)return;
		length=0;
		start_time=System.currentTimeMillis();
		comm.interval=toInt(INTERVAL);
		duration=30;
		ymin = toDouble(YMIN.getText().toString());
		ymax = toDouble(YMAX.getText().toString());
		ejplot.setWorld(0, duration, ymin,( Math.abs(ymax-ymin)<0.0001)?(ymax+0.0001):ymax);
		if(!running){
			Toast.makeText(getBaseContext(),"Logging -> "+comm.command,Toast.LENGTH_SHORT).show();
			running=true;cro.run();}
	}
	
	
	
	
	Runnable cro = new Runnable() {  
	    @Override 
	    public void run() {
	    	if(!running || !comm.connected)return;
	    	if(!comm.hasData){mHandler.postDelayed(cro, 10);return;}
	    	msg.setText(comm.read);
	    	X[length]=(float) ((System.currentTimeMillis()-start_time)/1.0e3);
	    	Y[length]=(float) toDouble(comm.read);
	    	if(length==0){ymin=Y[length];ymax=Y[length];}
	    	else if(Y[length]<ymin)ymin=Y[length];
	    	else if(Y[length]>ymax)ymax=Y[length];
		    	
	    	
	    	if(X[length]>duration){
	    		duration+=10;
	    		ejplot.setWorld(0, duration, ymin, ymax);
	    	}
	    	if(length>9999){
	    		running=false;
	    		Toast.makeText(getBaseContext(),"Stopped logging !",Toast.LENGTH_SHORT).show();
	    		return;
	    	}
	    	ejplot.clearPlots();
			if(length>1)ejplot.line(X,Y,length,1);
			ejplot.updatePlots();
			length++;
			
			comm.hasData=false;
	    	mHandler.postDelayed(cro, comm.interval);
	    	
	      
	    }
	};

	
	
	
private void appendToFile(OutputStreamWriter writer,float[] x,float[] y,int length) throws IOException{
	for(int i=0;i<length;i++){writer.append(x[i]+" "+y[i]+"\n");}
	writer.append("\n");
	
}

public void dumpToFile(){
	SimpleDateFormat s = new SimpleDateFormat("dd-MM_hh-mm-ss");
	String format = s.format(new Date());
	Log.e("FILENAME",format+"");
	filename = format+".txt";
	Log.e("SAVING to ",""+filename);
	try {
    	File outputFile = new File(dataDirectory, filename);
  		outputFile.createNewFile();
		FileOutputStream fOut = new FileOutputStream(outputFile);
		OutputStreamWriter myOutWriter =  new OutputStreamWriter(fOut);
		appendToFile(myOutWriter,X,Y,length);
        myOutWriter.close();
        fOut.close();
        
        Toast.makeText(getBaseContext(), "Done writing to ./SCPI_DATA_LOGGER/" + filename + "",Toast.LENGTH_SHORT).show();
    } catch (Exception e) {
        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }
	
	
}


}
