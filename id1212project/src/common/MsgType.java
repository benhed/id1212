package common;


//Defines all messages that can be sent between client and server
public enum MsgType {

    //Specifies a user name. A client sends this message to tell the server its name.
    USER,

    //A new guess. A client sends such a message to make the server broadcast the message to all clients (when the time is right).
    ENTRY,

    //An entry that is broadcasted from server to all clients.
    BROADCAST,

    //Client is about to close, all server recourses related to the sending client should be released.
    DISCONNECT
}
