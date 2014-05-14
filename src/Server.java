/*
 *  @author Jon Koch, Andy Vaccaro, Derek Burritt, Dan Dorrity
 *  @version 1.0
 *  Published 4/4/14
 *  This is the server file for a multi- threader client/server that is used for sending chat messages back and forth
 */

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class Server{
	InetAddress ipAddy; // InetAddress referenced as ipAddy
	int port = 29500; // @param port int setting the port that connections can be made on to 29500
	String msg = ""; // @param msg String used to store the messages that are passing through the server
	Receive r; // @param r Receive reference to the Class Receive
   Vector<PrintWriter> printers = new Vector<PrintWriter>();
	public Server(){
		
		//Networking Stuff*****
		
		try {
			ipAddy = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} // setting ipAddy to the local host ip address
		String servInfo = ("Current IP address : " + ipAddy.getHostAddress() + " Port: " + port+"\n"); // print out the ipAddy and the listening port
		
		//***** GUI**********
		// Creating the JFrame for the server
		JFrame jf = new JFrame("Server");
		jf.setLayout(new BorderLayout(1,1));
		JPanel label = new JPanel(new FlowLayout());
		label.add(new JLabel(servInfo));
		JPanel buttons = new JPanel(new FlowLayout());
		JButton exit = new JButton("Exit");
		exit.addActionListener(new ActionListener(){
	          public void actionPerformed(ActionEvent ae){
	        	// Exit button for the server gui  
	        	  System.exit(0);
	          }
		        });
		buttons.add(exit);
		jf.add(label,BorderLayout.NORTH);
		jf.add(buttons, BorderLayout.CENTER);
		
		jf.setDefaultCloseOperation(jf.DO_NOTHING_ON_CLOSE);
		jf.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				System.exit(0);		
			}
		});
		jf.pack();
		jf.setVisible(true);
		// end of gui creation
		
		 try { // Starting the networking code, server socket created and then server accepts and passes to Receive threads
			ServerSocket ss = new ServerSocket(port); // creating a server socket, ss, using the port
			while(true){
				Socket s = ss.accept(); // creating a socket , s , and setting it equal to a conenction using ss.accept
				r = new Receive(s);
			}
		  } 
		 catch(Exception e){
			 // catch an error accepting a connection
		 }
	}
	
	public class Receive extends Thread{
		//Inner class for each connection
		PrintWriter pw; // @param pw PrintWriter reference to PrintWriter
		BufferedReader br; // @param br BUfferedReader reference to BufferedReader
		Socket s; // @param s Socket reference to Socket
		
		public Receive(Socket s){	//Parameterized Constructor
			this.s = s;
			start(); // starts the run
		}
		public void run(){
				try {
					// Creating the Reader and Writer and storing the writer in the vector
					br = new BufferedReader(new InputStreamReader (s.getInputStream())); // creating a buffered reader that reads in a message using the socket
				   pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
				   printers.add(pw);
				   while((msg = br.readLine())!=null){ // reading the message in from a client and printing it to all clients
					   for(PrintWriter apw : printers){
						   apw.println(msg);
						   apw.flush();        
					   }
					}
               
               // remove "pw" from vector on close
					printers.remove(pw);
				}
				catch(SocketException se){
					// Client closing caught here
					
				}
				catch(IOException e){
					// catches not being able to print
				}
		}
	}
	
	public static void main(String[] args) {
		
		new Server();
//		new Game();
	}

	

	
	

	

}

