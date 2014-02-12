package main;

import backEnd.ServerBackEnd;
import backEnd.UserBackEnd;
import backEnd.ConversationBackEnd;
import backEnd.Message;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Chat server runner.
 */
public class Server {
	/**
	 * Start a chat server.
	 */
	public static void main(String[] args) {
		ServerBackEnd server = new ServerBackEnd(4444);
		try {
			server.serve();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
