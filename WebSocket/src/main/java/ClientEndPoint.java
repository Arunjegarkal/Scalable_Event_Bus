/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import org.glassfish.tyrus.client.ClientManager;


public class ClientEndPoint {
    private static CountDownLatch messageLatch;
    private static CountDownLatch recvLatch;
    private static final String SENT_MESSAGE = "ACK";
    private static Timer timer;

    public static void main(String [] args){
    		final String arg1_option=args[0];
    		final String arg2_topic=args[1];
    		final MessageFormat MF=new MessageFormat();
        	
    		
        try {
            String wsAddr = "ws://localhost:8080/websockets/StringEndPoint";
            
            messageLatch = new CountDownLatch(10);
            recvLatch = new CountDownLatch(10);
            timer = new Timer();

            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

            ClientManager client = ClientManager.createClient();
            client.asyncConnectToServer(new Endpoint() {
            	
                @Override
                public void onOpen(final Session session, EndpointConfig config) {
                	System.out.println("Connected..." );
                	System.out.println("Session ID "+session.getId() );
                    try {
                        session.addMessageHandler(new MessageHandler.Whole<String>() {
                        	
                        	
                            //@Override
                            public void onMessage(String message) {
                                    System.out.println("[CLIENT RECV] Received message: "+message);
                                    //session.getAsyncRemote().sendText("topic :: "+ arg1);
                                    //String msg = "Message " + SENT_MESSAGE + "_" + message.split(",")[1];
                                    //session.getAsyncRemote().sendText(msg);
                                    //System.out.println("[CLIENT SEND] " + message);
                            		//System.out.println("[CLIENT SEND] " + recvLatch.getCount());
                                    recvLatch.countDown();
                            }
                        });
                        
                        if(arg1_option.equals("1"))
                        {

                        	
                            Publisher p=new Publisher(timer,session,messageLatch,arg2_topic+"::");
                            p.start();
                        	/*while(true)
                        	{
                        		session.getBasicRemote().sendText("for::p");
                        	}*/
                        	
                        }
                        else if(arg1_option.equals("2"))
                        {
                        	java.util.Date date;date=new java.util.Date();
            	        	String msg = SENT_MESSAGE+ UUID.randomUUID();
            	        	MF.Message="Register";
            	        	MF.Time=new Timestamp(date.getTime());
            	        	MF.Topic=arg2_topic;
            	        	OutputStream file = new FileOutputStream("quarks.ser");
            	            OutputStream buffer = new BufferedOutputStream(file);
            	        	//ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            	        	ObjectOutputStream out = new ObjectOutputStream(buffer);
            	        	out.writeObject(MF);
            	        	session.getBasicRemote().sendObject(out);
            	        	out.close();
                        	//session.getBasicRemote().sendText("R::"+arg2_topic);
                        }
                        
                        //session.getBasicRemote().sendText("STOP");
                    } catch (Exception ex) {
                        Logger.getLogger(ClientEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                

                
                
            }, cec, new URI(wsAddr));
            
            recvLatch.await(100, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

