package com.kodycodes.ChatClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

@SuppressWarnings("deprecation")
public class ChatClient implements Runnable{
	private Socket socket = null;
	private Thread thread = null;
	private DataInputStream console = null;
	private DataOutputStream streamOut = null;
	private ChatClientThread client = null;
	
	public ChatClient()
	{
		System.out.println("Connecting to server...");
		try{
			socket = new Socket("192.168.1.152", 1001);
			System.out.println("Connected to server: " + socket);
			start();
		}
		catch(UnknownHostException uhe)
		{
			System.out.println("Host unknown: " + uhe.getMessage());
		}
		catch(IOException ioe)
		{
			System.out.println("Unexpected exception: " + ioe.getMessage());
		}
	}
	
	
	@Override
	public void run() 
	{
		while(thread != null)
		{
			try{
				streamOut.writeUTF(console.readLine());
				streamOut.flush();
			}
			catch(IOException ioe)
			{
				System.out.println("Sending Error: " + ioe.getMessage());
				try{
					stop();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public void handle(String msg)
	{
		System.out.println(msg);
	}
	
	public void start() throws IOException
	{
		console = new DataInputStream(System.in);
		streamOut = new DataOutputStream(socket.getOutputStream());
		if(thread == null)
		{
			client = new ChatClientThread(this, socket);
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void stop() throws IOException
	{
		if(thread != null)
		{
			thread.stop();
			thread = null;
		}
		try{
			if(console != null)
				console.close();
			if(streamOut != null)
				streamOut.close();
			if(socket != null)
				socket.close();
		}
		catch(IOException ioe)
		{
			System.out.println("Error closing...");
		}
		client.close();
		client.stop();
	}
	
	public static void main(String[] args)
	{
		new ChatClient();
	}

}
