package me.cchiang.lookforthings;

/**
 * Created by btholmes on 3/26/17.
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Game {

    private String gameID;
    private String dateCreated;
    private String challengerUID;
    private String opponentUID;
    private String winnerUID;
    private boolean stillInPlay;
    private boolean opponentHasAccepted;


    public Game(String challenger, String opponent ){
        Random random = new Random();
        String gameID = System.currentTimeMillis() +""+ random.nextInt(100000);

        this.gameID = gameID;
        dateCreated =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.challengerUID = challenger;
        this.opponentUID = opponent;
        this.winnerUID = null;
        stillInPlay = true;
        opponentHasAccepted = false;
    }

    public boolean isOpponentHasAccepted() {return opponentHasAccepted; }

    public boolean isOpponent(String uid){return this.opponentUID.equals(uid); }

    public String getGameID() {
        return gameID;
    }

    public String getDate() {
        return dateCreated;
    }

    public String getChallenger() {
        return challengerUID;
    }

    public String getOpponent() {
        return opponentUID;
    }

    public String getWinner() {
        return winnerUID;
    }

    public boolean isStillInPlay() {
        return stillInPlay;
    }


    public void setOpponentHasAccepted(boolean accepted) {this.opponentHasAccepted = accepted; }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public void setDate(String date) {
        this.dateCreated = date;
    }

    public void setChallenger(String challenger) {
        this.challengerUID = challenger;
    }

    public void setOpponent(String opponent) {
        this.opponentUID = opponent;
    }

    public void setWinner(String winner) {
        this.winnerUID = winner;
    }

    public void setStillInPlay(boolean stillInPlay) {
        this.stillInPlay = stillInPlay;
    }
}
