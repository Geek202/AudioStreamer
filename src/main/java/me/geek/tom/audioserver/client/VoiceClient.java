package me.geek.tom.audioserver.client;

import javax.sound.sampled.AudioFormat;
import java.io.*;
import java.net.Socket;
import java.util.function.Predicate;

public class VoiceClient extends Thread {


    private AudioFormat format = new AudioFormat(16000F, 16, 1, true, true);

    private String ip;
    private int port;
    private Socket socket;

    private Predicate<VoiceClient> canSend;

    private boolean active = false;

    private DataOutputStream output;
    private DataInputStream input;

    private VoiceAudioInput audioInput;
    private VoiceAudioOutput audioOutput;

    public VoiceClient(String ip) {
        this(ip, 1337);
    }

    public VoiceClient(String ip, int port) {
        this(ip, port, null);
    }

    public VoiceClient(String ip, int port, Predicate<VoiceClient> shouldSend) {
        this.ip = ip;
        this.port = port;
        this.canSend = shouldSend;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(ip, this.port);
            active = true;

            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            audioInput = new VoiceAudioInput(this);
            audioOutput = new VoiceAudioOutput(this);

            audioInput.start();
            audioOutput.start();

            if (canSend != null)
                audioInput.setCanSend(canSend);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AudioFormat getFormat() {
        return format;
    }

    public boolean isRunning() {
        return active;
    }

    public DataOutputStream getOutputStream() {
        return output;
    }

    public DataInputStream getInput() {
        return input;
    }

    public void shutdown() {
        try {
            if (audioInput != null) {
                audioInput.interrupt();
                audioInput.shutdown();
            }
            if (audioOutput != null) {
                audioOutput.interrupt();
                audioOutput.shutdown();
            }
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }
            if (socket != null) {
                socket.close();
            }

            active = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
