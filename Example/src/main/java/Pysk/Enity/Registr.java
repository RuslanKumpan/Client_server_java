package Pysk.Enity;


import Pysk.Go;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Registr extends JPanel {
    private JTextField login = new JTextField(), name = new JTextField();
    private JPasswordField password = new JPasswordField();
    private JButton singap = new JButton("Registr"), log = new JButton("Login");

    public Registr(){
        setSize(800,600);
        setLayout(null);
        name.setBounds(250,200,300,30);
        login.setBounds(250,100,300,30);
        password.setBounds(250,150,300,30);
        singap.setBounds(250,250,300,20);
        log.setBounds(250,300,300,30);

        add(login);
        add(password);
        add(singap);
        add(name);
        add(log);

        singap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ResultSet result = Go.statement.executeQuery("SELECT id_reg FROM register WHERE login=\'" + login.getText() + "\' ");
                    if (!result.next()) {
                        Go.statement.execute("INSERT INTO register (name,login,password) VALUES (\'" + name.getText()  + "\' ,  \'"+login.getText()+"\',\'" +password.getText()+ "\')");
                        Go.layout.show(Go.mainPanel,"Login");
                    }
                    else  {JOptionPane.showMessageDialog(null, "User  with the same login has been register");}
                }catch (SQLException e1){e1.printStackTrace();}
            }
        });
        log.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Go.layout.show(Go.mainPanel, "Login");
            }
        });
    }
}
