package server.net;

import common.Constants;
import common.MessageException;
import common.MsgType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.StringJoiner;

//Represents one player (client).
class ClientHandler implements Runnable {
    private static final String JOIN_MESSAGE = " has joined the lobby.";
    private static final String LEAVE_MESSAGE = " has left the lobby.";
    private final Server server;
    private final Socket clientSocket;
    private BufferedReader fromClient;
    private PrintWriter toClient;
    private String username = "anon";
    private boolean connected;
    private boolean hasPlayed = false;
    private int score = 0;
    private int roundScore = 0;

    ClientHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        connected = true;
    }

    @Override
    public void run() {
        try {
            fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            toClient = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        sendMsg("Pick a username using the command 'user' followed by the desired username.");

        while (connected) {
            try {
                Message msg = new Message(fromClient.readLine());
                switch (msg.msgType) {
                    case USER:
                        if(server.acceptedUsername(msg.msgBody)){
                            username = msg.msgBody;
                            server.broadcastNotify(username + JOIN_MESSAGE);
                            sendMsg("Welcome to rock, paper scissor. Type your guess to play (rock/paper/scissor). Type 'quit' to leave.");
                        }
                        else{
                            sendMsg("That name is already taken, try again.");
                        }
                        break;
                    case ENTRY:
                        if(!username.equalsIgnoreCase("anon")){
                            if(msg.msgBody.equalsIgnoreCase("rock") || msg.msgBody.equalsIgnoreCase("paper") || msg.msgBody.equalsIgnoreCase("scissor")){
                                if(!hasPlayed) {
                                    hasPlayed = true;
                                    sendMsg("Good choice! Waiting...");
                                    server.broadcast(username + ": " + msg.msgBody);
                                }
                                else{
                                    sendMsg("You already picked what to play. Wait for other players to make their guess.");
                                }
                            }
                            else{
                                sendMsg("Invalid input. Try again. Available input: 'rock', 'paper' or 'scissor'. Type 'quit' to leave.");
                            }
                        }
                        else{
                            sendMsg("You have to pick a custom username to play. Use the command 'user' followed by your desired username.");
                        }
                        break;
                    case DISCONNECT:
                        disconnectClient();
                        server.broadcastNotify(username + LEAVE_MESSAGE);
                        server.broadcast();
                        break;
                    default:
                        throw new MessageException("Received corrupt message: " + msg.receivedString);
                }
            } catch (IOException ioe) {
                disconnectClient();
                throw new MessageException(ioe);
            }
        }
    }

    void sendMsg(String msg) {
        StringJoiner joiner = new StringJoiner(Constants.MSG_DELIMETER);
        joiner.add(MsgType.BROADCAST.toString());
        joiner.add(msg);
        toClient.println(joiner.toString());
    }

    private void disconnectClient() {
        try {
            clientSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        connected = false;
        server.removeHandler(this);
    }

    public void resetPlayerStatus(){
        hasPlayed = false;
    }

    public String getUsername(){
        return username;
    }

    public void setScore(int score){ this.score = score; }

    public void incrementRoundScore(){ roundScore = roundScore + 1; }

    public void resetRoundScore(){ roundScore = 0; }

    public int getRoundScore(){ return roundScore; }

    public int getScore(){ return score; }


    private static class Message {
        private MsgType msgType;
        private String msgBody;
        private String receivedString;

        private Message(String receivedString) {
            parse(receivedString);
            this.receivedString = receivedString;
        }

        private void parse(String strToParse) {
            try {
                String[] msgTokens = strToParse.split(Constants.MSG_DELIMETER);
                msgType = MsgType.valueOf(msgTokens[0].toUpperCase());
                if (hasBody(msgTokens)) {
                    msgBody = msgTokens[1];
                }
            } catch (Throwable throwable) {
                throw new MessageException(throwable);
            }
        }

        private boolean hasBody(String[] msgTokens) {
            return msgTokens.length > 1;
        }
    }
}
