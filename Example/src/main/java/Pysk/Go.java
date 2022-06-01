package Pysk;

import Pysk.Enity.Chat;
import Pysk.Enity.EnterPoint;
import Pysk.Enity.Registr;


import javax.swing.*;
import java.awt.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Go extends JFrame{

    public static  Statement statement;

    public static CardLayout layout = new CardLayout();
    public static JPanel mainPanel = new JPanel();
    private Go(){
        super("Chatik");
        setSize(800,627);
        mainPanel.setLayout(layout);
        mainPanel.add(new EnterPoint(), "Login");
        mainPanel.add(new Registr(), "Registr");


        add(mainPanel);
        setLocationRelativeTo(null);
        setResizable(false);

        setVisible(true);

        layout.show(mainPanel, "Login"); //Layout.next chat

        try {
            statement = DriverManager.getConnection("jdbc:mysql://localhost:3306/example", "root", "4Fevral251768.").createStatement();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Go();
            }
        });
    }
}
