
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import javax.websocket.DeploymentException;
import javax.websocket.Session;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.collections.map.MultiValueMap;
import org.glassfish.tyrus.server.Server;




class GetTimestamp  extends TimerTask{
	java.util.Date date;
	//EventBus EB=new EventBus();
	@Override
	public void run() {
		// TODO Auto-generated method stub
		date=new java.util.Date();
		EventBus.time=new Timestamp(date.getTime());
	}
	
}
public class EventBus 
{
    static Server server = null;
    static Options options = new Options();
    //static HashMap<String,String> client_list=new HashMap<String,String>(); 
    private static Set<Session> clients = Collections.synchronizedSet(new HashSet<Session>());
    static List list;
    static MultiValueMap map = new MultiValueMap();
    static MultiValueMap message_datamap = new MultiValueMap();
    //Set entrySet = map.entrySet();
    //Iterator it = entrySet.iterator();
    static Timestamp time;
    MessageFormat MF=new MessageFormat();
    public EventBus()
    {
    	Timer sche = new Timer();
		sche.schedule(new GetTimestamp(), 0, 1000);
    }
    private static void buildOptions() {
        // build option tables
        options.addOption(new Option("help", "print this message"));
        options.addOption(Option.builder("port").hasArg()
                .desc("port number")
                .build());
        options.addOption(Option.builder("ip").hasArg()
                .desc("external ip address")
                .build());
    }
    public static String[] parseArgs(String[] args) {
        String[] rst = new String[2];
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("indexing server", options);
                System.exit(0);
            }
            rst[0] = line.getOptionValue("ip", "127.0.0.1");
            rst[1] = line.getOptionValue("port", "8080");
        } catch (ParseException exp) {
            System.out.println("Arguments Error:" + exp.getMessage());
            System.exit(-1);
        }
        return rst;
    }


    //add the subscribers to the list
    public void add_subscriber(Session session)
    {
    	clients.add(session);	
    }
    
    public void Handle_message(String message, final Session session){
    	//use buffering
        InputStream file;
		try {
			file = new FileInputStream("quarks.ser");
			InputStream buffer = new BufferedInputStream(file);
	        ObjectInput input = new ObjectInputStream (buffer);
	        MF = (MessageFormat) input.readObject();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} 
        
    	//if(message.startsWith("R::"))
	    if(MF.Message.equals("Register"))
		{
    		//message=message.substring(3);
	    	//String[] split=message.split(",");
	    	String[] split=MF.Topic.split(",");
	    	for (String m: split)
            {
            	System.out.println(""+m);
            	System.out.println("-----------------------------"+m);
    	    	Add_to_Client_list(session.getId(), m);
    	    	System.out.println("[SERVER RECV] Session " + session.getId() + " Registred Topic : " + m);
            }
	    	
	    }
        else
	    {
	    	//String message_topic=message.substring(0,message.indexOf("::"));
	    	//String msg=message.substring(message.indexOf("::"));
	    	//System.out.println("--------"+message_topic);
	    	//String[] split=message_topic.split(",");
        	String message_topic=MF.Topic;
	    	String msg=MF.Message;
	    	//System.out.println("--------"+message_topic);
	    	String[] split=message_topic.split(",");
            
        	for (String m: split)
            {
		    	synchronized(clients){
		    		
	    	      // Iterate over the connected sessions
	    	      // and broadcast the received message
	    	      for(Session client : clients){
	    	    	  /*System.out.println("message "+message_topic);
	    	    	  System.out.println("client.getId() "+client.getId());
	    	    	  System.out.println("client_list.get(client.getId()) "+client_list.get(client.getId()));
	    	    	  //System.out.println("*************"+client_list.get(client.getId()).equals(message_topic));
	    	    	  System.out.println("**********"+client);*/
	    	    	  
	    	    	  /*String temp=client_list.get(client.getId());
	    	    	  if(temp!=null )
	    	    	  {
	    	    		  if (!client.equals(session) && temp.equals(message_topic))
	    	    	        {
	    	    			  String msg=message;
	    	    	        	try {
	    							client.getBasicRemote().sendText(msg);
	    							System.out.println("--------Sent ----- "+time+" Time "+message+" to "+client.getId());
	    						} 
	    	    	        	catch (IOException e) {
	    							// TODO Auto-generated catch block
	    							e.printStackTrace();
	    						}
	    	    	        	
	    	    	    	}
	    	    	  }*/
	    	    	  
	    	    	  Set entrySet = map.entrySet();
	    	          Iterator it = entrySet.iterator();
	    	          //System.out.println("-----"+map.get(client.getId()));
	    	          //System.out.println("--==");
	    	          
	    	          //System.out.println("  Object key  Object value");
	    	          //while (it.hasNext()) {
	    	        	  //Map.Entry mapEntry = (Map.Entry) it.next();
	    	              list = (List) map.get(client.getId());
	    	              if(list!=null)
	    	              {
	    	              for (int j = 0; j < list.size(); j++) {
	    	                  //System.out.println("\t" + mapEntry.getKey() + "\t  " + list.get(j));
	    	                  //System.out.println("j"+j);
	    	            	  //System.out.println("list.get(j)"+list.get(j));
	    	                  //System.out.println("message_topic"+message_topic);
	    	                  //System.out.println("!client.equals(session)"+!client.equals(session));
	    	                  
	    	                  if (!client.equals(session) && list.get(j).equals(m))
	      	    	        {
	    	                	  //System.out.println("enee");
	      	    			  //String msg=message;
	      	    	        	try {
	      							client.getBasicRemote().sendText(m+msg);
	      							System.out.println("--------Sent ----- "+time+" Time "+m+msg+" to "+client.getId());
	      							Add_to_message_datamap(time,msg);
	      						} 
	      	    	        	catch (IOException e) {
	      							// TODO Auto-generated catch block
	      							e.printStackTrace();
	      						}
	      	    	        	
	      	    	    	}
	    	                  
	    	              }
	    	              //System.out.println("----");
	    	          }
	    	      }
		    	}
            }
	    }
    }
    //Adding Client and topic to the list
    public void Add_to_Client_list(String session,String message)
    {
    	/*client_list.put(session, message);
    	for(Entry<String, String> m:client_list.entrySet()){  
     	   System.out.println("Client "+m.getKey()+" Registred to "+m.getValue());  
     	   //return m.getKey()+" "+m.getValue();
     	  }*/
    	//System.out.println("added");
    	map.put(session, message);
    	/*Set entrySet = map.entrySet();
        Iterator it = entrySet.iterator();
        
    	while (it.hasNext()) {
            Map.Entry mapEntry = (Map.Entry) it.next();
            list = (List) map.get(mapEntry.getKey());
            for (int j = 0; j < list.size(); j++) {
                System.out.println("\t" + mapEntry.getKey() + "\t  " + list.get(j));
            }
        }*/
    	
    }
    public void Add_to_message_datamap(Timestamp timestamp,String message)
    {
    	message_datamap.put(timestamp, message);	
    }
    public static void Start_EventBus()
    {
    	String[] args=args1;
    	EventBusConfig config = EventBusConfig.getInstance();
        buildOptions();
        //The port that we should run on can be set into an environment variable
        //Look for that variable and default to 8080 if it isn't there.
        String[] argValues = parseArgs(args);
        config.setIp_addr(argValues[0]);
        config.setPort_num(Integer.valueOf(argValues[1]));
        
        server = new Server(config.getIp_addr(), config.getPort_num(), "/websockets", null, ServerEndPoint.class);
        
        try {
            server.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Event Bus Started..");
            System.out.print("Please press a key to stop");
            reader.readLine();
        }
        catch(DeploymentException DE)
        {
        	System.out.println("Another instance of Event Bus is running");
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
    public static String[] args1;
    public static void main( String[] args )
    {
    	args1=args;
    	Start_EventBus();
    }
}
