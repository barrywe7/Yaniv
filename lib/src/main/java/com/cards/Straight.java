package com.cards;

/**
 * Straight is a sequential collection of cards of the same suit
 *
 * @author Barry Irvine
 */
@SuppressWarnings("serial")
public class Straight extends CardVector {

    /**
     * The suit of the cards in the straight
     */
    private com.cards.Suit suit;
    /**
     * The Janiv score of a straight. E.g. picture cards = 10, jokers =0, Ace=1
     */
    private int value;
    /**
     * The number of jokers in the straight
     */
    private int jokerCount;

    /**
     * Creates an empty straight object
     */
    public Straight() {
        super();
        suit = null;
        value = 0;
        jokerCount = 0;
    }

    /**
     * Adds a card to a straight. The card is inserted in the correct order
     * based on the sequence of the other cards and the straight size and value
     * are also updated
     *
     * @param card The card that should be added to the straight
     */

    public void addCard(final com.cards.Card card) {
        int pos = 0; // The position of the card within the vector
        com.cards.Card c1; // A temporary card object
        this.value += card.getSoftValue();
        // Add the card's value to the straight
        if (card.getSuit() != null) {
            // If the card is not a joker, define the suit
            this.suit = card.getSuit();
        }
        if (card.getJoker() != null) {
            // If the card is a joker increment the joker count
            this.jokerCount++;
        }

        // Insert in the correct order
        for (int i = 0; i < this.size(); i++) {
            c1 = this.elementAt(i);
            if (card.getJoker() == null) {
                if (c1.getJoker() == null && card.getValue() > c1.getValue()) {
                    pos = i + (card.getValue() - c1.getValue());
                } else if (card.getValue() < c1.getValue()) {
                    pos = i;
                    break;
                }

            } else {// joker
                if (pos == 0)
                    pos = this.size(); // Default the joker to the end

                if (i > 0
                        && this.elementAt(i - 1).getValue() != (this
                        .elementAt(i).getValue() - 1)) {
                    // Found a gap between the two cards in the straight
                    pos = i;
                }
            }
        }

        if (pos >= this.size()) {
            this.addElement(card);
        } else {
            this.insertElementAt(card, pos);
        }

    }

    /**
     * @return The highest card in a straight object. Since the object is
     * ordered this is the last element.
     */
    public com.cards.Card getHighestCard() {
        return this.lastElement();
    }

    /**
     * @return The count of jokers in a straight object.
     */
    public int getJokerCount() {
        return this.jokerCount;
    }

    /**
     * @return The lowest card in a straight object. Since the object is ordered
     * this is the first element.
     */

    public com.cards.Card getLowestCard() {
        return this.firstElement();
    }

    /**
     * @return The suit of the straight object
     */
    public com.cards.Suit getSuit() {
        return this.suit;
    }

    /**
     * @return The Janiv score value of the straight object. E.g. picture cards
     * = 10, jokers = 0, Ace = 1
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Removes a card from a straight. This also deducts the card value from the
     * straight and reduces the straight size.
     *
     * @param card The card that should be removed from the straight.
     */
    private void remCard(final com.cards.Card card) {
        this.value -= card.getSoftValue();
        this.remove(card);
    }

    /**
     * This method removes a card from the top or the bottom of the straight.
     * Unless this would expose a joker to the end of a straight
     */
    public void removeExtraCard() {
        if (this.elementAt(this.size() - 2).getJoker() == null)
            this.remCard(this.lastElement());
        else if (this.elementAt(1).getJoker() == null)
            this.remCard(this.firstElement());
    }

}
