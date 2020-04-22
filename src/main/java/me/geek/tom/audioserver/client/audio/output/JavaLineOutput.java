package me.geek.tom.audioserver.client.audio.output;

import me.geek.tom.audioserver.client.VoiceClient;
import me.geek.tom.audioserver.client.audio.JavaAudioUtil;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class JavaLineOutput implements IAudioOutput {

    private DataLine.Info speaker;
    private SourceDataLine line;

    @Override
    public void start(VoiceClient vc) throws Exception {
        AudioFormat format = JavaAudioUtil.getFormat();
        this.speaker = new DataLine.Info(SourceDataLine.class,format);
        line = (SourceDataLine) AudioSystem.getLine(speaker);
        line.open(format, 2200);
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
