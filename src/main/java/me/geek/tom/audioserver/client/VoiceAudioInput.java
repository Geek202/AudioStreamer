package me.geek.tom.audioserver.client;

import me.geek.tom.audioserver.client.audio.input.IAudioInput;

import java.io.IOException;
import java.util.function.Predicate;

public class VoiceAudioInput extends Thread {

    private VoiceClient vc;

    private Predicate<VoiceClient> canSend = vc -> true;

    private IAudioInput input;

    public void setCanSend(Predicate<VoiceClient> canSend) {
        this.canSend = canSend;
    }

    public VoiceAudioInput(VoiceClient vc, IAudioInput input) {
        this.vc = vc;

        this.input = input;
    }

    @Override
    public void run() {
        try {
            this.input.start(this.vc);

            while (vc.isRunning()) {
                while (canSend.test(vc) && vc.isRunning()) {
                    try {
                        byte[] data = new byte[2200];
                        int read = this.input.read(data, data.length);

                        if (read > 0) {
                            vc.getOutputStream().writeInt(data.length);
                            vc.getOutputStream().write(data);
                        }
                    } catch (IOException e) {
                        vc.shutdown();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        this.input.cleanup();
    }
}
