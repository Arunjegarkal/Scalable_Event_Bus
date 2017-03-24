package chat_ServerClient;

import java.io.*;
import java.net.*;

public class MyServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			ServerSocket ss=new ServerSocket(3333);
			Socket s= ss.accept(); // Establishes the connection
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String str1="", str2="";
			while(!str1.equals("stop")){
				str1 = dis.readUTF();
				System.out.println("Client says :"+str1);
				str2= br.readLine();
				dos.writeUTF(str2);
				dos.flush();
			}
			dis.close();
			dos.close();
			br.close();
			s.close();
			ss.close();
		}
		catch(Exception e){
		}
	}

}
