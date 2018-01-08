package client.net;

import common.Constants;
import common.MessageException;
import common.MsgType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.StringJoiner;

/**
 * Manages all communication with the server.
 */
public class ServerConnection {
    private Socket socket;
    private PrintWriter toServer;
    private BufferedReader fromServer;
    private volatile boolean connected;

    //Connect to the server
    public void connect(int port, String hostName,  ServerMsgHandler smh) throws
            IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(hostName, port), 100000);
        socket.setSoTimeout(10000000);
        connected = true;
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        toServer = new PrintWriter(socket.getOutputStream(), true);

        new Thread(new ServerRequestHandler(smh)).start();
    }

    //disconnect from the server and close socket
    public void disconnect() throws IOException {
        sendMsg(MsgType.DISCONNECT.toString());
        socket.close();
        socket = null;
        connected = false;
    }

    //send a username to the server
    public void sendUsername(String username) {
        sendMsg(MsgType.USER.toString(), username);
    }

    //send a guess to the server
    public void sendGuess(String msg) {sendMsg(MsgType.ENTRY.toString(), msg);}

    //send a message to the server
    private void sendMsg(String... parts) {
        StringJoiner joiner = new StringJoiner(Constants.MSG_DELIMETER);
        for (String part : parts) {
            joiner.add(part);
        }
        toServer.println(joiner.toString());
    }

    private class ServerRequestHandler implements Runnable {
        private final ServerMsgHandler serverMsgHandler;

        private ServerRequestHandler(ServerMsgHandler serverMsgHandler) {
            this.serverMsgHandler = serverMsgHandler;
        }

        @Override
        public void run() {
            try {
                for (;;) {
                    serverMsgHandler.handleReceivedMsg(extractMsgBody(fromServer.readLine()));
                }
            } catch (Throwable connectionFailure) {
                if (connected) {
                    serverMsgHandler.handleReceivedMsg("Lost connection.");
                }
            }
        }

        private String extractMsgBody(String entireMsg) {
            String[] msgParts = entireMsg.split(Constants.MSG_DELIMETER);
            if (MsgType.valueOf(msgParts[0].toUpperCase()) != MsgType.BROADCAST) {
                throw new MessageException("Received corrupt message: " + entireMsg);
            }
            return msgParts[1];
        }
    }
}
