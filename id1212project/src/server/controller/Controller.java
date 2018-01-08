package server.controller;

import server.model.Guesses;

public class Controller {
    private final Guesses guesses = new Guesses();

    public void appendGuess(String guess) {
        guesses.appendGuess(guess);
    }

    public String[] getGuesses() {return guesses.getGuessList();}

    public void clearGuessList(){guesses.clearGuessList();}

    public int getGuessListLength(){return guesses.getGuessListLength();}
}
