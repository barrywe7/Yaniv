package com.cards;

/**
 * A DealtCard is an extension of Card. It includes a boolean to show whether the player has selected the card.
 *
 * @author Barry Irvine
 */
public class DealtCard extends Card {

    /**
     * True when the dealt card has been isSelected by the current player
     */
    private boolean selected;

    /**
     * Creates a new dealt card for the given card
     *
     * @param card The card that is dealt
     */
    public DealtCard(final Card card) {
        super(card.getRank(), card.getSuit(), card.getJoker(), card.getDeckIndex());
        this.selected = false;
    }

    /**
     * @return True if the card is is selected
     */
    public boolean isSelected() {
        return this.selected;
    }

    /**
     * Sets the card's isSelected status to the boolean isSelected
     *
     * @param selected Whether the card is selected or not
     */
    public synchronized void setSelected(final boolean selected) {
        this.selected = selected;
    }

}
