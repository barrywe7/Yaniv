package com.cards;

import java.util.Vector;

/**
 * A card vector is a collection of card objects.
 *
 * @author Barry Irvine
 */
@SuppressWarnings("serial")
public class CardVector extends Vector<Card> {

    /**
     * Create a new vector of cards.
     */
    public CardVector() {
        super();
    }

    /**
     * @param card The card to check for overlap
     * @return If the card is found in the Card Vector then 1 is returned.
     * Otherwise 0 is returned.
     */
    public int cardsOverlap(Card card) {
        if (this.contains(card)) {
            return 1;
        }
        return 0;
    }

    /**
     * Checks the Card Vector against another one.
     *
     * @param cardVector The Card Vector to check against.
     * @return A count of how many cards exist in both vectors.
     */
    public int cardsOverlap(final CardVector cardVector) {
        // Checks two Card vectors and returns count of matching cards

        int count = 0;
        for (Card card : cardVector) {
            if (this.contains(card)) {
                count++;
            }
        }
        return count;
    }


    /**
     * @return True if the cards are all of the same rank
     */

    public boolean isOfAKind() {

        boolean ok = true;

        if (this.size() < 2)
            return false;

        // Loop through all the cards and check that the rank of the previous
        // card is the same as the current card
        for (int i = 1; i < this.size(); i++) {
            if (this.elementAt(i).getRank() != this.elementAt(i - 1)
                    .getRank()) {
                ok = false;
                break;
            }
        }

        return ok;
    }

    /**
     * @return True if the cards make up a valid straight. Assumes that jokers
     * are in the first positions of the vector.
     */
    public boolean isStraight() {

        boolean ok = true;
        int joker = 0;
        if (this.size() < 3)
            return false;

        // This function assumes that the jokers are in the first positions of
        // the vector
        for (int i = 0; i < this.size(); i++) {
            if (this.elementAt(i).getJoker() != null) {
                joker++;
            } else if (i > 0 && this.elementAt(i - 1).getJoker() == null) {
                if (!this.elementAt(i - 1).equals(
                        this.elementAt(i).getPrevCard())) {
                    if (joker >= 1
                            && i >= joker
                            && this.elementAt(i - 1).equals(
                            this.elementAt(i).getLowerCard(2))) {
                        joker--;
                    } else if (joker == 2
                            && i >= joker
                            && this.elementAt(i - 1).equals(
                            this.elementAt(i).getLowerCard(3))) {
                        joker -= 2;
                    } else {
                        ok = false;
                        break;
                    }
                }
            }
        }
        return ok;
    }

    public int jokerCount() {
        int jokerCount = 0;
        for (int i = 0; i < this.size(); i++) {
            if (this.elementAt(i).getJoker() != null) {
                jokerCount++;
            }
        }
        return jokerCount;
    }

    public String print() {
        StringBuilder s = new StringBuilder();
        for (Card card : this) {
            s.append(card.toShortString()).append(" ");
        }
        return s.toString();
    }

}
