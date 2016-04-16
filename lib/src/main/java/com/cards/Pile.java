package com.cards;

import com.game.Game;

import java.util.Vector;

/**
 * The pile represents a collection of face-up cards that a player can pickup. Not all Pile Cards can be isSelected
 *
 * @author Barry Irvine
 * @see PileCard
 */
@SuppressWarnings("serial")
public class Pile extends Vector<PileCard> {

    private static boolean kind;

    private Game game;

    /**
     * Creates a new empty pile.
     */
    public Pile(Game game) {
        super();
        this.game = game;
    }

    /**
     * Adds a single card to the pile. Since only one card is added it can be picked up and the available flag is set.
     *
     * @param card The card added to the pile
     */
    public void addCard(final Card card) {
        this.addElement(new PileCard(card));
        this.lastElement().setAvailable();
        kind = true;
    }

    /**
     * Adds a number of cards to the pile. If an x of a kind is put on the pile then all the cards are available to be picked up. If a straight is placed on the
     * pile then only the extremities of the straight can be picked up.
     *
     * @param handCards The cards being added to the pile from a hand
     */
    public void addCards(final CardVector handCards) {
        for (Card handCard : handCards) {
            this.addElement(new PileCard(handCard));
        }

        if (handCards.size() > 1) {
            if (handCards.elementAt(0).getValue() == handCards.elementAt(1).getValue()) { // Of a kind
                for (PileCard pileCard : this) {
                    pileCard.setAvailable();
                }
                kind = true;
            } else // Must be a straight
            {
                this.firstElement().setAvailable();
                this.lastElement().setAvailable();
                kind = false;
            }
        } else {
            this.firstElement().setAvailable();
            kind = true;
        }
    }

    /**
     * Empties the pile, moving all the cards to the deck and adds the new cards
     *
     * @param handCards The cards that were just discarded from a hand
     */
    public void dropCards(final CardVector handCards) {
        this.emptyPile();
        this.addCards(handCards);
    }

    /**
     * Loops through the pile, removing each card and putting it back in the Deck.
     *
     * @see Deck
     */
    private void emptyPile() {
        while (this.size() > 0) {
            game.addCardToDeck(this.elementAt(0));
            this.remove(0);
        }
    }

    /**
     * @return The most useful card on the pile to the current player.
     */
    public PileCard getBestCard() {
        byte bestScore = 0;
        com.cards.PileCard bestCard = null;
        for (com.cards.PileCard pileCard : this) {
            if (pileCard.getScore() > bestScore) {
                bestCard = pileCard;
                bestScore = pileCard.getScore();
            }
        }
        return bestCard;
    }

    /**
     * @param position The position of the card in the pile
     * @return The pile card at the given position. If there is no pile card at that position null is returned.
     */
    public PileCard getCard(final int position) {
        return position >= 0 && position < this.size() ? this.elementAt(position) : null;
    }

    /**
     * Checks if the cards on the pile are either a single card or an x of kind.
     *
     * @return Returns true or false.
     */
    public boolean isKind() {
        return kind;
    }

    /**
     * Ranks each available pile card based on its usefulness to the current player.
     *
     * @param playerHand   The current player's hand
     * @param handStraight A straight that the player has in their hand
     * @param handKind     An x of a kind that the player has
     */
    public void rankPileCards(Hand playerHand, Straight handStraight, Kind handKind) {
        for (final PileCard pileCard : this) {
            if (pileCard.available()) {
                pileCard.rankCard(playerHand, handStraight, handKind);
            }
        }
    }

    /**
     * Drops a number of cards from the player's hand onto the pile and returns the pile card that the player picked up. The rest of the pile is moved to the back
     * of the Deck.
     *
     * @param pileCard  The card on the pile that the player picks up
     * @param handCards The cards that the player discards from their hand
     * @return The pile card that the player wanted.
     */
    public Card swapCards(final Card pileCard, final CardVector handCards) {
        this.remove(pileCard);
        this.dropCards(handCards);
        return pileCard;
    }

}
