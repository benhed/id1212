package client.view;


//Defines all commands that can be performed by a user.
public enum Command {

    //Specifies a user name. This name will be prepended to all entries in the guess list.
    USER,

    //Establish a connection to the server. The first parameter is IP address (or host name), the second is port number.
    CONNECT,

    //Leave the chat application.
    QUIT,

    //No command was specified. This means the entire command line is interpreted as an entry in the conversation, and is sent to all clients.
    NO_COMMAND
}
