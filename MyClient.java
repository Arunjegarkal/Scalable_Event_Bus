package chat_ServerClient;

import java.io.*;
import java.net.*;

public class MyClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			Socket s=new Socket("localhost",3333);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String str1="", str2="";
			while(!str1.equals("stop")){
				str1= br.readLine();
				dos.writeUTF(str1);
				dos.flush();
				str2 = dis.readUTF();
				System.out.println("Server says :"+str2);		
			}
			//dis.close();
			dos.close();
			//br.close();
			s.close();		
		}
		catch(Exception e){
		}
	}

}
