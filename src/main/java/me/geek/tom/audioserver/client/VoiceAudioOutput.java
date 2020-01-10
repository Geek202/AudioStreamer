package me.geek.tom.audioserver.client;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.EOFException;
import java.io.IOException;

public class VoiceAudioOutput extends Thread {

    private VoiceClient vc;

    private DataLine.Info speaker;
    private SourceDataLine line;

    public VoiceAudioOutput(VoiceClient vc) {
        this.vc = vc;

        this.speaker = new DataLine.Info(SourceDataLine.class, vc.getFormat());
    }

    @Override
    public void run() {
        try {
            line = (SourceDataLine) AudioSystem.getLine(speaker);
            line.open(vc.getFormat(), 2200);
            line.start();
            byte[] data = new byte[4096];
            int count;
            int length;

            while (vc.isRunning()) {
                try {
                    if (vc.getInput().available() > 0) {
                        count = vc.getInput().readInt();

                        while (count > 0 && vc.isRunning()) {
                            length = data.length;
                            if (length > count)
                                length = count;

                            length = vc.getInput().read(data, 0, length);

                            if (length < 0)
                                throw new EOFException();

                            try {
                                line.write(data, 0, length);
                            } catch (IllegalArgumentException ignored) {
                            }

                            count -= length;
                        }
                    } else {
                        Thread.sleep(20);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    vc.shutdown();
                } catch (InterruptedException ignored) {
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
