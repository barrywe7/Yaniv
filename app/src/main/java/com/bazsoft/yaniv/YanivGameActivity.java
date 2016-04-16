package com.bazsoft.yaniv;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.cards.Card;
import com.cards.CardVector;
import com.cards.Deck;
import com.cards.Hand;
import com.cards.HandCard;
import com.cards.Kind;
import com.cards.PileCard;
import com.cards.Straight;
import com.game.Game;
import com.game.GameTurn;
import com.game.Turn;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.player.Player;
import com.player.PlayerType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

public class YanivGameActivity extends YanivActivity implements OnClickListener, TextWatcher, DialogInterface.OnClickListener, AnimationListener,
        OnCancelListener, RadioGroup.OnCheckedChangeListener {

    private static final int ALERT_ACHIEVEMENT = 8;
    private static final int ALERT_INVALID_PILE_CARD = 4;
    private static final int ALERT_INVALID_SELECTION = 3;
    private static final int ALERT_NEW_GAME = 16;
    private static final int ALERT_NEW_GAME_PLAYERS = 17;
    private static final int ALERT_PICKUP_FROM_PILE = 10;
    private static final int ALERT_PICKUP_FROM_DECK = 11;
    private static final int ALERT_PRESS_YANIV = 12;
    private static final int ALERT_PICKUP_PILE_CARD = 13;
    private static final int ALERT_PICKUP_DECK_CARD = 14;
    private static final int ALERT_QUICKHANDS_TOO_MANY = 1;
    private static final int ALERT_QUICKHANDS_WRONG_CARD = 0;
    private static final int ALERT_WRONG_TURN = 2;
    private static final int ALERT_SCORE_ADD = 5;
    private static final int ALERT_SCORE_DEDUCT = 6;
    private static final int ALERT_TUTORIAL = 9;
    private static final int ALERT_TUTORIAL_END = 15;
    private static final int ALERT_WINNER = 7;
    private static final int CARD_BACK_IMAGE = R.drawable.card_b;
    private static final int CARDS_PER_PLAYER = 5;
    private static final int DECK_IMAGE = R.drawable.deck;
    private static final int DECK_IMAGE_VIEW = R.id.ImageView_Deck;
    /**
     * The human player on the phone. Currently always 0, if I ever introduce multiplayer this could be different.
     */
    private static final int THIS_PLAYER = 0;
    private static final int WHAT_COMPUTER_TURN = 1;
    private static final int WHAT_COMPUTER_QUICK_HANDS = 2;
    private static final int WHAT_CONSOLE_UPDATER = 3;
    private static final int WHAT_CLOSE_SCOREBOARD = 4;
    private static final int WHAT_SHOW_TUTORIAL_DIALOG = 5;
    private static long mTimeSinceQuickHands;
    private static long mTimeSinceComputerQuickHands;
    private static AccountTask mAccountUpload;
    /**
     * The text view where all the player's moves are described.
     */
    private static TextView mConsole;
    /**
     * Whether the game is paused or not.
     */
    private static boolean mPause;
    /**
     * A string containing the next dialog message.
     */
    private static String mDialogMessage;
    /**
     * Boolean that shows that human player has a quick hands opportunity.
     */
    private static boolean mQuickHands = false;
    /**
     * A boolean indicating whether speedup has been activated. This is only available once the human-controlled player is out of the game.
     */
    private static boolean mSpeedUp = false;
    /**
     * The number of Yanivs in a row that the human player has had.
     */
    private static int mYanivsInARow = 0;
    /**
     * Whether the human player could have called Yaniv on the previous turn.
     */
    private static boolean mYanivPossible = false;
    /**
     * A hash map storing the location of each player's cards in the layout.
     */
    private static HashMap<Integer, Integer> playerCardMap = new HashMap<>();
    /**
     * A hash map storing the drawable used for the compass bearing for each player.
     */
    private static HashMap<Integer, Integer> playerCompassMap = new HashMap<>();
    /**
     * A hash map storing the progress bar used for each computer player
     */
    private static HashMap<Integer, Integer> playerProgressBarMap = new HashMap<>();
    /**
     * The number of times that the player has halved their score this game.
     */
    private static int mHalf;
    /**
     * The number of assafs that the player has caused in this game.
     */
    private static int mAssafsThisGame;
    private static ComputerHandler mMainHandler;
    private static int mNumPlayersStartOfRound;

    /*
     * private void animateLastCard() { if (!mSpeedUp && Game.getCurrentPlayerIndex() != THIS_PLAYER) { final ViewGroup layout = (ViewGroup)
     * findViewById(playerCardMap.get(Game.getCurrentPlayerIndex())); final ImageView card = (ImageView)
     * layout.getChildAt(Game.getCurrentPlayer().getHand().size() - 1); int cardImage; switch (Game.getGameTurn().turnType) { case PICKUP_PILE: cardImage =
     * getResources().getIdentifier(Game.getCurrentPlayer().getHand().getLastCard().getAndroidImage(), "drawable", "com.bazsoft.yaniv"); break; default: cardImage
     * = CARD_BACK_IMAGE; } RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeLayout_Main);
     *
     * ImageView newCard = new ImageView(this); //Bitmap bm = BitmapFactory.
     *
     * //Bitmap scaledGalleryPic = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
     *
     * newCard.setImageResource(R.drawable.card_joker1); newCard.setAdjustViewBounds(true); newCard.setScaleType(ScaleType.FIT_XY); //newCard. int[] location =
     * new int[2]; card.getLocationOnScreen(location); RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
     * LayoutParams.WRAP_CONTENT); //params.leftMargin = location[0]; //params.topMargin = location[1]; params.setMargins(location[0], location[1], 0, 0);
     * params.height=card.getMeasuredHeight(); params.width=card.getMeasuredWidth(); rl.addView(newCard, params); } /*card.clearAnimation(); final float padding =
     * card.getPaddingLeft(); final AnimatorSet set = new AnimatorSet(); set.playTogether( ObjectAnimator.ofFloat(card, "rotationY", 0.0f, 180.0f),
     * ObjectAnimator.ofFloat(card, "x", padding)); set.setDuration(1500).start(); Log.d(DEBUG_TAG, "Starting animation for "+ card);
     *
     *
     * set.addListener(new AnimatorListenerAdapter () {
     *
     * @Override public void onAnimationEnd(Animator animator) { if (Game.getWinner() == -1 && mGameMode != GameMode.TUTORIAL.ordinal()) {
     * card.setImageResource(CARD_BACK_IMAGE); } else { card.setImageResource(getResources().getIdentifier(((Card) card.getTag()).getAndroidImage(), "drawable",
     * "com.bazsoft.yaniv")); } card.clearAnimation(); //onAnimationEnd(animator); //card.setAnimation(null); }
     *
     * @Override public void onAnimationCancel(Animator animation) { card.clearAnimation(); set.end(); }
     *
     * }); } }
     */
    private static SlidingDrawer mSlidingDrawer;
    private static int mTutorialStep;
    private static Turn mTutorialTurn;
    private static CardVector mDropCards = new CardVector();
    private static Card mPickupCard;
    private static String mFacebookMessage;
    private static String mHeyZapMessage;
    private static Integer mIntTargetValue;
    private static int mGameMode;
    private Game game;
    private ComputerPlayerTask mComputerPlayerTask = new ComputerPlayerTask();

    /**
     * Creates new player objects
     *
     * @param playerName The name of player 1 (the human player).
     */
    private static Player[] createPlayers(String playerName, int numPlayers) {
        Player[] players = new Player[numPlayers];
        players[THIS_PLAYER] = new Player(playerName, 1);
        for (int i = 0; i < numPlayers; i++) {
            if (i != THIS_PLAYER) {
                players[i] = new Player("Player " + (i + 1), PlayerType.COMPUTER, (i + 1));
            }
        }
        return players;
    }

    /**
     * Defines which layout objects are used for each player's cards.
     */
    private static void initPlayerMaps() {
        final int BOTTOM = 0;
        final int LEFT = 1;
        final int TOP = 2;
        final int RIGHT = 3;
        final int playerCards[] = {R.id.LinearLayout_BottomPlayer, R.id.FrameLayout_LeftPlayer, R.id.FrameLayout_TopPlayer, R.id.FrameLayout_RightPlayer};
        final int playerBearings[] = {R.drawable.compass_south, R.drawable.compass_west, R.drawable.compass_north, R.drawable.compass_east};
        final int playerProgressBars[] = {0, R.id.ProgressBarLeft, R.id.ProgressBarTop, R.id.ProgressBarRight};

        // Player 1's cards are always on the bottom
        playerCardMap.put(0, playerCards[BOTTOM]);
        playerCompassMap.put(0, playerBearings[BOTTOM]);
        switch (Game.getNumPlayers()) {
            case 2:
                // Two players. Therefore player 2 at the top
                playerCardMap.put(1, playerCards[TOP]);
                playerCompassMap.put(1, playerBearings[TOP]);
                playerProgressBarMap.put(1, playerProgressBars[TOP]);
                break;
            case 3:
            case 4:
                playerCardMap.put(1, playerCards[LEFT]);
                playerCompassMap.put(1, playerBearings[LEFT]);
                playerProgressBarMap.put(1, playerProgressBars[LEFT]);
                playerCardMap.put(2, playerCards[TOP]);
                playerCompassMap.put(2, playerBearings[TOP]);
                playerProgressBarMap.put(2, playerProgressBars[TOP]);
                playerCardMap.put(3, playerCards[RIGHT]);
                playerCompassMap.put(3, playerBearings[RIGHT]);
                playerProgressBarMap.put(3, playerProgressBars[RIGHT]);
                break;
        }
    }

    private static void makeViewVisible(View view, boolean visible) {
        if (visible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * update the server with the latest settings data - everything but the image
     */
    private void updateServerData() {
        // make sure we don't collide with another pending update
        if (mAccountUpload == null || mAccountUpload.getStatus() == AsyncTask.Status.FINISHED || mAccountUpload.isCancelled()) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.ProgressBar_Game);
            mAccountUpload = new AccountTask(getApplicationContext(), progressBar);
            mAccountUpload.execute();
        } else {
            Log.w(DEBUG_TAG, "Warning: update task already in progress");
        }
    }

    private void printTurn() {
        final GameTurn turn = game.getGameTurn();
        switch (turn.turnType) {
            case PICKUP_PILE:
                mConsole.append(game.getCurrentPlayer().getName() + " drops " + turn.cardsDropped.print() + "and picks up " + turn.cardPickedUp.toShortString() + "\n");
                break;
            case PICKUP_DECK:
                mConsole.append(game.getCurrentPlayer().getName() + " drops " + turn.cardsDropped.print() + "and picks up from the deck" + "\n");
                break;
            case CALL_YANIV:
                mConsole.append(game.getCurrentPlayer().getName() + " has called Yaniv with " + game.getCurrentPlayer().getHand().getValue() + " points\n");
                if (game.getCurrentPlayerIndex() != game.getWinner()) {
                    final int score = game.getWinningPlayer().getScore();
                    for (Player player : game.getPlayers()) {
                        if (player.getScore() == score && (player.getID() - 1) != game.getCurrentPlayerIndex()) {
                            mConsole.append(game.getCurrentPlayer().getName() + " has been ASSAF-ed by " + player.getName() + "\n");
                        }
                    }
                }
                for (final Player player : game.getPlayers()) {
                    if (player.isJustOut()) {
                        mConsole.append(player.getName() + " is OUT!\n");
                    }
                }
                break;
        }
        if (game.getCurrentPlayerIndex() == THIS_PLAYER) {
            updateYanivAtColour();
        }
    }

    /**
     * Used to scroll the console to the last message automatically.
     *
     * @param s The editable
     */
    @Override
    public void afterTextChanged(Editable s) {
        int lineCount = mConsole.getLineCount();
        // If internal layout has not been built yet.
        if (lineCount == 0) {
            return;
        }
        int viewHeight = mConsole.getMeasuredHeight();
        int lineHeight = mConsole.getLineHeight();

        int difference = (lineCount * lineHeight) - viewHeight;
        if (difference > 1 && mConsole.getScrollY() < difference) {
            mConsole.scrollTo(0, difference);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Ignored
    }

    public void checkQuickHands() {
        // If the previous player picked up from the deck
        if (game.getPreviousPlayerIndex() != -1 && game.getGameTurn() != null && game.getGameTurn().turnType == Turn.PICKUP_DECK) {
            PlayerType currPlayerType = game.getCurrentPlayerType();
            PlayerType prevPlayerType = game.getPreviousPlayerType();
            Card lastCard = game.getLastCardPickedUp();

            int randomInt;
            // If the previous player played a kind and the picked up
            // card is the same rank as the kind.
            if (game.pile().isKind() && lastCard != null && lastCard.getRank() == game.pile().firstElement().getRank()
                    && game.getPrevPlayer().getHand().contains(lastCard)) {
                if (prevPlayerType == PlayerType.COMPUTER) {
                    Random randomGenerator = new Random(System.currentTimeMillis());
                    switch (currPlayerType) {
                        case COMPUTER:
                            // Generate a number 0..2
                            randomInt = randomGenerator.nextInt(3);
                            if (randomInt == 0) {
                                playQuickHandsMove();
                            }
                            break;
                        case HUMAN:
                            randomInt = randomGenerator.nextInt(getSpeed()) + (4 - mAppPrefs.getComputerSpeed());
                            // Computer will play if the human hasn't reacted in the
                            // next 0.5 to 2.5 or 5 seconds
                            mMainHandler.sendEmptyMessageDelayed(WHAT_COMPUTER_QUICK_HANDS, randomInt * 500);
                            break;
                    }
                }
                if (prevPlayerType == PlayerType.HUMAN) {
                    mQuickHands = true;
                }
            }
        }
    }

    /**
     * Show each player's cards, the pile and the deck.
     *
     * @param reveal If reveal is true then all players' cards are visible. Otherwise only player 1's cards can be seen and the card back image is shown for all other
     *               players.
     */
    private void displayAllCards(final boolean reveal) {
        displayAllHandCards(reveal);
        displayPileCards(false);
        displayDeckCard();
    }

    /**
     * Shows each player's cards.
     *
     * @param reveal If reveal is true then all players' cards are visible. Otherwise only player 1's cards can be seen and the card back image is shown for all other
     *               players.
     */
    private void displayAllHandCards(final boolean reveal) {
        for (int i = 0; i < Game.getNumPlayers(); i++) {
            if (i == THIS_PLAYER) {
                displayHandCards(i, true);
                updateHandValue(getThisPlayerHand().getValue().toString());
            } else
                displayHandCards(i, reveal);
        }
    }

    /**
     * Refreshes the pile and the cards for an individual player.
     *
     * @param playerNum The player whose cards are to be updated.
     * @param reveal    If reveal is true then the player's cards are visible. For player 1 this always true.
     */
    private void displayCards(final int playerNum, final boolean reveal) {
        if (playerNum == THIS_PLAYER) {
            final Integer handValue = getThisPlayerHand().getValue();
            updateHandValue(handValue.toString());
            displayHandCards(THIS_PLAYER, true);
            updateTargetValueColour(handValue);
        } else {
            displayHandCards(playerNum, reveal);
        }
        displayPileCards(true);
    }

    /**
     * Shows an image representing the deck.
     */
    private void displayDeckCard() {
        ImageView deck = (ImageView) findViewById(DECK_IMAGE_VIEW);
        deck.setImageResource(DECK_IMAGE);
    }

    /**
     * Displays all the cards in a player's hand.
     *
     * @param playerNum The number of the player whose cards are being updated.
     * @param reveal    Whether a player's cards are visible or only the card back image is shown.
     */
    private void displayHandCards(final int playerNum, final boolean reveal) {
        final ViewGroup layout = (ViewGroup) findViewById(playerCardMap.get(playerNum));
        displayPlayerCards(layout, game.getPlayer(playerNum).getHand(), reveal);
    }

    /**
     * Displays all the cards on the pile.
     */
    private void displayPileCards(boolean playSound) {
        final LinearLayout pile = (LinearLayout) findViewById(R.id.LinearLayout_Pile);
        for (int index = 0; index < pile.getChildCount(); index++) {
            final ImageView card = (ImageView) pile.getChildAt(index);
            if (game.pile().size() > index) {
        /*
         * Set the card image to the appropriate pile card and ensure that it's visible. The pile card is associated with the tag so that it can be referenced
         * easily.
         */
                card.setImageResource(getResources().getIdentifier(game.pile().getCard(index).getAndroidImage(), "drawable", "com.bazsoft.yaniv"));
                card.setVisibility(View.VISIBLE);
                card.setTag(game.pile().getCard(index));
                if (playSound && !mSpeedUp) {
                    SoundManager.playSound(SoundEffect.SLAP);
                }
            } else {
        /*
         * Remove the image, any tags and the visibility of the image so that all other pile cards take precedence over it.
         */
                card.setImageDrawable(null);
                card.setTag(null);
                card.setVisibility(ImageView.GONE);
            }
        }
    }

    /**
     * Displays cards for a particular player's hand within the layout.
     *
     * @param layout The linear layout that the cards are displayed in.
     * @param hand   The hand of cards that are being displayed.
     * @param reveal Whether the card image or the back of a card are drawn.
     */
    private void displayPlayerCards(final ViewGroup layout, final Hand hand, final boolean reveal) {
        for (int index = 0; index < layout.getChildCount(); index++) {
            final View view = layout.getChildAt(index);
            if (!(view instanceof ImageView)) {
                break;
            }
            final ImageView card = (ImageView) view;
            if (hand.size() > index) {
                if (reveal) {
                    card.setImageResource(getResources().getIdentifier(hand.getCard(index).getAndroidImage(), "drawable", "com.bazsoft.yaniv"));
                } else {
                    card.setImageResource(CARD_BACK_IMAGE);
                }
                card.setVisibility(View.VISIBLE);
                card.setTag(hand.getCard(index));
                if (card.isSelected()) {
                    card.setSelected(false);
                    card.scrollBy(0, -(int) getResources().getDimension(R.dimen.scroll_padding)); // Move down

                }
            } else {

                if (hand.size() == 0) {
                    card.setVisibility(ImageView.INVISIBLE);
                    card.setImageResource(CARD_BACK_IMAGE);
                } else {
                    card.setVisibility(ImageView.GONE);
                }
                card.setTag(null);
            }
        }

    }

    private void checkForYanivAchievements() {
        if (!mAchievements[Achievement.YANIV.ordinal()]) {
            displayAchievement(Achievement.YANIV);
        }
        if (!mAchievements[Achievement.YANIV_FIVE_CARDS.ordinal()] && getThisPlayerHand().size() == 5 && game.getYanivMinimum() == 5) {
            displayAchievement(Achievement.YANIV_FIVE_CARDS);
        }
        if (!mAchievements[Achievement.YANIV_ZERO_SCORE.ordinal()] && getThisPlayerHand().getValue() == 0) {
            displayAchievement(Achievement.YANIV_ZERO_SCORE);
        }
        if (!mAchievements[Achievement.YANIV_KNOCK_OUT.ordinal()] && mNumPlayersStartOfRound > Game.getPlayersStillIn()) {
            displayAchievement(Achievement.YANIV_KNOCK_OUT);
        }
        if (!mAchievements[Achievement.ASSAF_YOU.ordinal()] && game.getCurrentPlayerIndex() != THIS_PLAYER) {
            displayAchievement(Achievement.ASSAF_YOU);
        }
        if (!mAchievements[Achievement.LUCKY_STREAK.ordinal()] && mYanivsInARow == 5) {
            displayAchievement(Achievement.LUCKY_STREAK);
        }
        if (!mAchievements[Achievement.STEALTH_ASSAF.ordinal()] && game.getCurrentPlayerIndex() != THIS_PLAYER && mYanivPossible) {
            displayAchievement(Achievement.STEALTH_ASSAF);
        }
        if (!mAchievements[Achievement.ONE_TURN.ordinal()] && game.getTurn() == 1) {
            displayAchievement(Achievement.ONE_TURN);
        }
        if (!mAchievements[Achievement.TWO_TURNS.ordinal()] && game.getTurn() == 2) {
            displayAchievement(Achievement.TWO_TURNS);
        }
        if (!mAchievements[Achievement.ASSAF_FRENZY.ordinal()] && mAssafsThisGame >= 5) {
            displayAchievement(Achievement.ASSAF_FRENZY);
        }

    }

    /**
     * Performs all the logic at the end of a round when a player has called Yaniv.
     */
    private void handleYaniv() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Reveal every player's cards.
                displayAllCards(true);

                // Don't play sounds if sped up
                if (!mSpeedUp) {
                    if (game.getWinner() == THIS_PLAYER
                            || (game.getCurrentPlayerIndex() != THIS_PLAYER && getThisPlayerHand().getValue().equals(game.getWinningPlayer().getHand().getValue()))) {
                        SoundManager.playSound(SoundEffect.CHEER);
                        mYanivsInARow++;
                        if (game.getCurrentPlayerIndex() != THIS_PLAYER) {
                            mAssafsThisGame++;
                        }
                        if (mGameMode != GameMode.TUTORIAL.ordinal()) {
                            checkForYanivAchievements();
                        }
                    } else if (game.getCurrentPlayerIndex() == THIS_PLAYER) {
                        SoundManager.playSound(SoundEffect.OW);
                        if (!mAchievements[Achievement.ASSAF_ME.ordinal()]) {
                            displayAchievement(Achievement.ASSAF_ME);
                        }
                        if (game.getPlayer(THIS_PLAYER).isScoreHalved() && game.getPlayer(THIS_PLAYER).getScore() > 25
                                && !mAchievements[Achievement.GOLDEN_ASSAF.ordinal()]) {
                            displayAchievement(Achievement.GOLDEN_ASSAF);
                        }
                        mYanivsInARow = 0;
                    } else {
                        SoundManager.playSound(SoundEffect.BOO);
                        mYanivsInARow = 0;
                        if (!mAchievements[Achievement.ONE_MORE_TURN.ordinal()]) {
                            Straight straight = getThisPlayerHand().findStraight();
                            Kind kind = getThisPlayerHand().findOfAKind();
                            if (straight.size() > 0 || kind.size() > 2) {
                                displayAchievement(Achievement.ONE_MORE_TURN);
                            }
                        }
                    }
                    if (game.getPlayer(THIS_PLAYER).isScoreHalved()) {
                        mHalf++;
                        switch (game.getPlayer(THIS_PLAYER).getScore()) {
                            case 25:
                                if (!mAchievements[Achievement.HALF_FIFTY.ordinal()]) {
                                    displayAchievement(Achievement.HALF_FIFTY);
                                }
                                break;
                            case 50:
                                if (!mAchievements[Achievement.HALF_HUNDRED.ordinal()]) {
                                    displayAchievement(Achievement.HALF_HUNDRED);
                                }
                                break;
                            case 75:
                                if (!mAchievements[Achievement.HALF_150.ordinal()]) {
                                    displayAchievement(Achievement.HALF_150);
                                }
                                break;
                            case 100:
                                if (!mAchievements[Achievement.LUCKY_ESCAPE.ordinal()]) {
                                    displayAchievement(Achievement.LUCKY_ESCAPE);
                                }
                        }
                        if (mHalf >= 3 && !mAchievements[Achievement.MANY_HALVES.ordinal()]) {
                            displayAchievement(Achievement.MANY_HALVES);
                        }
                    }
                }
                animateRoundStatus();
            }

        });
    }

    private void animateRoundStatus() {
        final ImageView roundStatus = (ImageView) findViewById(R.id.ImageView_Status);
        final Animation scale = AnimationUtils.loadAnimation(YanivGameActivity.this, R.anim.scale);
        scale.setDuration(1000);
        // Don't animate if sped up.
        if (!mSpeedUp) {
            roundStatus.setVisibility(View.VISIBLE);
            if (game.getWinner() == game.getCurrentPlayerIndex()) {
                roundStatus.setImageResource(R.drawable.yaniv);
            } else {
                roundStatus.setImageResource(R.drawable.assaf);
            }
            roundStatus.startAnimation(scale);
            scale.setAnimationListener(YanivGameActivity.this);
        } else {
            onAnimationEnd(scale);
        }

    }

    private void initScoreboard() {
        mSlidingDrawer = (SlidingDrawer) findViewById(R.id.SlidingDrawer);
        mSlidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

            @Override
            public void onDrawerOpened() {
                makeRoundVisible(false);
                makeYanivAtVisible(false);
                makeTopPlayerVisible(false);
                mPause = true;
            }

        });
        mSlidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

            @Override
            public void onDrawerClosed() {
                mPause = false;
                makeRoundVisible(true);
                makeYanivAtVisible(true);
                makeTopPlayerVisible(true);
            }

        });
        updateScoreboard();
    }

    private void initSounds() {
        SoundManager.getInstance();
        SoundManager.initSounds(this);
    }

    /**
     * Defines the textview used to display the player's moves.
     */
    private void initConsole() {
        mConsole = (TextView) findViewById(R.id.TextView_Console);
        mConsole.setMovementMethod(ScrollingMovementMethod.getInstance());
        mConsole.addTextChangedListener(this);

    }

    /**
     * Creates a new game.
     *
     * @param playerName The name of the player 1.
     */
    private void newGame(String playerName) {
        Log.d(DEBUG_TAG, "New game");
        game = new Game(createPlayers(playerName, mAppPrefs.getPreferencesNumPlayers()), CARDS_PER_PLAYER, mAppPrefs.getYanivMinimum());
        initPlayerMaps();
        initScoreboard();
        mConsole.setText("");
        mConsole.scrollTo(0, 0);
        mSpeedUp = false;
        makeSpeedUpButtonVisible(false);
        makeNextRoundButtonVisible(false);
        makeNewGameButtonVisible(false);
        makeBottomViewsVisible(true);
        updateYanivAtValue();
        mAssafsThisGame = 0;
        mHalf = 0;
        // Clear left and right hand players off the screen temporarily in case
        // the number of players has been reduced.
        displayPlayerCards((ViewGroup) findViewById(R.id.FrameLayout_LeftPlayer), new Hand(), false);
        displayPlayerCards((ViewGroup) findViewById(R.id.FrameLayout_RightPlayer), new Hand(), false);
        newRound();
    }

    /**
     * Starts a new round.
     */
    private void newRound() {
        game.newRound();
        mQuickHands = false;
        if (!mSpeedUp) {
            SoundManager.playSound(SoundEffect.SHUFFLE);
        }
        game.dealCards();
        displayAllCards(false);
        mConsole.append("Round " + game.getRound() + "\n=========\n");

        updateRoundValue();
        mNumPlayersStartOfRound = Game.getPlayersStillIn();
        updateTargetValue();
        playNextTurn();
        mConsole.append("The winner from last round " + game.getCurrentPlayer().getName() + " starts\n");

    }

    private void updateYanivAtValue() {
        final TextView yanivValue = (TextView) findViewById(R.id.TextView_YanivAtValue);
        yanivValue.setText(String.valueOf(game.getYanivMinimum()));
    }

    private void updateRoundValue() {
        final TextView roundValue = (TextView) findViewById(R.id.TextView_RoundValue);
        roundValue.setText(String.valueOf(game.getRound()));
    }

    private void updateYanivAtColour() {
        TextView yanivValue = (TextView) findViewById(R.id.TextView_YanivAtValue);
        if (game.getPlayer(THIS_PLAYER).getHand().getValue() <= game.getYanivMinimum()) {
            yanivValue.setTextColor(getResources().getColor(R.color.baize));
        } else {
            yanivValue.setTextColor(getResources().getColor(R.color.red));
        }
    }

    private void updateTargetValue() {
        TextView targetValue = (TextView) findViewById(R.id.TextView_TargetValue);
        mIntTargetValue = (50 - (game.getPlayer(THIS_PLAYER).getScore() % 50));
        targetValue.setText(mIntTargetValue.toString());
        updateTargetValueColour(getThisPlayerHand().getValue());
    }

    private void updateTargetValueColour(int handValue) {
        TextView targetValue = (TextView) findViewById(R.id.TextView_TargetValue);
        LinearLayout targetLayout = (LinearLayout) findViewById(R.id.LinearLayout_Target);
        if (mIntTargetValue - handValue > 10 && !(mIntTargetValue >= 30 && mIntTargetValue <= (30 + game.getYanivMinimum()))) {
            targetLayout.setVisibility(View.INVISIBLE);
        } else {
            targetLayout.setVisibility(View.VISIBLE);
            int colour;
            if (handValue == mIntTargetValue) {
                colour = getResources().getColor(R.color.baize);
            } else {
                colour = getResources().getColor(R.color.red);
            }
            targetValue.setTextColor(colour);
        }

    }

    private int computePenalty() {
        int penalty = 0;
        boolean applyPenalty = false;
        for (Player player : game.getPlayers()) {
            if (player.getID() != (THIS_PLAYER + 1)) {
                if (!player.isOut()) {
                    penalty += ((201 - player.getScore()) / (Game.getNumPlayers() - (Game.getPlayersStillIn() - 1)));
                    if (player.getScore() > 0) {
                        applyPenalty = true;
                    }
                }
            }
        }
        if (penalty > 200) {
            penalty = 200;
        }
        return applyPenalty ? penalty : 0;
    }

    private void calculateRating() {
        int difference;
        int ratingToDeduct = 0;
        int ratingToAdd = 0;
        if (mNumPlayersStartOfRound > Game.getPlayersStillIn()) {
            int playerScore;
            for (Player player : game.getPlayers()) {
                if (player.isJustOut()) {
                    playerScore = player.getScore();
                    for (Player player2 : game.getPlayers()) {
                        if (player2.getID() != player.getID() && (!player2.isOut() || player2.isJustOut())) {
                            difference = (playerScore - player2.getScore());
                            if (player.getID() == (THIS_PLAYER + 1)) {
                                ratingToDeduct += (difference / (Game.getNumPlayers() - Game.getPlayersStillIn()));
                            } else if (player2.getID() == (THIS_PLAYER + 1)) {
                                ratingToAdd += (difference / Game.getPlayersStillIn());
                            }
                        }

                    }
                    player.resetJustOut();
                }
            }

            // An opponent is out but more opponents remain
            if (ratingToAdd > 0 && ratingToDeduct == 0 && Game.getPlayersStillIn() > 1) {
                mAppPrefs.incrementRating(ratingToAdd);
                mDialogMessage = String.format(getResources().getString(R.string.alert_add), ratingToAdd, mAppPrefs.getRating());
                mHeyZapMessage = String.format(getResources().getString(R.string.heyzap_rating), mAppPrefs.getRating());
                showDialog(ALERT_SCORE_ADD);
                // updateServerData();
            }

            // The player is out
            if (ratingToDeduct > 0) {
                mAppPrefs.deductRating(ratingToDeduct - ratingToAdd);
                mAppPrefs.incrementPlayed();
                mAppPrefs.deleteWonPlayerGames();
                mDialogMessage = String.format(getResources().getString(R.string.alert_deduct), (ratingToDeduct - ratingToAdd), mAppPrefs.getRating());
                mHeyZapMessage = String.format(getResources().getString(R.string.heyzap_rating), mAppPrefs.getRating());
                showDialog(ALERT_SCORE_DEDUCT);

                updateServerData();
            }
        }

        // You've won the game - all other players are out.
        if (Game.getPlayersStillIn() == 1 && !game.getPlayer(THIS_PLAYER).isOut()) {
            mAppPrefs.incrementRating(ratingToAdd);
            mAppPrefs.incrementPlayed();
            mAppPrefs.incrementWon();
            mDialogMessage = String.format(getResources().getString(R.string.alert_add), ratingToAdd, mAppPrefs.getRating());
            mHeyZapMessage = String.format(getResources().getString(R.string.heyzap_rating), mAppPrefs.getRating());
            showDialog(ALERT_WINNER);

            switch (Game.getNumPlayers()) {
                case 2:
                    mAppPrefs.setWon2Player(mAppPrefs.get2PlayerWon() + 1);
                    break;
                case 3:
                    mAppPrefs.setWon3Player(mAppPrefs.get3PlayerWon() + 1);
                    break;
                case 4:
                    mAppPrefs.setWon4Player(mAppPrefs.get4PlayerWon() + 1);
                    break;
            }
            if (!mAchievements[Achievement.WIN.ordinal()]) {
                displayAchievement(Achievement.WIN);
            }
            if (!mAchievements[Achievement.WIN_TEN.ordinal()] && mAppPrefs.getWon() >= 10) {
                displayAchievement(Achievement.WIN_TEN);
            }
            if (!mAchievements[Achievement.KEEP_IT_LOW.ordinal()] && game.getPlayer(THIS_PLAYER).getScore() < 50) {
                displayAchievement(Achievement.KEEP_IT_LOW);
            }
            if (!mAchievements[Achievement.QUICK_MOVES.ordinal()] && game.getRound() <= 15) {
                displayAchievement(Achievement.QUICK_MOVES);
            }
            if (!mAchievements[Achievement.INTERMINABLE.ordinal()] && game.getRound() >= 50) {
                displayAchievement(Achievement.INTERMINABLE);
            }

            if (!mAchievements[Achievement.TRIATHLON.ordinal()] && mAppPrefs.get2PlayerWon() > 0 && mAppPrefs.get3PlayerWon() > 0 && mAppPrefs.get4PlayerWon() > 0) {
                displayAchievement(Achievement.TRIATHLON);
            }
            updateServerData();

        }

        if (!mAchievements[Achievement.PROFESSIONAL.ordinal()] && mAppPrefs.getRating() >= 10000) {
            displayAchievement(Achievement.PROFESSIONAL);
        }

        if (!mAchievements[Achievement.PLAY_FIVE.ordinal()] && mAppPrefs.getPlayed() >= 5) {
            displayAchievement(Achievement.PLAY_FIVE);
        }
        if (!mAchievements[Achievement.PLAY_100.ordinal()] && mAppPrefs.getPlayed() >= 100) {
            displayAchievement(Achievement.PLAY_100);
        }

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        switch ((int) animation.getDuration()) {
            case 1000:
                // This is a Yaniv/Assaf Animation
                findViewById(R.id.ImageView_Status).setVisibility(View.INVISIBLE);
                updateScoreboard();
                if (!mSpeedUp) {
                    mSlidingDrawer.animateOpen();
                    mMainHandler.sendEmptyMessageDelayed(WHAT_CLOSE_SCOREBOARD, 5000);
                }

                calculateRating();

                if (mGameMode == GameMode.TUTORIAL.ordinal()) {
                    nextTutorialStep();
                    break;
                }

                // Determine whether the game is over or whether new rounds are
                // possible.
                if (Game.getPlayersStillIn() > 1) {
                    if (game.getPlayer(THIS_PLAYER).isOut()) {
                        if (!mSpeedUp) {
                            makeSpeedUpButtonVisible(true);
                            makeBottomViewsVisible(false);
                        }
                        makeNewGameButtonVisible(true);
                    }
                    if (!mSpeedUp) {
                        makeNextRoundButtonVisible(true);
                    } else {
                        // Automatically go to the next round when sped up.
                        newRound();
                    }
                } else {
                    makeNewGameButtonVisible(true);
                }
                break;
            case 3500:
                // Tutorial animation
                nextTutorialStep();
                break;
        }
    }

    private void displayAchievement(Achievement achievement) {
        mDialogMessage = String.format(getResources().getString(R.string.alert_achievement), achievement.title);
        mFacebookMessage = String.format(getResources().getString(R.string.facebook_post), achievement.title);
        mHeyZapMessage = String.format(getResources().getString(R.string.heyzap_achievement), achievement.title);
        SoundManager.playSound(SoundEffect.TADA);
        mAchievements[achievement.ordinal()] = true;
        mAppPrefs.saveAchievements(mAchievements);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDialog(ALERT_ACHIEVEMENT);

            }

        });
        // updateServerData();
    }

    private void checkPlayedCardsForAchievements() {
        CardVector selection = getThisPlayerHand().getSelection();
        mYanivPossible = getThisPlayerHand().getValue() <= mAppPrefs.getGameYanivMinimum();

        if (selection.isOfAKind()) {
            switch (selection.size()) {
                case 2:
                    if (!mAchievements[Achievement.TWO_OF_A_KIND.ordinal()]) {
                        displayAchievement(Achievement.TWO_OF_A_KIND);
                    }
                    break;
                case 3:
                    if (!mAchievements[Achievement.THREE_OF_A_KIND.ordinal()]) {
                        displayAchievement(Achievement.THREE_OF_A_KIND);
                    }
                    break;
                case 4:
                    if (!mAchievements[Achievement.FOUR_OF_A_KIND.ordinal()]) {
                        displayAchievement(Achievement.FOUR_OF_A_KIND);
                    }
                    break;
            }
        } else if (selection.isStraight()) {
            if (selection.jokerCount() > 0) {
                if (!mAchievements[Achievement.STRAIGHT_WITH_JOKER.ordinal()]) {
                    displayAchievement(Achievement.STRAIGHT_WITH_JOKER);
                }
                if (selection.jokerCount() == 2 && selection.size() == 5) {
                    if (!mAchievements[Achievement.STRAIGHT_WITH_TWO_JOKERS.ordinal()]) {
                        displayAchievement(Achievement.STRAIGHT_WITH_TWO_JOKERS);
                    }

                }
            } else {
                switch (selection.size()) {
                    case 3:
                        if (!mAchievements[Achievement.STRAIGHT_OF_THREE.ordinal()]) {
                            displayAchievement(Achievement.STRAIGHT_OF_THREE);
                        }
                        break;
                    case 4:
                        if (!mAchievements[Achievement.STRAIGHT_OF_FOUR.ordinal()]) {
                            displayAchievement(Achievement.STRAIGHT_OF_FOUR);

                        }
                        break;
                    case 5:
                        if (!mAchievements[Achievement.STRAIGHT_OF_FIVE.ordinal()]) {
                            displayAchievement(Achievement.STRAIGHT_OF_FIVE);
                        }
                }
            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FacebookApplication.FACEBOOK_AUTH_REQUEST:
                mFacebook.getFacebook().authorizeCallback(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                mFacebook.postToWall(mFacebookMessage, YanivGameActivity.this);
                mPause = false;
                dialog.cancel();
                break;
            case Dialog.BUTTON_NEUTRAL:
                mPause = false;
                dialog.cancel();
                break;
        }
    }

    private synchronized Hand getThisPlayerHand() {
        return game.getPlayer(THIS_PLAYER).getHand();
    }

    /**
     * Handles the logic if one of the cards or a button is clicked.
     *
     * @param view The button or card image that was pressed.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // If one of the bottom player (human) cards is selected.
            case R.id.ImageView_BP1:
            case R.id.ImageView_BP2:
            case R.id.ImageView_BP3:
            case R.id.ImageView_BP4:
            case R.id.ImageView_BP5:
                if (game.getWinner() == -1) {
                    HandCard handCard = (HandCard) view.getTag();
                    SoundManager.playSound(SoundEffect.DRAW);
                    if (!view.isSelected()) {
                        view.scrollBy(0, (int) getResources().getDimension(R.dimen.scroll_padding)); // Move up
                        view.setSelected(true);
                        handCard.setSelected(true);
                    } else {
                        view.scrollBy(0, -(int) getResources().getDimension(R.dimen.scroll_padding)); // Move down
                        view.setSelected(false);
                        handCard.setSelected(false);
                    }
                }
                break;
            // If one of the pile cards is selected.
            case R.id.ImageView_Pile1:
            case R.id.ImageView_Pile2:
            case R.id.ImageView_Pile3:
            case R.id.ImageView_Pile4:
            case R.id.ImageView_Pile5:
                if (mQuickHands) {
                    mPause = true;
                    CardVector selection = getThisPlayerHand().getSortedSelection();
                    Card lastCard = game.getLastCardPickedUp();
                    if (selection.size() == 1)
                        if (selection.firstElement().equals(lastCard)) {
                            if (!mAchievements[Achievement.QUICK_HANDS.ordinal()] && mGameMode != GameMode.TUTORIAL.ordinal()) {
                                displayAchievement(Achievement.QUICK_HANDS);
                            } else {
                                // The dialog also removes the pause
                                mPause = false;
                            }
                            game.dropOntoPile(selection.firstElement());
                            if (mGameMode == GameMode.TUTORIAL.ordinal()) {
                                displayCards(0, true);
                                nextTutorialStep();
                            } else {
                                displayCards(0, false);
                            }

                        } else {
                            showDialog(ALERT_QUICKHANDS_WRONG_CARD);
                        }
                    else {
                        showDialog(ALERT_QUICKHANDS_TOO_MANY);
                    }
                } else {
                    CardVector selection = getThisPlayerHand().getSortedSelection();
                    Card lastCard = game.getLastCardPickedUp();
                    // This stops the player clicking on a pile card within 0.5 seconds of them having quick hands. This stops accidental pickups.
                    long now = System.currentTimeMillis();
                    if (selection.size() != 1 || (selection.firstElement().equals(lastCard))
                            || (now - mTimeSinceQuickHands > 500 && now - mTimeSinceComputerQuickHands > 500)) {
                        PileCard pileCard = (PileCard) view.getTag();
                        if (mGameMode == GameMode.TUTORIAL.ordinal()) {
                            if (mTutorialTurn != null) {
                                switch (mTutorialTurn) {
                                    case PICKUP_PILE:
                                        if (selection.equals(mDropCards) && pileCard.equals(mPickupCard)) {
                                            game.swapWithPileCard(pileCard, selection);
                                            printTurn();
                                            displayCards(game.getCurrentPlayerIndex(), true);
                                            nextTutorialStep();
                                        } else {
                                            showDialog(ALERT_PICKUP_PILE_CARD);
                                        }
                                        break;
                                    case PICKUP_DECK:
                                        showDialog(ALERT_PICKUP_FROM_DECK);
                                        break;
                                    case CALL_YANIV:
                                        showDialog(ALERT_PRESS_YANIV);
                                        break;
                                }
                            }
                        } else if (game.getWinner() == -1 && pileCard != null) {
                            if (game.getCurrentPlayerIndex() == THIS_PLAYER) {
                                if (pileCard.available()) {
                                    if (getThisPlayerHand().isValidSelection()) {
                                        mMainHandler.removeMessages(WHAT_COMPUTER_QUICK_HANDS);
                                        checkPlayedCardsForAchievements();
                                        game.swapWithPileCard(pileCard, getThisPlayerHand().getSortedSelection());
                                        printTurn();
                                        displayCards(game.getCurrentPlayerIndex(), false);
                                        playNextTurn();
                                    } else {
                                        showDialog(ALERT_INVALID_SELECTION);
                                    }
                                } else {
                                    showDialog(ALERT_INVALID_PILE_CARD);
                                }
                            } else {
                                showDialog(ALERT_WRONG_TURN);
                            }
                        }
                    }
                }
                break;
            case R.id.ImageView_Deck:
                if (game.getWinner() == -1) {
                    if (game.getCurrentPlayerIndex() == THIS_PLAYER) {
                        if (mGameMode == GameMode.TUTORIAL.ordinal()) {
                            if (!mQuickHands) {
                                switch (mTutorialTurn) {
                                    case PICKUP_PILE:
                                        showDialog(ALERT_PICKUP_FROM_PILE);
                                        break;
                                    case PICKUP_DECK:
                                        if (getThisPlayerHand().getSortedSelection().equals(mDropCards)) {
                                            game.swapWithDeckCard(getThisPlayerHand().getSortedSelection());
                                            printTurn();
                                            displayCards(THIS_PLAYER, true);
                                            nextTutorialStep();
                                        } else {
                                            showDialog(ALERT_PICKUP_DECK_CARD);
                                        }
                                        break;
                                    case CALL_YANIV:
                                        showDialog(ALERT_PRESS_YANIV);
                                        break;
                                }
                            }
                        } else if (getThisPlayerHand().isValidSelection()) {
                            mMainHandler.removeMessages(WHAT_COMPUTER_QUICK_HANDS);
                            checkPlayedCardsForAchievements();
                            game.swapWithDeckCard(getThisPlayerHand().getSortedSelection());
                            printTurn();
                            displayCards(THIS_PLAYER, true);
                            playNextTurn();
                        } else {
                            showDialog(ALERT_INVALID_SELECTION);
                        }
                    } else if (mGameMode != GameMode.TUTORIAL.ordinal()) {
                        showDialog(ALERT_WRONG_TURN);
                    }
                }
                break;
            case R.id.Button_Yaniv:
                long now = System.currentTimeMillis();
                if (now - mTimeSinceComputerQuickHands > 500) {
                    // Make button invisible again after it's pressed.
                    view.setVisibility(View.INVISIBLE);
                    SoundManager.playSound(SoundEffect.YANIV);
                    mMainHandler.removeMessages(WHAT_COMPUTER_QUICK_HANDS);
                    game.callYaniv();
                    printTurn();
                    handleYaniv();
                }
                break;
            case R.id.Button_Next_Round:
                // Make button invisible again after it's pressed.
                view.setVisibility(View.INVISIBLE);
                SoundManager.playSound(SoundEffect.CLICK);
                // Display display = ((WindowManager)
                // getSystemService(WINDOW_SERVICE))
                // .getDefaultDisplay();

                if (mSlidingDrawer.isOpened()) {
                    // && (display.getOrientation() == Surface.ROTATION_0 ||
                    // display.getOrientation() == Surface.ROTATION_180)) {
                    mMainHandler.removeMessages(WHAT_CLOSE_SCOREBOARD);
                    mSlidingDrawer.animateClose();
                }
                newRound();
                break;
            case R.id.Button_New_Game:
                SoundManager.playSound(SoundEffect.CLICK);
                if (mComputerPlayerTask.getStatus() == AsyncTask.Status.RUNNING) {
                    mComputerPlayerTask.cancel(true);
                }
                mMainHandler.removeMessages(WHAT_COMPUTER_TURN);
                mMainHandler.removeMessages(WHAT_COMPUTER_QUICK_HANDS);
                showDialog(ALERT_NEW_GAME_PLAYERS);
                break;
            case R.id.Button_Speed_Up:
                SoundManager.playSound(SoundEffect.CLICK);
                // Make button invisible again after it's pressed
                view.setVisibility(View.INVISIBLE);
                mSpeedUp = true;
                clickNextRoundButton();
                break;
        }

    }

    /**
     * Creates the various dialogs
     */
    @Override
    protected Dialog onCreateDialog(final int id) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        mPause = true;

        switch (id) {
            case ALERT_QUICKHANDS_WRONG_CARD:
                builder.setTitle(R.string.alert_title_invalid_card).setNeutralButton(android.R.string.ok, this).setMessage(R.string.alert_quick_hands_wrong_card)
                        .setOnCancelListener(this);
                break;
            case ALERT_QUICKHANDS_TOO_MANY:
                builder.setTitle(R.string.alert_title_invalid_card).setNeutralButton(android.R.string.ok, this).setMessage(R.string.alert_quick_hands_too_many)
                        .setOnCancelListener(this);
                break;
            case ALERT_WRONG_TURN:
                builder.setTitle(R.string.alert_title_wrong_turn).setNeutralButton(android.R.string.ok, this).setMessage(R.string.alert_wrong_turn)
                        .setOnCancelListener(this);
                break;
            case ALERT_INVALID_SELECTION:
                builder.setTitle(R.string.alert_title_invalid_cards).setNeutralButton(android.R.string.ok, this).setMessage(R.string.alert_invalid_cards)
                        .setOnCancelListener(this);
                break;
            case ALERT_INVALID_PILE_CARD:
                builder.setTitle(R.string.alert_title_invalid_pile_card).setNeutralButton(android.R.string.ok, this).setMessage(R.string.alert_invalid_pile_card)
                        .setOnCancelListener(this);
                break;
            case ALERT_SCORE_DEDUCT:
                builder.setMessage("").setTitle(R.string.alert_title_out).setNeutralButton(android.R.string.ok, this).setCancelable(false);
                break;
            case ALERT_SCORE_ADD:
                builder.setMessage("").setTitle(R.string.alert_title_players_out).setNeutralButton(android.R.string.ok, this).setCancelable(false);
                break;
            case ALERT_WINNER:
                builder.setMessage("").setTitle(R.string.alert_title_winner).setNeutralButton(android.R.string.ok, this).setCancelable(false);
                break;
            case ALERT_ACHIEVEMENT:
                builder.setTitle(R.string.alert_title_achievement).setMessage("").setNeutralButton(android.R.string.ok, this);
                if (mAppPrefs.isFacebookEnabled()) {
                    builder.setPositiveButton("Publish", this);
                }
                break;
            case ALERT_TUTORIAL:
                builder.setMessage("").setTitle(R.string.menu_item_tutorial).setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mPause = false;
                        nextTutorialStep();

                    }
                }).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPause = false;
                        dialog.dismiss();
                        nextTutorialStep();
                    }
                });
                break;
            case ALERT_PICKUP_FROM_DECK:
                builder.setTitle(R.string.alert_title_invalid_card).setNeutralButton(android.R.string.ok, this).setMessage(R.string.alert_pickup_from_deck)
                        .setOnCancelListener(this);
                break;
            case ALERT_PICKUP_FROM_PILE:
                builder.setTitle(R.string.alert_title_invalid_card).setNeutralButton(android.R.string.ok, this).setMessage(R.string.alert_pickup_from_pile)
                        .setOnCancelListener(this);
                break;
            case ALERT_PRESS_YANIV:
                builder.setTitle(R.string.alert_title_call_yaniv).setNeutralButton(android.R.string.ok, this).setMessage(R.string.alert_call_yaniv)
                        .setOnCancelListener(this);
                break;
            case ALERT_PICKUP_PILE_CARD:
                builder.setTitle(R.string.alert_title_invalid_cards).setNeutralButton(android.R.string.ok, this).setMessage("").setOnCancelListener(this);
                break;
            case ALERT_PICKUP_DECK_CARD:
                builder.setTitle(R.string.alert_title_invalid_cards).setNeutralButton(android.R.string.ok, this).setMessage("").setOnCancelListener(this);
                break;
            case ALERT_TUTORIAL_END:
                if (!mAchievements[Achievement.COMPLETE_TUTORIAL.ordinal()]) {
                    mDialogMessage = mDialogMessage + " " + String.format(getResources().getString(R.string.alert_achievement), Achievement.COMPLETE_TUTORIAL.title);
                    mFacebookMessage = String.format(getResources().getString(R.string.facebook_post), Achievement.COMPLETE_TUTORIAL.title);
                    SoundManager.playSound(SoundEffect.TADA);
                    mAchievements[Achievement.COMPLETE_TUTORIAL.ordinal()] = true;
                    if (mAppPrefs.isFacebookEnabled()) {
                        builder.setPositiveButton("Publish", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mFacebook.postToWall(mFacebookMessage, YanivGameActivity.this);
                                mPause = false;
                                dialog.dismiss();
                                YanivGameActivity.this.finish();
                            }
                        });
                    }
                }
                builder.setMessage(mDialogMessage).setTitle(R.string.menu_item_tutorial).setCancelable(false)
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPause = false;
                                dialog.cancel();
                                YanivGameActivity.this.finish();
                            }
                        });
                break;
            case ALERT_NEW_GAME:
                builder.setTitle(R.string.alert_title_new_game).setOnCancelListener(new OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mPause = false;
                        if (game.getWinner() == -1) {
                            playTurn();
                        }

                    }
                }).setMessage("").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        mAppPrefs.deductRating(computePenalty());
                        updateServerData();
                        mAppPrefs.deleteWonPlayerGames();
                        showDialog(ALERT_NEW_GAME_PLAYERS);
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPause = false;
                        dialog.dismiss();
                        if (game.getWinner() == -1) {
                            playTurn();
                        }
                    }
                });
                break;
            case ALERT_NEW_GAME_PLAYERS:
                final View newGameDialogLayout = LayoutInflater.from(this).inflate(R.layout.new_game, (ViewGroup) findViewById(R.id.root), true);
                final Spinner playerSpinner = (Spinner) newGameDialogLayout.findViewById(R.id.Spinner_NumPlayers);

                ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.num_players, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                playerSpinner.setAdapter(adapter);
                playerSpinner.setSelection(mAppPrefs.getPreferencesNumPlayers() - 2);
                playerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                        mAppPrefs.setPreferencesNumPlayers(selectedItemPosition + 2);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });

                initYanivMinimum(newGameDialogLayout);

                builder.setTitle(R.string.alert_title_new_game).setOnCancelListener(new OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mPause = false;
                        if (game.getWinner() == -1) {
                            playTurn();
                        }

                    }
                }).setView(newGameDialogLayout).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mPause = false;
                        Button newGameButton = (Button) findViewById(R.id.Button_New_Game);
                        newGameButton.setVisibility(View.INVISIBLE);
                        newGame(mAppPrefs.getGamePlayerName());
                    }
                });
        }

        return builder.create();
    }

    private void initYanivMinimum(View newGameDialogLayout) {
        final RadioGroup radioGroup = (RadioGroup) newGameDialogLayout.findViewById(R.id.RadioGroup_Yaniv_RadioButtons);
        final int minimum = mAppPrefs.getYanivMinimum();
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View view = radioGroup.getChildAt(i);
            if (Integer.parseInt((String) view.getTag()) == minimum) {
                radioGroup.check(view.getId());
            }

        }
        radioGroup.setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        SoundManager.playSound(SoundEffect.CLICK);
        final int score = Integer.parseInt((String) group.findViewById(checkedId).getTag());
        mAppPrefs.setYanivMinimum(score);
    }

    /**
     * Creates the options menu when the menu button is pressed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.gameoptions, menu);
        menu.findItem(R.id.help_menu_item).setIntent(new Intent(this, YanivHelpActivity.class));
        menu.findItem(R.id.settings_menu_item).setIntent(new Intent(this, YanivSettingsActivity.class));
        if (mGameMode == GameMode.TUTORIAL.ordinal()) {
            menu.removeItem(R.id.new_game_menu_item);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.new_game_menu_item:
                if (mComputerPlayerTask.getStatus() == AsyncTask.Status.RUNNING) {
                    mComputerPlayerTask.cancel(true);
                }
                mMainHandler.removeMessages(WHAT_COMPUTER_TURN);
                mMainHandler.removeMessages(WHAT_COMPUTER_QUICK_HANDS);
                if (!game.getPlayer(THIS_PLAYER).isOut()) {
                    int penalty = computePenalty();
                    if (penalty > 0) {
                        mDialogMessage = String.format(getResources().getString(R.string.alert_new_game), penalty);
                        showDialog(ALERT_NEW_GAME);
                    } else {
                        showDialog(ALERT_NEW_GAME_PLAYERS);
                    }
                }
                return true;
            default:
                startActivity(item.getIntent());
                return true;
        }
    }

    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mComputerPlayerTask.getStatus() == AsyncTask.Status.RUNNING) {
            mComputerPlayerTask.cancel(true);
        }
        if (mAccountUpload != null) {
            mAccountUpload.cancel(true);
        }
        mMainHandler.removeMessages(WHAT_COMPUTER_TURN);
        mMainHandler.removeMessages(WHAT_COMPUTER_QUICK_HANDS);
        if (mGameMode != GameMode.TUTORIAL.ordinal()) {
            if (Game.getPlayersStillIn() > 1) {
                saveGame();
            } else {
                mAppPrefs.deleteGame();
            }
        } else {
            mAppPrefs.setTutorialStep(mTutorialStep);
            mAppPrefs.setTutorialConsole(mConsole.getText().toString());
        }
        saveAchievements();

    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        mPause = true;
        switch (id) {
            case ALERT_SCORE_DEDUCT:
                ((CustomDialog) dialog).setMessage(mDialogMessage);
                break;
            case ALERT_SCORE_ADD:
                ((CustomDialog) dialog).setMessage(mDialogMessage);
                break;
            case ALERT_WINNER:
                ((CustomDialog) dialog).setMessage(mDialogMessage);
                break;
            case ALERT_ACHIEVEMENT:
                ((CustomDialog) dialog).setMessage(mDialogMessage);
                break;
            case ALERT_TUTORIAL:
                ((CustomDialog) dialog).setMessage(mDialogMessage);
                break;
            case ALERT_PICKUP_PILE_CARD:
                ((CustomDialog) dialog).setMessage(String.format(getResources().getString(R.string.alert_tutorial_wrong_pile_card), mDropCards.print(),
                        mPickupCard.toShortString()));
                break;
            case ALERT_PICKUP_DECK_CARD:
                ((CustomDialog) dialog).setMessage(String.format(getResources().getString(R.string.alert_tutorial_wrong_deck_card), mDropCards.print()));
                break;
            case ALERT_NEW_GAME:
                ((CustomDialog) dialog).setMessage(mDialogMessage);

        }
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        AdView adView = (AdView) this.findViewById(R.id.adViewGame);
        adView.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainHandler = new ComputerHandler();

        final int screenSizeMask = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);

        if (screenSizeMask < Configuration.SCREENLAYOUT_SIZE_LARGE) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        Intent callingIntent = getIntent();
        mTimeSinceQuickHands = System.currentTimeMillis() - 500;
        mTimeSinceComputerQuickHands = System.currentTimeMillis() - 500;
        mGameMode = callingIntent.getIntExtra("com.bazsoft.yaniv.MODE", GameMode.RESUME.ordinal());
        if (mGameMode == GameMode.NEW.ordinal()) {
            callingIntent.removeExtra("com.bazsoft.yaniv.MODE");
        }

        mAppPrefs = AppPreferences.getInstance(getApplicationContext());

        mPause = false;

        initAchievements();
        initSounds();
        initFacebook();
        initConsole();
        if (mGameMode == GameMode.RESUME.ordinal() && mAppPrefs.isSavedGame()) {
            restoreGame();
        } else if (mGameMode == GameMode.TUTORIAL.ordinal()) {
            mTutorialStep = mAppPrefs.getTutorialStep();
            initTutorial();
        } else {
            mAppPrefs.deleteGame();
            newGame(mAppPrefs.getGamePlayerName());
        }

    }

    private void initFacebook() {
        if (mAppPrefs.isFacebookEnabled()) {
            mFacebook = FacebookApplication.getInstance(this);
        }
    }

    @Override
    public void onDestroy() {
        mAppPrefs.deleteTutorial();
        super.onDestroy();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Ignored

    }

    /**
     * Sets the next player and plays their turn.
     */
    private void playNextTurn() {
        game.setNextPlayer();
        playTurn();
    }

    private void playQuickHandsMove() {
        Card lastCard = game.getLastCardPickedUp();
        game.dropOntoPile(lastCard);
        mConsole.append(game.getPrevPlayer().getName() + " quickly drops " + lastCard.toShortString() + "\n");
        displayCards(game.getPreviousPlayerIndex(), false);
    }

    private void makeBottomViewsVisible(boolean visible) {
        final LinearLayout handValueLayout = (LinearLayout) findViewById(R.id.LinearLayout_Hand);
        final LinearLayout targetLayout = (LinearLayout) findViewById(R.id.LinearLayout_Target);
        makeViewVisible(handValueLayout, visible);
        makeViewVisible(targetLayout, visible);
    }

    private void makeYanivButtonVisible(boolean visible) {
        final Button button = (Button) findViewById(R.id.Button_Yaniv);
        makeViewVisible(button, visible);
    }

    private void makeTargetValueVisible(boolean visible) {
        final View view = findViewById(R.id.LinearLayout_Target);
        makeViewVisible(view, visible);
    }

    private void makeRoundVisible(boolean visible) {
        final View view = findViewById(R.id.LinearLayout_Round);
        makeViewVisible(view, visible);
    }

    private void makeYanivAtVisible(boolean visible) {
        final View view = findViewById(R.id.LinearLayout_YanivAt);
        makeViewVisible(view, visible);
    }

    private void makeTopPlayerVisible(boolean visible) {
        final View view = findViewById(R.id.FrameLayout_TopPlayer);
        makeViewVisible(view, visible);
    }

    private void makeSpeedUpButtonVisible(boolean visible) {
        final Button button = (Button) findViewById(R.id.Button_Speed_Up);
        makeViewVisible(button, visible);
    }

    private void makeNewGameButtonVisible(boolean visible) {
        final Button button = (Button) findViewById(R.id.Button_New_Game);
        makeViewVisible(button, visible);
    }

    private void makeNextRoundButtonVisible(boolean visible) {
        final Button button = (Button) findViewById(R.id.Button_Next_Round);
        makeViewVisible(button, visible);
    }

    private void clickNextRoundButton() {
        final Button button = (Button) findViewById(R.id.Button_Next_Round);
        if (button.getVisibility() == View.VISIBLE) {
            button.performClick();
        }
    }

    private void updateHandValue(String value) {
        final TextView handValue = (TextView) findViewById(R.id.TextView_HandValue);
        handValue.setText(value);
    }

    private void updateCompassImage() {
        ImageView compass = (ImageView) findViewById(R.id.ImageView_Compass);
        compass.setImageResource(playerCompassMap.get(game.getCurrentPlayerIndex()));

    }

    private void playTurn() {
        updateCompassImage();
        if (game.getCurrentPlayerType() == PlayerType.HUMAN && game.getCurrentPlayer().getHand().getValue() <= game.getYanivMinimum()) {
            makeYanivButtonVisible(true);
            makeTargetValueVisible(false);
        } else {
            makeYanivButtonVisible(false);
        }
        checkQuickHands();
        if (game.getCurrentPlayerType() == PlayerType.COMPUTER) {
            int randomInt = 0;
            if (!mSpeedUp) {
                Random randomGenerator = new Random(System.currentTimeMillis());
                randomInt = randomGenerator.nextInt(getSpeed()) + (4 - mAppPrefs.getComputerSpeed());
            }
            mComputerPlayerTask = new ComputerPlayerTask();
            mComputerPlayerTask.execute(randomInt);
        }

    }

    private int getSpeed() {
        int speed = mAppPrefs.getComputerSpeed();
        int maxNum;
        switch (speed) {
            case 1:
                maxNum = 7;
                break;
            case 2:
                maxNum = 4;
                break;
            default:
                maxNum = 3;
                break;
        }
        return maxNum;

    }

    private void initAchievements() {
        if (mAchievements == null) {
            Log.i(DEBUG_TAG, "Initialising achievements");
            mAchievements = new boolean[Achievement.values().length];
        }
        Log.i(DEBUG_TAG, "Updating achievements");
        mAppPrefs.updateAchievements(mAchievements);
    }

    /**
     * Starts the tutorial
     */
    private void initTutorial() {
        // Define which cards should be shown for each player
        // Using deck positions A-Kd = 0-12, A-Kh = 13-25, A-Kc = 26-38, A-Ks =
        // 39-51
        int[] player1Cards = {10, 20, 22, 36, 39};
        // Jd, 8h, 10h, Jc, As
        int[] player1Cards12 = {20, 22, 31, 39};
        // 8h, 10h, 6c, As - player 1 has exchanged J's for 6c
        int[] player1Cards16 = {20, 31, 39, 9};
        // 8h, 6c, As, 10d - player 1 has exchanged 10h for 10d
        int[] player1Cards18 = {20, 31, 39};
        // 8h, 6c, As - player 1 has dropped 10d
        int[] player1Cards21 = {31, 39, 18};
        // 6c, As, 6h - player 1 has exchanged 8h for 6h
        int[] player1Cards25 = {39, 1};
        // As, 2d
        int[] player2Cards = {16, 25, 29, 51, 52};
        // 4h, Kh, 4c Ks, JKR
        int[] player2Cards14 = {16, 18, 29, 52};
        // 4h, 6h, 4c, JKR - player 2 has exchanged K's for 6h
        int[] player2Cards19 = {29, 37};
        // 4c, Qc - player 2 has exchanged 4, JKR, 6 for Qc
        int[] player2Cards23 = {29, 30};
        // 4c, 5c - player 2 has exchanged Qc for 5c
        int[] player2Cards27 = {29, 53};
        // 4c, JKR - player 2 has exchanged 5c for JKR
        int[] deckCards = {31, 18, 9, 37, 30, 1, 53};
        // 6c, 7s, 10d, Qc, 5c, 2d, JKR
        int[] deckCards12 = {18, 9, 37, 30, 1, 53};
        int[] deckCards14 = {9, 37, 30, 1, 53};
        int[] deckCards16 = {37, 30, 1, 53};
        int[] deckCards19 = {30, 1, 53};
        int[] deckCards23 = {1, 53};
        int[] deckCards25 = {53};
        int[] deckCards27 = {};
        int[] pileCards = {45};
        int[] pileCards12 = {10, 36};
        int[] pileCards14 = {25, 51};
        int[] pileCards16 = {22};
        int[] pileCards18 = {22, 9};
        int[] pileCards19 = {16, 52, 18};
        int[] pileCards21 = {20};
        int[] pileCards23 = {37};
        int[] pileCards25 = {31, 18};
        int[] pileCards27 = {30};
        int[] player1set, player2set, pileset, deckset;
        String playerName = mAppPrefs.getGamePlayerName();
        mConsole.setText(mAppPrefs.getTutorialConsole());
        game = new Game(createPlayers(playerName, 2));
        game.setRound(1);
        initPlayerMaps();
        mQuickHands = false;
        mSpeedUp = false;
        updateTargetValue();
        updateYanivAtValue();
        updateRoundValue();
        switch (mTutorialStep) {
            case 12:
            case 13:
                player1set = player1Cards12;
                player2set = player2Cards;
                pileset = pileCards12;
                deckset = deckCards12;
                break;
            case 14:
            case 15:
                player1set = player1Cards12;
                player2set = player2Cards14;
                pileset = pileCards14;
                deckset = deckCards14;
                break;
            case 16:
                player1set = player1Cards16;
                player2set = player2Cards14;
                pileset = pileCards16;
                deckset = deckCards16;
                break;
            case 17:
                player1set = player1Cards16;
                player2set = player2Cards14;
                pileset = pileCards16;
                deckset = deckCards16;
                break;
            case 18:
                player1set = player1Cards18;
                player2set = player2Cards14;
                pileset = pileCards18;
                deckset = deckCards16;
                break;
            case 19:
            case 20:
            case 21:
                player1set = player1Cards18;
                player2set = player2Cards19;
                pileset = pileCards19;
                deckset = deckCards19;
                break;
            case 22:
            case 23:
                player1set = player1Cards21;
                player2set = player2Cards19;
                pileset = pileCards21;
                deckset = deckCards19;
                break;
            case 24:
            case 25:
                player1set = player1Cards21;
                player2set = player2Cards23;
                pileset = pileCards23;
                deckset = deckCards23;
                break;
            case 26:
            case 27:
                player1set = player1Cards25;
                player2set = player2Cards23;
                pileset = pileCards25;
                deckset = deckCards25;
                break;
            case 28:
            case 29:
            case 30:
                player1set = player1Cards25;
                player2set = player2Cards27;
                pileset = pileCards27;
                deckset = deckCards27;
                break;
            default:
                player1set = player1Cards;
                player2set = player2Cards;
                deckset = deckCards;
                pileset = pileCards;
        }
        for (int card : player1set) {
            getThisPlayerHand().addCard(Deck.getCard(card));
        }
        for (int card : player2set) {
            game.getPlayer(1).getHand().addCard(Deck.getCard(card));
        }
        game.getDeck().clear();
        for (int card : deckset) {
            game.getDeck().add(Deck.getCard(card));
        }
        for (int card : pileset) {
            game.pile().addCard(Deck.getCard(card));
        }
        initScoreboard();
        displayAllCards(true);
        nextTutorialStep();
    }

    private void growShrinkView(View layout) {
        final Animation growShrink = AnimationUtils.loadAnimation(this, R.anim.grow_shrink);
        if (layout instanceof ViewGroup) {
            for (int index = 0; index < ((ViewGroup) layout).getChildCount(); index++) {
                View view = ((ViewGroup) layout).getChildAt(index);
                if (!(view instanceof ImageView)) {
                    break;
                }
                ImageView card = (ImageView) view;
                card.startAnimation(growShrink);
                growShrink.setAnimationListener(this);
            }
        } else {
            ImageView image = (ImageView) layout;
            image.startAnimation(growShrink);
            growShrink.setAnimationListener(this);
        }
    }

    private void displayTutorialToast(int toast_text, int yOffset, View layout) {
        Toast toast;
        toast = Toast.makeText(this, toast_text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, yOffset);
        toast.show();
        growShrinkView(layout);

    }

    private void nextTutorialStep() {
        mTutorialStep++;
        switch (mTutorialStep) {
            case 1:
                mDialogMessage = getResources().getString(R.string.tutorial_01);
                showDialog(ALERT_TUTORIAL);
                break;
            case 2:
                displayTutorialToast(R.string.tutorial_toast_2, 180, findViewById(playerCardMap.get(0)));
                break;
            case 3:
                displayTutorialToast(R.string.tutorial_toast_3, -120, findViewById(playerCardMap.get(1)));
                break;
            case 4:
                displayTutorialToast(R.string.tutorial_toast_4, 0, findViewById(R.id.LinearLayout_Deck));
                break;
            case 5:
                displayTutorialToast(R.string.tutorial_toast_5, 0, findViewById(R.id.LinearLayout_Pile));
                break;
            case 6:
                displayTutorialToast(R.string.tutorial_toast_6, 100, findViewById(R.id.ImageView_Compass));
                break;
            case 7:
                mDialogMessage = getResources().getString(R.string.tutorial_07);
                showDialog(ALERT_TUTORIAL);
                break;
            case 8:
                mDialogMessage = getResources().getString(R.string.tutorial_08);
                mMainHandler.sendEmptyMessageDelayed(WHAT_SHOW_TUTORIAL_DIALOG, 250);
                break;
            case 9:
                mDialogMessage = getResources().getString(R.string.tutorial_09);
                mMainHandler.sendEmptyMessageDelayed(WHAT_SHOW_TUTORIAL_DIALOG, 250);
                break;
            case 10:
                mDialogMessage = getResources().getString(R.string.tutorial_10);
                mMainHandler.sendEmptyMessageDelayed(WHAT_SHOW_TUTORIAL_DIALOG, 250);
                break;
            case 11:
                mDialogMessage = getResources().getString(R.string.tutorial_11);
                mMainHandler.sendEmptyMessageDelayed(WHAT_SHOW_TUTORIAL_DIALOG, 250);
                break;
            case 12:
                mTutorialTurn = Turn.PICKUP_DECK;
                mDropCards.clear();
                mDropCards.add(Deck.getCard(10));
                mDropCards.add(Deck.getCard(36));
                game.setCurrentPlayer(-1);
                game.setWinner(-1);
                playNextTurn();
                break;
            case 13:
                mDialogMessage = getResources().getString(R.string.tutorial_13);
                showDialog(ALERT_TUTORIAL);
                break;
            case 14:
                game.setCurrentPlayer(0);
                playNextTurn();
                break;
            case 15:
                mDialogMessage = getResources().getString(R.string.tutorial_15);
                showDialog(ALERT_TUTORIAL);
                break;
            case 16:
                mTutorialTurn = Turn.PICKUP_DECK;
                mDropCards.clear();
                mDropCards.add(Deck.getCard(22));
                game.setCurrentPlayer(1);
                playNextTurn();
                break;
            case 17:
                mDialogMessage = getResources().getString(R.string.tutorial_17);
                showDialog(ALERT_TUTORIAL);
                break;
            case 18:
                game.setPrevPlayer(0);
                mQuickHands = true;
                break;
            case 19:
                mQuickHands = false;
                mDialogMessage = getResources().getString(R.string.tutorial_19);
                showDialog(ALERT_TUTORIAL);
                break;
            case 20:
                game.setCurrentPlayer(0);
                playNextTurn();
                break;
            case 21:
                mDialogMessage = getResources().getString(R.string.tutorial_21);
                showDialog(ALERT_TUTORIAL);
                break;
            case 22:
                mTutorialTurn = Turn.PICKUP_PILE;
                mDropCards.clear();
                mDropCards.add(Deck.getCard(20));
                mPickupCard = Deck.getCard(18);
                game.setCurrentPlayer(1);
                playNextTurn();
                break;
            case 23:
                mDialogMessage = getResources().getString(R.string.tutorial_23);
                showDialog(ALERT_TUTORIAL);
                break;
            case 24:
                game.setCurrentPlayer(0);
                playNextTurn();
                break;
            case 25:
                mDialogMessage = getResources().getString(R.string.tutorial_25);
                showDialog(ALERT_TUTORIAL);
                break;
            case 26:
                mTutorialTurn = Turn.PICKUP_DECK;
                mDropCards.clear();
                mDropCards.add(Deck.getCard(18));
                mDropCards.add(Deck.getCard(31));
                game.setCurrentPlayer(1);
                playNextTurn();
                break;
            case 27:
                mDialogMessage = getResources().getString(R.string.tutorial_27);
                showDialog(ALERT_TUTORIAL);
                break;
            case 28:
                game.setCurrentPlayer(0);
                playNextTurn();
                break;
            case 29:
                mDialogMessage = getResources().getString(R.string.tutorial_29);
                showDialog(ALERT_TUTORIAL);
                makeYanivButtonVisible(true);
                break;
            case 30:
                mTutorialTurn = Turn.CALL_YANIV;
                game.setCurrentPlayer(1);
                playNextTurn();
                break;
            case 31:
                mDialogMessage = getResources().getString(R.string.tutorial_31);

                showDialog(ALERT_TUTORIAL_END);
        }

    }

    /**
     * Restores all the data for a game.
     */
    private void restoreGame() {
        game = new Game(createPlayers(mAppPrefs.getGamePlayerName(), mAppPrefs.getGameNumPlayers()), CARDS_PER_PLAYER, mAppPrefs.getGameYanivMinimum());
        initPlayerMaps();
        mConsole.setText(mAppPrefs.getConsoleText());

        for (Player player : game.getPlayers()) {
            mAppPrefs.updatePlayer(player);
        }

        mAppPrefs.updatePile(game.pile());
        mAppPrefs.updateDeck(game.getDeck());

        game.setWinner(mAppPrefs.getWinner());
        game.setCurrentPlayer(mAppPrefs.getCurrentPlayer());
        game.setPrevPlayer(mAppPrefs.getPreviousPlayer());
        game.setRound(mAppPrefs.getRound());
        game.setTurn(mAppPrefs.getTurn());
        game.setGameTurn(mAppPrefs.getGameTurn());
        mNumPlayersStartOfRound = mAppPrefs.getRoundPlayers();
        initScoreboard();

        mYanivsInARow = mAppPrefs.getYanivsInARow();
        mAssafsThisGame = mAppPrefs.getAssafsThisGame();
        mYanivPossible = mAppPrefs.getYanivPossible();
        mHalf = mAppPrefs.getHalves();
        updateYanivAtValue();
        updateYanivAtColour();
        updateRoundValue();

        if (game.getPlayer(THIS_PLAYER).isOut()) {
            makeSpeedUpButtonVisible(true);
            makeNewGameButtonVisible(true);
            makeBottomViewsVisible(false);
        } else {
            makeBottomViewsVisible(true);
        }
        updateTargetValue();
        if (game.getWinner() != -1) {
            displayAllCards(true);
            makeNextRoundButtonVisible(true);
            calculateRating();
        } else {
            displayAllCards(false);
            playTurn();
        }

        mMainHandler.sendEmptyMessageDelayed(WHAT_CONSOLE_UPDATER, 500);

    }

    /**
     * Saves all the achievements to the shared preferences and also the player's rating, games won and games played.
     */
    private void saveAchievements() {
        int achievementCount = 0;
        for (boolean achievement : mAchievements) {
            if (achievement) {
                achievementCount++;
            }
        }
        Log.i(DEBUG_TAG, "Saving " + achievementCount + " achievements");

        mAppPrefs.saveAchievements(mAchievements);
    }

    /**
     * Saves all the data necessary to restore a game.
     */
    private void saveGame() {

        mAppPrefs.setGameNumPlayers(Game.getNumPlayers());
        mAppPrefs.setRound(game.getRound());
        mAppPrefs.setTurn(game.getTurn());
        mAppPrefs.setConsoleText(mConsole.getText().toString());
        mAppPrefs.setWinner(game.getWinner());
        mAppPrefs.setCurrentPlayer(game.getCurrentPlayerIndex());
        mAppPrefs.setPreviousPlayer(game.getPreviousPlayerIndex());
        mAppPrefs.setRoundPlayers(mNumPlayersStartOfRound);
        mAppPrefs.saveGameTurn(game.getGameTurn());
        mAppPrefs.setGameYanivMinimum(game.getYanivMinimum());

        for (Player player : game.getPlayers()) {
            mAppPrefs.savePlayer(player);
        }

        mAppPrefs.savePile(game.pile());
        mAppPrefs.saveDeck(game.getDeck());

        mAppPrefs.setYanivs(mYanivsInARow);
        mAppPrefs.setYanivPossible(mYanivPossible);
        mAppPrefs.setAssaf(mAssafsThisGame);
        mAppPrefs.setHalves(mHalf);
    }

    /**
     * Updates the score board with the latest scores and orders it by player score ascending.
     */
    private void updateScoreboard() {

        Player[] copyPlayers = game.getPlayers().clone();

        Arrays.sort(copyPlayers, new Comparator<Player>() {
            @Override
            public int compare(final Player p1, final Player p2) {
                if (p1.isOut() && p2.isOut() && p1.getRoundOut() != -1 && p2.getRoundOut() != -1 && p2.getRoundOut() != p1.getRoundOut()) {
                    return p2.getRoundOut() - p1.getRoundOut();
                }
                return p1.getScore() - p2.getScore();
            }
        });

        TableLayout table = (TableLayout) findViewById(R.id.TableLayout_Scores);
        for (int i = 0; i < table.getChildCount(); i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            final int headerRows = 3;
            if (i < headerRows) {
                // Do nothing - header
            } else {
                if (i - (headerRows - 1) <= Game.getNumPlayers()) {
                    row.setVisibility(View.VISIBLE);
                    ImageView avatarCell = (ImageView) row.getChildAt(0);
                    TextView nameCell = (TextView) row.getChildAt(1);
                    TextView scoreCell = (TextView) row.getChildAt(2);
                    TextView scoreChange = (TextView) ((LinearLayout) row.getChildAt(3)).getChildAt(0);
                    TextView halved = (TextView) ((LinearLayout) row.getChildAt(3)).getChildAt(1);
                    ImageView happy = (ImageView) ((LinearLayout) row.getChildAt(3)).getChildAt(2);
                    if (copyPlayers[i - headerRows].getPlayerType() == PlayerType.HUMAN) {
                        String strAvatarUri = mAppPrefs.getAvatar();
                        if (strAvatarUri != null) {
                            Uri imageUri = Uri.parse(strAvatarUri);
                            avatarCell.setImageURI(imageUri);
                        } else {
                            avatarCell.setImageResource(R.drawable.avatar_not_set);
                        }
                    } else {// Computer
                        avatarCell.setImageResource(R.drawable.android);
                    }
                    nameCell.setText(copyPlayers[i - headerRows].getName());
                    int score = copyPlayers[i - headerRows].getScore();
                    if (copyPlayers[i - headerRows].isOut()) {
                        scoreCell.setText(String.valueOf(score) + " (OUT)");
                    } else {
                        scoreCell.setText(String.valueOf(score));
                    }
                    if (copyPlayers[i - headerRows].isScoreHalved()) {
                        halved.setVisibility(View.VISIBLE);
                    } else {
                        halved.setVisibility(View.GONE);
                    }
                    if (copyPlayers[i - headerRows].getScoreChange() > 0) {
                        scoreChange.setText("+" + copyPlayers[i - headerRows].getScoreChange());
                        if (!copyPlayers[i - headerRows].isOut()
                                || (copyPlayers[i - headerRows].isOut() && copyPlayers[i - headerRows].getRoundOut() >= (game.getRound() - 1))) {
                            scoreChange.setVisibility(View.VISIBLE);
                        } else {
                            scoreChange.setVisibility(View.GONE);
                        }
                        happy.setVisibility(View.GONE);
                    } else if (copyPlayers[i - headerRows].getScoreChange() == 0) {
                        scoreChange.setVisibility(View.GONE);
                        happy.setVisibility(View.VISIBLE);
                    } else {
                        scoreChange.setVisibility(View.GONE);
                        happy.setVisibility(View.GONE);
                    }
                } else {
                    row.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mPause = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mSlidingDrawer.isOpened()) {
                mSlidingDrawer.animateClose();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private class ComputerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_COMPUTER_TURN: // Normal computer move
                    Log.d(DEBUG_TAG, "Computer turn");
                    printTurn();
                    if (game.getGameTurn().turnType == Turn.CALL_YANIV) {
                        handleYaniv();
                    } else {
                        if (mGameMode == GameMode.TUTORIAL.ordinal()) {
                            displayCards(game.getCurrentPlayerIndex(), true);
                            nextTutorialStep();
                        } else {
                            displayCards(game.getCurrentPlayerIndex(), false);
                            playNextTurn();
                        }
                    }
                    break;
                case WHAT_COMPUTER_QUICK_HANDS: // Quick hands move
                    mTimeSinceComputerQuickHands = System.currentTimeMillis();
                    playQuickHandsMove();
                    if (!mAchievements[Achievement.TOO_SLOW.ordinal()]) {
                        displayAchievement(Achievement.TOO_SLOW);
                    }
                    break;
                case WHAT_CONSOLE_UPDATER: // Scroll the console to the bottom
                    afterTextChanged(mConsole.getEditableText());
                    break;
                case WHAT_CLOSE_SCOREBOARD:
                    if (mSlidingDrawer.isOpened()) {
                        mSlidingDrawer.animateClose();
                    }
                    if (mGameMode == GameMode.TUTORIAL.ordinal()) {
                        nextTutorialStep();
                    }
                    break;
                case WHAT_SHOW_TUTORIAL_DIALOG:
                    showDialog(ALERT_TUTORIAL);
                    break;
            }

        }

    }

    private class ComputerPlayerTask extends AsyncTask<Integer, Integer, Boolean> {

        private ProgressBar mProgressBar;

        @Override
        protected Boolean doInBackground(Integer... params) {
            final int delayTime = params[0];
            // The line below can cause a concurrent exception - not sure why
            // Log.i(DEBUG_TAG, mGame.getCurrentPlayer().getName() + " : "
            // + mGame.getCurrentPlayer().printHand());

            YanivGameActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setMax(delayTime - 1);
                }
            });
            boolean interupted = false;
            for (int i = 0; i < delayTime; i++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.e(DEBUG_TAG, "Can't sleep thread");
                    interupted = true;
                    break;
                }
                if (mPause) {
                    i--;
                } else {
                    publishProgress(1);
                }
            }
            if (!interupted) {
                if (mQuickHands) {
                    mQuickHands = false; // Stop the human playing quick hands now.
                    mTimeSinceQuickHands = System.currentTimeMillis();
                }
                game.playAutomaticTurn();
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (result) {
                mMainHandler.sendEmptyMessage(WHAT_COMPUTER_TURN);
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            mProgressBar = (ProgressBar) findViewById(playerProgressBarMap.get(game.getCurrentPlayerIndex()));
            mProgressBar.setProgress(0);
            if (!mSpeedUp) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onCancelled() {
            Log.i(DEBUG_TAG, "onCancelled");
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressBar.incrementProgressBy(values[0]);
        }

    }

}
