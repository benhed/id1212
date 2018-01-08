package client.net;

//Handles broadcast messages from server.
public interface ServerMsgHandler {
    void handleReceivedMsg(String msg);
}
