package me.geek.tom.audioserver.client;

import javax.sound.sampled.AudioFormat;
import java.io.*;
import java.net.Socket;

public class VoiceClient extends Thread {


    private AudioFormat format = new AudioFormat(16000F, 16, 1, true, true);

    private String ip;
    private Socket socket;

    private boolean active = false;

    private DataOutputStream output;
    private DataInputStream input;

    private VoiceAudioInput audioInput;
    private VoiceAudioOutput audioOutput;

    public VoiceClient(String ip) {
        this.ip = ip;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(ip, 1337);
            active = true;

            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            audioInput = new VoiceAudioInput(this);
            audioOutput = new VoiceAudioOutput(this);

            audioInput.start();
            audioOutput.start();

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
