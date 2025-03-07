package net.ovydon.serverManager.gui;

import net.ovydon.serverManager.Main;
import net.ovydon.serverManager.model.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Ovydon
 */
public class MainWindow extends JFrame {

    JPanel inputPanel; // server input form
    JPanel serverPanel; // List of servers + delete btn + edit btn

    JButton loadConfig;
    JButton createConfig;


    public MainWindow(){

        repaintWindow();

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

            // tests:
            for (Server server : Main.getServerList()){
                // name already existing?
                if (name.equals(server.getVelocityConfigName())){
                    // name does exist
                    JOptionPane.showMessageDialog(null, "The server name \"" + name + "\" already exists!", "Invalid server name", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Ip + port combination already existing?
                if (server.getPort().equals(port) && server.getServerIP().equals(ip)){
                    // ip-port-combination does exist
                    JOptionPane.showMessageDialog(null, "You already added a server with the following IP and Port:\nIP: " + ip + "\nPort: " + port, "Server already added", JOptionPane.ERROR_MESSAGE);
                }
            }
            // name is not allowed to contain spaces or special symbols
            if (!name.matches("[a-zA-z0-9]+")){
                JOptionPane.showMessageDialog(null, "The server name is only allowed to contain letters and numbers!", "Invalid server name", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Main.addServer(new Server(name, ip, port));

            // reset window
            // input empty + reload server list
            repaintWindow();

            System.out.println(Main.getServerListString());
        });

        inputPanel.add(new JLabel("server name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("IP:"));
        inputPanel.add(ipField);
        inputPanel.add(new JLabel("Port:"));
        inputPanel.add(portField);
        inputPanel.add(new JLabel(""));
        inputPanel.add(addButton);
    }

    private void initServerPanel(){
        serverPanel = new JPanel();
        serverPanel.setBorder(BorderFactory.createTitledBorder("server list"));

        Server[] servers = new Server[Main.getServerList().size()];
        servers = Main.getServerList().toArray(servers);

        JList<Server> serverList = new JList<>(servers);
        JScrollPane listScrollPane = new JScrollPane(serverList);

        JButton editButton = new JButton("Edit");

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Action Listener edit-Button
                System.out.println("edit server");
                System.out.println(serverList.getSelectedValue().toString());
            }
        });

        JButton deleteButton = new JButton("Delete");

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Action Listener delete-button
                Server selectedServer = serverList.getSelectedValue();
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure that you want to delete \"" + selectedServer.getVelocityConfigName() + "\" (" + selectedServer.getServerIP() + ")?", "Delete Server", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION){
                    // remove server
                    Main.removeServer(selectedServer);
                    // info: server is deleted
                    JOptionPane.showMessageDialog(null, "The server has been removed!", "Server deleted", JOptionPane.INFORMATION_MESSAGE);
                    repaintWindow();
                }

            }
        });

        serverPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        // GridBagConstraints ScrollPane
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3; // width over 3 columns
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.8;
        gbc.insets = new Insets(5, 5, 5, 5);

        serverPanel.add(listScrollPane, gbc);

        // GridBagConstraints delete-button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // width over 1 column
        gbc.weightx = 0.3;
        gbc.weighty = 0.2;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(10, 5, 10, 5);

        serverPanel.add(deleteButton, gbc);

        // GridBagConstraints edit-button
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(10, 5, 10, 5);

        serverPanel.add(editButton, gbc);
    }

    private void repaintWindow(){
        this.setTitle("Velocity Server Controller");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600,400);

        initInputPanel();
        initServerPanel();

        this.setLayout(new BorderLayout());
        this.add(inputPanel, BorderLayout.CENTER);
        this.add(serverPanel, BorderLayout.EAST);

        loadConfig = new JButton("load velocity.toml");
        loadConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Action Listener load config
                System.out.println("load config");
            }
        });

        createConfig = new JButton("create velocity.toml");
        createConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Action Listener create config
                System.out.println("create config file");
            }
        });

        JPanel configPanel = new JPanel();
        configPanel.add(createConfig);
        configPanel.add(loadConfig);

        this.add(configPanel, BorderLayout.SOUTH);

        this.setVisible(true);
    }

}
