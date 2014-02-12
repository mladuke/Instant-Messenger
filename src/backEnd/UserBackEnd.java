package backEnd;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.io.*;

import backEnd.ServerBackEnd;
import backEnd.Message.MessageType;

import main.Server;

/**
 * UserBackEnd message handles connection between the client and the server
 * for one individual user. It keeps track of the 
 *
 */
public class UserBackEnd implements Runnable {
    private String name;
    private List<String> membershipList;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ServerBackEnd server;
    private BlockingQueue<Message> messages;
    private boolean done = false;
    private boolean disconnected = false;
    
    /**
     * Initializes an instance of UserBackEnd
     * @param s The socket that the user will connect to.
     * @param server The ServerBackEnd object that this object will be working with.
     */
    public UserBackEnd(Socket s, ServerBackEnd server) {
        this.socket = s;
        this.membershipList = new ArrayList<String>();
        this.messages = new PriorityBlockingQueue<Message>();
        this.server = server;
    }
    /**
     * This method handles both sending messages to the client
     * and also processing messages that the client sends to
     * the Server. 
     */
    public void run() {
        Thread queueThread = new Thread(new Runnable() {
            public void run() {
                System.out.println("running...");
                while (!disconnected || membershipList.size() > 0) {
                    try {
                        publish(messages.take());
                    } catch (InterruptedException e) {
                        //e.printStackTrace(out);
                        disconnected = true;
                    } 
                }               
            }
        });
        queueThread.start();
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
            String username = in.readLine();
            while (!(server.addUser(username, this))) {
                out.println(username+ " InvalUserError");
                username = in.readLine();
            }
            this.name = username;
            out.println("Connected to Server.");
            String line = in.readLine();
            while (!done && line != null) {
                if (line.equals(name + " dcServ")) {
                    done = true;
                }
                else {
                    handleRequest(line);
                    line = in.readLine();
                }
            }
        } catch (Exception e) {
        } finally {
            synchronized (server.userMap) {
                server.userMap.put(name, null);
            }
                disconnected = true;
        }
    }
    /**
     * This method takes a line that's read from the socket, and sends it along to where it
     * needs to go in the server for it to be handled. 
     * @param request This is the one line message from the client that is being processed
     */
    private void handleRequest(String request) {
        String[] words = request.split(" ", 4);
        switch (words.length) {
            case 2:
                if (words[1].equals("pollConv")){
                	synchronized(server.convMap){
                	for (ConversationBackEnd convo: server.convMap.values()){
                		if (convo != null){
                			out.println(convo.getName()+" pollConvAdd");
                		}
                	}
                }
                } break;
            case 3:
                if (words[1].equals("newConv")) {
                	if (server.convMap.containsKey(words[2])){
                		out.println(words[2]+" newConvError");
                	}else{
                		server.addToQueue(new Message(MessageType.SERVER, words[2], name, "makeConvo"));
                	}
                }else if (words[1].equals("cConv")) {
                	if (server.convMap.containsKey(words[2])){
                		server.convMap.get(words[2]).addToQueue(new Message(MessageType.CONVERSATION, words[2], name, "connect"));
                	}else{
                		out.println(words[2]+" cConvError");
                	}
                }else if (words[1].equals("dcConv")) {
                	if (server.convMap.get(words[2])!= null){
                		server.convMap.get(words[2]).addToQueue(new Message(MessageType.CONVERSATION, words[2], name, "disconnect"));
                	}
                }else if (words[1].equals("pollUsers")) {
                	if (server.convMap.get(words[2])!= null){
                		synchronized(server.convMap.get(words[2])){
                		for (String user:server.convMap.get(words[2]).viewUserList() ){
                			out.println(words[2]+" pollUserAdd "+user);
                		}               	
                		}           
                	}
                } break;
            case 4:
            	if (server.convMap.get(words[2]) != null){
            		server.convMap.get(words[2]).addToQueue(new Message(MessageType.MESSAGE, words[2], name, words[3]));
            	}break;                
            default:
                break;
        }
    }
    
    /**
     * Adds a message to the blocking queue of messages to be processed
     * @param m Message to be processed
     */
    public void addToQueue(Message m) {
        messages.add(m);
    }
    
    /**
     * Writes to the socket. It sends information from the server to the client.
     * @param m Message to be sent.
     */
    private void publish(Message m) {
       switch (m.getType()) {
       case SERVER:
    	   if (m.getMessage().equals("addedUser")){
    		   out.println(m.getSender()+" pollUserAdd "+m.getRecipient());
    	   }else if (m.getMessage().equals("removedUser")){
    		   out.println(m.getSender()+" pollUserDel "+m.getRecipient());
    	   }else if (m.getMessage().equals("createdConv")){
    	   		out.println(m.getSender()+" pollConvAdd");
    	   }else if (m.getMessage().equals("endedConv")){
    		   out.println(m.getSender() +" pollConvDel");
    	   }else{
    		   out.println(m.getMessage());
    	   }
           break;
       case CONVERSATION:
           if (m.getMessage().equals("disconnect")) {
               membershipList.remove(m.getSender());
               out.println(m.getSender()+" convDC");
           }
           else if (m.getMessage().equals("connect")) {
               membershipList.add(m.getSender());
               out.println(m.getSender() + " convJoin");
           }
           break;
       case MESSAGE:
           out.println(m.getSender() + " recMsg " + m.getMessage());
           break;
       default:
           break;
       }
    }
    
    /**
     * Simple getter for name attribute
     * @return name of the User handled by this instance of UserBackEnd
     */
    public String getName(){
    	return this.name;
    }
}
