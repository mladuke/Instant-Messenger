package backEnd;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import main.Server;

import backEnd.ServerBackEnd;
import backEnd.Message.MessageType;

/**
 * ConversationBackEnd handles a single conversation. It keeps a list of users
 * and manages telling the user classes of new users and messages.
 */
public class ConversationBackEnd implements Runnable {
    private final List<String> userList = new ArrayList<String>();
    private final String convoName;
    private final BlockingQueue<Message> commands = new PriorityBlockingQueue<Message>();
    private final ServerBackEnd server;
    /**
     * This method initializes ConversationBackEnd
     * @param name This is the name of the conversation.
     * @param server This is the name of the ServerBackEnd instance that it is working with
     * @param user This is the name of the user that created this conversation
     */
    public ConversationBackEnd(String name,ServerBackEnd server, String user){
        this.convoName = name;
        this.server = server;
        userList.add(user);
    }
    /**
     * 
     * @return List of Users in the conversation
     */
    public List<String> viewUserList(){
    	List<String> userNameList = new ArrayList<String>();
        for (String user : this.userList){
            userNameList.add(user);
    	}
        return userNameList;
    }
    /**
     * This method adds a message object to the blocking queue of commands.
     * @param msg Message object outgoing from this class
     */
    public void addToQueue(Message msg){
        this.commands.add(msg);
    }
    /**
     * Goes through commands and calls appropriate methods to deal with each.
     */
    @Override
    public void run() {
        while (!(userList.isEmpty())){
            Message msg;
			try {
				msg = this.commands.take();
                if (msg.getType() ==  MessageType.CONVERSATION){
                    String newUser = msg.getSender();
                    if(msg.getMessage().equals("connect")){
                    	userList.add(newUser);
                    	this.server.userMap.get(newUser).addToQueue(new Message(MessageType.CONVERSATION, newUser,this.convoName, "connect"));
                        for (String user: userList){
                        	if (!(user.equals(newUser))){
                        		this.server.userMap.get(user).addToQueue(new Message(MessageType.SERVER,newUser,this.convoName,"addedUser"));
                        	}
                        }
                    }else if(msg.getMessage().equals("disconnect")) {
                        userList.remove(newUser);
                        if (server.userMap.get(newUser) != null) {
                            this.server.userMap.get(newUser).addToQueue(new Message(MessageType.CONVERSATION,newUser,this.convoName,"disconnect"));
                        }
                        for (String user: userList){
                            this.server.userMap.get(user).addToQueue(new Message(MessageType.SERVER,newUser,this.convoName,"removedUser"));
                        }

                    }
                }else if (msg.getType() == MessageType.MESSAGE){
                	String msgText = msg.getSender()+": "+msg.getMessage();
                	for (String u : userList){
                		this.server.userMap.get(u).addToQueue(new Message(MessageType.MESSAGE,u,this.convoName, msgText));
                	}
                }
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


            }
        
        synchronized (server.convMap) {
            this.server.convMap.remove(this.convoName);
        }
        for (UserBackEnd user: this.server.userMap.values()){
        	if (user != null) {
        	    user.addToQueue(new Message(MessageType.SERVER,user.getName(),this.convoName,"endedConv"));
        	}
        }
        Thread.currentThread().interrupt();
        return;
    }
    /**
     * Returns a string with the conversation name followed by the list of users
     */
    @Override public String toString(){
        return convoName + ": " + userList.toString();
    }
    /**
     * A getter for convoName
     * @return convoName The name of this conversation.
     */
    public String getName(){
    	return convoName;
    }
}