import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
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
	private static Set<Session> clients = 
		    Collections.synchronizedSet(new HashSet<Session>());
	
	@OnOpen
    public void handleOpen(Session session){
		// Add session to the connected sessions set
	    clients.add(session);
		System.out.println("Client is now connected...");
    }
    @OnMessage
    public void onMessage(String message, Session session) 
    	    throws IOException {
    	    
    	    synchronized(clients){
    	      // Iterate over the connected sessions
    	      // and broadcast the received message
    	      for(Session client : clients){
    	        if (!client.equals(session)){
    	          client.getBasicRemote().sendText(message);
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
}
