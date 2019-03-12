package chat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

	public static void main(String[] args) {
		
		System.out.println("Chatting Server Starting.");
		
		int portNo = 5555;
		
		if (args.length == 1 ) {
			portNo = Integer.parseInt(args[0]);
		}
		
		ChatManager cm = new ChatManager(portNo);
	}
	
}
class Chatter {
	private Socket clientSocket;
	private BufferedReader br;
	private PrintWriter pw;
	private ChatRoom chatRoom;
	private String chatterID;
	
	Chatter(ChatRoom chatRoom,
			Socket clientSocket, String chatterID) {
		System.out.println("Chatter 생성 : " + chatterID);
		try {
			this.chatRoom = chatRoom;
			this.clientSocket = clientSocket;
			this.chatterID = chatterID;
			br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
			(new readSocketThread()).start();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	public void sendMessage(String message) {
		pw.println(message);
		pw.flush();
	}
	
	class readSocketThread extends Thread {
		String inputString = null;
		public void run() {
			try {
				while(true) {
					inputString = br.readLine();
					chatRoom.chatEveryChatter(inputString);
				}
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
	}
}
class ChatRoom {
	private String roomName;
	private Vector joinChatters = new Vector();
	private Chatter roomMaker;
	
	ChatRoom(String roomName){
		System.out.println("채팅방 개설 : " + roomName);
		this.roomName = roomName;
		this.roomMaker = roomMaker;
	}
	
	public synchronized void joinChatter(Chatter chatter) {
		joinChatters.add(chatter);
	}
	
	public synchronized String getName() {
		return roomName;
	}
	
	public synchronized void chatEveryChatter(String message) {
		for(int i=0; i<joinChatters.size();i++) {
			((Chatter)joinChatters.get(i)).sendMessage(message);
		}
	}
	
	public int size() {
		return joinChatters.size();
	}
	
}
class ChatRoomManager {
	private Vector chatRooms = new Vector();
	
	ChatRoomManager() {
		System.out.println("ChatRoomManager Starting.");
		chatRooms.add(new ChatRoom("대기실"));
	}
	
	public void makeroom(String roomName) {}
	
	public void deleteRoom(String roomName) {}
	
	public void enterRoom(String roomName, Socket clientSocket) {
		Chatter chatter = null;
		ChatRoom tempRoom = null;
		boolean exitFor = false;
		int i;
		for(i=0; exitFor==false&&i<chatRooms.size();i++) {
			tempRoom = (ChatRoom)chatRooms.get(i);
			if(tempRoom.getName().equals(roomName)) {
				chatter = new Chatter(tempRoom, clientSocket, String.valueOf(tempRoom.size()+1));
				tempRoom.joinChatter(chatter);
				exitFor = true;
			}
		}
	}
	
	public void exitRoom(Chatter chatter) {}
}
class ChatManager {
	private int serverPort;
	private ServerSocket serverSocket;
	private ChatRoomManager chatRoomManager;
	
	ChatManager(int serverPort) {
		System.out.println("Chatting Manager Starting");
		
		try {
			this.serverPort = serverPort;
			chatRoomManager = new ChatRoomManager();
			serverSocket = new ServerSocket(serverPort);
			new listenerThread().start();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	
	class listenerThread extends Thread {
		private boolean stopListener = false;
		Socket clientSocket = null;
		public void run() {
			try {
				while (!stopListener) {
					System.out.println("Waitting Client...");
					clientSocket = serverSocket.accept();
					chatRoomManager.enterRoom("대기실", clientSocket);
					System.out.println("Connection Established form:"+clientSocket.getInetAddress().getHostAddress());
				}
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
	}
}