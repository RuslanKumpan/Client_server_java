package Pysk.Enity;

import Pysk.Go;
import Pysk.persistence.HibernateUtil;
import org.hibernate.Session;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FontFormatException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JTabbedPane;
import javax.swing.JList;

public class chatting_interface_multi extends JFrame implements ActionListener,Runnable{

    JFrame frame;//окно
    JTextArea msg;// Групповой диалог

    private JButton singin = new JButton("Save");
    JTextField text_str_send;// Окно отправки сообщения
    JButton sendBtn;// Кнопка отправки сообщения
    JButton exitBtn;// Кнопка выхода
    String user="";// имя пользователя
    Socket socket;//Разъем
    BufferedReader br;// Входной поток
    BufferedWriter bw;// Выходной поток
    JTabbedPane tabbedPane;// Диалоговый список
    JList list;// Показать список пользователей
    DefaultListModel listmodel;// Список модуля
    ArrayList<oneForone> oneForones=new ArrayList<oneForone>();// Хранить приватный объект чата
    public static boolean user_is_alive=false;// Определяем, есть ли еще пользователь
    public chatting_interface_multi(String user) {
        this.user=user;
        frame=new JFrame("Пользователь:"+user);

        frame.getContentPane().setLayout(null);

        msg = new JTextArea();

        setSize(600,1000);
        JLabel label = new JLabel("Введите сообщение");
        label.setBounds(20, 413, 85, 15);
        frame.getContentPane().add(label);

        text_str_send = new JTextField();
        text_str_send.setBounds(78, 413, 297, 21);
        frame.getContentPane().add(text_str_send);
        text_str_send.setColumns(10);
        text_str_send.addActionListener(this);


        sendBtn = new JButton("Отправить");
        sendBtn.setBounds(385, 412, 70, 23);
        frame.getContentPane().add(sendBtn);
        sendBtn.addActionListener(this);



        exitBtn = new JButton("Выбывать");
        exitBtn.setBounds(465, 412, 65, 23);
        frame.getContentPane().add(exitBtn);

        JScrollPane jsp=new JScrollPane(msg);//полоса прокрутки
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(20, 35, 410, 368);
        tabbedPane.add("Групповой чат",jsp);
        frame.getContentPane().add(tabbedPane);

        listmodel = new DefaultListModel();
        list = new JList(listmodel);
        list.setBounds(440, 60, 92, 340);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                String name=(String) list.getSelectedValue();// Получить двойное имя
                if (evt.getClickCount() == 2) {// Двойной щелчок мышью
                    try {
                        ResultSet result2 = Go.statement.executeQuery("SELECT id_reg FROM register WHERE login=\'" + name + "\' AND" + " online_reg=\'" + 1 + "\' ");
                        if(result2.next()){
                            JTextArea msg=new JTextArea();
                            tabbedPane.add(name,msg);
                            oneForone oneforone=new oneForone(msg, name);
                            oneForones.add(oneforone);
                        }
                        //Не читает сообщение
                        else {
                            try {
                                JTextArea msg = new JTextArea();
                                String value = text_str_send.getText();
                                Go.statement.execute("UPDATE register SET message=\'" + value + "\' WHERE login=\'" + name + "\'");

                                tabbedPane.add(name, msg);
                                oneForone oneforone1 = new oneForone(msg, name);
                                oneForones.add(oneforone1);


                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });

        frame.getContentPane().add(list);
        exitBtn.addActionListener(this);

        frame.setVisible(true);
        frame.setSize(558,487);
    }

    public void setConnection(Socket socket) {
        this.socket=socket;
        try {
            br=new BufferedReader(new
                    InputStreamReader(socket.getInputStream()));
            bw=new BufferedWriter(new
                    OutputStreamWriter(socket.getOutputStream()));
            user_is_alive=true;
            // msg.append (another + "online" + "\ n");
            bw.write("join:"+user+"\n");
            bw.flush();
            Thread thread=new Thread(this);
            thread.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            String content_recv;
            while(user_is_alive) {
                content_recv=br.readLine();
                // Кто-то присоединяется или покидает чат для обновления участников онлайн
                if(content_recv.indexOf("USER")==0) {
                    String msString[]=content_recv.split(":");
                    listmodel.removeAllElements();
                    for(int i=1;i<msString.length;i++) {
                        listmodel.addElement(msString[i]);
                    }
                }else {
                    if(content_recv.indexOf("системное уведомление")==0) {
                        msg.append(content_recv+"\n");
                    }else {
                        String Temp[]=content_recv.split("\\|");// Используем | как разделитель для выхода с \\
                        if(Temp.length==3) {
                            if(Temp[2].equals("Групповой чат"))
                                msg.append(Temp[0]+":"+Temp[1]+"\n");
                            else if(Temp[2].equals(user)){//Личная переписка
                                boolean flag=false;
                                for(int i=0;i<oneForones.size();i++) {
                                    if(oneForones.get(i).getName().equals(Temp[0])) {
                                        oneForones.get(i).getMsg().append(Temp[0]+":"+Temp[1]+"\n");
                                        flag=true;
                                        break;
                                    }
                                }
                                if(!flag) {// Открываем, не открывая приватное окно чата
                                    JTextArea msg=new JTextArea();
                                    tabbedPane.add(Temp[0],msg);
                                    oneForone oneforone=new oneForone(msg, Temp[0]);
                                    oneForones.add(oneforone);
                                    msg.append(Temp[0]+":"+Temp[1]+"\n");
                                }
                                System.out.print(Temp[0]+":"+Temp[1]+"\n");
                            }
                        }
                    }
                }
            }
        }catch(Exception e) {
//			e.printStackTrace();
            return;
        }
    }

    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if(e.getSource()==sendBtn||e.getSource()==text_str_send) {
            //Отправить сообщение
            sendmessage();

        }else if(e.getSource()==exitBtn) {
            //выбывать
            shutdown();
        }
    }

    //Отправить сообщение
    void sendmessage() {
        try {
            String to = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());// Получить текущую страницу
            String value=text_str_send.getText();
            String send_msg=user+"|"+value+"|"+to+"\n";

            //String content_send = user + ":" + text_str_send.getText () + "\ n"; // Тело сообщения

            if(!to.equals("Групповой чат")) {
                for(int i=0;i<oneForones.size();i++)
                    if(oneForones.get(i).getName().equals(to)) {
                        oneForones.get(i).getMsg().append(user+":"+value+"\n");
                    }
            }

            bw.write(send_msg);
            bw.flush();
//				msg.append(content_send);
  //          text_str_send.setText("");
        }catch (Exception e) {
            // TODO: handle exception
        }
    }


    //выбывать
    void shutdown() {
        user_is_alive=false;
        try {
            Go.statement.execute("UPDATE register SET online_reg = 0 WHERE login=\'" + user + "\'");
            bw.write("exit:"+user);
            bw.flush();
            bw.close();
            socket.close();
            this.dispose();
            System.exit(0);
        }catch (Exception e) {
            // TODO: handle exception
        }

    }
}

class oneForone extends JFrame{
    JTextArea msg;
    String name;
    public oneForone(JTextArea msg,String name) {
        this.name=name;
        this.msg=msg;
    }
    public JTextArea getMsg() {
        return msg;
    }
    public String getName() {
        return name;
    }
}