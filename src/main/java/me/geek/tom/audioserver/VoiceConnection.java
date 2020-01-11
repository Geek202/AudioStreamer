package me.geek.tom.audioserver;

import java.io.*;
import java.net.Socket;

public class VoiceConnection extends Thread {

    private Socket socket;

    private DataOutputStream output;
    private DataInputStream input;

    private VoiceServerManager owner;

    private boolean connected = false;

    public Socket getSocket() {
        return socket;
    }

    public VoiceConnection(Socket s, VoiceServerManager vm) {
        socket = s;
        owner = vm;
    }

    @Override
    public void run() {

        // Initialise socket streams
        try {
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            connected = false;
            e.printStackTrace();
        }

        connected = true;

        // Listen for audio
        new Thread(() -> {
            while (VoiceConnection.this.connected) {
                try {
                    int dataLen = input.readInt();
                    byte[] audioData = new byte[dataLen];
                    input.read(audioData);

                    owner.sendToAll(dataLen, audioData, VoiceConnection.this);
                } catch (IOException e) {
                    close();
                } catch (Exception e) {
                    close();
                }
            }
            close();
        }).run();
    }

    public void sendAudio(int byteCount, byte[] audioData) throws IOException {
        if (!connected)
            return;

        output.writeInt(byteCount);
        output.write(audioData);
    }

    private void close() {
        connected = false;

        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException ignored) {
        }

        owner.removeConnection(this);
    }
}
