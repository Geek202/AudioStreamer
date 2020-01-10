package me.geek.tom.audioserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListenThread extends Thread {

    public VoiceServerManager owner;

    public ServerListenThread(VoiceServerManager voiceServerManager) {
        super();
        owner = voiceServerManager;
    }

    @Override
    public void run() {

        ServerSocket server = owner.getSocket();

        while (owner.isRunning()) {
            try {
                Socket s = server.accept();

                VoiceConnection vc = new VoiceConnection(s, owner);
                vc.start();

                owner.addConnection(vc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
