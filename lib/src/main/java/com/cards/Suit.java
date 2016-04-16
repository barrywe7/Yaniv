package com.cards;

/**
 * An enumerated type of the four playing card suits: DIAMONDS, HEARTS, CLUBS
 * and SPADES. The type includes a short single character suit representation
 * and the Unicode symbol for each suit.
 *
 * @author Barry Irvine
 */

public enum Suit {
    /**
     * The diamonds suit. Represented by UTF-8 code 2666 and the short string
     * "d".
     */
    DIAMONDS('\u2666', "d"),
    /**
     * The hearts suit. Represented by UTF-8 code 2665 and the short string "h".
     */
    HEARTS('\u2665', "h"),
    /**
     * The clubs suit. Represented by UTF-8 code 2663 and the short string "c".
     */
    CLUBS('\u2663', "c"),
    /**
     * The spades suit. Represented by UTF-8 code 2660 and the short string "s".
     */
    SPADES('\u2660', "s");

    /**
     * The UTF-8 Unicode symbol for the suit
     */
    public final char unicodeSymbol;
    /**
     * A single character representation of the suit: d,h,c or s.
     */
    public final String shortDisplay;

    /**
     * @param unicodeSymbol The UTF-8 Unicode symbol for the suit
     * @param shortDisplay  A single character representation of the suit
     */
    Suit(final char unicodeSymbol, final String shortDisplay) {
        this.unicodeSymbol = unicodeSymbol;
        this.shortDisplay = shortDisplay;
    }

}
