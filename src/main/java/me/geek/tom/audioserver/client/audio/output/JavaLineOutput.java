package me.geek.tom.audioserver.client.audio.output;

import me.geek.tom.audioserver.client.VoiceClient;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class JavaLineOutput implements IAudioOutput {

    private DataLine.Info speaker;
    private SourceDataLine line;

    @Override
    public void start(VoiceClient vc) throws Exception {
        this.speaker = new DataLine.Info(SourceDataLine.class, vc.getFormat());
        line = (SourceDataLine) AudioSystem.getLine(speaker);
        line.open(vc.getFormat(), 2200);
        line.start();
    }

    @Override
    public void write(byte[] data, int length) {
        line.write(data, 0, length);
    }

    @Override
    public void cleanup() {
        line.flush();
        line.close();
    }
}
