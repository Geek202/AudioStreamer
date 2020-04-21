package me.geek.tom.audioserver.client.audio.output;

import me.geek.tom.audioserver.client.VoiceClient;

public interface IAudioOutput {
    void start(VoiceClient vc) throws Exception;
    void write(byte[] data, int length);
    void cleanup();
}
