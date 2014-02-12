package backEnd;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Message class that allows for easier communication within the server. The
 * abstraction allows for easier use of information in the messages.
 */
public class Message implements Comparable<Message>{
    
	/**
	 * Relevant section of the server for the message to be processed
	 *
	 */
    public enum MessageType {
    	/** message to be processed in serverBackEnd*/
        SERVER,
        /** message to be processed in ConversationBackEnd*/
        CONVERSATION,
        /** message type that will be sent transfered between the UserBackEnd and ConversationBackEnd
         * representing messages that the users of the chat conversations are sending to each other.
         */
        MESSAGE
    }
    
    private final MessageType type;
    private final String recipient;
    private final String sender;
    private final String message;
    private final static AtomicLong seq = new AtomicLong();
    private final long currSeq;
    /**
     * Initializes a Message object
     * @param type The type describes the general areas that the message will be interacting with
     * @param recipient The recipient of the message.
     * @param sender The sender of the message.
     * @param message The content of the message itself
     */
    public Message(MessageType type, String recipient, String sender, String message) {
        this.type = type;
        this.recipient = recipient;
        this.sender = sender;
        this.message = message;
        this.currSeq = seq.incrementAndGet();
    }
    /**
     * @return type of message
     */
    public MessageType getType(){
    	return this.type;
    }
    /**
     * @return recipient of message
     */
    public String getRecipient(){
    	return this.recipient;
    }
    /**
     * @return sender of message
     */
    public String getSender(){
    	return this.sender;
    }
    /**
     * @return current sequence
     */
    public long getCurrSeq(){
    	return this.currSeq;
    }
    /**
     * @return message The content of the message
     */
    public String getMessage(){
    	return this.message;
    }
    
    /**
     * Compares this message's location in the queue
     * Useful for: Determining execution order.
     */
    public int compareTo(Message other){
    	if (this.currSeq > other.getCurrSeq()){
    		return 1;
    	}
    	return -1;
    }

   
    /*
    Comparator Class, don't think its needed:
    
    
    package backEnd;

	import java.util.Comparator;
	
	public class Compare implements Comparator<Message>{


	public int compare(Message arg0, Message arg1) {
		if (arg0.getCurrSeq() > arg1.getCurrSeq()){
			return 1;
		}return -1;
	
	}


	}
     */
    
    
}
