package net.ovydon.serverManager.model;

import net.ovydon.serverManager.Main;

import javax.swing.*;
import java.net.Inet4Address;

/**
 * @author Ovydon
 */
public class Server {

    public static final int NAME = 1;
    public static final int IP_PORT = 2;

    // Entity Minecraft-Server
    private String velocityConfigName;
    private Inet4Address publicIP;
    private String port;
    private boolean addToTry = false;

    public Server(String velocityConfigName, Inet4Address publicIP, String port){
        this.velocityConfigName = velocityConfigName;
        if (!setPort(port)) throw new IllegalArgumentException("Invalid port: " + port);
        if (!setPublicIP(publicIP)) throw new IllegalArgumentException("Invalid IPv4-Address: " + publicIP.getHostAddress());
    }

    public Server(String velocityConfigName, String publicIP, String port){
        this.velocityConfigName = velocityConfigName;
        if (!setPort(port)) throw new IllegalArgumentException("Invalid port: " + port);
        if (!setPublicIP(publicIP)) throw new IllegalArgumentException("Invalid IPv4-Address: " + publicIP);
    }

    /**
     *
     * @param name the server name
     * @param ip the server IPv4
     * @param port the 5-digit server port
     * @return 0 - does not exist </br>1 - name already exists </br>2 - ip-port-combination already exists
     */
    public static int alreadyExsiting(String name, Inet4Address ip, String port){
        return alreadyExisting(name, ip.getHostAddress(), port);
    }

    /**
     *
     * @param name the server name
     * @param ip the server IPv4 in format d.d.d.d
     * @param port the 5-digit server port
     * @return 0 - does not exist </br>1 - name already exists </br>2 - ip-port-combination already exists
     */
    public static int alreadyExisting(String name, String ip, String port){
        for (Server server : Main.getServerList()){
            // test for name
            if (server.getVelocityConfigName().equals(name)){
                // name does already exist
                return NAME;
            }

            // test for ip-port-combination
            if (server.getPubicIPString().equals(ip) && server.getPort().equals(port)){
                // ip-port-combination does already exist
                return IP_PORT;
            }
        }

        return 0;
    }

    /**
     *
     * @param i return from alreadyExisting(...). Server.NAME, Server.IP_PORT
     */
    public static void messageForUser(int i){
        switch (i){
            case NAME:
                JOptionPane.showMessageDialog(null, "The server name already exists!", "Invalid server name", JOptionPane.ERROR_MESSAGE);
                break;
            case IP_PORT:
                JOptionPane.showMessageDialog(null, "You already added a server with the same IP and Port.", "Server already added", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    public String getServerIP(){
        return publicIP.getHostAddress() + ":" + port;
    }

    public String getPort(){
        return port;
    }

    public Inet4Address getPublicIP(){
        return publicIP;
    }

    public String getPubicIPString(){
        return publicIP.getHostAddress();
    }

    public boolean setPort(String port){
        if (port.length() != 5)
            return false;

        this.port = port;
        return true;
    }

    public boolean setPublicIP (Inet4Address ipv4){
        publicIP = ipv4;
        return true;
    }

    public boolean setPublicIP (String IP){

        publicIP = Inet4Address.ofLiteral(IP);
        return true;
    }

    public String getVelocityConfigName() {
        return velocityConfigName;
    }

    public void setVelocityConfigName(String velocityConfigName) {
        this.velocityConfigName = velocityConfigName;
    }

    @Override
    public String toString(){
        return (addToTry ? "[try] " : "") + velocityConfigName + " = " + getPubicIPString() + ":" + getPort();
    }

    public boolean addToTry() {
        return addToTry;
    }

    public void setAddToTry(boolean addToTry) {
        this.addToTry = addToTry;
    }
}
