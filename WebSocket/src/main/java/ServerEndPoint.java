/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Open your web browser, open this link: 
 * http://www.websocket.org/echo.html
 * 
 * And if you see "This web browser support WebSocket", which means you can test
 * this websocket on this web page now. 
 * 
 * In the location part, input
 * ws://localhost:8080/websockets/StringEndPoint
 * 
 * In the message box, type:
 * 
 * Test WebSocket
 * 
 * Press send button, you should be able to see your message in your console. 
 * 
 * In the message box, type:
 * 
 * GET
 * 
 * Press send button, you should be able to see the random messages sent by your
 * java program to the web browser. 
 * 
 * @author zhangwei
 */
@ServerEndpoint("/StringEndPoint")
public class ServerEndPoint {
    private Timer timer;
    
    EventBus eb=new EventBus();
    @OnOpen
    public void onOpen(Session session) {
    	eb.add_subscriber(session);
        System.out.println("[SERVER] Open session " + session.getId());
       
    }

    @OnMessage
    public void onMessage(String message, final Session session) {
    	try{
    		eb.Handle_message(message,session);
        }
        catch(NullPointerException n)
        {
        	System.out.println("Aborted..");
        }
    	catch(IllegalStateException i)
        {
        	System.out.println("Subscriber Unreachable...");
        }
        catch(Exception e)
        {
        	System.out.println("Aborted..");
        }
    	
        /*if(message.startsWith("R::"))
	    {
	    	message=message.substring(3);
	    	//System.out.println(""+message);
	    	Add_to_Client_list(session.getId(), message);
	    	System.out.println("[SERVER RECV] Session " + session.getId() + " Registred Topic : " + message);
	    }
        else
	    {
	    	String message_topic=message.substring(0,message.indexOf("::"));
	    	//System.out.println("--------"+message_topic);
	    	synchronized(clients){
	    		
    	      // Iterate over the connected sessions
    	      // and broadcast the received message
    	      for(Session client : clients){
    	    	if (!client.equals(session) && client_list.get(client.getId()).equals(message_topic))
    	        {
    	        	String msg=message;
    	        	try {
						client.getBasicRemote().sendText(msg);
						//System.out.println("--------Sent ----- "+message+" to "+client.getId());
					} 
    	        	catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	        	
    	    	}
    	      }
	    	}
	    }*/
        /*if ("GET".equals(message)) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                	try {
                        //String msg = "Message " + UUID.randomUUID();
                        //System.out.println("[SERVER SEND] " + msg);
                        session.getBasicRemote().sendText(message);
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            }, 0, 1000);
        }*/
        /*else if("random".equals(message)){
        	timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                	RandomGen(session);
                	
                }
            }, 0, 1000);
        }
        else if ("STOP".equals(message)) {
            timer.cancel();
        } 
        else
        {
        	timer.cancel();
        }*/
    }

    @OnClose
    public void onClose(Session session) {
        try{
        	timer.cancel();
            System.out.println("[SERVER] Session " + session.getId() + " is closed.");
        }
        catch(NullPointerException n)
        {
        	System.out.println("Aborted...");
        }
        catch(Exception e)
        {
        	System.out.println("Aborted...");
        }
        finally
        {
            System.out.println("[SERVER] Session " + session.getId() + " is closed.");
        }
    	
    }
    
    public void ReadFile(Session session){
    	String FILENAME = "D:\\TTU\\Spring 2016\\AOS\\Project 3\\Peer1\\F18.txt";

    		BufferedReader br = null;
    		FileReader fr = null;

    		try {

    			fr = new FileReader(FILENAME);
    			br = new BufferedReader(fr);

    			String sCurrentLine;

    			br = new BufferedReader(new FileReader(FILENAME));

    			while ((sCurrentLine = br.readLine()) != null) {
    				//System.out.println(sCurrentLine+"\n");
    				session.getBasicRemote().sendText(sCurrentLine);
    			}

    		}
    		
    		catch (IOException e) {

    			e.printStackTrace();

    		} finally {

    			try {

    				if (br != null)
    					br.close();

    				if (fr != null)
    					fr.close();

    			} catch (IOException ex) {

    				ex.printStackTrace();

    			}

    		}

    }
    
    public void RandomGen(Session session){
    	try {
            String msg = "Message " + UUID.randomUUID();
            System.out.println("[SERVER SEND] " + msg);
            session.getBasicRemote().sendText(msg);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
