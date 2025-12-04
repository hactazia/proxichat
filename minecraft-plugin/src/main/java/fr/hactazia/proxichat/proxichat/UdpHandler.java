package fr.hactazia.proxichat.proxichat;

import com.google.gson.JsonObject;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

public class UdpHandler {
    public InetAddress serverIp;
    public int serverPort;
    public DatagramSocket socket;
    public ProxiChatPlugin main;
    short ping_interval;

    public UdpHandler(ProxiChatPlugin main) {
        this.main = main;
        this.ping_interval = (short) main.getConfig().getInt("server_ping");
        this.serverPort = main.getConfig().getInt("server_port");
        try {
            this.serverIp = InetAddress.getByName(main.getConfig().getString("server_ip"));
            this.socket = new DatagramSocket();
        } catch (Exception e) {
            main.getLogger().severe("Socket exception: " + e.getMessage());
        }
    }

    public void send(byte[] message) {
        try {
            main.getLogger().info("Sending UDP message (" + message.length + " bytes) to " + serverIp.getHostAddress() + ":" + serverPort);
            if (message.length > 1400) {
                main.getLogger().warning("UDP message size (" + message.length + " bytes) exceeds typical MTU. This may cause delivery issues!");
            }
            DatagramPacket packet = new DatagramPacket(message, message.length, serverIp, serverPort);
            socket.send(packet);
            main.getLogger().info("UDP packet sent successfully");
        } catch (Exception e) {
            main.getLogger().severe("Sending exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void send(JsonObject message) {
        send(message.toString());
    }

    public void send(String message) {
        send(message.getBytes());
    }

    public void close() {
        socket.close();
    }

    private Date lastOutPing = new Date();

    public void onTick() {
        var now = new Date();
        if (now.getTime() - lastOutPing.getTime() > ping_interval * 1000) {
            lastOutPing = now;
            main.udpHandler.send("{\"type\":\"ping\"}".getBytes());
        }
    }
}
