// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI, String loginID) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      if (message.startsWith("#")) {
    	  handleCommand(message);
      } else {
    	  sendToServer(message);
      }
    	
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  private void handleCommand(String command) throws IOException {
	 
	  
	  
	  if (command.equals("#quit")) {
		  quit();
	  } else if (command.equals("#logoff")) {
		  this.closeConnection();
	  } else if (command.startsWith("#sethost ")) {
		  
		  if (!this.isConnected()) {
			  String host = command.substring(9).trim();
			  
			  if (!host.isEmpty()) {
				  this.setHost(host);
				  clientUI.display("Host set to " + host);
			  
			  } else {
				  clientUI.display("Error: Host cannot be empty");
			  }
		  } else {
			  clientUI.display("Error: Cannot set host while connected");
		  }
	  } else if (command.startsWith("#setport ")) {
		  
		  if (!this.isConnected()) {
			  String portStr = command.substring(9).trim();
			  
			  try {
				  int port = Integer.parseInt(portStr);
				  this.setPort(port);
				  clientUI.display("Port set to " + port);
			  } catch (NumberFormatException e) {
				  clientUI.display("Error: Invalid port input, port must be an integer");
			  }
		  } else {
			  clientUI.display("Error: Cannot set port while connected");
		  }
	  } else if (command.equals("#login")) {
		  if (!this.isConnected()) {
			  this.openConnection();
		  } else {
			  clientUI.display("Already Connected!");
		  }
	  } else if (command.equals("#gethost")) {
		  clientUI.display(this.getHost());
	  } else if (command.equals("#getport")) {
		  clientUI.display(Integer.toString(this.getPort()));
	  } else {
		  clientUI.display("Invalid Command");
	  }
	  
	  
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  /**
	 * Implements the hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  	@Override
	protected void connectionException(Exception exception) {
  		clientUI.display("The server is shut down");
  		quit();
	}
  	
  	/**
	 * Implements the hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  	@Override
	protected void connectionClosed() {
  		clientUI.display("Connection closed");
	}
  	
  	@Override
  	protected void connectionEstablished() {
  		String msg = "#login " + loginID;
  		
  		try {
  			this.sendToServer(msg);
  		} catch (IOException excep) {
  			clientUI.display("failed to send message");
  		}
  		
  	}
	
}
//End of ChatClient class
