package com.cards;

/**
 * Kind is a collection of cards of the same rank
 *
 * @author Barry Irvine
 */
@SuppressWarnings("serial")
public class Kind extends CardVector {

    /**
     * The Yaniv score of an x of a kind. E.g. picture cards = 10, Ace=1
     */
    private int value;
    /**
     * The rank of the cards that make up the x of a kind
     */
    private Rank rank;

    /**
     * Creates an empty Kind object
     */
    public Kind() {
        super();
        value = 0;
        rank = null;
    }

    /**
     * Adds a card to a kind. The size of the x of a kind is incremented and the
     * Yaniv score value is increased
     *
     * @param card The card added to an of a kind
     */
    public void addCard(Card card) {
        this.value += card.getSoftValue();
        this.rank = card.getRank();
        this.addElement(card);
    }

    /**
     * @return The rank of the cards that make up the x of a kind
     * @see Rank
     */
    public Rank getRank() {
        return this.rank;
    }

    /**
     * @return The Yaniv score of an x of a kind. E.g. picture cards = 10, Ace=1
     */
    public int getValue() {
        return this.value;
    }

}
