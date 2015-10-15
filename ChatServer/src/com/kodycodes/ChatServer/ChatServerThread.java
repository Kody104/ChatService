package com.kodycodes.ChatServer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@SuppressWarnings("deprecation")
public class ChatServerThread extends Thread {
	private String nick = null;
	private int state = 0;
	private ChatServer server = null;
	private Socket socket = null;
	private int ID = -1;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
	
	public ChatServerThread(ChatServer server, Socket socket)
	{
		super();
		this.server = server;
		this.socket = socket;
		ID = socket.getPort();
	}
	
	public void send(String msg)
	{
		try{
			streamOut.writeUTF(msg);
			streamOut.flush();
		}
		catch(IOException ioe)
		{
			System.out.println(ID + "ERROR sending: " + ioe.getMessage());
			stop();
		}
	}
	
	public boolean hasNick()
	{
		if(nick != null)
			return true;
		return false;
	}
	
	public void setNick(String nick)
	{
		this.nick = nick;
	}
	
	public String getNick()
	{
		return nick;
	}
	
	public int getID()
	{
		return ID;
	}
	
	@Override
	public void run()
	{
		System.out.println("Server Thread " + ID + " is running.");
		while(true)
		{
			if(state == 0)
				send("Enter your nickname.");
			if(state == 10)
				send("Enter your password.");
			if(state == 11)
				send("Enter the user's nickname");
			try{
				state = server.handle(ID, streamIn.readUTF(), state);
			}
			catch(IOException ioe)
			{
				System.out.println(ID + "ERROR reading: " + ioe.getMessage());
				server.remove(ID);
				stop();
			}
		}
	}
	
	public void open() throws IOException
	{
		streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	}
	
	public void close() throws IOException
	{
		if(socket != null) 
			socket.close();
		if(streamIn != null)
			streamIn.close();
		if(streamOut != null)
			streamOut.close();
	}

}
