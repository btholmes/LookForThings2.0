package me.cchiang.lookforthings;

/**
 * Created by btholmes on 3/26/17.
 */

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Game implements Serializable{

    private String gameID;
    private String date;
    private String challenger;
    private String opponent;
    private String opponentDisplayName;
    private String challengerDisplayName;
    private String winner;
    private List<String> words = new ArrayList<>();
    private boolean stillInPlay;
    private boolean opponentHasAccepted;

    public Game() {

    }

    public Game(String challenger, String opponent, List<String> words ){
        Random random = new Random();
        String gameID = System.currentTimeMillis() +""+ random.nextInt(100000);

        this.words = words;
        this.gameID = gameID;
        date =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.challenger = challenger;
        this.opponent = opponent;
        this.winner = null;
        stillInPlay = true;
        opponentHasAccepted = false;
    }

    public Game(boolean stillInPlay, boolean opponentHasAccepted, String opponentUID, String gameID, String dateCreated, String challengerUID){
        this.stillInPlay = stillInPlay;
        this.opponentHasAccepted = opponentHasAccepted;
        this.opponent = opponentUID;
        this.gameID = gameID;
        this.date = dateCreated;
        this.challenger = challengerUID;
    }

    public String getOpponentDisplayName() {
        return opponentDisplayName;
    }

    public void setOpponentDisplayName(String opponentDisplayName) {
        this.opponentDisplayName = opponentDisplayName;
    }

    public String getChallengerDisplayName() {
        return challengerDisplayName;
    }

    public void setChallengerDisplayName(String challengerDisplayName) {
        this.challengerDisplayName = challengerDisplayName;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(ArrayList<String> words) {
        this.words = words;
    }

    public boolean isOpponentHasAccepted() {return opponentHasAccepted; }

    public boolean isOpponent(String uid){return this.opponent.equals(uid); }

    public String getGameID() {
        return gameID;
    }

    public String getDate() {
        return date;
    }

    public String getChallenger() {
        return challenger;
    }

    public String getOpponent() {
        return opponent;
    }

    public String getWinner() {
        return winner;
    }

    public boolean isStillInPlay() {
        return stillInPlay;
    }


    public void setOpponentHasAccepted(boolean accepted) {this.opponentHasAccepted = accepted; }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setChallenger(String challenger) {
        this.challenger = challenger;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void setStillInPlay(boolean stillInPlay) {
        this.stillInPlay = stillInPlay;
    }
}
