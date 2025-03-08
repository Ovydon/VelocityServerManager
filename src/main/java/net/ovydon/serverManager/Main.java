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

    private static ArrayList<Server> serverList;

    public static void main(String[] args) {
        initModels();
        initGUI();
    }

    public static void initModels(){
        serverList = new ArrayList<>();
    }

    public static void initGUI(){
        new MainWindow();
    }

    public static ArrayList<Server> getServerList(){
        return serverList;
    }

    public static void setServerList(ArrayList<Server> newServerList){
        serverList = newServerList;
    }

    public static void addServer(Server server){
        serverList.add(server);
    }

    public static void removeServer(Server server){
        serverList.remove(server);
    }

    public static void getAllCurrentServers(File velocityFile){

        try {
            BufferedReader br = new BufferedReader(new FileReader(velocityFile));
            String line;
            boolean server_list = false;
            boolean try_list = false;

            ArrayList<String> serverLines = new ArrayList<>();
            ArrayList<String> tryLines = new ArrayList<>();

            while ((line = br.readLine()) != null){

                // if ignore line
                if (line.isEmpty() || line.equals(" ") || line.startsWith("#"))
                    continue;


                if (server_list){
                    // detected try-statement
                    if (line.startsWith("try = [")){
                        // switch mode to try-statement-reading
                        try_list = true;
                        server_list = false;
                        continue;
                    }
                    serverLines.add(line);
                } else if (try_list){
                    if (line.startsWith("]")) {
                        // end of try statement
                        try_list = false;
                        continue;
                    }
                    // try-statement reading
                    tryLines.add(line);
                }

                if (line.equals("[servers]"))
                    server_list = true;
                else if (line.startsWith("[")) {
                    server_list = false;
                    try_list = false;
                }

            }

            // reset server list (all servers will be deleted)
            Main.setServerList(new ArrayList<>());

            for (String serverLine : serverLines){

                // delete line break
                serverLine = serverLine.replace("\n", "");

                // delete spaces
                serverLine = serverLine.replace(" ", "");

                // get server-name, ip and port
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

                // get try-statement
                boolean tryStatement = false;
                for (String tryLine : tryLines){
                    // extract server name from line
                    tryLine = tryLine.replace("\"", "").replace("\t", "").replace(" ", "");

                    if (tryLine.contentEquals(serverName)){
                        tryStatement = true;
                        break;
                    }
                }

                // create Server
                Server server = new Server(serverName.toString(), Inet4Address.ofLiteral(serverIP.toString().replace("\"", "")), serverPort.toString().replace("\"", ""));
                server.setAddToTry(tryStatement);

                // save Server in server list
                Main.addServer(server);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}