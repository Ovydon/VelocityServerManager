package net.ovydon.serverManager.gui;

import net.ovydon.serverManager.Main;
import net.ovydon.serverManager.model.Server;
import net.ovydon.serverManager.model.TOMLFileFilter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Ovydon
 */
public class MainWindow extends JFrame {

    JPanel inputPanel; // server input form
    JPanel serverPanel; // List of servers + delete btn + edit btn

    JButton loadConfig;
    JButton createConfig;


    public MainWindow(){


        initInputPanel();
        initServerPanel();

        repaintWindow();

    }

    private void initInputPanel(){
        inputPanel = new JPanel(new GridLayout(4,2));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Server"));

        JTextField nameField = new JTextField();
        JTextField ipField = new JTextField();
        JTextField portField = new JTextField();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(_ -> {
            // Server erstellen
            String name = nameField.getText();
            String ip = ipField.getText();
            String port = portField.getText();

            // name is not allowed to contain spaces or special symbols
            if (!name.matches("[a-zA-z0-9]+")){
                JOptionPane.showMessageDialog(null, "The server name is only allowed to contain letters and numbers!", "Invalid server name", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // all fields filled
            if (name.isEmpty() || ip.isEmpty() || port.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "The name, ip and port properties must be filled.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // test server properties
            int test = Server.alreadyExisting(name, ip, port);
            switch (test){
                case Server.NAME: case Server.IP_PORT:
                    Server.messageForUser(test);
                    return;
            }


            try {
                Main.addServer(new Server(name, ip, port));
            } catch (IllegalArgumentException e){
                JOptionPane.showMessageDialog(null,
                        "The IP or Port is not in the correct format!\n" +
                        "IP must have the form 255.255.255.255\n" +
                        "The Port must be a 5-digit port e.g. 25565",
                        "Format Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // reset window
            // input empty + reload server list
            repaintWindow();
        });

        inputPanel.add(new JLabel("server name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("IP-Address:"));
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

        System.out.println("Check server list:");
        for (Server s : servers){
            System.out.println(s.toString());
        }

        JList<Server> serverList = new JList<>(servers);
        JScrollPane listScrollPane = new JScrollPane(serverList);

        JButton editButton = new JButton("Edit");

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Action Listener edit-Button
                Server selectedServer = serverList.getSelectedValue();

                // create a frame for editing server properties
                JFrame editFrame = new JFrame("Edit Server");
                editFrame.setSize(300, 200);

                // components
                // inputs
                JPanel inputPanel = new JPanel();
                inputPanel.setLayout(new GridLayout(3, 2));
                JTextField name = new JTextField();
                JTextField ip = new JTextField();
                JTextField port = new JTextField();

                // set inputs to current values
                name.setText(selectedServer.getVelocityConfigName());
                ip.setText(selectedServer.getPubicIPString());
                port.setText(selectedServer.getPort());

                inputPanel.add(new JLabel("server name:"));
                inputPanel.add(name);
                inputPanel.add(new JLabel("IP-Address:"));
                inputPanel.add(ip);
                inputPanel.add(new JLabel("Port:"));
                inputPanel.add(port);

                // buttons
                JPanel buttonPanel = new JPanel();

                // cancel button to go back to main menu
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // close edit frame
                        editFrame.setVisible(false);
                        editFrame.dispose();
                    }
                });

                // edit button for overwriting server properties
                JButton editButton = new JButton("Edit");
                editButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // check values
                        // name is not allowed to contain spaces or special symbols
                        if (!name.getText().matches("[a-zA-z0-9]+")){
                            JOptionPane.showMessageDialog(null, "The server name is only allowed to contain letters and numbers!", "Invalid server name", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // test server properties
                        int test = Server.alreadyExisting(name.getText(), ip.getText(), port.getText());
                        switch (test){
                            case 0:

                                break;
                            case Server.NAME:
                                if (name.getText().equals(selectedServer.getVelocityConfigName()))
                                    break;

                            case Server.IP_PORT:
                                Server.messageForUser(test);
                                return;
                        }

                        // change server properties
                        selectedServer.setVelocityConfigName(name.getText());

                        if (!selectedServer.setPublicIP(ip.getText()))
                            JOptionPane.showMessageDialog(null,
                                    "The IP is not in the correct format!\n" +
                                            "IP must have the form 255.255.255.255",
                                    "Format Error", JOptionPane.ERROR_MESSAGE);
                        if (!selectedServer.setPort(port.getText()))
                            JOptionPane.showMessageDialog(null,
                                    "The Port is not in the correct format!\n" +
                                            "The Port must be a 5-digit port e.g. 25565",
                                    "Format Error", JOptionPane.ERROR_MESSAGE);

                        // repaint master window --> reload server list
                        repaintWindow();

                        // close edit frame
                        editFrame.setVisible(false);
                        editFrame.dispose();
                    }
                });

                buttonPanel.add(cancelButton);
                buttonPanel.add(editButton);

                JPanel borderPanel = new JPanel();
                borderPanel.setBorder(new EmptyBorder(10,5,10,5));
                borderPanel.setLayout(new BorderLayout());
                borderPanel.add(inputPanel, BorderLayout.CENTER);
                borderPanel.add(buttonPanel, BorderLayout.SOUTH);

                editFrame.add(borderPanel);
                editFrame.setVisible(true);
            }
        });

        JButton deleteButton = new JButton("Delete");

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Action Listener delete-button
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

        this.remove(inputPanel);
        this.remove(serverPanel);

        initInputPanel();
        initServerPanel();

        this.setLayout(new BorderLayout());
        this.add(inputPanel, BorderLayout.CENTER);
        this.add(serverPanel, BorderLayout.EAST);

        loadConfig = new JButton("load velocity.toml");
        loadConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Action Listener load config
                // pick file
                File velocityFile = getVelocityFile();
                System.out.println("got file");
                // set servers according to file
                Main.getAllCurrentServers(velocityFile);
                repaintWindow();
            }
        });

        createConfig = new JButton("create velocity.toml");
        createConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Action Listener create config
                // ask user: save standard config or overwrite existing config?
                String[] options = new String[]{"default config", "overwrite existing", "cancel"};
                int choice = JOptionPane.showOptionDialog(null,
                        "Do you want to get a default config or overwrite a existing one?",
                        "Choose Config",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[2]);

                File velocityFile;
                switch (choice){
                    case JOptionPane.YES_OPTION:
                        // user chose default configuration
                        velocityFile = new File("velocity.toml");
                        break;
                    case JOptionPane.NO_OPTION:
                        // user chose overwrite
                        velocityFile = new File("velocity.toml");
                        break;
                    case JOptionPane.CANCEL_OPTION: default:
                        // user chose cancel
                        return;

                }

                // --- configurate velocityFile according to server list ---

                try {
                    BufferedReader br = new BufferedReader(new FileReader(velocityFile));
                    String line;
                    boolean servers = false;

                    ArrayList<String> configFileText = new ArrayList<>();
                    // add comment that this file was created with Velocity Server-Manager
                    configFileText.add("# this file was created with Velocity Server-Manager " + (new Date()));

                    while ((line = br.readLine()) != null){

                        if (!servers)
                            // get all lines that are not in [servers]
                            configFileText.add(line);

                        // only [servers] relevant
                        if (line.equals("[servers]")){
                            servers = true;
                            // default comments at start of [servers]
                            configFileText.add("# Configure your servers here. Each key represents the server's name, and the value\n" +
                                    "# represents the IP address of the server to connect to.");

                            // add server information
                            for (Server s : Main.getServerList()){
                                configFileText.add(s.toString());
                            }

                            // default comments at start of try = []
                            configFileText.add("\n# In what order we should try servers when a player logs in or is kicked from a server.");

                            // add try
                            configFileText.add("try = [");
                            for (Server s : Main.getServerList()){
                                if (s.addToTry()){
                                    configFileText.add("\t\"" + s.getVelocityConfigName() + "\",");
                                }
                            }
                            // remove comma from last line
                            String last = configFileText.getLast().replace(",", "");
                            configFileText.removeLast();
                            configFileText.add(last);
                            // close try-statement
                            configFileText.add("]");

                        } else if (line.startsWith("["))
                            servers = false;

                    }

                    // file is completely read

                    // get file directory
                    File directory = getVelocityDirectory();
                    if (directory == null)
                        return;

                    File newVelocityFile = new File(directory.getPath() + "/velocity.toml");

                    try (FileWriter writer = new FileWriter(newVelocityFile)){
                        // write config text
                        for (String text : configFileText) {
                            writer.write(text + "\n");
                        }
                    } catch (IOException exception){
                        exception.printStackTrace();
                    }


                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JPanel configPanel = new JPanel();
        configPanel.add(createConfig);
        configPanel.add(loadConfig);

        this.add(configPanel, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    /**
     *
     * @return a .toml-file or null if no file was choosen
     */
    private static File getVelocityFile(){
        // the velocityFile that will be returned
        File velocityFile;

        // create JFileChooser to catch file from user
        JFileChooser fileChooser = new JFileChooser();

        // only Files can be selected
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // remove all default FileFilter
        fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());

        // create JFileFilter
        fileChooser.setFileFilter(new TOMLFileFilter());

        int fileResult = fileChooser.showOpenDialog(new JFrame());
        if (fileResult == JFileChooser.APPROVE_OPTION){
            velocityFile = fileChooser.getSelectedFile();
            return velocityFile;
        }

        return null;
    }

    private static File getVelocityDirectory(){
        // the velocityDirectory that will be returned
        File velocityDirectory;

        // create JFileChooser to catch file from user
        JFileChooser fileChooser = new JFileChooser();

        // only Files can be selected
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int fileResult = fileChooser.showOpenDialog(new JFrame());
        if (fileResult == JFileChooser.APPROVE_OPTION){
            velocityDirectory = fileChooser.getSelectedFile();
            return velocityDirectory;
        }

        return null;
    }

}
