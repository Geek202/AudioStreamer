package me.geek.tom.audioserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

public class VoiceServerManager {
    private Set<VoiceConnection> connections = new HashSet<>();

    private ServerListenThread serverListenThread;

    private ServerSocket serverSocket;

    private boolean running;

    public void begin(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        serverListenThread = new ServerListenThread(this);
        serverListenThread.start();

        running = true;
    }

    public void removeConnection(VoiceConnection vc) {
        connections.remove(vc);
    }

    public void addConnection(VoiceConnection vc) {
        connections.add(vc);
    }

    public void stop() {
        serverListenThread.interrupt();

        try {
            serverSocket.close();
            serverSocket = null;
        } catch (IOException ignored) {
        }

        running = false;
    }

    public void sendToAll(int byteCount, byte[] audioData, VoiceConnection source) throws IOException {
        connections.forEach(vc -> {
            if (vc != source) {
                try {
                    vc.sendAudio(byteCount, audioData);
                } catch (IOException e) {
                    System.out.println("Error encoutered while sending audio to " + vc.getSocket().getInetAddress() + ":");
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean isRunning() {
        return running;
    }

    public ServerSocket getSocket() {
        return serverSocket;
    }
}
