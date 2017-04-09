
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;

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
    @OnOpen
    public void handleOpen(){
        System.out.println("Client is now connected...");
    }
    @OnMessage
    public String handleMessage(String message  ){
        System.out.println("received from client "+message);
        String replyMessage = "Replyed assas"+message;
        System.out.println("Sent message to client "+replyMessage);
        return replyMessage;
    }
    @OnClose
    public void handleClose(){
        System.out.println("Client is now Disconnected...");
    }
    @OnError
    public void handleError(Throwable t){
        t.printStackTrace();
    }    
}
