package Pysk.Enity;


import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Chat extends Component {
    public static void main(String[]args) {
    }
    public Chat(String value){
        try {

            String user= value;
            Socket socket=new Socket("localhost",8888);
            chatting_interface_multi clInterface_multi=new chatting_interface_multi(user);
            clInterface_multi.setConnection(socket);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
