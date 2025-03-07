package net.ovydon.serverManager;


import net.ovydon.serverManager.gui.MainWindow;
import net.ovydon.serverManager.model.Server;

import java.io.*;
import java.net.Inet4Address;
import java.util.ArrayList;

/**
 * @author Ovydon
 */
public class Main {

    private static MainWindow mainWindow;
    private static ArrayList<Server> serverList;

    public static void main(String[] args) {
        initModels();
        initGUI();
        System.out.println("GUI steht");

    }

    public static void initModels(){
        serverList = new ArrayList<>();

        File velocityFile = new File("velocity.toml");
        getAllCurrentServers(velocityFile);
        System.out.println(getServerListString());
    }

    public static void initGUI(){
        mainWindow = new MainWindow();
    }

    public static String getServerListString(){
        String output = "";
        for (Server s : serverList){
            output += s.toString() + "\n";
        }
        if (output.isEmpty())
            return "";
        return output.substring(0, output.length()-1);
    }

    public static ArrayList<Server> getServerList(){
        return serverList;
    }

    public static void addServer(Server server){
        serverList.add(server);
    }

    public static void getAllCurrentServers(File velocityFile){

        try {
            BufferedReader br = new BufferedReader(new FileReader(velocityFile));
            String line;
            boolean server_list = false;

            ArrayList<String> serverLines = new ArrayList<>();

            while ((line = br.readLine()) != null){
                if (server_list){
                    // wie soll mit allem nach "try = [" umgegangen werden?
                    // ignorieren
                    if (line.equals("try = [")){
                        break;
                    } else if (line.isEmpty() || line.equals(" ") || line.startsWith("#")){
                        continue;
                    }
                    serverLines.add(line);
                }

                if (line.equals("[servers]"))
                    server_list = true;
                else if (line.startsWith("["))
                    server_list = false;

            }

            for (String serverLine : serverLines){

                // Absätze entfernen
                serverLine = serverLine.replace("\n", "");

                // Leerzeichen entfernen
                serverLine = serverLine.replace(" ", "");

                // Servernamen, IP und Port auslesen
                StringBuilder serverName = new StringBuilder();
                StringBuilder serverIP = new StringBuilder();
                StringBuilder serverPort = new StringBuilder();

                // 0 = serverName | 1 = serverIP | 2 = serverPort
                int variable = 0;

                for (char c : serverLine.toCharArray()){
                    if (c == '='){
                        variable = 1;
                    } else if (c == ':'){
                        variable = 2;
                    } else {
                        switch (variable){
                            case 0:
                                serverName.append(c);
                                break;
                            case 1:
                                serverIP.append(c);
                                break;
                            case 2:
                                serverPort.append(c);
                                break;
                        }
                    }
                }

                // Server erstellen und
                // Server in einer Liste speichern
                Main.addServer(new Server(serverName.toString(), Inet4Address.ofLiteral(serverIP.toString().replace("\"", "")), serverPort.toString().replace("\"", "")));

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}