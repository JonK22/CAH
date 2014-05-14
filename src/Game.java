import java.util.*;
import java.net.*;
import java.io.*;

public class Game {
	InetAddress ipAddy; // InetAddress referenced as ipAddy
	int port = 29501; // @param port int setting the port that connections can be made on to 29500
	String msg = ""; // @param msg String used to store the messages that are passing through the server
	GamePlayer gp; // @param r Receive reference to the Class Receive
	TopicSocket ts;
	Vector<PrintWriter> printers = new Vector<PrintWriter>();
	Vector<PrintWriter> tPrinters = new Vector<PrintWriter>();
	Vector<String> blackCards = new Vector<String>();
	Vector<String> whiteCards = new Vector<String>();
	Vector<String> roundCards = new Vector<String>();
	Hashtable<Integer, String> players = new Hashtable<Integer, String>();
	int numPlaying = 1; // for final have this coming from a field in server
	int hashPlayerLoc = 1;
	public Game(){
		loadCards();
		try {
			ipAddy = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} // setting ipAddy to the local host ip address
		String servInfo = ("Current IP address : " + ipAddy.getHostAddress() + " Port: " + port+"\n"); // print out the ipAddy and the listening port
		
		
		 try { // Starting the networking code, server socket created and then server accepts and passes to Receive threads
			ServerSocket ss = new ServerSocket(port); // creating a server socket, ss, using the port
			ServerSocket ssTs = new ServerSocket(port+1);
			while(true){
				Socket s = ss.accept();
				gp = new GamePlayer(s);
				Socket sts = ssTs.accept();// creating a socket , s , and setting it equal to a conenction using ss.accept
				ts = new TopicSocket(sts);
				
			}
		  } 
		 catch(Exception e){
			 // catch an error accepting a connection
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
