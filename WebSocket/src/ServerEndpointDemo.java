import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.websocket.Session;
/*

 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jeg's
 */
@ServerEndpoint("/serverendpointdemo")
public class ServerEndpointDemo {
	//Dataset to store all the clients information
	private static Set<Session> clients = Collections.synchronizedSet(new HashSet<Session>());
	//Dataset to store the client registred topic list
	static HashMap<String,String> client_list=new HashMap<String,String>();  
	@OnOpen
    public void handleOpen(Session session){
		// Add session to the connected sessions set
	    clients.add(session);
	    System.out.println("Client is now connected...");
    }
    @OnMessage
    public void onMessage(String message, Session session) 
    	    throws IOException {
    	    if(message.startsWith("Topic-"))
    	    {
    	    	message=message.substring(6);
    	    	System.out.println(""+message);
    	    	Add_to_Client_list(session.getId(), message);
    	    	
    	    }
    	    else
    	    {
    	    	String message_topic=message.substring(0,message.indexOf("::"));
    	    	System.out.println("--------"+message_topic);
    	    	synchronized(clients){
    	    		
	    	      // Iterate over the connected sessions
	    	      // and broadcast the received message
	    	      for(Session client : clients){
	    	    	if (!client.equals(session) && client_list.get(client.getId()).equals(message_topic))
	    	        {
	    	        	String msg=message;
	    	        	client.getBasicRemote().sendText(msg);
	    	    	}
	    	      }
    	    	}
    	    }
    }
    
    @OnClose
    public void handleClose(Session session){
    	// Remove session from the connected sessions set
        clients.remove(session);
        System.out.println("Client is now Disconnected...");
    }
    @OnError
    public void handleError(Throwable t){
        t.printStackTrace();
    }    
    public void Add_to_Client_list(String session,String message)
    {
    	client_list.put(session, message);
    	for(Map.Entry m:client_list.entrySet()){  
     	   System.out.println("Client "+m.getKey()+" Registred to "+m.getValue());  
     	   //return m.getKey()+" "+m.getValue();
     	  }
    }
    public HashMap<Integer, String> get_topic()
    {
    	HashMap<Integer,String> hm=new HashMap<Integer,String>();  
    	  hm.put(100,"Amit");  
    	  hm.put(101,"Vijay");  
    	  hm.put(102,"Rahul");  
    	  for(Map.Entry m:hm.entrySet()){  
    	   System.out.println(m.getKey()+" "+m.getValue());  
    	   //return m.getKey()+" "+m.getValue();
    	  }
    	  return hm;
    }
}