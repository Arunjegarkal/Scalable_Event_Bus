import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import javax.websocket.EncodeException;
import javax.websocket.Session;

class DisplayMsgCount extends TimerTask {
	static int c=0,p=0;
	
	public void run() {
		
       System.out.println("----------------------Number of Publication in 5sec "+c+" - "+p+"="+(c-p));
       p=c;
    }
}



public class Publisher extends Thread{
	Timer timer;
	 Session session;
	 CountDownLatch messageLatch;
	 String SENT_MESSAGE;
	 public static int msg_count=0;
	 public int prev_msg_count=0;
	Publisher(Timer timer,Session session,CountDownLatch messageLatch,String topic)
	{
		this.timer=timer;
		this.session=session;
		this.messageLatch=messageLatch;
		SENT_MESSAGE=topic;
		// And From your main() method or any other method
		Timer sche = new Timer();
		sche.schedule(new DisplayMsgCount(), 0, 5000);
	}
	
	public void run()
	{
		MessageFormat MF=new MessageFormat();
		java.util.Date date;
		while(true)
		{
			try {
	            //String msg = "Message " + SENT_MESSAGE + "_" + UUID.randomUUID();
				date=new java.util.Date();
	        	String msg = SENT_MESSAGE+ UUID.randomUUID();
	        	MF.Message=""+UUID.randomUUID();
	        	MF.Time=new Timestamp(date.getTime());
	        	MF.Topic=SENT_MESSAGE;
	        	//use buffering
	            OutputStream file = new FileOutputStream("quarks.ser");
	            OutputStream buffer = new BufferedOutputStream(file);
	        	//ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
	        	ObjectOutputStream out = new ObjectOutputStream(buffer);
	        	out.writeObject(MF);
	        	session.getBasicRemote().sendObject(out);
	        	out.close();
	        	//System.out.println("[CLIENT SEND] " + msg);
	            //session.getBasicRemote().sendText(msg);
	            
	            messageLatch.countDown();
	            msg_count=msg_count+1;
	            DisplayMsgCount.c=DisplayMsgCount.c+1;
	          //System.out.println(" " + DisplayMsgCount.c);
	        } 
	        catch(IllegalStateException i)
			{
				System.out.println("Event bus Unreachable...");
				//EventBus.Start_EventBus();
			}
	        catch (IOException ex) 
	        {
	            System.err.println(ex.getMessage());
	        } catch (EncodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*try
			{
				timer.scheduleAtFixedRate(new TimerTask() {
				   public void run() {
		                try {
		                    //String msg = "Message " + SENT_MESSAGE + "_" + UUID.randomUUID();
		                	String msg = SENT_MESSAGE+ UUID.randomUUID();
		                	//System.out.println("[CLIENT SEND] " + msg);
		                    session.getBasicRemote().sendText(msg);
		                    messageLatch.countDown();
		                    msg_count=msg_count+1;
		                    DisplayMsgCount.c=DisplayMsgCount.c+1;
		                  System.out.println(" " + DisplayMsgCount.c);
		                } 
		                catch(IllegalStateException i)
		        		{
		    				System.out.println("Event bus Unreachable...");
		    				
		        		}
		                catch (IOException ex) 
		                {
		                    System.err.println(ex.getMessage());
		                }
		            }
		        }, 0, 1000);
			}
			catch(IllegalStateException i)
		{
				System.out.println("Publisher Aborted...");
		}*/
	}
}