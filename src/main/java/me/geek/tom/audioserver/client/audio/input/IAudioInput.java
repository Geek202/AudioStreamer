package me.geek.tom.audioserver.client.audio.input;

import me.geek.tom.audioserver.client.VoiceClient;

import java.io.IOException;

public interface IAudioInput {
    void start(VoiceClient vc) throws Exception;

    int read(byte[] buf, int maxAmount) throws IOException;

    void cleanup();
}
