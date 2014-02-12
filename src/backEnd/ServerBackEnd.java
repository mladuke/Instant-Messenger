package backEnd;

import backEnd.Message.MessageType;
import backEnd.ServerBackEnd;
import backEnd.UserBackEnd;
import backEnd.ConversationBackEnd;
import backEnd.Message;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * ServerBackEnd is a class that deals with the connection of new users
 * and the handling of new conversations.
 */
public class ServerBackEnd {
    ServerSocket servSocket = null;
    private final BlockingQueue<Message> commands = new PriorityBlockingQueue<Message>();
    public HashMap<String, UserBackEnd> userMap = new HashMap<String, UserBackEnd>();
    public HashMap<String, ConversationBackEnd> convMap = new HashMap<String, ConversationBackEnd>();
    private boolean done = false;
    /**
     * Initializes a instance of ServerBackEnd
     * @param port The port that the server will be connecting to.
     */
    public ServerBackEnd(int port)
    {  try
       {  System.out.println("Binding to port " + port + ", please wait  ...");
          servSocket = new ServerSocket(port);  
          System.out.println("Server started: " + servSocket);
       }
       catch(IOException ioe)
       {  
           System.out.println(ioe); 
       }
    }
    /**
     * Creates a thread that pops elements off the blocking queue of
     * commands and executes them.
     * @throws IOException
     */
    public void serve() throws IOException{
        Thread queueThread = new Thread(new Runnable() {
            public void run() {
                while (!done) {
                    try {
                        executeCommand(commands.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        queueThread.start();
        while (true) {
            // block until a client connects
            final Socket socket = servSocket.accept();
            //we add the clients into our list while redefining the run methodx
            Thread a = new Thread(new UserBackEnd(socket, this));
            //start the client
            a.start();
        }
    }
    /**
     * Takes a command Message and handles the action that it requires. The
     * server currently only deals with messages about making conversations.
     * It did have other functionailty in a previous iteration.
     * @param command Message object that needs to be handled
     */
    private void executeCommand(Message command){
        //if (command.getMessage().equals("poll")){
            //String pollReturn = poll(Message.getSender()); 
            //userMap.get(command.getSender()).addToQueue(pollReturn);
        if (command.getMessage().equals("makeConvo")){
            String convName = command.getRecipient();
            String initUser = command.getSender();
            //if (!convMap.containsKey(convName)){
            ConversationBackEnd conv = new ConversationBackEnd(convName, this, initUser);
            convMap.put(convName, conv);
            Thread a = new Thread(conv);
            a.start();
            userMap.get(initUser).addToQueue(new Message(MessageType.CONVERSATION,initUser,convName,"connect"));
            for (UserBackEnd user : userMap.values()){
            	user.addToQueue(new Message(MessageType.SERVER,user.getName(),convName,"createdConv" ));
            }
        }
    }
    /**
     * Adds a user object to the hashmap userMap.
     * @param username
     * @param user
     * @return
     */
    public synchronized boolean addUser(String username, UserBackEnd user){
        if (userMap.get(username) == null){
            userMap.put(username, user);
            return true;
        } else {
            return false;
        }
    }
    /**
     * Adds a command to the BlockingQueue
     * @param m Message that needs to be executed
     */
    public void addToQueue(Message m) {
        commands.add(m);
    }
    
}
