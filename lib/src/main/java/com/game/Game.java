package com.game;

import com.cards.Card;
import com.cards.CardVector;
import com.cards.Deck;
import com.cards.Hand;
import com.cards.Kind;
import com.cards.Pile;
import com.cards.PileCard;
import com.cards.Straight;
import com.player.Player;
import com.player.PlayerType;

/**
 * The Game class includes all the logic for determining the behaviour of the computer players in a game of Yaniv. A Game consists of a deck of cards, a discard
 * pile and some players.
 *
 * @author Barry Irvine
 * @see Deck
 * @see Pile
 * @see Player
 */
public class Game {

    /**
     * The number of cards per player in Yaniv is normally 5 or 7. The game defaults to 5. The code does not yet support 7 card Yaniv.
     */
    private static final int DEFAULT_CARDS_PER_PLAYER = 5;
    /**
     * The value that your hand needs to be equal to or lower than before calling Yaniv.
     */
    private static final int DEFAULT_CALL_YANIV_MINIMUM = 5;
    /**
     * The players of the Yaniv game.
     */
    private static Player[] players;
    /**
     * The number of players that are not yet out
     */
    private static int numPlayersStillIn;
    protected GameTurn gameTurn;
    /**
     * The Deck from which cards are dealt and players can pick up from.
     */
    private Deck deck;
    /**
     * The visible discard Pile where players drop cards and can pickup from.
     */
    private Pile pile;
    /**
     * The number of players
     */
    private int numPlayers;
    /**
     * The number of cards dealt initially to each player.
     */
    private int cardsPerPlayer;
    /**
     * The value that a player's hand needs to be lower than before they can call Yaniv.
     */
    private int callYanivMinimum;
    /**
     * The winner of the last round of Yaniv. Used to determine who starts the next round.
     */
    private int winner = -1;
    /**
     * The current round number.
     */
    private int round;
    /**
     * The current turn number.
     */
    private int turnNum;
    /**
     * A variable indicating which player currently has a turn.
     */
    private int currentPlayer = -1;
    /**
     * A variable indicating which player played last.
     */
    private int prevPlayer = -1;

    /**
     * Creates an empty game for the players
     *
     * @param players The array of players
     */
    public Game(Player[] players) {
        this(players, DEFAULT_CARDS_PER_PLAYER, DEFAULT_CALL_YANIV_MINIMUM);
    }

    /**
     * Creates an empty game for the players. Each player will be dealt the cardsPerPlayer cards
     *
     * @param players          The array of players
     * @param cardsPerPlayer   The number of cards per player
     * @param callYanivMinimum The minimum Yaniv score
     */
    public Game(Player[] players, int cardsPerPlayer, int callYanivMinimum) {

        Game.players = players;
        this.cardsPerPlayer = cardsPerPlayer;
        this.numPlayers = players.length;
        Game.numPlayersStillIn = numPlayers;
        this.deck = new Deck(true);
        this.pile = new Pile(this);
        this.callYanivMinimum = callYanivMinimum;
        this.round = 0;
        this.currentPlayer = -1;
        this.prevPlayer = -1;
        this.gameTurn = null;
        Player.setGame(this);
    }

    public static int getNumPlayers() {
        return players.length;
    }

    public static int getPlayersStillIn() {
        return numPlayersStillIn;
    }

    /**
     * Adds a card to the deck. Used when the pile is emptied and the remaining pile cards are put on the deck.
     *
     * @param card The card added to the deck
     */
    public void addCardToDeck(final Card card) {
        deck.addElement(card);
    }

    public GameTurn getGameTurn() {
        return this.gameTurn;
    }

    public void setGameTurn(GameTurn turn) {
        this.gameTurn = turn;
    }

    public int getYanivMinimum() {
        return this.callYanivMinimum;
    }

    /**
     * Used when the current player calls "Yaniv". It is determined whether the current player actually won (he has the lowest score) or whether he was Assaf-ed
     * by one of the other players. This function calculates the scores for all the players based on the result.
     */
    public void callYaniv() {
        // Store the current player's score
        final int score = getCurrentPlayer().getHand().getValue();
        // Assaf defines whether the current player has been beaten by another
        // player
        boolean assaf = false;
        // The Assaf score is the score value of a player's hand that has beaten the current player's score. This is needed for the scenario that multiple players
        // beat the current player. Only the one(s) with the absolute lowest score win.
        int assafScore = score;
        this.winner = currentPlayer;

        // First determine who has the best score
        for (int i = 0; i < numPlayers; i++) {
            if ((!players[i].isOut()) && i != currentPlayer && players[i].getHand().getValue() <= assafScore) {
                assafScore = players[i].getHand().getValue();
                assaf = true;
            }
        }
        for (int i = 0; i < numPlayers; i++) {
            if ((!players[i].isOut()) && i != currentPlayer && players[i].getHand().getValue() <= assafScore) {
                // System.out.println(getCurrentPlayer().getName() + " has been ASSAF-ed by " + players[i].getName());
                winner = i;
            }
        }
        if (assaf) {
            // Current player gets a 30 point penalty
            getCurrentPlayer().addScore(score + 30);
        } else {
            getCurrentPlayer().addScore(0);
        }

        for (int i = 0; i < numPlayers; i++) {
            if ((!players[i].isOut()) && i != currentPlayer) {
                // Don't add any score for Assaf players that have the lowest score
                // TODO: Put in new option for assaf scoring.
                if (players[i].getHand().getValue() > assafScore) {
                    players[i].addScore(players[i].getHand().getValue());
                } else {
                    players[i].addScore(0);
                }
            }
        }
        this.setGameTurn(new GameTurn(Turn.CALL_YANIV));

    }

    public void decrementPlayersIn() {
        numPlayersStillIn--;
    }

    public void dealCards() {

        deck = new Deck(true);
        pile = new Pile(this);

        for (Player player : players) {
            player.getHand().empty();
        }
        for (int i = 0; i < cardsPerPlayer; i++) {
            for (Player player : players) {
                if (!(player.isOut()))
                    player.getHand().addCard(deck.dealCard());
            }
        }
        pile.addCard(deck.dealCard());
    }

    public Player getCurrentPlayer() {
        if (currentPlayer != -1) {
            return Game.players[currentPlayer];
        }
        return null;
    }

    public void setCurrentPlayer(int playerIndex) {
        currentPlayer = playerIndex;
    }

    public CardVector getDeck() {
        return deck.getCards();
    }

    public Player getPrevPlayer() {
        if (prevPlayer != -1)
            return players[prevPlayer];
        return null;
    }

    public void setPrevPlayer(int playerIndex) {
        prevPlayer = playerIndex;
    }

    public Player getPlayer(int index) {
        return players[index];
    }

    public Player[] getPlayers() {
        return players;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayer;
    }

    public int getPreviousPlayerIndex() {
        return prevPlayer;
    }

    public PlayerType getPreviousPlayerType() {
        if (prevPlayer != -1) {
            return players[prevPlayer].getPlayerType();
        }
        return null;
    }

    public Card getLastCardPickedUp() {
        if (prevPlayer != -1) {
            return gameTurn.cardPickedUp;
        }
        return null;
    }

    public PlayerType getCurrentPlayerType() {
        if (currentPlayer != -1) {
            return players[currentPlayer].getPlayerType();
        }
        return null;
    }

    /**
     * @return The winner of the last round of Yaniv. If this was the last round then it is the overall winner of the game.
     */
    public int getWinner() {
        return winner;
    }

    /**
     * Used when restoring a game from a saved copy.
     *
     * @param winner The player number of the winner of the last round.
     */
    public void setWinner(int winner) {
        this.winner = winner;
    }

    public Player getWinningPlayer() {
        if (winner != -1) {
            return players[winner];
        } else {
            return null;
        }

    }

    /**
     * @return The pile cards including whether a card is available to be picked up.
     */
    public Pile pile() {
        return this.pile;
    }

    /**
     * Plays a turn for the computer.
     */
    public void playAutomaticTurn() {
    /* Check for available moves */

        Hand playerHand; // stores current player's hand
        Kind handKind; // stores rank of highest x of a kind in a player's hand
        Straight handStraight; // stores straight in player's hand
        Card highestCard; // temporary Card variable for highest droppable card in hand
        int highestCardPos; // Position of the highest playable card in the player's hand
        PileCard bestCard; // Best card in the available cards on the pickup pile
        int overlappingCards; // Temporary variable used to see how many cards overlap in two sets
        boolean betterHand;
        Card prevCard = null;

        // Create a shorthand for current player's hand
        playerHand = getCurrentPlayer().getHand();

        // See if there are any multiples of the same rank
        handKind = playerHand.findOfAKind();

        // See if there are any straights of the same suit
        handStraight = playerHand.findStraight();

        // Get highest card in hand
        highestCardPos = playerHand.getHighestCardPos();
        highestCard = playerHand.getCard(highestCardPos);

        pile.rankPileCards(playerHand, handStraight, handKind);

        bestCard = pile.getBestCard();

        if (playerHand.getValue() <= callYanivMinimum) {
            betterHand = false;
            if (playerHand.size() > 0) {
                for (Player player : players) {
          /*
           * Check that no other player has a known better hand than current player
           */
                    if (!(player.equals(getCurrentPlayer()) || player.isOut())
                            && ((player.getHand().size() == 1 && player.getHand().isLastCardKnown() && player.getHand().getLastCard() != null && player.getHand()
                            .getLastCard().getSoftValue() <= playerHand.getValue()) || player.getHand().size() == 0)) {
                        betterHand = true;
                    }
                }
            }
            if (!betterHand) {
                callYaniv();
                return;
            }
        }

        if (prevPlayer != -1 && gameTurn != null && gameTurn.turnType == Turn.PICKUP_PILE) {
            prevCard = gameTurn.cardPickedUp;
        }

        // If we have a straight and it's worth more than any x of a kinds and
        // the highest card in the hand we'll play that
        if (handStraight.size() > 0 && handStraight.getValue() >= handKind.getValue() && handStraight.getValue() >= highestCard.getSoftValue()) {

            // Need to check that the pickup card doesn't add to our existing
            // straight

            if (bestCard != null && (bestCard.getScore() & PileCard.STRAIGHT) != 0) {

                // If the potential straight and the existing straight are just
                // extensions of each other or replace the joker with a value
                // card
                // then we should not drop the straight and pickup the card (if
                // we have free cards in our hand apart from jokers - already
                // checked)
                overlappingCards = bestCard.getStraight().cardsOverlap(handStraight);
                if (overlappingCards == handStraight.size()
                        || overlappingCards + handStraight.getJokerCount() - bestCard.getStraight().getJokerCount() == handStraight.size()) {
                    // Get the highest card in the hand that isn't part of the straight
                    while (handStraight.cardsOverlap(highestCard) > 0 && highestCardPos > 0) {
                        highestCard = playerHand.getCard(--highestCardPos);
                    }

                    // Drop high card and pick up to add to straight
                    swapWithPileCard(bestCard, highestCard, 1);
                    return;
                }

            }

            // Drop straight and pickup card
            if (bestCard != null) {
                swapWithPileCard(bestCard, handStraight, 2);
            } else {
                // Drop straight and pickup from deck
                swapWithDeckCard(handStraight, 3);
            }
            return;
        }

        // If we have an x of a kind and it's worth more than the highest card
        // in the hand and is bigger than a potential straight we'll play that
        // Added handKind size on 13/11.
        if (handKind.size() > 0 && handKind.getValue() >= highestCard.getSoftValue()) {

            // Need to check that the pickup card doesn't add to our existing of
            // a kind

            if (bestCard != null && (bestCard.getScore() & PileCard.OF_A_KIND) != 0) {
                if (bestCard.getRank() == handKind.getRank()) {
                    // Get the highest card in the hand that isn't of a kind
                    while (highestCard.getRank() == handKind.getRank() && highestCardPos > 0) {
                        highestCard = playerHand.getCard(--highestCardPos);
                    }

                    if (prevCard != null && prevCard.getRank() == highestCard.getRank() && highestCardPos > 0
                            && (playerHand.size() - playerHand.getJokerCount()) > highestCardPos) {
                        highestCard = playerHand.getCard(--highestCardPos);
                    }
                    // Drop high card and pick up to add to of a kind
                    swapWithPileCard(bestCard, highestCard, 4);
                    return;
                }
            }

            // Drop of a kind and pickup card
            if (bestCard != null) {
                // Need to check that straight isn't in of a kind we are
                // dropping

                if (bestCard.getStraight().size() > 0 && (bestCard.getScore() & PileCard.OF_A_KIND) != 0 && handKind.cardsOverlap(bestCard.getStraight()) > 0) {

                    // Either pickup card now has no value to the player - go to
                    // deck
                    // or we want to drop something else and pick up straight
                    // Might need to make this if tighter

                    if (bestCard.getStraight().size() - bestCard.getStraight().getJokerCount() > handKind.size()) {
                        // Going to drop something else and pickup straight

                        // Find the highest card that isn't part of the
                        // potential straight
                        while (bestCard.getStraight().cardsOverlap(highestCard) > 0) {
                            highestCard = playerHand.getCard(--highestCardPos);
                        }
                        swapWithPileCard(bestCard, highestCard, 5);
                    } else {
                        // If the card the previous player picked up is in the x
                        // of a kind
                        // we were about to play and we have other cards
                        // available.
                        if (prevCard != null && prevCard.getRank() == handKind.getRank() && (playerHand.size() - playerHand.getJokerCount() - handKind.size()) > 0) {
                            while (highestCard.getRank() == handKind.getRank() && highestCardPos > 0) {
                                highestCard = playerHand.getCard(--highestCardPos);
                            }
                            highestCard = playerHand.getCard(highestCardPos);
                            // Only swap the highest card if it is not part of
                            // the kind or a joker.
                            if (!(highestCard.getRank() != handKind.getRank() && highestCard.getJoker() != null)) {
                                swapWithPileCard(bestCard, highestCard, 6);
                            }
                        }
                        swapWithDeckCard(handKind, 7);
                    }
                    return;
                } else {
                    // If the card the previous player picked up is in the x of
                    // a kind
                    // we were about to play and we have other cards available.
                    if (prevCard != null && prevCard.getRank() == handKind.getRank() && (playerHand.size() - playerHand.getJokerCount() - handKind.size() > 0)) {
                        while (highestCard.getRank() == handKind.getRank() && highestCardPos > 0) {
                            highestCard = playerHand.getCard(--highestCardPos);
                        }
                        // Only swap the highest card if it is not part of the
                        // kind or a joker.
                        if (!(highestCard.getRank() != handKind.getRank() && highestCard.getJoker() != null)) {
                            swapWithPileCard(bestCard, highestCard, 8);
                            return;
                        }
                    }
                    /**
                     * If the hand kind we were about to drop overlaps with the potential straight we only want to drop the card in the kind that doesn't overlap
                     */
                    if (handKind.cardsOverlap(bestCard.getStraight()) > 0) {
                        while (bestCard.getStraight().cardsOverlap(highestCard) > 0) {
                            highestCard = playerHand.getCard(--highestCardPos);
                        }
                        swapWithPileCard(bestCard, highestCard, 9);

                    } else {
                        swapWithPileCard(bestCard, handKind, 10);
                    }
                    return;
                }
            } else {
                // If the card the previous player picked up is in the x of a
                // kind
                // we were about to play and we have other cards available.
                if (prevCard != null && prevCard.getRank() == handKind.getRank() && (playerHand.size() - playerHand.getJokerCount() - handKind.size()) > 0) {
                    while (highestCard.getRank() == handKind.getRank() && highestCardPos > 0) {
                        highestCard = playerHand.getCard(--highestCardPos);
                    }
                    // Only swap the highest card if it is not part of the kind
                    // or a joker.
                    if (!(highestCard.getRank() != handKind.getRank() && highestCard.getJoker() != null)) {
                        // Double check that the new highest card doesn't also
                        // form part of a kind.
                        handKind = playerHand.searchOfAKind(highestCard);
                        if (handKind.size() > 0) {
                            swapWithDeckCard(handKind, 11);
                        } else {
                            swapWithDeckCard(highestCard, 12);
                        }
                        return;
                    }
                }
                // Drop of a kind and pickup from deck
                swapWithDeckCard(handKind, 13);
                return;
            }
        }

        if (bestCard != null) {

            if ((bestCard.getScore() & PileCard.STRAIGHT) != 0) {
                if (bestCard.getStraight().contains(bestCard)) {
                    // Get the highest card in the hand that isn't part of the
                    // search straight
                    while (bestCard.getStraight().cardsOverlap(highestCard) > 0) {
                        highestCard = playerHand.getCard(--highestCardPos);
                    }
                }
            }

            // Need to check if best card matches the rank of our highest card

            if ((bestCard.getScore() & PileCard.OF_A_KIND) != 0) {
                if (bestCard.getRank() == highestCard.getRank() && highestCardPos > playerHand.getJokerCount()) {
                    // Get the highest card in the hand that isn't of a kind
                    while (highestCard.getRank() == bestCard.getRank() && highestCardPos > 0) {
                        // Decrement
                        highestCard = playerHand.getCard(--highestCardPos);
                    }
                }
            }

            if (highestCard.getRank() == handKind.getRank()) {
                // Even though the kind has a lower value than the original
                // highest card
                // we're still dropping the of a kind - probably because
                // of another potential kind
                swapWithPileCard(bestCard, handKind, 14);
            } else {
                if (prevCard != null && prevCard.getRank() == highestCard.getRank() && highestCardPos > 0
                        && (playerHand.size() - playerHand.getJokerCount()) > highestCardPos) {
                    // Decrement
                    highestCard = playerHand.getCard(--highestCardPos);
                }

                // Stops infinite loop where players are too scared to play
                // because they know someone else has lower.
                if (playerHand.getValue() > callYanivMinimum || playerHand.getValue() <= callYanivMinimum && bestCard.getValue() < highestCard.getValue()) {

                    /**
                     * Need to check if the highestCard now forms part of a straight
                     */
                    if (handStraight.cardsOverlap(highestCard) > 0) {
                        swapWithPileCard(bestCard, handStraight, 16);
                    } else if ((bestCard.getScore() & PileCard.OF_A_KIND) != 0 && bestCard.getRank() == highestCard.getRank()) {
                        // We were going to pick up the pile card but it's no use to us now.
                        // Drop highest card and pickup from deck
                        swapWithDeckCard(highestCard, 17);
                    } else {
                        // Drop highest card and pickup card
                        swapWithPileCard(bestCard, highestCard, 18);
                    }
                } else {
                    if (prevCard != null && prevCard.getRank() == highestCard.getRank() && highestCardPos > 0
                            && (playerHand.size() - playerHand.getJokerCount()) > highestCardPos) {
                        highestCard = playerHand.getCard(--highestCardPos);
                    }

                    // Double check that the new highest card doesn't also
                    // form part of a kind.
                    handKind = playerHand.searchOfAKind(highestCard);
                    if (handKind.size() > 0) {
                        swapWithDeckCard(handKind, 19);
                    } else {
                        swapWithDeckCard(highestCard, 20);
                    }
                }
            }
        } else {

            if (prevCard != null && prevCard.getRank() == highestCard.getRank() && highestCardPos > 0
                    && (playerHand.size() - playerHand.getJokerCount()) > highestCardPos) {
                // Decrementing
                highestCard = playerHand.getCard(--highestCardPos);
            }

            // Drop highest card and pickup from deck
            swapWithDeckCard(highestCard, 21);
        }

    } // End playAutomaticTurn

    /**
     * Sets the Yaniv boolean back to false and updates the turn and round fields.
     */
    public void newRound() {
        round++;
        turnNum = 0;
    }

    public int getRound() {
        return round;
    }

    /**
     * Updates the round number for a saved game.
     *
     * @param round The current number
     */
    public void setRound(final int round) {
        this.round = round;
    }

    public int getTurn() {
        return turnNum;
    }

    /**
     * Updates the turn number for a saved game.
     *
     * @param turn The current turn number
     */
    public void setTurn(final int turn) {
        this.turnNum = turn;
    }

    /**
     * Defines whose turn it is now. Players that are already out are ignored. And otherwise it loops through the number of players until it first the next player
     * still in.
     */

    public void setNextPlayer() {
        if (winner != -1) {
            if (winner > (numPlayers - 1)) {
                // Started a new game with less players than before
                // and the winning player is no longer playing.
                currentPlayer = 0;
            } else {
                currentPlayer = winner;
                // System.out.printf("The winner from last round %s starts%n", getCurrentPlayer().getName());
            }
            winner = -1;
            prevPlayer = -1;
        } else {
            prevPlayer = currentPlayer;
            do {
                currentPlayer++;
                if (currentPlayer >= players.length) {
                    currentPlayer = 0;
                }
            } while (players[currentPlayer].isOut());
        }
        // Impossible to determine the turn number for saved games
        if (turnNum != -1 && currentPlayer == 0) {
            turnNum++;
        }
    }

    /**
     * Drops a card from the player's hand onto the pile and adds the next card from the deck to their hand. The branch number parameter is used to log the if
     * statement this logic is based on.
     *
     * @param handCard  The card being dropped from the player's hand
     * @param branchNum The branch number used to log which logic was used
     */
    public void swapWithDeckCard(final Card handCard, int branchNum) {
        CardVector handCards = new CardVector();
        handCards.add(handCard);

        swapWithDeckCard(handCards, branchNum);
    }

    /**
     * Drops a collection of cards from the player's hand onto the pile and adds the next card from the deck to their hand.
     *
     * @param handCards The cards being dropped from the player's hand
     */
    public void swapWithDeckCard(CardVector handCards) {
        swapWithDeckCard(handCards, -1);
    }

    /**
     * Drops a collection of cards from the player's hand onto the pile and adds the next card from the deck to their hand. The branch number parameter is used to
     * log which if statement this logic was based on.
     *
     * @param handCards The cards being dropped from the player's hand
     * @param branchNum The branch number used to log which logic was used
     */
    public void swapWithDeckCard(CardVector handCards, int branchNum) {
        Hand playerHand = getCurrentPlayer().getHand();
        playerHand.removeCards(handCards);
        pile.dropCards(handCards);
        Card deckCard = deck.dealCard();
        playerHand.addCard(deckCard);
        playerHand.setLastCardKnown(false);
        setGameTurn(new GameTurn(Turn.PICKUP_DECK, deckCard, handCards, branchNum));
    }

    /**
     * Swaps a card on the pile with one in the player's hand. The branch number parameter is used to log which if statement this logic was based on.
     *
     * @param pileCard  The card at the top of the pile
     * @param handCard  The card in the player's hand
     * @param branchNum The branch number used to log which logic was used
     */
    public void swapWithPileCard(final Card pileCard, final Card handCard, final int branchNum) {
        final CardVector handCards = new CardVector();
        handCards.add(handCard);

        swapWithPileCard(pileCard, handCards, branchNum);
    }

    /**
     * Swaps a card on the pile with a collection of cards from the player's hand.
     *
     * @param pileCard  The card at the top of the pile
     * @param handCards The cards in the player's hand
     */
    public void swapWithPileCard(Card pileCard, CardVector handCards) {
        swapWithPileCard(pileCard, handCards, -1);
    }

    /**
     * Swaps a card on the pile with a collection of cards from the player's hand. The branch number parameter is used to log which if statement this logic was
     * based on.
     *
     * @param pileCard  The card at the top of the pile
     * @param handCards The cards in the player's hand
     * @param branchNum The branch number used to log which logic was use
     */
    public void swapWithPileCard(Card pileCard, CardVector handCards, int branchNum) {
        Player currPlayer = getCurrentPlayer();
        Hand playerHand = currPlayer.getHand();

        if (handCards.firstElement() != null) {
            playerHand.removeCards(handCards);
            playerHand.addCard(pile.swapCards(pileCard, handCards));

        }

        playerHand.setLastCardKnown(true);
        setGameTurn(new GameTurn(Turn.PICKUP_PILE, pileCard, handCards, branchNum));

    }

    public void dropOntoPile(final Card card) {
        final Player prevPlayer = getPrevPlayer();
        final Hand playerHand = prevPlayer.getHand();
        playerHand.removeCard(card);
        pile.addCard(card);
    }

}
