package com.cards;

/**
 * A card object is made up of a suit and a rank. For joker cards both of these
 * values are null and the joker type has a value.
 *
 * @author Barry Irvine
 * @see Suit
 * @see Rank
 * @see Joker
 */
public class Card {

    /**
     * The suit of a card.
     */
    private final Suit suit;
    /**
     * The rank of a card.
     *
     * @see Rank
     */
    private final Rank rank;
    /**
     * The joker value of a card. This only has a value for the two joker cards.
     *
     * @see Joker
     */
    private final Joker joker;

    /**
     * A unique integer identifying the card in an unsorted deck.
     */
    private final int deckIndex;

    /**
     * The Android resource name for the card image.
     */
    private final String androidImage;

    /**
     * Creates a new Joker card.
     *
     * @param joker     The joker of a new card.
     * @param deckIndex The unique position of the card in an unsorted deck.
     */

    public Card(final Joker joker, final int deckIndex) {
        this(null, null, joker, deckIndex);
    }

    /**
     * Creates a new card of the given rank and suit.
     *
     * @param rank      The rank of a new card.
     * @param suit      The suit of a new card.
     * @param deckIndex The unique position of the card in an unsorted deck.
     */
    public Card(final Rank rank, final Suit suit, int deckIndex) {
        this(rank, suit, null, deckIndex);
    }

    /**
     * Defines the rank, suit, joker and image of a new card.
     *
     * @param rank      The rank of a new card - or null for Joker.
     * @param suit      The suit of a new card - or null for Joker.
     * @param joker     The joker value of a new card or null for all other cards.
     * @param deckIndex The unique position of the card in an unsorted deck.
     */
    public Card(final Rank rank, final Suit suit, final Joker joker, final int deckIndex) {
        this.androidImage = "card_" + ((joker != null) ? joker.toString().toLowerCase() : (rank.shortDisplay.toLowerCase() + suit.shortDisplay));
        this.joker = joker;
        this.rank = rank;
        this.suit = suit;
        this.deckIndex = deckIndex;
    }

    /**
     * @return True if the suit and rank of both cards are identical. Although
     * joker comparisons aren't envisaged, these are included for
     * completeness.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof Card)) {
            return false;
        }
        final Card other = (Card) obj;
        return rank == other.getRank() && suit == other.getSuit() && joker == other.getJoker();
    }

    /**
     * Returns the unique deck index of a given card.
     *
     * @return The unique deck index for the card
     */
    public int getDeckIndex() {
        return this.deckIndex;
    }

    /**
     * @param increment The number of ranks higher than the existing card.
     * @return Returns a card increment ranks higher than the current one of the
     * same suit. If the current rank + increment is greater than a King
     * then null is returned.
     */
    public Card getHigherCard(final int increment) {
        if (this.rank != null && this.rank.ordinal() + increment <= Rank.KING.ordinal()) {
            return Deck.getCard(this.deckIndex + increment);
        }
        return null;
    }

    /**
     * @return The image resource for Android.
     */
    public String getAndroidImage() {
        return androidImage;
    }

    /**
     * @return The joker of a card.
     */
    public Joker getJoker() {
        return joker;
    }

    /**
     * @param decrement The number of ranks lower than the existing card.
     * @return Returns a card decrement ranks lower than the existing one of the
     * same suit. If the current rank - decrement is lower than an Ace
     * then null is returned.
     */
    public Card getLowerCard(final int decrement) {
        if (rank != null && rank.ordinal() - decrement >= Rank.ACE.ordinal()) {
            return Deck.getCard(this.deckIndex - decrement);
        }
        return null;
    }

    /**
     * @return Returns the next highest card of the same suit. If the card is
     * already a King then null is returned.
     */
    public Card getNextCard() {
        if (this.rank != null && this.rank.ordinal() + 1 <= Rank.KING.ordinal()) {
            return Deck.getCard(this.deckIndex + 1);
        }
        return null;
    }

    /**
     * @return The next lowest card of the same suit. If the current card is an
     * Ace then null is returned.
     */
    public Card getPrevCard() {
        if (this.rank != null && this.rank.ordinal() - 1 >= Rank.ACE.ordinal()) {
            return Deck.getCard(this.deckIndex - 1);
        }
        return null;
    }

    /**
     * @return The rank of a card.
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * @return The Yaniv score of a card. This is 0 for a joker, 1 for an Ace,
     * 10 for a picture card or face value for all other cards.
     */
    public int getSoftValue() {
        // Return ordinal value of rank + 1, 0 for a joker or 10 for a picture
        // card
        final int i = getValue() + 1;

        if (i > 0 && i < 10) {
            return i;
        }
        if (i >= 10) {
            return 10;
        }
        return 0;
    }

    /**
     * @return The suit of a card.
     */
    public Suit getSuit() {
        return suit;
    }

    /**
     * @return The ordinal value of the card's suit or -1 or -2 for a joker.
     */
    public int getSuitValue() {
        // Return ordinal value of suit or -1, -2 for Joker
        return (getJoker() == null) ? getSuit().ordinal() : (getJoker().ordinal() - 2);
    }

    /**
     * @return The ordinal value of the card's rank or -1 or -2 for a joker
     */
    public int getValue() {
        // Return ordinal value of rank or -1, -2 for Joker
        return (getJoker() == null) ? getRank().ordinal() : (getJoker().ordinal() - 2);
    }

    /**
     * @return A short string representation of the card based on the rank and
     * suit E.g. 7♦. For joker cards "JKR" is returned. UTF-8 should be
     * used for the correct display.
     */
    public String toShortString() {
        return joker != null ? "JKR" : (rank.shortDisplay + suit.unicodeSymbol);
    }

    /**
     * @return A string representation of the card based on the rank and suit
     * E.g. SEVEN of DIAMONDS. For joker cards "Joker" is returned.
     */
    public String toString() {
        return joker != null ? "Joker" : (rank.toString() + " of " + suit.toString());
    }
}
