package me.cchiang.lookforthings.model;

import java.util.List;

/**
 * Created by btholmes on 4/10/17.
 */

public class Player {


    private List<String> wordsLeft;
    private String displayName;
    private String uid;
    private String lastPictureTaken;
    private boolean challenger;
    private boolean acceptedChallenge;


    public Player(String uid, List<String> wordsLeft){
        this.uid = uid;
        this.wordsLeft = wordsLeft;
    }

    public List<String> getWords() {
        return wordsLeft;
    }

    public void setWords(List<String> wordsLeft) {
        this.wordsLeft = wordsLeft;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isChallenger() {
        return challenger;
    }

    public void setChallenger(boolean challenger) {
        this.challenger = challenger;
    }

    public boolean isAcceptedChallenge() {
        return acceptedChallenge;
    }

    public void setAcceptedChallenge(boolean acceptedChallenge) {
        this.acceptedChallenge = acceptedChallenge;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLastPictureTaken() {
        return lastPictureTaken;
    }

    public void setLastPictureTaken(String lastPictureTaken) {
        this.lastPictureTaken = lastPictureTaken;
    }
}
