import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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

import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;

import com.Avro.BroadcastMessage;
import com.Avro.msgfmt;

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
	 String Topic;
	 public static int msg_count=0;
	 public int prev_msg_count=0;
	Publisher(Session session,CountDownLatch messageLatch,String topic,int Type)
	{
		this.timer=timer;
		this.session=session;
		this.messageLatch=messageLatch;
		Topic=topic;
		// And From your main() method or any other method
		Timer sche = new Timer();
		sche.schedule(new DisplayMsgCount(), 0, 5000);
	}
	
	public void run()
	{
		MessageFormat MF=new MessageFormat();
		java.util.Date date;
		int j=0;
		while(true)
		{
			try {
	            BroadcastMessage BM=new BroadcastMessage(session,""+UUID.randomUUID(),Topic,1,""+new Timestamp(System.currentTimeMillis()),"send.avro");
					msg_count=msg_count+1;
    	            DisplayMsgCount.c=DisplayMsgCount.c+1;
    	          
	        } 
	        catch(IllegalStateException i)
			{
	        	this.stop();
				System.out.println("Event bus Unreachable...");
			}
		}
		
	}
}