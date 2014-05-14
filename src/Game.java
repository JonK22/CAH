import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Main class to handle all game functionality.
 * @author Master Kunk
 *
 */
public class Game {
	InetAddress ipAddy; //IP Address of game server.
	int port = 29501; //Port for the game server.
	
	Vector<String> blackCards = new Vector<String>();	//Available black cards.
	Vector<String> whiteCards = new Vector<String>();	//Available white cards.
	Vector<String> roundCards = new Vector<String>();	//Cards played in the round.
	
	Hashtable<Integer, Player> players = new Hashtable<Integer, Player>();	//Existing players.
	
	/**
	 * Using the given number of players, start a new game. Loads
	 * in the cards needed for the game from resources, then establishes
	 * a socket to accept client connections.
	 * 
	 * @param	numberOfPlayers	The number of players in the game.
	 */
	public Game(int numberOfPlayers){
		
		//Load in cards for the game.
		loadCards();
		
		//Initialize the game server's IP.
		try{
			
			ipAddy = InetAddress.getLocalHost();
		}
		catch(UnknownHostException uhe){
			uhe.printStackTrace();
		}
		
		//Accept connections to the game server.
		try{ 
			ServerSocket serverSocket = new ServerSocket(port);
			
			//Only accept connections up until the specified number of players.
			for(int i=0;i<numberOfPlayers;i++){
				Socket s = serverSocket.accept();
				//TODO - Establish Player object for this connection.
				
			}
		  } 
		 catch(Exception e){
			 //TODO - catch an error accepting a connection
		 }
		
	}
	public void loadCards(){
		try{
		BufferedReader blackBr = new BufferedReader(new FileReader(new File("black.csv")));
		String read="";
		while((read = blackBr.readLine()) != null){
			//System.out.println(read);
			blackCards.add(read);			
		}
		blackBr.close();
		BufferedReader whiteBr = new BufferedReader(new FileReader(new File("white.csv")));
		while((read = whiteBr.readLine()) != null){
			whiteCards.add(read);			
		}
		whiteBr.close();
		}catch(IOException e){
			// error with file
		}
	}
	public class GamePlayer extends Thread{
		PrintWriter pw; // @param pw PrintWriter reference to PrintWriter
		BufferedReader br; // @param br BUfferedReader reference to BufferedReader
		Socket s;
		String msg;
		public GamePlayer(Socket s){
			this.s = s;
			start();
		}
		
		public void run(){
			try {
				// Creating the Reader and Writer and storing the writer in the vector
				br = new BufferedReader(new InputStreamReader (s.getInputStream())); // creating a buffered reader that reads in a message using the socket
			   pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
			   printers.add(pw);
			   players.put(hashPlayerLoc, br.readLine());
			   hashPlayerLoc++;
			   for(int i =1; i <=10; i++){
				   drawOne();
				   
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
		public void drawOne(){
			Random r = new Random();
			int randCard = r.nextInt(whiteCards.size());
			pw.println(whiteCards.get(randCard));
			pw.flush();
			System.out.println(whiteCards.get(randCard));
			whiteCards.remove(randCard);
			
		}
		
		public void sendRound(){
			
			for(PrintWriter apw : printers){
				for(int i = 0; i< roundCards.size();i++){
					apw.println(roundCards.get(i));
					apw.flush();
				}
			}
			
		}
		public void oneRound(){
			try {
				roundCards.add(br.readLine());
				while(roundCards.size() != players.size()){
					Thread.sleep(300);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(InterruptedException e){
				
			}
			sendRound();
			//getWinner();
			drawOne();
		}
	}
	public class TopicSocket extends Thread{
		Socket s;
		PrintWriter tpw;
		BufferedReader tbr;
		public TopicSocket(Socket s){
			this.s = s;
			start();
		}
		public void run(){
			try {
				tpw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
				tPrinters.add(tpw);
				tbr = new BufferedReader(new InputStreamReader(s.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getTopic();
		}
		public void getTopic(){
			
			Random r = new Random();
			int randCard = r.nextInt(blackCards.size());
			for(PrintWriter apw : tPrinters){
				try {
					while(!tbr.readLine().equals("GOT TOPIC")){
					apw.println(blackCards.get(randCard));
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(">>>>>>>>: "+blackCards.get(randCard));
				apw.flush();
			}
			blackCards.remove(randCard);
		}
		
	}
	public void getWinner(){
		
	}
	public static void main(String[] args){
		new Game();
	}
}
