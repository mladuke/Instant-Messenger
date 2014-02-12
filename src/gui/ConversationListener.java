package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * ConversationListener implements WindowListener
 * We are using primarily for the functionality of allowing us to realize when windows are closed.
 */
public class ConversationListener implements WindowListener {
    
    private BackEndGUI gui;
    private String name;
    public ConversationListener(BackEndGUI gui) {
        super();
        this.gui = gui;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        
    }

    @Override
    public void windowClosing(WindowEvent e) {
        gui.dcConv(e.getWindow().getName());
    }

    @Override
    public void windowClosed(WindowEvent e) {
        
    }

    @Override
    public void windowIconified(WindowEvent e) {
        
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        
    }

    @Override
    public void windowActivated(WindowEvent e) {
        
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        
    }

}
