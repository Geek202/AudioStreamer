package me.geek.tom.audioserver.client;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.function.Predicate;

public class VoiceAudioInput extends Thread {

    private VoiceClient vc;

    private Predicate<VoiceClient> canSend = vc -> true;

    private DataLine.Info mic;
    private TargetDataLine line;

    public void setCanSend(Predicate<VoiceClient> canSend) {
        this.canSend = canSend;
    }

    public VoiceAudioInput(VoiceClient vc) {
        this.vc = vc;

        this.mic = new DataLine.Info(TargetDataLine.class, vc.getFormat());
    }

    @Override
    public void run() {
        try {
            if (!AudioSystem.isLineSupported(mic)) {
                return;
            }
            line = (TargetDataLine) AudioSystem.getLine(mic);
            line.open(vc.getFormat(), 2200);
            line.start();
            AudioInputStream audioInput = new AudioInputStream(line);

            while (vc.isRunning()) {
                while (canSend.test(vc) && vc.isRunning()) {
                    try {
                        int available = audioInput.available();
                        byte[] data = new byte[Math.min(available, 2200)];

                        int read = audioInput.read(data, 0, data.length);

                        if (read > 0) {
                            vc.getOutputStream().writeInt(data.length);
                            vc.getOutputStream().write(data);
                        }
                    } catch (IOException e) {
                        vc.shutdown();
                    }
                }
            }
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        line.flush();
        line.close();
    }
}
