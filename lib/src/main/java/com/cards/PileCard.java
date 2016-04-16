package com.cards;

/**
 * PileCard is an extension of Dealt Card. It includes whether a card on the
 * pile is available to be picked up.
 *
 * @author Barry Irvine
 * @see DealtCard
 */
public class PileCard extends DealtCard {

    /**
     * Used to score a low card (defined as a 3, 2 or Ace).
     */
    public final static byte LOW_CARD = 1;
    /**
     * Use to score an x of a kind.
     */
    public final static byte OF_A_KIND = 2;
    /**
     * Used to score a straight.
     */
    public final static byte STRAIGHT = 4;
    /**
     * Used to score a joker.
     */
    public final static byte JOKER = 8;
    /**
     * Whether debugging messages are sent to the log file.
     */
    private static boolean DEBUG = false;
    /**
     * The card is available to be picked up. For straights this is the cards at
     * either end, for of a kind it is all cards
     */
    private boolean available;
    /**
     * The usefulness of a card on the pile to the current player.
     */
    private byte cardScore;
    /**
     * If the pile card forms a straight with other cards in a player's hand.
     */
    private Straight straight;

    /**
     * Creates a pile card that is the same as the given card
     *
     * @param card The card that is wrapped as a pile card
     */
    public PileCard(Card card) {
        super(card);
        this.available = false;
        this.cardScore = 0;
    }

    /**
     * @return Whether a pile card can be picked up or not
     */
    public boolean available() {
        return this.available;
    }

    /**
     * @return The scoring of a card's usefulness to the current player.
     */
    public byte getScore() {
        return this.cardScore;
    }

    /**
     * @return The straight that this card forms with the player's hand.
     */
    public Straight getStraight() {
        return this.straight;
    }

    /**
     * Ranks a card based on its usefulness to the current player.
     *
     * @param playerHand   The players hand
     * @param handStraight A straight in the player's hand
     * @param handKind     An x of a kind in the player's hand
     */
    public void rankCard(final Hand playerHand, final Straight handStraight, final Kind handKind) {

        this.straight = playerHand.searchStraight(this);

        if (this.getJoker() != null) {
            cardScore = JOKER;
            return;
        }
        if (this.getSoftValue() <= 3
                || ((playerHand.size() - playerHand.getJokerCount()) == 1 && this
                .getSoftValue() <= 5)) {
            cardScore = LOW_CARD;
        }

        // Check that the player has other non-Joker cards in his hand that
        // don't form part of a straight.
        if ((handStraight.size() + playerHand.getJokerCount() - handStraight.getJokerCount()) < playerHand.size() && this.straight.size() > 0) {

            int overlappingCards = this.straight.cardsOverlap(
                    handStraight);
            // So add the straight if no cards overlap (could be because we have
            // no
            // hand straight), or because the straight is an extension of the
            // existing one
            // or because the straight doesn't use a joker like the existing
            // straight
            if (overlappingCards == 0
                    || (overlappingCards == handStraight.size() && this.straight
                    .size() > handStraight.size())
                    || (overlappingCards < 3
                    && this.straight.getJokerCount() == 0 && handStraight
                    .getJokerCount() == 1)) {
                cardScore += STRAIGHT;
            }

        }

        // If the player has more cards in his hand than just the x of a kind
        // and this card has the same rank.
        if (handKind.size() < playerHand.size()
                && playerHand.searchRank(this.getRank())
                && (playerHand.size() - playerHand.getJokerCount()) > 1) {
            // Check that the of a kind is not in the straight we have
            // in our the hand
            if (handStraight.size() > 0) {
                if (!(this.getValue() <= handStraight.getHighestCard()
                        .getValue() && this.getValue() >= handStraight
                        .getLowestCard().getValue())) {
                    cardScore += OF_A_KIND;
                } else {
                    // Need to check whether the hand contains a card of
                    // the same rank that is not in the straight
                    for (com.cards.HandCard handCard : playerHand) {
                        if (handCard.getRank() == this.getRank()
                                && handCard.getSuit() != handStraight.getSuit()) {
                            cardScore += OF_A_KIND;
                            break;
                        }
                    }
                }

            }
            // Check that the player has cards to exchange
            else if (!((handKind.size() + playerHand.getJokerCount()) == playerHand.size())) {
                cardScore += OF_A_KIND;
            }
        }
    }

    /**
     * Defines a pile card as available to be picked up.
     */
    public void setAvailable() {
        this.available = true;
    }

}
