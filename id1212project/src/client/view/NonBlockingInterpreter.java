package client.view;

import client.controller.Controller;
import client.net.ServerMsgHandler;

import java.util.Scanner;

public class NonBlockingInterpreter implements Runnable {
    private static final String PROMPT = "-> ";
    private final Scanner scan = new Scanner(System.in);
    private boolean receivingCmds = false;
    private Controller contr;
    private final String hostname = "localhost";
    private final int port = 8080;

    public void start() {
        if (receivingCmds) {
            return;
        }
        receivingCmds = true;
        contr = new Controller();
        new Thread(this).start();
        System.out.println("Welcome! Type 'connect' to connect to the server.");
    }

    @Override
    public void run() {
        while (receivingCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()) {
                    case QUIT:
                        receivingCmds = false;
                        contr.disconnect();
                        break;
                    case CONNECT:
                        contr.connect(hostname, port, new ConsoleOutput());
                        break;
                    case USER:
                        contr.sendUsername(cmdLine.getMsg());
                        break;
                    default:
                        contr.sendMsg(cmdLine.getUserInput());
                }
            } catch (Exception e) {
                println("Operation failed");
            }
        }
    }

    private String readNextLine() {
        print(PROMPT);
        return scan.nextLine();
    }

    private class ConsoleOutput implements ServerMsgHandler {
        @Override
        public void handleReceivedMsg(String msg) {
            println(msg);
            print(PROMPT);
        }
    }

    synchronized void print(String output) {
        System.out.print(output);
    }

    synchronized void println(String output) {
        System.out.println(output);
    }
}
