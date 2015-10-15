package com.kodycodes.ChatClient;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClientThread extends Thread{
	private ChatClient client = null;
	private Socket socket = null;
	private DataInputStream streamIn = null;
	
	public ChatClientThread(ChatClient client, Socket socket)
	{
		this.client = client;
		this.socket = socket;
		open();
		start();
	}
	
	public void open()
	{
		try{
			streamIn = new DataInputStream(socket.getInputStream());
		}
		catch(IOException ioe)
		{
			 System.out.println("Error getting input stream: " + ioe);
	         try{
				client.stop();
			} 
	        catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public void close()
	{
		try{
			if(streamIn != null)
				streamIn.close();
		}
		catch(IOException ioe)
		{
			System.out.println("Error closing input stream: " + ioe);
		}
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			try{
				client.handle(streamIn.readUTF());
			}
			catch(IOException ioe)
			{
				System.out.println("Listening error: " + ioe.getMessage());
	            try{
					client.stop();
				} 
	            catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
}
