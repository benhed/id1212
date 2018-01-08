package server.model;

import java.util.ArrayList;
import java.util.List;

public class RockPaperScissor {

    private static final String USERNAME_DELIMETER = ": ";

    public String[] assertWinner(String[] userGuesses){
        List<String> result = new ArrayList<>();

        for(int i = 0; i < userGuesses.length; i++){
            for(int k = i+1; k < userGuesses.length; k++) {
                switch (userGuesses[i].split(USERNAME_DELIMETER)[1].toLowerCase()) {
                    case "rock":
                        if (userGuesses[k].split(USERNAME_DELIMETER)[1].equals("scissor")) {
                            result.add(userGuesses[i].split(USERNAME_DELIMETER)[0]);
                        }
                        else if (userGuesses[k].split(USERNAME_DELIMETER)[1].equals("paper")) {
                            result.add(userGuesses[k].split(USERNAME_DELIMETER)[0]);
                        }
                        break;
                    case "paper":
                        if (userGuesses[k].split(USERNAME_DELIMETER)[1].equals("rock")) {
                            result.add(userGuesses[i].split(USERNAME_DELIMETER)[0]);
                        }
                        else if (userGuesses[k].split(USERNAME_DELIMETER)[1].equals("scissor")) {
                            result.add(userGuesses[k].split(USERNAME_DELIMETER)[0]);
                        }
                        break;
                    case "scissor":
                        if (userGuesses[k].split(USERNAME_DELIMETER)[1].equals("paper")) {
                            result.add(userGuesses[i].split(USERNAME_DELIMETER)[0]);
                        }
                        else if (userGuesses[k].split(USERNAME_DELIMETER)[1].equals("rock")) {
                            result.add(userGuesses[k].split(USERNAME_DELIMETER)[0]);
                        }
                        break;
                }
            }
        }
        return result.toArray(new String[0]);
    }
}
