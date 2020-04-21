package me.geek.tom.audioserver.client;

import me.geek.tom.audioserver.client.audio.output.IAudioOutput;

import java.io.EOFException;
import java.io.IOException;

public class VoiceAudioOutput extends Thread {

    private VoiceClient vc;

    private IAudioOutput output;

    public VoiceAudioOutput(VoiceClient vc, IAudioOutput output) {
        this.vc = vc;
        this.output = output;
    }

    @Override
    public void run() {
        try {
            this.output.start(this.vc);
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
                                this.output.write(data, length);
                            } catch (IllegalArgumentException ignored) {
                            }

                            count -= length;
                        }
                    } else {
                        Thread.sleep(20);
                    }
                } catch (IOException e) {
                    vc.shutdown();
                } catch (InterruptedException ignored) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        this.output.cleanup();
    }
}
