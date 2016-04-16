package com.bazsoft.yaniv;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.cards.Card;
import com.cards.CardVector;
import com.cards.Deck;
import com.cards.HandCard;
import com.cards.Pile;
import com.cards.PileCard;
import com.game.Game;
import com.game.GameTurn;
import com.game.Turn;
import com.player.Player;

import java.util.Date;
import java.util.Set;

public class AppPreferences {
    /**
     * The number of assafs that the player has caused in the saved game.
     */
    private static final String GAME_ASSAF = "Assaf";
    /**
     * The entire string output on the console of a saved game.
     */
    private static final String GAME_CONSOLE = "Console";
    /**
     * An integer representing the current player of a saved game.
     */
    private static final String GAME_CURRENT_PLAYER = "Current Player";
    /**
     * A "|" delimited list of the deck positions of the deck cards.
     */
    private static final String GAME_DECK_CARDS = "Deck cards";
    /**
     * The number of times a player has halved their score in a game.
     */
    private static final String GAME_HALVES = "Halved";
    /**
     * The number of players in the saved game.
     */
    private static final String GAME_NUM_PLAYERS = "Number of Players";
    /**
     * A "|" delimited list of the deck positions of the pile cards.
     */
    private static final String GAME_PILE_CARDS = "Pile cards";
    /**
     * A "|" delimited list of the deck positions of player 1's cards.
     */
    private static final String GAME_PLAYER_1_HAND = "Player 1 hand";
    /**
     * Whether player 1 is just out
     */
    private static final String GAME_PLAYER_1_JUST_OUT = "Player 1 just out";
    /**
     * The score of player 1 in the saved game.
     */
    private static final String GAME_PLAYER_1_SCORE = "Player 1 score";
    /**
     * The change in score of player 1 in the saved game.
     */
    private static final String GAME_PLAYER_1_SCORE_CHANGE = "Player 1 change score";
    /**
     * Whether player 1 has just halved their score.
     */
    private static final String GAME_PLAYER_1_SCORE_HALVED = "Player 1 score halved";
    /**
     * The round in which player 1 went out.
     */
    private static final String GAME_PLAYER_1_ROUND_OUT = "Player 1 round out";
    /**
     * A "|" delimited list of the deck positions of player 2's cards.
     */
    private static final String GAME_PLAYER_2_HAND = "Player 2 hand";
    /**
     * Whether player 2 is just out
     */
    private static final String GAME_PLAYER_2_JUST_OUT = "Player 2 just out";
    /**
     * The score of player 2 in the saved game.
     */
    private static final String GAME_PLAYER_2_SCORE = "Player 2 score";
    /**
     * The change in score of player 2 in the saved game.
     */
    private static final String GAME_PLAYER_2_SCORE_CHANGE = "Player 2 change score";
    /**
     * Whether player 2 has just halved their score.
     */
    private static final String GAME_PLAYER_2_SCORE_HALVED = "Player 2 score halved";
    /**
     * The round in which player 2 went out.
     */
    private static final String GAME_PLAYER_2_ROUND_OUT = "Player 2 round out";
    /**
     * A "|" delimited list of the deck positions of player 3's cards.
     */
    private static final String GAME_PLAYER_3_HAND = "Player 3 hand";
    /**
     * Whether player 3 is just out
     */
    private static final String GAME_PLAYER_3_JUST_OUT = "Player 3 just out";
    /**
     * The score of player 3 in the saved game.
     */
    private static final String GAME_PLAYER_3_SCORE = "Player 3 score";
    /**
     * The change in score of player 3 in the saved game.
     */
    private static final String GAME_PLAYER_3_SCORE_CHANGE = "Player 3 change score";
    /**
     * Whether player 1 has just halved their score.
     */
    private static final String GAME_PLAYER_3_SCORE_HALVED = "Player 3 score halved";
    /**
     * The round in which player 3 went out.
     */
    private static final String GAME_PLAYER_3_ROUND_OUT = "Player 3 round out";
    /**
     * A "|" delimited list of the deck positions of player 4's cards.
     */
    private static final String GAME_PLAYER_4_HAND = "Player 4 hand";
    /**
     * Whether player 4 is just out
     */
    private static final String GAME_PLAYER_4_JUST_OUT = "Player 4 just out";
    /**
     * The score of player 4 in the saved game.
     */
    private static final String GAME_PLAYER_4_SCORE = "Player 4 score";
    /**
     * The change in score of player 4 in the saved game.
     */
    private static final String GAME_PLAYER_4_SCORE_CHANGE = "Player 4 change score";
    /**
     * Whether player 1 has just halved their score.
     */
    private static final String GAME_PLAYER_4_SCORE_HALVED = "Player 4 score halved";
    /**
     * The round in which player 4 went out.
     */
    private static final String GAME_PLAYER_4_ROUND_OUT = "Player 4 round out";
    /**
     * The name of player 1 in the saved game.
     */
    private static final String GAME_PLAYER_NAME = "Player Name";
    /**
     * Used as the label for all the shared preferences.
     */
    private static final String GAME_PREFERENCES = "GamePrefs";
    /**
     * Stores a '|' delimited string of integers representing achievements earned.
     */
    private static final String GAME_PREFERENCES_ACHIEVEMENTS = "Achievements";
    /**
     * Used to store the location of the file used for a player's avatar.
     */
    private static final String GAME_PREFERENCES_AVATAR = "Avatar"; // String
    /**
     * Used to store the speed of the computer players.
     */
    private static final String GAME_PREFERENCES_SPEED = "Speed"; // Int
    private static final String GAME_PREFERENCES_EMAIL = "Email"; // String
    private static final String GAME_PREFERENCES_FACEBOOK = "Facebook"; // Boolean
    private static final String GAME_PREFERENCES_FACEBOOK_TOKEN = "Facebook Token"; // String
    private static final String GAME_PREFERENCES_FACEBOOK_EXPIRES = "Facebook Expiry"; // Long
    private static final String GAME_PREFERENCES_FIRST_TIME = "First time"; // Boolean
    private static final String GAME_PREFERENCES_FRIENDS = "Friends"; // String
    private static final String GAME_PREFERENCES_FOLLOWERS = "Followers"; // String
    /**
     * Stores the date when the friends and followers were last checked against the web.
     */
    private static final String GAME_PREFERENCES_FRIENDS_UPDATED = "Friends updated"; // Long
    /**
     * Used to store whether mute is preferred to sound effects.
     */
    private static final String GAME_PREFERENCES_MUTE = "Mute"; // Boolean
    /**
     * Used to store the language that the application will be displayed in.
     */
    private static final String GAME_PREFERENCES_LANGUAGE = "Language"; // String
    private static final String GAME_PREFERENCES_YANIV_MINIMUM = "Yaniv Minimum"; // Int
    private static final String GAME_YANIV_MINIMUM = "Game Yaniv Minimum"; // Int
    /**
     * Used to store the server player id for a player
     */
    private static final String GAME_PREFERENCES_PLAYER_ID = "Player Id"; // Int
    /**
     * Used to store player 1's name.
     */
    private static final String GAME_PREFERENCES_NAME = "Name"; // String
    /**
     * Integer used to determine how many players will be in a new game.
     */
    private static final String GAME_PREFERENCES_NUM_PLAYERS = "Num Players";
    /**
     * Stores a count of how many games a player has completed.
     */
    private static final String GAME_PREFERENCES_PLAYED = "Played"; // Int
    /**
     * Used to store a player's rating.
     */
    private static final String GAME_PREFERENCES_RATING = "Rating"; // Int
    /**
     * Stores a count of how many games a player has won.
     */
    private static final String GAME_PREFERENCES_WON = "Won"; // Int
    /**
     * An integer representing the previous player in the saved game.
     */
    private static final String GAME_PREVIOUS_PLAYER = "Previous Player";
    /**
     * An integer for the current round number in the saved game.
     */
    private static final String GAME_ROUND = "Round";
    /**
     * An integer for the number of players in the round.
     */
    private static final String GAME_ROUND_PLAYERS = "Round Players";
    /*
     * An integer representing the turn number.
     */
    private static final String GAME_TURN = "Turn";
    /**
     * An integer for the number of 2 player games won without losses.
     */
    private static final String GAME_WON_2PLAYER = "Won 2 Player";
    /**
     * An integer for the number of 3 player games won without losses.
     */
    private static final String GAME_WON_3PLAYER = "Won 3 Player";
    /**
     * An integer for the number of 4 player games won without losses.
     */
    private static final String GAME_WON_4PLAYER = "Won 4 Player";
    /**
     * An integer representing the winner of the previous round in the saved game.
     */
    private static final String GAME_WINNER = "Winner";
    /**
     * A boolean indicating that Yaniv was possible the previous go but you chose not to take it.
     */
    private static final String GAME_YANIV_POSSIBLE = "Yaniv possible";
    /**
     * An integer representing how many Yanivs in a row you currently have.
     */
    private static final String GAME_YANIVS = "Yaniv";
    /**
     * An integer representing the current step in the tutorial.
     */
    private static final String TUTORIAL_STEP = "Tutorial Step";
    /**
     * A string representing the text for the tutorial console.
     */
    private static final String TUTORIAL_CONSOLE = "Tutorial console";
    /**
     * A boolean representing whether the server has been updated since the last data change.
     */
    private static final String GAME_PREFERENCES_SERVER_UPDATED = "Server updated";
    private static final String GAME_CARD_PICKED_UP = "Cards picked up";
    private static final String GAME_CARDS_DROPPED = "Cards dropped";
    private static final String GAME_TURN_TYPE = "Turn type";
    private static AppPreferences _instance;
    private static Context mContext;
    private static SharedPreferences appSharedPrefs;
    private static Editor prefsEditor;

    /**
     * Creates a new AppPreferences for the given context. Since this is private it can only be obtained via the getInstance method. It then defines a reader and editor for the preferences.
     *
     * @param context
     */
    private AppPreferences(Context context) {
        mContext = context;
        appSharedPrefs = context.getSharedPreferences(GAME_PREFERENCES, Activity.MODE_PRIVATE);
        prefsEditor = appSharedPrefs.edit();
    }

    /**
     * Requests the instance of the App Preferences and creates it if it does not exist.
     *
     * @return Returns the single instance of the AppPreferences
     */
    public static synchronized AppPreferences getInstance(Context context) {
        if (_instance == null)
            _instance = new AppPreferences(context);
        return _instance;
    }

    /**
     * Deletes all the preferences related to a saved game state.
     */
    public void deleteGame() {
        prefsEditor.remove(GAME_NUM_PLAYERS);
        prefsEditor.remove(GAME_ROUND);
        prefsEditor.remove(GAME_ROUND_PLAYERS);
        prefsEditor.remove(GAME_CONSOLE);
        prefsEditor.remove(GAME_WINNER);
        prefsEditor.remove(GAME_CURRENT_PLAYER);
        prefsEditor.remove(GAME_PREVIOUS_PLAYER);
        prefsEditor.remove(GAME_PLAYER_NAME);

        prefsEditor.remove(GAME_PLAYER_1_SCORE);
        prefsEditor.remove(GAME_PLAYER_1_HAND);
        prefsEditor.remove(GAME_PLAYER_1_JUST_OUT);
        prefsEditor.remove(GAME_PLAYER_1_ROUND_OUT);

        prefsEditor.remove(GAME_PLAYER_2_SCORE);
        prefsEditor.remove(GAME_PLAYER_2_HAND);
        prefsEditor.remove(GAME_PLAYER_2_JUST_OUT);
        prefsEditor.remove(GAME_PLAYER_2_ROUND_OUT);

        prefsEditor.remove(GAME_PLAYER_3_SCORE);
        prefsEditor.remove(GAME_PLAYER_3_HAND);
        prefsEditor.remove(GAME_PLAYER_3_JUST_OUT);
        prefsEditor.remove(GAME_PLAYER_3_ROUND_OUT);

        prefsEditor.remove(GAME_PLAYER_4_SCORE);
        prefsEditor.remove(GAME_PLAYER_4_HAND);
        prefsEditor.remove(GAME_PLAYER_4_JUST_OUT);
        prefsEditor.remove(GAME_PLAYER_4_ROUND_OUT);

        prefsEditor.remove(GAME_PILE_CARDS);
        prefsEditor.remove(GAME_DECK_CARDS);
        prefsEditor.remove(GAME_YANIVS);
        prefsEditor.remove(GAME_YANIV_POSSIBLE);
        prefsEditor.remove(GAME_YANIV_MINIMUM);
        prefsEditor.remove(GAME_ASSAF);
        prefsEditor.remove(GAME_HALVES);
        prefsEditor.remove(GAME_TURN);
        prefsEditor.remove(GAME_TURN_TYPE);
        prefsEditor.remove(GAME_CARDS_DROPPED);
        prefsEditor.remove(GAME_CARD_PICKED_UP);
        prefsEditor.commit();
    }

    /**
     * Deletes the count of how many 2, 3 and 4 player games that a player has won.
     */
    public void deleteWonPlayerGames() {
        prefsEditor.remove(GAME_WON_2PLAYER);
        prefsEditor.remove(GAME_WON_3PLAYER);
        prefsEditor.remove(GAME_WON_4PLAYER);
        prefsEditor.commit();
    }

    public void deleteTutorial() {
        prefsEditor.remove(TUTORIAL_STEP);
        prefsEditor.remove(TUTORIAL_CONSOLE);
        prefsEditor.commit();
    }

    /**
     * @return The path to the player's avatar.
     */
    public String getAvatar() {
        return appSharedPrefs.getString(GAME_PREFERENCES_AVATAR, null);
    }

    /**
     * Sets the path to the player's avatar.
     *
     * @param avatar A string representing the full filename of the avatar image.
     */
    public void setAvatar(String avatar) {
        prefsEditor.putString(GAME_PREFERENCES_AVATAR, avatar);
        prefsEditor.commit();
    }

    /**
     * @return The speed of the computer players.
     */
    public int getComputerSpeed() {
        return appSharedPrefs.getInt(GAME_PREFERENCES_SPEED, 1);
    }

    public void setComputerSpeed(int speed) {
        prefsEditor.putInt(GAME_PREFERENCES_SPEED, speed);
        prefsEditor.commit();
    }

    /**
     * @return The text on the console used to describe all the moves in the game.
     */
    public String getConsoleText() {
        return appSharedPrefs.getString(GAME_CONSOLE, "");
    }

    /**
     * Saves the text on the console describing all the moves in the game.
     *
     * @param text The complete text of the console. This can be a very large string.
     */
    public void setConsoleText(String text) {
        prefsEditor.putString(GAME_CONSOLE, text);
        prefsEditor.commit();
    }

    public boolean isServerUpdated() {
        return false;
        // return appSharedPrefs.getBoolean(GAME_PREFERENCES_SERVER_UPDATED, false);
    }

    public void setServerUpdated(boolean updated) {
        prefsEditor.putBoolean(GAME_PREFERENCES_SERVER_UPDATED, updated);
        prefsEditor.commit();
    }

    public void saveGameTurn(GameTurn turn) {
        if (turn != null && turn.cardPickedUp != null) {
            prefsEditor.putInt(GAME_CARD_PICKED_UP, turn.cardPickedUp.getDeckIndex());
        } else {
            prefsEditor.remove(GAME_CARD_PICKED_UP);
        }
        if (turn != null && turn.cardsDropped != null && turn.cardsDropped.size() > 0) {
            StringBuilder cardString = new StringBuilder();
            for (Card card : turn.cardsDropped) {
                cardString.append(card.getDeckIndex()).append("|");
            }
            prefsEditor.putString(GAME_CARDS_DROPPED, cardString.toString());
        } else {
            prefsEditor.remove(GAME_CARDS_DROPPED);
        }
        if (turn != null && turn.turnType != null) {
            prefsEditor.putInt(GAME_TURN_TYPE, turn.turnType.ordinal());
        } else {
            prefsEditor.remove(GAME_TURN_TYPE);
        }
        prefsEditor.commit();
    }

    public GameTurn getGameTurn() {
        GameTurn turn = new GameTurn();
        if (appSharedPrefs.contains(GAME_TURN_TYPE)) {
            turn.turnType = Turn.values()[appSharedPrefs.getInt(GAME_TURN_TYPE, 0)];
            if (appSharedPrefs.contains(GAME_CARD_PICKED_UP)) {
                turn.cardPickedUp = Deck.getCard(appSharedPrefs.getInt(GAME_CARD_PICKED_UP, 0));
            }
            if (appSharedPrefs.contains(GAME_CARDS_DROPPED)) {
                String[] cards = appSharedPrefs.getString(GAME_CARDS_DROPPED, "").split("\\|");
                for (String card : cards) {
                    if (!card.equals("")) {
                        turn.cardsDropped.add(Deck.getCard(Integer.parseInt(card)));
                    }
                }
            }
        }
        return turn;
    }

    /**
     * @return Returns the current player in saved game.
     */
    public int getCurrentPlayer() {
        Log.i(Const.DEBUG_TAG, "Restoring current player as " + appSharedPrefs.getInt(GAME_CURRENT_PLAYER, 0));
        return appSharedPrefs.getInt(GAME_CURRENT_PLAYER, 0);
    }

    /**
     * Saves the current player of a game.
     *
     * @param currentPlayer An integer representing the player whose turn it is.
     */
    public void setCurrentPlayer(int currentPlayer) {
        Log.i(Const.DEBUG_TAG, "Saving current player as " + currentPlayer);
        prefsEditor.putInt(GAME_CURRENT_PLAYER, currentPlayer);
        prefsEditor.commit();

    }

    public int getPlayerId() {
        return appSharedPrefs.getInt(GAME_PREFERENCES_PLAYER_ID, -1);
    }

    public void setPlayerId(int playerId) {
        prefsEditor.putInt(GAME_PREFERENCES_PLAYER_ID, playerId);
        prefsEditor.commit();
    }

    public String getEmail() {
        return appSharedPrefs.getString(GAME_PREFERENCES_EMAIL, "");
    }

    public void setEmail(String email) {
        prefsEditor.putString(GAME_PREFERENCES_EMAIL, email);
        prefsEditor.commit();
        this.setServerUpdated(false);
    }

    public boolean isFacebookEnabled() {
        return appSharedPrefs.getBoolean(GAME_PREFERENCES_FACEBOOK, false);
    }

    public void setFacebookEnabled(boolean enabled) {
        prefsEditor.putBoolean(GAME_PREFERENCES_FACEBOOK, enabled);
        prefsEditor.commit();
    }

    public String getFacebookToken() {
        return appSharedPrefs.getString(GAME_PREFERENCES_FACEBOOK_TOKEN, null);
    }

    public long getFacebookTokenExpires() {
        return appSharedPrefs.getLong(GAME_PREFERENCES_FACEBOOK_EXPIRES, -1);
    }

    public boolean isFirstTime() {
        return appSharedPrefs.getBoolean(GAME_PREFERENCES_FIRST_TIME, true);
    }

    public void setFirstTime(boolean firstTime) {
        prefsEditor.putBoolean(GAME_PREFERENCES_FIRST_TIME, firstTime);
        prefsEditor.commit();
    }

    /**
     * @return Returns the number of players in a saved game.
     */
    public int getGameNumPlayers() {
        return appSharedPrefs.getInt(GAME_NUM_PLAYERS, 2);
    }

    public void setGameNumPlayers(int players) {
        prefsEditor.putInt(GAME_NUM_PLAYERS, players);
        prefsEditor.commit();
    }

    /**
     * @return Returns the player name in the game. This is actually overridden by the preferred name but if this is null then Player 1 is used.
     */
    public String getGamePlayerName() {
        return appSharedPrefs.getString(GAME_PREFERENCES_NAME, appSharedPrefs.getString(GAME_PLAYER_NAME, "Player 1"));
    }

    /**
     * @return Whether mute is preferred for the game.
     */
    public Boolean getMute() {
        return appSharedPrefs.getBoolean(GAME_PREFERENCES_MUTE, false);
    }

    /**
     * Sets the preferred mute status.
     *
     * @param mute A boolean parameter.
     */
    public void setMute(boolean mute) {
        prefsEditor.putBoolean(GAME_PREFERENCES_MUTE, mute);
        prefsEditor.commit();
    }

    /**
     * @return Returns the number of games that the user has completed.
     */

    public int getPlayed() {
        return appSharedPrefs.getInt(GAME_PREFERENCES_PLAYED, 0);
    }

    /**
     * Saves the number of games that the user has completed.
     *
     * @param played An integer representing the number of games played.
     */
    private void setPlayed(int played) {
        prefsEditor.putInt(GAME_PREFERENCES_PLAYED, played);
        prefsEditor.commit();
        this.setServerUpdated(false);
    }

    /**
     * @return Returns the preferred number of players for a new game.
     */
    public int getPreferencesNumPlayers() {
        return appSharedPrefs.getInt(GAME_PREFERENCES_NUM_PLAYERS, 2);
    }

    /**
     * Settings the preferred number of players when starting a new game.
     *
     * @param players An integer between 2 and 4.
     */
    public void setPreferencesNumPlayers(int players) {
        prefsEditor.putInt(GAME_PREFERENCES_NUM_PLAYERS, players);
        prefsEditor.commit();
    }

    /**
     * @return Returns the preferred player name defined in the settings screen.
     */
    public String getPreferencesPlayerName() {
        return appSharedPrefs.getString(GAME_PREFERENCES_NAME, "");
    }

    /**
     * @return Returns the previous player in saved game.
     */
    public int getPreviousPlayer() {
        Log.i(Const.DEBUG_TAG, "Restoring previous player as " + appSharedPrefs.getInt(GAME_PREVIOUS_PLAYER, 0));
        return appSharedPrefs.getInt(GAME_PREVIOUS_PLAYER, 0);
    }

    /**
     * Saves the player that went before the current one in a game.
     *
     * @param previousPlayer An integer representing the player whose turn it just was.
     */
    public void setPreviousPlayer(int previousPlayer) {
        Log.i(Const.DEBUG_TAG, "Saving previous player as " + previousPlayer);
        prefsEditor.putInt(GAME_PREVIOUS_PLAYER, previousPlayer);
        prefsEditor.commit();

    }

    /**
     * @return The numerical rating of a player. This increases and decreases based on wins and losses. It defaults to 1000.
     */
    public int getRating() {
        return appSharedPrefs.getInt(GAME_PREFERENCES_RATING, 1000);
    }

    private void setRating(int rating) {
        prefsEditor.putInt(GAME_PREFERENCES_RATING, rating);
        prefsEditor.commit();
        this.setServerUpdated(false);
    }

    /**
     * @return The round number of the current game of Yaniv.
     */
    public int getRound() {
        return appSharedPrefs.getInt(GAME_ROUND, 0);
    }

    /**
     * Saves the round number of the current game.
     *
     * @param round An integer representing the round number.
     */
    public void setRound(int round) {
        prefsEditor.putInt(GAME_ROUND, round);
        prefsEditor.commit();
    }

    /**
     * @return The turn number of the current round of Yaniv.
     */
    public int getTurn() {
        return appSharedPrefs.getInt(GAME_TURN, -1);
    }

    /**
     * Saves the turn number of the current round.
     *
     * @param turn An integer representing the turn number.
     */
    public void setTurn(int turn) {
        prefsEditor.putInt(GAME_TURN, turn);
        prefsEditor.commit();
    }

    /**
     * @return The number of players who were still in at the start of the round.
     */
    public int getRoundPlayers() {
        return appSharedPrefs.getInt(GAME_ROUND_PLAYERS, Game.getPlayersStillIn());
    }

    /**
     * Saves the number of players who were still in at the start of the round.
     *
     * @param players An integer representing how many players were still in the game.
     */
    public void setRoundPlayers(int players) {
        prefsEditor.putInt(GAME_ROUND_PLAYERS, players);
        prefsEditor.commit();
    }

    public int getTutorialStep() {
        return appSharedPrefs.getInt(TUTORIAL_STEP, 0);
    }

    /**
     * Saves the current step in the tutorial.
     *
     * @param tutorialStep An integer representing the current step within the tutorial.
     */
    public void setTutorialStep(int tutorialStep) {
        prefsEditor.putInt(TUTORIAL_STEP, tutorialStep - 1);
        prefsEditor.commit();
    }

    public String getTutorialConsole() {
        String str = mContext.getResources().getString(R.string.menu_item_tutorial);
        return appSharedPrefs.getString(TUTORIAL_CONSOLE, str + "\n=========\n");
    }

    public void setTutorialConsole(String tutorialConsole) {
        prefsEditor.putString(TUTORIAL_CONSOLE, tutorialConsole);
        prefsEditor.commit();
    }

    /**
     * @return The winner of the game/last round. If the round is not yet over this should be -1.
     */
    public int getWinner() {
        Log.i(Const.DEBUG_TAG, "Restoring winner as " + appSharedPrefs.getInt(GAME_WINNER, -1));
        return appSharedPrefs.getInt(GAME_WINNER, -1);
    }

    /**
     * Saves the winner of a game. If the round is still in progress this will be -1.
     *
     * @param winner An integer representing the winning player.
     */
    public void setWinner(int winner) {
        Log.i(Const.DEBUG_TAG, "Saving winner as " + winner);
        prefsEditor.putInt(GAME_WINNER, winner);
        prefsEditor.commit();
    }

    /**
     * @return The number of games that the user has won.
     */
    public int getWon() {
        return appSharedPrefs.getInt(GAME_PREFERENCES_WON, 0);
    }

    /**
     * Saves the number of games that the user has won.
     *
     * @param won An integer representing the number of games won.
     */
    private void setWon(int won) {
        prefsEditor.putInt(GAME_PREFERENCES_WON, won);
        prefsEditor.commit();
        this.setServerUpdated(false);
    }

    /**
     * @return The number of games that the user has won.
     */
    public int get2PlayerWon() {
        return appSharedPrefs.getInt(GAME_WON_2PLAYER, 0);
    }

    /**
     * @return The number of games that the user has won.
     */
    public int get3PlayerWon() {
        return appSharedPrefs.getInt(GAME_WON_3PLAYER, 0);
    }

    /**
     * @return The number of games that the user has won.
     */
    public int get4PlayerWon() {
        return appSharedPrefs.getInt(GAME_WON_4PLAYER, 0);
    }

    public int getYanivMinimum() {
        return appSharedPrefs.getInt(GAME_PREFERENCES_YANIV_MINIMUM, 5);
    }

    public void setYanivMinimum(int minimum) {
        prefsEditor.putInt(GAME_PREFERENCES_YANIV_MINIMUM, minimum);
        prefsEditor.commit();
    }

    public int getGameYanivMinimum() {
        return appSharedPrefs.getInt(GAME_YANIV_MINIMUM, 5);
    }

    public void setGameYanivMinimum(int minimum) {
        prefsEditor.putInt(GAME_YANIV_MINIMUM, minimum);
        prefsEditor.commit();
    }

    /**
     * @return Whether the user could have called Yaniv on the previous turn.
     */
    public boolean getYanivPossible() {
        return appSharedPrefs.getBoolean(GAME_YANIV_POSSIBLE, false);
    }

    /**
     * Saves whether the user could have called Yaniv on the previous turn.
     *
     * @param possible If the user could have called Yaniv on the previous go
     */
    public void setYanivPossible(boolean possible) {
        prefsEditor.putBoolean(GAME_YANIV_POSSIBLE, possible);
        prefsEditor.commit();
    }

    /**
     * @return The number of Yanivs in a row that the user has had in this game.
     */
    public int getYanivsInARow() {
        return appSharedPrefs.getInt(GAME_YANIVS, 0);
    }

    /**
     * @return The number of Assafs that the user has caused in this game.
     */
    public int getAssafsThisGame() {
        return appSharedPrefs.getInt(GAME_ASSAF, 0);
    }

    /**
     * @return The number of times the user has halved their score in a game.
     */
    public int getHalves() {
        return appSharedPrefs.getInt(GAME_HALVES, 0);
    }

    /**
     * Saves the number of times that the user has halved their score this game.
     */

    public void setHalves(int halved) {
        prefsEditor.putInt(GAME_HALVES, halved);
        prefsEditor.commit();
    }

    /**
     * @return Returns true if all the ratings etc. have been updated.
     */
    public boolean isPlayer1ScoreUpToDate() {
        return !appSharedPrefs.contains(GAME_PLAYER_1_JUST_OUT);
    }

    /**
     * @return Returns true if a saved game exists.
     */
    public boolean isSavedGame() {
        return appSharedPrefs.contains(GAME_PLAYER_NAME);
    }

    /**
     * Saves all the achievements as a delimited list.
     *
     * @param achievements A boolean array indicating which achievements a player has attained.
     */
    public void saveAchievements(boolean[] achievements) {
        StringBuilder achievementsString = new StringBuilder();
        for (int i = 0; i < achievements.length; i++) {
            if (achievements[i]) {
                achievementsString.append(i).append("|");
            }
        }
        prefsEditor.putString(GAME_PREFERENCES_ACHIEVEMENTS, achievementsString.toString());
        prefsEditor.commit();
        this.setServerUpdated(false);
    }

    /**
     * Saves the state of the deck.
     *
     * @param deck The Deck object containing all the cards remaining the deck.
     */
    public void saveDeck(CardVector deck) {
        Log.i(Const.DEBUG_TAG, "Saving deck: " + deck.print());
        StringBuilder cardString = new StringBuilder();
        for (Card card : deck) {
            cardString.append(card.getDeckIndex()).append("|");
        }
        prefsEditor.putString(GAME_DECK_CARDS, cardString.toString());
        prefsEditor.commit();
    }

    /**
     * Saves the state of the pile.
     *
     * @param pile The Pile object containing all the cards on the pile.
     */
    public void savePile(Pile pile) {
        if (pile.size() > 0) {
            StringBuilder cardString = new StringBuilder();
            for (PileCard card : pile) {
                cardString.append(card.getDeckIndex()).append("|");
            }
            prefsEditor.putString(GAME_PILE_CARDS, cardString.toString());
        } else {
            prefsEditor.remove(GAME_PILE_CARDS);
        }
        prefsEditor.commit();
    }

    /**
     * Saves the given player in a game. This includes their score, the cards in their hand and whether they were just out.
     *
     * @param player The Player object that contains all the relevant information.
     */
    public void savePlayer(Player player) {
        StringBuilder cardString = new StringBuilder();
        Log.i(Const.DEBUG_TAG, "Saving: " + player.getName() + " : " + player.printHand());
        if (player.getHand().size() > 0) {
            for (HandCard card : player.getHand()) {
                cardString.append(card.getDeckIndex()).append("|");
            }
        }
        switch (player.getID()) {
            case 1:
                prefsEditor.putString(GAME_PLAYER_NAME, player.getName());
                prefsEditor.putInt(GAME_PLAYER_1_SCORE, player.getScore());
                prefsEditor.putInt(GAME_PLAYER_1_SCORE_CHANGE, player.getScoreChange());
                prefsEditor.putBoolean(GAME_PLAYER_1_SCORE_HALVED, player.isScoreHalved());
                if (!cardString.toString().equals("")) {
                    prefsEditor.putString(GAME_PLAYER_1_HAND, cardString.toString());
                } else {
                    prefsEditor.remove(GAME_PLAYER_1_HAND);
                }
                if (player.isJustOut()) {
                    prefsEditor.putBoolean(GAME_PLAYER_1_JUST_OUT, player.isJustOut());
                } else {
                    prefsEditor.remove(GAME_PLAYER_1_JUST_OUT);
                }
                prefsEditor.putInt(GAME_PLAYER_1_ROUND_OUT, player.getRoundOut());

                break;
            case 2:
                prefsEditor.putInt(GAME_PLAYER_2_SCORE, player.getScore());
                prefsEditor.putInt(GAME_PLAYER_2_SCORE_CHANGE, player.getScoreChange());
                prefsEditor.putBoolean(GAME_PLAYER_2_SCORE_HALVED, player.isScoreHalved());
                if (!cardString.toString().equals("")) {
                    prefsEditor.putString(GAME_PLAYER_2_HAND, cardString.toString());
                } else {
                    prefsEditor.remove(GAME_PLAYER_2_HAND);
                }
                if (player.isJustOut()) {
                    prefsEditor.putBoolean(GAME_PLAYER_2_JUST_OUT, player.isJustOut());
                } else {
                    prefsEditor.remove(GAME_PLAYER_2_JUST_OUT);
                }
                prefsEditor.putInt(GAME_PLAYER_2_ROUND_OUT, player.getRoundOut());
                break;
            case 3:
                prefsEditor.putInt(GAME_PLAYER_3_SCORE, player.getScore());
                prefsEditor.putInt(GAME_PLAYER_3_SCORE_CHANGE, player.getScoreChange());
                prefsEditor.putBoolean(GAME_PLAYER_3_SCORE_HALVED, player.isScoreHalved());
                if (!cardString.toString().equals("")) {
                    prefsEditor.putString(GAME_PLAYER_3_HAND, cardString.toString());
                } else {
                    prefsEditor.remove(GAME_PLAYER_3_HAND);
                }
                if (player.isJustOut())
                    prefsEditor.putBoolean(GAME_PLAYER_3_JUST_OUT, player.isJustOut());
                else {
                    prefsEditor.remove(GAME_PLAYER_3_JUST_OUT);
                }
                prefsEditor.putInt(GAME_PLAYER_3_ROUND_OUT, player.getRoundOut());

                break;
            case 4:
                prefsEditor.putInt(GAME_PLAYER_4_SCORE, player.getScore());
                prefsEditor.putInt(GAME_PLAYER_4_SCORE_CHANGE, player.getScoreChange());
                prefsEditor.putBoolean(GAME_PLAYER_4_SCORE_HALVED, player.isScoreHalved());
                if (!cardString.toString().equals("")) {
                    prefsEditor.putString(GAME_PLAYER_4_HAND, cardString.toString());
                } else {
                    prefsEditor.remove(GAME_PLAYER_4_HAND);
                }
                if (player.isJustOut())
                    prefsEditor.putBoolean(GAME_PLAYER_4_JUST_OUT, player.isJustOut());
                else
                    prefsEditor.remove(GAME_PLAYER_4_JUST_OUT);
                prefsEditor.putInt(GAME_PLAYER_4_ROUND_OUT, player.getRoundOut());

                break;
        }
        prefsEditor.commit();
    }

    public void saveFacebookToken(String token, Long expires) {
        Log.i(Const.DEBUG_TAG, "Saving facebook token: " + token);
        Log.i(Const.DEBUG_TAG, "Token expires: " + new Date(expires).toString());
        prefsEditor.putString(GAME_PREFERENCES_FACEBOOK_TOKEN, token);
        prefsEditor.putLong(GAME_PREFERENCES_FACEBOOK_EXPIRES, expires);
        prefsEditor.commit();
    }

    /**
     * Saves the preferred player name.
     *
     * @param name A string for player 1.
     */
    public void setName(String name) {
        if (name != null && !name.equals("") && name.length() != 0) {
            prefsEditor.putString(GAME_PREFERENCES_NAME, name);
        } else {
            prefsEditor.remove(GAME_PREFERENCES_NAME);
        }
        prefsEditor.commit();
        this.setServerUpdated(false);
    }

    public void incrementPlayed() {
        this.setPlayed(this.getPlayed() + 1);
    }

    public void resetRating() {
        this.setPlayed(0);
        this.setRating(1000);
        this.setWon(0);
    }

    /**
     * Deducts a number of points from a player's current rating.
     *
     * @param deduct Number of points to deduct
     */
    public void deductRating(int deduct) {
        int rating = this.getRating();
        rating -= deduct;
        if (rating < 0) {
            rating = 0;
        }
        this.setRating(rating);
    }

    /**
     * Adds a number of points to a player's current rating.
     *
     * @param increment Number of points to increment
     */
    public void incrementRating(int increment) {
        this.setRating(this.getRating() + increment);
    }

    public void incrementWon() {
        this.setWon(this.getWon() + 1);
    }

    /**
     * Saves the number of 2 player games that the user has won without losses.
     */
    public void setWon2Player(int won) {
        prefsEditor.putInt(GAME_WON_2PLAYER, won);
        prefsEditor.commit();
    }

    /**
     * Saves the number of 3 player games that the user has won without losses.
     */
    public void setWon3Player(int won) {
        prefsEditor.putInt(GAME_WON_3PLAYER, won);
        prefsEditor.commit();
    }

    /**
     * Saves the number of 4 player games that the user has won without losses.
     */
    public void setWon4Player(int won) {
        prefsEditor.putInt(GAME_WON_4PLAYER, won);
        prefsEditor.commit();
    }

    /**
     * Saves the number of Yanivs in a row that the user has had so far this game.
     *
     * @param yanivs Number of yanivs in a row this game
     */
    public void setYanivs(int yanivs) {
        prefsEditor.putInt(GAME_YANIVS, yanivs);
        prefsEditor.commit();
    }

    /**
     * Saves the number of Assafs that the user has had so far this game.
     *
     * @param assafs Total number of assafs this game
     */
    public void setAssaf(int assafs) {
        prefsEditor.putInt(GAME_ASSAF, assafs);
        prefsEditor.commit();
    }

    /**
     * Updates the achievements array with all the saved achievements.
     *
     * @param achievements A boolean array that will be updated with the saved achievements.
     */
    public void updateAchievements(boolean[] achievements) {
        // String achieves = "0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|31|33|34|36|37";
        String[] achievementsString = appSharedPrefs.getString(GAME_PREFERENCES_ACHIEVEMENTS, "").split("\\|");
        // String[] achievementsString = achieves.split("\\|");
        // Set the attained achievements to true
        for (String achievement : achievementsString) {
            if (!achievement.equals("")) {
                achievements[Integer.parseInt(achievement)] = true;
            }
        }
    }

    public String getFriends() {
        return appSharedPrefs.getString(GAME_PREFERENCES_FRIENDS, "");
    }

    public void setFriends(Set<Long> friends) {
        StringBuilder friendsString = new StringBuilder();
        for (long friend : friends) {
            friendsString.append(friend).append("|");
        }
        prefsEditor.putString(GAME_PREFERENCES_FRIENDS, friendsString.toString());
        prefsEditor.commit();
    }

    public String getFollowers() {
        return appSharedPrefs.getString(GAME_PREFERENCES_FOLLOWERS, "");
    }

    public void setFollowers(Set<Long> followers) {
        StringBuilder followersString = new StringBuilder();
        for (Long follower : followers) {
            followersString.append(follower).append("|");
        }
        prefsEditor.putString(GAME_PREFERENCES_FOLLOWERS, followersString.toString());
        prefsEditor.commit();
    }

    public long getFriendsUpdated() {
        return appSharedPrefs.getLong(GAME_PREFERENCES_FRIENDS_UPDATED, 0);
    }

    public void setFriendsUpdated(long time) {
        prefsEditor.putLong(GAME_PREFERENCES_FRIENDS_UPDATED, time);
        prefsEditor.commit();
    }

    public String getLanguage() {
        return appSharedPrefs.getString(GAME_PREFERENCES_LANGUAGE, "default");
    }

    public void setLanguage(String locale) {
        prefsEditor.putString(GAME_PREFERENCES_LANGUAGE, locale);
        prefsEditor.commit();
    }

    public String getAchievements() {
        return appSharedPrefs.getString(GAME_PREFERENCES_ACHIEVEMENTS, "");
    }

    /**
     * Updates the Deck object with the saved deck state.
     *
     * @param deck The Deck object is updated with the cards from the saved game.
     */
    public void updateDeck(final CardVector deck) {
        deck.clear();
        if (appSharedPrefs.contains(GAME_DECK_CARDS)) {
            final String[] cards = appSharedPrefs.getString(GAME_DECK_CARDS, "").split("\\|");
            for (String card : cards) {
                deck.add(Deck.getCard(Integer.parseInt(card)));
            }
        }
    }

    /**
     * Updates the pile object with the saved state of the pile.
     *
     * @param pile The Pile object is updated with the cards from a saved game.
     */
    public void updatePile(Pile pile) {
        final CardVector pileCards = new CardVector();
        pile.clear();
        if (appSharedPrefs.contains(GAME_PILE_CARDS)) {
            final String[] cards = appSharedPrefs.getString(GAME_PILE_CARDS, "").split("\\|");
            for (String card : cards) {
                pileCards.add(Deck.getCard(Integer.parseInt(card)));
            }
            pile.addCards(pileCards);
        }
    }

    public int getPlayerScore(int playerId) {
        switch (playerId) {
            case 1:
                return appSharedPrefs.getInt(GAME_PLAYER_1_SCORE, 0);
            case 2:
                return appSharedPrefs.getInt(GAME_PLAYER_2_SCORE, 0);
            case 3:
                return appSharedPrefs.getInt(GAME_PLAYER_3_SCORE, 0);
            case 4:
                return appSharedPrefs.getInt(GAME_PLAYER_4_SCORE, 0);
        }
        return 0;
    }

    /**
     * Updates the given player with their saved game state. This includes their score, whether they were just out and the cards in their hand.
     *
     * @param player The Player object that contains all the player details.
     */
    public void updatePlayer(Player player) {
        String[] cards = null;
        switch (player.getID()) {
            case 1:
                cards = appSharedPrefs.getString(GAME_PLAYER_1_HAND, "").split("\\|");
                player.setScore(appSharedPrefs.getInt(GAME_PLAYER_1_SCORE, 0));
                player.setScoreChange(appSharedPrefs.getInt(GAME_PLAYER_1_SCORE_CHANGE, -1));
                player.setScoreHalved(appSharedPrefs.getBoolean(GAME_PLAYER_1_SCORE_HALVED, false));
                player.setJustOut(appSharedPrefs.getBoolean(GAME_PLAYER_1_JUST_OUT, false));
                player.setRoundOut(appSharedPrefs.getInt(GAME_PLAYER_1_ROUND_OUT, -1));

                break;
            case 2:
                cards = appSharedPrefs.getString(GAME_PLAYER_2_HAND, "").split("\\|");
                player.setScore(appSharedPrefs.getInt(GAME_PLAYER_2_SCORE, 0));
                player.setScoreChange(appSharedPrefs.getInt(GAME_PLAYER_2_SCORE_CHANGE, -1));
                player.setScoreHalved(appSharedPrefs.getBoolean(GAME_PLAYER_2_SCORE_HALVED, false));
                player.setJustOut(appSharedPrefs.getBoolean(GAME_PLAYER_2_JUST_OUT, false));
                player.setRoundOut(appSharedPrefs.getInt(GAME_PLAYER_2_ROUND_OUT, -1));

                break;
            case 3:
                cards = appSharedPrefs.getString(GAME_PLAYER_3_HAND, "").split("\\|");
                player.setScore(appSharedPrefs.getInt(GAME_PLAYER_3_SCORE, 0));
                player.setScoreChange(appSharedPrefs.getInt(GAME_PLAYER_3_SCORE_CHANGE, -1));
                player.setScoreHalved(appSharedPrefs.getBoolean(GAME_PLAYER_3_SCORE_HALVED, false));
                player.setJustOut(appSharedPrefs.getBoolean(GAME_PLAYER_3_JUST_OUT, false));
                player.setRoundOut(appSharedPrefs.getInt(GAME_PLAYER_3_ROUND_OUT, -1));

                break;
            case 4:
                cards = appSharedPrefs.getString(GAME_PLAYER_4_HAND, "").split("\\|");
                player.setScore(appSharedPrefs.getInt(GAME_PLAYER_4_SCORE, 0));
                player.setScoreChange(appSharedPrefs.getInt(GAME_PLAYER_4_SCORE_CHANGE, -1));
                player.setScoreHalved(appSharedPrefs.getBoolean(GAME_PLAYER_4_SCORE_HALVED, false));
                player.setJustOut(appSharedPrefs.getBoolean(GAME_PLAYER_4_JUST_OUT, false));
                player.setRoundOut(appSharedPrefs.getInt(GAME_PLAYER_4_ROUND_OUT, -1));

                break;

        }
        player.getHand().clear();
        for (final String card : cards) {
            if (!card.equals("")) {
                player.getHand().addCard(Deck.getCard(Integer.parseInt(card)));
            }
        }
        Log.i(Const.DEBUG_TAG, "Restored: " + player.getName() + " : " + player.printHand());
    }
}
