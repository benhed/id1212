package server.net;

import server.controller.Controller;
import server.model.RockPaperScissor;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Receives guesses and broadcasts them to all players. All communication to/from any
 * player node pass this server.
 */
public class Server {
    private static final int LINGER_TIME = 5000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private final Controller contr = new Controller();
    private final RockPaperScissor rps = new RockPaperScissor();
    private final List<ClientHandler> clients = new ArrayList<>();
    private int portNo = 8080;

    public static void main(String[] args) {
        Server server = new Server();
        server.serve();
    }

    void broadcast(String msg) {
        contr.appendGuess(msg);
        synchronized (clients) {
            if(contr.getGuessListLength() == clients.size()) {
                for (ClientHandler client : clients){
                    for (String entry : contr.getGuesses()) {
                        client.sendMsg(entry);
                    }
                    String[] result = rps.assertWinner(contr.getGuesses());
                    if(result.length > 0) {
                        for (int i = 0; i < result.length; i++) {
                            if (result[i].equalsIgnoreCase(client.getUsername())) {
                                client.incrementRoundScore();
                            }
                        }
                    }
                    client.resetPlayerStatus();
                    client.setScore(client.getScore() + client.getRoundScore());
                    client.sendMsg("You got " + client.getRoundScore() + " points this round!");
                    client.sendMsg("Your total score is: " + client.getScore());
                    client.resetRoundScore();
                    client.sendMsg("Make a guess to play again or type 'quit' to leave.");
                }
                contr.clearGuessList();
            }
        }
    }

    void broadcast() {
        synchronized (clients) {
            if(contr.getGuessListLength() == clients.size()) {
                for (ClientHandler client : clients){
                    for (String entry : contr.getGuesses()) {
                        client.sendMsg(entry);
                    }
                    String[] result = rps.assertWinner(contr.getGuesses());
                    if(result.length > 0) {
                        for (int i = 0; i < result.length; i++) {
                            if (result[i].equalsIgnoreCase(client.getUsername())) {
                                client.incrementRoundScore();
                            }
                        }
                    }
                    client.resetPlayerStatus();
                    client.setScore(client.getScore() + client.getRoundScore());
                    client.sendMsg("You got " + client.getRoundScore() + " points this round!");
                    client.sendMsg("Your total score is: " + client.getScore());
                    client.resetRoundScore();
                    client.sendMsg("Make a guess to play again or type 'quit' to leave.");
                }
                contr.clearGuessList();
            }
        }
    }

    void broadcastNotify(String msg) {
        synchronized (clients) {
            for (ClientHandler client : clients){
                client.sendMsg(msg + " Players currently in lobby: " + clients.size());
            }
        }
    }

    void removeHandler(ClientHandler handler) {
        synchronized (clients) {
            clients.remove(handler);
        }
    }

    private void serve() {
        try {
            ServerSocket listeningSocket = new ServerSocket(portNo);
            while (true) {
                Socket clientSocket = listeningSocket.accept();
                startHandler(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server failure.");
        }
    }

    private void startHandler(Socket clientSocket) throws SocketException {
        clientSocket.setSoLinger(true, LINGER_TIME);
        clientSocket.setSoTimeout(TIMEOUT_HALF_HOUR);
        ClientHandler handler = new ClientHandler(this, clientSocket);
        synchronized (clients) {
            clients.add(handler);
        }
        Thread handlerThread = new Thread(handler);
        handlerThread.setPriority(Thread.MAX_PRIORITY);
        handlerThread.start();
    }

    protected boolean acceptedUsername(String username){
        for (ClientHandler client : clients) {
            if(client.getUsername().equalsIgnoreCase( username)){
                return false;
            }
        }
        return true;
    }
}
