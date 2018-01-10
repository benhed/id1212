package client.controller;

import client.net.ServerConnection;
import client.net.ServerMsgHandler;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;

//client controller. calls functions in ServerConnection
public class Controller {
    private final ServerConnection serverConnection = new ServerConnection();

    public void connect(String host, int port, ServerMsgHandler serverMsgHandler) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.connect(port, host, serverMsgHandler);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        }).thenRun(() -> serverMsgHandler.handleReceivedMsg("Connected to " + host + ":" + port));
    }

    public void disconnect() throws IOException {
        serverConnection.disconnect();
    }

    public void sendUsername(String username) {
        CompletableFuture.runAsync(() -> serverConnection.sendUsername(username));
    }

    public void sendMsg(String msg) {
        CompletableFuture.runAsync(() -> serverConnection.sendGuess(msg));
    }
}

