package server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Guesses {
    private final List<String> guessList = Collections.synchronizedList(new ArrayList<>());

    public void appendGuess(String guess) {
        guessList.add(guess);
    }

    public String[] getGuessList() {
        return guessList.toArray(new String[0]);
    }

    public int getGuessListLength(){
        return guessList.size();
    }

    public void clearGuessList(){
        guessList.clear();
    }
}
