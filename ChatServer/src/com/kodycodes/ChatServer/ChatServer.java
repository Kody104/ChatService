package com.kodycodes.ChatServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("deprecation")
public class ChatServer implements Runnable{
	private Thread thread = null;
	private ServerSocket server = null;
	private ChatServerThread[] clients = new ChatServerThread[10];
	private int clientCount = 0;
	
	public ChatServer()
	{
		try{
			System.out.println("Server is starting on port 1001...");
			server = new ServerSocket(1001);
			System.out.println("Server started successfully! " + server);
			start();
		}
		catch(IOException ioe)
		{
			System.out.println("Couldn't bind to port 1001: " + ioe.getMessage());
		}
	}
	
	@Override
	public void run() 
	{
		while(thread != null)
		{
			try{
				addNewThread(server.accept());
			}
			catch(IOException ioe)
			{
				System.out.println("Server accept error: " + ioe.getMessage());
				stop();
			}
		}
	}
	
	public synchronized int handle(int ID, String input, int state)
	{	
		if(state == 0)
		{
			for(int i = 0; i < clientCount; i++)
			{
				if(clients[i].hasNick())
				{
					if(clients[i].getNick().equalsIgnoreCase(input))
					{
						clients[findPlayer(ID)].send("That nickname already exists!");
						return state;
					}
				}
			}
			clients[findPlayer(ID)].setNick(input);
			clients[findPlayer(ID)].send("Nickname accepted.");
			return 1;
		}
		
		if(state == 10)
		{
			clients[findPlayer(ID)].send("Password accepted.");
			return 1;
		}
		
		if(state == 11)
		{
			for(int i = 0; i < clientCount; i++)
			{
				if(clients[i].getNick().equalsIgnoreCase(input))
				{
					clients[i].send("You've been kicked!");
					remove(clients[i].getID());
					clients[findPlayer(ID)].send(input + " has been kicked successfully!");
					return 1;
				}
			}
			clients[findPlayer(ID)].send("Couldn't find "+ input + ".");
			return 1;
		}
		
		if(input.equalsIgnoreCase(".close"))
		{
			for(int i = 0; i < clientCount; i++)
				clients[i].send(clients[findPlayer(ID)].getNick() + " has closed his client.");
			remove(ID);
			return state;
		}
		else if(input.equalsIgnoreCase(".who"))
		{
			clients[findPlayer(ID)].send("There are currently " + clientCount + " online: ");
			for(int i = 0; i < clientCount; i++)
				clients[findPlayer(ID)].send(clients[i].getNick());
			return state;
		}
		else if(input.equalsIgnoreCase(".kick"))
		{
			if(clients[findPlayer(ID)].getNick().equalsIgnoreCase("[admin]kody"))
				return 11;
			else
			{
				clients[findPlayer(ID)].send("You don't have the permission to use this command!");
				return state;
			}
		}
		
		for(int i = 0; i < clientCount; i++)
			clients[i].send(clients[findPlayer(ID)].getNick() + ": " + input);
		return state;
	}
	
	public synchronized void remove(int ID)
	{
		int pos = findPlayer(ID);
		if(pos >= 0)
		{
			ChatServerThread toTerminate = clients[pos];
			System.out.println("Removing player thread " + ID + " at " + pos);
			if(pos < clientCount - 1)
			{
				for(int i = pos+1; i < clientCount; i++)
					clients[i-1] = clients[i];
			}
			clientCount--;
			try{
				toTerminate.close();
			}
			catch(IOException ioe)
			{
				System.out.println("Error closing thread: " + ioe);
			}
			toTerminate.stop();
		}
	}
	
	private int findPlayer(int ID)
	{
		for(int i = 0; i < clientCount; i++)
		{
			if(clients[i].getID() == ID)
				return i;
		}
		return -1;
	}
	
	public void start()
	{
		if(thread == null)
		{
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void stop()
	{
		if(thread != null)
		{
			thread.stop();
			thread = null;
		}
	}
	
	public static void main(String[] args)
	{
		new ChatServer();
	}
	
	public void addNewThread(Socket socket)
	{
		if(clientCount < clients.length)
		{
			System.out.println("Client connected: " + socket);
			clients[clientCount] = new ChatServerThread(this, socket);
			try{
				clients[clientCount].open();
				clients[clientCount].start();
				clientCount++;
			}
			catch(IOException ioe)
			{
				System.out.println("Error opening thread: " + ioe);
			}
			System.out.println("Number of clients online: " + clientCount);
		}
	}
	

}
