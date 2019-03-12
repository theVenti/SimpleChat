package chat;

import java.io.*;
import java.net.*;

public class ChatClient {

	public static void main (String[] args) {
		
		String addr = "192.168.0.162";
		int portNo = 5555;
		
		if (args.length ==2) {
			addr = args[0];
			portNo = Integer.parseInt(args[1]);
		}
		
		ChatterManager cm = new ChatterManager(addr, portNo);
		
	}
}

class ChatterManager {
	
	private String serverIP;
	private int serverPort;
	private Socket clientSocket;
	private BufferedReader br;
	private PrintWriter pw;
	private BufferedReader keyboard;
	
	ChatterManager (String serverIP, int serverPort) {
		try {
			
			this.serverIP = serverIP;
			this.serverPort = serverPort;
			clientSocket = new Socket(serverIP, serverPort);
			
			br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
			
			keyboard = new BufferedReader(new InputStreamReader(System.in,"KSC5601"));
			
			(new readSocketThread()).start();
			(new writeSocketThread()).start();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	
	class readSocketThread extends Thread {
		public void run() {
			try {
				while(true) {
					System.out.println(br.readLine());
				}
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
	}
	
	class writeSocketThread extends Thread {
		String inputString = null;
		public void run() {
			try {
				while ((inputString = keyboard.readLine()) != null) {
					pw.println(inputString);
					pw.flush();
				}
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
	}
}