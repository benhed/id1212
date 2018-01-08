package client.startup;

import client.view.NonBlockingInterpreter;

public class Main {
    public static void main(String[] args) {
        new NonBlockingInterpreter().start();
    }
}
