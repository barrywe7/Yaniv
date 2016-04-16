package com.cards;

/**
 * A HandCard is identical to a DealtCard.
 *
 * @author Barry Irvine
 * @see com.cards.DealtCard
 */
public class HandCard extends com.cards.DealtCard {

    /**
     * Creates a new hand card that is the same as the supplied card
     *
     * @param card Card to be wrapped in a Hand
     */
    public HandCard(com.cards.Card card) {
        super(card);
    }
}
