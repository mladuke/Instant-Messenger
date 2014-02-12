package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
 * This gui class is the one that the user initially interacts with and allows
 * them to connect to the server and conversations.
 */
public class MainGUI extends JFrame implements ActionListener {
    
    private final JLabel connectionStatus;
    private boolean connected = false;
    private final JLabel errorBar;
    private final JButton disconnect;
    
    private final JPanel connectionPanel;
    private final JTextField ip;
    private final JTextField port;
    private final JButton connectToServer;
    
    private final JPanel usernamePanel;
    private final JTextField username;
    private final JButton enterUsername;
    
    private final JPanel convPanel;
    private final JTextField newConvName;
    private final JButton newConv;
    private final JButton joinConv;
    private final JList openConvList;
    private final DefaultListModel openConvModel;
    private final JButton viewHistory;
    private final JList pastConvList;
    private final DefaultListModel pastConvModel;
    
    private final GroupLayout mainLayout;
    
    private final BackEndGUI model;
    /**
     * Initiallizes an instance of the main gui.
     * @param model The backendgui that the Main gui will work with for communications to server
     */
    public MainGUI(BackEndGUI model) {
        this.model = model;
        this.setSize(500, 500);
        connectionStatus = new JLabel();
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {
                MainGUI.this.model.disconnect();
                MainGUI.this.setVisible(false);
            }
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        connectionStatus.setText("not connected");
        errorBar = new JLabel("");
        disconnect = new JButton("Disconnect");
        disconnect.setActionCommand("disconnect");
        disconnect.addActionListener(this);
        disconnect.setVisible(false);
        
        
        // connection panel
        connectionPanel = new JPanel();
        ip = new JTextField();
        port = new JTextField();
        port.setActionCommand("connectToServer");
        port.addActionListener(this);
        JLabel ipLabel = new JLabel("IP:");
        JLabel portLabel = new JLabel("Port:");
        connectToServer = new JButton();
        connectToServer.setText("Connect");
        connectToServer.setActionCommand("connectToServer");
        connectToServer.addActionListener(this);
        
        // connection panel layout
        GroupLayout connectionLayout = new GroupLayout(connectionPanel);
        connectionPanel.setLayout(connectionLayout);
        connectionLayout.setAutoCreateGaps(true);
        connectionLayout.setAutoCreateContainerGaps(true);
        connectionLayout.setHorizontalGroup(
                connectionLayout.createSequentialGroup()
                .addComponent(ipLabel)
                .addComponent(ip)
                .addComponent(portLabel)
                .addComponent(port)
                .addComponent(connectToServer)
                );
        connectionLayout.setVerticalGroup(
                connectionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(ipLabel)
                .addComponent(ip)
                .addComponent(portLabel)
                .addComponent(port)
                .addComponent(connectToServer)
                );
        connectionLayout.linkSize(SwingConstants.VERTICAL, ipLabel, ip, portLabel, port, connectToServer);
        
        // username panel 
        username = new JTextField();
        username.setActionCommand("enterUsername");
        username.addActionListener(this);
        enterUsername = new JButton();
        enterUsername.addActionListener(this);
        enterUsername.setText("Enter");
        enterUsername.setActionCommand("enterUsername");
        usernamePanel = new JPanel();
        
        // username panel layout
        GroupLayout usernameLayout = new GroupLayout(usernamePanel);
        usernamePanel.setLayout(usernameLayout);
        usernameLayout.setAutoCreateGaps(true);
        usernameLayout.setAutoCreateContainerGaps(true);
        
        usernameLayout.setHorizontalGroup(
                usernameLayout.createSequentialGroup()
                .addComponent(username)
                .addComponent(enterUsername)
                );
        usernameLayout.setVerticalGroup(
                usernameLayout.createParallelGroup()
                .addComponent(username)
                .addComponent(enterUsername)
                );
        usernameLayout.linkSize(SwingConstants.VERTICAL, username, enterUsername);
        
        // conversation panel
        convPanel = new JPanel();
        newConvName = new JTextField();
        newConvName.setActionCommand("newConv");
        newConvName.addActionListener(this);
        newConv = new JButton("New Conversation");
        newConv.setActionCommand("newConv");
        newConv.addActionListener(this);
        JLabel openConversations = new JLabel("Open Conversations: ");
        joinConv = new JButton("Join");
        joinConv.setActionCommand("joinConv");
        joinConv.addActionListener(this);
        openConvModel = new DefaultListModel();
        openConvList = new JList(openConvModel);
        openConvList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JLabel conversationHistory = new JLabel("Conversation History: ");
        viewHistory = new JButton("View");
        viewHistory.setActionCommand("viewHistory");
        viewHistory.addActionListener(this);
        pastConvModel = new DefaultListModel();
        pastConvList = new JList(pastConvModel);
        pastConvList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // conversation panel layout
        GroupLayout convLayout = new GroupLayout(convPanel);
        convPanel.setLayout(convLayout);
        convLayout.setAutoCreateGaps(true);
        convLayout.setAutoCreateContainerGaps(true);
        convLayout.setHorizontalGroup(
                convLayout.createParallelGroup()
                .addGroup(convLayout.createSequentialGroup()
                        .addComponent(newConvName)
                        .addComponent(newConv))
                .addGroup(convLayout.createSequentialGroup()
                        .addComponent(openConversations)
                        .addComponent(joinConv))
                .addComponent(openConvList)
                .addGroup(convLayout.createSequentialGroup()
                        .addComponent(conversationHistory)
                        .addComponent(viewHistory))
                .addComponent(pastConvList)
                );
        
        convLayout.setVerticalGroup(
                convLayout.createSequentialGroup()
                .addGroup(convLayout.createParallelGroup()
                        .addComponent(newConvName)
                        .addComponent(newConv))
                .addGroup(convLayout.createParallelGroup()
                        .addComponent(openConversations)
                        .addComponent(joinConv))
                .addComponent(openConvList)
                .addGroup(convLayout.createParallelGroup()
                        .addComponent(conversationHistory)
                        .addComponent(viewHistory))
                .addComponent(pastConvList)
                );
        convLayout.linkSize(SwingConstants.VERTICAL, newConvName, newConv, openConversations, joinConv, conversationHistory, viewHistory);
                
        // main layout
        mainLayout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(mainLayout);
        mainLayout.setAutoCreateGaps(true);
        mainLayout.setAutoCreateContainerGaps(true);
        mainLayout.setHorizontalGroup(
                mainLayout.createParallelGroup()
                .addGroup(mainLayout.createSequentialGroup()
                        .addComponent(connectionStatus)
                        .addComponent(disconnect))
                .addComponent(connectionPanel)
                .addComponent(errorBar)
                );
        mainLayout.setVerticalGroup(
                mainLayout.createSequentialGroup()
                .addGroup(mainLayout.createParallelGroup()
                        .addComponent(connectionStatus)
                        .addComponent(disconnect))
                .addComponent(connectionPanel)
                .addComponent(errorBar)
                );
    }
    /**
     * adds a new conversation to the list of open conversations
     * @param name the name of the new conversation
     */
    public void addConversation(String name) {
        openConvModel.addElement(name);
    }
    /**
     * removes a conversation from the list of open conversations 
     * (if there is a conversation history, it will still be available)
     * @param name the name of the conversations
     */
    public void removeConversation(String name) {
        openConvModel.removeElement(name);
    }
    
    /**
     * adds a conversation to list of archived conversations
     * @param name name of conversation to archive
     */
    public void archiveConversation(String name) {
        if (!pastConvModel.contains(name)) {
            pastConvModel.addElement(name);
        }
    }
    /**
     * displays an error message 
     * @param error the message to display
     */
    public void displayError(String error) {
        errorBar.setText(error);
    }
    
    /**
     * resets the gui when the client has disconnected from the server
     */
    public void disconnectServer() {
        this.setVisible(false);
        /*if (connectionStatus.getText().contains("Connecti")) {
            mainLayout.replace(usernamePanel, connectionPanel);
        }
        else {
            mainLayout.replace(convPanel, connectionPanel);
        }
        connectionStatus.setText("Not Connected");
        disconnect.setVisible(false); */
    }
    /**
     * Takes user input events and calls the appropriate methods to deal with each type of input.
     */
    @Override
    public void actionPerformed(final ActionEvent e) {     
        if (e.getActionCommand().equals("connectToServer")) {
            if (!ip.getText().matches("[\\s]*") && !port.getText().matches("[\\s]*")) {
                if (model.connectToServer(ip.getText(),
                        Integer.parseInt(port.getText()))) {
                    connectionStatus
                            .setText("Connecting to Server... Enter a username.");
                    errorBar.setText("");
                    disconnect.setVisible(true);
                    mainLayout.replace(connectionPanel, usernamePanel);
                } else {
                    errorBar.setText("Connection Failed. Try again. ");
                    ip.setText("");
                    port.setText("");
                }
            } else {
                errorBar.setText("Connection Failed. Try again. ");
                ip.setText("");
                port.setText("");
            }
        }
        else if (e.getActionCommand().equals("enterUsername")) {
            // check usernames...
            if (username.getText().matches("[\\w]+") && model.checkUsername(username.getText())) {
                connectionStatus.setText("Connected to Server.  Username: " + username.getText());
                errorBar.setText("");
                username.setText("");
                mainLayout.replace(usernamePanel, convPanel);
            }
            else {
                connectionStatus.setText("Connecting to Server...");
                errorBar.setText("Invalid Username.");
                username.setText("");
            }
        }
        else if (e.getActionCommand().equals("newConv") && newConvName.getText().matches("[\\w]+")) {
            //if (!pastConvModel.contains(newConvName.getText())) {
                model.newConversation(newConvName.getText());
            //}
            newConvName.setText("");
        }
        
        else if (e.getActionCommand().equals("joinConv") && openConvList.getSelectedValue() != null) {
            model.cConv(openConvList.getSelectedValue().toString());
            
        }
        
        else if (e.getActionCommand().equals("disconnect")) {
            model.disconnect();
            this.setVisible(false);
        }
        
        else if (e.getActionCommand().equals("viewHistory") && pastConvList.getSelectedValue() != null) {
            model.viewHistory(pastConvList.getSelectedValue().toString());
        }
    }
}
