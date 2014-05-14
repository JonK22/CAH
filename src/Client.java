/*
 *  @author Jon Koch, Andy Vaccaro, Derek Burritt, Dan Dorrity
 *  @version 1.0
 *  Published 4/4/14
 *  This is the Client file for a multi- threader client/server that is used for sending chat messages back and forth
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class Client {
   JLabel topicText;
	String user="User"; // @param user String used to store the User's name 
	Socket gameS, topicS;
	BufferedReader gbr, tbr;
	PrintWriter gpw, tpw;
	JTextArea[] handCards = new JTextArea[10], playCards = new JTextArea[8];
	Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	public Client(){ 
		// Main GUI creation
		JFrame jf = new JFrame("Cards Against Humanity");
		jf.setLayout(new BorderLayout(1,1));
		
		Chat c = new Chat(); // Creates an object of "Chat" 
		jf.add(c,BorderLayout.EAST);
      
      CAHGame cah = new CAHGame();
      jf.add(cah);
		
		JMenuBar jmb = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener(){
	          public void actionPerformed(ActionEvent ae){
		            	 System.exit(0);
		             }
	          });
		file.add(exit);
		// Creation of the Menu
		JMenu tools = new JMenu("Tools");
		JMenuItem help = new JMenuItem("Help");
		help.addActionListener(new ActionListener(){
	          public void actionPerformed(ActionEvent ae){
	        	  JOptionPane.showMessageDialog(null, "<html><b><u>Help</u></b></html>\nFor IP and Port number check the server attempting to connect to.\n"
	        	  		+ "Username field will allow the user to set their own name prior to connecting.\n"
	        	  		+ "Once connected the client will be able to send and receive messages.\n"
	        	  		, "Help",
	        				 JOptionPane.DEFAULT_OPTION);
		            }
		        });
		tools.add(help);
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new ActionListener(){
	          public void actionPerformed(ActionEvent ae){
	        	  JOptionPane.showMessageDialog(null, "<html><b><u>About</u></b></html>\n\n"
	        	  		+ "Programmed by Jon Koch, Andy Vaccaro, Derek Burritt, Dan Dorrity\n"
	        	  		+ "Version: 1.0\n"
	        	  		+ "Published: 4/4/14"
	        	  		, "About",
	        				 JOptionPane.DEFAULT_OPTION);
		            }
		        });
		tools.add(about);
		jmb.add(file);
		jmb.add(tools);
		
		jf.setJMenuBar(jmb);
		jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
		jf.setSize(1200,875);
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
	}
	
	public class Chat extends JPanel{
		// Class Chat extends JPanel for ease of use in further projects
		JPanel serverInfo; // @param serverInfo JPanel reference for a JPanel
		JTextArea jta; // @param jta JTextArea reference for JTextArea
		JTextField sendField, ip, port, uName;
		 // @param sendField Reference to JTextField
		// @param ip Reference to JTextField
		// @param port Reference to JTextField
		// @param uName Reference to JTextField
		JButton connect,disconnect, send;
		 // @param connect JButton reference for JButton
		 // @param disconnect JButton reference for JButton
		 // @param send JButton reference for JButton
		JLabel lip, lport; 
		 // @param lip JLabel reference for JLabel
		 // @param lport JLabel reference for JLabel
		Socket s; // @param s Socket Reference for Socket
		PrintWriter pwo; // @param pwo PrintWriter Reference for Printwriter
		BufferedReader br; // @param br BufferedReader reference for bufferedReader
		String msg=""; // @param msg String used to take in the message the user enters
		int portNum=-1; // @param port int used to receive the port the wishes to connect to
		String ipAddy=""; // @param ipAddy String Used to receive the ip address that the user wishes to connect to
		public Chat(){ 
			// Default Constructor
			
			// Setting up the Panel
			setLayout(new BorderLayout(1,1));
			JPanel serverInfoT = new JPanel(new FlowLayout());
			lip = new JLabel("IP: ");
			serverInfoT.add(lip);
			ip = new JTextField("localhost",10);
			serverInfoT.add(ip);
			lport = new JLabel("Port: ");
			serverInfoT.add(lport);
			port = new JTextField("29500",5);
			serverInfoT.add(port);
			JPanel serverInfoM = new JPanel(new FlowLayout());
			serverInfoM.add(new JLabel("Username: "));
			uName = new JTextField("Username",10);
			serverInfoM.add(uName);
			JPanel serverInfoB = new JPanel(new FlowLayout());
			connect = new JButton("Connect");
			connect.addActionListener(new ActionListener(){
	          public void actionPerformed(ActionEvent ae){
	        	  // On connect grab the IP, Port, Username and attempt to connect
	        	  // changes which buttons are active
	             ipAddy = ip.getText();
	             portNum = Integer.parseInt(port.getText());
	             jta.append("\nAttempting to connect to Server " + ipAddy +":" +portNum);
	             try{
	            	connect.setEnabled(false);
	            	uName.setEditable(false);
	 				
	 				s = new Socket(ipAddy, portNum); //Creating the socket using the ip and port gathered by the user and referenced as "s"
	 				IncomingMsg im = new IncomingMsg(s);
	 				user = uName.getText();
	 				new GamePlay(ipAddy, portNum+1, user);
	 				new TopicPlay(ipAddy, portNum+2);
	 				jta.append("\nServer Connection Successful...");
	 				disconnect.setEnabled(true);
	 				ip.setEditable(false);
					port.setEditable(false);
	 				send.setEnabled(true);
	 				user = uName.getText();
	 				pwo = new PrintWriter(new OutputStreamWriter(s.getOutputStream())); //creating a new Print Writer and referencing it to pwo
	 				br = new BufferedReader(new InputStreamReader(s.getInputStream()));
	 				
	 				}
	 			
	 			catch(IOException ie){
	 				jta.append("\nCould not connect to server at " + ipAddy + " on port " + portNum);
	 				connect.setEnabled(true);
	 				uName.setEditable(true);
	 			}
	            } 
	        });
			serverInfoB.add(connect);
			disconnect = new JButton("Disconnect");
			disconnect.addActionListener(new ActionListener(){
		          public void actionPerformed(ActionEvent ae){
		        	  // Disconnection from server, sends server information to know a client is disconnecting
		        	  // Changes which buttons are active
		             try {
		            	 sendMsg("CLIENTCLOSE");
		            	 s.close();
		            	disconnect.setEnabled(false);
		            	send.setEnabled(false);
						
						connect.setEnabled(true);
						uName.setEditable(true);
						ip.setEditable(true);
						port.setEditable(true);
						jta.append("\nDisconnected from server...");
						
					} catch (Exception e) {
						disconnect.setEnabled(true);
		            	send.setEnabled(false);
						jta.append("\nFailed to disconnect from server...");
						
					}
		            } 
		        });
			disconnect.setEnabled(false);
			serverInfoB.add(disconnect);
			serverInfo = new JPanel(new GridLayout(0,1));
			serverInfo.add(serverInfoT);
			serverInfo.add(serverInfoM);
			serverInfo.add(serverInfoB);
			add(serverInfo, BorderLayout.NORTH);
			
			//*******    JTA          *********************
			jta = new JTextArea(20,30);
			jta.setLineWrap(true);
			jta.setEditable(false);
			JScrollPane jsp = new JScrollPane(jta);
			
			add(jsp, BorderLayout.CENTER);

			//*******    JTF/send      *********************
			
			JPanel sendPan = new JPanel(new FlowLayout());
			sendField = new JTextField(14);
			sendPan.add(sendField);
			send = new JButton("Send");
			send.addActionListener(new ActionListener(){
		          public void actionPerformed(ActionEvent ae){
		        	  // if send is clicked it takes the text from the sendField and sends it to the server
			             String msg = user + ": ";
			             msg+=sendField.getText();
			             sendMsg(msg);
			            } 
			        });
			send.setEnabled(false);
			sendPan.add(send);
			add(sendPan,BorderLayout.SOUTH);
         
         
			
		}
		
		public void sendMsg(String msg){
			// method for sending text to server and resetting the send filed
			pwo.println(msg);
			pwo.flush();
			sendField.setText("");
		}
		
		public class IncomingMsg extends Thread{
			// class for incomming messages this is a thread to allow it to not hold up anything else within the client
			Socket s;  // @param s Socket Reference for Socket
			BufferedReader br;  // @param br BufferedReader reference for bufferedReader
			public IncomingMsg(Socket s){
				this.s= s;
				start(); // starts the thread
			}
			
			public void run(){
				try {
					// creates the bufferedreader
					br = new BufferedReader(new InputStreamReader(s.getInputStream())); // creating a buffered reader that reads in a message using the socket
					while(true){
                     msg = br.readLine();
                     String[] msgBreak = msg.split(":");
 						if(!msg.equals("CLIENTCLOSE")){ // append to jta if server sends other than "CLIENTCLOSE"
 							if(!msgBreak[1].equals("")){
 								jta.append("\n"+msg);
 								jta.selectAll();
 								int loc = jta.getSelectionEnd();
 								jta.select(loc,loc);
 							}
 						}
					}
				} 
				catch(SocketException se){
					//caught when server closes and client is connected
					// closes connection and changes active fields and buttons
					jta.append("\nServer connection closed...");
					try {
						s.close();
					} catch (IOException e) {
						// catches the closing of a socket 
					}
	            	disconnect.setEnabled(false);
	            	send.setEnabled(false);
					connect.setEnabled(true);
					uName.setEditable(true);
					ip.setEditable(true);
					port.setEditable(true);
				}
				catch(Exception e){
					//catches exceptions else not caught
				}
			
			}
		}
		
	}
    public class CAHGame extends JPanel{
      
         public CAHGame(){
            setLayout(new BorderLayout());
            JPanel play = new JPanel(new GridLayout(0,1));
            JPanel topic = new JPanel(new FlowLayout());
            topic.setBackground(Color.BLACK);
            topic.setPreferredSize(new Dimension(800,150));
            
            JPanel playedCards = new JPanel(new FlowLayout());
            play.add(topic);
            topicText = new JLabel("<html><font color=white size=8>This is where the topic card text will be placed</font></html");
            play.add(playedCards);
            TitledBorder playedCardsB = BorderFactory.createTitledBorder(
                    loweredetched, "Played Cards");
            	playedCardsB.setTitleJustification(TitledBorder.LEFT);
            	playedCards.setBorder(playedCardsB);
            for(int i=0; i<6;i++){
               JPanel aCard = new JPanel(new FlowLayout());
               aCard.setBackground(Color.WHITE);
               aCard.setPreferredSize(new Dimension(125, 175));
               aCard.setBorder(loweredetched);
               playCards[i] = new JTextArea("\n\n\nCards Against\nHumanity", 10,10);
               playCards[i].setEditable(false);
               playCards[i].setLineWrap(true);
               playCards[i].setWrapStyleWord(true);
               aCard.add(playCards[i]);
               playedCards.add(aCard);
            }
            
            topic.add(topicText);
            JPanel myCards = new JPanel(new FlowLayout());
            TitledBorder myCardsB = BorderFactory.createTitledBorder(
                    loweredetched, "My Hand");
            	myCardsB.setTitleJustification(TitledBorder.LEFT);
            	myCards.setBorder(myCardsB);
            for(int i=0; i<10;i++){
               JPanel aCard = new JPanel(new FlowLayout());
               aCard.setBackground(Color.WHITE);
               
               aCard.setPreferredSize(new Dimension(125, 175));
               aCard.setBorder(loweredetched);
               handCards[i] = new JTextArea("\n\n\nCards Against\nHumanity", 10,10);
               handCards[i].setEditable(false);
               handCards[i].setLineWrap(true);
               handCards[i].setWrapStyleWord(true);
               aCard.add(handCards[i]);
               myCards.add(aCard);
            }
            add(play, BorderLayout.NORTH);
            add(myCards, BorderLayout.CENTER);       
       }
         
    }
    public class GamePlay{
    	ArrayList<String> myHand = new ArrayList<String>();
    	public GamePlay(String ipAddy, int port2, String uName){
    		createGame(ipAddy, port2, uName);
    	}
    	
    	public void createGame(String ipAddy, int port2, String uName){
         	try {
    			gameS = new Socket(ipAddy, port2);
    			gpw = new PrintWriter(new OutputStreamWriter(gameS.getOutputStream())); //creating a new Print Writer and referencing it to pwo
    			gbr = new BufferedReader(new InputStreamReader(gameS.getInputStream()));
    			System.out.println("in the game socket");
    			gpw.println(uName);
    			gpw.flush();
    		} catch (UnknownHostException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
         	for(int i=0;i<10;i++){
         		getCard();
         		System.out.println("getting card");
         		
         	}
         	printMyHand();
         	System.out.println("");
         	
         }
    	public void getCard(){
    		try {
				myHand.add(gbr.readLine());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	public void printMyHand(){
    		for(int i = 0; i< myHand.size();i++){
    			handCards[i].setText(myHand.get(i));
    		}
    	}
    }
    public class TopicPlay extends Thread{
    	public TopicPlay(String ipAddy, int port3){
    		
    		try {
    			topicS = new Socket(ipAddy, port3);
				tbr = new BufferedReader(new InputStreamReader(topicS.getInputStream()));
				tpw = new PrintWriter(new OutputStreamWriter(topicS.getOutputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		start();
			System.out.println("in the topic socket");
    	}
    	public void run(){
    		getTopic();
    	}
    	public void getTopic(){
    		try {
    			String newTopic = tbr.readLine();
    			System.out.println(">>>>>> "+newTopic);
				topicText.setText(newTopic);
				tpw.println("GOT TOPIC");
				tpw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
	public static void main(String[] args) {
		new Client();

	}

}

