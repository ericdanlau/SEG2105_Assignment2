package edu.seg2105.server.ui;

import edu.seg2105.client.common.ChatIF;
import edu.seg2105.edu.server.backend.EchoServer;

import java.util.Scanner;
import java.io.*;


public class ServerConsole implements ChatIF {

	
	EchoServer server;
	
	final public static int DEFAULT_PORT = 5555;
	
	Scanner fromConsole;
	
	
	public ServerConsole(int port) {
		server = new EchoServer(port,this);
		
		fromConsole = new Scanner(System.in);
	}
	
	
	public void accept() {
		try
	    {

	      String message;

	      while (true) 
	      {
	        message = fromConsole.nextLine();
	        server.handleMessageFromServerUI(message);
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!");
	    }
	}
	
	
	
	@Override
	public void display(String message) {
		System.out.println("> " + message);
	}
	
	public static void main(String[] args) throws IOException 
	  {
	    int port = 0; //Port to listen on

	    try
	    {
	      port = Integer.parseInt(args[0]); //Get port from command line
	    }
	    catch(Throwable t)
	    {
	      port = DEFAULT_PORT; //Set port to 5555
	    }
		
	    ServerConsole sv = new ServerConsole(port);
	    sv.server.listen();
	    sv.accept();
	  }

}
