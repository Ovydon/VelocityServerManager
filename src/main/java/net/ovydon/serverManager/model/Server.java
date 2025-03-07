package net.ovydon.serverManager.model;

import java.net.Inet4Address;

/**
 * @author Ovydon
 */
public class Server {

    // Entity Minecraft-Server
    private String velocityConfigName;
    private Inet4Address publicIP;
    private String port;

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
        return velocityConfigName + " = " + getPubicIPString() + ":" + getPort();
    }
}
