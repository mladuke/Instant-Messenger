package gui;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * BackEndGUI
 * This class manages the sockets and communication that is done client side. It
 * contains methods for sending various types of messages, and also methods for
 * reading messages and modifying the gui appropriately based on what is recieved.
 **/
public class BackEndGUI {
	String host;
	int port;
	Socket connection;
    private BufferedReader in;
    private PrintWriter out;
    String uid;

	boolean done = false;
	MainGUI gui;
	
	private Thread inputProcessing;
	
	public HashMap<String, ConversationGUI> convMap = new HashMap<String, ConversationGUI>();
	
	/**
	 * Constructor for BackEndGUI class
	 * It doesn't take any parameters. It opens the window for the gui and makes it visible.
	 **/
	public BackEndGUI() {
	    gui = new MainGUI(this);
	    gui.setVisible(true);
	}
	/**
	 * This method connects to the server using the port number and host name specified.
	 * @param host A string that specifies the server's address(ip address, "localhost", etc.)
	 * @param port An integer that specifies the port number to connect to
	 * @return It returns whether the connect to server was successful.
	 */
	public boolean connectToServer(String host, int port){
        System.out.println("SocketClient initialized");
        /** Obtain an address object of the server*/
        InetAddress address;
		try {
			address = InetAddress.getByName(host);
        /** Establish a socket connection*/
        connection = new Socket(address, port);
        this.in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        this.out = new PrintWriter(connection.getOutputStream(), true);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	

	/**
	 * Attempts to connect with a certain username. It will return
	 * whether that uid was claimed successfully.
	 * @param uid Username that is attempting to be claimed.
	 * @return It returns whether that username was claimed successfully.
	 */
	public boolean checkUsername(String uid){
	    out.println(uid);
	    try {
            String response = in.readLine();
            String[] rsplit = response.split(" ", 3);
            if (rsplit[1].equals("InvalUserError")){
                return false;
            } else {
                pollConv();
                return true;
            }
        } catch (IOException e) {
            System.out.println("Check username Exception");
            return false;
        }
	}
	/**
	 * Sends a message from the user to a conversation that they're in.
	 * @param conv The conversation that is being sent to.
	 * @param message The message that is being sent.
	 */
	public void sendMessage(String conv, String message){
		this.out.println(uid + " sndMsg " + conv + " " + message);
	}
	/**
	 * Tells server that the user is attempting to connect to a certain conversation.
	 * @param conv The conversation that the user attempting to connect to.
	 */
	public void cConv(String conv){
	    if (convMap.get(conv) == null || !convMap.get(conv).isVisible()) {
	        this.out.println(uid + " cConv " + conv);
	    }
	}
	/**
	 * Tells the server to disconnect the user from a certain conversation.
	 * @param conv Conversation that user is attempting to disconnect from.
	 */
	public void dcConv(String conv){
	    this.out.println(uid + " dcConv " + conv);
	    convMap.get(conv).disconnect();
	}
	/**
	 * Requests the userlist for a certain conversation.
	 * @param conv Conversation that we are requesting the userlist for.
	 */
	public void pollUsers(String conv){
	    this.out.println(uid + " pollUsers " + conv);
	}
	/**
	 * Requests for a list of conversation names.
	 */
	public void pollConv(){
	    this.out.println(uid+ " pollConv");
	    processInputs();
	}
	/**
	 * Sends requst to open new conversation
	 * @param conv Name of the conversation that is attempting to be creating.
	 */
	public void newConversation(String conv){
	    this.out.println(uid + " newConv " + conv);
	}
	/**
	 * Notifies server that the user is disconnecting from it.
	 */
	public void disconnect() {
	    for (ConversationGUI convo : convMap.values()) {
	        if (convo != null) {
	            dcConv(convo.getTitle());
	        }
	    }
	    this.out.println(uid + " dcServ");
	    try {
            this.connection.close();
        } catch (IOException e) {
        }
	    System.exit(0);
	}
	
	private void serverDisconnect() {
	    for (ConversationGUI convo : convMap.values()) {
            if (convo != null) {
                dcConv(convo.getTitle());
            }
        }
	    gui.setVisible(false);
	    try {
            this.connection.close();
        } catch (IOException e) {
        }
	    System.exit(0);
	}
	/**
	 * Opens up a window that shows the past messages in conversation conv.
	 * @param conv The conversation that we are attempting to get history of.
	 */
	public void viewHistory(String conv) {
	    convMap.get(conv).openHistory();
	}
	/**
	 * Reads from the socket, and processes
	 * incoming information.
	 */
	public void processInputs(){
	    this.inputProcessing = new Thread(new Runnable() {
            public void run() {
                while (!done) {
                    String line;
                    try {
                        line = in.readLine();
                        processLine(line);
                    } catch (IOException e) {
                        done = true;
                        serverDisconnect();
                    }
                }
            }
        });
        inputProcessing.start();
	}
	/**
	 * Processes a line of information from the server. It will call
	 * the appropriate methods to modify the gui in response to the
	 * inputs in accordance with the protocol.
	 * @param line The line of information being processed.
	 */
	public void processLine(String line){
        String[] rsplit = line.split(" ", 3);
        String command = rsplit[1];
	    if (command.equals("pollConvAdd")){
	        gui.addConversation(rsplit[0]);
	    } else if (command.equals("pollConvDel")){
	        gui.removeConversation(rsplit[0]);
	    } else if (command.equals("pollUserAdd")){
	        convMap.get(rsplit[0]).addUser(rsplit[2]);
	    } else if (command.equals("pollUserDel")){
	        convMap.get(rsplit[0]).removeUser(rsplit[2]);
	    } else if (command.equals("cConvError")){
	        gui.displayError("Could connect to conversation. Please try again.");
	    } else if (command.equals("newConvError")){
	        gui.displayError("This conversation already exists.");
	    } else if (command.equals("convDC")){
	        convMap.get(rsplit[0]).disconnect();
	        gui.archiveConversation(rsplit[0]);
	    } else if (command.equals("convJoin")){
	        if (!convMap.containsKey(rsplit[0])) {
	            convMap.put(rsplit[0], new ConversationGUI(this, rsplit[0]));
	            convMap.get(rsplit[0]).addWindowListener(new ConversationListener(this));
	        }
	        convMap.get(rsplit[0]).connect();
	        pollUsers(rsplit[0]);
	    } else if (command.equals("servDC")){
	        gui.disconnectServer();
	        done = true;
	    } else if (command.equals("recMsg")){
	        convMap.get(rsplit[0]).displayMessage(rsplit[2]);
	    }
	}
}
