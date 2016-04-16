package com.player;

import com.cards.Hand;
import com.cards.HandCard;
import com.game.Game;

public class Player {

    /**
     * The current game
     */
    private static Game game;
    /**
     * The cards in the player's hand
     */

    private Hand hand;
    /**
     * The id of a player. This is just the player number. E.g. 1, 2, 3 or 4
     */
    private int id;
    /**
     * The name of the player. For computer-controlled players this is simply Player x.
     */
    private String name;
    /**
     * Whether a player is out or not. This is set once a player's score exceeds 200.
     */
    private boolean out;
    /**
     * Indicates whether a player has just gone out in the current round. Needs to be maintained by the main application.
     */
    private boolean justOut;
    /**
     * Indicates that a player has just had their score halved.
     */
    private boolean halvedScore;
    /**
     * The change in a player's score
     */
    private int scoreChange;
    /**
     * The type of player. Either HUMAN or COMPUTER.
     *
     * @see PlayerType
     */
    private PlayerType playerType;
    /**
     * The player's current score. This is initialised to 0 and a player is out when it reaches 200.
     */
    private int score;
    /**
     * The round in which a player went out
     */
    private int roundOut;

    /**
     * Creates a new Player. Since only a name has been specified, this defines a human-controlled player.
     *
     * @param newName The new player name
     */
    public Player(String newName, int id) {
        this(newName, PlayerType.HUMAN, id);
    }

    /**
     * Creates a new Player based on the player name and the player type. Their score is defaulted to 0, they are not out and they have a new Hand.
     *
     * @param name       The player name
     * @param playerType The type of player
     * @see Hand
     */
    public Player(String name, PlayerType playerType, int id) {
        this.name = name;
        this.score = 0;
        this.scoreChange = -1;
        this.out = false;
        this.justOut = false;
        this.playerType = playerType;
        this.id = id;
        this.halvedScore = false;
        this.hand = new Hand();
        this.roundOut = -1;
    }

    public static void setGame(Game game) {
        Player.game = game;
    }

    /**
     * Adds the specified score to a player's total. If the player's score exceeds 200 then they are out. If the player's score lands on an exact multiple of 50
     * then their score is halved.
     *
     * @param increment The score to increment by
     */
    public void addScore(int increment) {
        this.scoreChange = increment;
        this.halvedScore = false;
        if (increment == 0) {
            return;
        }
        this.score += increment;
        // System.out.println(this.name + " gains " + increment + " points.");
        if (this.score % 50 == 0) {
            this.halvedScore = true;
            this.score = this.score / 2;
            // System.out.println(this.name + " has halved their score to "
            // + this.score + "\nafter hitting an exact multiple of 50!");
        }
        if (score > 200) {
            out = true;
            justOut = true;
            roundOut = game.getRound();
            // System.out.println(this.name + " is OUT!");
            game.decrementPlayersIn();
        }

    }

    public int getRoundOut() {
        return this.roundOut;
    }

    public void setRoundOut(int roundOut) {
        this.roundOut = roundOut;
    }

    /**
     * @return The hand of the player.
     * @see Hand
     */
    public Hand getHand() {
        return this.hand;
    }

    /**
     * @return The ID of the player. E.g. their sequential number. 1-4.
     */
    public int getID() {
        return this.id;
    }

    /**
     * @return The name of a given Player.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return The type of player. Either HUMAN or COMPUTER controlled.
     * @see PlayerType
     */

    public PlayerType getPlayerType() {
        return this.playerType;
    }

    /**
     * @return The current score of a given player.
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Sets a player's score. Used when restored a player object.
     *
     * @param score If score is greater than 200 the player is also set to out.
     */
    public void setScore(int score) {
        this.score = score;
        if (score > 200) {
            game.decrementPlayersIn();
            this.out = true;
        }
    }

    /**
     * Sets the justOut flag to false
     */
    public void resetJustOut() {
        this.justOut = false;
    }

    /**
     * @return Whether a given Player is out of the game or not.
     */
    public boolean isJustOut() {
        return this.justOut;
    }

    /**
     * Sets the justOut flag to a value
     */
    public void setJustOut(Boolean justOut) {
        this.justOut = justOut;
    }

    /**
     * @return Whether a given Player has just had their score halved.
     */
    public boolean isScoreHalved() {
        return this.halvedScore;
    }

    public void setScoreHalved(boolean halved) {
        this.halvedScore = halved;
    }

    /**
     * @return Whether a given Player is out of the game or not.
     */
    public boolean isOut() {
        return this.out;
    }

    /**
     * @return The change in score for a Player.
     */
    public int getScoreChange() {
        return this.scoreChange;
    }

    public void setScoreChange(int change) {
        this.scoreChange = change;
    }

    /**
     * @return Returns a string with all the details of a player including what cards they have in their hand. Since this information should not be visible on
     * screen this is only intended for debugging purposes
     */
    public String printStates() {
        String s;
        s = String.format("ID:%d name: %-20s score: %03d hand: ", id, name, score);
        if (!out) {
            for (HandCard handCard : hand) {
                s = s + String.format("%-15s", handCard.toShortString());
            }
            s = s + String.format(" Value:%02d", hand.getValue());
            s = s + String.format("%n");
        } else
            s = s + String.format("O U T%n");

        return s;
    }

    public String printHand() {
        StringBuilder s = new StringBuilder();
        for (HandCard handCard : hand) {
            s.append(handCard.toShortString()).append(" ");
        }
        return s.toString();
    }
}
