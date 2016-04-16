package com.cards;

import java.util.Collections;

/**
 * The deck comprises a collection of 52 or 54 cards.
 * This is dependent on whether the deck is instantiated with
 * jokers or not.
 *
 * @author Barry Irvine
 */
@SuppressWarnings("serial")
public final class Deck extends CardVector {

    /**
     * Number of cards in deck including jokers.
     */
    private final static int MAX_NUM_CARDS = 54;
    /**
     * Immutable array containing whole deck of cards.
     */
    private final static Card[] deck = new Card[MAX_NUM_CARDS];

    /**
     * Creates a new deck object without jokers
     */
    public Deck() {
        this(false);
    }

    /**
     * Creates a new deck object.
     *
     * @param jokers If this parameter is true the deck includes jokers.
     */
    public Deck(final boolean jokers) {
        super();
        int i = 0;

        for (final Suit suit : Suit.values()) {
            for (final com.cards.Rank rank : com.cards.Rank.values()) {
                deck[i] = new Card(rank, suit, i);
                i++;
            }
        }
        if (jokers) {
            for (final Joker joker : Joker.values()) {
                deck[i] = new Card(joker, i);
                i++;
            }
        }
        // Copy immutable array to a vector
        for (Card card : deck) {
            this.addElement(card);
        }
        Collections.shuffle(this);
    }

    /**
     * Only really used for debug tools where you want a card from the deck
     *
     * @param i The index within the array
     * @return The card at the given index of the array
     */
    public static Card getCard(final int i) {
        return deck[i];
    }

    public CardVector getCards() {
        return this;
    }

    /**
     * @return Returns the top card off the deck. This is then removed from the deck.
     */
    public Card dealCard() {
        final Card card = this.firstElement();
        this.removeElement(card);
        return card;
    }

}
