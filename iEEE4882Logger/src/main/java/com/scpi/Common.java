package com.scpi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.SystemClock;
import android.util.Log;



public class Common{
	   private static Common instance = null;
	   private long timestamp;
	   public String ip=new String();
	   public String command=new String();
	   public String identity=new String();
	   String title="Data logger",read="";
	   public int port=8888,interval=100;
	   public Socket socket;
	   public InetAddress serverAddr;
	   public boolean connected=false,hasData=false;
	   protected Common() {
	      // Exists only to defeat instantiation.
		   timestamp = System.currentTimeMillis();
	   }
	    public boolean make_connection(String h,int p,String c){
	    	ip=h;
	    	port=p;
	    	command=c;
	    	new Thread(new ClientThread()).start();
	    	return true;
	    }
	    
	    public static Common getInstance() {
	      if(instance == null) {
	         instance = new Common();
	      }
	      return instance;
	   }
	    
	    
		class ClientThread implements Runnable {

			private BufferedReader input;
			private BufferedWriter output;

			@Override
			public void run() {

				try {
					serverAddr = InetAddress.getByName(ip);
					socket = new Socket(serverAddr, port);
					connected=true;
					this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

					output.append("*IDN?"+'\n');
					output.flush();
					identity = input.readLine();

					while (!Thread.currentThread().isInterrupted()) {
						if(!connected){SystemClock.sleep(100); continue;}
						try {
							while(hasData)SystemClock.sleep(10);;
							//Log.e("writing", command+"\n");
							output.append(command+'\n');
							output.flush();
							read = input.readLine();
							hasData=true;//updateConversationHandler.post(new updateUIThread(read));
							//Log.e("read",read);

						} catch (IOException e) {
							hasData=true;
							connected=false;
							e.printStackTrace();
						}
					}
				

					
					
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}

		}
		
		
		
		
		
	    
}

