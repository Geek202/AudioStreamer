package me.geek.tom.audioserver.mains;

import me.geek.tom.audioserver.VoiceServerManager;

import java.io.IOException;
import java.util.Scanner;

public class VoiceServer {

    private void run(String[] args) throws IOException {
        VoiceServerManager manager = new VoiceServerManager();

        manager.begin(56789);

        Scanner s = new Scanner(System.in);

        String cmd = "";
        while (!cmd.equals("quit")) {
            cmd = s.nextLine();
        }
    }

    public static void main(String[] args) throws IOException {
        new VoiceServer().run(args);
    }
}
