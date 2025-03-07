package net.ovydon.serverController.gui;

import net.ovydon.serverController.Main;
import net.ovydon.serverController.model.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {

    JPanel inputPanel; // server input form
    JPanel serverPanel; // List of servers + delete btn + edit btn

    JButton loadConfig;
    JButton createConfig;


    public MainWindow(){

        this.setTitle("Velocity Server Controller");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600,400);

        initInputPanel();
        initServerPanel();
        initButtons();

        this.setLayout(new BorderLayout());
        this.add(inputPanel, BorderLayout.CENTER);

        this.setVisible(true);

    }

    private void initInputPanel(){
        inputPanel = new JPanel(new GridLayout(4,2));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Neuen Server hinzufügen"));

        JTextField nameField = new JTextField();
        JTextField ipField = new JTextField();
        JTextField portField = new JTextField();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(_ -> {
            // Server erstellen
            String name = nameField.getText();
            String ip = ipField.getText();
            String port = portField.getText();

            // TODO test ob Name schon existiert
            // TODO test ob ip + port Kombination schon existiert

            Main.addServer(new Server(name, ip, port));

            // reset panel
            nameField.setText("");
            ipField.setText("");
            portField.setText("");
            System.out.println(Main.getServerListString());
        });

        inputPanel.add(new JLabel("Server-Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("IP:"));
        inputPanel.add(ipField);
        inputPanel.add(new JLabel("Port:"));
        inputPanel.add(portField);
        inputPanel.add(new JLabel(""));
        inputPanel.add(addButton);
    }

    private void initServerPanel(){

    }

    private void initButtons(){

    }

}
