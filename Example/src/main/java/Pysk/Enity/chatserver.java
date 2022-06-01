package Pysk.Enity;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class chatserver {
    public static ArrayList<BufferedReader> ins=new ArrayList<BufferedReader>();
    public static ArrayList<BufferedWriter> outs=new ArrayList<BufferedWriter>();
    public static LinkedList<String> msgList=new LinkedList<String>();
    public static LinkedList<String> user=new LinkedList<String>();// Текущий онлайн список пользователей
    public static Thread t_send,t_accept;
    public static ServerSocket serverSocket;
    public chatserver() {

        try {
            serverSocket=new ServerSocket(8888);
            AcceptSocket as=new AcceptSocket();
            SendSocket ss=new SendSocket();
//			Socket socket=serverSocket.accept();
//			chatting_interface_multi srvif=new
//          chatting_interface_multi ("сторона сервера");
//			srvif.setConnection(socket);
            t_accept=new Thread(as);
            t_accept.start();

            t_send=new Thread(ss);
            t_send.start();

            System.out.println("Сервер запущен ...");
            System.out.println("Запуск завершен");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void main(String[]args) {
        new chatserver();
    }
}

class AcceptSocket implements Runnable{
    public void run() {
        while(chatserver.t_accept.isAlive()) {
            try {
                Socket socket=chatserver.serverSocket.accept();
                if(socket!=null) {
                    BufferedReader in=new
                            BufferedReader(new InputStreamReader(socket.getInputStream()));
                    chatserver.ins.add(in);
                    BufferedWriter out=new
                            BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    chatserver.outs.add(out);
                    new GetMsgFromClient(in,out).start();
                }
            }catch(Exception e) {

            }
        }
    }
}

class GetMsgFromClient extends Thread{
    BufferedReader in;BufferedWriter out;
    public GetMsgFromClient(BufferedReader in,BufferedWriter out) {
        this.in=in;this.out=out;
    }
    public void run() {
        while(this.isAlive()) {
            try {
                String strMsg=in.readLine();
                if(strMsg!=null) {
                    if(strMsg.indexOf("exit")==0) {// Кто-то покинул чат
                        chatserver.ins.remove(in);
                        chatserver.outs.remove(out);
                        String user_=strMsg.split(":")[1];
                        // Удалить пользовательский столбец
                        for(int i=0;i<chatserver.user.size();i++)
                            if(chatserver.user.get(i).equals(user_))
                                chatserver.user.remove(i);
                        String User="USER";
                        for(int i=0;i<chatserver.user.size();i++)
                            User+=(":"+chatserver.user.get(i));
                        chatserver.msgList.add(User);
                        strMsg="системное уведомление-"+strMsg.split(":")[1]+ " Выйти из чата";
                        System.out.println(user_+"exit");
                        chatserver.msgList.addFirst(strMsg);
                        break;
                    }
                    //Тут добавить
                    if(strMsg.indexOf("join")==0) {// Кто-то присоединился к чату
                        String user_=strMsg.split(":")[1];
                        chatserver.user.addFirst(user_);// Добавить пользовательский столбец
                        String User="USER";
                        for(int i=0;i<chatserver.user.size();i++)
                            User+=(":"+chatserver.user.get(i));
                        chatserver.msgList.add(User);
                        strMsg="системное уведомление-"+strMsg.split(":")[1]+"-Присоединиться к чату";
                        System.out.println(user_+"join");
                    }

                    chatserver.msgList.addFirst(strMsg);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                break;
            }

        }
    }
}
class SendSocket implements Runnable{

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while(chatserver.t_send.isAlive()) {
            try {
                if(!chatserver.msgList.isEmpty()) {
                    String string=chatserver.msgList.removeLast();
                    for(int i=0;i<chatserver.outs.size();i++) {
                        chatserver.outs.get(i).write(string+"\n");
                        chatserver.outs.get(i).flush();
//						System.out.println(chatserver.outs.get(i).toString());
                    }
                }
            }catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
}
