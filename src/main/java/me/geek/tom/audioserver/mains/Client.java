package me.geek.tom.audioserver.mains;

import me.geek.tom.audioserver.client.VoiceClient;
import me.geek.tom.audioserver.client.audio.input.JavaLineInput;
import me.geek.tom.audioserver.client.audio.output.JavaLineOutput;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Missing an argument for IP!");
            System.exit(1);
        }
        VoiceClient client = new VoiceClient(args[0], new JavaLineInput(), new JavaLineOutput());
        client.start();

        Scanner scanner = new Scanner(System.in);
        String cmd = "";

        while (!cmd.equals("quit"))
            cmd = scanner.nextLine();
    }
}
