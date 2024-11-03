package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import ocsf.server.*;
import edu.seg2105.client.common.ChatIF;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
	String loginKey = "loginID";
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  ChatIF serverUI;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port,ChatIF serverUI)
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    System.out.println("Message received: " + msg + " from " + client.getInfo(loginKey));
    
    String msgStr = (String)msg;
    if (msgStr.startsWith("#login ")) {
    	
    	if (!(client.getInfo(loginKey) == null)) {
    		String terminateMsg = "Error: #login only allowed as first command";
    		
    		try {
    			client.sendToClient(terminateMsg);
    			client.close();
    		} catch (IOException e) {}
    	} else {
    		String loginID = msgStr.substring(7).trim();
        	client.setInfo(loginKey, loginID );
        	serverUI.display(loginID + " has logged on");
    		
    	}
    	
    	
    } else {
    	String loginID = (String) client.getInfo(loginKey);
    	String message = loginID +"> " + msgStr;
    	this.sendToAllClients(message);
    }
  
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  /**
	 * Implements the hook method called each time a new client connection is
	 * accepted. The default implementation does nothing.
	 * @param client the connection connected to the client.
	 */
  	@Override
	protected void clientConnected(ConnectionToClient client) {
		System.out.println("Client Connected");
	}

	/**
	 * Implements the hook method called each time a client disconnects.
	 * The default implementation does nothing. The method
	 * may be overridden by subclasses but should remains synchronized.
	 *
	 * @param client the connection with the client.
	 */
	@Override
	synchronized protected void clientDisconnected(
		ConnectionToClient client) {
		System.out.println("Client Disconneced");
	}
	
	public void quit() {
		System.exit(0);
	}
	
	/**
	 * Implements the hook method called each time an exception is thrown in a
	 * ConnectionToClient thread.
	 * The method may be overridden by subclasses but should remains
	 * synchronized.
	 *
	 * @param client the client that raised the exception.
	 * @param Throwable the exception thrown.
	 */
	synchronized protected void clientException(
		ConnectionToClient client, Throwable exception) {
		
		 clientDisconnected(client);
	}
  
	public void handleMessageFromServerUI (String message) throws IOException {
		if (message.startsWith("#")) {
			handleCommand(message);
		} else {
			serverUI.display(message);
			sendToAllClients("SERVER MSG> " + message);
		}
		
	}
	
	private void handleCommand(String command) throws IOException {
		if (command.equals("#quit")) {
			this.quit();
		} else if (command.equals("#stop")) {
			this.stopListening();
		} else if (command.equals("#close")) {
			this.close();
		} else if (command.startsWith("#setport ")) {
			
			if (!this.isListening() && this.getNumberOfClients() == 0) {
				String portStr = command.substring(9).trim();
				
				try {
					  int port = Integer.parseInt(portStr);
					  this.setPort(port);
					  serverUI.display("Port set to " + port);
				  } catch (NumberFormatException e) {
					  serverUI.display("Error: Invalid port input, port must be an integer");
				  }
			  } else {
				  serverUI.display("Error: Cannot set port while connected");
			}
		  	
		} else if (command.equals("#start")) {
			if (!this.isListening()) {
				this.listen();
			}
		} else if (command.equals("#getport")) {
			serverUI.display(Integer.toString(this.getPort()));
		} else {
			serverUI.display("Invalid Command");
		}
	}
	
	
	
	
  
}
//End of EchoServer class
