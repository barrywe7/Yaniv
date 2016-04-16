package com.cards;

/**
 * An enumerated type of the thirteen playing card ranks: ACE to KING
 *
 * @author Barry Irvine
 */
public enum Rank {
    /**
     * An ACE card rank, abbreviated to "A".
     */
    ACE("A"),
    /**
     * The TWO or DEUCE card rank. Abbreviated to "2".
     */
    TWO("2"),
    /**
     * The THREE card rank, abbreviated to "3".
     */
    THREE("3"),
    /**
     * The FOUR card rank, abbreviated to "4".
     */
    FOUR("4"),
    /**
     * The FIVE card rank, abbreviated to "5".
     */
    FIVE("5"),
    /**
     * The SIX card rank, abbreviated to "6".
     */
    SIX("6"),
    /**
     * The SEVEN card rank, abbreviated to "7".
     */
    SEVEN("7"),
    /**
     * The EIGHT card rank, abbreviated to "8".
     */
    EIGHT("8"),
    /**
     * The NINE card rank, abbreviated to "9".
     */
    NINE("9"),
    /**
     * The TEN card rank, abbreviated to "10".
     */
    TEN("10"),
    /**
     * The JACK card rank, abbreviated to "J".
     */
    JACK("J"),
    /**
     * The QUEEN card rank, abbreviated to "Q".
     */
    QUEEN("Q"),
    /**
     * The KING card rank, abbreviated to "K".
     */
    KING("K");

    /**
     * A single character representation of the rank
     */
    public final String shortDisplay;

    /**
     * Defines the short character representation of the rank.
     *
     * @param shortDisplay The one or two character representation of the rank
     */
    Rank(final String shortDisplay) {
        this.shortDisplay = shortDisplay;
    }

}
