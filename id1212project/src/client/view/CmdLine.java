package client.view;

class CmdLine {
    private static final String PARAM_DELIMETER = " ";
    private String msg;
    private Command cmd;
    private final String enteredLine;

    CmdLine(String enteredLine) {
        this.enteredLine = enteredLine;
        parseInput(enteredLine);
    }

    Command getCmd() {
        return cmd;
    }

    String getMsg(){return msg;}

    String getUserInput() {
        return enteredLine;
    }

    private String removeExtraSpaces(String source) {
        if (source == null) {
            return source;
        }
        return source.trim().replaceAll(PARAM_DELIMETER + "+", PARAM_DELIMETER);
    }

    private void parseInput(String enteredLine){
        try{
            if(enteredLine.equalsIgnoreCase("quit")){
                cmd = Command.QUIT;
            }
            else if(enteredLine.equalsIgnoreCase("connect")){
                cmd = Command.CONNECT;
            }
            else{
                String[] splitMsg = removeExtraSpaces(enteredLine).split(PARAM_DELIMETER);
                cmd = Command.valueOf(splitMsg[0].toUpperCase());
                msg = splitMsg[1];
            }
        }catch(Throwable failedToReadMsg){
            cmd = Command.NO_COMMAND;
        }
    }
}
