package com.cards;

import java.util.Vector;

/**
 * The hand contains a collection of cards, their score value and a count of the number of jokers in the hand.
 *
 * @author Barry Irvine
 */
@SuppressWarnings("serial")
public class Hand extends Vector<HandCard> {

    /**
     * Whether debugging messages are written to the logfile.
     */
    private static final boolean DEBUG = false;
    /**
     * The score value of the hand.
     */
    private int value;
    /**
     * The number of jokers in the hand.
     */
    private int jokerCount;
    /**
     * The last card that the player picked up.
     */
    private Card lastCard;

    /**
     * If the last card was picked up from the pile this should be true.
     */
    private boolean lastCardKnown = false;

    /**
     *
     * @param cards
     *          A collection of cards that we hope make up an x of a kind
     * @return True if the cards are all of the same rank
     */

    /**
     * Create an empty hand object.
     */
    public Hand() {
        super();
        this.value = 0;
        this.jokerCount = 0;
        this.lastCard = null;
        lastCardKnown = false;
    }

    /**
     * @param selection
     * @return A sorted selection of cards. This is only relevant to straights so that they are shown in the correct order
     */
    private static CardVector sortSelection(final CardVector selection) {
        int joker = 0;
        if (selection.size() > 2 && selection.isStraight()) {
            // Sort straight
            for (int i = 0; i < selection.size(); i++) {
                if (selection.elementAt(i).getJoker() != null) {
                    joker++;
                } else if (i > 0 && selection.elementAt(i - 1).getJoker() == null) {
                    if (!selection.elementAt(i - 1).equals(selection.elementAt(i).getPrevCard())) {
                        if (joker >= 1 && i >= joker && selection.elementAt(i - 1).equals(selection.elementAt(i).getLowerCard(2))) {
                            joker--;
                            selection.insertElementAt(selection.firstElement(), i);
                            selection.remove(0);
                        } else if (joker == 2 && i >= joker && selection.elementAt(i - 1).equals(selection.elementAt(i).getLowerCard(3))) {
                            joker -= 2;
                            selection.insertElementAt(selection.firstElement(), i);
                            selection.insertElementAt(selection.elementAt(1), i);
                            selection.remove(0);
                            selection.remove(0);
                        }
                    }
                }
            }
        }
        return selection;
    }

    /**
     * Adds the card to a hand and sorts the hand by rank.
     *
     * @param card The card added to the Hand
     */
    public void addCard(final Card card) {
        if (card != null) {
            this.addElement(new HandCard(card));
            this.value += card.getSoftValue();
            this.lastCard = card;
        }

        sortByValue();

        if (card != null && card.getJoker() != null)
            this.jokerCount++;

    }

    /**
     * Empties the hand of all cards.
     */
    public void empty() {
        this.clear();
        this.value = 0;
        this.jokerCount = 0;
        this.lastCard = null;
        lastCardKnown = false;
    }

    /**
     * Searches the player's hand for an x of a kind.
     *
     * @return The x of a kind that was found or an empty object
     */
    public Kind findOfAKind() {
        Card c1;
        Card c2;
        final Kind kind = new Kind();

        for (int i = (this.size() - 1); i > 0; i--) {
            c1 = this.elementAt(i);
            int count = 1;
            for (int j = (i - 1); j >= 0; j--) {

                c2 = this.elementAt(j);

                // Joker doesn't have a rank but not valid for of a kind
                if (c2.getJoker() != null) {
                    if (count > 1) {
                        return kind;
                    }
                    break;
                }

                if (c1.getRank() == c2.getRank()) {
                    count++;
                    if (count == 2) {
                        kind.addCard(c1);
                    }
                    kind.addCard(c2);
                    // If we're already at the last element no point checking
                    // more
                    if (j == 0) {
                        return kind;
                    }
                }
                // Since the Vector is ordered there are no more possible of
                // this rank
                else {
                    if (count > 1) {
                        return kind;
                    }
                }
            }
        }
        return kind; // No of a kinds found
    }

    /**
     * Searches the hand for a particular card and if two or more cards of the same rank are found then it returns the kind.
     *
     * @param card The card which we want to search the hand for.
     * @return A kind. If 1 or less cards of the given rank are found then an empty Kind is returned.
     */
    public Kind searchOfAKind(Card card) {
        final Kind kind = new Kind();
        for (HandCard handCard : this) {
            if (handCard.getRank() == card.getRank()) {
                kind.addCard(handCard);
            }
        }
        if (kind.size() < 2) {
            kind.clear();
        }
        return kind;
    }

    /**
     * @return A straight object. If the player has no straight in their hand this will be empty
     */
    public Straight findStraight() {

        final Straight straight = new Straight();

        final Vector<Card> copyHand = new Vector<Card>(getSortBySuit());

        Card c1;
        Card c2;
        Card c3;
        Card c4 = null;
        Card c5;

        // Can't have a sequence if we already have less than 3 cards
        if (copyHand.size() < 3) {
            return straight;
        }

        // Since we don't ever want to return jokers on the outside of a series
        // always return empty straight here
        if (copyHand.size() == 3 && this.jokerCount == 2) {
            return straight;
        }

        // Loop through cards down to after the joker(s). This way we find the
        // highest value straight first

        for (int i = copyHand.size() - 1; i > (this.jokerCount); i--) {
            c1 = copyHand.elementAt(i);
            c2 = copyHand.elementAt(i - 1);
            c3 = null;

            switch (this.jokerCount) {
                case 0:
                    if (i > (this.jokerCount + 1)) {
                        c3 = copyHand.elementAt(i - 2);
                    }

                    // No jokers. Therefore straight needs to be at least c3, c2, c1

                    if (c3 != null && c1.equals(c2.getNextCard()) && c2.equals(c3.getNextCard())) {

                        straight.addCard(c1);
                        straight.addCard(c2);
                        straight.addCard(c3);

                        // Check for possibility of c4, c3, c2, c1
                        if (i > this.jokerCount + 2) {
                            c4 = copyHand.elementAt(i - 3);
                            if (c3.equals(c4.getNextCard())) {
                                straight.addCard(c4);
                            }
                        }

                        // Check for rare possibility of c5, c4, c3, c2, c1
                        if (i > this.jokerCount + 3 && straight.size() == 4) {
                            c5 = copyHand.elementAt(i - 4);
                            if (c4 != null && c4.equals(c5.getNextCard())) {
                                straight.addCard(c5);
                            }
                        }

                        return straight;
                    }
                    break;

                case 1:
                    // One joker. Therefore straight needs to be ascending sequence
                    // of:
                    // 1. c2, joker, c1
                    // 2. c3, c2, joker, c1
                    // 3. c4, c3, c2, joker, c1
                    // 4. c3, c2, joker, c1
                    // 5. c4, c3, joker, c2, c1
                    // 6. c3, c2, c1 - don't use joker or c4
                    // 7. c4, joker, c3, c2, c1
                    // 8. c4, c3, c2, c1 - don't use joker

                    // Check for options 1-3 first
                    // 1. c2, joker, c1
                    // 2. c3, c2, joker, c1
                    // 3. c4, c3, c2, joker, c1
                    if (c1.equals(c2.getHigherCard(2))) {

                        // Found straight of c2, joker, c1
                        straight.addCard(c2);
                        straight.addCard(copyHand.elementAt(0)); // Joker
                        straight.addCard(c1);

                        // Check for possibility of c3, c2 joker, c1
                        if (i > this.jokerCount + 1) {
                            c3 = copyHand.elementAt(i - 2);

                            if (c2.equals(c3.getNextCard())) {

                                straight.addCard(c3);

                                // Check for possibility of c4, c3, c2, joker, c1

                                if (i > this.jokerCount + 2) {
                                    c4 = copyHand.elementAt(i - 3);
                                    if (c3.equals(c4.getNextCard())) {
                                        straight.addCard(c4);
                                    }
                                }
                            }
                        }
                        return straight;
                    }

                    // Check for options 4-5
                    // 4. c3, joker, c2, c1
                    // 5. c4 c3, joker, c1, c2

                    if (i > this.jokerCount + 1) {
                        c3 = copyHand.elementAt(i - 2);
                    }

                    if (c3 != null && c1.equals(c2.getNextCard()) && c2.equals(c3.getHigherCard(2))) {

                        // Found straight of c3, joker, c2, c1
                        straight.addCard(c3);
                        straight.addCard(c2);
                        straight.addCard(copyHand.firstElement()); // Joker
                        straight.addCard(c1);

                        // Check for possibility of c4, c3, joker, c2, c1
                        if (i > this.jokerCount + 2) {
                            c4 = copyHand.elementAt(i - 3);

                            if (c3.equals(c4.getNextCard())) {

                                // Found a straight of c4, c3, joker, c2, c1

                                straight.addCard(c4);
                            }
                        }
                        return straight;
                    }

                    // Check for options 6-8
                    // 6. c3, c2, c1 - don't use joker or c4
                    // 7. c4, joker, c3, c2, c1
                    // 8. c4, c3, c2, c1 - don't use joker

                    if (c3 != null && c1.equals(c2.getNextCard()) && c2.equals(c3.getNextCard())) {

                        // Found straight of c1, c2, c3
                        straight.addCard(c3);
                        straight.addCard(c2);
                        straight.addCard(c1);

                        if (i > (this.jokerCount + 2)) {
                            c4 = copyHand.elementAt(i - 3);

                            // Check for possibility of c4, joker, c3, c2, c1
                            if (c3.equals(c4.getHigherCard(2))) {
                                straight.addCard(c4);
                                straight.addCard(copyHand.firstElement()); // Joker
                            }

                            // Check for possibility of c4, c3, c2, c1

                            if (c3.equals(c4.getNextCard())) {

                                // Found a straight of c4, c3, c2, c1
                                straight.addCard(c4);
                            }
                        }
                        return straight;
                    }
                    break;
                case 2:
                    // Two jokers. Therefore straight needs to be ascending sequence
                    // of:
                    // 1. c2, joker, c1 - don't use other joker or c3
                    // 2. c3, c2, joker, c1 - don't use other joker
                    // 3. c3, joker, c2, joker, c1
                    // 4. c2, joker, joker, c1
                    // 5. c3, c2, joker, joker, c1
                    // 6. c3, joker, c2, c1 - don't use other joker
                    // 7. c3, joker, joker, c2, c1
                    // 8. c3, c2, c1 - don't use either joker

                    // Check for options 1-3 first
                    // 1. c2, joker, c1 - don't use other joker or c3
                    // 2. c3, c2, joker, c1 - don't use other joker
                    // 3. c3, joker, c2, joker, c1
                    if (c1.equals(c2.getHigherCard(2))) {

                        // Found straight of c1, joker, c2

                        straight.addCard(c2);
                        straight.addCard(copyHand.firstElement()); // Joker
                        straight.addCard(c1);

                        // Check for possibility of c1, joker, c2, c3
                        if (i > this.jokerCount + 1) {
                            c3 = copyHand.elementAt(i - 2);

                            if (c2.equals(c3.getNextCard())) {
                                straight.addCard(c3);
                            }

                            // Check for possibility of c3, joker, c2, joker, c1

                            if (c2.equals(c3.getHigherCard(2))) {
                                straight.addCard(c3);
                                straight.addCard(copyHand.elementAt(1)); // Joker
                            }
                        }
                        return straight;
                    }

                    // Now check for options 4-5
                    // 4. c2, joker, joker, c1
                    // 5. c3, c2, joker, joker, c1
                    if (c1.equals(c2.getHigherCard(3))) {

                        // Found straight of c1, joker, joker, c2
                        straight.addCard(c2);
                        straight.addCard(copyHand.elementAt(0)); // Joker
                        straight.addCard(copyHand.elementAt(1)); // Joker
                        straight.addCard(c1);

                        // Check for possibility of c3, c2, joker, joker, c1
                        if (i > this.jokerCount + 1) {
                            c3 = copyHand.elementAt(i - 2);

                            if (c2.equals(c3.getNextCard())) {
                                // Found straight of c3, c2, joker, joker, c1
                                straight.addCard(c3);
                            }
                        }
                        return straight;
                    }
                    // Now check for option 6-8
                    // 6. c3, joker, c2, c1 - don't use other joker
                    // 7. c3, joker, joker, c2, c1
                    // 8. c3, c2, c1 - don't use either joker
                    if (i > this.jokerCount + 1) {
                        c3 = copyHand.elementAt(i - 2);
                    }

                    if (c3 != null && c1.equals(c2.getNextCard())) {

                        // Check option 6
                        if (c2.equals(c3.getHigherCard(2))) {
                            straight.addCard(c3);
                            straight.addCard(c2);
                            straight.addCard(copyHand.firstElement()); // Joker
                            straight.addCard(c1);
                        }

                        // Check option 7
                        if (c2.equals(c3.getHigherCard(3))) {
                            straight.addCard(c3);
                            straight.addCard(c2);
                            straight.addCard(copyHand.firstElement()); // Joker
                            straight.addCard(copyHand.elementAt(1)); // Joker
                            straight.addCard(c1);
                        }

                        // Check option 8
                        if (c2.equals(c3.getNextCard())) {
                            straight.addCard(c3);
                            straight.addCard(c2);
                            straight.addCard(c1);
                        }
                        return straight;
                    }
            } // End switch joker count

        } // End for
        return straight;
    } // End find straight

    /**
     * @param position Position of the card in the hand
     * @return The card at given position in a hand.
     */
    public HandCard getCard(final int position) {
        if (position >= 0 && position < this.size())
            return this.elementAt(position);
        else
            return null;
    }

    /**
     * @return Position of the highest card in the hand Since the cards are sorted by rank this is the last card in the hand
     */
    public int getHighestCardPos() {
        return (this.size() - 1);
    }

    /**
     * @return The number of jokers in a hand.
     */
    public int getJokerCount() {
        return this.jokerCount;
    }

    /**
     * @return The last card that the player picked up if it was from the pile.
     */
    public Card getLastCard() {
        return this.lastCard;
    }

    /**
     * @return A collection of cards in the hand that the player has isSelected
     */
    public synchronized CardVector getSelection() {
        final CardVector selectedCards = new CardVector();
        for (final HandCard handCard : this) {
            if (handCard.isSelected()) {
                selectedCards.addElement(handCard);
            }
        }
        return selectedCards;
    }

    /**
     * @return A collection of cards sorted so that cards of the same suit value are grouped together. Cards with the same suit are sorted by rank. Note that
     * jokers have the lowest suit value of -1 or -2 and therefore always appear first in the set
     */
    private Vector<HandCard> getSortBySuit() {

        // sortedHand is the Vector that is returned by the method
        final Vector<HandCard> sortedHand = new Vector<>();

        // copyHand is a duplicate of the player's Hand
        final Hand copyHand = (Hand) this.clone();
        HandCard c1, c2;

        while (copyHand.size() > 0) {

            int pos = 0; // Position of first card.

            c1 = copyHand.elementAt(0); // First card.

            for (int i = 1; i < copyHand.size(); i++) {
                c2 = copyHand.elementAt(i);

                if (c2.getSuitValue() < c1.getSuitValue() ||
                        // Card 1 is lower than Card 2
                        (c1.getSuitValue() == c2.getSuitValue() &&
                                // Cards are the same suit so check rank
                                c2.getValue() < c1.getValue())) {
                    pos = i;
                    c1 = c2;
                }
            }
            copyHand.removeElementAt(pos);
            sortedHand.addElement(c1);
        }
        return sortedHand;
    }

    /**
     * @return A sorted selection of cards. This is only relevant to straights so that they are shown in the correct order.
     */
    public synchronized CardVector getSortedSelection() {
        return sortSelection(getSelection());
    }

    /**
     * @return The value of the cards in a hand.
     */
    public Integer getValue() {
        return value;
    }

    /**
     * @return Returns true if the isSelected cards form a valid set to play E.g. a single card, an x of a kind or a straight
     */
    public boolean isValidSelection() {
        final CardVector selection = getSelection();
        switch (selection.size()) {
            case 0: // No cards have been isSelected
                return false;
            case 1: // Only one card isSelected - always true
                return true;
            case 2: // Too short for a straight
                return selection.isOfAKind();
            default:
                if (selection.isOfAKind() || selection.isStraight())
                    return true;
        }
        return false;
    }

    /**
     * Removes the card from the hand and updates the hand value and joker count.
     *
     * @param card THe card to be removed from the hand
     */
    public void removeCard(final Card card) {
        value -= card.getSoftValue();
        this.removeElement(card);
        if (card.getJoker() != null)
            this.jokerCount--;
    }

    /**
     * Removes a collection of cards from the hand, updating the hand value and joker count.
     *
     * @param cards The cards to be removed from the hand
     */
    public void removeCards(final CardVector cards) {
        for (final Card card : cards) {
            this.removeCard(card);
        }
    }

    /**
     * @param rank The rank of the card that we are looking for in the player's hand
     * @return True if a card of this rank is found within the player's hand
     */
    public boolean searchRank(final com.cards.Rank rank) {
        for (HandCard handCard : this) {
            if (handCard.getRank() == rank) {
                return true;
            }
        }
        return false;
    }

    /**
     * Searches the hand for at least two more cards that would make a straight with the supplied card
     *
     * @param card The card that we need to make a straight with
     */
    public Straight searchStraight(final Card card) {

        final Straight straight = new Straight();

        // If the card is a joker we won't bother searching for a straight
        if (card.getJoker() != null) {
            return straight;
        }

        // If the player has less than 3 cards they can't add to a straight
        if (this.size() < 3) {
            return straight;
        } // Empty straight object

        // Too many jokers and not enough other cards
        if (this.size() - this.jokerCount <= 2) {
            return straight;
        } // Empty straight object

        // Remainder of variable initialisations needed for this method
        Card c1;
        Card c2 = null;
        Card c3 = null;
        Card lowestCard;
        Card highestCard;
        Boolean found = false;

        // First try and find a straight without using jokers
        c1 = card.getNextCard();

        if (c1 != null && this.contains(c1)) { // If null then card is a KING
            c2 = card.getPrevCard();
            if (c2 != null && this.contains(c2)) { // If null then card is an
                // ACE
                // We've found a straight either side of card
                found = true;
            } else { // Nothing below, we'll try the next above
                c2 = c1.getNextCard();
                if (c2 != null && this.contains(c2)) {
                    // We've found two cards above card
                    found = true;
                } else if (this.jokerCount > 0) {
                    c2 = c1.getHigherCard(2);
                    if (c2 != null && this.contains(c2)) {
                        // We've found a card above card, a joker and then
                        // another card
                        c3 = this.firstElement(); // Set c3 to a joker
                        found = true;
                    }
                }
            }
        } else { // Nothing above, we'll try below
            c1 = card.getPrevCard();
            if (c1 != null && this.contains(c1)) { // If null then card is an
                // ACE
                c2 = c1.getPrevCard();
                if (c2 != null && this.contains(c2)) {
                    // We've found two cards below card
                    found = true;
                } else if (this.jokerCount > 0) {
                    c2 = c1.getLowerCard(2);
                    if (c2 != null && this.contains(c2)) {
                        // We've found a card below card, a joker and then
                        // another card
                        c3 = this.firstElement(); // Set c3 to a joker
                        found = true;
                    }
                }
            }
        }

        // Now try with jokers if necessary
        if (!found && this.jokerCount > 0) {
            c1 = card.getHigherCard(2);
            if (c1 != null && this.contains(c1)) { // If null then card is a
                // QUEEN or KING
                // We've found a straight with a joker and then a higher
                // card
                c2 = this.firstElement(); // Set c2 to a joker
                found = true;
            } else { // Nothing above let's try below
                c1 = card.getLowerCard(2);
                if (c1 != null && this.contains(c1)) { // If null then card
                    // is a TWO or ACE
                    // We've found a straight with a joker and then a lower
                    // card
                    c2 = this.firstElement(); // Set c2 to a joker
                    found = true;
                }
            }
        }

        // Now try with 2 jokers if necessary
        if (!found && this.jokerCount == 2) {
            c1 = card.getHigherCard(3);
            if (c1 != null && this.contains(c1)) { // If null then card is a
                // JACK, QUEEN or KING
                // We've found a straight with 2 jokers and then a higher
                // card
                c2 = this.firstElement(); // Set c2 to a joker
                c3 = this.elementAt(1); // Set c3 to second joker
                found = true;
            } else { // Nothing above let's try below
                c1 = card.getLowerCard(3);
                if (c1 != null && this.contains(c1)) { // If null then card is a
                    // THREE, TWO or
                    // ACE
                    // We've found a straight with 2 jokers and then a
                    // lower card
                    c2 = this.firstElement(); // Set c2 to a joker
                    c3 = this.elementAt(1); // Set c3 to second joker
                    found = true;
                }
            }

        }

        if (found) {
            straight.addCard(c1);
            straight.addCard(card);
            straight.addCard(c2);
            if (c3 != null) {
                straight.addCard(c3);
            }

            // Now check for additional cards
            lowestCard = straight.getLowestCard();
            highestCard = straight.getHighestCard();

            // Check for higher cards first
            while (found && (highestCard.getRank().ordinal() < com.cards.Rank.KING.ordinal())) {
                c1 = highestCard.getNextCard();
                if (this.contains(c1)) {
                    found = true;
                    straight.addCard(c1);
                    highestCard = straight.getHighestCard();
                } else if (straight.getJokerCount() < this.jokerCount) {
                    c1 = highestCard.getHigherCard(2);
                    if (this.contains(c1)) {
                        found = true;
                        straight.addCard(c1);
                        straight.addCard(this.elementAt(straight.getJokerCount()));
                        // Equates to 0 if we haven't added a joker already or 1
                        // if we have
                        highestCard = straight.getHighestCard();
                    } else {
                        found = false;
                    }
                } else { // No more higher cards
                    found = false;
                }
            }

            // Check for lower cards
            found = true;
            while (found && lowestCard.getRank().ordinal() > Rank.ACE.ordinal()) {
                c1 = lowestCard.getPrevCard();
                if (this.contains(c1)) {
                    found = true;
                    straight.addCard(c1);
                    lowestCard = straight.getLowestCard();
                } else if (straight.getJokerCount() < this.jokerCount) {
                    c1 = lowestCard.getLowerCard(2);
                    if (this.contains(c1)) {
                        found = true;
                        straight.addCard(c1);
                        straight.addCard(this.elementAt(straight.getJokerCount()));
                        // Equates to 0 if we haven't added a joker already or 1
                        // if we have
                        lowestCard = straight.getLowestCard();
                    } else {
                        found = false;
                    }
                } else { // No more lower cards
                    found = false;
                }
            }
        }

        if (straight.size() > this.size()) {
            // The potential straight is bigger than the hand - therefore need to delete an element
            straight.removeExtraCard();
        }
        return straight;
    }

    /**
     * Sorts the cards in the hand so that cards of the same rank are grouped together. Cards with the identical rank are sorted by suit Note that jokers have the
     * lowest value, -1 and -2. So they will always appear first
     */
    private void sortByValue() {

        final Vector<HandCard> newHand = new Vector<>();
        while (this.size() > 0) {

            int pos = 0; // Position of first card.

            HandCard c1 = this.elementAt(0); // First card.

            for (int i = 1; i < this.size(); i++) {
                final HandCard c2 = this.elementAt(i);

                if (c2.getValue() < c1.getValue() ||
                        // Card 1 is lower than Card 2
                        (c2.getValue() == c1.getValue() &&
                                // Cards are the same rank so check suit
                                c2.getSuitValue() < c1.getSuitValue())) {
                    pos = i;
                    c1 = c2;
                }
            }
            this.removeElementAt(pos);
            newHand.addElement(c1);
        }
        this.addAll(newHand);
    }

    public boolean isLastCardKnown() {
        return lastCardKnown;
    }

    public void setLastCardKnown(boolean lastCardKnown) {
        this.lastCardKnown = lastCardKnown;
    }

}
