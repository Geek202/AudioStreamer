package me.geek.tom.audioserver.client;

import me.geek.tom.audioserver.client.audio.input.IAudioInput;
import me.geek.tom.audioserver.client.audio.output.IAudioOutput;

import javax.sound.sampled.AudioFormat;
import java.io.*;
import java.net.Socket;
import java.util.function.Predicate;

public class VoiceClient extends Thread {

    private String ip;
    private int port;
    private Socket socket;

    private Predicate<VoiceClient> canSend;

    private boolean active = false;

    private DataOutputStream output;
    private DataInputStream input;

    private VoiceAudioInput audioInput;
    private VoiceAudioOutput audioOutput;

    private final IAudioInput audioInputStream;
    private final IAudioOutput audioOutputStream;

    public VoiceClient(String ip, IAudioInput input, IAudioOutput output) {
        this(ip, 56789, input, output);
    }

    public VoiceClient(String ip, int port, IAudioInput input, IAudioOutput output) {
        this(ip, port, null, input, output);
    }

    public VoiceClient(String ip, int port, Predicate<VoiceClient> shouldSend, IAudioInput input, IAudioOutput output) {
        this.ip = ip;
        this.port = port;
        this.canSend = shouldSend;
        this.audioInputStream = input;
        this.audioOutputStream = output;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(ip, this.port);
            active = true;

            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            audioInput = new VoiceAudioInput(this, this.audioInputStream);
            audioOutput = new VoiceAudioOutput(this, this.audioOutputStream);

            audioInput.start();
            audioOutput.start();

            if (canSend != null)
                audioInput.setCanSend(canSend);

        } catch (IOException e) {
            e.printStackTrace();
        }
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
