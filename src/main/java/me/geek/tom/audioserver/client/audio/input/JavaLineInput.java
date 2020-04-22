package me.geek.tom.audioserver.client.audio.input;

import me.geek.tom.audioserver.client.VoiceClient;
import me.geek.tom.audioserver.client.audio.JavaAudioUtil;

import javax.sound.sampled.*;
import java.io.IOException;

public class JavaLineInput implements IAudioInput {

    private DataLine.Info mic;
    private TargetDataLine line;

    private AudioInputStream audioInput;

    @Override
    public void start(VoiceClient vc) throws Exception {
        AudioFormat format = JavaAudioUtil.getFormat();
        this.mic = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(mic)) {
            return;
        }
        line = (TargetDataLine) AudioSystem.getLine(mic);
        line.open(format, 2200);
        line.start();
        audioInput = new AudioInputStream(line);
    }

    @Override
    public int read(byte[] buf, int maxAmount) throws IOException {
        return audioInput.read(buf, 0, maxAmount);
    }

    @Override
    public void cleanup() {
        line.flush();
        line.close();
    }
}
