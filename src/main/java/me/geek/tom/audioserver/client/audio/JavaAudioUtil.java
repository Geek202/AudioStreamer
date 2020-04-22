package me.geek.tom.audioserver.client.audio;

import javax.sound.sampled.AudioFormat;

public class JavaAudioUtil {

    public static AudioFormat getFormat() {
        return new AudioFormat(16000F, 16, 1, true, true);
    }

}
