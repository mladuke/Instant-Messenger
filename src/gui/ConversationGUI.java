package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * This is the class that manages a conversation. It manages user
 * inputs to the conversation, and it also manages the displaying
 * the contents of the conversation along with displaying the
 * individuals participating in said conversation.
 */
public class ConversationGUI extends JFrame implements ActionListener { 
    
    private final JButton leaveConvo;
    private final JTextField messageField;
    private final JButton enter;
    private final JList messages;
    private final DefaultListModel messagesModel;
    private final JScrollPane messagePane;
    private final DefaultListModel userListModel;
    private final JList userList;
    private final JScrollPane userListPane;
    private final JSplitPane usersAndMessages;
    private boolean connected = true;
    
    private final BackEndGUI model;
    /**
     * Initializes the conversation gui
     * @param gui The backendgui that it will communicate with.
     * @param name The name of the conversation.
     */
    public ConversationGUI(BackEndGUI gui, String name) {
        
        model = gui;
        // components
        this.setTitle(name);
        this.setName(name);
        this.setSize(500, 500);
        leaveConvo = new JButton();
        leaveConvo.setName("Leave Conversation");
        messageField = new JTextField();
        messageField.setActionCommand("enter");
        messageField.addActionListener(this);
        enter = new JButton();
        enter.setName("Enter");
        enter.setText("Enter");
        enter.setActionCommand("enter");
        enter.addActionListener(this);
        messagesModel = new DefaultListModel();
        messages = new JList(messagesModel);
        messagePane = new JScrollPane(messages);
        userListModel = new DefaultListModel();
        userList = new JList(userListModel);
        userListPane = new JScrollPane(userList);
        userListPane.setSize(100, 400);
        usersAndMessages = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, messagePane, userListPane);
        usersAndMessages.setDividerLocation(300);
        
        // layout
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(usersAndMessages)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(messageField)
                        .addComponent(enter))
                );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addComponent(usersAndMessages)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(messageField)
                        .addComponent(enter))
                );
        layout.linkSize(SwingConstants.VERTICAL, messageField, enter);
    }
    
    /**
     * displays a new message
     * @param message 
     */
    public void displayMessage(String message) {
        messagesModel.addElement(message);
        messages.ensureIndexIsVisible(messagesModel.getSize() - 1);
    }
    
    /**
     * adds a new user to the conversation
     * @param name
     */
    public void addUser(String name) {
        userListModel.addElement(name);
    }
    
    /**
     * removes a user from the conversation
     * @param name
     */
    public void removeUser(String name) {
        userListModel.removeElement(name);
    }
    /**
     * Makes the conversation window appear
     */
    public void connect() {
        connected = true;
        userListModel.clear();
        this.setVisible(true);
        messageField.setVisible(true);
        enter.setVisible(true);
        userListPane.setVisible(true);
    }
    /**
     * Disconnects from a conversation and makes the window disappear
     */
    public void disconnect() {
        connected = false;
        this.setVisible(false);
    }
    
    /**
     * Makes the conversation window of a previously closed conversation reappear.
     */
    public void openHistory() {
        this.setVisible(true);
        messageField.setVisible(false);
        enter.setVisible(false);
        userListPane.setVisible(false);
    }
    
    /**
     * sends a new message 
     * @param message
     */
    private void sendMessage(String message) {
        model.sendMessage(this.getTitle(), message);
    }
    
    /**
     * When enter is pressed, this triggers and calls the method to send a message.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("enter") && !messageField.getText().matches("[\\s]*")) {
            sendMessage(messageField.getText());
            messageField.setText("");
        }
    }

}
